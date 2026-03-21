package com.example.bank.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, String> {

    Page<TransactionJpaEntity> findByAccountNumberOrderByCreatedAtDesc(
            String accountNumber, Pageable pageable);
}
