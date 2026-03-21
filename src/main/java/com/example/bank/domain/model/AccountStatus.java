package com.example.bank.domain.model;

/**
 * 口座の状態を表す列挙型。
 */
public enum AccountStatus {
    /** 有効（取引可能） */
    ACTIVE,
    /** 解約済み */
    CLOSED
}
