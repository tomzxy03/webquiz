package com.tomzxy.web_quiz.configs.init;


import com.tomzxy.web_quiz.containts.PredefinedRole;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.models.Permission;
import com.tomzxy.web_quiz.models.Role;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.models.rolepermission.RolePermissionObject;
import com.tomzxy.web_quiz.repositories.PermissionRepo;
import com.tomzxy.web_quiz.repositories.RolePermissionObjectRepo;
import com.tomzxy.web_quiz.repositories.RoleRepo;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.services.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Order(2)
public class RolePermissionInit {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PermissionRepo permissionRepo;
    private final RolePermissionObjectRepo rolePermissionObjectRepo;
    private final RoleService roleService;
    @NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "1111";

    @NonFinal
    static final String BASIC_USER_NAME = "tomzxy03";

    @NonFinal
    static final String BASIC_PASSWORD = "1111";

    @NonFinal
    static final String[] objectModelAdmin = {
            "subject",
            "chapter",
            "user",
            "notification",
            "group",
            "quiz",
            "question",
            "answer",
            "quiz_result",
            "answer_user",
            "role",
            "permission"
    };
    @NonFinal
    static final Map<String, List<String>> objectModelUser = Map.of(
            "subject", List.of("VIEW"),
            "chapter", List.of("VIEW"),
            "user", List.of("VIEW", "UPDATE"),
            "notifications", List.of("VIEW"),
            "group", List.of("VIEW", "CREATE"), // join group ( add user to group)
            "quiz", List.of("VIEW"),
            "quiz_result", List.of("VIEW", "CREATE"),
            "question", List.of("VIEW"),
            "answer", List.of("VIEW"),
            "answer_user", List.of("VIEW")
    );
    @NonFinal
    static final Map<String, List<String>> objectModelGroup = Map.of(

            "user", List.of("CREATE"),
            "notifications", List.of("CREATE"),
            "group", List.of("CREATE"), // create group
            "quiz", List.of("CREATE"),
            "quiz_result", List.of( "DELETE"),
            "answer_user", List.of("VIEW")
    );
    @Bean
    public CommandLineRunner initDataBase() {
        return args -> {
            if(userRepo.findByUserName(ADMIN_USER_NAME).isEmpty()){

                // initialization Role Admin and Basic
                Role adminRole= new Role();
                Role basicRole = new Role();
                Role groupRole = new Role();
                if(roleRepo.findByName(PredefinedRole.ADMIN_ROLE).isEmpty()){

                    adminRole.setName(PredefinedRole.ADMIN_ROLE);
                    roleRepo.save(adminRole);
                    //initialization permission of admin
                    var role = roleRepo.findByName(adminRole.getName()).orElseThrow(()->new NotFoundException("Role not exists"));
                    List<Permission> permissions = permissionRepo.findAll();
                    for(String object: objectModelAdmin){
                        for (Permission permission: permissions){
                            RolePermissionObject rolePermission = new RolePermissionObject(role,permission,object);
                            rolePermissionObjectRepo.save(rolePermission);
                        }
                    }
                }
                if(roleRepo.findByName(PredefinedRole.USER_ROLE).isEmpty()){
                    basicRole.setName(PredefinedRole.USER_ROLE);
                    roleRepo.save(basicRole);
                    //initialization permission of user
//                    var role = roleRepository.findByName(basicRole.getName()).orElseThrow(()->new ResourceNotFoundException("Role not exists"));
                    roleService.addRolePermissionObject(basicRole, objectModelUser);

                }
                if(roleRepo.findByName(PredefinedRole.CREATOR_ROLE).isEmpty()){
                    groupRole.setName(PredefinedRole.CREATOR_ROLE);
                    roleRepo.save(groupRole);
                    //initialization permission of group creator
//                    var role = roleRepository.findByName(groupRole.getName()).orElseThrow(()->new ResourceNotFoundException("Role not exists"));
                    roleService.addRolePermissionObject(groupRole, objectModelGroup);

                }
                User userAmin = new User();
                userAmin.setUserName(ADMIN_USER_NAME);
                userAmin.setEmail("admin@gmail.com");
                userAmin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
                userAmin.setRoles(new HashSet<>(Set.of(adminRole)));
                userRepo.save(userAmin);

                User userBasic = new User();
                userBasic.setUserName(BASIC_USER_NAME);
                userBasic.setEmail("tomzxy03@gmail.com");
                userBasic.setPassword(passwordEncoder.encode(BASIC_PASSWORD));
                userBasic.setRoles(new HashSet<>(Set.of(basicRole)));
                userRepo.save(userBasic);


                System.out.println("Roles and user have been initialized.");

            }
        };
    }

}
