package com.example.bank.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 口座作成リクエストDTO。
 *
 * @param ownerName 口座名義人（必須、100文字以内）
 */
public record CreateAccountRequest(
        @NotBlank(message = "口座名義人は必須です")
        @Size(max = 100, message = "口座名義人は100文字以内で入力してください")
        String ownerName
) {
}
