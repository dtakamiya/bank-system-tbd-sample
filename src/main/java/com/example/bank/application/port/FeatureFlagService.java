package com.example.bank.application.port;

public interface FeatureFlagService {

    boolean isEnabled(String featureName);
}
