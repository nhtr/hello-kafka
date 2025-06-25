package com.nhtr.pss.kafka;

import com.nhtr.pss.websocket.SessionManager;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

// consumer API manual assignment
public class KafkaConsumerWorker implements Runnable {
    private final String topic;
    private final KafkaConsumer<String, String> consumer;
    private volatile boolean running = true;
    private final SessionManager sessionManager = SessionManager.getInstance();

    public KafkaConsumerWorker(String topic) {
        this.topic = topic;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:29092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "manual-consumer-group-" + topic);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        this.consumer = new KafkaConsumer<>(props);
        this.consumer.subscribe(Collections.singletonList(topic));
    }

//    @Override
//    public void run() {
//        try {
//            while (running) {
//                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
//                for (ConsumerRecord<String, String> record : records) {
//                    sessionManager.sendToSubscribers(topic, record.value());
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Consumer for topic " + topic + " stopped with error: " + e.getMessage());
//        } finally {
//            consumer.close();
//        }
//    }

    @Override
    public void run() {
        try {
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    sessionManager.sendToSubscribers(topic, record.value());
                }
                // Commit offset thường xuyên để tránh mất dữ liệu
                consumer.commitSync();
            }
        } catch (Exception e) {
            System.err.println("Consumer for topic " + topic + " stopped with error: " + e.getMessage());
        } finally {
            try {
                // Final commit
                consumer.commitSync();
            } catch (Exception e) {
                System.err.println("Failed to commit offset during shutdown: " + e.getMessage());
            }
            consumer.close();
            System.out.println("Kafka consumer for topic " + topic + " closed.");
        }
    }

    public void shutdown() {
        running = false;
    }
}

