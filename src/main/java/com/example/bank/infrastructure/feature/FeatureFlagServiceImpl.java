package com.example.bank.infrastructure.feature;

import com.example.bank.application.port.FeatureFlagService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * {@link FeatureFlagService} のSpring Environment実装。
 *
 * <p>Springの {@link Environment} を利用して、{@code bank.features.<フィーチャー名>}
 * プロパティからフィーチャーフラグの有効/無効を判定する。</p>
 */
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
