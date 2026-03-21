package com.example.bank.application.port;

/**
 * フィーチャーフラグの状態を提供するサービスのポートインターフェース。
 *
 * <p>各機能の有効・無効をフラグ名で判定する。</p>
 */
public interface FeatureFlagService {

    /**
     * 指定されたフィーチャーフラグが有効かどうかを判定する。
     *
     * @param featureName フィーチャーフラグ名
     * @return 有効な場合は {@code true}、無効な場合は {@code false}
     */
    boolean isEnabled(String featureName);
}
