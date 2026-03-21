package com.example.bank.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link Money} 値オブジェクトのユニットテスト。
 *
 * <p>金額の生成・加算・減算・比較・等価性を検証する。</p>
 *
 * @see Money
 */
class MoneyTest {

    @Nested
    @DisplayName("生成")
    class Creation {

        @Test
        @DisplayName("正の金額でMoneyを生成できること")
        void shouldCreateWithPositiveAmount() {
            Money money = Money.of(1000);

            assertThat(money).isNotNull();
        }

        @Test
        @DisplayName("0の金額でMoneyを生成できること")
        void shouldCreateWithZeroAmount() {
            Money money = Money.of(0);

            assertThat(money).isNotNull();
        }

        @Test
        @DisplayName("負の金額ではIllegalArgumentExceptionがスローされること")
        void shouldThrowExceptionForNegativeAmount() {
            assertThatThrownBy(() -> Money.of(-1))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("BigDecimalでMoneyを生成できること")
        void shouldCreateFromBigDecimal() {
            Money money = Money.of(new BigDecimal("1000.50"));

            assertThat(money.getAmount()).isEqualByComparingTo(new BigDecimal("1000.50"));
        }

        @Test
        @DisplayName("Money.ZEROが0であること")
        void shouldHaveZeroConstant() {
            assertThat(Money.ZERO).isEqualTo(Money.of(0));
        }
    }

    @Nested
    @DisplayName("演算")
    class Operations {

        @Test
        @DisplayName("add()で2つのMoneyを加算できること")
        void shouldAddTwoMoneyValues() {
            // Arrange
            Money a = Money.of(1000);
            Money b = Money.of(500);

            // Act
            Money result = a.add(b);

            // Assert
            assertThat(result).isEqualTo(Money.of(1500));
        }

        @Test
        @DisplayName("subtract()で減算できること")
        void shouldSubtractMoney() {
            // Arrange
            Money a = Money.of(1000);
            Money b = Money.of(300);

            // Act
            Money result = a.subtract(b);

            // Assert
            assertThat(result).isEqualTo(Money.of(700));
        }

        @Test
        @DisplayName("subtract()で結果が負になる場合はエラーになること")
        void shouldThrowExceptionWhenSubtractionResultsInNegative() {
            // Arrange
            Money a = Money.of(100);
            Money b = Money.of(200);

            // Act & Assert
            assertThatThrownBy(() -> a.subtract(b))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("比較")
    class Comparison {

        @Test
        @DisplayName("isGreaterThanOrEqual()が同値で正しく判定すること")
        void shouldReturnTrueForEqualAmount() {
            // Arrange
            Money a = Money.of(100);
            Money b = Money.of(100);

            // Act & Assert
            assertThat(a.isGreaterThanOrEqual(b)).isTrue();
        }

        @Test
        @DisplayName("isGreaterThanOrEqual()がより大きい値で正しく判定すること")
        void shouldReturnTrueForGreaterAmount() {
            // Arrange
            Money a = Money.of(200);
            Money b = Money.of(100);

            // Act & Assert
            assertThat(a.isGreaterThanOrEqual(b)).isTrue();
        }

        @Test
        @DisplayName("isGreaterThanOrEqual()がより小さい値で正しく判定すること")
        void shouldReturnFalseForSmallerAmount() {
            // Arrange
            Money a = Money.of(50);
            Money b = Money.of(100);

            // Act & Assert
            assertThat(a.isGreaterThanOrEqual(b)).isFalse();
        }

        @Test
        @DisplayName("isPositive()が正の金額でtrueを返すこと")
        void shouldReturnTrueForPositiveAmount() {
            assertThat(Money.of(1).isPositive()).isTrue();
        }

        @Test
        @DisplayName("isPositive()が0でfalseを返すこと")
        void shouldReturnFalseForZeroAmount() {
            assertThat(Money.ZERO.isPositive()).isFalse();
        }
    }

    @Nested
    @DisplayName("値オブジェクトの等価性")
    class Equality {

        @Test
        @DisplayName("同値のMoneyがequalsで等しいこと")
        void shouldBeEqualForSameValue() {
            // Arrange
            Money a = Money.of(1000);
            Money b = Money.of(1000);

            // Act & Assert
            assertThat(a).isEqualTo(b);
        }

        @Test
        @DisplayName("同値のMoneyがhashCodeで等しいこと")
        void shouldHaveSameHashCodeForSameValue() {
            // Arrange
            Money a = Money.of(1000);
            Money b = Money.of(1000);

            // Act & Assert
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("異なる値のMoneyがequalsで等しくないこと")
        void shouldNotBeEqualForDifferentValues() {
            // Arrange
            Money a = Money.of(1000);
            Money b = Money.of(2000);

            // Act & Assert
            assertThat(a).isNotEqualTo(b);
        }
    }
}
