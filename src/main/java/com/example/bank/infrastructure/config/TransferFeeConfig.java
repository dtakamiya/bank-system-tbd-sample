package com.example.bank.infrastructure.config;

import com.example.bank.application.policy.FixedFeeTransferPolicy;
import com.example.bank.application.policy.NoFeeTransferPolicy;
import com.example.bank.application.port.FeatureFlagService;
import com.example.bank.application.port.TransferFeePolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;

/**
 * 送金手数料ポリシーのSpring Bean定義。
 *
 * <p>フィーチャーフラグ {@code transfer-fee} の状態に応じて、
 * 固定手数料ポリシーまたは手数料なしポリシーを切り替える。</p>
 */
@Configuration
public class TransferFeeConfig {

    @Bean
    @Primary
    public TransferFeePolicy transferFeePolicy(
            FeatureFlagService featureFlagService,
            @Value("${bank.transfer.fee-rate:0.01}") BigDecimal feeRate) {
        if (featureFlagService.isEnabled("transfer-fee")) {
            return new FixedFeeTransferPolicy(feeRate);
        }
        return new NoFeeTransferPolicy();
    }
}
