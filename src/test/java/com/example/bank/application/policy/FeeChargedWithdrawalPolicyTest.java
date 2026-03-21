package com.example.bank.application.policy;

import com.example.bank.application.port.WithdrawalResult;
import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.InsufficientBalanceException;
import com.example.bank.domain.model.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeeChargedWithdrawalPolicyTest {

    @Test
    void shouldChargeFeeAndWithdraw() {
        FeeChargedWithdrawalPolicy policy = new FeeChargedWithdrawalPolicy(new BigDecimal("0.01"));
        Account account = Account.create("テスト太郎").deposit(Money.of(10000));

        WithdrawalResult result = policy.withdraw(account, Money.of(3000));

        assertEquals(Money.of(BigDecimal.valueOf(30).setScale(2)), result.fee());
        assertEquals(Money.of(BigDecimal.valueOf(3030).setScale(2)), result.withdrawnAmount());
        assertEquals(Money.of(BigDecimal.valueOf(6970).setScale(2)), result.updatedAccount().getBalance());
    }

    @Test
    void shouldCalculateFeeCorrectlyWithDifferentRate() {
        FeeChargedWithdrawalPolicy policy = new FeeChargedWithdrawalPolicy(new BigDecimal("0.05"));
        Account account = Account.create("テスト太郎").deposit(Money.of(10000));

        WithdrawalResult result = policy.withdraw(account, Money.of(2000));

        assertEquals(Money.of(BigDecimal.valueOf(100).setScale(2)), result.fee());
        assertEquals(Money.of(BigDecimal.valueOf(2100).setScale(2)), result.withdrawnAmount());
    }

    @Test
    void shouldThrowInsufficientBalanceWhenFeeAddedExceedsBalance() {
        FeeChargedWithdrawalPolicy policy = new FeeChargedWithdrawalPolicy(new BigDecimal("0.01"));
        Account account = Account.create("テスト太郎").deposit(Money.of(3000));

        assertThrows(InsufficientBalanceException.class,
                () -> policy.withdraw(account, Money.of(3000)));
    }
}
