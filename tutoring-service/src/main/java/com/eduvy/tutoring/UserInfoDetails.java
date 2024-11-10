package com.eduvy.tutoring;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class UserInfoDetails implements UserDetails {

    private String email;
    private String nickname;
    private String password;
    private List<GrantedAuthority> authorities;



    public UserInfoDetails(String email, String nickname, String password, List<GrantedAuthority> authorities) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.authorities = Collections.unmodifiableList(authorities);
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}