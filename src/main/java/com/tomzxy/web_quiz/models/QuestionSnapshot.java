package com.tomzxy.web_quiz.models;

import com.tomzxy.web_quiz.enums.Level;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionSnapshot {
	@Column(name = "question_text", nullable = false, length = 1000)
	private String questionText;

	@Column(name = "question_type", length = 20)
	private String questionType;

	@Column(name = "question_points", nullable = false)
	private Integer questionPoints = 1;

	public Integer getQuestionPoints() {
		return this.questionPoints;
	}

	public void setQuestionPoints(Integer questionPoints) {
		this.questionPoints = questionPoints;
	}
} 