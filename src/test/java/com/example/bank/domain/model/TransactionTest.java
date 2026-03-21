package com.example.bank.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionTest {

    private final AccountNumber accountNumber = new AccountNumber("1234567890");

    @Nested
    @DisplayName("入金トランザクション")
    class DepositTransaction {

        @Test
        @DisplayName("入金トランザクションを生成できること")
        void shouldCreateDepositTransaction() {
            Money amount = Money.of(1000);
            Money balanceAfter = Money.of(1000);

            Transaction transaction = Transaction.deposit(accountNumber, amount, balanceAfter);

            assertThat(transaction.getType()).isEqualTo(TransactionType.DEPOSIT);
            assertThat(transaction.getAccountNumber()).isEqualTo(accountNumber);
            assertThat(transaction.getAmount()).isEqualTo(amount);
            assertThat(transaction.getBalanceAfter()).isEqualTo(balanceAfter);
        }

        @Test
        @DisplayName("入金トランザクションがIDと日時を保持すること")
        void shouldHaveIdAndTimestamp() {
            Transaction transaction = Transaction.deposit(
                    accountNumber, Money.of(1000), Money.of(1000));

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
            Money amount = Money.of(500);
            Money balanceAfter = Money.of(500);

            Transaction transaction = Transaction.withdrawal(accountNumber, amount, balanceAfter);

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
            Money amount = Money.of(5000);
            Money balanceAfter = Money.of(0);

            Transaction transaction = Transaction.refund(accountNumber, amount, balanceAfter);

            assertThat(transaction.getType()).isEqualTo(TransactionType.REFUND);
            assertThat(transaction.getAccountNumber()).isEqualTo(accountNumber);
            assertThat(transaction.getAmount()).isEqualTo(amount);
            assertThat(transaction.getBalanceAfter()).isEqualTo(balanceAfter);
        }

        @Test
        @DisplayName("払い戻しトランザクションがIDと日時を保持すること")
        void shouldHaveIdAndTimestamp() {
            Transaction transaction = Transaction.refund(
                    accountNumber, Money.of(3000), Money.of(0));

            assertThat(transaction.getId()).isNotNull();
            assertThat(transaction.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("TransactionType")
    class TransactionTypeTest {

        @Test
        @DisplayName("DEPOSIT, WITHDRAWAL, REFUNDの3つの値が存在すること")
        void shouldHaveThreeValues() {
            TransactionType[] values = TransactionType.values();

            assertThat(values).hasSize(3);
            assertThat(values).containsExactlyInAnyOrder(
                    TransactionType.DEPOSIT,
                    TransactionType.WITHDRAWAL,
                    TransactionType.REFUND);
        }
    }
}
