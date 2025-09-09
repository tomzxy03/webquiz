package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.snapshot.AnswersSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswersSnapshotRepo extends JpaRepository<AnswersSnapshot, Long> {
    
    @Query("SELECT as FROM AnswersSnapshot as WHERE as.questionSnapshot.id = :questionSnapshotId")
    List<AnswersSnapshot> findByQuestionSnapshotId(@Param("questionSnapshotId") Long questionSnapshotId);
    
    @Query("SELECT as FROM AnswersSnapshot as WHERE as.correctAnswerText = :correctAnswerText")
    Optional<AnswersSnapshot> findByCorrectAnswerText(@Param("correctAnswerText") String correctAnswerText);
    
    @Query("SELECT as FROM AnswersSnapshot as WHERE as.isActive = true")
    List<AnswersSnapshot> findActiveAnswersSnapshots();
    
    @Query("SELECT as FROM AnswersSnapshot as WHERE as.correctAnswerText LIKE %:text%")
    List<AnswersSnapshot> findByCorrectAnswerTextContaining(@Param("text") String text);
}
