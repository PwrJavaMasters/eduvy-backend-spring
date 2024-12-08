package com.eduvy.chat.controller;

import com.eduvy.chat.config.security.JwtAuthFilter;
import com.eduvy.chat.model.ChatMessage;
import com.eduvy.chat.model.ChatNotification;
import com.eduvy.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @GetMapping("/chat/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> getChatMessage(@PathVariable("senderId") String senderId,
                                                            @PathVariable("recipientId") String recipientId) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        logger.info("Received ChatMessage: Sender={}, Recipient={}, Message={}",
                chatMessage.getSenderId(),
                chatMessage.getRecipientId(),
                chatMessage.getMessage());

        ChatMessage savedMsg = chatMessageService.save(chatMessage);
        logger.info("Saved ChatMessage: ChatId={}, Sender={}, Recipient={}, Message={}",
                savedMsg.getChatId(),
                savedMsg.getSenderId(),
                savedMsg.getRecipientId(),
                savedMsg.getMessage());

        ChatNotification notification = new ChatNotification(
                savedMsg.getId(),
                savedMsg.getSenderId(),
                savedMsg.getRecipientId(),
                savedMsg.getMessage()
        );
        logger.info("Prepared ChatNotification: Id={}, Sender={}, Recipient={}, Message={}",
                notification.getId(),
                notification.getSenderId(),
                notification.getRecipientId(),
                notification.getMessage());

        // Send the notification and log the event
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(), "/queue/messages", notification);

        logger.info("Sent ChatNotification to User: RecipientId={}, Destination={}",
                chatMessage.getRecipientId(), "/queue/messages");
    }
}