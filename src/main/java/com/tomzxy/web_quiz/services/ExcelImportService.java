package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.responses.question.QuestionFileResDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ExcelImportService {
    List<QuestionFileResDTO> importQuestionsFromExcel(MultipartFile file);
}
