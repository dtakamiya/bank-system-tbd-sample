package com.example.bank.presentation.response;

/**
 * APIエラーレスポンスで使用するエラーコードの列挙型。
 */
public enum ErrorCode {
    /** 口座が見つからない */
    ACCOUNT_NOT_FOUND,
    /** 残高不足 */
    INSUFFICIENT_BALANCE,
    /** 不正な金額指定 */
    INVALID_AMOUNT,
    /** リクエスト内容が不正 */
    INVALID_REQUEST,
    /** 機能が無効化されている */
    FEATURE_DISABLED,
    /** 口座が既に解約済み */
    ACCOUNT_ALREADY_CLOSED
}
