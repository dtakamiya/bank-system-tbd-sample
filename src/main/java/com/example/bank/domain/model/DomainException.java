package com.example.bank.domain.model;

/**
 * ドメイン層で発生するビジネスルール違反を表す基底例外クラス。
 *
 * <p>sealed クラスとして定義され、許可されたサブクラスのみが継承できる。</p>
 */
public sealed abstract class DomainException extends RuntimeException
        permits InsufficientBalanceException, AccountNotFoundException, InvalidAmountException, FeatureDisabledException, AccountAlreadyClosedException {

    /**
     * 指定されたメッセージでドメイン例外を生成する。
     *
     * @param message エラーメッセージ
     */
    protected DomainException(String message) {
        super(message);
    }
}
