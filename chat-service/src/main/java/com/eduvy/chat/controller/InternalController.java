package com.eduvy.chat.controller;

import com.eduvy.chat.model.User;
import com.eduvy.chat.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@AllArgsConstructor
public class InternalController {

    UserService userService;

    @PostMapping("/add-user")
    public ResponseEntity<Void> editUserUpdate(@RequestBody User user) {
        boolean isSaved = userService.saveUser(user);
        if (!isSaved) return ResponseEntity.status(422).build();
        return ResponseEntity.ok().build();
    }
}
