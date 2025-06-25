package com.nhtr.orderservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/ping")
    public Map<String, String> createOrder() {
        Map<String, String> res = new HashMap<>();
        res.put("response", "pong");
        return res;
    }
}
