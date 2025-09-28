package com.tomzxy.web_quiz.services.impl;


import com.tomzxy.web_quiz.dto.requests.RoleReqDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.RoleResDTO;
import com.tomzxy.web_quiz.exception.NotFoundException;
import com.tomzxy.web_quiz.mapstructs.RoleMapper;
import com.tomzxy.web_quiz.repositories.RoleRepo;
import com.tomzxy.web_quiz.services.ConvertToPageResDTO;
import com.tomzxy.web_quiz.models.Role;
import com.tomzxy.web_quiz.services.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final RoleRepo roleRepo;
    private final RoleMapper roleMapper;
    private final ConvertToPageResDTO convertToPageResDTO;


    @Override
    public PageResDTO<RoleResDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Role> roles = roleRepo.findAllActive(true, pageable);
        return convertToPageResDTO.convertPageResponse(roles, pageable, roleMapper::toRoleResDTO);
    }

    @Override
    public RoleResDTO create_Role(RoleReqDTO roleReqDto) {
        Role role = roleMapper.toRole(roleReqDto);
        return roleMapper.toRoleResDTO(roleRepo.save(role));
    }

    @Override
    public RoleResDTO update_Role(Long role_id, RoleReqDTO roleReqDTO) {
        Role role = roleRepo.findByIdAndActive(role_id).orElseThrow(() -> new NotFoundException("Role not found"));
        roleMapper.updateRole(role, roleReqDTO);
        return roleMapper.toRoleResDTO(roleRepo.save(role));
    }

    @Override
    public RoleResDTO get_Role(Long role_id) {
        return roleMapper.toRoleResDTO(roleRepo.findByIdAndActive(role_id).orElseThrow(() -> new NotFoundException("Role not found")));
    }

    @Override
    public void delete_Role(Long role_id) {
        Role role = roleRepo.findByIdAndActive(role_id).orElseThrow(() -> new NotFoundException("Role not found"));
        role.setActive(false);
        roleRepo.save(role);
    }

    
}
