package com.example.bank.presentation.response;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.AccountStatus;
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
                "id-1", accountNumber, "田中太郎", Money.of(1000), AccountStatus.ACTIVE, LocalDateTime.now());

        AccountResponse response = AccountResponse.from(account);

        assertThat(response.accountNumber()).isEqualTo("1234567890");
        assertThat(response.ownerName()).isEqualTo("田中太郎");
        assertThat(response.balance()).isEqualByComparingTo(Money.of(1000).getAmount());
        assertThat(response.status()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("CLOSED口座のレスポンスにCLOSEDが含まれること")
    void shouldConvertClosedAccount() {
        AccountNumber accountNumber = new AccountNumber("1234567890");
        Account account = Account.reconstruct(
                "id-1", accountNumber, "田中太郎", Money.of(0), AccountStatus.CLOSED, LocalDateTime.now());

        AccountResponse response = AccountResponse.from(account);

        assertThat(response.status()).isEqualTo("CLOSED");
        assertThat(response.balance()).isEqualByComparingTo(Money.of(0).getAmount());
    }
}
