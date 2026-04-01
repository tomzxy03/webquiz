package com.tomzxy.web_quiz.dto.responses.Quiz;

import java.time.LocalDateTime;
import java.util.List;

import com.tomzxy.web_quiz.dto.responses.UserAnswerResDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptDetailResDTO {
    private AttemptResDTO attemptInfo;
    private List<UserAnswerResDTO> answers;
}