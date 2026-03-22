package com.example.bank.infrastructure.config;

import com.example.bank.application.policy.FixedFeeTransferPolicy;
import com.example.bank.application.policy.NoFeeTransferPolicy;
import com.example.bank.application.port.FeatureFlagService;
import com.example.bank.application.port.TransferFeePolicy;
import com.example.bank.domain.service.TransferService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;

/**
 * 送金関連のSpring Bean定義。
 *
 * <p>フィーチャーフラグ {@code transfer-fee} の状態に応じた手数料ポリシーの切替と、
 * TransferServiceドメインサービスのBean登録を行う。</p>
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

    @Bean
    public TransferService transferService() {
        return new TransferService();
    }
}
