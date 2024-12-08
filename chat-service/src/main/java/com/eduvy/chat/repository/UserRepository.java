package com.eduvy.chat.repository;

import com.eduvy.chat.model.User;
import com.eduvy.chat.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User,String> {
    List<User> findAllByStatus(UserStatus userStatus);

    User findByEmail(String email);
}
