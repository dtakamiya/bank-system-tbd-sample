package com.example.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 銀行システムのSpring Bootアプリケーションエントリーポイント。
 */
@SpringBootApplication
public class BankSystemApplication {

    /**
     * アプリケーションを起動する。
     *
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(BankSystemApplication.class, args);
    }
}
