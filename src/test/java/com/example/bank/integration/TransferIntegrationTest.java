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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("口座間送金 統合テスト")
class TransferIntegrationTest {

    private static final String CREATE_ACCOUNT_URL = "/api/v1/accounts";
    private static final String DEPOSIT_BODY = """
            {"amount": 50000}
            """;

    private String createAndFundAccount(MockMvc mockMvc, String ownerName) throws Exception {
        MvcResult createResult = mockMvc.perform(post(CREATE_ACCOUNT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ownerName\": \"" + ownerName + "\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        String accountNumber = com.jayway.jsonpath.JsonPath.read(
                createResult.getResponse().getContentAsString(), "$.accountNumber");
        mockMvc.perform(post(CREATE_ACCOUNT_URL + "/" + accountNumber + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DEPOSIT_BODY))
                .andExpect(status().isOk());
        return accountNumber;
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @TestPropertySource(properties = {
            "bank.features.account-transfer=true",
            "bank.features.transfer-fee=false"
    })
    @DisplayName("account-transfer=true, transfer-fee=false")
    class TransferWithoutFee {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("手数料なし送金が正常に完了すること")
        void shouldTransferWithoutFee() throws Exception {
            // Arrange
            String sourceAccount = createAndFundAccount(mockMvc, "送金元太郎");
            String targetAccount = createAndFundAccount(mockMvc, "送金先花子");

            // Act & Assert
            mockMvc.perform(post(CREATE_ACCOUNT_URL + "/" + sourceAccount + "/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"targetAccountNumber\": \"" + targetAccount + "\", \"amount\": 10000}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.amount").value(10000))
                    .andExpect(jsonPath("$.fee").value(0))
                    .andExpect(jsonPath("$.sourceBalanceAfter").value(40000))
                    .andExpect(jsonPath("$.targetBalanceAfter").value(60000));
        }

        @Test
        @DisplayName("送金後に取引履歴にTRANSFER_OUT/TRANSFER_INが記録されること")
        void shouldRecordTransferTransactions() throws Exception {
            // Arrange
            String sourceAccount = createAndFundAccount(mockMvc, "履歴送金元");
            String targetAccount = createAndFundAccount(mockMvc, "履歴送金先");

            // Act
            mockMvc.perform(post(CREATE_ACCOUNT_URL + "/" + sourceAccount + "/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"targetAccountNumber\": \"" + targetAccount + "\", \"amount\": 5000}"))
                    .andExpect(status().isOk());

            // Assert — 送金元の取引履歴にTRANSFER_OUTがある
            mockMvc.perform(get("/api/v1/accounts/" + sourceAccount + "/transactions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.transactions[?(@.type == 'TRANSFER_OUT')]").exists());

            // Assert — 送金先の取引履歴にTRANSFER_INがある
            mockMvc.perform(get("/api/v1/accounts/" + targetAccount + "/transactions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.transactions[?(@.type == 'TRANSFER_IN')]").exists());
        }

        @Test
        @DisplayName("同一口座への送金で422が返されること")
        void shouldReturn422ForSameAccount() throws Exception {
            String account = createAndFundAccount(mockMvc, "同一口座太郎");

            mockMvc.perform(post(CREATE_ACCOUNT_URL + "/" + account + "/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"targetAccountNumber\": \"" + account + "\", \"amount\": 10000}"))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.code").value("SAME_ACCOUNT_TRANSFER"));
        }

        @Test
        @DisplayName("残高不足で422が返されること")
        void shouldReturn422ForInsufficientBalance() throws Exception {
            String sourceAccount = createAndFundAccount(mockMvc, "残高不足太郎");
            String targetAccount = createAndFundAccount(mockMvc, "残高不足花子");

            mockMvc.perform(post(CREATE_ACCOUNT_URL + "/" + sourceAccount + "/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"targetAccountNumber\": \"" + targetAccount + "\", \"amount\": 999999}"))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.code").value("INSUFFICIENT_BALANCE"));
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @TestPropertySource(properties = {
            "bank.features.account-transfer=true",
            "bank.features.transfer-fee=true",
            "bank.transfer.fee-rate=0.01"
    })
    @DisplayName("account-transfer=true, transfer-fee=true")
    class TransferWithFee {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("手数料あり送金が正常に完了すること（手数料率1%）")
        void shouldTransferWithFee() throws Exception {
            String sourceAccount = createAndFundAccount(mockMvc, "手数料送金元");
            String targetAccount = createAndFundAccount(mockMvc, "手数料送金先");

            mockMvc.perform(post(CREATE_ACCOUNT_URL + "/" + sourceAccount + "/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"targetAccountNumber\": \"" + targetAccount + "\", \"amount\": 10000}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.amount").value(10000))
                    .andExpect(jsonPath("$.fee").value(100))
                    .andExpect(jsonPath("$.sourceBalanceAfter").value(39900))
                    .andExpect(jsonPath("$.targetBalanceAfter").value(60000));
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @TestPropertySource(properties = {
            "bank.features.account-transfer=false"
    })
    @DisplayName("account-transfer=false")
    class TransferDisabled {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("フラグOFFで501が返されること")
        void shouldReturn501WhenDisabled() throws Exception {
            mockMvc.perform(post(CREATE_ACCOUNT_URL + "/0000000001/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"targetAccountNumber\": \"0000000002\", \"amount\": 10000}"))
                    .andExpect(status().isNotImplemented())
                    .andExpect(jsonPath("$.code").value("FEATURE_DISABLED"));
        }
    }
}
