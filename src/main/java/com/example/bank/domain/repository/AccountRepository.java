package com.example.bank.domain.repository;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNumber;

import java.util.Optional;

/**
 * 銀行口座の永続化を担うリポジトリインターフェース。
 * 口座の保存および口座番号による検索機能を提供する。
 */
public interface AccountRepository {

    /**
     * 口座を保存する。新規作成・既存更新の両方に対応する。
     *
     * @param account 保存対象の口座
     * @return 保存後の口座（IDや監査情報が付与された状態）
     */
    Account save(Account account);

    /**
     * 口座番号に一致する口座を検索する。
     *
     * @param accountNumber 検索対象の口座番号
     * @return 該当する口座。存在しない場合は空の {@link Optional}
     */
    Optional<Account> findByAccountNumber(AccountNumber accountNumber);
}
