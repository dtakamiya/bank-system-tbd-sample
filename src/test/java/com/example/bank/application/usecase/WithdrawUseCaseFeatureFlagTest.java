package com.example.bank.application.usecase;

import com.example.bank.application.port.FeatureFlagService;
import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.AccountStatus;
import com.example.bank.domain.model.FeatureDisabledException;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.repository.AccountRepository;
import com.example.bank.domain.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithdrawUseCaseFeatureFlagTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FeatureFlagService featureFlagService;

    private final AccountNumber accountNumber = new AccountNumber("1234567890");

    @Test
    @DisplayName("フラグONの場合、出金が正常に実行されること")
    void shouldWithdrawWhenFlagIsEnabled() {
        when(featureFlagService.isEnabled("withdrawal")).thenReturn(true);
        Account account = Account.reconstruct(
                "id-1", accountNumber, "田中太郎", Money.of(1000), AccountStatus.ACTIVE, LocalDateTime.now());
        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WithdrawUseCase useCase = new WithdrawUseCase(
                accountRepository, transactionRepository, featureFlagService);
        Account result = useCase.execute(accountNumber, Money.of(500));

        assertThat(result.getBalance()).isEqualTo(Money.of(500));
    }

    @Test
    @DisplayName("フラグOFFの場合、FeatureDisabledExceptionがスローされること")
    void shouldThrowExceptionWhenFlagIsDisabled() {
        when(featureFlagService.isEnabled("withdrawal")).thenReturn(false);

        WithdrawUseCase useCase = new WithdrawUseCase(
                accountRepository, transactionRepository, featureFlagService);

        assertThatThrownBy(() -> useCase.execute(accountNumber, Money.of(500)))
                .isInstanceOf(FeatureDisabledException.class);
    }
}
