package com.example.pdca.repository;

import com.example.pdca.model.CheckResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 检查结果数据访问接口
 * 提供基本的数据库操作方法
 */
@Repository
public interface CheckResultRepository extends JpaRepository<CheckResult, Long> {
    // 可以添加自定义查询方法
} 