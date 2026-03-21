package com.example.bank.presentation.response;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AccountResponseTest {

    @Test
    @DisplayName("AccountからAccountResponseに変換できること")
    void shouldConvertFromAccount() {
        AccountNumber accountNumber = new AccountNumber("1234567890");
        Account account = Account.reconstruct(
                "id-1", accountNumber, "田中太郎", Money.of(1000), LocalDateTime.now());

        AccountResponse response = AccountResponse.from(account);

        assertThat(response.accountNumber()).isEqualTo("1234567890");
        assertThat(response.ownerName()).isEqualTo("田中太郎");
        assertThat(response.balance()).isEqualByComparingTo(Money.of(1000).getAmount());
    }
}
