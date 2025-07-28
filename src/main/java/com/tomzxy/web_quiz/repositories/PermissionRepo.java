package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepo extends JpaRepository<Permission, String>, JpaSpecificationExecutor<Permission> {
    
    // Basic CRUD with pagination
    @Query("SELECT p FROM Permission p WHERE p.is_active = true")
    Page<Permission> findAllActive(Pageable pageable);
    
    // Search functionality
    @Query("SELECT p FROM Permission p WHERE LOWER(p.permissionName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND p.is_active = true")
    Page<Permission> searchByPermissionNameOrDescription(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Find by permission name
    @Query("SELECT p FROM Permission p WHERE p.permissionName = :permissionName AND p.is_active = true")
    Optional<Permission> findByPermissionName(@Param("permissionName") String permissionName);
    
    // Check if permission exists by name
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Permission p " +
           "WHERE p.permissionName = :permissionName AND p.is_active = true")
    boolean existsByPermissionName(@Param("permissionName") String permissionName);
    
    // Find permissions by role count
    @Query("SELECT p FROM Permission p WHERE SIZE(p.rolePermissionObjects) = :roleCount AND p.is_active = true")
    Page<Permission> findByRoleCount(@Param("roleCount") int roleCount, Pageable pageable);
    
    // Find permissions with minimum role count
    @Query("SELECT p FROM Permission p WHERE SIZE(p.rolePermissionObjects) >= :minRoleCount AND p.is_active = true")
    Page<Permission> findByMinRoleCount(@Param("minRoleCount") int minRoleCount, Pageable pageable);
    
    // Find permissions without roles
    @Query("SELECT p FROM Permission p WHERE SIZE(p.rolePermissionObjects) = 0 AND p.is_active = true")
    Page<Permission> findPermissionsWithoutRoles(Pageable pageable);
    
    // Find permissions with most roles
    @Query("SELECT p FROM Permission p WHERE p.is_active = true ORDER BY SIZE(p.rolePermissionObjects) DESC")
    Page<Permission> findPermissionsWithMostRoles(Pageable pageable);
    
    // Find recent permissions
    @Query("SELECT p FROM Permission p WHERE p.is_active = true ORDER BY p.permissionName ASC")
    Page<Permission> findAllOrdered(Pageable pageable);
    
    // Count permissions by role count
    @Query("SELECT COUNT(p) FROM Permission p WHERE SIZE(p.rolePermissionObjects) = :roleCount AND p.is_active = true")
    long countByRoleCount(@Param("roleCount") int roleCount);
    
    // Find all permission names
    @Query("SELECT p.permissionName FROM Permission p WHERE p.is_active = true")
    List<String> findAllPermissionNames();
    
    // Find permissions by name list
    @Query("SELECT p FROM Permission p WHERE p.permissionName IN :names AND p.is_active = true")
    List<Permission> findByPermissionNames(@Param("names") List<String> names);
    
    // Find permissions with description containing specific text
    @Query("SELECT p FROM Permission p WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :descriptionText, '%')) AND p.is_active = true")
    Page<Permission> findByDescriptionContaining(@Param("descriptionText") String descriptionText, Pageable pageable);
    
    // Find permissions with empty description
    @Query("SELECT p FROM Permission p WHERE (p.description IS NULL OR p.description = '') AND p.is_active = true")
    Page<Permission> findPermissionsWithEmptyDescription(Pageable pageable);
    
    // Find permissions with non-empty description
    @Query("SELECT p FROM Permission p WHERE p.description IS NOT NULL AND p.description != '' AND p.is_active = true")
    Page<Permission> findPermissionsWithDescription(Pageable pageable);
    
    // Find permissions by object type
    @Query("SELECT p FROM Permission p JOIN p.rolePermissionObjects rpo WHERE rpo.rolePermissionId.objectType = :objectType AND p.is_active = true")
    Page<Permission> findByObjectType(@Param("objectType") String objectType, Pageable pageable);
    
    // Find permissions by role name
    @Query("SELECT p FROM Permission p JOIN p.rolePermissionObjects rpo JOIN rpo.role r WHERE r.name = :roleName AND p.is_active = true")
    Page<Permission> findByRoleName(@Param("roleName") String roleName, Pageable pageable);
    
    // Find permissions by role ID
    @Query("SELECT p FROM Permission p JOIN p.rolePermissionObjects rpo WHERE rpo.role.id = :roleId AND p.is_active = true")
    Page<Permission> findByRoleId(@Param("roleId") Long roleId, Pageable pageable);
    
    // Find permissions by multiple role IDs
    @Query("SELECT p FROM Permission p JOIN p.rolePermissionObjects rpo WHERE rpo.role.id IN :roleIds AND p.is_active = true")
    Page<Permission> findByRoleIds(@Param("roleIds") List<Long> roleIds, Pageable pageable);
    
    // Find permissions by role count and name pattern
    @Query("SELECT p FROM Permission p WHERE SIZE(p.rolePermissionObjects) = :roleCount " +
           "AND LOWER(p.permissionName) LIKE LOWER(CONCAT('%', :namePattern, '%')) AND p.is_active = true")
    Page<Permission> findByRoleCountAndNamePattern(@Param("roleCount") int roleCount, 
                                                  @Param("namePattern") String namePattern, Pageable pageable);
}
