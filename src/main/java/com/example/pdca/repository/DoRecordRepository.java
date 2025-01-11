package com.example.pdca.repository;

import com.example.pdca.model.DoRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 执行记录数据访问接口
 * 提供基本的数据库操作方法
 */
@Repository
public interface DoRecordRepository extends JpaRepository<DoRecord, Long> {
    // 可以添加自定义查询方法
} 