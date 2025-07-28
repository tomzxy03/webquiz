package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.models.Lobby;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LobbyRepo extends JpaRepository<Lobby, Long>, JpaSpecificationExecutor<Lobby> {
    
    // Basic CRUD with pagination
    @Query("SELECT l FROM Lobby l WHERE l.is_active = true")
    Page<Lobby> findAllActive(Pageable pageable);
    
    // Search functionality
    @Query("SELECT l FROM Lobby l WHERE LOWER(l.groupName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND l.is_active = true")
    Page<Lobby> searchByGroupName(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Find by group name
    @Query("SELECT l FROM Lobby l WHERE l.groupName = :groupName AND l.is_active = true")
    Optional<Lobby> findByGroupName(@Param("groupName") String groupName);
    
    // Check if group exists by name
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Lobby l " +
           "WHERE l.groupName = :groupName AND l.is_active = true")
    boolean existsByGroupName(@Param("groupName") String groupName);
    
    // Find groups by member count
    @Query("SELECT l FROM Lobby l WHERE SIZE(l.members) = :memberCount AND l.is_active = true")
    Page<Lobby> findByMemberCount(@Param("memberCount") int memberCount, Pageable pageable);
    
    // Find groups with minimum member count
    @Query("SELECT l FROM Lobby l WHERE SIZE(l.members) >= :minMemberCount AND l.is_active = true")
    Page<Lobby> findByMinMemberCount(@Param("minMemberCount") int minMemberCount, Pageable pageable);
    
    // Find groups with maximum member count
    @Query("SELECT l FROM Lobby l WHERE SIZE(l.members) <= :maxMemberCount AND l.is_active = true")
    Page<Lobby> findByMaxMemberCount(@Param("maxMemberCount") int maxMemberCount, Pageable pageable);
    
    // Find groups by member count range
    @Query("SELECT l FROM Lobby l WHERE SIZE(l.members) BETWEEN :minMemberCount AND :maxMemberCount AND l.is_active = true")
    Page<Lobby> findByMemberCountRange(@Param("minMemberCount") int minMemberCount, 
                                      @Param("maxMemberCount") int maxMemberCount, Pageable pageable);
    
    // Find groups by specific member
    @Query("SELECT l FROM Lobby l JOIN l.members m WHERE m.id = :memberId AND l.is_active = true")
    Page<Lobby> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);
    
    // Find groups by multiple members
    @Query("SELECT l FROM Lobby l JOIN l.members m WHERE m.id IN :memberIds AND l.is_active = true")
    Page<Lobby> findByMemberIds(@Param("memberIds") Set<Long> memberIds, Pageable pageable);
    
    // Find groups with quizzes count
    @Query("SELECT l FROM Lobby l WHERE SIZE(l.quizzes) = :quizCount AND l.is_active = true")
    Page<Lobby> findByQuizCount(@Param("quizCount") int quizCount, Pageable pageable);
    
    // Find groups with minimum quizzes count
    @Query("SELECT l FROM Lobby l WHERE SIZE(l.quizzes) >= :minQuizCount AND l.is_active = true")
    Page<Lobby> findByMinQuizCount(@Param("minQuizCount") int minQuizCount, Pageable pageable);
    
    // Find groups without quizzes
    @Query("SELECT l FROM Lobby l WHERE SIZE(l.quizzes) = 0 AND l.is_active = true")
    Page<Lobby> findGroupsWithoutQuizzes(Pageable pageable);
    
    // Find groups with most members
    @Query("SELECT l FROM Lobby l WHERE l.is_active = true ORDER BY SIZE(l.members) DESC")
    Page<Lobby> findGroupsWithMostMembers(Pageable pageable);
    
    // Find groups with most quizzes
    @Query("SELECT l FROM Lobby l WHERE l.is_active = true ORDER BY SIZE(l.quizzes) DESC")
    Page<Lobby> findGroupsWithMostQuizzes(Pageable pageable);
    
    // Find recent groups
    @Query("SELECT l FROM Lobby l WHERE l.is_active = true ORDER BY l.create_at DESC")
    Page<Lobby> findRecentGroups(Pageable pageable);
    
    // Count groups by member count
    @Query("SELECT COUNT(l) FROM Lobby l WHERE SIZE(l.members) = :memberCount AND l.is_active = true")
    long countByMemberCount(@Param("memberCount") int memberCount);
    
    // Count groups by quiz count
    @Query("SELECT COUNT(l) FROM Lobby l WHERE SIZE(l.quizzes) = :quizCount AND l.is_active = true")
    long countByQuizCount(@Param("quizCount") int quizCount);
    
    // Find groups with specific quiz count range
    @Query("SELECT l FROM Lobby l WHERE SIZE(l.quizzes) BETWEEN :minQuizzes AND :maxQuizzes AND l.is_active = true")
    Page<Lobby> findByQuizCountRange(@Param("minQuizzes") int minQuizzes, 
                                    @Param("maxQuizzes") int maxQuizzes, Pageable pageable);
    
    // Find groups by member username
    @Query("SELECT l FROM Lobby l JOIN l.members m WHERE m.userName = :userName AND l.is_active = true")
    Page<Lobby> findByMemberUserName(@Param("userName") String userName, Pageable pageable);
    
    // Find groups by member email
    @Query("SELECT l FROM Lobby l JOIN l.members m WHERE m.email = :email AND l.is_active = true")
    Page<Lobby> findByMemberEmail(@Param("email") String email, Pageable pageable);
    
    // Find groups with specific member role
    @Query("SELECT l FROM Lobby l JOIN l.members m JOIN m.roles r WHERE r.name = :roleName AND l.is_active = true")
    Page<Lobby> findByMemberRole(@Param("roleName") String roleName, Pageable pageable);
    
    // Find groups with members having multiple roles
    @Query("SELECT l FROM Lobby l JOIN l.members m WHERE SIZE(m.roles) > 1 AND l.is_active = true")
    Page<Lobby> findGroupsWithMultiRoleMembers(Pageable pageable);
    
    // Find groups by member count and quiz count
    @Query("SELECT l FROM Lobby l WHERE SIZE(l.members) = :memberCount AND SIZE(l.quizzes) = :quizCount AND l.is_active = true")
    Page<Lobby> findByMemberCountAndQuizCount(@Param("memberCount") int memberCount, 
                                             @Param("quizCount") int quizCount, Pageable pageable);
    
    // Find all group names
    @Query("SELECT l.groupName FROM Lobby l WHERE l.is_active = true")
    List<String> findAllGroupNames();
    
    // Find groups by ID list
    @Query("SELECT l FROM Lobby l WHERE l.id IN :ids AND l.is_active = true")
    List<Lobby> findByIds(@Param("ids") List<Long> ids);
    
    // Find groups with specific member count and name pattern
    @Query("SELECT l FROM Lobby l WHERE SIZE(l.members) = :memberCount " +
           "AND LOWER(l.groupName) LIKE LOWER(CONCAT('%', :namePattern, '%')) AND l.is_active = true")
    Page<Lobby> findByMemberCountAndNamePattern(@Param("memberCount") int memberCount, 
                                               @Param("namePattern") String namePattern, Pageable pageable);
} 