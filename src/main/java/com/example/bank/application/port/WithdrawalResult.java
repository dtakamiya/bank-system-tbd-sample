package com.example.bank.application.port;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.Money;

/**
 * 出金処理の結果を保持するレコード。
 *
 * <p>更新後の口座、実際に引き出された金額、適用された手数料を格納する。</p>
 *
 * @param updatedAccount 出金後の口座
 * @param withdrawnAmount 実際に引き出された金額（手数料込み）
 * @param fee 適用された手数料
 */
public record WithdrawalResult(
        Account updatedAccount,
        Money withdrawnAmount,
        Money fee
) {
}
