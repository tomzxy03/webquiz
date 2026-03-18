package com.tomzxy.web_quiz.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tomzxy.web_quiz.models.User.User;
import com.tomzxy.web_quiz.models.rolepermission.RolePermissionObject;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "role")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {
    @Column(name = "name")
    String name;

    public Role(String name) {
        this.name = name;
    }

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @JsonIgnore
    Set<User> user;

    @OneToMany(mappedBy = "role", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
    @Column(name = "role_object")
    Set<RolePermissionObject> rolePermissionObjects = new HashSet<>();
}
