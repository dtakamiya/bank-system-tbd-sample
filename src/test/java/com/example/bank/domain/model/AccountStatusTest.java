package com.example.bank.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link AccountStatus} 列挙型のユニットテスト。
 *
 * <p>口座ステータスの定義値と文字列変換を検証する。</p>
 *
 * @see AccountStatus
 */
@DisplayName("AccountStatus 列挙型")
class AccountStatusTest {

    @Test
    @DisplayName("ACTIVEとCLOSEDの2つの値が存在すること")
    void ACTIVEとCLOSEDの2つの値が存在すること() {
        // Act
        AccountStatus[] values = AccountStatus.values();

        // Assert
        assertThat(values).hasSize(2);
        assertThat(values).containsExactlyInAnyOrder(AccountStatus.ACTIVE, AccountStatus.CLOSED);
    }

    @Test
    @DisplayName("valueOf()で文字列からenumに変換できること")
    void valueOf_で文字列からenumに変換できること() {
        // Act & Assert
        assertThat(AccountStatus.valueOf("ACTIVE")).isEqualTo(AccountStatus.ACTIVE);
        assertThat(AccountStatus.valueOf("CLOSED")).isEqualTo(AccountStatus.CLOSED);
    }
}
