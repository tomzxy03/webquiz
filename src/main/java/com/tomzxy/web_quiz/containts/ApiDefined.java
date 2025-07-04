package com.tomzxy.web_quiz.containts;

public abstract class ApiDefined {
    public static final String API_PREFIX = "/webQuiz"; // version of api
    /*
     * Base: requestMapping
     * Id: Get by Id, Put, Delete one
     */

    public static final class Auth {
        public static final String BASE = API_PREFIX + "/auth";
        public static final String LOGIN = "/login";
        public static final String LOGOUT = "/logout";
        public static final String TOKEN = "/token";
        public static final String FORGOT_PASSWORD = "/forgot_password";
        public static final String RESET_PASSWORD = "/reset_password";
        public static final String ME = "/me";
        public static final String REGISTER = "/register";
        public static final String REFRESH_TOKEN = "/refresh_token";
        public static final String UPDATE_INFO = "/update_info";
        public static final String CHANGE_PASSWORD = "/change_password";
        public static final String CHANGE_AVATAR = "/avatar";

        public static final String INTROSPECT = "/introspect";

    }

    public static final class User {
        public static final String BASE = API_PREFIX + "/users";
        public static final String ID = "{userId}";
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
        public static final String ID = "{subjectId}";
        public static final String DELETE_MANY = "/delete_many";
        public static final String ADD_CHAPTER = ID + "/chapters";
    }

    public static final class Chapter {
        public static final String BASE = API_PREFIX + "/chapters";
        public static final String ID = "{chapterId}";
        public static final String DELETE_MANY = "/delete_many";
        public static final String ADD_QUESTION = ID + "/add-question";

    }

    public static final class Question {
        public static final String BASE = API_PREFIX + "/questions";
        public static final String ID = "{questionId}";
        public static final String DELETE_MANY = "/delete_many";
        public static final String ADD_ANSWER = ID + "/add-answer";
        public static final String TEXT = "/question_text"; // find by question name
        public static final String CHAPTER = "/chapter_text"; // filter with chapter
        public static final String LEVEL = "/level"; // change level
    }

    public static final class Answer {
        public static final String BASE = API_PREFIX + "/answers";
        public static final String ID = "{answerId}";
        public static final String DELETE_MANY = "/delete_many";
    }

    public static final class Group {
        public static final String BASE = API_PREFIX + "/groups";
        public static final String ID = "{groupId}";
        public static final String DELETE_MANY = "/delete_many";
        public static final String MEMBER = ID + "/members";
        public static final String ADD_MEMBER = ID + "/add-member";
        public static final String NOTIFICATIONS =ID+ "/notifications";

        public static final String NOTIFICATION=NOTIFICATIONS+"/{notificationId}";
        public static final String QUIZ =ID+ "/quizzes";
        public static final String JOIN_QUIZ= QUIZ+"/{quizId}";
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

        public static final String ADD_QUIZ = "{chapterId}";

        public static final String QUESTION = ID + "/questions";
        public static final String DELETE_QUESTION = ID + "/{questionId}";
        public static final String DELETE_QUESTIONS = ID + DELETE_MANY;

    }

    public static final class Quiz_Result {
        public static final String BASE = API_PREFIX + "/quizzes_result";
        public static final String ID = "{quiz_resultId}";
        public static final String DELETE_MANY = "/delete_many";

    }
}
