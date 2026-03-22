package com.example.bank.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Transaction} ドメインモデルのユニットテスト。
 *
 * <p>入金・出金・払い戻しトランザクションの生成とトランザクション種別を検証する。</p>
 *
 * @see Transaction
 */
class TransactionTest {

    private final AccountNumber accountNumber = new AccountNumber("1234567890");

    @Nested
    @DisplayName("入金トランザクション")
    class DepositTransaction {

        @Test
        @DisplayName("入金トランザクションを生成できること")
        void shouldCreateDepositTransaction() {
            // Arrange
            Money amount = Money.of(1000);
            Money balanceAfter = Money.of(1000);

            // Act
            Transaction transaction = Transaction.deposit(accountNumber, amount, balanceAfter);

            // Assert
            assertThat(transaction.getType()).isEqualTo(TransactionType.DEPOSIT);
            assertThat(transaction.getAccountNumber()).isEqualTo(accountNumber);
            assertThat(transaction.getAmount()).isEqualTo(amount);
            assertThat(transaction.getBalanceAfter()).isEqualTo(balanceAfter);
        }

        @Test
        @DisplayName("入金トランザクションがIDと日時を保持すること")
        void shouldHaveIdAndTimestamp() {
            // Act
            Transaction transaction = Transaction.deposit(
                    accountNumber, Money.of(1000), Money.of(1000));

            // Assert
            assertThat(transaction.getId()).isNotNull();
            assertThat(transaction.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("出金トランザクション")
    class WithdrawalTransaction {

        @Test
        @DisplayName("出金トランザクションを生成できること")
        void shouldCreateWithdrawalTransaction() {
            // Arrange
            Money amount = Money.of(500);
            Money balanceAfter = Money.of(500);

            // Act
            Transaction transaction = Transaction.withdrawal(accountNumber, amount, balanceAfter);

            // Assert
            assertThat(transaction.getType()).isEqualTo(TransactionType.WITHDRAWAL);
            assertThat(transaction.getAccountNumber()).isEqualTo(accountNumber);
            assertThat(transaction.getAmount()).isEqualTo(amount);
            assertThat(transaction.getBalanceAfter()).isEqualTo(balanceAfter);
        }
    }

    @Nested
    @DisplayName("払い戻しトランザクション")
    class RefundTransaction {

        @Test
        @DisplayName("払い戻しトランザクションを生成できること")
        void shouldCreateRefundTransaction() {
            // Arrange
            Money amount = Money.of(5000);
            Money balanceAfter = Money.of(0);

            // Act
            Transaction transaction = Transaction.refund(accountNumber, amount, balanceAfter);

            // Assert
            assertThat(transaction.getType()).isEqualTo(TransactionType.REFUND);
            assertThat(transaction.getAccountNumber()).isEqualTo(accountNumber);
            assertThat(transaction.getAmount()).isEqualTo(amount);
            assertThat(transaction.getBalanceAfter()).isEqualTo(balanceAfter);
        }

        @Test
        @DisplayName("払い戻しトランザクションがIDと日時を保持すること")
        void shouldHaveIdAndTimestamp() {
            // Act
            Transaction transaction = Transaction.refund(
                    accountNumber, Money.of(3000), Money.of(0));

            // Assert
            assertThat(transaction.getId()).isNotNull();
            assertThat(transaction.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("送金出金トランザクション")
    class TransferOutTransaction {

        private final AccountNumber referenceAccountNumber = new AccountNumber("0987654321");

        @Test
        @DisplayName("送金出金トランザクションを生成できること")
        void shouldCreateTransferOutTransaction() {
            // Arrange
            Money amount = Money.of(10000);
            Money fee = Money.of(100);
            Money balanceAfter = Money.of(39900);

            // Act
            Transaction transaction = Transaction.transferOut(
                    accountNumber, amount, fee, balanceAfter, referenceAccountNumber);

            // Assert
            assertThat(transaction.getType()).isEqualTo(TransactionType.TRANSFER_OUT);
            assertThat(transaction.getAccountNumber()).isEqualTo(accountNumber);
            assertThat(transaction.getAmount()).isEqualTo(amount);
            assertThat(transaction.getFee()).isEqualTo(fee);
            assertThat(transaction.getBalanceAfter()).isEqualTo(balanceAfter);
            assertThat(transaction.getReferenceAccountNumber()).isEqualTo(referenceAccountNumber);
        }

        @Test
        @DisplayName("手数料なしの送金出金トランザクションを生成できること")
        void shouldCreateTransferOutTransactionWithNoFee() {
            // Act
            Transaction transaction = Transaction.transferOut(
                    accountNumber, Money.of(10000), Money.ZERO, Money.of(40000), referenceAccountNumber);

            // Assert
            assertThat(transaction.getFee()).isEqualTo(Money.ZERO);
        }
    }

    @Nested
    @DisplayName("送金入金トランザクション")
    class TransferInTransaction {

        private final AccountNumber referenceAccountNumber = new AccountNumber("0987654321");

        @Test
        @DisplayName("送金入金トランザクションを生成できること")
        void shouldCreateTransferInTransaction() {
            // Arrange
            Money amount = Money.of(10000);
            Money balanceAfter = Money.of(30000);

            // Act
            Transaction transaction = Transaction.transferIn(
                    accountNumber, amount, balanceAfter, referenceAccountNumber);

            // Assert
            assertThat(transaction.getType()).isEqualTo(TransactionType.TRANSFER_IN);
            assertThat(transaction.getAccountNumber()).isEqualTo(accountNumber);
            assertThat(transaction.getAmount()).isEqualTo(amount);
            assertThat(transaction.getBalanceAfter()).isEqualTo(balanceAfter);
            assertThat(transaction.getReferenceAccountNumber()).isEqualTo(referenceAccountNumber);
            assertThat(transaction.getFee()).isNull();
        }
    }

    @Nested
    @DisplayName("TransactionType")
    class TransactionTypeTest {

        @Test
        @DisplayName("DEPOSIT, WITHDRAWAL, REFUND, TRANSFER_OUT, TRANSFER_INの5つの値が存在すること")
        void shouldHaveFiveValues() {
            // Act
            TransactionType[] values = TransactionType.values();

            // Assert
            assertThat(values).hasSize(5);
            assertThat(values).containsExactlyInAnyOrder(
                    TransactionType.DEPOSIT,
                    TransactionType.WITHDRAWAL,
                    TransactionType.REFUND,
                    TransactionType.TRANSFER_OUT,
                    TransactionType.TRANSFER_IN);
        }
    }

    @Nested
    @DisplayName("既存トランザクションの後方互換性")
    class BackwardCompatibility {

        @Test
        @DisplayName("既存の入金トランザクションではfeeとreferenceAccountNumberがnullであること")
        void shouldHaveNullFeeAndReferenceForDeposit() {
            // Act
            Transaction transaction = Transaction.deposit(accountNumber, Money.of(1000), Money.of(1000));

            // Assert
            assertThat(transaction.getFee()).isNull();
            assertThat(transaction.getReferenceAccountNumber()).isNull();
        }
    }

    @Nested
    @DisplayName("reconstruct")
    class ReconstructTest {

        @Test
        @DisplayName("8引数のreconstructでfeeとreferenceAccountNumberが復元されること")
        void shouldReconstructWithFeeAndReference() {
            // Arrange
            AccountNumber refAccount = new AccountNumber("0987654321");

            // Act
            Transaction transaction = Transaction.reconstruct(
                    "tx-001", accountNumber, TransactionType.TRANSFER_OUT,
                    Money.of(10000), Money.of(100), Money.of(39900),
                    refAccount, java.time.LocalDateTime.of(2026, 3, 22, 10, 0));

            // Assert
            assertThat(transaction.getId()).isEqualTo("tx-001");
            assertThat(transaction.getType()).isEqualTo(TransactionType.TRANSFER_OUT);
            assertThat(transaction.getAmount()).isEqualTo(Money.of(10000));
            assertThat(transaction.getFee()).isEqualTo(Money.of(100));
            assertThat(transaction.getBalanceAfter()).isEqualTo(Money.of(39900));
            assertThat(transaction.getReferenceAccountNumber()).isEqualTo(refAccount);
        }

        @Test
        @DisplayName("6引数のreconstructでTRANSFER_OUTを使用すると例外がスローされること")
        void shouldThrowWhenReconstructingTransferOutWithSixParams() {
            // Act & Assert
            org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                    Transaction.reconstruct(
                            "tx-001", accountNumber, TransactionType.TRANSFER_OUT,
                            Money.of(10000), Money.of(39900),
                            java.time.LocalDateTime.of(2026, 3, 22, 10, 0))
            ).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("6引数のreconstructでTRANSFER_INを使用すると例外がスローされること")
        void shouldThrowWhenReconstructingTransferInWithSixParams() {
            // Act & Assert
            org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                    Transaction.reconstruct(
                            "tx-001", accountNumber, TransactionType.TRANSFER_IN,
                            Money.of(10000), Money.of(30000),
                            java.time.LocalDateTime.of(2026, 3, 22, 10, 0))
            ).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
