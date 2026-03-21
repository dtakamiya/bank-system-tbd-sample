package com.example.bank.application.port;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.Money;

public record WithdrawalResult(
        Account updatedAccount,
        Money withdrawnAmount,
        Money fee
) {
}
