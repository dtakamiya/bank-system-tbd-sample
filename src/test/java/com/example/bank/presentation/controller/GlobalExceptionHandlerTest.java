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

/**
 * {@link GlobalExceptionHandler} のユニットテスト。
 *
 * <p>ドメイン例外（口座未存在・残高不足・不正金額）が適切なHTTPステータスコードと
 * エラーレスポンスに変換されることを検証する。</p>
 *
 * @see GlobalExceptionHandler
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("AccountNotFoundException → 404レスポンス")
    void shouldReturn404ForAccountNotFound() {
        // Arrange
        AccountNotFoundException ex =
                new AccountNotFoundException(new AccountNumber("1234567890"));

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleAccountNotFound(ex);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody().code()).isEqualTo("ACCOUNT_NOT_FOUND");
        assertThat(response.getBody().message()).contains("1234567890");
    }

    @Test
    @DisplayName("InsufficientBalanceException → 422レスポンス")
    void shouldReturn422ForInsufficientBalance() {
        // Arrange
        InsufficientBalanceException ex =
                new InsufficientBalanceException(Money.of(100), Money.of(500));

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleInsufficientBalance(ex);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(422);
        assertThat(response.getBody().code()).isEqualTo("INSUFFICIENT_BALANCE");
    }

    @Test
    @DisplayName("InvalidAmountException → 400レスポンス")
    void shouldReturn400ForInvalidAmount() {
        // Arrange
        InvalidAmountException ex = new InvalidAmountException(-100);

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleInvalidAmount(ex);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().code()).isEqualTo("INVALID_AMOUNT");
    }
}
