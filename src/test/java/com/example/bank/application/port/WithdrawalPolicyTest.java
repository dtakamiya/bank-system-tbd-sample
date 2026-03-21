package com.example.bank.application.port;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.Money;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class WithdrawalPolicyTest {

    @Test
    void shouldBeImplementableAsLambdaOrAnonymousClass() {
        WithdrawalPolicy policy = (account, amount) -> {
            Account updated = account.withdraw(amount);
            return new WithdrawalResult(updated, amount, Money.ZERO);
        };

        Account account = Account.create("テスト太郎");
        Account deposited = account.deposit(Money.of(1000));

        WithdrawalResult result = policy.withdraw(deposited, Money.of(500));

        assertNotNull(result);
        assertNotNull(result.updatedAccount());
    }
}
