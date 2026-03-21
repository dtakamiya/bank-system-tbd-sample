package com.example.bank.infrastructure.config;

import com.example.bank.application.policy.FeeChargedWithdrawalPolicy;
import com.example.bank.application.policy.StandardWithdrawalPolicy;
import com.example.bank.application.port.FeatureFlagService;
import com.example.bank.application.port.WithdrawalPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;

/**
 * 出金ポリシーのSpring Bean定義。
 *
 * <p>フィーチャーフラグ {@code withdrawal-fee} の状態に応じて、
 * 手数料付き出金ポリシーまたは標準出金ポリシーを切り替える。</p>
 */
@Configuration
public class WithdrawalPolicyConfig {

    /**
     * プライマリの出金ポリシーBeanを生成する。
     *
     * <p>フィーチャーフラグが有効な場合は手数料付きポリシーを、
     * 無効な場合は標準ポリシーを返す。</p>
     *
     * @param featureFlagService フィーチャーフラグ判定サービス
     * @param feeRate 手数料率（デフォルト: 0.01）
     * @return 適用する出金ポリシー
     */
    @Bean
    @Primary
    public WithdrawalPolicy withdrawalPolicy(
            FeatureFlagService featureFlagService,
            @Value("${bank.withdrawal.fee-rate:0.01}") BigDecimal feeRate) {
        if (featureFlagService.isEnabled("withdrawal-fee")) {
            return new FeeChargedWithdrawalPolicy(feeRate);
        }
        return new StandardWithdrawalPolicy();
    }

    /**
     * 標準出金ポリシーBeanを生成する。
     *
     * <p>手数料なしの標準的な出金処理を行うポリシーを提供する。</p>
     *
     * @return 標準出金ポリシー
     */
    @Bean("standardWithdrawalPolicy")
    public WithdrawalPolicy standardWithdrawalPolicy() {
        return new StandardWithdrawalPolicy();
    }
}
