package com.example.pdca.service.impl;

import com.example.pdca.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 缓存服务实现类
 * 提供缓存管理的具体实现
 */
@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private CacheManager cacheManager;

    @Override
    public void clearCache(String cacheName) {
        Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
    }

    @Override
    public void clearAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> 
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear()
        );
    }

    @Override
    public long getCacheSize(String cacheName) {
        // 注意：这里的实现取决于具体的缓存实现
        // 对于 ConcurrentMapCacheManager，可能需要自定义实现
        return 0;
    }
} 