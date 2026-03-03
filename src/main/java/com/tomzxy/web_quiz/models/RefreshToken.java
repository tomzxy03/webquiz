package com.tomzxy.web_quiz.models;

import com.tomzxy.web_quiz.models.User.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_expired_at", columnList = "expired_at"),
        @Index(name = "idx_jti", columnList = "jti", unique = true)
})
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;          // internal primary key

    @Column(nullable = false, unique = true, length = 36)
    private String jti;        // JWT ID – unique identifier of the token

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expired_at", nullable = false)
    private Instant expiredAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    // Thông tin thiết bị
    @Column(name = "device_id", length = 100, nullable = true)
    private String deviceId;
    @Column(name = "device_name", length = 100, nullable = true)
    private String deviceName;
        @Column(name = "ip_address", length = 100, nullable = true)
    private String ipAddress;
    @Column(name = "user_agent", length = 500, nullable = true)
    private String userAgent;

    @Column(name = "last_used_at", nullable = true)
    private Instant lastUsedAt;


}