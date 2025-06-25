package com.nhtr.pss.kafka;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

// consumer API manual assignment
public class ManualKafkaConsumerManager {
    private final Map<String, KafkaConsumerWorker> consumerWorkers = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private static final ManualKafkaConsumerManager INSTANCE = new ManualKafkaConsumerManager();

    public static ManualKafkaConsumerManager getInstance() {
        return INSTANCE;
    }

    public synchronized void subscribeSymbol(String symbol) {
        if (!consumerWorkers.containsKey(symbol)) {
            KafkaConsumerWorker worker = new KafkaConsumerWorker(symbol);
            consumerWorkers.put(symbol, worker);
            executorService.submit(worker);
        }
    }

    public synchronized void unsubscribeSymbol(String symbol) {
        KafkaConsumerWorker worker = consumerWorkers.remove(symbol);
        if (worker != null) {
            worker.shutdown();
        }
    }

    // Graceful Shutdown
    public void shutdownAll() {
        for (KafkaConsumerWorker worker : consumerWorkers.values()) {
            worker.shutdown();
        }
        consumerWorkers.clear();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
