package com.example.bank.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    @Nested
    @DisplayName("生成")
    class Creation {

        @Test
        @DisplayName("Accountを生成できること（ownerName, accountNumber, 初期残高0）")
        void shouldCreateAccountWithZeroBalance() {
            Account account = Account.create("田中太郎");

            assertThat(account.getOwnerName()).isEqualTo("田中太郎");
            assertThat(account.getAccountNumber()).isNotNull();
            assertThat(account.getBalance()).isEqualTo(Money.of(0));
            assertThat(account.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("入金")
    class Deposit {

        @Test
        @DisplayName("deposit()で新しいAccountが返されること（イミュータブル）")
        void shouldReturnNewAccountOnDeposit() {
            Account original = Account.create("田中太郎");
            Account deposited = original.deposit(Money.of(1000));

            assertThat(deposited).isNotSameAs(original);
            assertThat(deposited.getBalance()).isEqualTo(Money.of(1000));
        }

        @Test
        @DisplayName("deposit()で元のAccountの残高が変わらないこと")
        void shouldNotModifyOriginalAccountOnDeposit() {
            Account original = Account.create("田中太郎");
            original.deposit(Money.of(1000));

            assertThat(original.getBalance()).isEqualTo(Money.of(0));
        }

        @Test
        @DisplayName("deposit()に0の金額を渡すとエラーになること")
        void shouldThrowExceptionForZeroDeposit() {
            Account account = Account.create("田中太郎");

            assertThatThrownBy(() -> account.deposit(Money.of(0)))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("出金")
    class Withdrawal {

        @Test
        @DisplayName("withdraw()で新しいAccountが返されること")
        void shouldReturnNewAccountOnWithdrawal() {
            Account account = Account.create("田中太郎").deposit(Money.of(1000));
            Account withdrawn = account.withdraw(Money.of(300));

            assertThat(withdrawn).isNotSameAs(account);
            assertThat(withdrawn.getBalance()).isEqualTo(Money.of(700));
        }

        @Test
        @DisplayName("withdraw()で残高不足時にInsufficientBalanceExceptionがスローされること")
        void shouldThrowExceptionWhenInsufficientBalance() {
            Account account = Account.create("田中太郎").deposit(Money.of(1000));

            assertThatThrownBy(() -> account.withdraw(Money.of(5000)))
                    .isInstanceOf(InsufficientBalanceException.class);
        }
    }

    @Nested
    @DisplayName("残高確認")
    class BalanceCheck {

        @Test
        @DisplayName("canWithdraw()が出金可能な場合にtrueを返すこと")
        void shouldReturnTrueWhenSufficientBalance() {
            Account account = Account.create("田中太郎").deposit(Money.of(1000));

            assertThat(account.canWithdraw(Money.of(1000))).isTrue();
        }

        @Test
        @DisplayName("canWithdraw()が残高不足の場合にfalseを返すこと")
        void shouldReturnFalseWhenInsufficientBalance() {
            Account account = Account.create("田中太郎").deposit(Money.of(100));

            assertThat(account.canWithdraw(Money.of(200))).isFalse();
        }
    }
}
