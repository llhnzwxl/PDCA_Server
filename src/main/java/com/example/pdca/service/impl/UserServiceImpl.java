package com.example.pdca.service.impl;

import com.example.pdca.dto.UserDTO;
import com.example.pdca.exception.UserAlreadyExistsException;
import com.example.pdca.model.User;
import com.example.pdca.repository.UserRepository;
import com.example.pdca.service.UserService;
import com.example.pdca.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 * 提供用户注册、登录等业务逻辑
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional
    public User register(UserDTO userDTO) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new UserAlreadyExistsException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new UserAlreadyExistsException("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        
        // 密码加密
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        
        // 设置默认角色
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : User.UserRole.USER);
        
        // 设置创建时间
        user.setCreateTime(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    @Override
    public String login(String username, String password) {
        // 执行认证
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );

        // 设置安全上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 获取用户
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        // 生成 JWT 令牌
        return jwtTokenUtil.generateToken(username);
    }

    @Override
    @Transactional
    public User updateUser(UserDTO userDTO) {
        User existingUser = userRepository.findById(userDTO.getId())
            .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 更新用户信息，注意不要更新敏感信息
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setRole(userDTO.getRole());

        return userRepository.save(existingUser);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getUsersByRole(User.UserRole role) {
        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long getUserCount() {
        return userRepository.count();
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
} 