package com.example.bank.presentation.response;

public record ErrorResponse(
        String code,
        String message
) {
}
