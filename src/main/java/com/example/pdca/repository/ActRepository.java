package com.example.pdca.repository;

import com.example.pdca.model.Act;
import com.example.pdca.model.Check;
import com.example.pdca.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 行动数据访问接口
 * 提供基本的数据库操作方法
 */
@Repository
public interface ActRepository extends JpaRepository<Act, Long> {
    List<Act> findByExecutor(User executor);
    List<Act> findByCheckPhase(Check checkPhase);
    List<Act> findByStatus(Act.ActStatus status);

    @Query("SELECT DISTINCT a FROM Act a WHERE a.executor = :user OR a.checkPhase.doPhase.plan.creator = :user")
    Page<Act> findByUserRelated(@Param("user") User user, Pageable pageable);

    @Query("SELECT DISTINCT a FROM Act a WHERE (a.executor = :user OR a.checkPhase.doPhase.plan.creator = :user) AND a.status = :status")
    Page<Act> findByUserRelatedAndStatus(
        @Param("user") User user, 
        @Param("status") Act.ActStatus status, 
        Pageable pageable);
} 