package com.example.bank.infrastructure.persistence;

import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.repository.TransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;

    public TransactionRepositoryImpl(TransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionJpaEntity entity = TransactionJpaEntity.fromDomain(transaction);
        jpaRepository.save(entity);
        return transaction;
    }

    @Override
    public List<Transaction> findByAccountNumber(AccountNumber accountNumber, int page, int size) {
        return jpaRepository
                .findByAccountNumberOrderByCreatedAtDesc(
                        accountNumber.value(), PageRequest.of(page, size))
                .map(TransactionJpaEntity::toDomain)
                .getContent();
    }
}
