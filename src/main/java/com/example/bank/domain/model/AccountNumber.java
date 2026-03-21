package com.example.bank.domain.model;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * 口座番号を表す値オブジェクト。
 *
 * <p>10桁の数字で構成され、不正な形式の場合は生成時に例外をスローする。</p>
 *
 * @param value 10桁の口座番号文字列
 */
public record AccountNumber(String value) {

    private static final Pattern PATTERN = Pattern.compile("\\d{10}");

    public AccountNumber {
        if (value == null || !PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "口座番号は10桁の数字である必要があります: " + value);
        }
    }

    /**
     * ランダムな10桁の口座番号を生成する。
     *
     * @return 新しい口座番号
     */
    public static AccountNumber generate() {
        long number = ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
        return new AccountNumber(String.valueOf(number));
    }
}
