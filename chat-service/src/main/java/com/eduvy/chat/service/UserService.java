package com.eduvy.chat.service;

import com.eduvy.chat.model.User;
import com.eduvy.chat.repository.UserRepository;
import com.eduvy.chat.model.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    public void saveUser(User user) {
        try {
            User existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser != null) {
                existingUser.setStatus(UserStatus.ONLINE);
                userRepository.save(existingUser);
            } else {
                user.setStatus(UserStatus.ONLINE);
                userRepository.save(user);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void disconnectUser(User user) {
        User onlineUser = userRepository.findByEmail(user.getEmail());
        if (onlineUser != null) {
            onlineUser.setStatus(UserStatus.OFFLINE);
            userRepository.save(onlineUser);
        }
    }

    public List<User> getConnectedUser(String email) {
        return userRepository.findAllByStatus(UserStatus.ONLINE)
                .stream()
                .filter(u -> !email.equals(u.getEmail()))
                .toList();
    }

}
