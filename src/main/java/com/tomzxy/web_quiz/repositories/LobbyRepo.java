package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.dto.responses.lobby.LobbyResDTO;
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
    @Query("SELECT l FROM Lobby l WHERE l.isActive = true")
    Page<Lobby> findAllActive(Pageable pageable);

    @Query("SELECT l FROM Lobby l WHERE l.id = :lobbyId AND l.isActive = true")
    Optional<Lobby> findByLobbyId(@Param("lobbyId") Long lobbyId);

    // Find all by host id
    @Query("SELECT l FROM Lobby l WHERE l.host.id = :hostId AND l.isActive = true")
    Page<Lobby> findAllByHostId(@Param("hostId") Long hostId, Pageable pageable);

    // Find all joined lobby by user id
    @Query("""
SELECT new com.tomzxy.web_quiz.dto.responses.lobby.LobbyResDTO(
    l.id,
    l.lobbyName,
    l.host.userName,
    COUNT(m)
)
FROM LobbyMember lm
JOIN lm.lobby l
LEFT JOIN LobbyMember m ON m.lobby.id = l.id
WHERE lm.user.id = :userId
AND l.isActive = true
GROUP BY l.id, l.lobbyName, l.host.userName
""")
    Page<LobbyResDTO> findJoinedLobbies(Long userId, Pageable pageable);

    // fin by code invite
    @Query("SELECT l FROM Lobby l WHERE l.codeInvite = :code AND l.isActive = true")
    Optional<Lobby> findByJoinCode(@Param("code") String code);


    // Search functionality
    @Query("SELECT l FROM Lobby l WHERE LOWER(l.lobbyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND l.isActive = true")
    Page<Lobby> searchByLobbyName(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Find by lobby name
    @Query("SELECT l FROM Lobby l WHERE l.lobbyName = :lobbyName AND l.isActive = true")
    Optional<Lobby> findByLobbyName(@Param("lobbyName") String lobbyName);

    // Check if lobby exists by name
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Lobby l " +
            "WHERE l.lobbyName = :lobbyName AND l.isActive = true")
    boolean existsByLobbyName(@Param("lobbyName") String lobbyName);

    // Find groups by member count
    @Query("SELECT l FROM Lobby l WHERE SIZE(l.members) = :memberCount AND l.isActive = true")
    Page<Lobby> findByMemberCount(@Param("memberCount") int memberCount, Pageable pageable);

    // Find groups with minimum member count
    @Query("SELECT l FROM Lobby l WHERE SIZE(l.members) >= :minMemberCount AND l.isActive = true")
    Page<Lobby> findByMinMemberCount(@Param("minMemberCount") int minMemberCount, Pageable pageable);

    // Find groups by specific member
    @Query("SELECT l FROM Lobby l JOIN l.members m WHERE m.id = :memberId AND l.isActive = true")
    Page<Lobby> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    // Find groups by multiple members
    @Query("SELECT l FROM Lobby l JOIN l.members m WHERE m.id IN :memberIds AND l.isActive = true")
    Page<Lobby> findByMemberIds(@Param("memberIds") Set<Long> memberIds, Pageable pageable);

    // Find groups with quizzes count
    @Query("SELECT l FROM Lobby l WHERE SIZE(l.quizzes) = :quizCount AND l.isActive = true")
    Page<Lobby> findByQuizCount(@Param("quizCount") int quizCount, Pageable pageable);

    // Find groups with minimum quizzes count
    @Query("SELECT l FROM Lobby l WHERE SIZE(l.quizzes) >= :minQuizCount AND l.isActive = true")
    Page<Lobby> findByMinQuizCount(@Param("minQuizCount") int minQuizCount, Pageable pageable);

    // Find groups without quizzes
    @Query("SELECT l FROM Lobby l WHERE SIZE(l.quizzes) = 0 AND l.isActive = true")
    Page<Lobby> findGroupsWithoutQuizzes(Pageable pageable);

    // Find groups with most members
    @Query("SELECT l FROM Lobby l WHERE l.isActive = true ORDER BY SIZE(l.members) DESC")
    Page<Lobby> findGroupsWithMostMembers(Pageable pageable);

    // Find groups with most quizzes
    @Query("SELECT l FROM Lobby l WHERE l.isActive = true ORDER BY SIZE(l.quizzes) DESC")
    Page<Lobby> findGroupsWithMostQuizzes(Pageable pageable);

    // Find recent groups
    @Query("SELECT l FROM Lobby l WHERE l.isActive = true ORDER BY l.createdAt DESC")
    Page<Lobby> findRecentGroups(Pageable pageable);

    // Count groups by member count
    @Query("SELECT COUNT(l) FROM Lobby l WHERE SIZE(l.members) = :memberCount AND l.isActive = true")
    long countByMemberCount(@Param("memberCount") int memberCount);

    // Count groups by quiz count
    @Query("SELECT COUNT(l) FROM Lobby l WHERE SIZE(l.quizzes) = :quizCount AND l.isActive = true")
    long countByQuizCount(@Param("quizCount") int quizCount);

    // Find groups by member username
    @Query("""
    SELECT DISTINCT l
    FROM Lobby l
    JOIN l.members lm
    JOIN lm.user u
    WHERE u.userName = :userName
""")
    Page<Lobby> findByMemberUserName(@Param("userName") String userName, Pageable pageable);

    // Find groups by member email
    @Query("""
    SELECT DISTINCT l
    FROM Lobby l
    JOIN l.members lm
    JOIN lm.user u
    WHERE u.email = :email
""")
    Page<Lobby> findByMemberEmail(@Param("email") String email, Pageable pageable);

//    // Find groups with specific member role
//    @Query("SELECT l FROM Lobby l JOIN l.members m JOIN m.roles r WHERE r.name = :roleName AND l.isActive = true")
//    Page<Lobby> findByMemberRole(@Param("roleName") String roleName, Pageable pageable);
//
//    // Find groups with members having multiple roles
//    @Query("SELECT l FROM Lobby l JOIN l.members m WHERE SIZE(m.roles) > 1 AND l.isActive = true")
//    Page<Lobby> findGroupsWithMultiRoleMembers(Pageable pageable);

    // Find all lobby names
    @Query("SELECT l.lobbyName FROM Lobby l WHERE l.isActive = true")
    List<String> findAllLobbyNames();

    // Find groups by ID list
    @Query("SELECT l FROM Lobby l WHERE l.id IN :ids AND l.isActive = true")
    List<Lobby> findByIds(@Param("ids") List<Long> ids);

    // Find lobbies by member userId
    @Query("SELECT l FROM Lobby l JOIN l.members m WHERE m.id = :userId AND l.isActive = true")
    List<Lobby> findByUserId(@Param("userId") Long userId);

    boolean existsByCodeInvite(String code);
}