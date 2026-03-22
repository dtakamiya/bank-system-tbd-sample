package com.example.bank.domain.service;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.SameAccountTransferException;
import com.example.bank.domain.model.TransferResult;

/**
 * 口座間送金を処理するドメインサービス。
 *
 * <p>2つの {@link Account} を協調させ、送金元からの出金と送金先への入金を実行する。
 * Pure Javaで実装され、フレームワークに依存しない。</p>
 */
public class TransferService {

    /**
     * 送金を実行する。
     *
     * @param sourceAccount 送金元口座
     * @param targetAccount 送金先口座
     * @param amount        送金額
     * @param fee           手数料
     * @return 送金結果
     * @throws SameAccountTransferException 送金元と送金先が同一口座の場合
     */
    public TransferResult transfer(Account sourceAccount, Account targetAccount,
                                   Money amount, Money fee) {
        if (sourceAccount.getAccountNumber().equals(targetAccount.getAccountNumber())) {
            throw new SameAccountTransferException(sourceAccount.getAccountNumber().value());
        }

        Account updatedSource = sourceAccount.withdraw(amount.add(fee));
        Account updatedTarget = targetAccount.deposit(amount);

        return new TransferResult(updatedSource, updatedTarget, amount, fee);
    }
}
