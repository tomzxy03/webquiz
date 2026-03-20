package com.tomzxy.web_quiz.containts;

public abstract class ApiDefined {
    public static final String API_PREFIX = "/api";

    public static final class Auth {
        public static final String BASE = API_PREFIX + "/auth";
        public static final String LOGIN = "/login";
        public static final String SIGNUP = "/signup";
        public static final String LOGOUT = "/logout";
        public static final String TOKEN = "/token";
        public static final String FORGOT_PASSWORD = "/forgot_password";
        public static final String RESET_PASSWORD = "/reset_password";
        public static final String ME = "/me";
        public static final String REGISTER = "/register";
        public static final String REFRESH = "/refresh";
        public static final String REFRESH_TOKEN = "/refresh_token";
        public static final String UPDATE_INFO = "/{userId}/profile";
        public static final String CHANGE_PASSWORD = "/change_password";
        public static final String CHANGE_AVATAR = "/avatar";
        public static final String INTROSPECT = "/introspect";
    }

    public static final class User {
        public static final String BASE = API_PREFIX + "/users";
        public static final String ID = "{userId}";
        public static final String PROFILE = "{userId}/profile";
        public static final String DELETE_MANY = "/delete_many";
        public static final String QUIZZES = ID + "/quizzes_result";
    }

    public static final class Role {
        public static final String BASE = API_PREFIX + "/roles";
        public static final String ID = "{roleId}";
        public static final String DELETE_MANY = "/delete_many";
    }

    public static final class Subject {
        public static final String BASE = API_PREFIX + "/subjects";
        public static final String QUIZ = "/quiz_count";
        public static final String ID = "{subjectId}";
        public static final String DELETE_MANY = "/delete_many";
    }

    public static final class Question {
        public static final String BASE = API_PREFIX + "/questions";
        public static final String ID = "{questionId}";
        public static final String SUBJECT = "/subject_text";
        public static final String ADD_LIST = "/add_list";
        public static final String DELETE_MANY = "/delete_many";
        public static final String ADD_ANSWER = ID + "/add-answer";
        public static final String TEXT = "/question_text";
        public static final String CHAPTER = "/chapter_text";
        public static final String LEVEL = "/level";
    }

    public static final class Answer {
        public static final String BASE = API_PREFIX + "/answers";
        public static final String ID = "{answerId}";
        public static final String DELETE_MANY = "/delete_many";
    }

    public static final class Group {
        public static final String BASE = API_PREFIX + "/groups";
        public static final String OWNED = "/owned";
        public static final String JOINED = "/joined";
        public static final String ID = "{groupId}";
        public static final String DELETE_MANY = "/delete_many";
        public static final String MEMBER = ID + "/members";
        public static final String MEMBER_ID = ID + "/members/{userId}";
        public static final String MEMBER_ROLE = ID + "/members/{userId}/role";
        public static final String ADD_MEMBER = ID + "/add-member";
        public static final String LEAVE = ID + "/leave";
        public static final String NOTIFICATIONS = ID + "/announcements";
        public static final String NOTIFICATION = NOTIFICATIONS + "/{announcementId}";
        public static final String ADD_NOTIFICATION = ID + "/add-announcement";
        public static final String UPDATE_NOTIFICATION = ID + "/{announcementId}/update";
        public static final String DELETE_NOTIFICATION = ID + "/{announcementId}/delete";
        public static final String QUIZ = ID + "/quizzes";
        public static final String QUIZ_ID = QUIZ + "/{quizId}";
        public static final String UPDATE_QUIZ = QUIZ + "/{quizId}/update";
        public static final String DELETE_QUIZ = QUIZ + "/{quizId}/delete";
        public static final String RESOURCES = ID + "/resources";
        public static final String RESOURCE_ID = RESOURCES + "/{resourceId}";
        public static final String BY_USER = "/user/{userId}";
    }

    public static final class Notification {
        public static final String BASE = API_PREFIX + "/notifications";
        public static final String ID = "{notificationId}";
        public static final String DELETE_MANY = "/delete_many";
        public static final String CHANGE_STATUS = ID + "/status";
    }

