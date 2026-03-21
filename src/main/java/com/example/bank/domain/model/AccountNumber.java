package com.example.bank.domain.model;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public record AccountNumber(String value) {

    private static final Pattern PATTERN = Pattern.compile("\\d{10}");

    public AccountNumber {
        if (value == null || !PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "口座番号は10桁の数字である必要があります: " + value);
        }
    }

    public static AccountNumber generate() {
        long number = ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
        return new AccountNumber(String.valueOf(number));
    }
}
