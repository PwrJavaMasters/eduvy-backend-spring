package com.eduvy.chat.repository;

import com.eduvy.chat.model.ChatRoom;
import com.eduvy.chat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);

//    ChatRoom findBySenderIdAndRecipientId(String senderId, String recipientId);

    @Query("SELECT DISTINCT u FROM User u " +
            "WHERE u.email IN (" +
            "SELECT cr.senderId FROM ChatRoom cr WHERE cr.recipientId = :senderId " +
            "UNION " +
            "SELECT cr.recipientId FROM ChatRoom cr WHERE cr.senderId = :senderId)")
    List<User> findUsersWithChatRooms(@Param("senderId") String senderId);
}


