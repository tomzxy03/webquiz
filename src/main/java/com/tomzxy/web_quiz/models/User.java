package com.tomzxy.web_quiz.models;

import com.tomzxy.web_quiz.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.Index;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified = false;

    @Column(name = "is_phone_verified", nullable = false)
    private boolean isPhoneVerified = false;

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
    private Set<QuizResult> quizResults = new HashSet<>();

    // Business Logic Methods
    public void addRole(Role role) {
        if (role != null) {
            this.roles.add(role);
        }
    }

    public void removeRole(Role role) {
        if (role != null) {
            this.roles.remove(role);
        }
    }

    public boolean hasRole(String roleName) {
        return this.roles.stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    public boolean hasAnyRole(Set<String> roleNames) {
        return this.roles.stream()
                .anyMatch(role -> roleNames.contains(role.getName()));
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    public boolean isAdult() {
        return getAge() >= 18;
    }

    public void verifyEmail() {
        this.isEmailVerified = true;
        this.setUpdatedAt(LocalDateTime.now());
    }

    public void verifyPhone() {
        this.isPhoneVerified = true;
        this.setUpdatedAt(LocalDateTime.now());
    }

    public boolean isFullyVerified() {
        return isEmailVerified && (phone == null || isPhoneVerified);
    }

    // Enhanced Learning Analytics Methods
    public int getTotalQuizzesTaken() {
        return quizResults.size();
    }

    public double getAverageScore() {
        if (quizResults.isEmpty()) {
            return 0.0;
        }
        return quizResults.stream()
                .mapToDouble(result -> result.getPercentageScore())
                .average()
                .orElse(0.0);
    }

    public int getTotalCorrectAnswers() {
        return quizResults.stream()
                .mapToInt(QuizResult::getTotalCorrected)
                .sum();
    }

    public int getTotalWrongAnswers() {
        return quizResults.stream()
                .mapToInt(QuizResult::getTotalFailed)
                .sum();
    }

    public int getTotalSkippedQuestions() {
        return quizResults.stream()
                .mapToInt(QuizResult::getTotalSkipped)
                .sum();
    }

    public long getTotalTimeSpent() {
        return quizResults.stream()
                .filter(result -> result.getCompletionTimeMinutes() != null)
                .mapToLong(result -> result.getCompletionTimeMinutes())
                .sum();
    }

    public QuizResult getBestQuizResult() {
        return quizResults.stream()
                .max((r1, r2) -> Double.compare(r1.getPercentageScore(), r2.getPercentageScore()))
                .orElse(null);
    }

    public QuizResult getLatestQuizResult() {
        return quizResults.stream()
                .max((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt()))
                .orElse(null);
    }

    public boolean isActiveLearner() {
        // User is active if they've taken quizzes in the last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return quizResults.stream()
                .anyMatch(result -> result.getCreatedAt().isAfter(thirtyDaysAgo));
    }

    public String getLearningLevel() {
        double avgScore = getAverageScore();
        if (avgScore >= 90) return "EXPERT";
        else if (avgScore >= 75) return "ADVANCED";
        else if (avgScore >= 60) return "INTERMEDIATE";
        else if (avgScore >= 40) return "BEGINNER";
        else return "NOVICE";
    }

    public boolean isImproving() {
        // Check if recent scores are better than older scores
        List<QuizResult> sortedResults = quizResults.stream()
                .sorted((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt()))
                .toList();
        
        if (sortedResults.size() < 2) return false;
        
        // Compare first half vs second half
        int midPoint = sortedResults.size() / 2;
        double firstHalfAvg = sortedResults.subList(0, midPoint).stream()
                .mapToDouble(QuizResult::getPercentageScore)
                .average()
                .orElse(0.0);
        double secondHalfAvg = sortedResults.subList(midPoint, sortedResults.size()).stream()
                .mapToDouble(QuizResult::getPercentageScore)
                .average()
                .orElse(0.0);
        
        return secondHalfAvg > firstHalfAvg;
    }

    public long getDaysSinceLastQuiz() {
        QuizResult latest = getLatestQuizResult();
        if (latest == null) return Long.MAX_VALUE;
        return java.time.Duration.between(latest.getCreatedAt(), LocalDateTime.now()).toDays();
    }

    public boolean isNewUser() {
        return getDaysSinceLastQuiz() <= 7 && getTotalQuizzesTaken() <= 3;
    }

    public boolean isExperiencedUser() {
        return getTotalQuizzesTaken() >= 10;
    }

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
