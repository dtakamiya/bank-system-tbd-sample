package com.example.bank.infrastructure.feature;

import com.example.bank.application.port.FeatureFlagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "bank.features.account-creation=true",
        "bank.features.withdrawal=false"
})
class FeatureFlagServiceImplTest {

    @Autowired
    private FeatureFlagService featureFlagService;

    @Test
    @DisplayName("フラグがONの場合isEnabled()がtrueを返すこと")
    void shouldReturnTrueWhenFlagIsEnabled() {
        assertThat(featureFlagService.isEnabled("account-creation")).isTrue();
    }

    @Test
    @DisplayName("フラグがOFFの場合isEnabled()がfalseを返すこと")
    void shouldReturnFalseWhenFlagIsDisabled() {
        assertThat(featureFlagService.isEnabled("withdrawal")).isFalse();
    }

    @Test
    @DisplayName("存在しないフラグはfalseを返すこと")
    void shouldReturnFalseForUnknownFlag() {
        assertThat(featureFlagService.isEnabled("unknown-feature")).isFalse();
    }
}
