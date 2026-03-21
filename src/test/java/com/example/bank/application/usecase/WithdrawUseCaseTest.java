package com.example.bank.application.usecase;

import com.example.bank.application.port.FeatureFlagService;
import com.example.bank.application.port.WithdrawalPolicy;
import com.example.bank.application.port.WithdrawalResult;
import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNotFoundException;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.AccountStatus;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.model.TransactionType;
import com.example.bank.domain.repository.AccountRepository;
import com.example.bank.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithdrawUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FeatureFlagService featureFlagService;

    @Mock
    private WithdrawalPolicy withdrawalPolicy;

    private WithdrawUseCase withdrawUseCase;

    private final AccountNumber accountNumber = new AccountNumber("1234567890");
    private Account account;

    @BeforeEach
    void setUp() {
        withdrawUseCase = new WithdrawUseCase(
                accountRepository, transactionRepository, featureFlagService, withdrawalPolicy);

        lenient().when(featureFlagService.isEnabled("withdrawal")).thenReturn(true);
        account = Account.reconstruct(
                "id-1", accountNumber, "田中太郎", Money.of(1000), AccountStatus.ACTIVE, LocalDateTime.now());
        lenient().when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));
        lenient().when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("Policyに出金を委譲し、結果を保存すること")
    void shouldDelegateToWithdrawalPolicyAndSaveResult() {
        Account withdrawn = account.withdraw(Money.of(500));
        WithdrawalResult policyResult = new WithdrawalResult(withdrawn, Money.of(500), Money.ZERO);
        when(withdrawalPolicy.withdraw(account, Money.of(500))).thenReturn(policyResult);

        Account result = withdrawUseCase.execute(accountNumber, Money.of(500));

        assertThat(result.getBalance()).isEqualTo(Money.of(500));
        verify(withdrawalPolicy).withdraw(account, Money.of(500));
    }

    @Test
    @DisplayName("出金後にAccountが更新保存されること")
    void shouldSaveUpdatedAccount() {
        Account withdrawn = account.withdraw(Money.of(500));
        WithdrawalResult policyResult = new WithdrawalResult(withdrawn, Money.of(500), Money.ZERO);
        when(withdrawalPolicy.withdraw(account, Money.of(500))).thenReturn(policyResult);

        withdrawUseCase.execute(accountNumber, Money.of(500));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertThat(captor.getValue().getBalance()).isEqualTo(Money.of(500));
    }

    @Test
    @DisplayName("手数料込み金額でTransaction記録されること")
    void shouldSaveFeeIncludedAmountAsTransactionAmount() {
        Account withdrawn = account.withdraw(Money.of(530));
        WithdrawalResult policyResult = new WithdrawalResult(withdrawn, Money.of(530), Money.of(30));
        when(withdrawalPolicy.withdraw(eq(account), eq(Money.of(500)))).thenReturn(policyResult);

        withdrawUseCase.execute(accountNumber, Money.of(500));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        Transaction saved = captor.getValue();
        assertThat(saved.getType()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(saved.getAmount()).isEqualTo(Money.of(530));
        assertThat(saved.getBalanceAfter()).isEqualTo(Money.of(470));
    }

    @Test
    @DisplayName("withdrawalフラグOFFでFeatureDisabledExceptionがスローされること")
    void shouldThrowFeatureDisabledExceptionWhenFlagOff() {
        when(featureFlagService.isEnabled("withdrawal")).thenReturn(false);

        assertThatThrownBy(() -> withdrawUseCase.execute(accountNumber, Money.of(500)))
                .isInstanceOf(com.example.bank.domain.model.FeatureDisabledException.class);

        verify(withdrawalPolicy, never()).withdraw(any(), any());
    }

    @Test
    @DisplayName("存在しない口座番号でAccountNotFoundExceptionがスローされること")
    void shouldThrowAccountNotFoundExceptionForNonExistentAccount() {
        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> withdrawUseCase.execute(accountNumber, Money.of(500)))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
