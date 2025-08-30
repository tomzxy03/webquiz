package com.tomzxy.web_quiz.services.impl;


import com.tomzxy.web_quiz.dto.requests.RoleReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.RoleResDTO;
import com.tomzxy.web_quiz.services.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {



    @Override
    public PageResDTO<RoleResDTO> getAll(int page, int size) {
        return null;
    }

    @Override
    public RoleResDTO create_Role(RoleReqDTO roleReqDto) {
        return null;
    }

    @Override
    public RoleResDTO update_Role(Long role_id, RoleReqDTO roleReqDTO) {
        return null;
    }

    @Override
    public RoleResDTO get_Role(Long role_id) {
        return null;
    }

    @Override
    public void delete_Role(Long role_id) {

    }
}
