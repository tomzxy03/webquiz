package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.Host.QuestionBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionBankRepo extends JpaRepository<QuestionBank, Long> {
}
