package com.scenic.manager.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 新增用户DTO
 */
@Data
public class UserAddDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotNull(message = "角色不能为空")
    private Integer role; // 1-超级管理员，2-普通管理员
}
