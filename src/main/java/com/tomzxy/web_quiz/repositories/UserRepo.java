package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.User.User;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // Basic CRUD with pagination
    @NonNull
    Page<User> findAllByIsActiveTrue(@NonNull Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.isActive = :isActive")
    Page<User> findAllByActive(@Param("isActive") boolean isActive, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.isActive = true and u.id = :userId")
    Optional<User> findByIdAndActive(Long userId);

    // Authentication and basic queries
    @EntityGraph(attributePaths = {
            "roles",
            "roles.rolePermissionObjects",
            "roles.rolePermissionObjects.permission"
    })
    Optional<User> findByUserName(String userName);
    @EntityGraph(attributePaths = {
            "roles",
            "roles.rolePermissionObjects",
            "roles.rolePermissionObjects.permission"
    })
    Optional<User> findByEmail(String email);
    @Query("""
        SELECT DISTINCT u
        FROM User u
        LEFT JOIN FETCH u.roles r
        LEFT JOIN FETCH r.rolePermissionObjects rpo
        LEFT JOIN FETCH rpo.permission
        WHERE u.email = :email
    """)
    Optional<User> findUserWithAuthorities(@Param("email") String email);

    Optional<User> findByUserNameAndIsActiveTrue(String userName);

    Optional<User> findByEmailAndIsActiveTrue(String email);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existsByEmailAndIsActiveTrue(String email);

    // Search functionality
    @Query("SELECT u FROM User u WHERE (LOWER(u.userName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND u.isActive = :isActive")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, @Param("isActive") boolean isActive,
            Pageable pageable);


    // Find users by role
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.isActive = :isActive")
    Page<User> findByRoleName(@Param("roleName") String roleName, @Param("isActive") boolean isActive,
            Pageable pageable);

    // Find users by multiple roles
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name IN :roleNames AND u.isActive = :isActive")
    Page<User> findByRoleNames(@Param("roleNames") Set<String> roleNames, @Param("isActive") boolean isActive,
            Pageable pageable);

    // Find users who are hosts of quizzes
    @Query("SELECT DISTINCT u FROM User u JOIN Quiz q ON u.id = q.host.id WHERE u.isActive = :isActive")
    Page<User> findQuizHosts(@Param("isActive") boolean isActive, Pageable pageable);

    // Find users who are members of a specific lobby
    @Query("SELECT u FROM User u JOIN u.lobbies l WHERE l.id = :lobbyId AND u.isActive = :isActive")
    Page<User> findByLobbyId(@Param("lobbyId") Long lobbyId, @Param("isActive") boolean isActive, Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.lobbies l WHERE l.id = :lobbyId AND u.isActive = true")
    List<User> findByLobbyId(@Param("lobbyId") Long lobbyId);

    // Find users by email domain
    @Query("SELECT u FROM User u WHERE u.email LIKE CONCAT('%@', :domain) AND u.isActive = :isActive")
    Page<User> findByEmailDomain(@Param("domain") String domain, @Param("isActive") boolean isActive,
            Pageable pageable);

    // Count users by role
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.isActive = :isActive")
    long countByRoleName(@Param("roleName") String roleName, @Param("isActive") boolean isActive);

    // Find recent users
    @Query("SELECT u FROM User u WHERE u.isActive = :isActive ORDER BY u.createdAt DESC")
    Page<User> findRecentUsers(@Param("isActive") boolean isActive, Pageable pageable);

    // Find users without any roles
    @Query("SELECT u FROM User u WHERE SIZE(u.roles) = 0 AND u.isActive = :isActive")
    Page<User> findUsersWithoutRoles(@Param("isActive") boolean isActive, Pageable pageable);

    // Find users with multiple roles
    @Query("SELECT u FROM User u WHERE SIZE(u.roles) > 1 AND u.isActive = :isActive")
    Page<User> findUsersWithMultipleRoles(@Param("isActive") boolean isActive, Pageable pageable);

    // Check if user exists by username and email
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u " +
            "WHERE (u.userName = :userName OR u.email = :email) AND u.isActive = :isActive")
    boolean existsByUserNameOrEmail(@Param("userName") String userName, @Param("email") String email);

    // Find all email domains used by users
    @Query("SELECT DISTINCT SUBSTRING(u.email, LOCATE('@', u.email) + 1) FROM User u WHERE u.email IS NOT NULL AND u.isActive = :isActive")
    List<String> findAllEmailDomains(@Param("isActive") boolean isActive);

    // Check if user has specific role
    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.roles r WHERE u.id = :userId AND r.name = :roleName AND u.isActive = :isActive")
    boolean userHasRole(@Param("userId") Long userId, @Param("roleName") String roleName,
            @Param("isActive") boolean isActive);

    // Check if user has any of the specified roles
    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.roles r WHERE u.id = :userId AND r.name IN :roleNames AND u.isActive = :isActive")
    boolean userHasAnyRole(@Param("userId") Long userId, @Param("roleNames") Set<String> roleNames,
            @Param("isActive") boolean isActive);
}
