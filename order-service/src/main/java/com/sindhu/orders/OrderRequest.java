package com.sindhu.orders;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record OrderRequest(
        @NotBlank String customerId,
        @NotBlank String product,
        @Min(1) int quantity,
        @DecimalMin("0.01") BigDecimal unitPrice) {
}
