package com.example.bank.domain.service;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountAlreadyClosedException;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.AccountStatus;
import com.example.bank.domain.model.InsufficientBalanceException;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.SameAccountTransferException;
import com.example.bank.domain.model.TransferResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TransferService")
class TransferServiceTest {

    private TransferService transferService;

    @BeforeEach
    void setUp() {
        transferService = new TransferService();
    }

    private Account createAccount(String number, String owner, long balance) {
        return Account.reconstruct(
                "id-" + number, new AccountNumber(number), owner,
                Money.of(balance), AccountStatus.ACTIVE, LocalDateTime.now());
    }

    @Nested
    @DisplayName("正常系")
    class Success {

        @Test
        @DisplayName("手数料なし送金: 送金元の残高が減り、送金先の残高が増えること")
        void shouldTransferWithoutFee() {
            // Arrange
            Account source = createAccount("0000000001", "送金元太郎", 50000);
            Account target = createAccount("0000000002", "送金先花子", 20000);

            // Act
            TransferResult result = transferService.transfer(
                    source, target, Money.of(10000), Money.ZERO);

            // Assert
            assertThat(result.sourceAccount().getBalance()).isEqualTo(Money.of(40000));
            assertThat(result.targetAccount().getBalance()).isEqualTo(Money.of(30000));
            assertThat(result.amount()).isEqualTo(Money.of(10000));
            assertThat(result.fee()).isEqualTo(Money.ZERO);
        }

        @Test
        @DisplayName("手数料あり送金: 送金元から送金額＋手数料が引かれること")
        void shouldTransferWithFee() {
            // Arrange
            Account source = createAccount("0000000001", "送金元太郎", 50000);
            Account target = createAccount("0000000002", "送金先花子", 20000);

            // Act
            TransferResult result = transferService.transfer(
                    source, target, Money.of(10000), Money.of(100));

            // Assert
            assertThat(result.sourceAccount().getBalance()).isEqualTo(Money.of(39900));
            assertThat(result.targetAccount().getBalance()).isEqualTo(Money.of(30000));
            assertThat(result.amount()).isEqualTo(Money.of(10000));
            assertThat(result.fee()).isEqualTo(Money.of(100));
        }
    }

    @Nested
    @DisplayName("異常系")
    class Failure {

        @Test
        @DisplayName("同一口座への送金でSameAccountTransferExceptionがスローされること")
        void shouldThrowForSameAccount() {
            // Arrange
            Account source = createAccount("0000000001", "太郎", 50000);
            Account target = createAccount("0000000001", "太郎", 50000);

            // Act & Assert
            assertThatThrownBy(() ->
                    transferService.transfer(source, target, Money.of(10000), Money.ZERO))
                    .isInstanceOf(SameAccountTransferException.class);
        }

        @Test
        @DisplayName("CLOSED送金元口座でAccountAlreadyClosedExceptionがスローされること")
        void shouldThrowForClosedSourceAccount() {
            // Arrange
            Account source = Account.reconstruct(
                    "id-1", new AccountNumber("0000000001"), "太郎",
                    Money.of(50000), AccountStatus.CLOSED, LocalDateTime.now());
            Account target = createAccount("0000000002", "花子", 20000);

            // Act & Assert
            assertThatThrownBy(() ->
                    transferService.transfer(source, target, Money.of(10000), Money.ZERO))
                    .isInstanceOf(AccountAlreadyClosedException.class);
        }

        @Test
        @DisplayName("CLOSED送金先口座でAccountAlreadyClosedExceptionがスローされること")
        void shouldThrowForClosedTargetAccount() {
            // Arrange
            Account source = createAccount("0000000001", "太郎", 50000);
            Account target = Account.reconstruct(
                    "id-2", new AccountNumber("0000000002"), "花子",
                    Money.of(20000), AccountStatus.CLOSED, LocalDateTime.now());

            // Act & Assert
            assertThatThrownBy(() ->
                    transferService.transfer(source, target, Money.of(10000), Money.ZERO))
                    .isInstanceOf(AccountAlreadyClosedException.class);
        }

        @Test
        @DisplayName("残高不足でInsufficientBalanceExceptionがスローされること")
        void shouldThrowForInsufficientBalance() {
            // Arrange
            Account source = createAccount("0000000001", "太郎", 5000);
            Account target = createAccount("0000000002", "花子", 20000);

            // Act & Assert
            assertThatThrownBy(() ->
                    transferService.transfer(source, target, Money.of(10000), Money.ZERO))
                    .isInstanceOf(InsufficientBalanceException.class);
        }

        @Test
        @DisplayName("手数料込みで残高不足の場合にInsufficientBalanceExceptionがスローされること")
        void shouldThrowForInsufficientBalanceIncludingFee() {
            // Arrange
            Account source = createAccount("0000000001", "太郎", 10000);
            Account target = createAccount("0000000002", "花子", 20000);

            // Act & Assert
            assertThatThrownBy(() ->
                    transferService.transfer(source, target, Money.of(10000), Money.of(100)))
                    .isInstanceOf(InsufficientBalanceException.class);
        }
    }
}
