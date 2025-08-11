package com.tomzxy.web_quiz.models;


import com.tomzxy.web_quiz.models.rolepermission.RolePermissionObject;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "permission", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    Set<RolePermissionObject> rolePermissionObjects = new HashSet<>();
} 