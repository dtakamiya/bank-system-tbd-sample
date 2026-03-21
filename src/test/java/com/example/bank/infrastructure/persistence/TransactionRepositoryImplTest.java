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

@DataJpaTest
@Import(TransactionRepositoryImpl.class)
class TransactionRepositoryImplTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private final AccountNumber accountNumber = new AccountNumber("1234567890");

    @Test
    @DisplayName("save()でTransactionが永続化されること")
    void shouldSaveTransaction() {
        Transaction transaction = Transaction.deposit(accountNumber, Money.of(1000), Money.of(1000));

        Transaction saved = transactionRepository.save(transaction);

        assertThat(saved.getId()).isEqualTo(transaction.getId());
    }

    @Test
    @DisplayName("findByAccountNumber()で口座の取引履歴が取得できること")
    void shouldFindTransactionsByAccountNumber() {
        transactionRepository.save(
                Transaction.deposit(accountNumber, Money.of(1000), Money.of(1000)));
        transactionRepository.save(
                Transaction.withdrawal(accountNumber, Money.of(300), Money.of(700)));

        List<Transaction> transactions = transactionRepository.findByAccountNumber(accountNumber, 0, 10);

        assertThat(transactions).hasSize(2);
    }

    @Test
    @DisplayName("findByAccountNumber()でページネーションが正しく動作すること")
    void shouldPaginateTransactions() {
        for (int i = 1; i <= 5; i++) {
            transactionRepository.save(
                    Transaction.deposit(accountNumber, Money.of(100 * i), Money.of(100 * i)));
        }

        List<Transaction> page0 = transactionRepository.findByAccountNumber(accountNumber, 0, 2);
        List<Transaction> page1 = transactionRepository.findByAccountNumber(accountNumber, 1, 2);

        assertThat(page0).hasSize(2);
        assertThat(page1).hasSize(2);
    }

    @Test
    @DisplayName("findByAccountNumber()で取引が作成日時の降順で返されること")
    void shouldReturnTransactionsInDescendingOrder() {
        Transaction first = Transaction.deposit(accountNumber, Money.of(100), Money.of(100));
        Transaction second = Transaction.deposit(accountNumber, Money.of(200), Money.of(300));
        transactionRepository.save(first);
        transactionRepository.save(second);

        List<Transaction> transactions = transactionRepository.findByAccountNumber(accountNumber, 0, 10);

        assertThat(transactions.get(0).getAmount()).isEqualTo(Money.of(200));
        assertThat(transactions.get(1).getAmount()).isEqualTo(Money.of(100));
    }

    @Test
    @DisplayName("他の口座の取引は含まれないこと")
    void shouldNotIncludeOtherAccountTransactions() {
        AccountNumber otherAccount = new AccountNumber("9876543210");
        transactionRepository.save(
                Transaction.deposit(accountNumber, Money.of(1000), Money.of(1000)));
        transactionRepository.save(
                Transaction.deposit(otherAccount, Money.of(2000), Money.of(2000)));

        List<Transaction> transactions = transactionRepository.findByAccountNumber(accountNumber, 0, 10);

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getAccountNumber()).isEqualTo(accountNumber);
    }
}
