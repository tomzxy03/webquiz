package com.tomzxy.web_quiz.models.snapshot;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

import com.tomzxy.web_quiz.enums.Level;
import com.tomzxy.web_quiz.models.BaseEntity;
import com.tomzxy.web_quiz.models.Quiz.QuizUserResponse;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionSnapshot extends BaseEntity {
	@Column(name = "question_text", nullable = false, length = 1000)
	private String questionText;

	@Column(name = "question_type", length = 20)
	private String questionType;

	@Column(name = "question_points", nullable = false)
	private Integer questionPoints = 1;

    @ManyToMany(mappedBy = "questionSnapshots", fetch = FetchType.LAZY)
    private List<QuizUserResponse> quizUserResponses;

    @OneToMany(mappedBy = "questionSnapshot", cascade = CascadeType.ALL)    
    private Set<AnswersSnapshot> answerSnapshots;

	public Integer getQuestionPoints() {
		return this.questionPoints;
	}

	public void setQuestionPoints(Integer questionPoints) {
		this.questionPoints = questionPoints;
	}
}
