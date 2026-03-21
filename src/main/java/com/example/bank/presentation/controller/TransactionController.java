package com.example.bank.presentation.controller;

import com.example.bank.application.usecase.GetTransactionHistoryUseCase;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Transaction;
import com.example.bank.presentation.response.TransactionListResponse;
import com.example.bank.presentation.response.TransactionResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 取引履歴に関するREST APIコントローラー。
 *
 * <p>口座に紐づく取引履歴の取得エンドポイントを提供する。
 * ベースパス: {@code /api/v1/accounts/{accountNumber}/transactions}
 */
@RestController
@RequestMapping("/api/v1/accounts/{accountNumber}/transactions")
public class TransactionController {

    private final GetTransactionHistoryUseCase getTransactionHistoryUseCase;

    /**
     * コンストラクタ。
     *
     * @param getTransactionHistoryUseCase 取引履歴取得ユースケース
     */
    public TransactionController(GetTransactionHistoryUseCase getTransactionHistoryUseCase) {
        this.getTransactionHistoryUseCase = getTransactionHistoryUseCase;
    }

    /**
     * 指定口座の取引履歴をページネーション付きで取得する。
     *
     * <p>GET /api/v1/accounts/{accountNumber}/transactions
     *
     * @param accountNumber 口座番号
     * @param page          ページ番号（0始まり、デフォルト: 0）
     * @param size          1ページあたりの件数（デフォルト: 20）
     * @return 取引履歴一覧
     */
    @GetMapping
    public TransactionListResponse getTransactions(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Transaction> transactions = getTransactionHistoryUseCase.execute(
                new AccountNumber(accountNumber), page, size);

        List<TransactionResponse> responses = transactions.stream()
                .map(TransactionResponse::from)
                .toList();

        return new TransactionListResponse(responses);
    }
}
