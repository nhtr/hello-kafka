package com.nhtr.pretrade.kafka.consumer;

import com.nhtr.schema.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventConsumer {
    @KafkaListener(topics = "order-created", groupId = "order-service")
    public void consumeBatch(ConsumerRecord<String, OrderEvent> consumerRecord) {
        log.info("Received message id: {}, value: {} ", consumerRecord.key(), consumerRecord.value());
    }
}
