package com.nhtr.pss;

import com.nhtr.pss.kafka.ManualKafkaConsumerManager;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class ShutdownManager {

    @PreDestroy
    public void onShutdown() {
        System.out.println("Graceful shutdown: closing all consumer threads...");
        ManualKafkaConsumerManager.getInstance().shutdownAll();
    }
}
