package com.example.bank.application.usecase;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAccountUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CreateAccountUseCase createAccountUseCase;

    @Test
    @DisplayName("口座名義人を指定して口座を開設できること")
    void shouldCreateAccountWithOwnerName() {
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account account = createAccountUseCase.execute("田中太郎");

        assertThat(account.getOwnerName()).isEqualTo("田中太郎");
    }

    @Test
    @DisplayName("作成された口座の初期残高が0であること")
    void shouldCreateAccountWithZeroBalance() {
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account account = createAccountUseCase.execute("田中太郎");

        assertThat(account.getBalance()).isEqualTo(Money.ZERO);
    }

    @Test
    @DisplayName("口座番号が10桁で生成されること")
    void shouldGenerateTenDigitAccountNumber() {
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account account = createAccountUseCase.execute("田中太郎");

        assertThat(account.getAccountNumber().value()).hasSize(10);
    }

    @Test
    @DisplayName("口座がリポジトリに保存されること")
    void shouldSaveAccountToRepository() {
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        createAccountUseCase.execute("田中太郎");

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertThat(captor.getValue().getOwnerName()).isEqualTo("田中太郎");
    }
}
