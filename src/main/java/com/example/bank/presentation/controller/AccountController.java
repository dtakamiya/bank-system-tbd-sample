package com.example.bank.presentation.controller;

import com.example.bank.application.usecase.CloseAccountUseCase;
import com.example.bank.application.usecase.CreateAccountUseCase;
import com.example.bank.application.usecase.DepositUseCase;
import com.example.bank.application.usecase.GetAccountUseCase;
import com.example.bank.application.usecase.WithdrawUseCase;
import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Money;
import com.example.bank.presentation.request.AmountRequest;
import com.example.bank.presentation.request.CreateAccountRequest;
import com.example.bank.presentation.response.AccountResponse;
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

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final CloseAccountUseCase closeAccountUseCase;

    public AccountController(CreateAccountUseCase createAccountUseCase,
                             GetAccountUseCase getAccountUseCase,
                             DepositUseCase depositUseCase,
                             WithdrawUseCase withdrawUseCase,
                             CloseAccountUseCase closeAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
        this.getAccountUseCase = getAccountUseCase;
        this.depositUseCase = depositUseCase;
        this.withdrawUseCase = withdrawUseCase;
        this.closeAccountUseCase = closeAccountUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request) {
        Account account = createAccountUseCase.execute(request.ownerName());
        return AccountResponse.from(account);
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse getAccount(@PathVariable String accountNumber) {
        Account account = getAccountUseCase.execute(new AccountNumber(accountNumber));
        return AccountResponse.from(account);
    }

    @PostMapping("/{accountNumber}/deposit")
    public AccountResponse deposit(@PathVariable String accountNumber,
                                   @Valid @RequestBody AmountRequest request) {
        Account account = depositUseCase.execute(
                new AccountNumber(accountNumber), Money.of(request.amount()));
        return AccountResponse.from(account);
    }

    @PostMapping("/{accountNumber}/withdraw")
    public AccountResponse withdraw(@PathVariable String accountNumber,
                                    @Valid @RequestBody AmountRequest request) {
        Account account = withdrawUseCase.execute(
                new AccountNumber(accountNumber), Money.of(request.amount()));
        return AccountResponse.from(account);
    }

    @DeleteMapping("/{accountNumber}")
    public AccountResponse closeAccount(@PathVariable String accountNumber) {
        Account account = closeAccountUseCase.execute(new AccountNumber(accountNumber));
        return AccountResponse.from(account);
    }
}
