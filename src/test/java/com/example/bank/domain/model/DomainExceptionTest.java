package com.example.bank.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainExceptionTest {

    @Nested
    @DisplayName("InsufficientBalanceException")
    class InsufficientBalance {

        @Test
        @DisplayName("残高と出金額をメッセージに含むこと")
        void shouldContainBalanceAndAmountInMessage() {
            Money currentBalance = Money.of(500);
            Money withdrawAmount = Money.of(1000);

            InsufficientBalanceException exception =
                    new InsufficientBalanceException(currentBalance, withdrawAmount);

            assertThat(exception.getMessage()).contains("500");
            assertThat(exception.getMessage()).contains("1000");
            assertThat(exception).isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("AccountNotFoundException")
    class AccountNotFound {

        @Test
        @DisplayName("口座番号をメッセージに含むこと")
        void shouldContainAccountNumberInMessage() {
            AccountNumber accountNumber = new AccountNumber("1234567890");

            AccountNotFoundException exception =
                    new AccountNotFoundException(accountNumber);

            assertThat(exception.getMessage()).contains("1234567890");
            assertThat(exception).isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("InvalidAmountException")
    class InvalidAmount {

        @Test
        @DisplayName("不正金額をメッセージに含むこと")
        void shouldContainInvalidAmountInMessage() {
            InvalidAmountException exception =
                    new InvalidAmountException(-100);

            assertThat(exception.getMessage()).contains("-100");
            assertThat(exception).isInstanceOf(DomainException.class);
        }
    }
}
