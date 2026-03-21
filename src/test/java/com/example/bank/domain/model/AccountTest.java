package com.example.bank.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link Account} ドメインモデルのユニットテスト。
 *
 * <p>口座の生成・入金・出金・ステータス管理・解約・残高確認の振る舞いとバリデーションを検証する。</p>
 *
 * @see Account
 */
class AccountTest {

    @Nested
    @DisplayName("生成")
    class Creation {

        @Test
        @DisplayName("Accountを生成できること（ownerName, accountNumber, 初期残高0）")
        void shouldCreateAccountWithZeroBalance() {
            // Act
            Account account = Account.create("田中太郎");

            // Assert
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
            // Arrange
            Account original = Account.create("田中太郎");

            // Act
            Account deposited = original.deposit(Money.of(1000));

            // Assert
            assertThat(deposited).isNotSameAs(original);
            assertThat(deposited.getBalance()).isEqualTo(Money.of(1000));
        }

        @Test
        @DisplayName("deposit()で元のAccountの残高が変わらないこと")
        void shouldNotModifyOriginalAccountOnDeposit() {
            // Arrange
            Account original = Account.create("田中太郎");

            // Act
            original.deposit(Money.of(1000));

            // Assert
            assertThat(original.getBalance()).isEqualTo(Money.of(0));
        }

        @Test
        @DisplayName("deposit()に0の金額を渡すとエラーになること")
        void shouldThrowExceptionForZeroDeposit() {
            // Arrange
            Account account = Account.create("田中太郎");

            // Act & Assert
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
            // Arrange
            Account account = Account.create("田中太郎").deposit(Money.of(1000));

            // Act
            Account withdrawn = account.withdraw(Money.of(300));

            // Assert
            assertThat(withdrawn).isNotSameAs(account);
            assertThat(withdrawn.getBalance()).isEqualTo(Money.of(700));
        }

        @Test
        @DisplayName("withdraw()で残高不足時にInsufficientBalanceExceptionがスローされること")
        void shouldThrowExceptionWhenInsufficientBalance() {
            // Arrange
            Account account = Account.create("田中太郎").deposit(Money.of(1000));

            // Act & Assert
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
            // Act
            Account account = Account.create("田中太郎");

            // Assert
            assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
            assertThat(account.isClosed()).isFalse();
        }

        @Test
        @DisplayName("reconstruct()でステータスを復元できること")
        void shouldReconstructWithStatus() {
            // Act
            Account account = Account.reconstruct(
                    "id-1", new AccountNumber("1234567890"),
                    "田中太郎", Money.of(1000), AccountStatus.CLOSED,
                    java.time.LocalDateTime.now());

            // Assert
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
            // Arrange
            Account account = Account.create("田中太郎");

            // Act
            Account deposited = account.deposit(Money.of(1000));

            // Assert
            assertThat(deposited.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        }

        @Test
        @DisplayName("withdraw後もステータスが維持されること")
        void shouldMaintainStatusAfterWithdraw() {
            // Arrange
            Account account = Account.create("田中太郎").deposit(Money.of(1000));

            // Act
            Account withdrawn = account.withdraw(Money.of(300));

            // Assert
            assertThat(withdrawn.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("解約")
    class Close {

        @Test
        @DisplayName("ACTIVE口座をclose()するとCLOSED状態になること")
        void shouldCloseActiveAccount() {
            // Arrange
            Account account = Account.create("田中太郎").deposit(Money.of(5000));

            // Act
            Account closed = account.close();

            // Assert
            assertThat(closed.getStatus()).isEqualTo(AccountStatus.CLOSED);
            assertThat(closed.isClosed()).isTrue();
            assertThat(closed.getBalance()).isEqualTo(Money.of(0));
        }

        @Test
        @DisplayName("close()で残高がゼロになること")
        void shouldSetBalanceToZeroOnClose() {
            // Arrange
            Account account = Account.create("田中太郎").deposit(Money.of(3000));

            // Act
            Account closed = account.close();

            // Assert
            assertThat(closed.getBalance()).isEqualTo(Money.of(0));
        }

        @Test
        @DisplayName("残高ゼロの口座をclose()できること")
        void shouldCloseAccountWithZeroBalance() {
            // Arrange
            Account account = Account.create("田中太郎");

            // Act
            Account closed = account.close();

            // Assert
            assertThat(closed.getStatus()).isEqualTo(AccountStatus.CLOSED);
            assertThat(closed.getBalance()).isEqualTo(Money.of(0));
        }

        @Test
        @DisplayName("CLOSED口座でclose()するとAccountAlreadyClosedExceptionがスローされること")
        void shouldThrowExceptionWhenClosingClosedAccount() {
            // Arrange
            Account account = Account.create("田中太郎").close();

            // Act & Assert
            assertThatThrownBy(account::close)
                    .isInstanceOf(AccountAlreadyClosedException.class);
        }

        @Test
        @DisplayName("CLOSED口座でdeposit()するとAccountAlreadyClosedExceptionがスローされること")
        void shouldThrowExceptionWhenDepositingToClosedAccount() {
            // Arrange
            Account account = Account.create("田中太郎").close();

            // Act & Assert
            assertThatThrownBy(() -> account.deposit(Money.of(1000)))
                    .isInstanceOf(AccountAlreadyClosedException.class);
        }

        @Test
        @DisplayName("CLOSED口座でwithdraw()するとAccountAlreadyClosedExceptionがスローされること")
        void shouldThrowExceptionWhenWithdrawingFromClosedAccount() {
            // Arrange
            Account closed = Account.reconstruct(
                    "id-1", new AccountNumber("1234567890"),
                    "田中太郎", Money.of(1000), AccountStatus.CLOSED,
                    java.time.LocalDateTime.now());

            // Act & Assert
            assertThatThrownBy(() -> closed.withdraw(Money.of(500)))
                    .isInstanceOf(AccountAlreadyClosedException.class);
        }

        @Test
        @DisplayName("close()はイミュータブルで元のAccountを変更しないこと")
        void shouldNotModifyOriginalAccountOnClose() {
            // Arrange
            Account original = Account.create("田中太郎").deposit(Money.of(5000));

            // Act
            original.close();

            // Assert
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
