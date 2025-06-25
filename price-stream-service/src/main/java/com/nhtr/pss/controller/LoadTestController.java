package com.nhtr.pss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhtr.pss.kafka.KafkaEventProducer;
import com.nhtr.pss.model.EventMessage;
import com.nhtr.pss.model.LoadTestRequest;
import com.nhtr.pss.model.SymbolLoadConfig;
import org.springframework.web.bind.annotation.*;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/load-test")
public class LoadTestController {

    private final KafkaEventProducer kafkaEventProducer;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    public LoadTestController(KafkaEventProducer kafkaEventProducer) {
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @PostMapping("/start")
    public String startLoadTest(@RequestBody LoadTestRequest request) {
        executorService.submit(() -> runLoadTest(request));
        return "Load test started.";
    }

    private void runLoadTest(LoadTestRequest request) {
        try {
            double currentPrice = request.getStartPrice();
            int sentMessages = 0;
            int delayMillis = 1000 / request.getMessagesPerSecond();

            while (sentMessages < request.getMessageCount()) {
                long timestamp = System.currentTimeMillis();
                double priceFluctuation = random.nextDouble() * 0.5 - 0.25; // +/- 0.25
                currentPrice += priceFluctuation;

                EventMessage eventMessage = new EventMessage(request.getSymbol(), String.format("%.2f", currentPrice), timestamp);
                String jsonMessage = objectMapper.writeValueAsString(eventMessage);
                kafkaEventProducer.send(request.getSymbol(), jsonMessage);

                sentMessages++;
                Thread.sleep(delayMillis);
            }

            System.out.println("Load test completed for symbol: " + request.getSymbol());

        } catch (Exception e) {
            System.err.println("Error during load test: " + e.getMessage());
        }
    }

    @PostMapping("/start-multiple-symbol")
    public String startLoadTestMultipleSymbol(@RequestBody LoadTestRequest request) {
        for (SymbolLoadConfig config : request.getSymbols()) {
            executorService.submit(() -> runLoadTest(config));
        }
        return "Multi-symbol load test started.";
    }

    private void runLoadTest(SymbolLoadConfig config) {
        try {
            double currentPrice = config.getStartPrice();
            int sentMessages = 0;
            int delayMillis = 1000 / config.getMessagesPerSecond();

            while (sentMessages < config.getMessageCount()) {
                long timestamp = System.currentTimeMillis();
                double priceFluctuation = random.nextDouble() * 0.5 - 0.25; // +/- 0.25
                currentPrice += priceFluctuation;

                EventMessage eventMessage = new EventMessage(config.getSymbol(), String.format("%.2f", currentPrice), timestamp);
                String jsonMessage = objectMapper.writeValueAsString(eventMessage);
                kafkaEventProducer.send(config.getSymbol(), jsonMessage);

                sentMessages++;
                Thread.sleep(delayMillis);
            }

            System.out.println("Load test completed for symbol: " + config.getSymbol());

        } catch (Exception e) {
            System.err.println("Error during load test for symbol " + config.getSymbol() + ": " + e.getMessage());
        }
    }
}