    public static final class Quiz {
        public static final String BASE = API_PREFIX + "/quizzes";
        public static final String ID = "{quizId}";
        public static final String DELETE_MANY = "/delete_many";
        public static final String ADD_QUIZ = "/{chapterId}";
        public static final String QUESTION = ID + "/questions";
        public static final String ADD_QUESTION = ID + "/add_list";
        public static final String UPDATE_QUESTION = ID + "/update_list"; 
        public static final String DELETE_QUESTION = ID + "/{questionId}";
        public static final String DELETE_QUESTIONS = ID + DELETE_MANY;
        public static final String LATEST = "/latest";
        public static final String FILTER = "/filter";
        public static final String SUBJECT_FILTER = "/subject/{subjectId}";
        public static final String SUBJECTS_LIST = "/subjects";
        public static final String STATISTICS = ID + "/statistics";
        public static final String TRENDING = "/trending";
    }

    public static final class QuizInstance {
        public static final String BASE = API_PREFIX + "/quiz-instances";
        public static final String ID = "{instanceId}";
        public static final String DELETE = ID + "/delete";
        public static final String SUBMIT = ID + "/submit";
        public static final String RESULT = ID + "/result";
        public static final String START = "/start";
        public static final String STATE = ID + "/state";
        public static final String ANSWERS = ID + "/answer";
        public static final String CHECK_ELIGIBILITY = "/check-eligibility";
    }

    public static final class QuizUserResponse {
        public static final String BASE = API_PREFIX + "/quiz_user_responses";
        public static final String ID = "{responseId}";
        public static final String DELETE_MANY = "/delete_many";
        public static final String SUBMIT = "/submit";
        public static final String ANSWER = ID + "/answer";
        public static final String SKIP = ID + "/skip";
        public static final String USER = "/user/{userId}";
        public static final String USER_PAGE = USER + "/page";
        public static final String QUIZ_INSTANCE = "/quiz-instance/{quizInstanceId}";
        public static final String QUIZ_INSTANCE_USER = QUIZ_INSTANCE + "/user/{userId}";
        public static final String CORRECT = "/correct";
        public static final String CORRECT_PAGE = CORRECT + "/page";
        public static final String TIME_RANGE = "/time-range";
        public static final String DATE_RANGE = "/date-range";
        public static final String RECENT = "/recent";
        public static final String SKIPPED = "/skipped";
        public static final String ANSWERED = "/answered";

    }

    public static final class QuizResult {
        public static final String BASE = API_PREFIX + "/quizzes_result";
        public static final String ID = "{quiz_resultId}";
        public static final String DELETE_MANY = "/delete_many";
    }

    public static final class Dashboard {
        public static final String BASE = API_PREFIX + "/dashboard";
        public static final String BY_USER = "/{userId}";
    }

    public static final class Statistics {
        public static final String BASE = API_PREFIX + "/statistics";
        public static final String USER = "/user/{userId}";
        public static final String QUIZ = "/quiz/{quizId}";
        public static final String GLOBAL = "/dashboard";
    }

    public static final class Files {
        public static final String BASE = API_PREFIX + "/files";
        public static final String UPLOAD = "/upload";
    }

    public static final class QuestionBank {
        public static final String ROOT = API_PREFIX + "/question-banks";
        public static final String MY_BANK = "/my-bank";
        public static final String LIST = "";
        public static final String BY_OWNER_ID = "/{ownerId}";
        public static final String CREATE = "";
        public static final String UPDATE = "";
        public static final String DELETE = "";
    }

    public static final class Folder {
        public static final String ROOT = API_PREFIX + "/folders";
        public static final String LIST = "";
        public static final String CREATE = "";
        public static final String BY_ID = "/{folderId}";
        public static final String ROOT_FOLDERS = "/root";
        public static final String SUBFOLDERS = "/{parentFolderId}/subfolders";
        public static final String TREE = "/tree";
        public static final String MOVE = "/{folderId}/move";
    }

    public static final class QuestionFolder {
        public static final String ROOT = API_PREFIX + "/questions-folder";
        public static final String LIST = "";
        public static final String CREATE = "";
        public static final String BY_ID = "/{questionId}";
        public static final String BY_FOLDER = "/folder/{folderId}";
        public static final String ROOT_LEVEL = "/root";
        public static final String TREE = "/tree";
        public static final String MOVE_TO_FOLDER = "/{questionId}/move";
        public static final String MOVE_TO_ROOT = "/{questionId}/move-root";
    }
}
