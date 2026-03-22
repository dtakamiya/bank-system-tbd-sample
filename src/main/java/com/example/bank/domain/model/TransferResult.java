package com.example.bank.domain.model;

/**
 * 送金結果を保持するレコード。
 *
 * @param sourceAccount 出金後の送金元口座
 * @param targetAccount 入金後の送金先口座
 * @param amount        送金額
 * @param fee           手数料
 */
public record TransferResult(
        Account sourceAccount,
        Account targetAccount,
        Money amount,
        Money fee
) {
}
