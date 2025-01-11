package com.example.pdca.service;

/**
 * 缓存服务接口
 * 提供缓存管理的通用方法
 */
public interface CacheService {
    /**
     * 清除指定缓存
     * @param cacheName 缓存名称
     */
    void clearCache(String cacheName);

    /**
     * 清除所有缓存
     */
    void clearAllCaches();

    /**
     * 获取缓存中的项目数量
     * @param cacheName 缓存名称
     * @return 缓存项目数量
     */
    long getCacheSize(String cacheName);
} 