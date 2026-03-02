package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.snapshot.AnswerSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswersSnapshotRepo extends JpaRepository<AnswerSnapshot, Long> {

    // Tìm tất cả answer theo questionSnapshot
    List<AnswerSnapshot> findByQuestionSnapshotId(Long questionSnapshotId);

    // Tìm theo nội dung answer
    Optional<AnswerSnapshot> findByContent(String content);

    // Tìm tất cả answer active
    List<AnswerSnapshot> findByIsActiveTrue();

    // Tìm chứa text trong content
    List<AnswerSnapshot> findByContentContaining(String text);
}