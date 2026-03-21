package com.example.bank.application.policy;

import com.example.bank.application.port.WithdrawalResult;
import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.InsufficientBalanceException;
import com.example.bank.domain.model.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link FeeChargedWithdrawalPolicy} のユニットテスト。
 *
 * <p>手数料付き出金ポリシーの振る舞いを検証する。
 * 手数料率に応じた手数料計算と、手数料込みの残高不足判定を確認する。</p>
 *
 * @see FeeChargedWithdrawalPolicy
 */
@DisplayName("FeeChargedWithdrawalPolicy: 手数料付き出金ポリシーのテスト")
class FeeChargedWithdrawalPolicyTest {

    @Test
    @DisplayName("手数料率1%で出金した場合、手数料・出金合計額・残高が正しいこと")
    void shouldChargeFeeAndWithdraw() {
        // Arrange
        FeeChargedWithdrawalPolicy policy = new FeeChargedWithdrawalPolicy(new BigDecimal("0.01"));
        Account account = Account.create("テスト太郎").deposit(Money.of(10000));

        // Act
        WithdrawalResult result = policy.withdraw(account, Money.of(3000));

        // Assert
        assertEquals(Money.of(BigDecimal.valueOf(30).setScale(2)), result.fee());
        assertEquals(Money.of(BigDecimal.valueOf(3030).setScale(2)), result.withdrawnAmount());
        assertEquals(Money.of(BigDecimal.valueOf(6970).setScale(2)), result.updatedAccount().getBalance());
    }

    @Test
    @DisplayName("手数料率5%で出金した場合、手数料・出金合計額が正しく計算されること")
    void shouldCalculateFeeCorrectlyWithDifferentRate() {
        // Arrange
        FeeChargedWithdrawalPolicy policy = new FeeChargedWithdrawalPolicy(new BigDecimal("0.05"));
        Account account = Account.create("テスト太郎").deposit(Money.of(10000));

        // Act
        WithdrawalResult result = policy.withdraw(account, Money.of(2000));

        // Assert
        assertEquals(Money.of(BigDecimal.valueOf(100).setScale(2)), result.fee());
        assertEquals(Money.of(BigDecimal.valueOf(2100).setScale(2)), result.withdrawnAmount());
    }

    @Test
    @DisplayName("手数料を加算すると残高を超える場合にInsufficientBalanceExceptionがスローされること")
    void shouldThrowInsufficientBalanceWhenFeeAddedExceedsBalance() {
        // Arrange
        FeeChargedWithdrawalPolicy policy = new FeeChargedWithdrawalPolicy(new BigDecimal("0.01"));
        Account account = Account.create("テスト太郎").deposit(Money.of(3000));

        // Act & Assert
        assertThrows(InsufficientBalanceException.class,
                () -> policy.withdraw(account, Money.of(3000)));
    }
}
