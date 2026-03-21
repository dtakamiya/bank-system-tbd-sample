package com.example.bank.application.port;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.Money;

/**
 * 出金ポリシーを定義するポートインターフェース。
 *
 * <p>出金時の手数料計算や金額調整のロジックを抽象化する。
 * 実装クラスによって標準出金や手数料付き出金など異なるポリシーを適用できる。</p>
 */
@FunctionalInterface
public interface WithdrawalPolicy {

    /**
     * 指定された口座から出金を実行する。
     *
     * @param account 出金元の口座
     * @param amount 出金希望額
     * @return 出金結果（更新後の口座、実際の出金額、手数料を含む）
     */
    WithdrawalResult withdraw(Account account, Money amount);
}
