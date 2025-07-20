package com.tomzxy.web_quiz.repositories;


import com.tomzxy.web_quiz.models.Subject;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;   
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepo extends JpaRepository<Subject, Long> {
    @NonNull
    Page<Subject> findAll(@NonNull Pageable pageable);

    @Query("select u from Subject u where u.is_active = :is_active")
    Page<Subject> findAllByActive(@Param("is_active") boolean is_active, Pageable pageable);

    @Query("select u from Subject u where u.is_active = :is_active")
    Optional<List<Subject>> findAllByActive(@Param("is_active") boolean is_active);
    
    Optional<Subject> findBySubjectName(String subjectName);


}
