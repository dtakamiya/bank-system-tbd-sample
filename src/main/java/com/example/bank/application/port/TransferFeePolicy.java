package com.example.bank.application.port;

import com.example.bank.domain.model.Money;

/**
 * 送金手数料ポリシーを定義するポートインターフェース。
 *
 * <p>送金額に対する手数料計算ロジックを抽象化する。
 * フィーチャーフラグにより手数料なし・固定手数料などの実装を切り替える。</p>
 */
@FunctionalInterface
public interface TransferFeePolicy {

    /**
     * 送金額に対する手数料を計算する。
     *
     * @param amount 送金額
     * @return 手数料
     */
    Money calculateFee(Money amount);
}
