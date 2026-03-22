package com.tomzxy.web_quiz.configs.init;

import com.tomzxy.web_quiz.models.Permission;
import com.tomzxy.web_quiz.models.Role;
import com.tomzxy.web_quiz.models.Host.QuestionBank;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.models.rolepermission.RolePermissionObject;
import com.tomzxy.web_quiz.containts.PredefinedRole;

import com.tomzxy.web_quiz.repositories.PermissionRepo;
import com.tomzxy.web_quiz.repositories.QuestionBankRepo;
import com.tomzxy.web_quiz.repositories.RolePermissionObjectRepo;
import com.tomzxy.web_quiz.repositories.RoleRepo;
import com.tomzxy.web_quiz.repositories.UserRepo;
import com.tomzxy.web_quiz.services.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class RolePermissionInit implements CommandLineRunner {

    private final PermissionRepo permissionRepo;
    private final RoleRepo roleRepo;
    private final RolePermissionObjectRepo rolePermissionObjectRepo;
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final QuestionBankRepo questionBankRepo;

    private static final String ADMIN = PredefinedRole.ADMIN_ROLE;
    private static final String USER = PredefinedRole.USER_ROLE;

    @Override
    public void run(String... args) {
        log.info("========= START DATABASE INITIALIZATION =========");

        seedPermissions();
        seedRoles();
        seedUsers();

        log.info("========= DATABASE INITIALIZED SUCCESS =========");
    }

    private void seedPermissions() {

        Map<String, String> permissions = Map.of(
                "VIEW", "xem dữ liệu",
                "CREATE", "thêm dữ liệu",
                "UPDATE", "sửa dữ liệu",
                "DELETE", "xóa dữ liệu");

        permissions.forEach((key, value) -> {
            permissionRepo.findById(key)
                    .orElseGet(() -> permissionRepo.save(new Permission(key, value)));
        });

        log.info("Permissions seeded.");
    }

    private void seedRoles() {

        Role adminRole = getOrCreateRole(ADMIN);
        Role userRole = getOrCreateRole(USER);

        if (rolePermissionObjectRepo.count() == 0) {

            // Admin full quyền
            List<Permission> permissions = permissionRepo.findAll();
            String[] adminObjects = {
                    "subject", "chapter", "user", "notification",
                    "group", "quiz", "question", "answer",
                    "quiz_result", "answer_user", "role", "permission", "question_bank", "question_folder"
            };

            for (String object : adminObjects) {
                for (Permission permission : permissions) {
                    rolePermissionObjectRepo.save(
                            new RolePermissionObject(adminRole, permission, object));
                }
            }

            // User role
            roleService.addRolePermissionObject(userRole, Map.of(
                    "subject", List.of("VIEW"),
                    "chapter", List.of("VIEW"),
                    "user", List.of("VIEW", "UPDATE"),
                    "group", List.of("VIEW", "CREATE"),
                    "quiz", List.of("VIEW"),
                    "quiz_result", List.of("VIEW"),
                    "notification", List.of("VIEW")));
        }

        log.info("Roles & permissions seeded.");
    }

    private Role getOrCreateRole(String roleName) {
        return roleRepo.findByName(roleName)
                .orElseGet(() -> roleRepo.save(new Role(roleName)));
    }

    // ================= USER =================

    private void seedUsers() {

        if (userRepo.findByUserName("admin").isEmpty()) {

            Role adminRole = roleRepo.findByName(ADMIN)
                    .orElseThrow();

            User admin = new User();
            admin.setUserName("admin");
            admin.setEmail("admin@gmail.com");
            admin.setEmailVerified(true);
            admin.setPassword(passwordEncoder.encode("datvip2003"));
            admin.setRoles(Set.of(adminRole));

            admin = userRepo.save(admin);

            QuestionBank bank = new QuestionBank();
            bank.setOwner(admin);
            questionBankRepo.save(bank);

            log.info("Admin user created.");
        }

        if (userRepo.findByUserName("tomzxy03").isEmpty()) {

            Role userRole = roleRepo.findByName(USER)
                    .orElseThrow();

            User user = new User();
            user.setUserName("tomzxy03");
            user.setEmail("tomzxy03@gmail.com");
            user.setPassword(passwordEncoder.encode("datvip2003"));
            user.setRoles(Set.of(userRole));

            userRepo.save(user);

            log.info("Basic user created.");
        }
    }
}