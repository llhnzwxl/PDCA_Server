package com.example.pdca.service;

import com.example.pdca.dto.UserDTO;
import com.example.pdca.model.User;

import java.util.List;

/**
 * 用户服务接口
 * 定义用户相关的业务逻辑
 */
public interface UserService {
    /**
     * 用户注册
     * @param userDTO 用户注册信息
     * @return 注册成功的用户
     */
    User register(UserDTO userDTO);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录令牌
     */
    String login(String username, String password);

    /**
     * 更新用户信息
     * @param userDTO 用户更新信息
     * @return 更新后的用户
     */
    User updateUser(UserDTO userDTO);

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);

    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<UserDTO> getAllUsers();

    /**
     * 根据角色获取用户
     * @param role 用户角色
     * @return 用户列表
     */
    List<UserDTO> getUsersByRole(User.UserRole role);

    /**
     * 获取用户总数
     * @return 用户总数
     */
    long getUserCount();
} 