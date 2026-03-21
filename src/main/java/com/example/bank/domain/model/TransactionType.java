package com.example.bank.domain.model;

/**
 * 取引種別を表す列挙型。
 */
public enum TransactionType {
    /** 入金 */
    DEPOSIT,
    /** 出金 */
    WITHDRAWAL,
    /** 返金 */
    REFUND
}
