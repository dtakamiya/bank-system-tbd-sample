package com.example.bank.presentation.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TransferRequest")
class TransferRequestTest {

    @Test
    @DisplayName("送金先口座番号と金額を保持すること")
    void shouldHoldTargetAccountNumberAndAmount() {
        // Act
        TransferRequest request = new TransferRequest("0000000002", 10000L);

        // Assert
        assertThat(request.targetAccountNumber()).isEqualTo("0000000002");
        assertThat(request.amount()).isEqualTo(10000L);
    }
}
