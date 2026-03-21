package com.example.bank.domain.model;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class AccountNumber {

    private static final String PATTERN = "\\d{10}";

    private final String value;

    public AccountNumber(String value) {
        if (value == null || !value.matches(PATTERN)) {
            throw new IllegalArgumentException(
                    "口座番号は10桁の数字である必要があります: " + value);
        }
        this.value = value;
    }

    public static AccountNumber generate() {
        long number = ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
        return new AccountNumber(String.valueOf(number));
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountNumber that = (AccountNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "AccountNumber{" + value + "}";
    }
}
