package com.example.bank.application.port;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.Money;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WithdrawalResultTest {

    @Test
    void shouldHoldUpdatedAccountAndWithdrawnAmountAndFeeZero() {
        Account account = Account.create("テスト太郎");
        Account deposited = account.deposit(Money.of(1000));
        Account withdrawn = deposited.withdraw(Money.of(500));

        WithdrawalResult result = new WithdrawalResult(withdrawn, Money.of(500), Money.ZERO);

        assertEquals(withdrawn, result.updatedAccount());
        assertEquals(Money.of(500), result.withdrawnAmount());
        assertEquals(Money.ZERO, result.fee());
    }

    @Test
    void shouldHoldFeeGreaterThanZero() {
        Account account = Account.create("テスト太郎");
        Account deposited = account.deposit(Money.of(10000));
        Account withdrawn = deposited.withdraw(Money.of(3030));

        WithdrawalResult result = new WithdrawalResult(withdrawn, Money.of(3030), Money.of(30));

        assertEquals(Money.of(3030), result.withdrawnAmount());
        assertEquals(Money.of(30), result.fee());
    }

    @Test
    void shouldReturnMoneyZeroAsFeeWhenNoFeeApplied() {
        Account account = Account.create("テスト太郎");
        Account deposited = account.deposit(Money.of(1000));
        Account withdrawn = deposited.withdraw(Money.of(500));

        WithdrawalResult result = new WithdrawalResult(withdrawn, Money.of(500), Money.ZERO);

        assertEquals(Money.ZERO, result.fee());
    }
}
