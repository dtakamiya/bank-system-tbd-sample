package com.example.bank.application.policy;

import com.example.bank.application.port.WithdrawalResult;
import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.InsufficientBalanceException;
import com.example.bank.domain.model.Money;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StandardWithdrawalPolicyTest {

    private final StandardWithdrawalPolicy policy = new StandardWithdrawalPolicy();

    @Test
    void shouldWithdrawAndReturnFeeZero() {
        Account account = Account.create("テスト太郎").deposit(Money.of(1000));

        WithdrawalResult result = policy.withdraw(account, Money.of(500));

        assertEquals(Money.of(500), result.updatedAccount().getBalance());
        assertEquals(Money.of(500), result.withdrawnAmount());
        assertEquals(Money.ZERO, result.fee());
    }

    @Test
    void shouldWithdrawAllBalance() {
        Account account = Account.create("テスト太郎").deposit(Money.of(1000));

        WithdrawalResult result = policy.withdraw(account, Money.of(1000));

        assertEquals(Money.ZERO, result.updatedAccount().getBalance());
        assertEquals(Money.ZERO, result.fee());
    }

    @Test
    void shouldThrowInsufficientBalanceException() {
        Account account = Account.create("テスト太郎").deposit(Money.of(500));

        assertThrows(InsufficientBalanceException.class,
                () -> policy.withdraw(account, Money.of(1000)));
    }
}
