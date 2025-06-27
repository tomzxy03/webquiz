package com.tomzxy.web_quiz.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Getter
@Setter
@Table(name = "role")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission{
    @Id
    @Column(name = "permission_name")
    String permissionName;

    @Column(name = "description")
    String description;
}
