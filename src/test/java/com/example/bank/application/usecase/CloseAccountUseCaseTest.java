package com.example.bank.application.usecase;

import com.example.bank.application.port.FeatureFlagService;
import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountAlreadyClosedException;
import com.example.bank.domain.model.AccountNotFoundException;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.AccountStatus;
import com.example.bank.domain.model.FeatureDisabledException;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.model.TransactionType;
import com.example.bank.domain.repository.AccountRepository;
import com.example.bank.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CloseAccountUseCaseTest {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private FeatureFlagService featureFlagService;
    private CloseAccountUseCase closeAccountUseCase;

    private final AccountNumber accountNumber = new AccountNumber("1234567890");

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        featureFlagService = mock(FeatureFlagService.class);
        closeAccountUseCase = new CloseAccountUseCase(
                accountRepository, transactionRepository, featureFlagService);
    }

    @Nested
    @DisplayName("正常系")
    class Success {

        @Test
        @DisplayName("残高ゼロの口座を解約できること（REFUNDトランザクションなし）")
        void shouldCloseAccountWithZeroBalance() {
            Account account = Account.reconstruct(
                    "id-1", accountNumber, "田中太郎", Money.of(0),
                    AccountStatus.ACTIVE, LocalDateTime.now());
            when(featureFlagService.isEnabled("account-closure")).thenReturn(true);
            when(accountRepository.findByAccountNumber(accountNumber))
                    .thenReturn(Optional.of(account));
            when(accountRepository.save(any(Account.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Account result = closeAccountUseCase.execute(accountNumber);

            assertThat(result.getStatus()).isEqualTo(AccountStatus.CLOSED);
            assertThat(result.getBalance()).isEqualTo(Money.of(0));
            verify(transactionRepository, never()).save(any(Transaction.class));
        }

        @Test
        @DisplayName("残高ありの口座を解約できること（REFUNDトランザクションあり）")
        void shouldCloseAccountWithBalanceAndRecordRefund() {
            Account account = Account.reconstruct(
                    "id-1", accountNumber, "田中太郎", Money.of(5000),
                    AccountStatus.ACTIVE, LocalDateTime.now());
            when(featureFlagService.isEnabled("account-closure")).thenReturn(true);
            when(accountRepository.findByAccountNumber(accountNumber))
                    .thenReturn(Optional.of(account));
            when(accountRepository.save(any(Account.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Account result = closeAccountUseCase.execute(accountNumber);

            assertThat(result.getStatus()).isEqualTo(AccountStatus.CLOSED);
            assertThat(result.getBalance()).isEqualTo(Money.of(0));

            ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
            verify(transactionRepository).save(txCaptor.capture());
            Transaction refundTx = txCaptor.getValue();
            assertThat(refundTx.getType()).isEqualTo(TransactionType.REFUND);
            assertThat(refundTx.getAmount()).isEqualTo(Money.of(5000));
            assertThat(refundTx.getBalanceAfter()).isEqualTo(Money.of(0));
        }
    }

    @Nested
    @DisplayName("異常系")
    class Failure {

        @Test
        @DisplayName("フラグOFFの場合にFeatureDisabledExceptionがスローされること")
        void shouldThrowWhenFeatureFlagDisabled() {
            when(featureFlagService.isEnabled("account-closure")).thenReturn(false);

            assertThatThrownBy(() -> closeAccountUseCase.execute(accountNumber))
                    .isInstanceOf(FeatureDisabledException.class);
        }

        @Test
        @DisplayName("口座が存在しない場合にAccountNotFoundExceptionがスローされること")
        void shouldThrowWhenAccountNotFound() {
            when(featureFlagService.isEnabled("account-closure")).thenReturn(true);
            when(accountRepository.findByAccountNumber(accountNumber))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> closeAccountUseCase.execute(accountNumber))
                    .isInstanceOf(AccountNotFoundException.class);
        }

        @Test
        @DisplayName("解約済み口座の場合にAccountAlreadyClosedExceptionがスローされること")
        void shouldThrowWhenAccountAlreadyClosed() {
            Account closedAccount = Account.reconstruct(
                    "id-1", accountNumber, "田中太郎", Money.of(0),
                    AccountStatus.CLOSED, LocalDateTime.now());
            when(featureFlagService.isEnabled("account-closure")).thenReturn(true);
            when(accountRepository.findByAccountNumber(accountNumber))
                    .thenReturn(Optional.of(closedAccount));

            assertThatThrownBy(() -> closeAccountUseCase.execute(accountNumber))
                    .isInstanceOf(AccountAlreadyClosedException.class);
        }
    }
}
