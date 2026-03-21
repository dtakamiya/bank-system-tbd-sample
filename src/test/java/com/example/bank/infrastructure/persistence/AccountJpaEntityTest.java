package com.example.bank.infrastructure.persistence;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountJpaEntityTest {

    @Test
    @DisplayName("AccountからAccountJpaEntityに変換できること")
    void shouldConvertFromDomain() {
        Account account = Account.create("田中太郎").deposit(Money.of(1000));

        AccountJpaEntity entity = AccountJpaEntity.fromDomain(account);

        assertThat(entity.getId()).isEqualTo(account.getId());
        assertThat(entity.getAccountNumber()).isEqualTo(account.getAccountNumber().value());
        assertThat(entity.getOwnerName()).isEqualTo("田中太郎");
        assertThat(entity.getBalance()).isEqualByComparingTo(account.getBalance().getAmount());
        assertThat(entity.getCreatedAt()).isEqualTo(account.getCreatedAt());
    }

    @Test
    @DisplayName("AccountJpaEntityからAccountに変換できること")
    void shouldConvertToDomain() {
        Account original = Account.create("田中太郎").deposit(Money.of(1000));
        AccountJpaEntity entity = AccountJpaEntity.fromDomain(original);

        Account restored = entity.toDomain();

        assertThat(restored.getId()).isEqualTo(original.getId());
        assertThat(restored.getAccountNumber()).isEqualTo(original.getAccountNumber());
        assertThat(restored.getOwnerName()).isEqualTo(original.getOwnerName());
        assertThat(restored.getBalance()).isEqualTo(original.getBalance());
        assertThat(restored.getCreatedAt()).isEqualTo(original.getCreatedAt());
    }

    @Test
    @DisplayName("変換の往復でデータが保持されること")
    void shouldPreserveDataOnRoundTrip() {
        Account original = Account.create("山田花子").deposit(Money.of(5000));

        Account roundTripped = AccountJpaEntity.fromDomain(original).toDomain();

        assertThat(roundTripped.getId()).isEqualTo(original.getId());
        assertThat(roundTripped.getOwnerName()).isEqualTo(original.getOwnerName());
        assertThat(roundTripped.getBalance()).isEqualTo(original.getBalance());
    }
}
