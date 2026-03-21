package com.example.bank.presentation.response;

/**
 * エラーレスポンスDTO。
 *
 * @param code    エラーコード（{@link ErrorCode} の名称）
 * @param message エラーメッセージ
 */
public record ErrorResponse(
        String code,
        String message
) {
}
