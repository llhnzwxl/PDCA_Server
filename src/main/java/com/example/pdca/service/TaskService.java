package com.example.pdca.service;

import com.example.pdca.model.Task;
import com.example.pdca.model.User;
import com.example.pdca.dto.TaskEvaluationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface TaskService {
    /**
     * 分配任务负责人
     * @param taskId 任务ID
     * @param assigneeId 负责人ID
     * @return 更新后的任务
     */
    Task assignTask(Long taskId, Long assigneeId);

    /**
     * 更新任务状态
     * @param taskId 任务ID
     * @param status 新状态
     * @return 更新后的任务
     */
    Task updateTaskStatus(Long taskId, Task.TaskStatus status);

    /**
     * 获取任务分页列表
     * @param pageable 分页参数
     * @return 任务分页对象
     */
    Page<Task> getPagedTasks(Pageable pageable);

    /**
     * 根据状态获取任务分页列表
     * @param status 任务状态
     * @param pageable 分页参数
     * @return 任务分页对象
     */
    Page<Task> getPagedTasksByStatus(Task.TaskStatus status, Pageable pageable);

    /**
     * 部分更新任务
     * @param taskId 任务ID
     * @param updates 更新字段
     * @return 更新后的任务
     */
    Task partialUpdateTask(Long taskId, Map<String, Object> updates);

    /**
     * 获取用户相关的任务分页列表
     * @param user 用户
     * @param pageable 分页参数
     * @return 任务分页对象
     */
    Page<Task> getPagedTasksByUser(User user, Pageable pageable);

    /**
     * 根据状态获取用户相关的任务分页列表
     * @param user 用户
     * @param status 任务状态
     * @param pageable 分页参数
     * @return 任务分页对象
     */
    Page<Task> getPagedTasksByUserAndStatus(User user, Task.TaskStatus status, Pageable pageable);

    /**
     * 获取用户负责的所有任务
     * @param assignee 负责人
     * @return 任务列表
     */
    List<Task> getTasksByAssignee(User assignee);

    /**
     * 根据状态获取用户负责的任务
     * @param assignee 负责人
     * @param status 任务状态
     * @return 任务列表
     */
    List<Task> getTasksByAssigneeAndStatus(User assignee, Task.TaskStatus status);

    /**
     * 根据ID获取任务
     * @param taskId 任务ID
     * @return 任务信息
     */
    Task getTaskById(Long taskId);

    /**
     * 评价任务
     * @param taskId 任务ID
     * @param evaluationDTO 评价信息
     * @param evaluator 评价人（必须是计划创建者）
     * @return 更新后的任务
     */
    Task evaluateTask(Long taskId, TaskEvaluationDTO evaluationDTO, User evaluator);
} 