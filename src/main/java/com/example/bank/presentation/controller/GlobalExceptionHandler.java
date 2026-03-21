package com.example.bank.presentation.controller;

import com.example.bank.domain.model.AccountAlreadyClosedException;
import com.example.bank.domain.model.AccountNotFoundException;
import com.example.bank.domain.model.FeatureDisabledException;
import com.example.bank.domain.model.InsufficientBalanceException;
import com.example.bank.domain.model.InvalidAmountException;
import com.example.bank.presentation.response.ErrorCode;
import com.example.bank.presentation.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * アプリケーション全体の例外ハンドラー。
 *
 * <p>ドメイン例外およびバリデーション例外をキャッチし、
 * 適切なHTTPステータスコードとエラーレスポンスに変換する。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 口座が見つからない場合の例外をハンドルする。
     *
     * @param ex {@link AccountNotFoundException}
     * @return 404 NOT_FOUND エラーレスポンス
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ErrorCode.ACCOUNT_NOT_FOUND, ex.getMessage());
    }

    /**
     * 残高不足の例外をハンドルする。
     *
     * @param ex {@link InsufficientBalanceException}
     * @return 422 UNPROCESSABLE_ENTITY エラーレスポンス
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.INSUFFICIENT_BALANCE, ex.getMessage());
    }

    /**
     * 不正な金額指定の例外をハンドルする。
     *
     * @param ex {@link InvalidAmountException}
     * @return 400 BAD_REQUEST エラーレスポンス
     */
    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAmount(InvalidAmountException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_AMOUNT, ex.getMessage());
    }

    /**
     * 既に解約済みの口座に対する操作の例外をハンドルする。
     *
     * @param ex {@link AccountAlreadyClosedException}
     * @return 422 UNPROCESSABLE_ENTITY エラーレスポンス
     */
    @ExceptionHandler(AccountAlreadyClosedException.class)
    public ResponseEntity<ErrorResponse> handleAccountAlreadyClosed(AccountAlreadyClosedException ex) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.ACCOUNT_ALREADY_CLOSED, ex.getMessage());
    }

    /**
     * 機能が無効化されている場合の例外をハンドルする。
     *
     * @param ex {@link FeatureDisabledException}
     * @return 501 NOT_IMPLEMENTED エラーレスポンス
     */
    @ExceptionHandler(FeatureDisabledException.class)
    public ResponseEntity<ErrorResponse> handleFeatureDisabled(FeatureDisabledException ex) {
        return buildErrorResponse(HttpStatus.NOT_IMPLEMENTED, ErrorCode.FEATURE_DISABLED, ex.getMessage());
    }

    /**
     * リクエストボディのバリデーションエラーをハンドルする。
     *
     * @param ex {@link MethodArgumentNotValidException}
     * @return 400 BAD_REQUEST エラーレスポンス（フィールドエラーの詳細を含む）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_REQUEST, message);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, ErrorCode code, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(code.name(), message));
    }
}
