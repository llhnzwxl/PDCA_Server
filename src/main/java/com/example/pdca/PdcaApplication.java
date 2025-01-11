package com.example.pdca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * PDCA 管理系统主应用程序入口
 * 
 * @author AI Assistant
 * @version 1.0
 * @description 项目启动类，配置 Spring Boot 应用程序的基本设置
 */
@SpringBootApplication
@EnableAsync // 启用异步方法支持
@EnableScheduling // 启用定时任务支持
@EnableAspectJAutoProxy // 启用 AOP 支持
public class PdcaApplication {

    /**
     * 应用程序主入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 启动 Spring Boot 应用程序
        SpringApplication.run(PdcaApplication.class, args);
    }
}
