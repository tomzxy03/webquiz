package com.tomzxy.web_quiz.controllers;


import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.QuestionReqDTO;
import com.tomzxy.web_quiz.dto.requests.SubjectReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.QuestionResDTO;
import com.tomzxy.web_quiz.dto.responses.SubjectResDTO;
import com.tomzxy.web_quiz.services.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.Subject.BASE)
@Tag(name = "Subjects", description = "Subject management APIs")
public class SubjectController {
    private final SubjectService subjectService;

    @GetMapping()
    @Operation(summary = "Get all subjects", description = "Retrieve all subjects")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subjects retrieved successfully")
    })
    public DataResDTO<List<SubjectResDTO>> getAllSubject(){
        log.info("Get all Subjects");
        return DataResDTO.ok(subjectService.get_all_subject());
    }

    @GetMapping(ApiDefined.Subject.ID)
    @Operation(summary = "Get subject by ID", description = "Retrieve a subject by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subject found successfully"),
        @ApiResponse(responseCode = "404", description = "Subject not found")
    })
    public DataResDTO<SubjectResDTO> getSubject(
            @Parameter(description = "Subject ID") @PathVariable Long subjectId){
        log.info("get Subject by {}", subjectId);
        return DataResDTO.ok(subjectService.get_subject(subjectId));
    }
    
    @PostMapping()
    @Operation(summary = "Create subject", description = "Create a new subject")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Subject created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public DataResDTO<Void> addSubject(@Valid @RequestBody SubjectReqDTO subjectReqDTO){
        log.info("add Subject");
        subjectService.create_subject(subjectReqDTO);
        return DataResDTO.create();
    }

    @PutMapping(ApiDefined.Subject.ID)
    @Operation(summary = "Update subject", description = "Update an existing subject")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subject updated successfully"),
        @ApiResponse(responseCode = "404", description = "Subject not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public DataResDTO<SubjectResDTO> updateSubject(
            @Parameter(description = "Subject ID") @PathVariable Long subjectId, 
            @RequestBody @Valid SubjectReqDTO subjectReqDTO){
        log.info("Update Subject with id {}", subjectId);
        return DataResDTO.update(subjectService.update_subject(subjectId,subjectReqDTO));
    }

    @DeleteMapping(ApiDefined.Subject.ID)
    @Operation(summary = "Delete subject", description = "Delete a subject by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subject deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Subject not found")
    })
    public DataResDTO<Void> deleteSubject(
            @Parameter(description = "Subject ID") @PathVariable Long subjectId){
        log.info("Delete Subject with id {}", subjectId);
        subjectService.delete_subject(subjectId);
        return DataResDTO.delete();
    }
}
