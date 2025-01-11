package com.example.pdca.config;

import com.example.pdca.model.User;
import com.example.pdca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 初始化数据配置
 * 在应用启动时创建默认管理员用户
 */
@Configuration
public class InitialDataConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            // 检查是否已存在管理员用户
            if (!userRepository.existsByUsername("admin")) {
                // 创建管理员用户
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123")); // 初始密码
                admin.setEmail("admin@pdca.com");
                admin.setRole(User.UserRole.ADMIN);
                admin.setEnabled(true);
                
                userRepository.save(admin);
                
                System.out.println("已创建管理员用户：");
                System.out.println("用户名: admin");
                System.out.println("密码: admin123");
            }

            // 创建测试用户
            if (!userRepository.existsByUsername("test")) {
                User testUser = new User();
                testUser.setUsername("test");
                testUser.setPassword(passwordEncoder.encode("test123")); // 初始密码
                testUser.setEmail("test@pdca.com");
                testUser.setRole(User.UserRole.USER);
                testUser.setEnabled(true);
                
                userRepository.save(testUser);
                
                System.out.println("已创建测试用户：");
                System.out.println("用户名: test");
                System.out.println("密码: test123");
            }

            // 创建项目经理用户
            if (!userRepository.existsByUsername("manager")) {
                User manager = new User();
                manager.setUsername("manager");
                manager.setPassword(passwordEncoder.encode("manager123")); // 初始密码
                manager.setEmail("manager@pdca.com");
                manager.setRole(User.UserRole.MANAGER);
                manager.setEnabled(true);
                
                userRepository.save(manager);
                
                System.out.println("已创建项目经理用户：");
                System.out.println("用户名: manager");
                System.out.println("密码: manager123");
            }
        };
    }
} 