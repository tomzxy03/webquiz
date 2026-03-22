## Các Tính Năng Quản Trị Hệ Thống (Admin APIs)

Tất cả các requests dưới đây sử dụng form base `DataResDTO<T>`, cấu trúc chuẩn trả về:
```json
{
  "code": 200,          // 200: OK, 201: Created
  "message": "...",     // Thông điệp kết quả
  "items": { ... }      // Chứa dữ liệu trả về theo từng DTO cụ thể
}
```

### 1. Thống Kê Dashboard
- **`GET /tomzxyadmin`**
  - **Request:** Trống
  - **Response (`AdminDashboardResDTO`):**
    ```json
    {
      "totalUsers": 1205,
      "totalGroups": 15,
      "totalQuizzes": 403,
      "totalResults": 15034,
      "userTrend": "+5.2% tháng này",
      "groupTrend": "+1.0% tháng này",
      "quizTrend": "+12.4% tháng này",
      "resultTrend": "+20.1% tháng này",
      "recentQuizzes": [ { "id": 1, "title": "C++ Basics", "creatorName": "tomzxy03", "createdAt": "2026-03-22T..." } ],
      "recentUsers": [ { "id": 2, "userName": "datvip2003", "email": "datvip2003@gmail.com", "joinedAt": "2026-03-20T..." } ]
    }
    ```

### 2. Quản Lý Người Dùng
- **`GET /tomzxyadmin/users`**
  - **Query parameters (`AdminListReqDTO`):** `page`, `size`, `search`, `status`
  - **Response (`PageResDTO<AdminUserResDTO>`):**
    ```json
    {
      "content": [
        { "id": 1, "userName": "datvip", "email": "dat@gmail.com", "role": "USER", "status": "ACTIVE", "joinedAt": "..." }
      ],
      "pageNo": 0, "pageSize": 10, "totalElements": 1, "totalPages": 1, "last": true
    }
    ```
- **`GET /tomzxyadmin/users/{userId}`**
  - **Request:** Trống
  - **Response (`AdminUserDetailResDTO`):** Chứa thông tin của `AdminUserResDTO` ở trên, cộng thêm:
    ```json
    {
      "groups": [ { "id": 1, "name": "Lập trình Java", "role": "MEMBER", "joinedAt": "..." } ],
      "results": [ { "id": 10, "quizTitle": "Java Core", "score": 90.0, "timeSpentSeconds": 1500, "submittedAt": "..." } ]
    }
    ```
- **`PUT /tomzxyadmin/users/{userId}`**
  - **Request Body (`AdminUserUpdateReqDTO`):**
    ```json
    { "status": "ACTIVE", "roleIds": [2] }
    ```
  - **Response:** `AdminUserResDTO`
- **`DELETE /tomzxyadmin/users/{userId}`**
  - **Response:** Trống (`items: null`, `code: 6` - Delete thành công). Soft-delete (vô hiệu hóa user).

### 3. Quản Lý Nhóm (Lobby)
- **`GET /tomzxyadmin/groups`**
  - **Query parameters:** `page`, `size`, `search`, `status`
  - **Response (`PageResDTO<AdminGroupResDTO>`):** Returns `{ id, name, ownerName, status, createdAt, memberCount }`
- **`GET /tomzxyadmin/groups/{groupId}`**
  - **Response (`AdminGroupDetailResDTO`):** Data giống `AdminGroupResDTO` và thêm chi tiết:
    ```json
    {
      "description": "Nhóm học Java",
      "quizCount": 5, "announcementCount": 2,
      "members": [ { "id": 1, "userName": "datvip", "email": "dat@gmail.com", "role": "HOST", "joinedAt": "..." } ],
      "quizzes": [ { "id": 3, "title": "OOP Basic", "status": "PUBLISHED", "createdAt": "..." } ]
    }
    ```
- **`PUT /tomzxyadmin/groups/{groupId}`**
  - **Request Body (`AdminGroupUpdateReqDTO`):** `{ "status": "ACTIVE" }`
  - **Response:** `AdminGroupResDTO`
- **`DELETE /tomzxyadmin/groups/{groupId}`**
  - **Response:** Trống. Soft-delete nhóm.

### 4. Quản Lý Quizzes (Bài Thi)
- **`GET /tomzxyadmin/quizzes`**
  - **Query params:** `page`, `size`, `search`, `status`, `groupId`
  - **Response (`PageResDTO<AdminQuizResDTO>`):** Returns `{ id, title, creatorName, groupName, status, visibility, createdAt }`
