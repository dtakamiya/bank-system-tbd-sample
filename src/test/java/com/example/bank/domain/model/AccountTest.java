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
    @DisplayName("ステータス")
    class Status {

        @Test
        @DisplayName("create()で生成された口座のステータスがACTIVEであること")
        void shouldCreateAccountWithActiveStatus() {
            Account account = Account.create("田中太郎");

            assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
            assertThat(account.isClosed()).isFalse();
        }

        @Test
        @DisplayName("reconstruct()でステータスを復元できること")
        void shouldReconstructWithStatus() {
            Account account = Account.reconstruct(
                    "id-1", new AccountNumber("1234567890"),
                    "田中太郎", Money.of(1000), AccountStatus.CLOSED,
                    java.time.LocalDateTime.now());

            assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
            assertThat(account.isClosed()).isTrue();
        }

        @Test
        @DisplayName("isClosed()がACTIVE口座でfalseを返すこと")
        void shouldReturnFalseForActiveAccount() {
            Account account = Account.create("田中太郎");

            assertThat(account.isClosed()).isFalse();
        }

        @Test
        @DisplayName("deposit後もステータスが維持されること")
        void shouldMaintainStatusAfterDeposit() {
            Account account = Account.create("田中太郎");
            Account deposited = account.deposit(Money.of(1000));

            assertThat(deposited.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        }

        @Test
        @DisplayName("withdraw後もステータスが維持されること")
        void shouldMaintainStatusAfterWithdraw() {
            Account account = Account.create("田中太郎").deposit(Money.of(1000));
            Account withdrawn = account.withdraw(Money.of(300));

            assertThat(withdrawn.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("解約")
    class Close {

        @Test
        @DisplayName("ACTIVE口座をclose()するとCLOSED状態になること")
        void shouldCloseActiveAccount() {
            Account account = Account.create("田中太郎").deposit(Money.of(5000));
            Account closed = account.close();

            assertThat(closed.getStatus()).isEqualTo(AccountStatus.CLOSED);
            assertThat(closed.isClosed()).isTrue();
            assertThat(closed.getBalance()).isEqualTo(Money.of(0));
        }

        @Test
        @DisplayName("close()で残高がゼロになること")
        void shouldSetBalanceToZeroOnClose() {
            Account account = Account.create("田中太郎").deposit(Money.of(3000));
            Account closed = account.close();

            assertThat(closed.getBalance()).isEqualTo(Money.of(0));
        }

        @Test
        @DisplayName("残高ゼロの口座をclose()できること")
        void shouldCloseAccountWithZeroBalance() {
            Account account = Account.create("田中太郎");
            Account closed = account.close();

            assertThat(closed.getStatus()).isEqualTo(AccountStatus.CLOSED);
            assertThat(closed.getBalance()).isEqualTo(Money.of(0));
        }

        @Test
        @DisplayName("CLOSED口座でclose()するとAccountAlreadyClosedExceptionがスローされること")
        void shouldThrowExceptionWhenClosingClosedAccount() {
            Account account = Account.create("田中太郎").close();

            assertThatThrownBy(account::close)
                    .isInstanceOf(AccountAlreadyClosedException.class);
        }

        @Test
        @DisplayName("CLOSED口座でdeposit()するとAccountAlreadyClosedExceptionがスローされること")
        void shouldThrowExceptionWhenDepositingToClosedAccount() {
            Account account = Account.create("田中太郎").close();

            assertThatThrownBy(() -> account.deposit(Money.of(1000)))
                    .isInstanceOf(AccountAlreadyClosedException.class);
        }

        @Test
        @DisplayName("CLOSED口座でwithdraw()するとAccountAlreadyClosedExceptionがスローされること")
        void shouldThrowExceptionWhenWithdrawingFromClosedAccount() {
            Account closed = Account.reconstruct(
                    "id-1", new AccountNumber("1234567890"),
                    "田中太郎", Money.of(1000), AccountStatus.CLOSED,
                    java.time.LocalDateTime.now());

            assertThatThrownBy(() -> closed.withdraw(Money.of(500)))
                    .isInstanceOf(AccountAlreadyClosedException.class);
        }

        @Test
        @DisplayName("close()はイミュータブルで元のAccountを変更しないこと")
        void shouldNotModifyOriginalAccountOnClose() {
            Account original = Account.create("田中太郎").deposit(Money.of(5000));
            original.close();

            assertThat(original.getStatus()).isEqualTo(AccountStatus.ACTIVE);
            assertThat(original.getBalance()).isEqualTo(Money.of(5000));
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
