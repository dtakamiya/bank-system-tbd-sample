package com.example.bank.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 取引JPAエンティティ用のSpring Data JPAリポジトリ。
 *
 * <p>{@link TransactionJpaEntity} に対する基本的なCRUD操作と、
 * 口座番号による取引履歴の検索機能を提供する。</p>
 */
public interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, String> {

    /**
     * 指定された口座番号の取引履歴を作成日時の降順で取得する。
     *
     * @param accountNumber 検索対象の口座番号
     * @param pageable ページネーション情報
     * @return 取引エンティティのページ
     */
    Page<TransactionJpaEntity> findByAccountNumberOrderByCreatedAtDesc(
            String accountNumber, Pageable pageable);
}
