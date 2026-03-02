package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.Quiz.QuizQuestionId;
import com.tomzxy.web_quiz.models.Quiz.QuizQuestionLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QuizQuestionLinkRepo extends JpaRepository<QuizQuestionLink, QuizQuestionId>, JpaSpecificationExecutor<QuizQuestionLink>  {

}
