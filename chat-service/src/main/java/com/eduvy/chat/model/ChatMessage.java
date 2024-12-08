package com.eduvy.chat.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String chatId;
    private String senderId;
    private String recipientId;
    private String message;
    private Date timeStamp;
}
