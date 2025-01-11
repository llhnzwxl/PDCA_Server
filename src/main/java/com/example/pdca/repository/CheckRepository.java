package com.example.pdca.repository;

import com.example.pdca.model.Check;
import com.example.pdca.model.DoPhase;
import com.example.pdca.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 检查数据访问接口
 * 提供基本的数据库操作方法
 */
@Repository
public interface CheckRepository extends JpaRepository<Check, Long> {
    List<Check> findByChecker(User checker);
    List<Check> findByDoPhase(DoPhase doPhase);
    List<Check> findByStatus(Check.CheckStatus status);
} 