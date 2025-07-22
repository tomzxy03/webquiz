package com.tomzxy.web_quiz.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "quiz_attempt")
public class QuizAttempt extends BaseEntity {

    @Column(name = "question_text")
    private String questionText;    

    @Column(name = "selected_answer")
    private String selectedAnswer;

    @Column(name = "is_correct")
    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "quiz_result_id")
    private QuizResult quizResult;
    
}
