package com.tomzxy.web_quiz.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@SuperBuilder
public abstract class BaseEntity implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private String uuid = UUID.randomUUID().toString();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;


    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return uuid != null && uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : super.hashCode();
    }

    public void activate() {
        this.setActive(true);
        this.setUpdatedAt(java.time.LocalDateTime.now());
    }

    public void deactivate() {
        this.setActive(false);
        this.setUpdatedAt(java.time.LocalDateTime.now());
    }
}
