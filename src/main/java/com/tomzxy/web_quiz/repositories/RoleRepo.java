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

    @Query("SELECT r FROM Role r WHERE r.isActive = :isActive")
    Page<Role> findAllActive(@Param("isActive") boolean isActive, Pageable pageable);

    //find by id
    @Query("SELECT r FROM Role r WHERE r.id = :id AND r.isActive = :isActive")
    Optional<Role> findByIdAndActive(@Param("id") Long id);
    
    // Search functionality
    @Query("SELECT r FROM Role r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND r.isActive = :isActive")
    Page<Role> searchByRoleName(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Find by role name
    @Query("SELECT r FROM Role r WHERE r.name = :name AND r.isActive = :isActive")
    Optional<Role> findByNameAndActive(@Param("name") String name);
    
    // Check if role exists by name
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r " +
           "WHERE r.name = :name AND r.isActive = :isActive")
    boolean existsByName(@Param("name") String name);
    
    // Find roles by user count
    @Query("SELECT r FROM Role r WHERE SIZE(r.user) = :userCount AND r.isActive = :isActive")
    Page<Role> findByUserCount(@Param("userCount") int userCount, Pageable pageable);
    
    // Find roles with minimum user count
    @Query("SELECT r FROM Role r WHERE SIZE(r.user) >= :minUserCount AND r.isActive = :isActive")
    Page<Role> findByMinUserCount(@Param("minUserCount") int minUserCount, Pageable pageable);
    
    // Find roles without users
    @Query("SELECT r FROM Role r WHERE SIZE(r.user) = 0 AND r.isActive = :isActive")
    Page<Role> findRolesWithoutUsers(Pageable pageable);
    
    // Find roles with most users
    @Query("SELECT r FROM Role r WHERE r.isActive = :isActive ORDER BY SIZE(r.user) DESC")
    Page<Role> findRolesWithMostUsers(Pageable pageable);
    
    // Find recent roles
    @Query("SELECT r FROM Role r WHERE r.isActive = :isActive ORDER BY r.createdAt DESC")
    Page<Role> findRecentRoles(Pageable pageable);
    
    // Count roles by user count
    @Query("SELECT COUNT(r) FROM Role r WHERE SIZE(r.user) = :userCount AND r.isActive = :isActive")
    long countByUserCount(@Param("userCount") int userCount);
    
    // Find all role names
    @Query("SELECT r.name FROM Role r WHERE r.isActive = :isActive")
    List<String> findAllRoleNames();
    
    // Find roles by ID list
    @Query("SELECT r FROM Role r WHERE r.id IN :ids AND r.isActive = :isActive")
    List<Role> findByIds(@Param("ids") List<Long> ids);
    
    // Find roles by permission count
    @Query("SELECT r FROM Role r WHERE SIZE(r.rolePermissionObjects) = :permissionCount AND r.isActive = true")
    Page<Role> findByPermissionCount(@Param("permissionCount") int permissionCount, Pageable pageable);
    
    // Find roles with minimum permission count
    @Query("SELECT r FROM Role r WHERE SIZE(r.rolePermissionObjects) >= :minPermissionCount AND r.isActive = true")
    Page<Role> findByMinPermissionCount(@Param("minPermissionCount") int minPermissionCount, Pageable pageable);
    
    // Find roles without permissions
    @Query("SELECT r FROM Role r WHERE SIZE(r.rolePermissionObjects) = 0 AND r.isActive = true")
    Page<Role> findRolesWithoutPermissions(Pageable pageable);
    
    // Find roles with most permissions
    @Query("SELECT r FROM Role r WHERE r.isActive = true ORDER BY SIZE(r.rolePermissionObjects) DESC")
    Page<Role> findRolesWithMostPermissions(Pageable pageable);
}
