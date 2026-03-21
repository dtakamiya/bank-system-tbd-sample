package com.example.bank.infrastructure.feature;

import com.example.bank.application.port.FeatureFlagService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class FeatureFlagServiceImpl implements FeatureFlagService {

    private final Environment environment;

    public FeatureFlagServiceImpl(Environment environment) {
        this.environment = environment;
    }

    @Override
    public boolean isEnabled(String featureName) {
        return environment.getProperty(
                "bank.features." + featureName, Boolean.class, false);
    }
}
