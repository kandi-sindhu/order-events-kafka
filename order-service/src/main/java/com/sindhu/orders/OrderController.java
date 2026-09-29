package com.sindhu.orders;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final String topic;

    public OrderController(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
                           @Value("${app.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderCreatedEvent create(@Valid @RequestBody OrderRequest request) {
        var event = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                request.customerId(),
                request.product(),
                request.quantity(),
                request.unitPrice().multiply(BigDecimal.valueOf(request.quantity())),
                Instant.now());

        // Keyed by customer so all of a customer's orders land on the same partition, in order.
        kafkaTemplate.send(topic, event.customerId(), event);
        return event;
    }
}
