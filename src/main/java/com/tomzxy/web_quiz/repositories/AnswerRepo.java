package com.tomzxy.web_quiz.repositories;


import com.tomzxy.web_quiz.enums.Level;
import com.tomzxy.web_quiz.models.Answer;
import com.tomzxy.web_quiz.models.Answer;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepo extends JpaRepository<Answer, Long>, JpaSpecificationExecutor<Answer> {
    @NonNull
    Page<Answer> findAll(@NonNull Pageable pageable);

    @Query("select u from Answer u where u.is_active = :is_active")
    Page<Answer> findAllByActive(@Param("is_active") boolean is_active, Pageable pageable);
    
    Optional<Answer> findByAnswerName(String answerName);


}
