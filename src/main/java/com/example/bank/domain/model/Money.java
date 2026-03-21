package com.example.bank.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 金額を表す値オブジェクト。
 *
 * <p>負の金額は許可されず、小数点以下2桁で管理される。
 * イミュータブルであり、演算は新しいインスタンスを返す。</p>
 */
public final class Money {

    private static final int SCALE = 2;

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("金額は0以上である必要があります: " + amount);
        }
        this.amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 整数値から {@code Money} を生成する。
     *
     * @param amount 金額（0以上）
     * @return 生成された {@code Money}
     * @throws IllegalArgumentException 金額が負の場合
     */
    public static Money of(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    /**
     * {@link BigDecimal} から {@code Money} を生成する。
     *
     * @param amount 金額（0以上）
     * @return 生成された {@code Money}
     * @throws IllegalArgumentException 金額が負の場合
     */
    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    /**
     * 金額を加算する。
     *
     * @param other 加算する金額
     * @return 加算結果の新しい {@code Money}
     */
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    /**
     * 金額を減算する。
     *
     * @param other 減算する金額
     * @return 減算結果の新しい {@code Money}
     * @throws IllegalArgumentException 結果が負になる場合
     */
    public Money subtract(Money other) {
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "減算結果が負になります: " + this.amount + " - " + other.amount);
        }
        return new Money(result);
    }

    /**
     * 金額が正（0より大きい）かどうかを判定する。
     *
     * @return 正であれば {@code true}
     */
    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * この金額が指定された金額以上かどうかを判定する。
     *
     * @param other 比較対象の金額
     * @return この金額が {@code other} 以上であれば {@code true}
     */
    public boolean isGreaterThanOrEqual(Money other) {
        return this.amount.compareTo(other.amount) >= 0;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return amount.compareTo(money.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return "Money{" + amount + "}";
    }
}
