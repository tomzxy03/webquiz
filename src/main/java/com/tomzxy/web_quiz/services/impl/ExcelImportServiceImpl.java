package com.tomzxy.web_quiz.services.impl;

import com.tomzxy.web_quiz.dto.responses.answer.AnswerFileResDTO;
import com.tomzxy.web_quiz.dto.responses.question.QuestionFileResDTO;
import com.tomzxy.web_quiz.enums.AnswerType;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.enums.ContentType;
import com.tomzxy.web_quiz.exception.ApiException;
import com.tomzxy.web_quiz.services.ExcelImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelImportServiceImpl implements ExcelImportService {

    private static final Map<String, String> HEADER_MAP = Map.ofEntries(
            Map.entry("câu hỏi", "question"),
            Map.entry("question", "question"),

            Map.entry("đáp án a", "a"),
            Map.entry("câu trả lời a", "a"),
            Map.entry("a", "a"),

            Map.entry("đáp án b", "b"),
            Map.entry("câu trả lời b", "b"),
            Map.entry("b", "b"),

            Map.entry("đáp án c", "c"),
            Map.entry("câu trả lời c", "c"),
            Map.entry("c", "c"),

            Map.entry("đáp án d", "d"),
            Map.entry("câu trả lời d", "d"),
            Map.entry("d", "d"),

            Map.entry("đáp án", "correct"),
            Map.entry("đáp án đúng", "correct"),

            Map.entry("loại", "type"),
            Map.entry("loại câu hỏi", "type"),
            Map.entry("type", "type"));

    @Override
    public List<QuestionFileResDTO> importQuestionsFromExcel(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApiException(AppCode.NOT_AVAILABLE, "File is empty");
        }

        List<QuestionFileResDTO> responseDtos = new ArrayList<>();

        try (InputStream is = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (!rows.hasNext()) {
                throw new ApiException(AppCode.NOT_AVAILABLE, "Excel sheet is empty");
            }

            // Validate header
            Row headerRow = rows.next();
            Map<String, Integer> fieldIndex = validateHeader(headerRow);

            int rowNumber = 1;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                rowNumber++;

                // Skip completely empty rows
                if (isRowEmpty(currentRow)) {
                    continue;
                }

                QuestionFileResDTO resDTO = parseRow(currentRow, rowNumber, fieldIndex);

                // For preview, we directly use the mapped ResDTO
                responseDtos.add(resDTO);
            }

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse Excel file", e);
            throw new ApiException(AppCode.NOT_AVAILABLE, "Failed to parse Excel file: " + e.getMessage());
        }

        return responseDtos;
    }

    private Map<String, Integer> validateHeader(Row headerRow) {
        Map<String, Integer> fieldIndex = new HashMap<>();

        for (Cell cell : headerRow) {
            String raw = getCellValue(cell).trim().toLowerCase();

            if (!HEADER_MAP.containsKey(raw)) {
                throw new ApiException(AppCode.NOT_AVAILABLE, "Unknown column: " + raw);
            }

            String field = HEADER_MAP.get(raw);
            fieldIndex.put(field, cell.getColumnIndex());
        }

        // required fields
        List<String> required = List.of("question", "a", "b", "correct");

        for (String r : required) {
            if (!fieldIndex.containsKey(r)) {
                throw new ApiException(AppCode.NOT_AVAILABLE, "Missing required column: " + r);
            }
        }

        return fieldIndex;
    }

    private QuestionFileResDTO parseRow(Row row, int rowNumber, Map<String, Integer> fieldIndex) {
        QuestionFileResDTO dto = new QuestionFileResDTO();

        dto.setQuestionName(getCellValue(row.getCell(getRequiredColumn(fieldIndex, "question", rowNumber))).trim());

        // As requested by user, the content type is TEXT and AnswerType comes from the
        // 'Type' column
        dto.setType(ContentType.TEXT);

        String typeCol = getCellValue(row.getCell(getRequiredColumn(fieldIndex, "type", rowNumber))).trim()
                .toUpperCase();
        dto.setAnswerType(parseAnswerType(typeCol, rowNumber));

        String correctAnswerCol = getCellValue(row.getCell(getRequiredColumn(fieldIndex, "correct", rowNumber))).trim()
                .toUpperCase();
        List<String> correctAnswers = Arrays.asList(correctAnswerCol.split("[,\\s]+")); // handles "A, B" or "A B" or
                                                                                        // just "A"

        List<AnswerFileResDTO> answers = new ArrayList<>();

        // Parse A, B, C, D
        for (int i = 0; i < 4; i++) {
            String key = String.valueOf((char) ('a' + i));

            Integer colIndex = fieldIndex.get(key);
            if (colIndex == null)
                continue; // optional

            String ansText = getCellValue(row.getCell(colIndex)).trim();
            if (ansText.isEmpty())
                continue;

            AnswerFileResDTO ansDTO = new AnswerFileResDTO();
            ansDTO.setAnswerText(ansText);
            ansDTO.setAnswerType(ContentType.TEXT);
            ansDTO.setOrderIndex(i + 1); // 1-based index based on A, B, C, D

            String optionLetter = key.toUpperCase();
            ansDTO.setIsCorrect(correctAnswers.contains(optionLetter));

            answers.add(ansDTO);
        }

        if (answers.isEmpty()) {
            throw new ApiException(AppCode.NOT_AVAILABLE, "No answers provided at row " + rowNumber);
        }

        dto.setAnswers(answers);

        // Sanity check for multiple choice vs single choice
        long correctCount = answers.stream().filter(ans -> Boolean.TRUE.equals(ans.getIsCorrect())).count();
        if (dto.getAnswerType() == AnswerType.SINGLE_CHOICE && correctCount > 1) {
            throw new ApiException(AppCode.NOT_AVAILABLE,
                    "SINGLE_CHOICE question at row " + rowNumber + " has multiple correct answers.");
        }

        return dto;
    }

    private Integer getRequiredColumn(Map<String, Integer> fieldIndex, String key, int rowNumber) {
        Integer idx = fieldIndex.get(key);
        if (idx == null) {
            throw new ApiException(AppCode.NOT_AVAILABLE,
                    "Missing column '" + key + "' at row " + rowNumber);
        }
        return idx;
    }

    private AnswerType parseAnswerType(String raw, int rowNumber) {
        String normalized = raw.trim().toUpperCase().replace(" ", "_");

        return switch (normalized) {
            case "SINGLE", "SINGLE_CHOICE" -> AnswerType.SINGLE_CHOICE;
            case "single", "single_choice" -> AnswerType.SINGLE_CHOICE;
            case "MULTIPLE", "MULTIPLE_CHOICE" -> AnswerType.MULTIPLE_CHOICE;
            case "multiple", "multiple_choice" -> AnswerType.MULTIPLE_CHOICE;
            default -> throw new ApiException(AppCode.NOT_AVAILABLE,
                    "Invalid question type at row " + rowNumber + ": " + raw);
        };
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toString();
                } else {
                    double val = cell.getNumericCellValue();
                    if (val == Math.floor(val)) {
                        yield String.valueOf((long) val);
                    }
                    yield String.valueOf(val);
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValue(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
