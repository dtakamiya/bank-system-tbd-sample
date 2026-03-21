package com.example.bank.application.port;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link WithdrawalResult} のユニットテスト。
 *
 * <p>出金結果レコードが、更新後の口座・出金額・手数料を
 * 正しく保持することを検証する。</p>
 *
 * @see WithdrawalResult
 */
@DisplayName("WithdrawalResult: 出金結果レコードのテスト")
class WithdrawalResultTest {

    @Test
    @DisplayName("手数料ゼロの場合、口座・出金額・手数料が正しく保持されること")
    void shouldHoldUpdatedAccountAndWithdrawnAmountAndFeeZero() {
        // Arrange
        Account account = Account.create("テスト太郎");
        Account deposited = account.deposit(Money.of(1000));
        Account withdrawn = deposited.withdraw(Money.of(500));

        // Act
        WithdrawalResult result = new WithdrawalResult(withdrawn, Money.of(500), Money.ZERO);

        // Assert
        assertEquals(withdrawn, result.updatedAccount());
        assertEquals(Money.of(500), result.withdrawnAmount());
        assertEquals(Money.ZERO, result.fee());
    }

    @Test
    @DisplayName("手数料がゼロより大きい場合、出金額と手数料が正しく保持されること")
    void shouldHoldFeeGreaterThanZero() {
        // Arrange
        Account account = Account.create("テスト太郎");
        Account deposited = account.deposit(Money.of(10000));
        Account withdrawn = deposited.withdraw(Money.of(3030));

        // Act
        WithdrawalResult result = new WithdrawalResult(withdrawn, Money.of(3030), Money.of(30));

        // Assert
        assertEquals(Money.of(3030), result.withdrawnAmount());
        assertEquals(Money.of(30), result.fee());
    }

    @Test
    @DisplayName("手数料なしの場合にfee()がMoney.ZEROを返すこと")
    void shouldReturnMoneyZeroAsFeeWhenNoFeeApplied() {
        // Arrange
        Account account = Account.create("テスト太郎");
        Account deposited = account.deposit(Money.of(1000));
        Account withdrawn = deposited.withdraw(Money.of(500));

        // Act
        WithdrawalResult result = new WithdrawalResult(withdrawn, Money.of(500), Money.ZERO);

        // Assert
        assertEquals(Money.ZERO, result.fee());
    }
}
