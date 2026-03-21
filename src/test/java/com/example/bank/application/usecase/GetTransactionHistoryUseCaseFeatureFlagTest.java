package com.example.bank.application.usecase;

import com.example.bank.application.port.FeatureFlagService;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.FeatureDisabledException;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTransactionHistoryUseCaseFeatureFlagTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FeatureFlagService featureFlagService;

    private final AccountNumber accountNumber = new AccountNumber("1234567890");

    @Test
    @DisplayName("フラグONの場合、取引履歴が正常に取得できること")
    void shouldGetHistoryWhenFlagIsEnabled() {
        when(featureFlagService.isEnabled("transaction-history")).thenReturn(true);
        List<Transaction> transactions = List.of(
                Transaction.deposit(accountNumber, Money.of(1000), Money.of(1000)));
        when(transactionRepository.findByAccountNumber(accountNumber, 0, 20))
                .thenReturn(transactions);

        GetTransactionHistoryUseCase useCase = new GetTransactionHistoryUseCase(
                transactionRepository, featureFlagService);
        List<Transaction> result = useCase.execute(accountNumber, 0, 20);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("フラグOFFの場合、FeatureDisabledExceptionがスローされること")
    void shouldThrowExceptionWhenFlagIsDisabled() {
        when(featureFlagService.isEnabled("transaction-history")).thenReturn(false);

        GetTransactionHistoryUseCase useCase = new GetTransactionHistoryUseCase(
                transactionRepository, featureFlagService);

        assertThatThrownBy(() -> useCase.execute(accountNumber, 0, 20))
                .isInstanceOf(FeatureDisabledException.class);
    }
}
