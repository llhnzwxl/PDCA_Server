package com.example.pdca.controller;

import com.example.pdca.dto.LoginRequest;
import com.example.pdca.dto.LoginResponse;
import com.example.pdca.dto.UserDTO;
import com.example.pdca.model.User;
import com.example.pdca.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户管理控制器
 * 提供用户注册、登录等REST API
 */
@RestController
@RequestMapping("/api/users")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ApiOperation("用户注册")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.register(userDTO));
    }

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        String token = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping
    @ApiOperation("获取所有用户")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/managers")
    @ApiOperation("获取所有负责人")
    public ResponseEntity<List<UserDTO>> getAllManagers() {
        List<UserDTO> managers = userService.getUsersByRole(User.UserRole.MANAGER);
        return ResponseEntity.ok(managers);
    }

    @GetMapping("/current")
    @ApiOperation("获取当前登录用户信息")
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        userDTO.setCreateTime(user.getCreateTime());
        userDTO.setLastLoginTime(user.getLastLoginTime());
        
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/count")
    @ApiOperation("获取用户总数")
    public ResponseEntity<Long> getUserCount() {
        long count = userService.getUserCount();
        return ResponseEntity.ok(count);
    }
} 