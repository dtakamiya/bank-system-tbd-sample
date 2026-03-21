package com.example.bank.application.policy;

import com.example.bank.application.port.WithdrawalResult;
import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.InsufficientBalanceException;
import com.example.bank.domain.model.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link StandardWithdrawalPolicy} のユニットテスト。
 *
 * <p>手数料なし出金ポリシーの振る舞いを検証する。</p>
 *
 * @see StandardWithdrawalPolicy
 */
@DisplayName("StandardWithdrawalPolicy: 手数料なし出金ポリシーのテスト")
class StandardWithdrawalPolicyTest {

    private final StandardWithdrawalPolicy policy = new StandardWithdrawalPolicy();

    @Test
    @DisplayName("出金額が残高以下の場合、出金できて手数料はゼロであること")
    void shouldWithdrawAndReturnFeeZero() {
        // Arrange
        Account account = Account.create("テスト太郎").deposit(Money.of(1000));

        // Act
        WithdrawalResult result = policy.withdraw(account, Money.of(500));

        // Assert
        assertEquals(Money.of(500), result.updatedAccount().getBalance());
        assertEquals(Money.of(500), result.withdrawnAmount());
        assertEquals(Money.ZERO, result.fee());
    }

    @Test
    @DisplayName("残高全額を出金できること")
    void shouldWithdrawAllBalance() {
        // Arrange
        Account account = Account.create("テスト太郎").deposit(Money.of(1000));

        // Act
        WithdrawalResult result = policy.withdraw(account, Money.of(1000));

        // Assert
        assertEquals(Money.ZERO, result.updatedAccount().getBalance());
        assertEquals(Money.ZERO, result.fee());
    }

    @Test
    @DisplayName("残高不足の場合にInsufficientBalanceExceptionがスローされること")
    void shouldThrowInsufficientBalanceException() {
        // Arrange
        Account account = Account.create("テスト太郎").deposit(Money.of(500));

        // Act & Assert
        assertThrows(InsufficientBalanceException.class,
                () -> policy.withdraw(account, Money.of(1000)));
    }
}
