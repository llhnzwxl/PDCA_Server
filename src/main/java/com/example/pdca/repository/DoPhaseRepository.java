package com.example.pdca.repository;

import com.example.pdca.model.DoPhase;
import com.example.pdca.model.Plan;
import com.example.pdca.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 执行阶段数据访问接口
 * 提供基本的数据库操作方法
 */
@Repository
public interface DoPhaseRepository extends JpaRepository<DoPhase, Long> {
    List<DoPhase> findByExecutor(User executor);
    List<DoPhase> findByPlan(Plan plan);
    List<DoPhase> findByStatus(DoPhase.DoStatus status);
} 