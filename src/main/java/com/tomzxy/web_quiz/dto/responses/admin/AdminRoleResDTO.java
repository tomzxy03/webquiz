package com.tomzxy.web_quiz.dto.responses.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRoleResDTO {
    private Long id;
    private String name;
    private Integer userCount;
    private List<String> permissions;
}
