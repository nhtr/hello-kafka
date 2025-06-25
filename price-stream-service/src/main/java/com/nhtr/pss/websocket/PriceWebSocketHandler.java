package com.nhtr.pss.websocket;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.nhtr.pss.model.SubscriptionRequest;
//import lombok.NonNull;
//import org.springframework.web.socket.*;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//public class PriceWebSocketHandler extends TextWebSocketHandler {
//
//    private final SessionManager sessionManager = SessionManager.getInstance();
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
//        sessionManager.registerSession(session);
//    }
//
//    @Override
//    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
//        SubscriptionRequest request = objectMapper.readValue(message.getPayload(), SubscriptionRequest.class);
//        sessionManager.subscribe(session, request.getSymbols());
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
//        sessionManager.unregisterSession(session);
//    }
//}

// Queue-based version
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhtr.pss.model.SubscriptionRequest;
import lombok.NonNull;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

public class PriceWebSocketHandler extends TextWebSocketHandler {

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        sessionManager.registerSession(session);
        System.out.println("WebSocket connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        SubscriptionRequest request = objectMapper.readValue(message.getPayload(), SubscriptionRequest.class);
        List<String> symbols = request.getSymbols();
        sessionManager.subscribe(session.getId(), symbols);
        System.out.println("WebSocket " + session.getId() + " subscribed to symbols: " + symbols);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionManager.unregisterSession(session);
        System.out.println("WebSocket disconnected: " + session.getId());
    }
}

