package com.example.pdca.dto;

import com.example.pdca.model.User;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 用户数据传输对象
 * 用于注册和更新用户信息
 */
@Data
public class UserDTO {
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少6位")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;

    private User.UserRole role;

    private LocalDateTime createTime;

    private LocalDateTime lastLoginTime;

    private boolean enabled = true;
} 