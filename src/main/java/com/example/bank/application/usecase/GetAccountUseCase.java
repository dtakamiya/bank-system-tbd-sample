package com.example.bank.application.usecase;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNotFoundException;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

/**
 * 口座情報の取得を行うユースケース。
 *
 * <p>口座番号を指定して口座情報を検索・取得する。</p>
 */
@Service
public class GetAccountUseCase {

    private final AccountRepository accountRepository;

    public GetAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * 指定された口座番号の口座情報を取得する。
     *
     * @param accountNumber 取得対象の口座番号
     * @return 該当する口座
     * @throws AccountNotFoundException 指定された口座番号の口座が存在しない場合
     */
    public Account execute(AccountNumber accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }
}
