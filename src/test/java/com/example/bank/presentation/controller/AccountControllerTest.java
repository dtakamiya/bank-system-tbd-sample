package com.example.bank.presentation.controller;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountAlreadyClosedException;
import com.example.bank.domain.model.AccountNotFoundException;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.AccountStatus;
import com.example.bank.domain.model.FeatureDisabledException;
import com.example.bank.domain.model.InsufficientBalanceException;
import com.example.bank.domain.model.Money;
import com.example.bank.application.usecase.CloseAccountUseCase;
import com.example.bank.application.usecase.CreateAccountUseCase;
import com.example.bank.application.usecase.DepositUseCase;
import com.example.bank.application.usecase.GetAccountUseCase;
import com.example.bank.application.usecase.WithdrawUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateAccountUseCase createAccountUseCase;

    @MockitoBean
    private GetAccountUseCase getAccountUseCase;

    @MockitoBean
    private DepositUseCase depositUseCase;

    @MockitoBean
    private WithdrawUseCase withdrawUseCase;

    @MockitoBean
    private CloseAccountUseCase closeAccountUseCase;

    private final AccountNumber accountNumber = new AccountNumber("1234567890");

    @Test
    @DisplayName("POST /api/v1/accounts — 口座開設が201 Createdを返すこと")
    void shouldCreateAccount() throws Exception {
        Account account = Account.reconstruct(
                "id-1", accountNumber, "田中太郎", Money.ZERO, AccountStatus.ACTIVE, LocalDateTime.now());
        when(createAccountUseCase.execute("田中太郎")).thenReturn(account);

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"ownerName": "田中太郎"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.ownerName").value("田中太郎"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    @DisplayName("POST /api/v1/accounts — ownerNameが空の場合400を返すこと")
    void shouldReturn400WhenOwnerNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"ownerName": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    @DisplayName("GET /api/v1/accounts/{accountNumber} — 口座情報を200で返すこと")
    void shouldGetAccount() throws Exception {
        Account account = Account.reconstruct(
                "id-1", accountNumber, "田中太郎", Money.of(1000), AccountStatus.ACTIVE, LocalDateTime.now());
        when(getAccountUseCase.execute(accountNumber)).thenReturn(account);

        mockMvc.perform(get("/api/v1/accounts/1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    @DisplayName("GET /api/v1/accounts/{accountNumber} — 存在しない口座で404を返すこと")
    void shouldReturn404ForNonExistentAccount() throws Exception {
        when(getAccountUseCase.execute(any(AccountNumber.class)))
                .thenThrow(new AccountNotFoundException(new AccountNumber("9999999999")));

        mockMvc.perform(get("/api/v1/accounts/9999999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"));
    }

    @Test
    @DisplayName("POST /api/v1/accounts/{accountNumber}/deposit — 入金が200を返すこと")
    void shouldDeposit() throws Exception {
        Account account = Account.reconstruct(
                "id-1", accountNumber, "田中太郎", Money.of(1500), AccountStatus.ACTIVE, LocalDateTime.now());
        when(depositUseCase.execute(eq(accountNumber), any(Money.class))).thenReturn(account);

        mockMvc.perform(post("/api/v1/accounts/1234567890/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 500}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500));
    }

    @Test
    @DisplayName("POST /api/v1/accounts/{accountNumber}/deposit — 金額が0以下で400を返すこと")
    void shouldReturn400WhenDepositAmountIsNotPositive() throws Exception {
        mockMvc.perform(post("/api/v1/accounts/1234567890/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": -100}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    @DisplayName("POST /api/v1/accounts/{accountNumber}/withdraw — 出金が200を返すこと")
    void shouldWithdraw() throws Exception {
        Account account = Account.reconstruct(
                "id-1", accountNumber, "田中太郎", Money.of(500), AccountStatus.ACTIVE, LocalDateTime.now());
        when(withdrawUseCase.execute(eq(accountNumber), any(Money.class))).thenReturn(account);

        mockMvc.perform(post("/api/v1/accounts/1234567890/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 500}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(500));
    }

    @Test
    @DisplayName("POST /api/v1/accounts/{accountNumber}/withdraw — 残高不足で422を返すこと")
    void shouldReturn422WhenInsufficientBalance() throws Exception {
        when(withdrawUseCase.execute(any(AccountNumber.class), any(Money.class)))
                .thenThrow(new InsufficientBalanceException(Money.of(100), Money.of(500)));

        mockMvc.perform(post("/api/v1/accounts/1234567890/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 500}
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_BALANCE"));
    }

    @Test
    @DisplayName("DELETE /api/v1/accounts/{accountNumber} — 口座解約が200を返すこと")
    void shouldCloseAccount() throws Exception {
        Account closedAccount = Account.reconstruct(
                "id-1", accountNumber, "田中太郎", Money.of(0), AccountStatus.CLOSED, LocalDateTime.now());
        when(closeAccountUseCase.execute(accountNumber)).thenReturn(closedAccount);

        mockMvc.perform(delete("/api/v1/accounts/1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.status").value("CLOSED"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    @DisplayName("DELETE /api/v1/accounts/{accountNumber} — 解約済み口座で422を返すこと")
    void shouldReturn422WhenAccountAlreadyClosed() throws Exception {
        when(closeAccountUseCase.execute(any(AccountNumber.class)))
                .thenThrow(new AccountAlreadyClosedException("1234567890"));

        mockMvc.perform(delete("/api/v1/accounts/1234567890"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("ACCOUNT_ALREADY_CLOSED"));
    }

    @Test
    @DisplayName("DELETE /api/v1/accounts/{accountNumber} — フラグOFFで501を返すこと")
    void shouldReturn501WhenFeatureDisabled() throws Exception {
        when(closeAccountUseCase.execute(any(AccountNumber.class)))
                .thenThrow(new FeatureDisabledException("account-closure"));

        mockMvc.perform(delete("/api/v1/accounts/1234567890"))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.code").value("FEATURE_DISABLED"));
    }

    @Test
    @DisplayName("GET /api/v1/accounts/{accountNumber} — レスポンスにstatusが含まれること")
    void shouldReturnStatusInResponse() throws Exception {
        Account account = Account.reconstruct(
                "id-1", accountNumber, "田中太郎", Money.of(1000), AccountStatus.ACTIVE, LocalDateTime.now());
        when(getAccountUseCase.execute(accountNumber)).thenReturn(account);

        mockMvc.perform(get("/api/v1/accounts/1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}
