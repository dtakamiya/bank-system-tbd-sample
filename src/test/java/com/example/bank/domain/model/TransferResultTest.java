package com.example.bank.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TransferResult")
class TransferResultTest {

    @Test
    @DisplayName("送金結果が送金元・送金先・送金額・手数料を保持すること")
    void shouldHoldTransferDetails() {
        // Arrange
        Account source = Account.create("送金元太郎").deposit(Money.of(50000));
        Account target = Account.create("送金先花子").deposit(Money.of(20000));
        Money amount = Money.of(10000);
        Money fee = Money.of(100);

        // Act
        TransferResult result = new TransferResult(source, target, amount, fee);

        // Assert
        assertThat(result.sourceAccount()).isEqualTo(source);
        assertThat(result.targetAccount()).isEqualTo(target);
        assertThat(result.amount()).isEqualTo(amount);
        assertThat(result.fee()).isEqualTo(fee);
    }

    @Test
    @DisplayName("手数料ゼロの送金結果を保持できること")
    void shouldHoldZeroFee() {
        // Arrange
        Account source = Account.create("送金元太郎");
        Account target = Account.create("送金先花子");

        // Act
        TransferResult result = new TransferResult(source, target, Money.of(5000), Money.ZERO);

        // Assert
        assertThat(result.fee()).isEqualTo(Money.ZERO);
    }
}
