package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.admin.*;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.admin.*;
import com.tomzxy.web_quiz.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping(path = ApiDefined.Admin.BASE)
@Tag(name = "Admin", description = "Admin management APIs")
public class AdminController {

    private final AdminService adminService;

    // ─── Dashboard ──────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Admin dashboard", description = "Get admin dashboard summary with counts, trends, and recent items")
    public ResponseEntity<DataResDTO<AdminDashboardResDTO>> getDashboard() {
        log.info("Admin: get dashboard");
        return ResponseEntity.ok(DataResDTO.ok(adminService.getDashboard()));
    }

    // ─── Users ──────────────────────────────────────────────────────────

    @GetMapping(ApiDefined.Admin.USERS)
    @Operation(summary = "List users", description = "List all users with pagination, search, and filters")
    public ResponseEntity<DataResDTO<PageResDTO<AdminUserResDTO>>> getUsers(AdminListReqDTO req) {
        log.info("Admin: list users");
        return ResponseEntity.ok(DataResDTO.ok(adminService.getUsers(req)));
    }

    @PutMapping(ApiDefined.Admin.USER_ID)
    @Operation(summary = "Update user", description = "Update user status and/or roles")
    public ResponseEntity<DataResDTO<AdminUserResDTO>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserUpdateReqDTO req) {
        log.info("Admin: update user {}", userId);
        return ResponseEntity.ok(DataResDTO.update(adminService.updateUser(userId, req)));
    }

    @GetMapping(ApiDefined.Admin.USER_ID)
    @Operation(summary = "Get user detail", description = "Get single user detail with roles, groups joined, quiz history")
    public ResponseEntity<DataResDTO<AdminUserDetailResDTO>> getUserDetail(@PathVariable Long userId) {
        log.info("Admin: get user detail {}", userId);
        return ResponseEntity.ok(DataResDTO.ok(adminService.getUserDetail(userId)));
    }

    @DeleteMapping(ApiDefined.Admin.USER_ID)
    @Operation(summary = "Delete user", description = "Soft-delete (deactivate) user")
    public ResponseEntity<DataResDTO<Void>> deleteUser(@PathVariable Long userId) {
        log.info("Admin: delete user {}", userId);
        adminService.deleteUser(userId);
        return ResponseEntity.ok(DataResDTO.delete());
    }

    // ─── Groups ─────────────────────────────────────────────────────────

    @GetMapping(ApiDefined.Admin.GROUPS)
    @Operation(summary = "List groups", description = "List all groups with pagination, search, and filters")
    public ResponseEntity<DataResDTO<PageResDTO<AdminGroupResDTO>>> getGroups(AdminListReqDTO req) {
        log.info("Admin: list groups");
        return ResponseEntity.ok(DataResDTO.ok(adminService.getGroups(req)));
    }

    @PutMapping(ApiDefined.Admin.GROUP_ID)
    @Operation(summary = "Update group", description = "Update group status")
    public ResponseEntity<DataResDTO<AdminGroupResDTO>> updateGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody AdminGroupUpdateReqDTO req) {
        log.info("Admin: update group {}", groupId);
        return ResponseEntity.ok(DataResDTO.update(adminService.updateGroup(groupId, req)));
    }

    @GetMapping(ApiDefined.Admin.GROUP_ID)
    @Operation(summary = "Get group detail", description = "Get single group detail with members, quizzes, announcements count")
    public ResponseEntity<DataResDTO<AdminGroupDetailResDTO>> getGroupDetail(@PathVariable Long groupId) {
        log.info("Admin: get group detail {}", groupId);
        return ResponseEntity.ok(DataResDTO.ok(adminService.getGroupDetail(groupId)));
    }

    @DeleteMapping(ApiDefined.Admin.GROUP_ID)
    @Operation(summary = "Delete group", description = "Soft-delete (deactivate) group")
    public ResponseEntity<DataResDTO<Void>> deleteGroup(@PathVariable Long groupId) {
        log.info("Admin: delete group {}", groupId);
        adminService.deleteGroup(groupId);
        return ResponseEntity.ok(DataResDTO.delete());
    }

    // ─── Quizzes ────────────────────────────────────────────────────────

    @GetMapping(ApiDefined.Admin.QUIZZES)
    @Operation(summary = "List quizzes", description = "List all quizzes with pagination, search, and filters")
    public ResponseEntity<DataResDTO<PageResDTO<AdminQuizResDTO>>> getQuizzes(AdminListReqDTO req) {
        log.info("Admin: list quizzes");
        return ResponseEntity.ok(DataResDTO.ok(adminService.getQuizzes(req)));
    }

    @PutMapping(ApiDefined.Admin.QUIZ_ID)
    @Operation(summary = "Update quiz", description = "Update quiz status and/or visibility")
    public ResponseEntity<DataResDTO<AdminQuizResDTO>> updateQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody AdminQuizUpdateReqDTO req) {
        log.info("Admin: update quiz {}", quizId);
        return ResponseEntity.ok(DataResDTO.update(adminService.updateQuiz(quizId, req)));
    }

    @GetMapping(ApiDefined.Admin.QUIZ_ID)
    @Operation(summary = "Get quiz detail", description = "Get single quiz detail with questions count, attempts count, config")
    public ResponseEntity<DataResDTO<AdminQuizDetailResDTO>> getQuizDetail(@PathVariable Long quizId) {
        log.info("Admin: get quiz detail {}", quizId);
        return ResponseEntity.ok(DataResDTO.ok(adminService.getQuizDetail(quizId)));
    }

    @DeleteMapping(ApiDefined.Admin.QUIZ_ID)
    @Operation(summary = "Delete quiz", description = "Soft-delete (deactivate) quiz")
    public ResponseEntity<DataResDTO<Void>> deleteQuiz(@PathVariable Long quizId) {
        log.info("Admin: delete quiz {}", quizId);
        adminService.deleteQuiz(quizId);
        return ResponseEntity.ok(DataResDTO.delete());
    }

    // ─── Results ────────────────────────────────────────────────────────

    @GetMapping(ApiDefined.Admin.RESULTS)
    @Operation(summary = "List results", description = "List all quiz results with pagination, search, and filters")
    public ResponseEntity<DataResDTO<PageResDTO<AdminResultResDTO>>> getResults(AdminListReqDTO req) {
        log.info("Admin: list results");
        return ResponseEntity.ok(DataResDTO.ok(adminService.getResults(req)));
    }

    @GetMapping(ApiDefined.Admin.RESULT_ID)
    @Operation(summary = "Get result detail", description = "Get single result detail with user responses and scoring breakdown")
    public ResponseEntity<DataResDTO<AdminResultDetailResDTO>> getResultDetail(@PathVariable Long resultId) {
        log.info("Admin: get result detail {}", resultId);
        return ResponseEntity.ok(DataResDTO.ok(adminService.getResultDetail(resultId)));
    }

    @DeleteMapping(ApiDefined.Admin.RESULT_ID)
    @Operation(summary = "Delete result", description = "Soft-delete quiz instance")
    public ResponseEntity<DataResDTO<Void>> deleteResult(@PathVariable Long resultId) {
        log.info("Admin: delete result {}", resultId);
        adminService.deleteResult(resultId);
        return ResponseEntity.ok(DataResDTO.delete());
    }

    // ─── Subjects ───────────────────────────────────────────────────────
    @GetMapping(ApiDefined.Admin.SUBJECTS)
    @Operation(summary = "List subjects", description = "Lists all subjects with quiz counts")
    public ResponseEntity<DataResDTO<java.util.List<AdminSubjectResDTO>>> getSubjects() {
        log.info("Admin: get subjects");
        return ResponseEntity.ok(DataResDTO.ok(adminService.getSubjects()));
    }

    @PostMapping(ApiDefined.Admin.SUBJECTS)
    @Operation(summary = "Create subject", description = "Create a new subject")
    public ResponseEntity<DataResDTO<Void>> createSubject(@Valid @RequestBody AdminSubjectReqDTO req) {
        log.info("Admin: create subject");
        adminService.createSubject(req);
        return ResponseEntity.ok(DataResDTO.create());
    }

    @PutMapping(ApiDefined.Admin.SUBJECT_ID)
    @Operation(summary = "Update subject", description = "Update subject details")
    public ResponseEntity<DataResDTO<AdminSubjectResDTO>> updateSubject(
            @PathVariable Long subjectId,
            @Valid @RequestBody AdminSubjectReqDTO req) {
        log.info("Admin: update subject {}", subjectId);
        return ResponseEntity.ok(DataResDTO.update(adminService.updateSubject(subjectId, req)));
    }

    @DeleteMapping(ApiDefined.Admin.SUBJECT_ID)
    @Operation(summary = "Delete subject", description = "Soft-delete subject")
    public ResponseEntity<DataResDTO<Void>> deleteSubject(@PathVariable Long subjectId) {
        log.info("Admin: delete subject {}", subjectId);
        adminService.deleteSubject(subjectId);
        return ResponseEntity.ok(DataResDTO.delete());
    }

    // ─── Roles ──────────────────────────────────────────────────────────
    @GetMapping(ApiDefined.Admin.ROLES)
    @Operation(summary = "List roles", description = "Lists all roles with user counts and permissions")
    public ResponseEntity<DataResDTO<java.util.List<AdminRoleResDTO>>> getRoles() {
        log.info("Admin: get roles");
        return ResponseEntity.ok(DataResDTO.ok(adminService.getRoles()));
    }

    @GetMapping(ApiDefined.Admin.ROLE_ID)
    @Operation(summary = "Get role detail", description = "Get single role detail with permissions and user count")
    public ResponseEntity<DataResDTO<AdminRoleResDTO>> getRole(@PathVariable Long roleId) {
        log.info("Admin: get role {}", roleId);
        return ResponseEntity.ok(DataResDTO.ok(adminService.getRole(roleId)));
    }

    @PostMapping(ApiDefined.Admin.ROLES)
    @Operation(summary = "Create role", description = "Create a new role with permissions")
    public ResponseEntity<DataResDTO<AdminRoleResDTO>> createRole(@Valid @RequestBody AdminRoleReqDTO req) {
        log.info("Admin: create role {}", req.getName());
        return ResponseEntity.ok(DataResDTO.create(adminService.createRole(req)));
    }

    @PutMapping(ApiDefined.Admin.ROLE_ID)
    @Operation(summary = "Update role", description = "Update an existing role's permissions or name")
    public ResponseEntity<DataResDTO<AdminRoleResDTO>> updateRole(
            @PathVariable Long roleId,
            @Valid @RequestBody AdminRoleReqDTO req) {
        log.info("Admin: update role {}", roleId);
        return ResponseEntity.ok(DataResDTO.update(adminService.updateRole(roleId, req)));
    }

    @DeleteMapping(ApiDefined.Admin.ROLE_ID)
    @Operation(summary = "Delete role", description = "Delete an existing role")
    public ResponseEntity<DataResDTO<Void>> deleteRole(@PathVariable Long roleId) {
        log.info("Admin: delete role {}", roleId);
        adminService.deleteRole(roleId);
        return ResponseEntity.ok(DataResDTO.delete());
    }

    // ─── Notifications ──────────────────────────────────────────────────
    @GetMapping(ApiDefined.Admin.NOTIFICATIONS)
    @Operation(summary = "List notifications", description = "Lists all notifications across groups")
    public ResponseEntity<DataResDTO<PageResDTO<AdminNotificationResDTO>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Admin: get notifications");
        return ResponseEntity.ok(DataResDTO.ok(adminService.getNotifications(page, size)));
    }

    @DeleteMapping(ApiDefined.Admin.NOTIFICATION_ID)
    @Operation(summary = "Delete notification", description = "Delete notification")
    public ResponseEntity<DataResDTO<Void>> deleteNotification(@PathVariable Long notificationId) {
        log.info("Admin: delete notification {}", notificationId);
        adminService.deleteNotification(notificationId);
        return ResponseEntity.ok(DataResDTO.delete());
    }

    // ─── Question Banks ─────────────────────────────────────────────────
    @GetMapping(ApiDefined.Admin.QUESTION_BANKS)
    @Operation(summary = "List question banks", description = "Lists all question banks with owner info")
    public ResponseEntity<DataResDTO<PageResDTO<AdminQuestionBankResDTO>>> getQuestionBanks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Admin: get question banks");
        return ResponseEntity.ok(DataResDTO.ok(adminService.getQuestionBanks(page, size)));
    }
}
