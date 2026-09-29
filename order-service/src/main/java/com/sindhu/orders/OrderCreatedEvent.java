package com.sindhu.orders;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderCreatedEvent(
        String orderId,
        String customerId,
        String product,
        int quantity,
        BigDecimal total,
        Instant createdAt) {
}
