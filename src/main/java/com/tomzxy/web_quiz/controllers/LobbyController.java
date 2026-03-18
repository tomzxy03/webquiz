package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.Lobby.LobbyReqDTO;
import com.tomzxy.web_quiz.dto.requests.Notification.NotificationReqDTO;
import com.tomzxy.web_quiz.dto.requests.quiz.QuizReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.Quiz.QuizResDTO;
import com.tomzxy.web_quiz.dto.responses.lobby.LobbyNotificationResDTO;
import com.tomzxy.web_quiz.dto.responses.lobby.LobbyQuizResDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
        @PreAuthorize("hasAuthority('group_VIEW')")
        public ResponseEntity<DataResDTO<PageResDTO<?>>> getAllGroups(
                        @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
                        @Parameter(description = "Search term") @RequestParam(required = false) String search) {
                log.info("Get all groups - page: {}, size: {}, search: {}", page, size, search);
                return ResponseEntity.status(HttpStatus.OK)
                                .body(DataResDTO.ok(lobbyService.getAllLobby(page, size)));
        }
        @GetMapping(ApiDefined.Group.OWNED)
        @Operation(summary = "Get all groups owned", description = "Retrieve all groups with pagination")
        @PreAuthorize("hasAuthority('group_VIEW')")
        public ResponseEntity<DataResDTO<PageResDTO<?>>> getAllGroupsOwned(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
            log.info("Get all groups owned - page: {}, size: {}", page, size);
            return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.ok(lobbyService.getAllLobbyOwned(page, size)));
        }

        @GetMapping(ApiDefined.Group.JOINED)
        @Operation(summary = "Get all groups joined", description = "Retrieve all groups with pagination")
        @PreAuthorize("hasAuthority('group_VIEW')")
        public ResponseEntity<DataResDTO<PageResDTO<?>>> getAllGroupsJoined(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
            log.info("Get all groups joined - page: {}, size: {}", page, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DataResDTO.ok(lobbyService.getAllLobbyJoined(page, size)));
        }

        @GetMapping(ApiDefined.Group.ID)
        @Operation(summary = "Get group by ID")
        @PreAuthorize("hasAuthority('group_VIEW')")
        public ResponseEntity<DataResDTO<LobbyResDTO>> getGroupById(
                        @Parameter(description = "Group ID") @PathVariable Long groupId) {
                log.info("Get group by id: {}", groupId);
                return ResponseEntity.status(HttpStatus.OK)
                                .body(DataResDTO.ok(lobbyService.getLobby(groupId)));
        }


        @PostMapping
        @Operation(summary = "Create group")
        @PreAuthorize("hasAuthority('group_CREATE')")
        public ResponseEntity<DataResDTO<LobbyResDTO>> createGroup(
                        @Valid @RequestBody LobbyReqDTO lobbyReqDTO) {
                log.info("Create group: {}", lobbyReqDTO.getLobbyName());
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(DataResDTO.create(lobbyService.createLobby(lobbyReqDTO)));
        }

        @PutMapping(ApiDefined.Group.ID)
        @Operation(summary = "Update group")
        @PreAuthorize("@lobbySecurity.isHost(#groupId, authentication)")
        public ResponseEntity<DataResDTO<LobbyResDTO>> updateGroup(
                        @PathVariable Long groupId,
                        @Valid @RequestBody LobbyReqDTO lobbyReqDTO) {
                log.info("Update group: {}", groupId);
                return ResponseEntity.status(HttpStatus.OK)
                                .body(DataResDTO.update(lobbyService.updateLobby(groupId, lobbyReqDTO)));
        }

        @DeleteMapping(ApiDefined.Group.ID)
        @Operation(summary = "Delete group")
        @PreAuthorize("@lobbySecurity.isHost(#groupId, authentication)")
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
        @PreAuthorize("hasAuthority('group_VIEW')")
        public ResponseEntity<DataResDTO<PageResDTO<?>>> getMembers(
                        @PathVariable Long groupId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                log.info("Get members for group: {}", groupId);
                return ResponseEntity.status(HttpStatus.OK)
                                .body(DataResDTO.ok(lobbyService.getAllMembers(groupId, page, size)));
        }
        // delete member
        @DeleteMapping(ApiDefined.Group.MEMBER_ID)
        @Operation(summary = "Remove member from group")
        @PreAuthorize("@lobbySecurity.isHost(#groupId, authentication)")
        public ResponseEntity<DataResDTO<Void>> removeMember(
                        @PathVariable Long groupId,
                        @PathVariable Long userId) {
                log.info("Remove member {} from group {}", userId, groupId);
                lobbyService.removeMember(groupId, userId);
                return ResponseEntity.status(HttpStatus.OK)
                                .body(DataResDTO.delete());
        }
        // leave
        @DeleteMapping(ApiDefined.Group.LEAVE)
        @Operation(summary = "Leave group")
        @PreAuthorize("hasAuthority('group_VIEW')")
        public ResponseEntity<DataResDTO<Void>> leaveGroup(
                        @PathVariable Long groupId
                        ) {
                log.info("Leave group {}", groupId);
                lobbyService.leaveLobby(groupId);
                return ResponseEntity.status(HttpStatus.OK)
                                .body(DataResDTO.delete());
        }


        // ======== Announcements ========

        @GetMapping(ApiDefined.Group.NOTIFICATIONS)
        @Operation(summary = "Get group announcements")
        @PreAuthorize("hasAuthority('group_VIEW')")
        public ResponseEntity<DataResDTO<PageResDTO<?>>> getAnnouncements(
                        @PathVariable Long groupId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                log.info("Get announcements for group: {}", groupId);
                return ResponseEntity.status(HttpStatus.OK)
                                .body(DataResDTO.ok(lobbyService.getAllNotifications(groupId, page,size)));
        }

        // add announcements
        @GetMapping(ApiDefined.Group.ADD_NOTIFICATION)
        @Operation(summary = "Add group announcements")
        @PreAuthorize("@lobbySecurity.isHost(#groupId, authentication)")
        public ResponseEntity<DataResDTO<LobbyNotificationResDTO>> addAnnouncements(
                @PathVariable Long groupId,
                @RequestBody NotificationReqDTO notificationReqDTO) {
            log.info("Add announcements for group: {}", groupId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(DataResDTO.ok(lobbyService.addNotification(groupId, notificationReqDTO)));
        }
        // update announcements
        @PutMapping(ApiDefined.Group.UPDATE_NOTIFICATION)
        @Operation(summary = "Update group announcements")
        @PreAuthorize("@lobbySecurity.isHost(#groupId, authentication)")
        public ResponseEntity<DataResDTO<LobbyNotificationResDTO>> updateAnnouncements(
                @PathVariable Long groupId,
                @PathVariable Long notificationId,
                @RequestBody @Valid NotificationReqDTO notificationReqDTO) {
            log.info("Update announcements for group: {}", groupId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(DataResDTO.ok(lobbyService.updateNotification(groupId, notificationId, notificationReqDTO)));
        }
        // delete announcements
        @DeleteMapping(ApiDefined.Group.DELETE_NOTIFICATION)
        @Operation(summary = "Delete group announcements")
        @PreAuthorize("@lobbySecurity.isHost(#groupId, authentication)")
        public ResponseEntity<DataResDTO<Void>> deleteAnnouncements(
                @PathVariable Long groupId,
                @PathVariable Long notificationId) {
            log.info("Delete announcements for group: {}", groupId);
            lobbyService.deleteNotification(groupId, notificationId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(DataResDTO.delete());
        }

        // ======== Group Quizzes ========

        @GetMapping(ApiDefined.Group.QUIZ)
        @Operation(summary = "Get quizzes in group")
        @PreAuthorize("hasAuthority('group_VIEW')")
        public ResponseEntity<DataResDTO<PageResDTO<QuizResDTO>>> getGroupQuizzes(
                        @PathVariable Long groupId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                log.info("Get quizzes for group: {}", groupId);
                return ResponseEntity.status(HttpStatus.OK)
                                .body(DataResDTO.ok(lobbyService.getGroupQuizzes(groupId, page, size)));
        }

        @PostMapping(ApiDefined.Group.QUIZ)
        @Operation(summary = "Add quiz to group")
        @PreAuthorize("@lobbySecurity.isHost(#groupId, authentication) OR @lobbySecurity.isMember(#groupId, authentication)")
        public ResponseEntity<DataResDTO<LobbyQuizResDTO>> addQuizToGroup(
                @PathVariable Long groupId,
                @RequestBody @Valid QuizReqDTO quizReqDTO) {
                log.info("Add quiz {} to group {}", quizReqDTO.getTitle(), groupId);
                return ResponseEntity.status(HttpStatus.OK)
                                .body(DataResDTO.ok(lobbyService.addQuizToGroup(groupId, quizReqDTO)));
        }
        // update quiz
        @PutMapping(ApiDefined.Group.UPDATE_QUIZ)
        @Operation(summary = "Update quiz in group")
        @PreAuthorize("@lobbySecurity.isHost(#groupId, authentication)")
        public ResponseEntity<DataResDTO<LobbyQuizResDTO>> updateQuiz(
                @PathVariable Long groupId,
                @PathVariable Long quizId,
                @RequestBody @Valid QuizReqDTO quizReqDTO
        ){
                log.info("Update quiz to group");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(DataResDTO.ok(lobbyService.updateQuizInGroup(groupId, quizId, quizReqDTO)));
        }
        @DeleteMapping(ApiDefined.Group.DELETE_QUIZ)
        @Operation(summary = "Remove quiz from group")
        @PreAuthorize("@lobbySecurity.isHost(#groupId, authentication)")
        public ResponseEntity<DataResDTO<Void>> removeQuizFromGroup(
                        @PathVariable Long groupId,
                        @PathVariable Long quizId) {
                log.info("Remove quiz {} from group {}", quizId, groupId);
                lobbyService.removeQuizFromGroup(groupId, quizId);
                return ResponseEntity.status(HttpStatus.OK)
                                .body(DataResDTO.delete());
        }
}
