package com.tomzxy.web_quiz.repositories;


import com.tomzxy.web_quiz.models.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepo extends JpaRepository<Permission, String> {
}
