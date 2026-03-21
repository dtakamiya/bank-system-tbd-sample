package com.example.bank.presentation.controller;

import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.Transaction;
import com.example.bank.application.usecase.GetTransactionHistoryUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link TransactionController} のWebレイヤーテスト。
 *
 * <p>取引履歴照会APIエンドポイントのリクエスト・レスポンスおよび
 * ページネーションのデフォルト値適用を検証する。</p>
 *
 * @see TransactionController
 */
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetTransactionHistoryUseCase getTransactionHistoryUseCase;

    private final AccountNumber accountNumber = new AccountNumber("1234567890");

    @Test
    @DisplayName("GET /api/v1/accounts/{accountNumber}/transactions — 取引履歴を200で返すこと")
    void shouldGetTransactionHistory() throws Exception {
        // Arrange
        List<Transaction> transactions = List.of(
                Transaction.deposit(accountNumber, Money.of(1000), Money.of(1000)),
                Transaction.withdrawal(accountNumber, Money.of(300), Money.of(700))
        );
        when(getTransactionHistoryUseCase.execute(accountNumber, 0, 20))
                .thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/1234567890/transactions")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions.length()").value(2))
                .andExpect(jsonPath("$.transactions[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$.transactions[1].type").value("WITHDRAWAL"));
    }

    @Test
    @DisplayName("ページネーションパラメータのデフォルト値が適用されること")
    void shouldUseDefaultPagination() throws Exception {
        // Arrange
        when(getTransactionHistoryUseCase.execute(accountNumber, 0, 20))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts/1234567890/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions.length()").value(0));
    }
}
