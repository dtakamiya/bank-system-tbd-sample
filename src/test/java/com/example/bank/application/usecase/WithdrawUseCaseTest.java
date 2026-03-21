package com.example.bank.application.usecase;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNotFoundException;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.InsufficientBalanceException;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithdrawUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private WithdrawUseCase withdrawUseCase;

    private final AccountNumber accountNumber = new AccountNumber("1234567890");

    @BeforeEach
    void setUp() {
        Account account = Account.reconstruct(
                "id-1", accountNumber, "田中太郎", Money.of(1000), LocalDateTime.now());
        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));
        lenient().when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("正常に出金できること（残高が減少すること）")
    void shouldWithdrawSuccessfully() {
        Account result = withdrawUseCase.execute(accountNumber, Money.of(500));

        assertThat(result.getBalance()).isEqualTo(Money.of(500));
    }

    @Test
    @DisplayName("出金後にAccountが更新保存されること")
    void shouldSaveUpdatedAccount() {
        withdrawUseCase.execute(accountNumber, Money.of(500));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertThat(captor.getValue().getBalance()).isEqualTo(Money.of(500));
    }

    @Test
    @DisplayName("出金の取引履歴（Transaction）が保存されること")
    void shouldSaveWithdrawalTransaction() {
        withdrawUseCase.execute(accountNumber, Money.of(500));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        Transaction saved = captor.getValue();
        assertThat(saved.getType()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(saved.getAmount()).isEqualTo(Money.of(500));
        assertThat(saved.getBalanceAfter()).isEqualTo(Money.of(500));
    }

    @Test
    @DisplayName("残高不足時にInsufficientBalanceExceptionがスローされること")
    void shouldThrowExceptionWhenInsufficientBalance() {
        assertThatThrownBy(() -> withdrawUseCase.execute(accountNumber, Money.of(5000)))
                .isInstanceOf(InsufficientBalanceException.class);

        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("存在しない口座番号でAccountNotFoundExceptionがスローされること")
    void shouldThrowExceptionForNonExistentAccount() {
        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> withdrawUseCase.execute(accountNumber, Money.of(500)))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
