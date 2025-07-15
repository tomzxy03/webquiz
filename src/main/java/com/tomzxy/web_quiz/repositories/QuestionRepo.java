package com.tomzxy.web_quiz.repositories;


import com.tomzxy.web_quiz.enums.Level;
import com.tomzxy.web_quiz.models.Question;
import com.tomzxy.web_quiz.models.Question;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepo extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {
    @NonNull
    Page<Question> findAll(@NonNull Pageable pageable);

    @Query("select u from Question u where u.is_active = :is_active")
    Page<Question> findAllByActive(@Param("is_active") boolean is_active, Pageable pageable);

    Optional<Question> findByQuestionName(String questionName);

    Optional<Question> findByLevel(Level level);

    boolean existsByQuestionName(String questionName);

    @Query("SELECT q.questionName FROM Question q WHERE q.questionName IN :names")
    List<String>  findAllExistQuestion(@Param("names") List<String> names);

}
