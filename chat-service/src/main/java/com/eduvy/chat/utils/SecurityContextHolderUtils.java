package com.eduvy.chat.utils;

import com.eduvy.user.config.security.UserInfoDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextHolderUtils {

    public static String getCurrentUserAuth0UserIdFromContext(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserInfoDetails userDetails = (UserInfoDetails) authentication.getPrincipal();

        return userDetails.getAuth0UserId();
    }

    public static String getCurrentUsernameFromContext(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserInfoDetails userDetails = (UserInfoDetails) authentication.getPrincipal();

        return userDetails.getNickname();
    }

    public static String getCurrentUserMailFromContext(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserInfoDetails userDetails = (UserInfoDetails) authentication.getPrincipal();

        return userDetails.getEmail();
    }
}
