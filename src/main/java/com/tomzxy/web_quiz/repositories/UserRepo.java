package com.tomzxy.web_quiz.repositories;


import com.tomzxy.web_quiz.models.User;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @NonNull
    Page<User> findAll(@NonNull Pageable pageable);

    @Query("select u from User u where u.is_active = :is_active")
    Page<User> findAllByActive(@Param("is_active") boolean is_active, Pageable pageable);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<User> findByEmail(String email);

}
