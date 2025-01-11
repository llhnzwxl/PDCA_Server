package com.example.pdca.repository;

import com.example.pdca.model.Plan;
import com.example.pdca.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 计划数据访问接口
 * 提供基本的数据库操作方法
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByCreator(User creator);
    List<Plan> findByStatus(Plan.PlanStatus status);
    List<Plan> findByPriority(Plan.PriorityLevel priority);
    Page<Plan> findAll(Pageable pageable);
    Page<Plan> findByStatus(Plan.PlanStatus status, Pageable pageable);
    Page<Plan> findByPriority(Plan.PriorityLevel priority, Pageable pageable);
    Page<Plan> findByStatusAndPriority(Plan.PlanStatus status, Plan.PriorityLevel priority, Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Plan p " +
           "LEFT JOIN FETCH p.tasks " +
           "WHERE p.creator = :user OR EXISTS (SELECT t FROM Task t WHERE t.plan = p AND t.assignee = :user)",
           countQuery = "SELECT COUNT(DISTINCT p) FROM Plan p " +
           "WHERE p.creator = :user OR EXISTS (SELECT t FROM Task t WHERE t.plan = p AND t.assignee = :user)")
    Page<Plan> findByUserRelated(@Param("user") User user, Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Plan p " +
           "LEFT JOIN FETCH p.tasks " +
           "WHERE (p.creator = :user OR EXISTS (SELECT t FROM Task t WHERE t.plan = p AND t.assignee = :user)) " +
           "AND p.status = :status",
           countQuery = "SELECT COUNT(DISTINCT p) FROM Plan p " +
           "WHERE (p.creator = :user OR EXISTS (SELECT t FROM Task t WHERE t.plan = p AND t.assignee = :user)) " +
           "AND p.status = :status")
    Page<Plan> findByUserRelatedAndStatus(
        @Param("user") User user, 
        @Param("status") Plan.PlanStatus status, 
        Pageable pageable);
} 