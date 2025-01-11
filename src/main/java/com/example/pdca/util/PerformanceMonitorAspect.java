package com.example.pdca.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能监控切面
 * 记录方法调用次数和总耗时
 */
@Aspect
@Component
public class PerformanceMonitorAspect {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitorAspect.class);

    // 方法调用统计
    private final ConcurrentHashMap<String, AtomicLong> methodCallCount = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> methodTotalTime = new ConcurrentHashMap<>();

    @Pointcut("within(@org.springframework.stereotype.Service *) || " +
              "within(@org.springframework.web.bind.annotation.RestController *)")
    public void monitoredMethods() {}

    @Around("monitoredMethods()")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();

        // 记录调用次数
        methodCallCount.computeIfAbsent(methodName, k -> new AtomicLong(0)).incrementAndGet();

        long startTime = System.nanoTime();
        try {
            // 执行原方法
            Object result = joinPoint.proceed();

            // 计算耗时
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000; // 转换为毫秒

            // 记录总耗时
            methodTotalTime.computeIfAbsent(methodName, k -> new AtomicLong(0)).addAndGet(duration);

            // 周期性输出性能报告
            if (methodCallCount.get(methodName).get() % 100 == 0) {
                logPerformanceReport(methodName);
            }

            return result;
        } catch (Throwable throwable) {
            throw throwable;
        }
    }

    /**
     * 输出性能报告
     * @param methodName 方法名
     */
    private void logPerformanceReport(String methodName) {
        long callCount = methodCallCount.getOrDefault(methodName, new AtomicLong(0)).get();
        long totalTime = methodTotalTime.getOrDefault(methodName, new AtomicLong(0)).get();
        
        double avgTime = callCount > 0 ? (double) totalTime / callCount : 0;

        logger.info("性能报告 - 方法: {}, 调用次数: {}, 平均耗时: {:.2f} ms",
            methodName, callCount, avgTime);
    }

    /**
     * 获取方法调用统计信息
     * @return 性能统计映射
     */
    public ConcurrentHashMap<String, AtomicLong> getMethodCallCount() {
        return methodCallCount;
    }

    /**
     * 获取方法总耗时统计
     * @return 耗时统计映射
     */
    public ConcurrentHashMap<String, AtomicLong> getMethodTotalTime() {
        return methodTotalTime;
    }
} 