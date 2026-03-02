I will give you some follows. Depending on the follows, you will update the code.

1. User login, register, logout, refresh token
2. User can create group for themselves so orther user can join group ( server will check code_invite in group). When user create group, they will be added role Host and manage those group. Host can add or remove user in group, and manage quiz, notification in group.
3. Host can create quiz in our group and that quiz just allow user in group to join. If anyone want to join quiz, they need to join group first if user access to quiz group, server will check user is being the group ortherwise return forbidden.
4. Admin can manage user, group, quiz, notification. Quiz is created by Admin will make to public and everyone can join it.
5. So quiz will be separated into 2 type: public quiz and group quiz. In addition if user not login, they is guest can join quiz public but can't join group quiz.
6. With each user, they have a question bank for themselves. User can create question in their question bank and use it to create quiz. In question bank, question can group into a folder and folder can be nested. User can create, update, delete, move question in their question bank. User can create, update, delete, move folder in their question bank. User can create, update, delete, move folder in their question bank.
7. When quiz has been publiced, server must be snapshot all information into QuizQuestionSnapshot,QuestionSnapshot,AnswerSnapshot to data presenrvation. If admin or host update quiz, update version and create new snapshot.
8. When user join quiz, server will create QuizInstance with the fields nessesary. Implement validation for each field, shuffle question and answer if needed depending on quiz config.
9. If shuffle question and answer is enabled, server will shuffle question and answer then save into QuizInstanceQuestion and QuizInstanceAnswer within orderIndex each question and answer.
10. During user exam quiz, server will use Redis to store user answer and quiz state. When user submit quiz, you can push hash redis to server, server will caculate answer, update field in QuizInstance and save user answer and quiz state into database.
11. About guest exam quiz public, it still same user exam quiz but userId is null and you can cron job to remove guest quiz instance after 1 day.


About structure, use Mapstruct to map DTO, get data from db need to ensure use pageable if have large data, use projection to get only needed data.

avoid problems and issuas about N+1 or something else.

should test features before deloying or adding to system.
Ensure everything is working as expected and can scale in future.
