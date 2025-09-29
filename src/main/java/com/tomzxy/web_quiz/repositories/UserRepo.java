package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.User;
import com.tomzxy.web_quiz.enums.Gender;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    Page<User> findAll(@NonNull Pageable pageable, @Param("isActive") boolean isActive);

    @Query("SELECT u FROM User u WHERE u.isActive = :isActive")
    Page<User> findAllByActive(@Param("isActive") boolean isActive, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.isActive = true and u.id = :userId")
    Optional<User> findByIdAndActive(Long userId);

    // Authentication and basic queries
    Optional<User> findByUserName(String userName, @Param("isActive") boolean isActive);
    Optional<User> findByEmail(String email, @Param("isActive") boolean isActive);
    boolean existsByEmail(String email, @Param("isActive") boolean isActive);
    boolean existsByPhone(String phone, @Param("isActive") boolean isActive);
    
    // Search functionality
    @Query("SELECT u FROM User u WHERE (LOWER(u.userName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND u.isActive = :isActive")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, @Param("isActive") boolean isActive, Pageable pageable);
    
    // Filter by gender
    @Query("SELECT u FROM User u WHERE u.gender = :gender AND u.isActive = :isActive")
    Page<User> findByGender(@Param("gender") Gender gender, @Param("isActive") boolean isActive,  Pageable pageable);
    
    // Filter by date of birth range
    @Query("SELECT u FROM User u WHERE u.dateOfBirth BETWEEN :startDate AND :endDate AND u.isActive = :isActive")
    Page<User> findByDobBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("isActive") boolean isActive, Pageable pageable);
    
    // Find users by role
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.isActive = :isActive")
    Page<User> findByRoleName(@Param("roleName") String roleName, @Param("isActive") boolean isActive, Pageable pageable);
    
    // Find users by multiple roles
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name IN :roleNames AND u.isActive = :isActive")
    Page<User> findByRoleNames(@Param("roleNames") Set<String> roleNames, @Param("isActive") boolean isActive, Pageable pageable);
    
    // Find users who are hosts of quizzes
    @Query("SELECT DISTINCT u FROM User u JOIN Quiz q ON u.id = q.host.id WHERE u.isActive = :isActive")
    Page<User> findQuizHosts(@Param("isActive") boolean isActive, Pageable pageable);
    
    // Find users who are members of a specific lobby
    @Query("SELECT u FROM User u JOIN u.lobbies l WHERE l.id = :lobbyId AND u.isActive = :isActive")
    Page<User> findByLobbyId(@Param("lobbyId") Long lobbyId, @Param("isActive") boolean isActive, Pageable pageable);
    
    // Find users with specific phone number pattern
    @Query("SELECT u FROM User u WHERE u.phone LIKE :phonePattern AND u.isActive = :isActive")
    Page<User> findByPhonePattern(@Param("phonePattern") String phonePattern, @Param("isActive") boolean isActive, Pageable pageable);
    
    // Find users by email domain
    @Query("SELECT u FROM User u WHERE u.email LIKE CONCAT('%@', :domain) AND u.isActive = :isActive")
    Page<User> findByEmailDomain(@Param("domain") String domain, @Param("isActive") boolean isActive, Pageable pageable);
    
    // Count users by role
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.isActive = :isActive")
    long countByRoleName(@Param("roleName") String roleName, @Param("isActive") boolean isActive);
    
    // Count users by gender
    @Query("SELECT COUNT(u) FROM User u WHERE u.gender = :gender AND u.isActive = :isActive")
    long countByGender(@Param("gender") Gender gender, @Param("isActive") boolean isActive);
    
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
    
    // Find users by age range (calculated from DOB)
    @Query("SELECT u FROM User u WHERE u.dateOfBirth IS NOT NULL AND " +
       "FLOOR(DATEDIFF(CURRENT_DATE, u.dateOfBirth) / 365.25) BETWEEN :minAge AND :maxAge AND u.isActive = :isActive")
    Page<User> findByAgeRange(@Param("minAge") int minAge, @Param("maxAge") int maxAge, Pageable pageable);
    
    // Check if user has specific role
    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.roles r WHERE u.id = :userId AND r.name = :roleName AND u.isActive = :isActive")
    boolean userHasRole(@Param("userId") Long userId, @Param("roleName") String roleName, @Param("isActive") boolean isActive);
    
    // Check if user has any of the specified roles
    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.roles r WHERE u.id = :userId AND r.name IN :roleNames AND u.isActive = :isActive")
    boolean userHasAnyRole(@Param("userId") Long userId, @Param("roleNames") Set<String> roleNames, @Param("isActive") boolean isActive);
}
