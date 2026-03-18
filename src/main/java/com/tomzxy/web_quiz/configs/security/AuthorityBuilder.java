package com.tomzxy.web_quiz.configs.security;

import com.tomzxy.web_quiz.models.Role;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.models.rolepermission.RolePermissionObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

public class AuthorityBuilder {

    public static Set<GrantedAuthority> build(User user) {

        Set<GrantedAuthority> authorities = new HashSet<>();

        for (Role role : user.getRoles()) {

            authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + role.getName())
            );

            for (RolePermissionObject rpo : role.getRolePermissionObjects()) {

                String objectType = rpo.getRolePermissionId().getObjectType();
                String permission = rpo.getPermission().getPermissionName();

                authorities.add(
                        new SimpleGrantedAuthority(objectType + "_" + permission)
                );
            }
        }

        return authorities;
    }
}
