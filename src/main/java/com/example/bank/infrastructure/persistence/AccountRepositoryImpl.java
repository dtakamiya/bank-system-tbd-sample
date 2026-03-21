package com.example.bank.infrastructure.persistence;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.repository.AccountRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * {@link AccountRepository} のJPA実装。
 *
 * <p>ドメイン層のリポジトリインターフェースを実装し、
 * {@link AccountJpaRepository} を介してデータベースへの永続化を行う。</p>
 */
@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountJpaRepository jpaRepository;

    public AccountRepositoryImpl(AccountJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Account save(Account account) {
        AccountJpaEntity entity = AccountJpaEntity.fromDomain(account);
        jpaRepository.save(entity);
        return account;
    }

    @Override
    public Optional<Account> findByAccountNumber(AccountNumber accountNumber) {
        return jpaRepository.findByAccountNumber(accountNumber.value())
                .map(AccountJpaEntity::toDomain);
    }
}
