package com.tomzxy.web_quiz.models;

import com.tomzxy.web_quiz.models.User.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_expired_at", columnList = "expiredAt"),
        @Index(name = "idx_jti", columnList = "jti", unique = true)
})
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;          // internal primary key

    @Column(nullable = false, unique = true, length = 36)
    private String jti;        // JWT ID – unique identifier of the token

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant issuedAt;

    @Column(nullable = false)
    private Instant expiredAt;

    @Column(nullable = false)
    private boolean revoked;

    // Thông tin thiết bị
    @Column(length = 100, nullable = true)
    private String deviceId;
    @Column(length = 100, nullable = true)
    private String deviceName;
    @Column(length = 100, nullable = true)
    private String ipAddress;
    @Column(length = 500, nullable = true)
    private String userAgent;

    private Instant lastUsedAt;


}