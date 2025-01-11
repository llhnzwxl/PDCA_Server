package com.example.pdca.controller;

import com.example.pdca.service.CacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 缓存管理控制器
 * 提供缓存相关的 REST API
 */
@RestController
@RequestMapping("/api/cache")
@Api(tags = "缓存管理")
public class CacheController {

    @Autowired
    private CacheService cacheService;

    @DeleteMapping("/{cacheName}")
    @ApiOperation("清除指定缓存")
    public ResponseEntity<String> clearCache(@PathVariable String cacheName) {
        cacheService.clearCache(cacheName);
        return ResponseEntity.ok("缓存 " + cacheName + " 已清除");
    }

    @DeleteMapping("/all")
    @ApiOperation("清除所有缓存")
    public ResponseEntity<String> clearAllCaches() {
        cacheService.clearAllCaches();
        return ResponseEntity.ok("所有缓存已清除");
    }
} 