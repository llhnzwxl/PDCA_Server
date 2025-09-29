package com.example.pdca.service.impl;

import com.example.pdca.model.Task;
import com.example.pdca.model.User;
import com.example.pdca.model.ActionLog;
import com.example.pdca.dto.ActionLogDTO;
import com.example.pdca.dto.TaskEvaluationDTO;
import com.example.pdca.repository.TaskRepository;
import com.example.pdca.repository.UserRepository;
import com.example.pdca.service.TaskService;
import com.example.pdca.service.ActionLogService;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActionLogService actionLogService;

    @Override
    @Transactional
    public Task assignTask(Long taskId, Long assigneeId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("任务不存在"));
            
        User assignee = null;
        if (assigneeId != null) {
            assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
            task.setAssignee(assignee);
        } else {
            task.setAssignee(null);
        }
        
        Task updatedTask = taskRepository.save(task);
        
        // 记录分配日志
        ActionLogDTO logDTO = new ActionLogDTO();
        logDTO.setLogType(ActionLog.LogType.CHANGE);
        logDTO.setContent("任务: " + task.getName() + 
            (assignee != null ? " 分配给: " + assignee.getUsername() : " 取消分配"));
        logDTO.setTaskId(taskId);
        logDTO.setPlanId(task.getPlan().getId());
        logDTO.setCreatorId(task.getPlan().getCreator().getId());
        actionLogService.createLog(logDTO);
        
        return updatedTask;
    }

    @Override
    public Task updateTaskStatus(Long taskId, Task.TaskStatus status) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("任务不存在"));
            
        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        
        // 记录状态变更日志
        ActionLogDTO logDTO = new ActionLogDTO();
        logDTO.setLogType(ActionLog.LogType.PROGRESS);
        logDTO.setContent("任务: " + task.getName() + " 状态更新为: " + status);
        logDTO.setTaskId(taskId);
        logDTO.setPlanId(task.getPlan().getId());
        logDTO.setCreatorId(task.getPlan().getCreator().getId());
        actionLogService.createLog(logDTO);
        
        return updatedTask;
    }

    @Override
    public Page<Task> getPagedTasks(Pageable pageable) {
        return taskRepository.findAllWithPlanAndAssignee(pageable);
    }

    @Override
    public Page<Task> getPagedTasksByStatus(Task.TaskStatus status, Pageable pageable) {
        return taskRepository.findByStatusWithPlanAndAssignee(status, pageable);
    }

    @Override
    public Task partialUpdateTask(Long taskId, Map<String, Object> updates) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("任务不存在"));

        BeanWrapper wrapper = new BeanWrapperImpl(task);
        
        updates.forEach((key, value) -> {
            if (wrapper.isWritableProperty(key)) {
                // 特殊处理负责人和状态
                if ("assignee".equals(key) && value instanceof Long) {
                    User assignee = userRepository.findById((Long) value)
                        .orElseThrow(() -> new RuntimeException("指定的负责人不存在"));
                    task.setAssignee(assignee);
                } else if ("status".equals(key) && value instanceof Task.TaskStatus) {
                    task.setStatus((Task.TaskStatus) value);
                } else {
                    wrapper.setPropertyValue(key, value);
                }
            }
        });

        Task updatedTask = taskRepository.save(task);
        
        // 记录更新日志
        ActionLogDTO logDTO = new ActionLogDTO();
        logDTO.setLogType(ActionLog.LogType.CHANGE);
        logDTO.setContent("更新了任务: " + task.getName());
        logDTO.setTaskId(taskId);
        logDTO.setPlanId(task.getPlan().getId());
        logDTO.setCreatorId(task.getPlan().getCreator().getId());
        actionLogService.createLog(logDTO);
        
        return updatedTask;
    }

    @Override
    public Page<Task> getPagedTasksByUser(User user, Pageable pageable) {
        return taskRepository.findByUserRelated(user, pageable);
    }

    @Override
    public Page<Task> getPagedTasksByUserAndStatus(User user, Task.TaskStatus status, Pageable pageable) {
        return taskRepository.findByUserRelatedAndStatus(user, status, pageable);
    }

    @Override
    public List<Task> getTasksByAssignee(User assignee) {
        return taskRepository.findByAssignee(assignee);
    }

    @Override
    public List<Task> getTasksByAssigneeAndStatus(User assignee, Task.TaskStatus status) {
        return taskRepository.findByAssigneeAndStatus(assignee, status);
    }

    @Override
    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("任务不存在: " + taskId));
    }

    @Override
    public Task evaluateTask(Long taskId, TaskEvaluationDTO evaluationDTO, User evaluator) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("任务不存在"));

        // 检查权限：只有计划创建者可以评价
        if (!task.getPlan().getCreator().getId().equals(evaluator.getId())) {
            throw new RuntimeException("只有计划创建者可以评价任务");
        }

        // 更新任务评价信息
        task.setScore(evaluationDTO.getScore());
        task.setEvaluation(evaluationDTO.getEvaluation());
        task.setEvaluateTime(LocalDateTime.now());
        
        Task updatedTask = taskRepository.save(task);

        // 记录评价日志
        ActionLogDTO logDTO = new ActionLogDTO();
        logDTO.setLogType(ActionLog.LogType.MILESTONE);
        logDTO.setContent("完成任务评价 - 得分: " + evaluationDTO.getScore());
        logDTO.setTaskId(taskId);
        logDTO.setPlanId(task.getPlan().getId());
        logDTO.setCreatorId(evaluator.getId());
        actionLogService.createLog(logDTO);

        return updatedTask;
    }
} 