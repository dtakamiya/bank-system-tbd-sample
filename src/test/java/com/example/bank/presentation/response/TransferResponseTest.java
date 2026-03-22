package com.example.bank.presentation.response;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.TransferResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TransferResponse")
class TransferResponseTest {

    @Test
    @DisplayName("送金結果から正しいレスポンスが生成されること")
    void shouldCreateFromTransferResult() {
        // Arrange
        Account source = Account.create("送金元太郎").deposit(Money.of(50000)).withdraw(Money.of(10100));
        Account target = Account.create("送金先花子").deposit(Money.of(30000));
        TransferResult result = new TransferResult(source, target, Money.of(10000), Money.of(100));

        // Act
        TransferResponse response = TransferResponse.from(result);

        // Assert
        assertThat(response.sourceAccountNumber()).isEqualTo(source.getAccountNumber().value());
        assertThat(response.targetAccountNumber()).isEqualTo(target.getAccountNumber().value());
        assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("10000.00"));
        assertThat(response.fee()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(response.sourceBalanceAfter()).isEqualByComparingTo(source.getBalance().getAmount());
        assertThat(response.targetBalanceAfter()).isEqualByComparingTo(target.getBalance().getAmount());
    }
}
