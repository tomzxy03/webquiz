package com.tomzxy.web_quiz.utils;

import com.sun.security.auth.UserPrincipal;
import com.tomzxy.web_quiz.configs.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class SecurityUtils {

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
        }

    public static String getCurrentUsername(){
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getName();
    }
    public static UserPrincipal getCurrentUser() {

        Authentication authentication = getAuthentication();

        if (authentication == null) {
            return null;
        }

        return (UserPrincipal) authentication.getPrincipal();
    }
    public static Long getCurrentUserId() {

        Authentication authentication = getAuthentication();

        if (authentication == null) {
            return null;
        }
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return user.id();
    }

}
