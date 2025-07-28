package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.Role;
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
public interface RoleRepo extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    
    // Basic queries
    Optional<Role> findByName(String name);
    
    // Search functionality
    @Query("SELECT r FROM Role r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND r.is_active = true")
    Page<Role> searchByRoleName(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Find by role name
    @Query("SELECT r FROM Role r WHERE r.name = :name AND r.is_active = true")
    Optional<Role> findByNameAndActive(@Param("name") String name);
    
    // Check if role exists by name
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r " +
           "WHERE r.name = :name AND r.is_active = true")
    boolean existsByName(@Param("name") String name);
    
    // Find roles by user count
    @Query("SELECT r FROM Role r WHERE SIZE(r.user) = :userCount AND r.is_active = true")
    Page<Role> findByUserCount(@Param("userCount") int userCount, Pageable pageable);
    
    // Find roles with minimum user count
    @Query("SELECT r FROM Role r WHERE SIZE(r.user) >= :minUserCount AND r.is_active = true")
    Page<Role> findByMinUserCount(@Param("minUserCount") int minUserCount, Pageable pageable);
    
    // Find roles without users
    @Query("SELECT r FROM Role r WHERE SIZE(r.user) = 0 AND r.is_active = true")
    Page<Role> findRolesWithoutUsers(Pageable pageable);
    
    // Find roles with most users
    @Query("SELECT r FROM Role r WHERE r.is_active = true ORDER BY SIZE(r.user) DESC")
    Page<Role> findRolesWithMostUsers(Pageable pageable);
    
    // Find recent roles
    @Query("SELECT r FROM Role r WHERE r.is_active = true ORDER BY r.create_at DESC")
    Page<Role> findRecentRoles(Pageable pageable);
    
    // Count roles by user count
    @Query("SELECT COUNT(r) FROM Role r WHERE SIZE(r.user) = :userCount AND r.is_active = true")
    long countByUserCount(@Param("userCount") int userCount);
    
    // Find all role names
    @Query("SELECT r.name FROM Role r WHERE r.is_active = true")
    List<String> findAllRoleNames();
    
    // Find roles by ID list
    @Query("SELECT r FROM Role r WHERE r.id IN :ids AND r.is_active = true")
    List<Role> findByIds(@Param("ids") List<Long> ids);
    
    // Find roles by permission count
    @Query("SELECT r FROM Role r WHERE SIZE(r.rolePermissionObjects) = :permissionCount AND r.is_active = true")
    Page<Role> findByPermissionCount(@Param("permissionCount") int permissionCount, Pageable pageable);
    
    // Find roles with minimum permission count
    @Query("SELECT r FROM Role r WHERE SIZE(r.rolePermissionObjects) >= :minPermissionCount AND r.is_active = true")
    Page<Role> findByMinPermissionCount(@Param("minPermissionCount") int minPermissionCount, Pageable pageable);
    
    // Find roles without permissions
    @Query("SELECT r FROM Role r WHERE SIZE(r.rolePermissionObjects) = 0 AND r.is_active = true")
    Page<Role> findRolesWithoutPermissions(Pageable pageable);
    
    // Find roles with most permissions
    @Query("SELECT r FROM Role r WHERE r.is_active = true ORDER BY SIZE(r.rolePermissionObjects) DESC")
    Page<Role> findRolesWithMostPermissions(Pageable pageable);
    
    // Find roles by user count and permission count
    @Query("SELECT r FROM Role r WHERE SIZE(r.user) = :userCount AND SIZE(r.rolePermissionObjects) = :permissionCount AND r.is_active = true")
    Page<Role> findByUserCountAndPermissionCount(@Param("userCount") int userCount, 
                                                @Param("permissionCount") int permissionCount, Pageable pageable);
}
