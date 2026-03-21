package com.example.bank.presentation.response;

import java.util.List;

/**
 * 取引履歴一覧のレスポンスDTO。
 *
 * @param transactions 取引情報のリスト
 */
public record TransactionListResponse(
        List<TransactionResponse> transactions
) {
}
