{
  "apiBaseUrl": "http://localhost:8080/api",
  "responseWrapper": {
    "description": "All API responses are wrapped in this structure",
    "structure": {
      "code": "number (0 = success, non-zero = error)",
      "message": "string (human readable message)",
      "items": "T (actual payload data)"
    }
  },
  "authentication": {
    "header": "Authorization: Bearer {token}",
    "endpoints": [
      {
        "method": "POST",
        "path": "/auth/login",
        "description": "User login",
        "request": {
          "body": {
            "email": "string",
            "password": "string"
          }
        },
        "response": {
          "items": {
            "token": "string",
            "user": {
              "id": "string",
              "email": "string",
              "username": "string",
              "fullName": "string (optional)",
              "avatar": "string (optional)",
              "createdAt": "Date",
              "updatedAt": "Date"
            },
            "expiresIn": "number"
          }
        }
      },
      {
        "method": "POST",
        "path": "/auth/signup",
        "description": "User registration",
        "request": {
          "body": {
            "email": "string",
            "password": "string",
            "username": "string",
            "fullName": "string (optional)"
          }
        },
        "response": {
          "items": {
            "token": "string",
            "user": "User object",
            "expiresIn": "number"
          }
        }
      },
      {
        "method": "POST",
        "path": "/auth/logout",
        "description": "User logout",
        "request": {
          "body": null
        },
        "response": {
          "items": {
            "success": "boolean"
          }
        }
      },
      {
        "method": "POST",
        "path": "/auth/refresh",
        "description": "Refresh authentication token",
        "request": {
          "body": {
            "refreshToken": "string"
          }
        },
        "response": {
          "items": {
            "token": "string",
            "user": "User object",
            "expiresIn": "number"
          }
        }
      },
      {
        "method": "GET",
        "path": "/auth/me",
        "description": "Get current authenticated user",
        "request": {
          "body": null
        },
        "response": {
          "items": {
            "id": "string",
            "email": "string",
            "username": "string",
            "fullName": "string (optional)",
            "avatar": "string (optional)",
            "createdAt": "Date",
            "updatedAt": "Date"
          }
        }
      }
    ]
  },
  "users": {
    "endpoints": [
      {
        "method": "GET",
        "path": "/users",
        "description": "Get all users (with optional filters)",
        "request": {
          "queryParams": {
            "search": "string (optional)",
            "role": "string (optional)"
          }
        },
        "response": {
          "items": [
            {
              "id": "string",
              "email": "string",
              "username": "string",
              "fullName": "string (optional)",
              "avatar": "string (optional)",
              "createdAt": "Date",
              "updatedAt": "Date"
            }
          ]
        }
      },
      {
        "method": "GET",
        "path": "/users/{id}",
        "description": "Get user by ID",
        "request": {
          "pathParams": {
            "id": "number"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "email": "string",
            "username": "string",
            "fullName": "string (optional)",
            "avatar": "string (optional)",
            "createdAt": "Date",
            "updatedAt": "Date"
          }
        }
      },
      {
        "method": "GET",
        "path": "/users/{id}/profile",
        "description": "Get user profile",
        "request": {
          "pathParams": {
            "id": "number"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "email": "string",
            "username": "string",
            "fullName": "string (optional)",
            "avatar": "string (optional)",
            "bio": "string (optional)",
            "isPublicProfile": "boolean",
            "totalQuizzesTaken": "number",
            "totalPoints": "number",
            "createdAt": "Date",
            "updatedAt": "Date"
          }
        }
      },
      {
        "method": "PUT",
        "path": "/users/{id}/profile",
        "description": "Update user profile",
        "request": {
          "pathParams": {
            "id": "number"
          },
          "body": {
            "fullName": "string (optional)",
            "bio": "string (optional)",
            "isPublicProfile": "boolean (optional)",
            "avatar": "string (optional)"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "email": "string",
            "username": "string",
            "fullName": "string (optional)",
            "avatar": "string (optional)",
            "bio": "string (optional)",
            "isPublicProfile": "boolean",
            "totalQuizzesTaken": "number",
            "totalPoints": "number",
            "createdAt": "Date",
            "updatedAt": "Date"
          }
        }
      }
    ]
  },
  "subjects": {
    "endpoints": [
      {
        "method": "GET",
        "path": "/subjects",
        "description": "Get all subjects",
        "request": {
          "body": null
        },
        "response": {
          "items": [
            {
              "id": "number",
              "subjectName": "string",
              "description": "string (optional)"
            }
          ]
        }
      },
      {
        "method": "GET",
        "path": "/subjects/{id}",
        "description": "Get subject by ID",
        "request": {
          "pathParams": {
            "id": "number"
          }
        },
        "response": {
          "items": {
            "id": "number",
            "subjectName": "string",
            "description": "string (optional)"
          }
        }
      },
      {
        "method": "POST",
        "path": "/subjects",
        "description": "Create new subject",
        "request": {
          "body": {
            "subjectName": "string",
            "description": "string (optional)"
          }
        },
        "response": {
          "items": {
            "id": "number",
            "subjectName": "string",
            "description": "string (optional)"
          }
        }
      },
      {
        "method": "PUT",
        "path": "/subjects/{id}",
        "description": "Update subject",
        "request": {
          "pathParams": {
            "id": "number"
          },
          "body": {
            "subjectName": "string",
            "description": "string (optional)"
          }
        },
        "response": {
          "items": {
            "id": "number",
            "subjectName": "string",
            "description": "string (optional)"
          }
        }
      },
      {
        "method": "DELETE",
        "path": "/subjects/{id}",
        "description": "Delete subject",
        "request": {
          "pathParams": {
            "id": "number"
          }
        },
        "response": {
          "items": null
        }
      }
    ]
  },
  "quizzes": {
    "endpoints": [
      {
        "method": "GET",
        "path": "/quizzes",
        "description": "Get all quizzes (with optional filters)",
        "request": {
          "queryParams": {
            "subject": "string (optional)",
            "minQuestions": "number (optional)",
            "maxQuestions": "number (optional)",
            "minDuration": "number (optional)",
            "maxDuration": "number (optional)",
            "isPublic": "boolean (optional)",
            "search": "string (optional)",
            "tags": "string[] (optional)"
          }
        },
        "response": {
          "items": [
            {
              "id": "string",
              "title": "string",
              "description": "string (optional)",
              "subject": "string",
              "questionCount": "number",
              "estimatedTime": "number",
              "isPublic": "boolean",
              "visibility": "string (optional: PUBLIC|GROUP|DRAFT)",
              "attemptCount": "number (optional)",
              "creatorId": "string (optional)",
              "createdAt": "Date (optional)",
              "updatedAt": "Date (optional)",
              "tags": "string[] (optional)"
            }
          ]
        }
      },
      {
        "method": "GET",
        "path": "/quizzes/{id}",
        "description": "Get quiz detail by ID",
        "request": {
          "pathParams": {
            "id": "number"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "title": "string",
            "description": "string (optional)",
            "subject": "string",
            "questionCount": "number",
            "estimatedTime": "number",
            "isPublic": "boolean",
            "visibility": "string (optional)",
            "attemptCount": "number (optional)",
            "creatorId": "string (optional)",
            "createdAt": "Date (optional)",
            "updatedAt": "Date (optional)",
            "tags": "string[] (optional)",
            "questions": [
              {
                "id": "string",
                "content": "string",
                "type": "string",
                "difficulty": "string",
                "bankId": "string",
                "folderId": "string (optional)",
                "explanation": "string (optional)",
                "tags": "string[] (optional)",
                "createdAt": "Date (optional)",
                "updatedAt": "Date (optional)"
              }
            ],
            "settings": {
              "pointsPerQuestion": "number (optional)",
              "randomizeQuestions": "boolean",
              "randomizeOptions": "boolean",
              "showCorrectAnswers": "boolean",
              "maxAttempts": "number (optional)",
              "passingScore": "number (optional)",
              "timeLimit": "number (optional)"
            },
            "creator": "User object (optional)"
          }
        }
      },
      {
        "method": "GET",
        "path": "/quizzes/subject/{subject}",
        "description": "Get quizzes by subject",
        "request": {
          "pathParams": {
            "subject": "string"
          }
        },
        "response": {
          "items": [
            {
              "id": "string",
              "title": "string",
              "description": "string (optional)",
              "subject": "string",
              "questionCount": "number",
              "estimatedTime": "number",
              "difficulty": "string",
              "isPublic": "boolean",
              "visibility": "string (optional)",
              "attemptCount": "number (optional)",
              "creatorId": "string (optional)",
              "createdAt": "Date (optional)",
              "updatedAt": "Date (optional)",
              "tags": "string[] (optional)"
            }
          ]
        }
      },
      {
        "method": "GET",
        "path": "/quizzes/popular",
        "description": "Get popular quizzes",
        "request": {
          "body": null
        },
        "response": {
          "items": [
            {
              "id": "string",
              "title": "string",
              "description": "string (optional)",
              "subject": "string",
              "questionCount": "number",
              "estimatedTime": "number",
              "difficulty": "string",
              "isPublic": "boolean",
              "visibility": "string (optional)",
              "attemptCount": "number (optional)",
              "creatorId": "string (optional)",
              "createdAt": "Date (optional)",
              "updatedAt": "Date (optional)",
              "tags": "string[] (optional)"
            }
          ]
        }
      },
      {
        "method": "GET",
        "path": "/quizzes/latest",
        "description": "Get latest quizzes",
        "request": {
          "body": null
        },
        "response": {
          "items": [
            {
              "id": "string",
              "title": "string",
              "description": "string (optional)",
              "subject": "string",
              "questionCount": "number",
              "estimatedTime": "number",
              "difficulty": "string",
              "isPublic": "boolean",
              "visibility": "string (optional)",
              "attemptCount": "number (optional)",
              "creatorId": "string (optional)",
              "createdAt": "Date (optional)",
              "updatedAt": "Date (optional)",
              "tags": "string[] (optional)"
            }
          ]
        }
      },
      {
        "method": "GET",
        "path": "/quizzes/search",
        "description": "Search quizzes",
        "request": {
          "queryParams": {
            "search": "string",
            "tags": "string[] (optional)"
          }
        },
        "response": {
          "items": [
            {
              "id": "string",
              "title": "string",
              "description": "string (optional)",
              "subject": "string",
              "questionCount": "number",
              "estimatedTime": "number",
              "difficulty": "string",
              "isPublic": "boolean",
              "visibility": "string (optional)",
              "attemptCount": "number (optional)",
              "creatorId": "string (optional)",
              "createdAt": "Date (optional)",
              "updatedAt": "Date (optional)",
              "tags": "string[] (optional)"
            }
          ]
        }
      },
      {
        "method": "GET",
        "path": "/quizzes/subjects",
        "description": "Get all distinct subject names",
        "request": {
          "body": null
        },
        "response": {
          "items": [
            "string"
          ]
        }
      },
      {
        "method": "POST",
        "path": "/quizzes",
        "description": "Create new quiz",
        "request": {
          "body": {
            "title": "string",
            "description": "string (optional)",
            "subject": "string",
            "estimatedTime": "number",
            "difficulty": "string (easy|medium|hard)",
            "isPublic": "boolean",
            "questions": [
              {
                "text": "string",
                "type": "string",
                "points": "number",
                "explanation": "string (optional)",
                "options": [
                  {
                    "text": "string",
                    "isCorrect": "boolean"
                  }
                ]
              }
            ],
            "settings": {
              "pointsPerQuestion": "number (optional)",
              "randomizeQuestions": "boolean",
              "randomizeOptions": "boolean",
              "showCorrectAnswers": "boolean",
              "maxAttempts": "number (optional)",
              "passingScore": "number (optional)",
              "timeLimit": "number (optional)"
            }
          }
        },
        "response": {
          "items": {
            "id": "string",
            "title": "string",
            "description": "string (optional)",
            "subject": "string",
            "questionCount": "number",
            "estimatedTime": "number",
            "difficulty": "string",
            "isPublic": "boolean",
            "visibility": "string (optional)",
            "questions": "Question[]",
            "settings": "QuizSettings object",
            "creator": "User object (optional)"
          }
        }
      },
      {
        "method": "PUT",
        "path": "/quizzes/{id}",
        "description": "Update quiz",
        "request": {
          "pathParams": {
            "id": "number"
          },
          "body": {
            "title": "string (optional)",
            "description": "string (optional)",
            "subject": "string (optional)",
            "estimatedTime": "number (optional)",
            "difficulty": "string (optional)",
            "isPublic": "boolean (optional)",
            "questions": "CreateQuestionRequest[] (optional)",
            "settings": "QuizSettings (optional)"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "title": "string",
            "description": "string (optional)",
            "subject": "string",
            "questionCount": "number",
            "estimatedTime": "number",
            "difficulty": "string",
            "isPublic": "boolean",
            "visibility": "string (optional)",
            "questions": "Question[]",
            "settings": "QuizSettings object",
            "creator": "User object (optional)"
          }
        }
      },
      {
        "method": "DELETE",
        "path": "/quizzes/{id}",
        "description": "Delete quiz",
        "request": {
          "pathParams": {
            "id": "number"
          }
        },
        "response": {
          "items": null
        }
      },
      {
        "method": "GET",
        "path": "/quizzes/{id}/statistics",
        "description": "Get quiz statistics",
        "request": {
          "pathParams": {
            "id": "number"
          }
        },
        "response": {
          "items": {
            "quizId": "string",
            "totalAttempts": "number",
            "averageScore": "number",
            "averageTime": "number",
            "completionRate": "number",
            "difficultyRating": "number"
          }
        }
      }
    ]
  },
  "questions": {
    "endpoints": [
      {
        "method": "GET",
        "path": "/quizzes/{quizId}/questions",
        "description": "Get all questions for a quiz",
        "request": {
          "pathParams": {
            "quizId": "string"
          }
        },
        "response": {
          "items": [
            {
              "id": "string",
              "content": "string",
              "type": "string",
              "difficulty": "string",
              "bankId": "string",
              "folderId": "string (optional)",
              "explanation": "string (optional)",
              "tags": "string[] (optional)",
              "createdAt": "Date (optional)",
              "updatedAt": "Date (optional)"
            }
          ]
        }
      },
      {
        "method": "GET",
        "path": "/quizzes/{quizId}/questions/{questionId}",
        "description": "Get question by ID",
        "request": {
          "pathParams": {
            "quizId": "string",
            "questionId": "string"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "content": "string",
            "type": "string",
            "difficulty": "string",
            "bankId": "string",
            "folderId": "string (optional)",
            "explanation": "string (optional)",
            "tags": "string[] (optional)",
            "createdAt": "Date (optional)",
            "updatedAt": "Date (optional)"
          }
        }
      },
      {
        "method": "POST",
        "path": "/quizzes/{quizId}/questions",
        "description": "Create question for a quiz",
        "request": {
          "pathParams": {
            "quizId": "string"
          },
          "body": {
            "text": "string",
            "type": "string",
            "points": "number",
            "explanation": "string (optional)",
            "options": [
              {
                "text": "string",
                "isCorrect": "boolean"
              }
            ]
          }
        },
        "response": {
          "items": {
            "id": "string",
            "content": "string",
            "type": "string",
            "difficulty": "string",
            "bankId": "string",
            "folderId": "string (optional)",
            "explanation": "string (optional)",
            "tags": "string[] (optional)",
            "createdAt": "Date (optional)",
            "updatedAt": "Date (optional)"
          }
        }
      },
      {
        "method": "PUT",
        "path": "/quizzes/{quizId}/questions/{questionId}",
        "description": "Update question",
        "request": {
          "pathParams": {
            "quizId": "string",
            "questionId": "string"
          },
          "body": {
            "text": "string (optional)",
            "type": "string (optional)",
            "points": "number (optional)",
            "explanation": "string (optional)",
            "options": [
              {
                "text": "string",
                "isCorrect": "boolean"
              }
            ]
          }
        },
        "response": {
          "items": {
            "id": "string",
            "content": "string",
            "type": "string",
            "difficulty": "string",
            "bankId": "string",
            "folderId": "string (optional)",
            "explanation": "string (optional)",
            "tags": "string[] (optional)",
            "createdAt": "Date (optional)",
            "updatedAt": "Date (optional)"
          }
        }
      },
      {
        "method": "DELETE",
        "path": "/quizzes/{quizId}/questions/{questionId}",
        "description": "Delete question",
        "request": {
          "pathParams": {
            "quizId": "string",
            "questionId": "string"
          }
        },
        "response": {
          "items": null
        }
      }
    ]
  },
  "attempts": {
    "endpoints": [
      {
        "method": "GET",
        "path": "/attempts",
        "description": "Get all exam attempts",
        "request": {
          "queryParams": {
            "userId": "string (optional)",
            "quizId": "string (optional)"
          }
        },
        "response": {
          "items": [
            {
              "id": "string",
              "quizId": "string",
              "userId": "string",
              "quiz": "Quiz object (optional)",
              "title": "string (optional)",
              "date": "string (optional)",
              "score": "string",
              "totalQuestions": "number",
              "correctAnswers": "number",
              "points": "number (optional)",
              "duration": "string",
              "completedAt": "Date",
              "badges": "string[]",
              "badgeColors": "string[]"
            }
          ]
        }
      },
      {
        "method": "GET",
        "path": "/attempts/{id}",
        "description": "Get exam attempt detail by ID",
        "request": {
          "pathParams": {
            "id": "number"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "quizId": "string",
            "userId": "string",
            "quiz": "Quiz object (optional)",
            "title": "string (optional)",
            "date": "string (optional)",
            "score": "string",
            "totalQuestions": "number",
            "correctAnswers": "number",
            "points": "number (optional)",
            "duration": "string",
            "completedAt": "Date",
            "badges": "string[]",
            "badgeColors": "string[]",
            "answers": [
              {
                "id": "string",
                "attemptId": "string",
                "questionId": "string",
                "question": "Question object (optional)",
                "selectedOptionIds": "string[]",
                "answerText": "string (optional)",
                "isCorrect": "boolean",
                "pointsEarned": "number"
              }
            ]
          }
        }
      },
      {
        "method": "GET",
        "path": "/attempts/user/{userId}",
        "description": "Get all attempts by user",
        "request": {
          "pathParams": {
            "userId": "string"
          }
        },
        "response": {
          "items": [
            {
              "id": "string",
              "quizId": "string",
              "userId": "string",
              "quiz": "Quiz object (optional)",
              "title": "string (optional)",
              "date": "string (optional)",
              "score": "string",
              "totalQuestions": "number",
              "correctAnswers": "number",
              "points": "number (optional)",
              "duration": "string",
              "completedAt": "Date",
              "badges": "string[]",
              "badgeColors": "string[]"
            }
          ]
        }
      },
      {
        "method": "GET",
        "path": "/attempts/quiz/{quizId}/user/{userId}",
        "description": "Get attempts for specific quiz and user",
        "request": {
          "pathParams": {
            "quizId": "string",
            "userId": "string"
          }
        },
        "response": {
          "items": [
            {
              "id": "string",
              "quizId": "string",
              "userId": "string",
              "quiz": "Quiz object (optional)",
              "title": "string (optional)",
              "date": "string (optional)",
              "score": "string",
              "totalQuestions": "number",
              "correctAnswers": "number",
              "points": "number (optional)",
              "duration": "string",
              "completedAt": "Date",
              "badges": "string[]",
              "badgeColors": "string[]"
            }
          ]
        }
      },
      {
        "method": "POST",
        "path": "/attempts",
        "description": "Create new exam attempt",
        "request": {
          "body": {
            "quizId": "string",
            "answers": [
              {
                "questionId": "string",
                "selectedOptionIds": "string[]",
                "answerText": "string (optional)"
              }
            ],
            "startedAt": "Date",
            "completedAt": "Date"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "quizId": "string",
            "userId": "string",
            "quiz": "Quiz object (optional)",
            "title": "string (optional)",
            "date": "string (optional)",
            "score": "string",
            "totalQuestions": "number",
            "correctAnswers": "number",
            "points": "number (optional)",
            "duration": "string",
            "completedAt": "Date",
            "badges": "string[]",
            "badgeColors": "string[]",
            "answers": "UserAnswer[]"
          }
        }
      },
      {
        "method": "DELETE",
        "path": "/attempts/{id}",
        "description": "Delete exam attempt",
        "request": {
          "pathParams": {
            "id": "number"
          }
        },
        "response": {
          "items": null
        }
      },
      {
        "method": "GET",
        "path": "/attempts/user/{userId}/statistics",
        "description": "Get user statistics",
        "request": {
          "pathParams": {
            "userId": "string"
          }
        },
        "response": {
          "items": {
            "userId": "string",
            "totalQuizzesTaken": "number",
            "totalPoints": "number",
            "averageScore": "number",
            "totalTimeSpent": "number",
            "quizzesBySubject": "object (key-value pairs)",
            "quizzesByDifficulty": "object (key-value pairs)",
            "recentActivity": "ExamAttempt[]"
          }
        }
      }
    ]
  },
  "groups": {
    "endpoints": [
      {
        "method": "GET",
        "path": "/groups",
        "description": "Get all groups (with optional filters)",
        "request": {
          "queryParams": {
            "search": "string (optional)",
            "role": "string (optional: OWNER|ADMIN|MEMBER)"
          }
        },
        "response": {
          "items": [
            {
              "id": "string",
              "name": "string",
              "description": "string (optional)",
              "memberCount": "number",
              "role": "string (optional: OWNER|ADMIN|MEMBER)",
              "creatorId": "string",
              "createdAt": "Date",
              "updatedAt": "Date"
            }
          ]
        }
      },
      {
        "method": "GET",
        "path": "/groups/{id}",
        "description": "Get group detail by ID",
        "request": {
          "pathParams": {
            "id": "number"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "name": "string",
            "description": "string (optional)",
            "memberCount": "number",
            "role": "string (optional)",
            "creatorId": "string",
            "createdAt": "Date",
            "updatedAt": "Date",
            "members": [
              {
                "id": "string",
                "groupId": "string",
                "userId": "string",
                "user": "User object (optional)",
                "name": "string",
                "role": "string",
                "avatar": "string (optional)",
                "joinedAt": "Date"
              }
            ],
            "quizzes": "Quiz[]",
            "announcements": [
              {
                "id": "string",
                "groupId": "string",
                "authorId": "string",
                "author": "User object (optional)",
                "title": "string",
                "content": "string",
                "isPinned": "boolean",
                "createdAt": "Date",
                "updatedAt": "Date"
              }
            ]
          }
        }
      },
      {
        "method": "GET",
        "path": "/groups/user/{userId}",
        "description": "Get groups by user",
        "request": {
          "pathParams": {
            "userId": "string"
          }
        },
        "response": {
          "items": [
            {
              "id": "string",
              "name": "string",
              "description": "string (optional)",
              "memberCount": "number",
              "role": "string (optional)",
              "creatorId": "string",
              "createdAt": "Date",
              "updatedAt": "Date"
            }
          ]
        }
      },
      {
        "method": "POST",
        "path": "/groups",
        "description": "Create new group",
        "request": {
          "body": {
            "name": "string",
            "description": "string (optional)"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "name": "string",
            "description": "string (optional)",
            "memberCount": "number",
            "role": "string",
            "creatorId": "string",
            "createdAt": "Date",
            "updatedAt": "Date",
            "members": "GroupMember[]",
            "quizzes": "Quiz[]",
            "announcements": "Announcement[]"
          }
        }
      },
      {
        "method": "PUT",
        "path": "/groups/{id}",
        "description": "Update group",
        "request": {
          "pathParams": {
            "id": "number"
          },
          "body": {
            "name": "string (optional)",
            "description": "string (optional)"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "name": "string",
            "description": "string (optional)",
            "memberCount": "number",
            "role": "string (optional)",
            "creatorId": "string",
            "createdAt": "Date",
            "updatedAt": "Date",
            "members": "GroupMember[]",
            "quizzes": "Quiz[]",
            "announcements": "Announcement[]"
          }
        }
      },
      {
        "method": "DELETE",
        "path": "/groups/{id}",
        "description": "Delete group",
        "request": {
          "pathParams": {
            "id": "number"
          }
        },
        "response": {
          "items": null
        }
      },
      {
        "method": "GET",
        "path": "/groups/{groupId}/members",
        "description": "Get group members",
        "request": {
          "pathParams": {
            "groupId": "string"
          }
        },
        "response": {
          "items": [
            {
              "id": "string",
              "groupId": "string",
              "userId": "string",
              "user": "User object (optional)",
              "name": "string",
              "role": "string",
              "avatar": "string (optional)",
              "joinedAt": "Date"
            }
          ]
        }
      },
      {
        "method": "POST",
        "path": "/groups/{groupId}/members",
        "description": "Add member to group (invite or add directly)",
        "request": {
          "pathParams": {
            "groupId": "string"
          },
          "body": {
            "email": "string (optional, if inviting by email)",
            "userId": "string (optional, if adding by userId)",
            "role": "string (optional: OWNER|ADMIN|MEMBER)"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "groupId": "string",
            "userId": "string",
            "user": "User object (optional)",
            "name": "string",
            "role": "string",
            "avatar": "string (optional)",
            "joinedAt": "Date"
          }
        }
      },
      {
        "method": "DELETE",
        "path": "/groups/{groupId}/members/{userId}",
        "description": "Remove member from group",
        "request": {
          "pathParams": {
            "groupId": "string",
            "userId": "string"
          }
        },
        "response": {
          "items": null
        }
      },
      {
        "method": "PUT",
        "path": "/groups/{groupId}/members/{userId}/role",
        "description": "Update member role",
        "request": {
          "pathParams": {
            "groupId": "string",
            "userId": "string"
          },
          "body": {
            "role": "string (OWNER|ADMIN|MEMBER)"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "groupId": "string",
            "userId": "string",
            "user": "User object (optional)",
            "name": "string",
            "role": "string",
            "avatar": "string (optional)",
            "joinedAt": "Date"
          }
        }
      },
      {
        "method": "GET",
        "path": "/groups/{groupId}/announcements",
        "description": "Get group announcements",
        "request": {
          "pathParams": {
            "groupId": "string"
          }
        },
        "response": {
          "items": [
            {
              "id": "string",
              "groupId": "string",
              "authorId": "string",
              "author": "User object (optional)",
              "title": "string",
              "content": "string",
              "isPinned": "boolean",
              "createdAt": "Date",
              "updatedAt": "Date"
            }
          ]
        }
      },
      {
        "method": "POST",
        "path": "/groups/{groupId}/announcements",
        "description": "Create announcement",
        "request": {
          "pathParams": {
            "groupId": "string"
          },
          "body": {
            "title": "string",
            "content": "string",
            "isPinned": "boolean (optional)"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "groupId": "string",
            "authorId": "string",
            "author": "User object (optional)",
            "title": "string",
            "content": "string",
            "isPinned": "boolean",
            "createdAt": "Date",
            "updatedAt": "Date"
          }
        }
      },
      {
        "method": "PUT",
        "path": "/groups/{groupId}/announcements/{announcementId}",
        "description": "Update announcement",
        "request": {
          "pathParams": {
            "groupId": "string",
            "announcementId": "string"
          },
          "body": {
            "title": "string (optional)",
            "content": "string (optional)",
            "isPinned": "boolean (optional)"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "groupId": "string",
            "authorId": "string",
            "author": "User object (optional)",
            "title": "string",
            "content": "string",
            "isPinned": "boolean",
            "createdAt": "Date",
            "updatedAt": "Date"
          }
        }
      },
      {
        "method": "DELETE",
        "path": "/groups/{groupId}/announcements/{announcementId}",
        "description": "Delete announcement",
        "request": {
          "pathParams": {
            "groupId": "string",
            "announcementId": "string"
          }
        },
        "response": {
          "items": null
        }
      },
      {
        "method": "GET",
        "path": "/groups/{groupId}/quizzes",
        "description": "Get quizzes in group",
        "request": {
          "pathParams": {
            "groupId": "string"
          }
        },
        "response": {
          "items": [
            {
              "id": "string",
              "title": "string",
              "description": "string (optional)",
              "subject": "string",
              "questionCount": "number",
              "estimatedTime": "number",
              "difficulty": "string",
              "isPublic": "boolean",
              "visibility": "string (optional)",
              "attemptCount": "number (optional)",
              "creatorId": "string (optional)",
              "createdAt": "Date (optional)",
              "updatedAt": "Date (optional)",
              "tags": "string[] (optional)"
            }
          ]
        }
      },
      {
        "method": "POST",
        "path": "/groups/{groupId}/quizzes",
        "description": "Add quiz to group",
        "request": {
          "pathParams": {
            "groupId": "string"
          },
          "body": {
            "quizId": "string"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "title": "string",
            "description": "string (optional)",
            "subject": "string",
            "questionCount": "number",
            "estimatedTime": "number",
            "difficulty": "string",
            "isPublic": "boolean",
            "visibility": "string (optional)",
            "attemptCount": "number (optional)",
            "creatorId": "string (optional)",
            "createdAt": "Date (optional)",
            "updatedAt": "Date (optional)",
            "tags": "string[] (optional)"
          }
        }
      },
      {
        "method": "DELETE",
        "path": "/groups/{groupId}/quizzes/{quizId}",
        "description": "Remove quiz from group",
        "request": {
          "pathParams": {
            "groupId": "string",
            "quizId": "string"
          }
        },
        "response": {
          "items": null
        }
      },
      {
        "method": "GET",
        "path": "/groups/{groupId}/resources",
        "description": "Get shared resources in group",
        "request": {
          "pathParams": {
            "groupId": "string"
          }
        },
        "response": {
          "items": [
            {
              "id": "string",
              "groupId": "string",
              "uploaderId": "string",
              "uploader": "User object (optional)",
              "title": "string",
              "description": "string (optional)",
              "fileUrl": "string",
              "fileType": "string",
              "fileSize": "number",
              "uploadedAt": "Date"
            }
          ]
        }
      },
      {
        "method": "POST",
        "path": "/groups/{groupId}/resources",
        "description": "Upload shared resource",
        "request": {
          "pathParams": {
            "groupId": "string"
          },
          "body": {
            "title": "string",
            "description": "string (optional)",
            "file": "File (multipart/form-data)"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "groupId": "string",
            "uploaderId": "string",
            "uploader": "User object (optional)",
            "title": "string",
            "description": "string (optional)",
            "fileUrl": "string",
            "fileType": "string",
            "fileSize": "number",
            "uploadedAt": "Date"
          }
        }
      },
      {
        "method": "DELETE",
        "path": "/groups/{groupId}/resources/{resourceId}",
        "description": "Delete shared resource",
        "request": {
          "pathParams": {
            "groupId": "string",
            "resourceId": "string"
          }
        },
        "response": {
          "items": null
        }
      }
    ]
  },
  "dashboard": {
    "endpoints": [
      {
        "method": "GET",
        "path": "/dashboard/{userId}",
        "description": "Get dashboard summary for user",
        "request": {
          "pathParams": {
            "userId": "string"
          }
        },
        "response": {
          "items": {
            "user": "User object",
            "statistics": "UserStatistics object",
            "recentAttempts": "ExamAttempt[]",
            "recentGroups": "Group[]",
            "recentQuizzes": "Quiz[]"
          }
        }
      }
    ]
  },
  "statistics": {
    "endpoints": [
      {
        "method": "GET",
        "path": "/statistics/user/{userId}",
        "description": "Get user statistics",
        "request": {
          "pathParams": {
            "userId": "string"
          }
        },
        "response": {
          "items": {
            "userId": "string",
            "totalQuizzesTaken": "number",
            "totalPoints": "number",
            "averageScore": "number",
            "totalTimeSpent": "number",
            "quizzesBySubject": "object",
            "quizzesByDifficulty": "object",
            "recentActivity": "ExamAttempt[]"
          }
        }
      },
      {
        "method": "GET",
        "path": "/statistics/quiz/{quizId}",
        "description": "Get quiz statistics",
        "request": {
          "pathParams": {
            "quizId": "string"
          }
        },
        "response": {
          "items": {
            "quizId": "string",
            "totalAttempts": "number",
            "averageScore": "number",
            "averageTime": "number",
            "completionRate": "number",
            "difficultyRating": "number"
          }
        }
      },
      {
        "method": "GET",
        "path": "/statistics/dashboard",
        "description": "Get global dashboard statistics",
        "request": {
          "body": null
        },
        "response": {
          "items": {
            "totalUsers": "number",
            "totalQuizzes": "number",
            "totalAttempts": "number",
            "totalGroups": "number"
          }
        }
      }
    ]
  },
  "files": {
    "endpoints": [
      {
        "method": "POST",
        "path": "/files/upload",
        "description": "Upload file",
        "request": {
          "body": {
            "file": "File (multipart/form-data)"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "url": "string",
            "fileName": "string",
            "size": "number",
            "contentType": "string"
          }
        }
      },
      {
        "method": "GET",
        "path": "/files/{fileId}",
        "description": "Download file",
        "request": {
          "pathParams": {
            "fileId": "string"
          }
        },
        "response": {
          "items": {
            "id": "string",
            "url": "string",
            "fileName": "string",
            "size": "number",
            "contentType": "string"
          }
        }
      }
    ]
  }
}
