package com.example.pdca.repository;

import com.example.pdca.model.ActionLog;
import com.example.pdca.model.Plan;
import com.example.pdca.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
    List<ActionLog> findByPlan(Plan plan);
    List<ActionLog> findByTask(Task task);
    List<ActionLog> findByPlanOrderByCreateTimeDesc(Plan plan);
    List<ActionLog> findByTaskOrderByCreateTimeDesc(Task task);
    Page<ActionLog> findByPlanOrderByCreateTimeDesc(Plan plan, Pageable pageable);
    Page<ActionLog> findByTaskOrderByCreateTimeDesc(Task task, Pageable pageable);

    @Query("SELECT DISTINCT al FROM ActionLog al " +
           "LEFT JOIN Task t ON al.task = t " +
           "WHERE al.plan.id = :planId " +
           "OR t.plan.id = :planId " +
           "ORDER BY al.createTime DESC")
    List<ActionLog> findByPlanIdWithTasksOrderByCreateTimeDesc(@Param("planId") Long planId);
} 