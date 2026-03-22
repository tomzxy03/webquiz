package com.tomzxy.web_quiz.repositories;

import com.tomzxy.web_quiz.dto.responses.lobby.LobbyResDTO;
import com.tomzxy.web_quiz.enums.LobbyRole;
import com.tomzxy.web_quiz.models.Host.LobbyMember;
import com.tomzxy.web_quiz.models.Host.LobbyMemberId;
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

@Repository
public interface LobbyMemberRepo
        extends JpaRepository<LobbyMember, LobbyMemberId>,
        JpaSpecificationExecutor<LobbyMember> {

    Page<LobbyMember> findByIdLobbyId(Long lobbyId, Pageable pageable);

    boolean existsByIdLobbyIdAndIdUserId(Long lobbyId, Long userId);

    void deleteByIdLobbyIdAndIdUserId(Long lobbyId, Long userId);

    Optional<LobbyMember> findByIdLobbyIdAndIdUserId(Long lobbyId, Long userId);

    Page<LobbyMember> findByUserIdAndRole(Long userId, LobbyRole role, Pageable pageable);

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
            AND lm.role = :role
            GROUP BY l.id, l.lobbyName, l.host.userName
            """)
    Page<LobbyResDTO> findOwnedLobbies(Long userId, LobbyRole role, Pageable pageable);

    @Query("""
                SELECT lm.lobby
                FROM LobbyMember lm
                WHERE lm.id.userId = :userId
            """)
    Page<Lobby> findByUserId(Long userId, Pageable pageable);

    // Dashboard: group summaries with role and open quiz count
    @Query("""
                SELECT lm
                FROM LobbyMember lm
                JOIN FETCH lm.lobby l
                WHERE lm.id.userId = :userId
                AND l.isActive = true
            """)
    List<LobbyMember> findMembershipsWithLobbyByUserId(@Param("userId") Long userId);

}