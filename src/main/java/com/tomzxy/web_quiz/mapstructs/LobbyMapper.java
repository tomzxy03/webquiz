package com.tomzxy.web_quiz.mapstructs;


import com.tomzxy.web_quiz.dto.requests.Lobby.LobbyReqDTO;
import com.tomzxy.web_quiz.dto.responses.lobby.LobbyResDTO;
import com.tomzxy.web_quiz.models.Lobby;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LobbyMapper {
    LobbyMapper MAPPER = Mappers.getMapper(LobbyMapper.class);

    @Mapping(target = "codeInvite", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "quizzes", ignore = true)
    Lobby toLobby(LobbyReqDTO lobbyReqDTO);



    LobbyResDTO toLobbyResDTO(Lobby lobby);

    @Mapping(target = "codeInvite", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "quizzes", ignore = true)
    void updateLobby(@MappingTarget Lobby lobby , LobbyReqDTO lobbyReqDTO);

}
