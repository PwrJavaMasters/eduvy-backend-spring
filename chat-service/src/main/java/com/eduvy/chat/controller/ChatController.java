package com.eduvy.chat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    @GetMapping("/chat/test")
    public String test() {
        return "Chat Service is up";
    }
}
