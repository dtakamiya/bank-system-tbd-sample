package com.example.bank.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * フィーチャーフラグの組み合わせマトリクスに基づくインテグレーションテスト。
 *
 * <p>withdrawal / withdrawal-fee / account-closure の各フラグON/OFFの組み合わせで、
 * APIエンドポイントが期待通りに動作することをSpringBootTestで検証する。</p>
 *
 * @see com.example.bank.infrastructure.feature.FeatureFlagServiceImpl
 */
@DisplayName("フラグ組み合わせマトリクス インテグレーションテスト")
class FeatureFlagCombinationTest {

    private static final String DEPOSIT_BODY = "{\"amount\": 10000}";
    private static final String WITHDRAW_BODY = "{\"amount\": 3000}";

    private String createAccount(MockMvc mockMvc) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ownerName\": \"テスト太郎\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        return body.replaceAll(".*\"accountNumber\":\"([^\"]+)\".*", "$1");
    }

    private void depositToAccount(MockMvc mockMvc, String accountNumber) throws Exception {
        mockMvc.perform(post("/api/v1/accounts/" + accountNumber + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DEPOSIT_BODY))
                .andExpect(status().isOk());
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @TestPropertySource(properties = {
            "bank.features.withdrawal=true",
            "bank.features.withdrawal-fee=false",
            "bank.features.account-closure=true"
    })
    @DisplayName("ケース1: withdrawal=ON, fee=OFF, closure=ON")
    class WithdrawalOnFeeOffClosureOn {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("出金は手数料なし、解約は手数料なし払い戻し")
        void shouldWithdrawWithoutFeeAndCloseAccount() throws Exception {
            // Arrange
            String accountNumber = createAccount(mockMvc);
            depositToAccount(mockMvc, accountNumber);

            // Act & Assert（出金: 手数料なし）
            mockMvc.perform(post("/api/v1/accounts/" + accountNumber + "/withdraw")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(WITHDRAW_BODY))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.balance").value(7000));

            // Act & Assert（解約: 手数料なし払い戻し）
            mockMvc.perform(delete("/api/v1/accounts/" + accountNumber))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CLOSED"));
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @TestPropertySource(properties = {
            "bank.features.withdrawal=true",
            "bank.features.withdrawal-fee=true",
            "bank.features.account-closure=true",
            "bank.withdrawal.fee-rate=0.01"
    })
    @DisplayName("ケース2: withdrawal=ON, fee=ON, closure=ON")
    class WithdrawalOnFeeOnClosureOn {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("出金は手数料付き（3000+30=3030引落）、解約は手数料なし払い戻し")
        void shouldWithdrawWithFeeAndCloseAccountWithoutFee() throws Exception {
            // Arrange
            String accountNumber = createAccount(mockMvc);
            depositToAccount(mockMvc, accountNumber);

            // Act & Assert（手数料付き出金: 3000 + 30(1%) = 3030引落、残高6970）
            mockMvc.perform(post("/api/v1/accounts/" + accountNumber + "/withdraw")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(WITHDRAW_BODY))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.balance").value(6970));

            // Act & Assert（解約: 残高6970が手数料なしで払い戻し）
            mockMvc.perform(delete("/api/v1/accounts/" + accountNumber))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CLOSED"))
                    .andExpect(jsonPath("$.balance").value(0));
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @TestPropertySource(properties = {
            "bank.features.withdrawal=false",
            "bank.features.account-closure=true"
    })
    @DisplayName("ケース3: withdrawal=OFF, closure=ON")
    class WithdrawalOffClosureOn {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("出金は501、解約は成功")
        void shouldReturnNotImplementedForWithdrawButCloseSucceeds() throws Exception {
            // Arrange
            String accountNumber = createAccount(mockMvc);
            depositToAccount(mockMvc, accountNumber);

            // Act & Assert（出金: フラグOFFのため501）
            mockMvc.perform(post("/api/v1/accounts/" + accountNumber + "/withdraw")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(WITHDRAW_BODY))
                    .andExpect(status().isNotImplemented());

            // Act & Assert（解約: フラグONのため成功）
            mockMvc.perform(delete("/api/v1/accounts/" + accountNumber))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CLOSED"));
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @TestPropertySource(properties = {
            "bank.features.withdrawal=true",
            "bank.features.withdrawal-fee=true",
            "bank.features.account-closure=false",
            "bank.withdrawal.fee-rate=0.01"
    })
    @DisplayName("ケース4: withdrawal=ON, fee=ON, closure=OFF")
    class WithdrawalOnFeeOnClosureOff {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("出金は手数料付き、解約は501")
        void shouldWithdrawWithFeeButClosureDisabled() throws Exception {
            // Arrange
            String accountNumber = createAccount(mockMvc);
            depositToAccount(mockMvc, accountNumber);

            // Act & Assert（出金: 手数料付き）
            mockMvc.perform(post("/api/v1/accounts/" + accountNumber + "/withdraw")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(WITHDRAW_BODY))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.balance").value(6970));

            // Act & Assert（解約: フラグOFFのため501）
            mockMvc.perform(delete("/api/v1/accounts/" + accountNumber))
                    .andExpect(status().isNotImplemented());
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @TestPropertySource(properties = {
            "bank.features.withdrawal=false",
            "bank.features.account-closure=false"
    })
    @DisplayName("ケース5: withdrawal=OFF, closure=OFF")
    class AllFlagsOff {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("出金・解約ともに501")
        void shouldReturnNotImplementedForBoth() throws Exception {
            // Arrange
            String accountNumber = createAccount(mockMvc);
            depositToAccount(mockMvc, accountNumber);

            // Act & Assert（出金: フラグOFFのため501）
            mockMvc.perform(post("/api/v1/accounts/" + accountNumber + "/withdraw")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(WITHDRAW_BODY))
                    .andExpect(status().isNotImplemented());

            // Act & Assert（解約: フラグOFFのため501）
            mockMvc.perform(delete("/api/v1/accounts/" + accountNumber))
                    .andExpect(status().isNotImplemented());
        }
    }
}
