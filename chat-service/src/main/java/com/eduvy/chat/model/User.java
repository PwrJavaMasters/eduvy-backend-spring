package com.eduvy.chat.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity // Marking the class as a JPA entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickName;

    private String email;

    @Enumerated(EnumType.STRING) // Mapping UserStatus as an enum in the database
    private UserStatus status;
}
