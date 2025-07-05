package com.tomzxy.web_quiz.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Getter
@Setter
@Table(name = "permission")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Permission{
    @Id
    @Column(name = "permission_name")
    String permissionName;

    @Column(name = "description")
    String description;

    @Column(name = "is_active")
    boolean is_active = true;

    public Permission(String permissionName, String description){
        this.permissionName=permissionName;
        this.description=description;
    }
}
