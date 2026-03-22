package com.example.bank.presentation.controller;

import com.example.bank.application.usecase.CloseAccountUseCase;
import com.example.bank.application.usecase.CreateAccountUseCase;
import com.example.bank.application.usecase.DepositUseCase;
import com.example.bank.application.usecase.GetAccountUseCase;
import com.example.bank.application.usecase.TransferUseCase;
import com.example.bank.application.usecase.WithdrawUseCase;
import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.TransferResult;
import com.example.bank.presentation.request.AmountRequest;
import com.example.bank.presentation.request.CreateAccountRequest;
import com.example.bank.presentation.request.TransferRequest;
import com.example.bank.presentation.response.AccountResponse;
import com.example.bank.presentation.response.TransferResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 口座に関するREST APIコントローラー。
 *
 * <p>口座の作成・照会・入金・出金・解約の各エンドポイントを提供する。
 * ベースパス: {@code /api/v1/accounts}
 */
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final CloseAccountUseCase closeAccountUseCase;
    private final TransferUseCase transferUseCase;

    public AccountController(CreateAccountUseCase createAccountUseCase,
                             GetAccountUseCase getAccountUseCase,
                             DepositUseCase depositUseCase,
                             WithdrawUseCase withdrawUseCase,
                             CloseAccountUseCase closeAccountUseCase,
                             TransferUseCase transferUseCase) {
        this.createAccountUseCase = createAccountUseCase;
        this.getAccountUseCase = getAccountUseCase;
        this.depositUseCase = depositUseCase;
        this.withdrawUseCase = withdrawUseCase;
        this.closeAccountUseCase = closeAccountUseCase;
        this.transferUseCase = transferUseCase;
    }

    /**
     * 新規口座を作成する。
     *
     * <p>POST /api/v1/accounts
     *
     * @param request 口座作成リクエスト（名義人名を含む）
     * @return 作成された口座情報
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request) {
        Account account = createAccountUseCase.execute(request.ownerName());
        return AccountResponse.from(account);
    }

    /**
     * 口座番号を指定して口座情報を取得する。
     *
     * <p>GET /api/v1/accounts/{accountNumber}
     *
     * @param accountNumber 口座番号
     * @return 口座情報
     */
    @GetMapping("/{accountNumber}")
    public AccountResponse getAccount(@PathVariable String accountNumber) {
        Account account = getAccountUseCase.execute(new AccountNumber(accountNumber));
        return AccountResponse.from(account);
    }

    /**
     * 指定口座に入金する。
     *
     * <p>POST /api/v1/accounts/{accountNumber}/deposit
     *
     * @param accountNumber 入金先の口座番号
     * @param request       入金金額リクエスト
     * @return 入金後の口座情報
     */
    @PostMapping("/{accountNumber}/deposit")
    public AccountResponse deposit(@PathVariable String accountNumber,
                                   @Valid @RequestBody AmountRequest request) {
        Account account = depositUseCase.execute(
                new AccountNumber(accountNumber), Money.of(request.amount()));
        return AccountResponse.from(account);
    }

    /**
     * 指定口座から出金する。
     *
     * <p>POST /api/v1/accounts/{accountNumber}/withdraw
     *
     * @param accountNumber 出金元の口座番号
     * @param request       出金金額リクエスト
     * @return 出金後の口座情報
     */
    @PostMapping("/{accountNumber}/withdraw")
    public AccountResponse withdraw(@PathVariable String accountNumber,
                                    @Valid @RequestBody AmountRequest request) {
        Account account = withdrawUseCase.execute(
                new AccountNumber(accountNumber), Money.of(request.amount()));
        return AccountResponse.from(account);
    }

    /**
     * 指定口座を解約する。
     *
     * <p>DELETE /api/v1/accounts/{accountNumber}
     *
     * @param accountNumber 解約する口座番号
     * @return 解約後の口座情報
     */
    /**
     * 口座間送金を実行する。
     *
     * <p>POST /api/v1/accounts/{accountNumber}/transfer
     *
     * @param accountNumber 送金元の口座番号
     * @param request       送金リクエスト（送金先口座番号、金額）
     * @return 送金結果
     */
    @PostMapping("/{accountNumber}/transfer")
    public TransferResponse transfer(@PathVariable String accountNumber,
                                     @Valid @RequestBody TransferRequest request) {
        TransferResult result = transferUseCase.execute(
                new AccountNumber(accountNumber),
                new AccountNumber(request.targetAccountNumber()),
                Money.of(request.amount()));
        return TransferResponse.from(result);
    }

    @DeleteMapping("/{accountNumber}")
    public AccountResponse closeAccount(@PathVariable String accountNumber) {
        Account account = closeAccountUseCase.execute(new AccountNumber(accountNumber));
        return AccountResponse.from(account);
    }
}
