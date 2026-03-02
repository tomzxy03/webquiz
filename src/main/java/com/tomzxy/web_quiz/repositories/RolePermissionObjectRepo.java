package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.rolepermission.RolePermissionId;
import com.tomzxy.web_quiz.models.rolepermission.RolePermissionObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionObjectRepo extends JpaRepository<RolePermissionObject, RolePermissionId> {

}
