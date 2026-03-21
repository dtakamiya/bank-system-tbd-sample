package com.example.bank.infrastructure.persistence;

import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.model.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionJpaEntityTest {

    private final AccountNumber accountNumber = new AccountNumber("1234567890");

    @Test
    @DisplayName("TransactionからTransactionJpaEntityに変換できること")
    void shouldConvertFromDomain() {
        Transaction transaction = Transaction.deposit(accountNumber, Money.of(1000), Money.of(1000));

        TransactionJpaEntity entity = TransactionJpaEntity.fromDomain(transaction);

        assertThat(entity.getId()).isEqualTo(transaction.getId());
        assertThat(entity.getAccountNumber()).isEqualTo("1234567890");
        assertThat(entity.getType()).isEqualTo("DEPOSIT");
        assertThat(entity.getAmount()).isEqualByComparingTo(transaction.getAmount().getAmount());
        assertThat(entity.getBalanceAfter()).isEqualByComparingTo(transaction.getBalanceAfter().getAmount());
        assertThat(entity.getCreatedAt()).isEqualTo(transaction.getCreatedAt());
    }

    @Test
    @DisplayName("TransactionJpaEntityからTransactionに変換できること")
    void shouldConvertToDomain() {
        Transaction original = Transaction.withdrawal(accountNumber, Money.of(500), Money.of(500));
        TransactionJpaEntity entity = TransactionJpaEntity.fromDomain(original);

        Transaction restored = entity.toDomain();

        assertThat(restored.getId()).isEqualTo(original.getId());
        assertThat(restored.getAccountNumber()).isEqualTo(original.getAccountNumber());
        assertThat(restored.getType()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(restored.getAmount()).isEqualTo(original.getAmount());
        assertThat(restored.getBalanceAfter()).isEqualTo(original.getBalanceAfter());
        assertThat(restored.getCreatedAt()).isEqualTo(original.getCreatedAt());
    }
}
