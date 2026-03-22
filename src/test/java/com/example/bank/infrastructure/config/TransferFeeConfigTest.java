package com.example.bank.infrastructure.config;

import com.example.bank.application.policy.FixedFeeTransferPolicy;
import com.example.bank.application.policy.NoFeeTransferPolicy;
import com.example.bank.application.port.TransferFeePolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

class TransferFeeConfigTest {

    @Nested
    @SpringBootTest
    @TestPropertySource(properties = {
            "bank.features.transfer-fee=false"
    })
    @DisplayName("transfer-fee=false の場合")
    class WhenTransferFeeDisabled {

        @Autowired
        private TransferFeePolicy transferFeePolicy;

        @Test
        @DisplayName("@Primary Bean が NoFeeTransferPolicy であること")
        void shouldRegisterNoFeePolicyAsPrimary() {
            assertThat(transferFeePolicy).isInstanceOf(NoFeeTransferPolicy.class);
        }
    }

    @Nested
    @SpringBootTest
    @TestPropertySource(properties = {
            "bank.features.transfer-fee=true",
            "bank.transfer.fee-rate=0.01"
    })
    @DisplayName("transfer-fee=true の場合")
    class WhenTransferFeeEnabled {

        @Autowired
        private TransferFeePolicy transferFeePolicy;

        @Test
        @DisplayName("@Primary Bean が FixedFeeTransferPolicy であること")
        void shouldRegisterFixedFeePolicyAsPrimary() {
            assertThat(transferFeePolicy).isInstanceOf(FixedFeeTransferPolicy.class);
        }
    }
}