- **`GET /tomzxyadmin/quizzes/{quizId}`**
  - **Response (`AdminQuizDetailResDTO`):** Data giống `AdminQuizResDTO` kèm thêm:
    ```json
    {
      "questionCount": 20, "attemptCount": 150,
      "config": {
         "timeLimitMinutes": 45,
         "passingScore": 50,
         "shuffleQuestions": true,
         "shuffleOptions": true,
         "allowReview": true,
         "maxAttempts": 2
      }
    }
    ```
- **`PUT /tomzxyadmin/quizzes/{quizId}`**
  - **Request Body (`AdminQuizUpdateReqDTO`):** `{ "status": "PUBLISHED", "visibility": "PUBLIC" }`
  - **Response:** `AdminQuizResDTO`
- **`DELETE /tomzxyadmin/quizzes/{quizId}`**
  - **Response:** Trống. Soft-delete quiz.

### 5. Quản Lý Lịch Sử Bài Làm (Results)
- **`GET /tomzxyadmin/results`**
  - **Query params:** `page`, `size`, `search` (theo user/quiz title)
  - **Response (`PageResDTO<AdminResultResDTO>`):** Returns `{ id, quizTitle, userName, groupName, status, score, timeSpentSeconds, submittedAt }`
- **`GET /tomzxyadmin/results/{resultId}`**
  - **Response (`AdminResultDetailResDTO`):** Data giống `AdminResultResDTO` cộng thêm:
    ```json
    {
      "correctAnswers": 15,
      "wrongAnswers": 4,
      "skippedAnswers": 1
    }
    ```
- **`DELETE /tomzxyadmin/results/{resultId}`**
  - **Response:** Trống. Soft-delete kết quả thi đó.

### 6. Quản Lý Môn Học (Subjects)
- **`GET /tomzxyadmin/subjects`**
  - **Response:** Array of `AdminSubjectResDTO` `{ id, name, description, active, quizCount }`
- **`POST /tomzxyadmin/subjects`**
  - **Request Body (`AdminSubjectReqDTO`):** `{ "name": "Lịch Sử", "description": "Lịch sử Việt Nam" }`
  - **Response:** Trống (`code: 201 Created`)
- **`PUT /tomzxyadmin/subjects/{subjectId}`**
  - **Request Body (`AdminSubjectReqDTO`):** Nhập tên và description cần update.
  - **Response:** `AdminSubjectResDTO`
- **`DELETE /tomzxyadmin/subjects/{subjectId}`**
  - **Response:** Trống. Soft-delete subject.

### 7. Quản Lý Quyền Hệ Thống (Roles)
- **`GET /tomzxyadmin/roles`**
  - **Response:** Array of `AdminRoleResDTO` 
    ```json
    [
      { "id": 1, "name": "ADMIN", "userCount": 2, "permissions": ["VIEW_user", "UPDATE_user", "DELETE_quiz"] }
    ]
    ```
- **`GET /tomzxyadmin/roles/{roleId}`**
  - **Response:** `AdminRoleResDTO`
- **`POST /tomzxyadmin/roles`**
  - **Request Body (`AdminRoleReqDTO`):**
    ```json
    {
      "name": "MODERATOR",
      "permissions": ["VIEW_user", "UPDATE_user", "VIEW_quiz"]
    }
    ```
  - **Response:** `AdminRoleResDTO`
- **`PUT /tomzxyadmin/roles/{roleId}`**
  - **Request Body (`AdminRoleReqDTO`):** (Ghi đè lại toàn bộ permissions của Role này)
  - **Response:** `AdminRoleResDTO`
- **`DELETE /tomzxyadmin/roles/{roleId}`**
  - **Response:** Trống. (Không thể xóa nếu role đang có người dùng)

### 8. Quản Lý Ngân Hàng Câu Hỏi (Question Banks)
- **`GET /tomzxyadmin/question-banks`**
  - **Query params:** `page`, `size`
  - **Response (`PageResDTO<AdminQuestionBankResDTO>`):**
    ```json
    {
      "content": [
        { "id": 1, "ownerName": "datvip2003", "folderCount": 10, "questionCount": 200, "createdAt": "..." }
      ]
    }
    ```

### 9. Quản Lý Thông Báo (Notifications)
- **`GET /tomzxyadmin/notifications`**
  - **Query params:** `page`, `size`
  - **Response (`PageResDTO<AdminNotificationResDTO>`):** Returns `{ id, title, type, targetGroupName, creatorName, createdAt }`
- **`DELETE /tomzxyadmin/notifications/{notificationId}`**
  - **Response:** Trống. Xóa cứng thông báo.
