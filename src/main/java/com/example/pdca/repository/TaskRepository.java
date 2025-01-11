package com.example.pdca.repository;

import com.example.pdca.model.Task;
import com.example.pdca.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 任务数据访问接口
 * 提供基本的数据库操作方法
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // 可以添加自定义查询方法

    Page<Task> findByStatus(Task.TaskStatus status, Pageable pageable);

    @Query(value = "SELECT DISTINCT t FROM Task t " +
           "LEFT JOIN FETCH t.plan " +
           "LEFT JOIN FETCH t.assignee",
           countQuery = "SELECT COUNT(DISTINCT t) FROM Task t")
    Page<Task> findAllWithPlanAndAssignee(Pageable pageable);
    
    @Query(value = "SELECT DISTINCT t FROM Task t " +
           "LEFT JOIN FETCH t.plan " +
           "LEFT JOIN FETCH t.assignee " +
           "WHERE t.status = :status",
           countQuery = "SELECT COUNT(DISTINCT t) FROM Task t WHERE t.status = :status")
    Page<Task> findByStatusWithPlanAndAssignee(@Param("status") Task.TaskStatus status, Pageable pageable);

    @Query(value = "SELECT DISTINCT t FROM Task t " +
           "LEFT JOIN FETCH t.plan " +
           "LEFT JOIN FETCH t.assignee " +
           "WHERE t.assignee = :user OR t.plan.creator = :user",
           countQuery = "SELECT COUNT(DISTINCT t) FROM Task t " +
           "WHERE t.assignee = :user OR t.plan.creator = :user")
    Page<Task> findByUserRelated(@Param("user") User user, Pageable pageable);

    @Query(value = "SELECT DISTINCT t FROM Task t " +
           "LEFT JOIN FETCH t.plan " +
           "LEFT JOIN FETCH t.assignee " +
           "WHERE (t.assignee = :user OR t.plan.creator = :user) " +
           "AND t.status = :status",
           countQuery = "SELECT COUNT(DISTINCT t) FROM Task t " +
           "WHERE (t.assignee = :user OR t.plan.creator = :user) " +
           "AND t.status = :status")
    Page<Task> findByUserRelatedAndStatus(
        @Param("user") User user, 
        @Param("status") Task.TaskStatus status, 
        Pageable pageable);

    List<Task> findByAssignee(User assignee);
    List<Task> findByAssigneeAndStatus(User assignee, Task.TaskStatus status);
} 