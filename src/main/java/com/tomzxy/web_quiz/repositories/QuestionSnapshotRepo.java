package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.snapshot.QuestionSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionSnapshotRepo extends JpaRepository<QuestionSnapshot, Long> {
    
    @Query("SELECT qs FROM QuestionSnapshot qs WHERE qs.content = :content")
    Optional<QuestionSnapshot> findByContent(@Param("content") String content);
    
    @Query("SELECT qs FROM QuestionSnapshot qs WHERE qs.type = :questionType")
    List<QuestionSnapshot> findByQuestionType(@Param("questionType") String questionType);
    
    @Query("SELECT qs FROM QuestionSnapshot qs WHERE qs.points = :points")
    List<QuestionSnapshot> findByQuestionPoints(@Param("points") Integer points);
    
    @Query("SELECT qs FROM QuestionSnapshot qs WHERE qs.isActive = true")
    List<QuestionSnapshot> findActiveQuestionSnapshots();
    
    @Query("SELECT qs FROM QuestionSnapshot qs WHERE qs.content LIKE %:text%")
    List<QuestionSnapshot> findByContentContaining(@Param("text") String text);
}
