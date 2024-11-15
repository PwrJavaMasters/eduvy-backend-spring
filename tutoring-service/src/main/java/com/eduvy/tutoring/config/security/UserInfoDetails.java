package com.eduvy.tutoring.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class UserInfoDetails implements UserDetails {

    private String auth0UserId;
    private String email;
    private String nickname;
    private String password;
    private List<GrantedAuthority> authorities;

    public UserInfoDetails(String auth0UserId, String email, String nickname, String password, List<GrantedAuthority> authorities) {
        this.auth0UserId = auth0UserId;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.authorities = Collections.unmodifiableList(authorities);
    }

    public String getAuth0UserId() { return auth0UserId; }

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