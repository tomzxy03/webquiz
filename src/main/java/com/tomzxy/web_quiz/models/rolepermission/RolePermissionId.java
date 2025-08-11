package com.tomzxy.web_quiz.models.rolepermission;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionId implements Serializable {
    @Column(name = "role_id")
    Long roleId;

    @Column(name = "permission_name")
    String permissionName;

    @Column(name = "object_type")
    String objectType;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RolePermissionId that = (RolePermissionId) o;
        return Objects.equals(roleId, that.roleId) && Objects.equals(permissionName, that.permissionName) && Objects.equals(objectType, that.objectType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, permissionName, objectType);
    }
}
