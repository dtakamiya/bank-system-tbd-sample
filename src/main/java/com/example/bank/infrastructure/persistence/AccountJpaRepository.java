package com.example.bank.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 口座JPAエンティティ用のSpring Data JPAリポジトリ。
 *
 * <p>{@link AccountJpaEntity} に対する基本的なCRUD操作と、
 * 口座番号による検索機能を提供する。</p>
 */
public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, String> {

    /**
     * 口座番号に一致する口座エンティティを検索する。
     *
     * @param accountNumber 検索対象の口座番号
     * @return 一致する口座エンティティ（存在しない場合は空）
     */
    Optional<AccountJpaEntity> findByAccountNumber(String accountNumber);
}
