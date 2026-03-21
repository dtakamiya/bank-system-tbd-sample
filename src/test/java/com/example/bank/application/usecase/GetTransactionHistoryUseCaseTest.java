package com.example.bank.application.usecase;

import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTransactionHistoryUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private GetTransactionHistoryUseCase getTransactionHistoryUseCase;

    private final AccountNumber accountNumber = new AccountNumber("1234567890");

    @Test
    @DisplayName("口座の取引履歴をページネーション付きで取得できること")
    void shouldGetTransactionHistory() {
        List<Transaction> transactions = List.of(
                Transaction.deposit(accountNumber, Money.of(1000), Money.of(1000)),
                Transaction.withdrawal(accountNumber, Money.of(300), Money.of(700))
        );
        when(transactionRepository.findByAccountNumber(accountNumber, 0, 20))
                .thenReturn(transactions);

        List<Transaction> result = getTransactionHistoryUseCase.execute(accountNumber, 0, 20);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("ページサイズとページ番号が正しく渡されること")
    void shouldPassCorrectPaginationParameters() {
        when(transactionRepository.findByAccountNumber(accountNumber, 1, 10))
                .thenReturn(List.of());

        getTransactionHistoryUseCase.execute(accountNumber, 1, 10);

        verify(transactionRepository).findByAccountNumber(accountNumber, 1, 10);
    }
}
