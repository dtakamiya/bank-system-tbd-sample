package com.example.bank.application.usecase;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNotFoundException;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.repository.AccountRepository;
import com.example.bank.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 口座への入金を行うユースケース。
 *
 * <p>指定された口座に入金し、取引履歴を記録する。</p>
 */
@Service
public class DepositUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public DepositUseCase(AccountRepository accountRepository,
                          TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * 指定された口座に入金を実行する。
     *
     * @param accountNumber 入金先の口座番号
     * @param amount 入金額
     * @return 入金後の口座
     * @throws AccountNotFoundException 指定された口座番号の口座が存在しない場合
     */
    @Transactional
    public Account execute(AccountNumber accountNumber, Money amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        Account deposited = account.deposit(amount);
        accountRepository.save(deposited);

        Transaction transaction = Transaction.deposit(
                accountNumber, amount, deposited.getBalance());
        transactionRepository.save(transaction);

        return deposited;
    }
}
