package com.eduvy.chat.controller;

import com.eduvy.chat.model.User;
import com.eduvy.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @MessageMapping("/user.addUser")
    @SendTo("/user/topic")
    public User addUser(@Payload User user)
    {
        userService.saveUser(user);
        return user;
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/topic")
    public User disconnectUser(@Payload User user)
    {
        userService.disconnectUser(user);
        return user;
    }

    @GetMapping("/getUsers")
    public ResponseEntity<List<User>> getAllUser()
    {
        return new ResponseEntity<>(userService.getConnectedUser(), HttpStatus.OK);

    }

}
