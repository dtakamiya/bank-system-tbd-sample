package com.example.bank.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountNumberTest {

    @Nested
    @DisplayName("生成")
    class Creation {

        @Test
        @DisplayName("10桁の数字文字列で生成できること")
        void shouldCreateWithTenDigitString() {
            AccountNumber accountNumber = new AccountNumber("1234567890");

            assertThat(accountNumber.getValue()).isEqualTo("1234567890");
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
            AccountNumber accountNumber = AccountNumber.generate();

            assertThat(accountNumber.getValue()).hasSize(10);
            assertThat(accountNumber.getValue()).matches("\\d{10}");
        }
    }

    @Nested
    @DisplayName("値オブジェクトの等価性")
    class Equality {

        @Test
        @DisplayName("同じ値のAccountNumberがequalsで等しいこと")
        void shouldBeEqualForSameValue() {
            AccountNumber a = new AccountNumber("1234567890");
            AccountNumber b = new AccountNumber("1234567890");

            assertThat(a).isEqualTo(b);
        }

        @Test
        @DisplayName("同じ値のAccountNumberがhashCodeで等しいこと")
        void shouldHaveSameHashCodeForSameValue() {
            AccountNumber a = new AccountNumber("1234567890");
            AccountNumber b = new AccountNumber("1234567890");

            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("異なる値のAccountNumberがequalsで等しくないこと")
        void shouldNotBeEqualForDifferentValues() {
            AccountNumber a = new AccountNumber("1234567890");
            AccountNumber b = new AccountNumber("0987654321");

            assertThat(a).isNotEqualTo(b);
        }
    }
}
