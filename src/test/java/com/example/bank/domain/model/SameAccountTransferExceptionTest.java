package com.example.bank.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SameAccountTransferException")
class SameAccountTransferExceptionTest {

    @Test
    @DisplayName("例外メッセージに口座番号が含まれること")
    void shouldContainAccountNumberInMessage() {
        // Arrange
        String accountNumber = "1234567890";

        // Act
        SameAccountTransferException exception = new SameAccountTransferException(accountNumber);

        // Assert
        assertThat(exception.getMessage()).contains(accountNumber);
    }

    @Test
    @DisplayName("DomainExceptionのサブクラスであること")
    void shouldBeDomainExceptionSubclass() {
        // Act
        SameAccountTransferException exception = new SameAccountTransferException("1234567890");

        // Assert
        assertThat(exception).isInstanceOf(DomainException.class);
    }
}
