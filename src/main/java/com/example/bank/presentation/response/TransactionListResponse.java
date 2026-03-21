package com.example.bank.presentation.response;

import java.util.List;

public record TransactionListResponse(
        List<TransactionResponse> transactions
) {
}
