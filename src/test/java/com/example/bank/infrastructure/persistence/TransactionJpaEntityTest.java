package com.example.bank.infrastructure.persistence;

import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.model.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link TransactionJpaEntity} のユニットテスト。
 *
 * <p>JPAエンティティとドメインモデル({@link Transaction})間の変換ロジックを検証する。</p>
 *
 * @see TransactionJpaEntity
 */
class TransactionJpaEntityTest {

    private final AccountNumber accountNumber = new AccountNumber("1234567890");

    @Test
    @DisplayName("TransactionからTransactionJpaEntityに変換できること")
    void shouldConvertFromDomain() {
        // Arrange
        Transaction transaction = Transaction.deposit(accountNumber, Money.of(1000), Money.of(1000));

        // Act
        TransactionJpaEntity entity = TransactionJpaEntity.fromDomain(transaction);

        // Assert
        assertThat(entity.getId()).isEqualTo(transaction.getId());
        assertThat(entity.getAccountNumber()).isEqualTo("1234567890");
        assertThat(entity.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(entity.getAmount()).isEqualByComparingTo(transaction.getAmount().getAmount());
        assertThat(entity.getBalanceAfter()).isEqualByComparingTo(transaction.getBalanceAfter().getAmount());
        assertThat(entity.getCreatedAt()).isEqualTo(transaction.getCreatedAt());
    }

    @Test
    @DisplayName("TransactionJpaEntityからTransactionに変換できること")
    void shouldConvertToDomain() {
        // Arrange
        Transaction original = Transaction.withdrawal(accountNumber, Money.of(500), Money.of(500));
        TransactionJpaEntity entity = TransactionJpaEntity.fromDomain(original);

        // Act
        Transaction restored = entity.toDomain();

        // Assert
        assertThat(restored.getId()).isEqualTo(original.getId());
        assertThat(restored.getAccountNumber()).isEqualTo(original.getAccountNumber());
        assertThat(restored.getType()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(restored.getAmount()).isEqualTo(original.getAmount());
        assertThat(restored.getBalanceAfter()).isEqualTo(original.getBalanceAfter());
        assertThat(restored.getCreatedAt()).isEqualTo(original.getCreatedAt());
    }

    @Test
    @DisplayName("TRANSFER_OUTのTransactionからEntityに変換しfeeとreferenceAccountNumberが保持されること")
    void shouldConvertTransferOutFromDomain() {
        // Arrange
        AccountNumber refAccount = new AccountNumber("0987654321");
        Transaction transaction = Transaction.transferOut(
                accountNumber, Money.of(10000), Money.of(100), Money.of(39900), refAccount);

        // Act
        TransactionJpaEntity entity = TransactionJpaEntity.fromDomain(transaction);

        // Assert
        assertThat(entity.getType()).isEqualTo(TransactionType.TRANSFER_OUT);
        assertThat(entity.getFee()).isEqualByComparingTo(new java.math.BigDecimal("100.00"));
        assertThat(entity.getReferenceAccountNumber()).isEqualTo("0987654321");
    }

    @Test
    @DisplayName("TRANSFER_INのTransactionからEntityに変換しreferenceAccountNumberが保持されること")
    void shouldConvertTransferInFromDomain() {
        // Arrange
        AccountNumber refAccount = new AccountNumber("0987654321");
        Transaction transaction = Transaction.transferIn(
                accountNumber, Money.of(10000), Money.of(30000), refAccount);

        // Act
        TransactionJpaEntity entity = TransactionJpaEntity.fromDomain(transaction);

        // Assert
        assertThat(entity.getType()).isEqualTo(TransactionType.TRANSFER_IN);
        assertThat(entity.getFee()).isNull();
        assertThat(entity.getReferenceAccountNumber()).isEqualTo("0987654321");
    }

    @Test
    @DisplayName("TRANSFER_OUTのEntityからTransactionに復元しfeeとreferenceAccountNumberが保持されること")
    void shouldConvertTransferOutToDomain() {
        // Arrange
        AccountNumber refAccount = new AccountNumber("0987654321");
        Transaction original = Transaction.transferOut(
                accountNumber, Money.of(10000), Money.of(100), Money.of(39900), refAccount);
        TransactionJpaEntity entity = TransactionJpaEntity.fromDomain(original);

        // Act
        Transaction restored = entity.toDomain();

        // Assert
        assertThat(restored.getType()).isEqualTo(TransactionType.TRANSFER_OUT);
        assertThat(restored.getFee()).isEqualTo(Money.of(100));
        assertThat(restored.getReferenceAccountNumber()).isEqualTo(refAccount);
    }

    @Test
    @DisplayName("既存のDEPOSIT変換でfeeとreferenceAccountNumberがnullであること")
    void shouldHaveNullFeeAndReferenceForDeposit() {
        // Arrange
        Transaction transaction = Transaction.deposit(accountNumber, Money.of(1000), Money.of(1000));

        // Act
        TransactionJpaEntity entity = TransactionJpaEntity.fromDomain(transaction);

        // Assert
        assertThat(entity.getFee()).isNull();
        assertThat(entity.getReferenceAccountNumber()).isNull();
    }
}
