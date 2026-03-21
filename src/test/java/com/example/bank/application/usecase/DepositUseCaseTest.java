package com.example.bank.application.usecase;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepositUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private DepositUseCase depositUseCase;

    private final AccountNumber accountNumber = new AccountNumber("1234567890");

    @BeforeEach
    void setUp() {
        Account account = Account.reconstruct(
                "id-1", accountNumber, "田中太郎", Money.of(1000), AccountStatus.ACTIVE, LocalDateTime.now());
        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));
        lenient().when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("正常に入金できること（残高が増加すること）")
    void shouldDepositSuccessfully() {
        Account result = depositUseCase.execute(accountNumber, Money.of(500));

        assertThat(result.getBalance()).isEqualTo(Money.of(1500));
    }

    @Test
    @DisplayName("入金後にAccountが更新保存されること")
    void shouldSaveUpdatedAccount() {
        depositUseCase.execute(accountNumber, Money.of(500));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertThat(captor.getValue().getBalance()).isEqualTo(Money.of(1500));
    }

    @Test
    @DisplayName("入金の取引履歴（Transaction）が保存されること")
    void shouldSaveDepositTransaction() {
        depositUseCase.execute(accountNumber, Money.of(500));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        Transaction saved = captor.getValue();
        assertThat(saved.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(saved.getAmount()).isEqualTo(Money.of(500));
        assertThat(saved.getBalanceAfter()).isEqualTo(Money.of(1500));
    }

    @Test
    @DisplayName("存在しない口座番号でAccountNotFoundExceptionがスローされること")
    void shouldThrowExceptionForNonExistentAccount() {
        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> depositUseCase.execute(accountNumber, Money.of(500)))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
