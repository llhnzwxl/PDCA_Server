package com.example.pdca.repository;

import com.example.pdca.model.DoPhase;
import com.example.pdca.model.Plan;
import com.example.pdca.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 执行数据访问接口
 * 提供基本的数据库操作方法
 */
@Repository
public interface DoRepository extends JpaRepository<DoPhase, Long> {
    List<DoPhase> findByExecutor(User executor);
    List<DoPhase> findByStatus(DoPhase.DoStatus status);
    List<DoPhase> findByPlan(Plan plan);
    Page<DoPhase> findByStatus(DoPhase.DoStatus status, Pageable pageable);
} 