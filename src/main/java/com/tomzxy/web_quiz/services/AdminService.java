package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.admin.*;
import java.util.List;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.admin.*;

public interface AdminService {

    AdminDashboardResDTO getDashboard();

    PageResDTO<AdminUserResDTO> getUsers(AdminListReqDTO req);

    AdminUserResDTO updateUser(Long userId, AdminUserUpdateReqDTO req);

    PageResDTO<AdminGroupResDTO> getGroups(AdminListReqDTO req);

    AdminGroupResDTO updateGroup(Long groupId, AdminGroupUpdateReqDTO req);

    PageResDTO<AdminQuizResDTO> getQuizzes(AdminListReqDTO req);

    AdminQuizResDTO updateQuiz(Long quizId, AdminQuizUpdateReqDTO req);

    PageResDTO<AdminResultResDTO> getResults(AdminListReqDTO req);

    AdminUserDetailResDTO getUserDetail(Long userId);

    void deleteUser(Long userId);

    AdminGroupDetailResDTO getGroupDetail(Long groupId);

    void deleteGroup(Long groupId);

    AdminQuizDetailResDTO getQuizDetail(Long quizId);

    void deleteQuiz(Long quizId);

    AdminResultDetailResDTO getResultDetail(Long resultId);

    void deleteResult(Long resultId);

    List<AdminSubjectResDTO> getSubjects();

    void createSubject(AdminSubjectReqDTO req);

    AdminSubjectResDTO updateSubject(Long subjectId, AdminSubjectReqDTO req);

    void deleteSubject(Long subjectId);

    List<AdminRoleResDTO> getRoles();

    AdminRoleResDTO getRole(Long roleId);

    PageResDTO<AdminNotificationResDTO> getNotifications(int page, int size);

    AdminRoleResDTO createRole(AdminRoleReqDTO req);

    AdminRoleResDTO updateRole(Long roleId, AdminRoleReqDTO req);

    void deleteRole(Long roleId);

    void deleteNotification(Long notificationId);

    PageResDTO<AdminQuestionBankResDTO> getQuestionBanks(int page, int size);
}
