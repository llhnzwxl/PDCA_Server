package com.example.pdca.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 定义用户基本信息和权限
 */
@Data
@Entity
@Table(name = "sys_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Column(unique = true, length = 50)
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @Email(message = "邮箱格式不正确")
    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;  // 用户角色

    private Boolean enabled = true;  // 账号是否可用

    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime lastLoginTime;  // 最后登录时间

    /**
     * 用户角色枚举
     */
    public enum UserRole {
        ADMIN,      // 管理员
        USER,       // 普通用户
        MANAGER     // 项目经理
    }
} 