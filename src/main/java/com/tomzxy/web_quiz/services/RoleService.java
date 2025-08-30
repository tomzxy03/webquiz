package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.RoleReqDTO;
import com.tomzxy.web_quiz.dto.requests.RoleReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.RoleResDTO;

public interface RoleService {

    PageResDTO<RoleResDTO> getAll(int page, int size);

    RoleResDTO create_Role(RoleReqDTO roleReqDto);

    RoleResDTO update_Role(Long role_id, RoleReqDTO roleReqDTO);

    RoleResDTO get_Role(Long role_id);

    void delete_Role(Long role_id);
}
