package com.example.bank.domain.model;

public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException(long amount) {
        super("不正な金額です: " + amount);
    }
}
