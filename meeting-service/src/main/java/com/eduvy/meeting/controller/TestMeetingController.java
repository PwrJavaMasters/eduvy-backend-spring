package com.eduvy.meeting.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/meeting")
@RestController
public class TestMeetingController {
    @GetMapping("/test")
    public String test() {
        return "Meeting Service is up";
    }
}
