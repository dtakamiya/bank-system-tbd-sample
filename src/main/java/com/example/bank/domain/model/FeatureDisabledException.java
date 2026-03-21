package com.example.bank.domain.model;

/**
 * 無効化されている機能にアクセスしようとした場合にスローされる例外。
 */
public final class FeatureDisabledException extends DomainException {

    /**
     * 機能無効例外を生成する。
     *
     * @param featureName 無効化されている機能名
     */
    public FeatureDisabledException(String featureName) {
        super("機能が無効です: " + featureName);
    }
}
