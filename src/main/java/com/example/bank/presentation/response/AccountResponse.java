package com.example.bank.presentation.response;

import com.example.bank.domain.model.Account;

import java.math.BigDecimal;

/**
 * 口座情報のレスポンスDTO。
 *
 * @param accountNumber 口座番号
 * @param ownerName     口座名義人
 * @param balance       残高
 * @param status        口座ステータス（ACTIVE, CLOSED など）
 */
public record AccountResponse(
        String accountNumber,
        String ownerName,
        BigDecimal balance,
        String status
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getAccountNumber().value(),
                account.getOwnerName(),
                account.getBalance().getAmount(),
                account.getStatus().name()
        );
    }
}
