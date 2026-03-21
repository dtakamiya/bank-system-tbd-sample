package com.example.bank.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link AccountNumber} 値オブジェクトのユニットテスト。
 *
 * <p>口座番号の生成・バリデーション・等価性を検証する。</p>
 *
 * @see AccountNumber
 */
class AccountNumberTest {

    @Nested
    @DisplayName("生成")
    class Creation {

        @Test
        @DisplayName("10桁の数字文字列で生成できること")
        void shouldCreateWithTenDigitString() {
            AccountNumber accountNumber = new AccountNumber("1234567890");

            assertThat(accountNumber.value()).isEqualTo("1234567890");
        }

        @Test
        @DisplayName("10桁未満の文字列ではエラーになること")
        void shouldThrowExceptionForLessThanTenDigits() {
            assertThatThrownBy(() -> new AccountNumber("12345"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("10桁超の文字列ではエラーになること")
        void shouldThrowExceptionForMoreThanTenDigits() {
            assertThatThrownBy(() -> new AccountNumber("12345678901"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("数字以外の文字を含む場合はエラーになること")
        void shouldThrowExceptionForNonDigitCharacters() {
            assertThatThrownBy(() -> new AccountNumber("123456789a"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("nullではエラーになること")
        void shouldThrowExceptionForNull() {
            assertThatThrownBy(() -> new AccountNumber(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("generate()")
    class Generate {

        @Test
        @DisplayName("ランダムな10桁の口座番号が生成されること")
        void shouldGenerateTenDigitAccountNumber() {
            // Act
            AccountNumber accountNumber = AccountNumber.generate();

            // Assert
            assertThat(accountNumber.value()).hasSize(10);
            assertThat(accountNumber.value()).matches("\\d{10}");
        }
    }

    @Nested
    @DisplayName("値オブジェクトの等価性")
    class Equality {

        @Test
        @DisplayName("同じ値のAccountNumberがequalsで等しいこと")
        void shouldBeEqualForSameValue() {
            // Arrange
            AccountNumber a = new AccountNumber("1234567890");
            AccountNumber b = new AccountNumber("1234567890");

            // Act & Assert
            assertThat(a).isEqualTo(b);
        }

        @Test
        @DisplayName("同じ値のAccountNumberがhashCodeで等しいこと")
        void shouldHaveSameHashCodeForSameValue() {
            // Arrange
            AccountNumber a = new AccountNumber("1234567890");
            AccountNumber b = new AccountNumber("1234567890");

            // Act & Assert
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("異なる値のAccountNumberがequalsで等しくないこと")
        void shouldNotBeEqualForDifferentValues() {
            // Arrange
            AccountNumber a = new AccountNumber("1234567890");
            AccountNumber b = new AccountNumber("0987654321");

            // Act & Assert
            assertThat(a).isNotEqualTo(b);
        }
    }
}
