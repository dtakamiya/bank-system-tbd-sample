package com.example.bank.application.usecase;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

/**
 * 銀行口座の新規作成を行うユースケース。
 *
 * <p>口座名義人の名前を受け取り、新しい口座を作成してリポジトリに保存する。</p>
 */
@Service
public class CreateAccountUseCase {

    private final AccountRepository accountRepository;

    public CreateAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * 指定された名義人名で新しい口座を作成する。
     *
     * @param ownerName 口座名義人の名前
     * @return 作成された口座
     */
    public Account execute(String ownerName) {
        Account account = Account.create(ownerName);
        return accountRepository.save(account);
    }
}
