package com.example.bank;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * {@link BankSystemApplication} のスモークテスト。
 *
 * <p>Springアプリケーションコンテキストが正常にロードされることを検証する。</p>
 *
 * @see BankSystemApplication
 */
@SpringBootTest
@DisplayName("BankSystemApplication スモークテスト")
class BankSystemApplicationTests {

    @Test
    @DisplayName("アプリケーションコンテキストが正常にロードされること")
    void contextLoads() {
    }
}
