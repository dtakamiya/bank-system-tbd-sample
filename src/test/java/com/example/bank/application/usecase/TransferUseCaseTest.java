package com.example.bank.application.usecase;

import com.example.bank.application.port.FeatureFlagService;
import com.example.bank.application.port.TransferFeePolicy;
import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNotFoundException;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.AccountStatus;
import com.example.bank.domain.model.FeatureDisabledException;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.SameAccountTransferException;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.model.TransactionType;
import com.example.bank.domain.model.TransferResult;
import com.example.bank.domain.repository.AccountRepository;
import com.example.bank.domain.repository.TransactionRepository;
import com.example.bank.domain.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransferUseCase")
class TransferUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FeatureFlagService featureFlagService;

    @Mock
    private TransferFeePolicy transferFeePolicy;

    @Mock
    private TransferService transferService;

    @InjectMocks
    private TransferUseCase transferUseCase;

    private final AccountNumber sourceAccountNumber = new AccountNumber("0000000001");
    private final AccountNumber targetAccountNumber = new AccountNumber("0000000002");
    private Account sourceAccount;
    private Account targetAccount;

    @BeforeEach
    void setUp() {
        sourceAccount = Account.reconstruct(
                "id-1", sourceAccountNumber, "送金元太郎",
                Money.of(50000), AccountStatus.ACTIVE, LocalDateTime.now());
        targetAccount = Account.reconstruct(
                "id-2", targetAccountNumber, "送金先花子",
                Money.of(20000), AccountStatus.ACTIVE, LocalDateTime.now());
    }

    @Nested
    @DisplayName("正常系")
    class Success {

        @BeforeEach
        void setUp() {
            lenient().when(featureFlagService.isEnabled("account-transfer")).thenReturn(true);
            lenient().when(accountRepository.findByAccountNumber(sourceAccountNumber))
                    .thenReturn(Optional.of(sourceAccount));
            lenient().when(accountRepository.findByAccountNumber(targetAccountNumber))
                    .thenReturn(Optional.of(targetAccount));
            lenient().when(accountRepository.save(any(Account.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
        }

        @Test
        @DisplayName("手数料なし送金: 口座が2件保存され、トランザクションが2件記録されること")
        void shouldTransferWithoutFee() {
            // Arrange
            when(transferFeePolicy.calculateFee(Money.of(10000))).thenReturn(Money.ZERO);
            Account updatedSource = sourceAccount.withdraw(Money.of(10000));
            Account updatedTarget = targetAccount.deposit(Money.of(10000));
            TransferResult result = new TransferResult(updatedSource, updatedTarget, Money.of(10000), Money.ZERO);
            when(transferService.transfer(sourceAccount, targetAccount, Money.of(10000), Money.ZERO))
                    .thenReturn(result);

            // Act
            transferUseCase.execute(sourceAccountNumber, targetAccountNumber, Money.of(10000));

            // Assert
            verify(accountRepository, times(2)).save(any(Account.class));
            verify(transactionRepository, times(2)).save(any(Transaction.class));
        }

        @Test
        @DisplayName("TRANSFER_OUTトランザクションにfeeとreferenceAccountNumberが記録されること")
        void shouldRecordTransferOutWithFeeAndReference() {
            // Arrange
            when(transferFeePolicy.calculateFee(Money.of(10000))).thenReturn(Money.of(100));
            Account updatedSource = sourceAccount.withdraw(Money.of(10100));
            Account updatedTarget = targetAccount.deposit(Money.of(10000));
            TransferResult result = new TransferResult(updatedSource, updatedTarget, Money.of(10000), Money.of(100));
            when(transferService.transfer(sourceAccount, targetAccount, Money.of(10000), Money.of(100)))
                    .thenReturn(result);

            // Act
            transferUseCase.execute(sourceAccountNumber, targetAccountNumber, Money.of(10000));

            // Assert
            ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
            verify(transactionRepository, times(2)).save(txCaptor.capture());

            Transaction transferOut = txCaptor.getAllValues().stream()
                    .filter(t -> t.getType() == TransactionType.TRANSFER_OUT)
                    .findFirst().orElseThrow();
            assertThat(transferOut.getAmount()).isEqualTo(Money.of(10000));
            assertThat(transferOut.getFee()).isEqualTo(Money.of(100));
            assertThat(transferOut.getReferenceAccountNumber()).isEqualTo(targetAccountNumber);

            Transaction transferIn = txCaptor.getAllValues().stream()
                    .filter(t -> t.getType() == TransactionType.TRANSFER_IN)
                    .findFirst().orElseThrow();
            assertThat(transferIn.getAmount()).isEqualTo(Money.of(10000));
            assertThat(transferIn.getReferenceAccountNumber()).isEqualTo(sourceAccountNumber);
        }
    }

    @Nested
    @DisplayName("異常系")
    class Failure {

        @Test
        @DisplayName("フラグOFFでFeatureDisabledExceptionがスローされること")
        void shouldThrowWhenFlagIsOff() {
            // Arrange
            when(featureFlagService.isEnabled("account-transfer")).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() ->
                    transferUseCase.execute(sourceAccountNumber, targetAccountNumber, Money.of(10000)))
                    .isInstanceOf(FeatureDisabledException.class);
            verify(accountRepository, never()).findByAccountNumber(any());
        }

        @Test
        @DisplayName("送金元口座が存在しない場合にAccountNotFoundExceptionがスローされること")
        void shouldThrowWhenSourceAccountNotFound() {
            // Arrange
            when(featureFlagService.isEnabled("account-transfer")).thenReturn(true);
            when(accountRepository.findByAccountNumber(sourceAccountNumber)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() ->
                    transferUseCase.execute(sourceAccountNumber, targetAccountNumber, Money.of(10000)))
                    .isInstanceOf(AccountNotFoundException.class);
        }

        @Test
        @DisplayName("送金先口座が存在しない場合にAccountNotFoundExceptionがスローされること")
        void shouldThrowWhenTargetAccountNotFound() {
            // Arrange
            when(featureFlagService.isEnabled("account-transfer")).thenReturn(true);
            when(accountRepository.findByAccountNumber(sourceAccountNumber))
                    .thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findByAccountNumber(targetAccountNumber)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() ->
                    transferUseCase.execute(sourceAccountNumber, targetAccountNumber, Money.of(10000)))
                    .isInstanceOf(AccountNotFoundException.class);
        }

        @Test
        @DisplayName("同一口座への送金でSameAccountTransferExceptionがスローされること")
        void shouldThrowForSameAccount() {
            // Arrange
            when(featureFlagService.isEnabled("account-transfer")).thenReturn(true);
            when(accountRepository.findByAccountNumber(sourceAccountNumber))
                    .thenReturn(Optional.of(sourceAccount));
            when(transferFeePolicy.calculateFee(any())).thenReturn(Money.ZERO);
            when(transferService.transfer(any(), any(), any(), any()))
                    .thenThrow(new SameAccountTransferException("0000000001"));

            // Act & Assert
            assertThatThrownBy(() ->
                    transferUseCase.execute(sourceAccountNumber, sourceAccountNumber, Money.of(10000)))
                    .isInstanceOf(SameAccountTransferException.class);
        }
    }
}
