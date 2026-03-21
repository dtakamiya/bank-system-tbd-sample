package com.example.bank.application.usecase;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNotFoundException;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.AccountStatus;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAccountUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private GetAccountUseCase getAccountUseCase;

    private final AccountNumber accountNumber = new AccountNumber("1234567890");

    @Test
    @DisplayName("存在する口座番号で口座情報を取得できること")
    void shouldGetAccountByAccountNumber() {
        Account account = Account.reconstruct(
                "id-1", accountNumber, "田中太郎", Money.of(1000), AccountStatus.ACTIVE, LocalDateTime.now());
        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        Account result = getAccountUseCase.execute(accountNumber);

        assertThat(result.getOwnerName()).isEqualTo("田中太郎");
        assertThat(result.getBalance()).isEqualTo(Money.of(1000));
    }

    @Test
    @DisplayName("存在しない口座番号でAccountNotFoundExceptionがスローされること")
    void shouldThrowExceptionForNonExistentAccount() {
        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> getAccountUseCase.execute(accountNumber))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
