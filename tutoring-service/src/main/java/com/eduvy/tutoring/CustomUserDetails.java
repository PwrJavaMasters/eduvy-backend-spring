package com.eduvy.tutoring;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails extends User{

    private String nickname;
    private String email;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
                             String nickname, String email) {
        super(username, password, Collections.singleton((GrantedAuthority) authorities));
        this.nickname = nickname;
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }
}
