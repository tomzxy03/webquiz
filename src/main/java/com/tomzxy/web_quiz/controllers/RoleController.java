package com.tomzxy.web_quiz.controllers;

import com.tomzxy.web_quiz.containts.ApiDefined;
import com.tomzxy.web_quiz.dto.requests.RoleReqDTO;
import com.tomzxy.web_quiz.dto.responses.DataResDTO;
import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import com.tomzxy.web_quiz.dto.responses.RoleResDTO;
import com.tomzxy.web_quiz.services.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = ApiDefined.Role.BASE)
@Tag(name = "Roles", description = "Role management APIs")
public class RoleController {
    
    private final RoleService roleService;

    @GetMapping()
    @Operation(summary = "Get all roles", description = "Retrieve all roles with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Roles retrieved successfully")
    })
    public DataResDTO<PageResDTO<RoleResDTO>> getAllRoles(
            @Parameter(description = "Page number (0-based)") @RequestParam @Min(0) int page,
            @Parameter(description = "Page size (minimum 10)") @RequestParam @Min(10) int size) {
        log.info("Get all roles");
        return DataResDTO.ok(roleService.getAll(page, size));
    }

    @GetMapping(ApiDefined.Role.ID)
    @Operation(summary = "Get role by ID", description = "Retrieve a role by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role found successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    public DataResDTO<RoleResDTO> getRole(
            @Parameter(description = "Role ID") @PathVariable Long roleId) {
        log.info("Get role by {}", roleId);
        return DataResDTO.ok(roleService.get_Role(roleId));
    }

    @PostMapping()
    @Operation(summary = "Create role", description = "Create a new role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Role created successfully",
            content = @Content(schema = @Schema(implementation = DataResDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public DataResDTO<RoleResDTO> createRole(@Valid @RequestBody RoleReqDTO roleReqDTO) {
        log.info("Create role");
        return DataResDTO.create(roleService.create_Role(roleReqDTO));
    }

    @PutMapping(ApiDefined.Role.ID)
    @Operation(summary = "Update role", description = "Update an existing role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role updated successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public DataResDTO<RoleResDTO> updateRole(
            @Parameter(description = "Role ID") @PathVariable Long roleId,
            @Valid @RequestBody RoleReqDTO roleReqDTO) {
        log.info("Update role with id {}", roleId);
        return DataResDTO.update(roleService.update_Role(roleId, roleReqDTO));
    }

    @DeleteMapping(ApiDefined.Role.ID)
    @Operation(summary = "Delete role", description = "Delete a role by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    public DataResDTO<Void> deleteRole(
            @Parameter(description = "Role ID") @PathVariable Long roleId) {
        log.info("Delete role with id {}", roleId);
        roleService.delete_Role(roleId);
        return DataResDTO.delete();
    }
} 