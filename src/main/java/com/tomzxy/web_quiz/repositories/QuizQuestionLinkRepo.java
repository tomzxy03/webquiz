package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.Quiz.QuizQuestionId;
import com.tomzxy.web_quiz.models.Quiz.QuizQuestionLink;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface QuizQuestionLinkRepo
        extends JpaRepository<QuizQuestionLink, QuizQuestionId>, JpaSpecificationExecutor<QuizQuestionLink> {

    void deleteAllByQuizId(Long id);

    void deleteByQuizId(Long quizId);

    List<QuizQuestionLink> findByQuizId(Long quizId);

    @Query("""
                SELECT DISTINCT ql
                FROM QuizQuestionLink ql
                JOIN FETCH ql.question q
                LEFT JOIN FETCH q.answers
                WHERE ql.quiz.id = :quizId
            """)
    List<QuizQuestionLink> findFullByQuizId(Long quizId);
}
