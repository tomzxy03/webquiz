# WebQuiz Project Requirements
- Triển khai hệ thống bằng Spring boot. 
1. Business Requirement
Role được chia thành USER,ADMIN,HOST
1.1. User & Authentication

User có thể đăng ký / đăng nhập (email, social login).
User có thể có role:
HOST (chủ class, tạo quiz, quản lý member).
USER (tham gia quiz).
Hỗ trợ refresh token, quản lý session.

1.2. Mô tả tổng quan về yêu cầu đặc biệt:

```
Actor chính trong hệ thống có 2 loại: ADMIN và USER.
+ User khi khởi tạo nhóm riêng sẽ được cấp quyền Host.
+ Bản thân mỗi User sẽ có nhiều class.
+ Mỗi Host có một nơi lưu trữ question bank ( trong question bank có thể chia các folder)
+ Question bank có thể được sử dụng bất kì trong nhóm nào ( không bị phụ thuộc)
+ Question có thể thêm thông qua import hoặc tự custom.
+ Bản thân quiz instance được tạo mỗi khi user join quiz.
+ Kết quả được đưa về hệ thống theo từng path mỗi khoảng thời gian. 
Và được lưu tại UserQuizResponse.
```

1.2. Quiz Management
- Host/Admin có thể tạo quiz từ question bank (hoặc tự custom).
- Quiz có các option:
```
Shuffle questions/answers.
Allow review after submit.
Time limit 
Show score immediately or delayed.
```
- Quiz có question types: MCQ (single/multiple), True/False, Essay (open text), Image-based.
- *Quiz được snapshot khi publish để đảm bảo toàn vẹn (kể cả khi bank thay đổi).*

1.3. Quiz Instance / Attempt
Khi user join quiz → tạo QuizInstance.
Một user có thể attempt nhiều lần (theo config).
Trong attempt:
Autosave response mỗi 3-5 phút (cache Redis).
Flush vào DB mỗi 15-20 phút.
Khi submit → flush toàn bộ data tới db → lock quiz instance + chấm điểm.

Chấm điểm có thể:
Immediate scoring (MCQ/True-False).
Deferred scoring (Essay).

1.4. Quiz User Response
Lưu câu trả lời user: selected option, timestamp, correct flag, earned point.
Có thể lấy lại response để review.

1.5. Result & Analytics
Sau khi submit: sinh QuizResult.
Thống kê:
Điểm trung bình.
Leaderboard (per quiz, per class).
Tỷ lệ pass/fail.
Phân tích câu hỏi (câu nào nhiều user sai).

1.6. Group/Class
User có thể tạo class (group).
Host có thể:
Mời/thêm thành viên.
Tạo quiz private cho class.
Gửi thông báo (announcement).

1.7. Notification
2 loại notification:
Global (toàn hệ thống).
Class-specific (chỉ trong class).
Notification có thể: quiz upcoming, quiz result available, announcement từ host.
User có thể đánh dấu read/unread.

2.1. Auth
POST /auth/register
POST /auth/login
POST /auth/refresh
POST /auth/logout


2.2. User
GET /users/me (profile).
PUT /users/me (update info).

2.3. Quiz
POST /quizzes (create quiz, only HOST/ADMIN).
GET /quizzes/public (list public quiz).
GET /quizzes/{quizId} (quiz detail).
PUT /quizzes/{quizId} (update quiz, HOST/ADMIN).
DELETE /quizzes/{quizId}.

2.4. Quiz Instance / Attempt
POST /quizzes/{quizId}/instances (start attempt).
GET /instances/{instanceId} (get attempt detail).
POST /instances/{instanceId}/submit (submit quiz).

2.5. Response
POST /instances/{instanceId}/responses (save response, autosave).
GET /instances/{instanceId}/responses (get all responses for review).

2.6. Result
GET /instances/{instanceId}/result (get result of attempt).
GET /quizzes/{quizId}/results (all results of quiz, for HOST).
GET /users/{userId}/results (all results of user).

2.7. Class / Group
POST /classes (create class).
GET /classes (list classes user joined).
POST /classes/{classId}/members (add member).
GET /classes/{classId}/members.
POST /classes/{classId}/quizzes (create quiz in class)

2.8. Notification
POST /notifications (create global notification, only ADMIN).
POST /classes/{classId}/notifications (create class notification, only HOST).
GET /notifications (list notifications user can see).
PUT /notifications/{id}/read (mark as read).


DB: PostgreSQL (JSONB cho options, index GIN).
Cache: Redis (autosave response, session).
Async: Kafka/RabbitMQ cho chấm điểm + notification.
Scale:
Response service có thể scale riêng.
Result/Analytics có thể sync sang ClickHouse/Elastic để query nhanh.
API Contract: REST cho client, có thể gRPC nội bộ service.



Work flow:

- User join quiz

User  ──(join)──▶ Quiz
   │
   └──▶ QuizInstance (được tạo mới)
           │
           ├── reference Quiz (snapshot question/answer + options)
           └── gắn với User (ai đang thi)

-processing quiz

User ──▶ QuizInstance
           │
           └──▶ QuizAttempt (mỗi lần thi)
                   │
                   └── status: IN_PROGRESS

- User response:



QuizAttempt
   │
   ├── (autosave every 10–60s) ──▶ Redis (cache)
   │                                    │
   │                                    └── flush 3–5m → DB
   │
   └──▶ QuizUserResponse (lưu từng câu trả lời)
           - questionId
           - selectedOption
           - timestamp
           - isCorrect
           - earnedPoint

- Submit quiz:

QuizAttempt (status: SUBMITTED)
      │
      ├──▶ lấy tất cả QuizUserResponse
      │
      └──▶ tính điểm → QuizResult
               - totalPoint
               - earnedPoint
               - status (pass/fail)
               - duration

- notification:
Class
   ├── has many Users
   ├── has many Quizzes
   └── can broadcast Notification
            ├── to Class (all members)
            └── to specific User
