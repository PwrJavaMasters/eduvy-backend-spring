package com.eduvy.user.utils;

import com.eduvy.user.config.security.UserInfoDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextHolderUtils {

    public static String getCurrentUserMailFromContext(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserInfoDetails userDetails = (UserInfoDetails) authentication.getPrincipal();

        return userDetails.getEmail();
    }
}
