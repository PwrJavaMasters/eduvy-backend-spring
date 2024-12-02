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
    private final UserRepository repository;

    public void saveUser(User user)
    {
        try
        {
            user.setStatus(UserStatus.ONLINE);
            repository.save(user);
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }

    }

    public void disconnectUser(User user)
    {
        User onlineUser = repository.findById(user.getNickName()).orElse(null);
        if(onlineUser != null)
        {
            user.setStatus(UserStatus.OFFLINE);
            repository.save(user);
        }
    }

    public List<User> getConnectedUser()
    {
        return repository.findAllByStatus(UserStatus.ONLINE);
    }
}
