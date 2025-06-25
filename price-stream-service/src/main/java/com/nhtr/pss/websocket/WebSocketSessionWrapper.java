package com.nhtr.pss.websocket;

import lombok.Getter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WebSocketSessionWrapper {

    @Getter
    private final WebSocketSession session;
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private final Thread writerThread;
    private volatile boolean running = true;

    public WebSocketSessionWrapper(WebSocketSession session) {
        this.session = session;

        this.writerThread = new Thread(this::processQueue);
        this.writerThread.start();
    }

    private void processQueue() {
        try {
            while (running && session.isOpen()) {
                String message = messageQueue.take(); // Blocking wait
                session.sendMessage(new TextMessage(message));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            System.err.println("WebSocket write error: " + e.getMessage());
        } finally {
            try {
                session.close();
            } catch (IOException e) {
                System.err.println("Error closing WebSocket session: " + e.getMessage());
            }
        }
    }

    public void sendMessage(String message) {
        if (running && session.isOpen()) {
            messageQueue.offer(message);
        }
    }

    public void shutdown() {
        running = false;
        writerThread.interrupt();
    }

}
