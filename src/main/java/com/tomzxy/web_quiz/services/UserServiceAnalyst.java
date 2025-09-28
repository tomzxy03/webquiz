package com.tomzxy.web_quiz.services;


import com.tomzxy.web_quiz.models.Quiz.QuizInstance;
import com.tomzxy.web_quiz.repositories.QuizInstanceRepo;


public interface UserServiceAnalyst {



    public int getTotalQuizzesTaken(Long userId);

    public double getAverageScore(Long userId);

    public int getTotalCorrectAnswers(Long userId);

    public int getTotalWrongAnswers();

    public int getTotalSkippedQuestions() ;

    public long getTotalTimeSpent();

    public QuizInstance getBestQuizResult(Long userId);

    public QuizInstance getLatestQuizResult();

    public boolean isActiveLearner();

    public String getLearningLevel();

    public boolean isImproving();
    public long getDaysSinceLastQuiz();

    public boolean isNewUser();
    public boolean isExperiencedUser();
}
