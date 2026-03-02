package com.tomzxy.web_quiz.services;

import com.tomzxy.web_quiz.dto.requests.RoleReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.RoleResDTO;
import com.tomzxy.web_quiz.models.Role;

import java.util.List;
import java.util.Map;

public interface RoleService {

    PageResDTO<RoleResDTO> getAll(int page, int size);

    RoleResDTO create_Role(RoleReqDTO roleReqDto);

    RoleResDTO update_Role(Long role_id, RoleReqDTO roleReqDTO);

    RoleResDTO get_Role(Long role_id);

    void delete_Role(Long role_id);

    void addRolePermissionObject(Role role, Map<String, List<String>> objectModel);
}
