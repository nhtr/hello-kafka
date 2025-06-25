package com.nhtr.pss.websocket;

// Queue-based version
import com.nhtr.pss.kafka.ManualKafkaConsumerManager;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final SessionManager INSTANCE = new SessionManager();

    private final Map<String, WebSocketSessionWrapper> sessions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> symbolToSessions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> sessionToSymbols = new ConcurrentHashMap<>();

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public void registerSession(WebSocketSession session) {
        sessions.put(session.getId(), new WebSocketSessionWrapper(session));
    }

    public void unregisterSession(WebSocketSession session) {
        sessions.remove(session.getId()).shutdown();

        Set<String> symbols = sessionToSymbols.remove(session.getId());
        if (symbols != null) {
            for (String symbol : symbols) {
                Set<String> sessionIds = symbolToSessions.get(symbol);
                if (sessionIds != null) {
                    sessionIds.remove(session.getId());
                    if (sessionIds.isEmpty()) {
                        symbolToSessions.remove(symbol);
                    }
                }
            }
        }
    }

    public void subscribe(String sessionId, List<String> symbols) {
        WebSocketSessionWrapper sessionWrapper = sessions.get(sessionId);
        if (sessionWrapper == null) return;

        for (String symbol : symbols) {
            symbolToSessions.computeIfAbsent(symbol, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
            sessionToSymbols.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet()).add(symbol);
            ManualKafkaConsumerManager.getInstance().subscribeSymbol(symbol);
        }
    }

    public void unsubscribe(String sessionId, List<String> symbols) {
        for (String symbol : symbols) {
            Set<String> sessionIds = symbolToSessions.get(symbol);
            if (sessionIds != null) {
                sessionIds.remove(sessionId);
                if (sessionIds.isEmpty()) {
                    symbolToSessions.remove(symbol);
                }
            }
        }
        Set<String> subscribedSymbols = sessionToSymbols.get(sessionId);
        if (subscribedSymbols != null) {
            subscribedSymbols.removeAll(symbols);
        }
    }

    public void sendToSubscribers(String symbol, String message) {
        Set<String> sessionIds = symbolToSessions.get(symbol);
        if (sessionIds != null) {
            for (String sessionId : sessionIds) {
                WebSocketSessionWrapper sessionWrapper = sessions.get(sessionId);
                if (sessionWrapper != null) {
                    sessionWrapper.sendMessage(message);
                }
            }
        }
    }
}

//
//import com.nhtr.pss.kafka.ManualKafkaConsumerManager;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class SessionManager {
//    private static final SessionManager INSTANCE = new SessionManager();
//    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
//    private final Map<String, Set<String>> symbolToSessions = new ConcurrentHashMap<>();
//    private final Map<String, Set<String>> sessionToSymbols = new ConcurrentHashMap<>();
//
//    public static SessionManager getInstance() {
//        return INSTANCE;
//    }
//
//    public void registerSession(WebSocketSession session) {
//        sessions.put(session.getId(), session);
//    }
//
//    public void subscribe(WebSocketSession session, List<String> symbols) {
////        sessionToSymbols.put(session.getId(), new HashSet<>(symbols));
////        for (String symbol : symbols) {
////            symbolToSessions.computeIfAbsent(symbol, k -> new HashSet<>()).add(session.getId());
////            redisPublisher.publishSubscription(symbol);
////        }
//        // handle for kafka manual assignment
//        sessionToSymbols.put(session.getId(), new HashSet<>(symbols));
//        for (String symbol : symbols) {
//            symbolToSessions.computeIfAbsent(symbol, k -> new HashSet<>()).add(session.getId());
//            ManualKafkaConsumerManager.getInstance().subscribeSymbol(symbol);
//        }
//    }
//
//    public void unregisterSession(WebSocketSession session) {
////        Set<String> symbols = sessionToSymbols.remove(session.getId());
////        if (symbols != null) {
////            for (String symbol : symbols) {
////                Set<String> sessionIds = symbolToSessions.get(symbol);
////                if (sessionIds != null) {
////                    sessionIds.remove(session.getId());
////                    if (sessionIds.isEmpty()) {
////                        symbolToSessions.remove(symbol);
////                        redisPublisher.publishUnsubscription(symbol);
////                    }
////                }
////            }
////        }
////        sessions.remove(session.getId());
//        // handle for kafka manual assignment
//        Set<String> symbols = sessionToSymbols.remove(session.getId());
//        if (symbols != null) {
//            for (String symbol : symbols) {
//                Set<String> sessionIds = symbolToSessions.get(symbol);
//                if (sessionIds != null) {
//                    sessionIds.remove(session.getId());
//                    if (sessionIds.isEmpty()) {
//                        symbolToSessions.remove(symbol);
//                        ManualKafkaConsumerManager.getInstance().unsubscribeSymbol(symbol);
//                    }
//                }
//            }
//        }
//        sessions.remove(session.getId());
//    }
//
//    public void sendToSubscribers(String symbol, String message) {
//        Set<String> sessionIds = symbolToSessions.get(symbol);
//        if (sessionIds != null) {
//            for (String sessionId : sessionIds) {
//                WebSocketSession session = sessions.get(sessionId);
//                if (session != null && session.isOpen()) {
//                    try {
//                        session.sendMessage(new TextMessage(message));
//                    } catch (IOException e) {
//                        // Handle failure
//                    }
//                }
//            }
//        }
//    }
//}
//
//
