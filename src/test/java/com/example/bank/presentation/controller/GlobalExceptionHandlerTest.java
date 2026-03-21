package com.example.bank.presentation.controller;

import com.example.bank.domain.model.AccountNotFoundException;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.InsufficientBalanceException;
import com.example.bank.domain.model.InvalidAmountException;
import com.example.bank.domain.model.Money;
import com.example.bank.presentation.response.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("AccountNotFoundException → 404レスポンス")
    void shouldReturn404ForAccountNotFound() {
        AccountNotFoundException ex =
                new AccountNotFoundException(new AccountNumber("1234567890"));

        ResponseEntity<ErrorResponse> response = handler.handleAccountNotFound(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody().code()).isEqualTo("ACCOUNT_NOT_FOUND");
        assertThat(response.getBody().message()).contains("1234567890");
    }

    @Test
    @DisplayName("InsufficientBalanceException → 422レスポンス")
    void shouldReturn422ForInsufficientBalance() {
        InsufficientBalanceException ex =
                new InsufficientBalanceException(Money.of(100), Money.of(500));

        ResponseEntity<ErrorResponse> response = handler.handleInsufficientBalance(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(422);
        assertThat(response.getBody().code()).isEqualTo("INSUFFICIENT_BALANCE");
    }

    @Test
    @DisplayName("InvalidAmountException → 400レスポンス")
    void shouldReturn400ForInvalidAmount() {
        InvalidAmountException ex = new InvalidAmountException(-100);

        ResponseEntity<ErrorResponse> response = handler.handleInvalidAmount(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().code()).isEqualTo("INVALID_AMOUNT");
    }
}
