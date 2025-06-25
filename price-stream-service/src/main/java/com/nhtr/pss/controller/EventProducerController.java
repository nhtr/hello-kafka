package com.nhtr.pss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhtr.pss.kafka.KafkaEventProducer;
import com.nhtr.pss.model.EventMessage;
import com.nhtr.pss.model.EventRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/events")
public class EventProducerController {

    private final KafkaEventProducer kafkaEventProducer;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EventProducerController(KafkaEventProducer kafkaEventProducer) {
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @PostMapping("/send")
    public String sendEvents(@RequestBody List<EventRequest> eventRequests) throws InterruptedException {
        for (EventRequest request : eventRequests) {
            executorService.submit(() -> {
                try {
                    EventMessage eventMessage = new EventMessage(request.getSymbol(), request.getPrice(), System.currentTimeMillis());
                    String jsonMessage = objectMapper.writeValueAsString(eventMessage);
                    kafkaEventProducer.send(request.getSymbol(), jsonMessage);
                } catch (Exception e) {
                    System.err.println("Error sending event: " + e.getMessage());
                }
            });
            Thread.sleep(500);
        }
        return "Events scheduled for sending.";
    }
}
