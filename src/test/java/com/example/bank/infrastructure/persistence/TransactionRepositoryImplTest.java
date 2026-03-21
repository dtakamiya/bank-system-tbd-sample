package com.example.bank.infrastructure.persistence;

import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.model.TransactionType;
import com.example.bank.domain.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link TransactionRepositoryImpl} のインテグレーションテスト。
 *
 * <p>Spring Data JPAを利用した取引リポジトリの永続化・検索・ページネーション操作を検証する。</p>
 *
 * @see TransactionRepositoryImpl
 */
@DataJpaTest
@Import(TransactionRepositoryImpl.class)
class TransactionRepositoryImplTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private final AccountNumber accountNumber = new AccountNumber("1234567890");

    @Test
    @DisplayName("save()でTransactionが永続化されること")
    void shouldSaveTransaction() {
        // Arrange
        Transaction transaction = Transaction.deposit(accountNumber, Money.of(1000), Money.of(1000));

        // Act
        Transaction saved = transactionRepository.save(transaction);

        // Assert
        assertThat(saved.getId()).isEqualTo(transaction.getId());
    }

    @Test
    @DisplayName("findByAccountNumber()で口座の取引履歴が取得できること")
    void shouldFindTransactionsByAccountNumber() {
        // Arrange
        transactionRepository.save(
                Transaction.deposit(accountNumber, Money.of(1000), Money.of(1000)));
        transactionRepository.save(
                Transaction.withdrawal(accountNumber, Money.of(300), Money.of(700)));

        // Act
        List<Transaction> transactions = transactionRepository.findByAccountNumber(accountNumber, 0, 10);

        // Assert
        assertThat(transactions).hasSize(2);
    }

    @Test
    @DisplayName("findByAccountNumber()でページネーションが正しく動作すること")
    void shouldPaginateTransactions() {
        // Arrange
        for (int i = 1; i <= 5; i++) {
            transactionRepository.save(
                    Transaction.deposit(accountNumber, Money.of(100 * i), Money.of(100 * i)));
        }

        // Act
        List<Transaction> page0 = transactionRepository.findByAccountNumber(accountNumber, 0, 2);
        List<Transaction> page1 = transactionRepository.findByAccountNumber(accountNumber, 1, 2);

        // Assert
        assertThat(page0).hasSize(2);
        assertThat(page1).hasSize(2);
    }

    @Test
    @DisplayName("findByAccountNumber()で取引が作成日時の降順で返されること")
    void shouldReturnTransactionsInDescendingOrder() {
        // Arrange
        Transaction first = Transaction.deposit(accountNumber, Money.of(100), Money.of(100));
        Transaction second = Transaction.deposit(accountNumber, Money.of(200), Money.of(300));
        transactionRepository.save(first);
        transactionRepository.save(second);

        // Act
        List<Transaction> transactions = transactionRepository.findByAccountNumber(accountNumber, 0, 10);

        // Assert
        assertThat(transactions.get(0).getAmount()).isEqualTo(Money.of(200));
        assertThat(transactions.get(1).getAmount()).isEqualTo(Money.of(100));
    }

    @Test
    @DisplayName("他の口座の取引は含まれないこと")
    void shouldNotIncludeOtherAccountTransactions() {
        // Arrange
        AccountNumber otherAccount = new AccountNumber("9876543210");
        transactionRepository.save(
                Transaction.deposit(accountNumber, Money.of(1000), Money.of(1000)));
        transactionRepository.save(
                Transaction.deposit(otherAccount, Money.of(2000), Money.of(2000)));

        // Act
        List<Transaction> transactions = transactionRepository.findByAccountNumber(accountNumber, 0, 10);

        // Assert
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getAccountNumber()).isEqualTo(accountNumber);
    }
}
