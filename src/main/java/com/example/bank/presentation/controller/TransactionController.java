package com.example.bank.presentation.controller;

import com.example.bank.application.usecase.GetTransactionHistoryUseCase;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Transaction;
import com.example.bank.presentation.response.TransactionResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts/{accountNumber}/transactions")
public class TransactionController {

    private final GetTransactionHistoryUseCase getTransactionHistoryUseCase;

    public TransactionController(GetTransactionHistoryUseCase getTransactionHistoryUseCase) {
        this.getTransactionHistoryUseCase = getTransactionHistoryUseCase;
    }

    @GetMapping
    public Map<String, Object> getTransactions(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Transaction> transactions = getTransactionHistoryUseCase.execute(
                new AccountNumber(accountNumber), page, size);

        List<TransactionResponse> responses = transactions.stream()
                .map(TransactionResponse::from)
                .toList();

        return Map.of("transactions", responses);
    }
}
