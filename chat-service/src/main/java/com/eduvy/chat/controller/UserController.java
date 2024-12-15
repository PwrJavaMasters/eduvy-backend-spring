package com.eduvy.chat.controller;

import com.eduvy.chat.model.User;
import com.eduvy.chat.service.ChatRoomService;
import com.eduvy.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class UserController {

    private final ChatRoomService chatRoomService;
    private final UserService userService;

    @MessageMapping("/user.addUser")
    @SendTo("/user/topic/public") //todo
    public User addUser(@Payload User user) {
        userService.connectUser(user);
        return user;
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/topic/public") //todo chat (problem)
    public User disconnectUser(@Payload User user) {
        userService.disconnectUser(user);
        return user;
    }

    @GetMapping("/getUsers")
    public ResponseEntity<List<User>> getAllUser(@RequestParam("email") String email) {
        System.out.println(email);
        return new ResponseEntity<>(userService.getConnectedUser(email), HttpStatus.OK);
    }

    @GetMapping("/with-chat-rooms") //todo chat
    public ResponseEntity<List<User>> getUsersWithChatRooms(@RequestParam("email") String email) {
        List<User> usersWithChatRooms = chatRoomService.getUsersWithChatRooms(email);
        return ResponseEntity.ok(usersWithChatRooms);
    }

}
