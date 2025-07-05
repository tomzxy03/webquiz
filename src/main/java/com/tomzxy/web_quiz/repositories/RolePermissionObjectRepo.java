package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.Role;
import com.tomzxy.web_quiz.models.rolepermission.RolePermissionId;
import com.tomzxy.web_quiz.models.rolepermission.RolePermissionObject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolePermissionObjectRepo extends JpaRepository<RolePermissionObject, RolePermissionId> {

}
