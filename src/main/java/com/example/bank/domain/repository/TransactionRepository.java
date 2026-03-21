package com.example.bank.domain.repository;

import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Transaction;

import java.util.List;

/**
 * 取引履歴の永続化を担うリポジトリインターフェース。
 * 取引の保存および口座番号による取引履歴の検索機能を提供する。
 */
public interface TransactionRepository {

    /**
     * 取引を保存する。
     *
     * @param transaction 保存対象の取引
     * @return 保存後の取引（IDや監査情報が付与された状態）
     */
    Transaction save(Transaction transaction);

    /**
     * 指定された口座番号に紐づく取引履歴をページネーション付きで取得する。
     *
     * @param accountNumber 検索対象の口座番号
     * @param page ページ番号（0始まり）
     * @param size 1ページあたりの取得件数
     * @return 該当する取引のリスト。存在しない場合は空のリスト
     */
    List<Transaction> findByAccountNumber(AccountNumber accountNumber, int page, int size);
}
