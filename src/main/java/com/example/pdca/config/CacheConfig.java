package com.example.pdca.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置类
 * 配置应用程序的缓存策略
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    /**
     * 配置缓存管理器
     * 使用本地内存缓存
     */
    @Bean
    @Override
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "users",           // 用户缓存
            "plans",           // 计划缓存
            "tasks",           // 任务缓存
            "user_plans",      // 用户计划缓存
            "plan_details"     // 计划详情缓存
        );
    }
} 