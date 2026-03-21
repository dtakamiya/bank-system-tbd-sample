package com.example.bank;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

/**
 * アーキテクチャ制約のテスト。
 *
 * <p>ArchUnitを使用して、オニオンアーキテクチャのレイヤー依存関係ルール
 * （domain / application / infrastructure / presentation）が
 * 守られていることを自動検証する。</p>
 */
@AnalyzeClasses(packages = "com.example.bank", importOptions = ImportOption.DoNotIncludeTests.class)
@DisplayName("アーキテクチャ制約テスト")
class ArchitectureTest {

    @ArchTest
    static final ArchRule onion_architecture_is_respected = onionArchitecture()
            .domainModels("..domain.model..")
            .domainServices("..domain.repository..")
            .applicationServices("..application..")
            .adapter("persistence", "..infrastructure.persistence..")
            .adapter("feature", "..infrastructure.feature..")
            .adapter("config", "..infrastructure.config..")
            .adapter("web", "..presentation..");

    @ArchTest
    static final ArchRule domain_should_not_depend_on_application =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..application..");

    @ArchTest
    static final ArchRule domain_should_not_depend_on_infrastructure =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..infrastructure..");

    @ArchTest
    static final ArchRule domain_should_not_depend_on_presentation =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..presentation..");

    @ArchTest
    static final ArchRule application_should_not_depend_on_infrastructure =
            noClasses().that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..infrastructure..");

    @ArchTest
    static final ArchRule application_should_not_depend_on_presentation =
            noClasses().that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..presentation..");

    @ArchTest
    static final ArchRule domain_should_not_use_spring_framework =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("org.springframework..");

    @ArchTest
    static final ArchRule application_policy_should_only_depend_on_domain =
            noClasses().that().resideInAPackage("..application.policy..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..infrastructure..", "..presentation..");

    @ArchTest
    static final ArchRule domain_should_not_depend_on_application_policy =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..application.policy..");

    @ArchTest
    static final ArchRule application_policy_should_not_depend_on_feature_flag_service =
            noClasses().that().resideInAPackage("..application.policy..")
                    .should().dependOnClassesThat()
                    .haveSimpleName("FeatureFlagService");
}
