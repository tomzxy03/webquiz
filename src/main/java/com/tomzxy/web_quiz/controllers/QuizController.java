package com.tomzxy.web_quiz.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomzxy.web_quiz.configs.IdentityResolver;
import com.tomzxy.web_quiz.configs.security.LobbySecurity;
import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.filter.QuizFilterReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizQuestionReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizDetailResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResDTO;
import com.tomzxy.web_quiz.dto.responses.QuizInstanceResDTO;
import com.tomzxy.web_quiz.enums.AppCode;
import com.tomzxy.web_quiz.models.IdentityContext;
import com.tomzxy.web_quiz.services.QuizInstanceService;
import com.tomzxy.web_quiz.services.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Id;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.Quiz.BASE)
@Tag(name = "Quizzes", description = "Quiz management APIs")
public class QuizController {

        private final ObjectMapper objectMapper;
        private final QuizService quizService;
        private final QuizInstanceService quizInstanceService;
        private final IdentityResolver identityResolver;
        private final LobbySecurity lobbySecurity;

        @GetMapping()
        @Operation(summary = "Get all quizzes", description = "Retrieve all quizzes with pagination")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Quizzes retrieved successfully")
        })
        public ResponseEntity<DataResDTO<PageResDTO<?>>> getAllQuizzes(
                        @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
                        @Parameter(description = "Page size (minimum 10)") @RequestParam(defaultValue = "10") @Min(10) int size) {
                log.info("Get all quizzes");
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(DataResDTO.ok(quizService.getAll(page, size)));
        }

        @GetMapping(ApiDefined.Quiz.FILTER)
        @Operation(summary = "Get all quizzes", description = "Retrieve all quizzes with pagination")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Quizzes retrieved successfully")
        })
        public ResponseEntity<DataResDTO<PageResDTO<?>>> getAllQuizzesWithFilter(
                        @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
                        @Parameter(description = "Page size (minimum 10)") @RequestParam(defaultValue = "10") @Min(10) int size,
                        @ModelAttribute @Valid QuizFilterReqDTO quizFilterReqDTO) {
                log.info("Get all quizzes with filter");
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(DataResDTO.ok(quizService.getAllWithFilter(quizFilterReqDTO, page, size)));
        }

        @GetMapping(ApiDefined.Quiz.ID)
        @Operation(summary = "Get quiz by ID", description = "Retrieve a quiz by its ID with user attempt state")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Quiz found successfully"),
                        @ApiResponse(responseCode = "404", description = "Quiz not found")
        })
        public ResponseEntity<DataResDTO<QuizDetailResDTO>> getQuiz(
                        @Parameter(description = "Quiz ID") @PathVariable Long quizId, HttpServletRequest httpServletRequest) throws JsonProcessingException {
                IdentityContext identity = identityResolver.resolve(httpServletRequest);
                log.info("Get quiz by {}", quizId);
                QuizDetailResDTO quiz = quizService.getQuizDetail(quizId, identity);
                // log all data quiz
                log.info("Quiz {} with attempt state {} and instance id {}", quiz, quiz.getAttemptState(), quiz.getInstanceId());
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(DataResDTO.ok(quiz));
        }

        // getLastestQuiz
        @GetMapping(ApiDefined.Quiz.LATEST)
        @Operation(summary = "Get quizzes lastest", description = "Retrieve a List quiz lastest")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Quiz found successfully"),
                @ApiResponse(responseCode = "404", description = "Quiz not found")
        })
        public ResponseEntity<DataResDTO<PageResDTO<?>>> getQuizLastest(
                @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
                @Parameter(description = "Page size (minimum 10)") @RequestParam(defaultValue = "10") @Min(10) int size) throws JsonProcessingException {
                log.info("Get quizzes lastest");
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(DataResDTO.ok(quizService.getLatestQuizzes(page,size)));
        }

        @PostMapping("/{quizId}/start")
        @Operation(summary = "Start quiz", description = "Start a new quiz attempt or resume existing")
        public ResponseEntity<DataResDTO<QuizInstanceResDTO>> startQuiz(
                        @Parameter(description = "Quiz ID") @PathVariable Long quizId, HttpServletRequest request) {
                IdentityContext identity = identityResolver.resolve(request);
                log.info("Starting quiz {} for user {}", quizId, identity.getUserId());

                if(!lobbySecurity.canAccessQuiz(quizId, identity)) {
                        log.warn("User {} attempted to access quiz {} without permission", identity.getUserId(), quizId);
                        return ResponseEntity
                                        .status(HttpStatus.FORBIDDEN)
                                        .body(DataResDTO.error(AppCode.FORBIDDEN, "Bạn không có quyền truy cập quiz này"));
                }
                QuizInstanceResDTO instance = quizInstanceService.createQuizInstance(quizId, identity);
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(DataResDTO.create(instance));
        }

        @GetMapping("/{quizId}/active-instance")
        @Operation(summary = "Get active instance", description = "Retrieve the current in-progress attempt for a quiz")
        @PreAuthorize("hasAuthority('quiz_VIEW')")
        public ResponseEntity<DataResDTO<QuizInstanceResDTO>> getActiveInstance(
                        @Parameter(description = "Quiz ID") @PathVariable Long quizId) {
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(DataResDTO.ok(quizService.getActiveInstance(quizId)));
        }

        @PostMapping()
        @Operation(summary = "Create quiz", description = "Create a new quiz")
        @PreAuthorize("hasAuthority('quiz_CREATE')")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Quiz created successfully", content = @Content(schema = @Schema(implementation = DataResDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<DataResDTO<QuizResDTO>> createQuiz(@Valid @RequestBody QuizReqDTO quizReqDTO) {
                log.info("Create quiz");
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(DataResDTO.create(quizService.create(quizReqDTO)));
        }

        @PostMapping(ApiDefined.Quiz.ADD_QUESTION)
        @Operation(summary = "Link questions to quiz", description = "Link question to quiz")
        @PreAuthorize("hasAuthority('quiz_CREATE') OR @lobbySecurity.isQuizHost(#quizId, authentication)")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Link questions to quiz successfully", content = @Content(schema = @Schema(implementation = DataResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<DataResDTO<?>> create_Questions(@Parameter(description = "Quiz ID") @PathVariable Long quizId , @Valid @RequestBody List<QuizQuestionReqDTO> quizReqDTO) {
            log.info("Link question to quiz");
            try{
                quizService.create_Questions(quizId,quizReqDTO);
            }catch (Exception e){
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(DataResDTO.error(AppCode.BAD_REQUEST, e.getMessage()));
            }
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DataResDTO.ok(null));
        }

        @PutMapping(ApiDefined.Quiz.UPDATE_QUESTION)
        @Operation(summary = "Update quiz questions", description = "Update quiz questions by quiz id")
        @PreAuthorize("hasAuthority('quiz_UPDATE') OR @lobbySecurity.isQuizHost(#quizId, authentication)")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update quiz questions successfully", content = @Content(schema = @Schema(implementation = DataResDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<DataResDTO<?>> update_Questions(@Parameter(description = "Quiz ID") @PathVariable Long quizId , @Valid @RequestBody List<QuizQuestionReqDTO> quizReqDTO) {
            log.info("Update quiz questions");
            try{
                quizService.update_Questions(quizId,quizReqDTO);
            }catch (Exception e){
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(DataResDTO.error(AppCode.BAD_REQUEST, e.getMessage()));
            }
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(DataResDTO.ok(null));
        }

        @PutMapping(ApiDefined.Quiz.ID)
        @Operation(summary = "Update quiz", description = "Update an existing quiz")
        @PreAuthorize("hasAuthority('quiz_UPDATE') OR @lobbySecurity.isQuizHost(#quizId, authentication)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Quiz updated successfully"),
                        @ApiResponse(responseCode = "404", description = "Quiz not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<DataResDTO<QuizResDTO>> updateQuiz(
                        @Parameter(description = "Quiz ID") @PathVariable Long quizId,
                        @Valid @RequestBody QuizReqDTO quizReqDTO) {
                log.info("Update quiz with id {}", quizId);
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(DataResDTO.update(quizService.update(quizId, quizReqDTO)));
        }

        @DeleteMapping(ApiDefined.Quiz.ID)
        @Operation(summary = "Delete quiz", description = "Delete a quiz by its ID")
        @PreAuthorize("hasAuthority('quiz_DELETE') OR @lobbySecurity.isQuizHost(#quizId, authentication)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Quiz deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Quiz not found")
        })
        public ResponseEntity<DataResDTO<Void>> deleteQuiz(
                        @Parameter(description = "Quiz ID") @PathVariable Long quizId) {
                log.info("Delete quiz with id {}", quizId);
                quizService.delete(quizId);
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(DataResDTO.delete());
        }
}
