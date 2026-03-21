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

/**
 * {@link GetAccountUseCase} のユニットテスト。
 *
 * <p>口座情報取得ユースケースの正常系・異常系を検証する。
 * 口座番号による口座取得と、存在しない口座番号の例外スローを確認する。</p>
 *
 * @see GetAccountUseCase
 */
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
        // Arrange
        Account account = Account.reconstruct(
                "id-1", accountNumber, "田中太郎", Money.of(1000), AccountStatus.ACTIVE, LocalDateTime.now());
        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        // Act
        Account result = getAccountUseCase.execute(accountNumber);

        // Assert
        assertThat(result.getOwnerName()).isEqualTo("田中太郎");
        assertThat(result.getBalance()).isEqualTo(Money.of(1000));
    }

    @Test
    @DisplayName("存在しない口座番号でAccountNotFoundExceptionがスローされること")
    void shouldThrowExceptionForNonExistentAccount() {
        // Arrange
        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> getAccountUseCase.execute(accountNumber))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
