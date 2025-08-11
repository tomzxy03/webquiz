package com.tomzxy.web_quiz.models.rolepermission;

import com.tomzxy.web_quiz.models.Permission;
import com.tomzxy.web_quiz.models.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "role_permission_object")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RolePermissionObject {

    @EmbeddedId
    RolePermissionId rolePermissionId;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    Role role;

    @ManyToOne
    @MapsId("permissionName")
    @JoinColumn(name = "permission_name")
    Permission permission;

    public RolePermissionObject(Role role, Permission permission, String objectType){
        this.role=role;
        this.permission=permission;
        this.rolePermissionId= new RolePermissionId(role.getId(),permission.getPermissionName(),objectType);
    }
}
