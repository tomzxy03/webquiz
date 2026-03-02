package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.Lobby.LobbyReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.lobby.LobbyResDTO;
import com.tomzxy.web_quiz.services.LobbyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.Group.BASE)
@Tag(name = "Groups", description = "Group/Lobby management APIs")
public class LobbyController {

    private final LobbyService lobbyService;

    // ======== Group CRUD ========

    @GetMapping
    @Operation(summary = "Get all groups", description = "Retrieve all groups with pagination")
    public ResponseEntity<DataResDTO<PageResDTO<?>>> getAllGroups(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        log.info("Get all groups - page: {}, size: {}, search: {}", page, size, search);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.ok(lobbyService.getAllLobby(page, size)));
    }

    @GetMapping(ApiDefined.Group.ID)
    @Operation(summary = "Get group by ID")
    public ResponseEntity<DataResDTO<LobbyResDTO>> getGroupById(
            @Parameter(description = "Group ID") @PathVariable Long groupId) {
        log.info("Get group by id: {}", groupId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.ok(lobbyService.getLobby(groupId)));
    }

    @GetMapping(ApiDefined.Group.BY_USER)
    @Operation(summary = "Get groups by user", description = "Get all groups that a user belongs to")
    public ResponseEntity<DataResDTO<PageResDTO<?>>> getGroupsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get groups for user: {}", userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.ok(lobbyService.getLobbyByUser(userId, page, size)));
    }

    @PostMapping
    @Operation(summary = "Create group")
    public ResponseEntity<DataResDTO<LobbyResDTO>> createGroup(
            @Valid @RequestBody LobbyReqDTO lobbyReqDTO) {
        log.info("Create group: {}", lobbyReqDTO.getLobbyName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResDTO.create(lobbyService.createLobby(lobbyReqDTO)));
    }

    @PutMapping(ApiDefined.Group.ID)
    @Operation(summary = "Update group")
    public ResponseEntity<DataResDTO<LobbyResDTO>> updateGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody LobbyReqDTO lobbyReqDTO) {
        log.info("Update group: {}", groupId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.update(lobbyService.updateLobby(groupId, lobbyReqDTO)));
    }

    @DeleteMapping(ApiDefined.Group.ID)
    @Operation(summary = "Delete group")
    public ResponseEntity<DataResDTO<Void>> deleteGroup(
            @PathVariable Long groupId) {
        log.info("Delete group: {}", groupId);
        lobbyService.deleteLobby(groupId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.delete());
    }

    // ======== Members ========

    @GetMapping(ApiDefined.Group.MEMBER)
    @Operation(summary = "Get group members")
    public ResponseEntity<DataResDTO<PageResDTO<?>>> getMembers(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get members for group: {}", groupId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.ok(lobbyService.getAllMembers(groupId, page, size)));
    }

    @PostMapping(ApiDefined.Group.MEMBER)
    @Operation(summary = "Add member to group")
    public ResponseEntity<DataResDTO<LobbyResDTO>> addMember(
            @PathVariable Long groupId,
            @RequestParam Long userId) {
        log.info("Add member {} to group {}", userId, groupId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.ok(lobbyService.addMember(groupId, userId)));
    }

    @DeleteMapping(ApiDefined.Group.MEMBER_ID)
    @Operation(summary = "Remove member from group")
    public ResponseEntity<DataResDTO<Void>> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        log.info("Remove member {} from group {}", userId, groupId);
        lobbyService.removeMember(groupId, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.delete());
    }

    // ======== Announcements ========

    @GetMapping(ApiDefined.Group.NOTIFICATIONS)
    @Operation(summary = "Get group announcements")
    public ResponseEntity<DataResDTO<PageResDTO<?>>> getAnnouncements(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get announcements for group: {}", groupId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.ok(lobbyService.getAllNotifications(groupId)));
    }

    // ======== Group Quizzes ========

    @GetMapping(ApiDefined.Group.QUIZ)
    @Operation(summary = "Get quizzes in group")
    public ResponseEntity<DataResDTO<PageResDTO<?>>> getGroupQuizzes(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get quizzes for group: {}", groupId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.ok(lobbyService.getGroupQuizzes(groupId, page, size)));
    }

    @PostMapping(ApiDefined.Group.QUIZ)
    @Operation(summary = "Add quiz to group")
    public ResponseEntity<DataResDTO<LobbyResDTO>> addQuizToGroup(
            @PathVariable Long groupId,
            @RequestParam Long quizId) {
        log.info("Add quiz {} to group {}", quizId, groupId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.ok(lobbyService.addQuizToGroup(groupId, quizId)));
    }

    @DeleteMapping(ApiDefined.Group.JOIN_QUIZ)
    @Operation(summary = "Remove quiz from group")
    public ResponseEntity<DataResDTO<Void>> removeQuizFromGroup(
            @PathVariable Long groupId,
            @PathVariable Long quizId) {
        log.info("Remove quiz {} from group {}", quizId, groupId);
        lobbyService.removeQuizFromGroup(groupId, quizId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.delete());
    }
}
