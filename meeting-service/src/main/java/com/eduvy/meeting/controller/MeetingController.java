package com.eduvy.meeting.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeetingController {
    @GetMapping("/meeting/test")
    public String test() {
        return "Meeting Service is up";
    }
}
