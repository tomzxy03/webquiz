package com.tomzxy.web_quiz.models.User;

import com.tomzxy.web_quiz.models.*;
import com.tomzxy.web_quiz.models.Host.QuestionBank;
import com.tomzxy.web_quiz.models.NotificationUser.Notification;
import com.tomzxy.web_quiz.models.QuizUser.QuizInstance;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.Index;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_username", columnList = "user_name"),
    @Index(name = "idx_user_phone", columnList = "phone")
})
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    
    @Column(name = "user_name", unique = true, nullable = false, length = 50)
    private String userName;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL)
    private QuestionBank hostQuestion;

    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified = false;



    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles", 
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"),
        indexes = {
            @Index(name = "idx_user_roles_user_id", columnList = "user_id"),
            @Index(name = "idx_user_roles_role_id", columnList = "role_id")
        }
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(mappedBy = "members")
    private Set<Lobby> lobbies = new HashSet<>();

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Notification> notifications = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuizInstance> quizInstances = new HashSet<>();


    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", isActive=" + isActive() +
                '}';
    }
}
