package com.example.bank.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountStatusTest {

    @Test
    void ACTIVEとCLOSEDの2つの値が存在すること() {
        AccountStatus[] values = AccountStatus.values();

        assertThat(values).hasSize(2);
        assertThat(values).containsExactlyInAnyOrder(AccountStatus.ACTIVE, AccountStatus.CLOSED);
    }

    @Test
    void valueOf_で文字列からenumに変換できること() {
        assertThat(AccountStatus.valueOf("ACTIVE")).isEqualTo(AccountStatus.ACTIVE);
        assertThat(AccountStatus.valueOf("CLOSED")).isEqualTo(AccountStatus.CLOSED);
    }
}
