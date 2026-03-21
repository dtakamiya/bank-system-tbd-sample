package com.example.bank.domain.model;

public final class FeatureDisabledException extends DomainException {

    public FeatureDisabledException(String featureName) {
        super("機能が無効です: " + featureName);
    }
}
