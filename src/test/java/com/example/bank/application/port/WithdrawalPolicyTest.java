package com.example.bank.application.port;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@link WithdrawalPolicy} のユニットテスト。
 *
 * <p>出金ポリシーインターフェースが関数型インターフェースとして
 * ラムダ式や匿名クラスで実装可能であることを検証する。</p>
 *
 * @see WithdrawalPolicy
 */
@DisplayName("WithdrawalPolicy: 出金ポリシーインターフェースのテスト")
class WithdrawalPolicyTest {

    @Test
    @DisplayName("ラムダ式で実装でき、正常に出金結果を返すこと")
    void shouldBeImplementableAsLambdaOrAnonymousClass() {
        // Arrange
        WithdrawalPolicy policy = (account, amount) -> {
            Account updated = account.withdraw(amount);
            return new WithdrawalResult(updated, amount, Money.ZERO);
        };

        Account account = Account.create("テスト太郎");
        Account deposited = account.deposit(Money.of(1000));

        // Act
        WithdrawalResult result = policy.withdraw(deposited, Money.of(500));

        // Assert
        assertNotNull(result);
        assertNotNull(result.updatedAccount());
    }
}
