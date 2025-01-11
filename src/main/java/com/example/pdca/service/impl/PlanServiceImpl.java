package com.example.pdca.service.impl;

import com.example.pdca.dto.PlanDTO;
import com.example.pdca.dto.TaskDTO;
import com.example.pdca.dto.DoDTO;
import com.example.pdca.dto.ActionLogDTO;
import com.example.pdca.model.DoPhase;
import com.example.pdca.model.Plan;
import com.example.pdca.model.Task;
import com.example.pdca.model.User;
import com.example.pdca.model.ActionLog;
import com.example.pdca.repository.PlanRepository;
import com.example.pdca.repository.TaskRepository;
import com.example.pdca.repository.UserRepository;
import com.example.pdca.service.PlanService;
import com.example.pdca.service.DoService;
import com.example.pdca.service.ActionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 计划服务实现类
 * 提供计划相关的业务逻辑实现
 */
@Service
@Transactional
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final DoService doService;
    private final ActionLogService actionLogService;

    @Autowired
    public PlanServiceImpl(PlanRepository planRepository, UserRepository userRepository, TaskRepository taskRepository, DoService doService, ActionLogService actionLogService) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.doService = doService;
        this.actionLogService = actionLogService;
    }

    @Override
    @Transactional
    public Plan createPlan(PlanDTO planDTO, User creator) {
        // 创建计划实体
        Plan plan = new Plan();
        plan.setTitle(planDTO.getTitle());
        plan.setDescription(planDTO.getDescription());
        plan.setPriority(planDTO.getPriority());
        plan.setStartTime(planDTO.getStartTime());
        plan.setEndTime(planDTO.getEndTime());
        plan.setCreator(creator);
        plan.setKeyIndicators(planDTO.getKeyIndicators());
        plan.setStatus(Plan.PlanStatus.PLANNING);

        // 保存计划
        Plan savedPlan = planRepository.save(plan);

        // 记录创建日志
        ActionLogDTO logDTO = new ActionLogDTO();
        logDTO.setLogType(ActionLog.LogType.MILESTONE);
        logDTO.setContent("创建了计划: " + plan.getTitle());
        logDTO.setPlanId(savedPlan.getId());
        logDTO.setCreatorId(creator.getId());
        actionLogService.createLog(logDTO);

        // 处理任务
        if (planDTO.getTasks() != null && !planDTO.getTasks().isEmpty()) {
            List<Task> tasks = planDTO.getTasks().stream().map(taskDTO -> {
                Task task = new Task();
                task.setName(taskDTO.getName());
                task.setDescription(taskDTO.getDescription());
                task.setStartTime(taskDTO.getStartTime());
                task.setEndTime(taskDTO.getEndTime());
                task.setPlan(savedPlan);

                // 设置任务负责人
                if (taskDTO.getAssigneeId() != null) {
                    User assignee = userRepository.findById(taskDTO.getAssigneeId())
                        .orElseThrow(() -> new RuntimeException("指定的任务负责人不存在"));
                    task.setAssignee(assignee);
                }

                task.setStatus(Task.TaskStatus.TODO);
                return task;
            }).collect(Collectors.toList());

            // 保存任务
            taskRepository.saveAll(tasks);
            savedPlan.setTasks(tasks);
        }

        return savedPlan;
    }

    @Override
    @Transactional
    public Plan updatePlan(PlanDTO planDTO) {
        // 查找现有计划
        Plan existingPlan = planRepository.findById(planDTO.getId())
            .orElseThrow(() -> new RuntimeException("计划不存在"));

        // 更新计划基本信息
        existingPlan.setTitle(planDTO.getTitle());
        existingPlan.setDescription(planDTO.getDescription());
        existingPlan.setPriority(planDTO.getPriority());
        existingPlan.setStartTime(planDTO.getStartTime());
        existingPlan.setEndTime(planDTO.getEndTime());
        existingPlan.setKeyIndicators(planDTO.getKeyIndicators());
        existingPlan.setStatus(planDTO.getStatus());

        // 更新任务
        if (planDTO.getTasks() != null) {
            // 删除旧任务
            taskRepository.deleteAll(existingPlan.getTasks());

            // 添加新任务
            List<Task> updatedTasks = planDTO.getTasks().stream().map(taskDTO -> {
                Task task = new Task();
                task.setName(taskDTO.getName());
                task.setDescription(taskDTO.getDescription());
                task.setStartTime(taskDTO.getStartTime());
                task.setEndTime(taskDTO.getEndTime());
                task.setPlan(existingPlan);

                // 设置任务负责人
                if (taskDTO.getAssigneeId() != null) {
                    User assignee = userRepository.findById(taskDTO.getAssigneeId())
                        .orElseThrow(() -> new RuntimeException("指定的任务负责人不存在"));
                    task.setAssignee(assignee);
                }

                task.setStatus(taskDTO.getStatus() != null ? taskDTO.getStatus() : Task.TaskStatus.TODO);
                return task;
            }).collect(Collectors.toList());

            taskRepository.saveAll(updatedTasks);
            existingPlan.setTasks(updatedTasks);
        }

        // 记录更新日志
        ActionLogDTO logDTO = new ActionLogDTO();
        logDTO.setLogType(ActionLog.LogType.CHANGE);
        logDTO.setContent("更新了计划: " + existingPlan.getTitle());
        logDTO.setPlanId(existingPlan.getId());
        logDTO.setCreatorId(existingPlan.getCreator().getId());
        actionLogService.createLog(logDTO);

        return planRepository.save(existingPlan);
    }

    @Override
    @Transactional
    public void deletePlan(Long planId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("计划不存在"));
        
        // 记录删除日志
        ActionLogDTO logDTO = new ActionLogDTO();
        logDTO.setLogType(ActionLog.LogType.CHANGE);
        logDTO.setContent("删除了计划: " + plan.getTitle());
        logDTO.setPlanId(planId);
        logDTO.setCreatorId(plan.getCreator().getId());
        actionLogService.createLog(logDTO);
        
        // 删除关联的任务
        taskRepository.deleteAll(plan.getTasks());
        
        // 删除计划
        planRepository.delete(plan);
    }

    @Override
    public Plan getPlanById(Long planId) {
        return planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("计划不存在"));
    }

    @Override
    public List<Plan> getPlansByCreator(User creator) {
        return planRepository.findByCreator(creator);
    }

    @Override
    public List<Plan> getPlansByStatus(Plan.PlanStatus status) {
        return planRepository.findByStatus(status);
    }

    @Override
    public Page<Plan> getPagedPlans(Pageable pageable) {
        return planRepository.findAll(pageable);
    }

    @Override
    public Page<Plan> getPagedPlansByStatus(Plan.PlanStatus status, Pageable pageable) {
        return planRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Plan> getPagedPlansByPriority(Plan.PriorityLevel priority, Pageable pageable) {
        return planRepository.findByPriority(priority, pageable);
    }

    @Override
    public Page<Plan> getPagedPlansByStatusAndPriority(Plan.PlanStatus status, Plan.PriorityLevel priority, Pageable pageable) {
        return planRepository.findByStatusAndPriority(status, priority, pageable);
    }

    @Override
    @Transactional
    public Plan startPlan(Long planId, User starter) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("计划不存在"));
        
        // 检查计划状态
        if (plan.getStatus() != Plan.PlanStatus.PLANNING) {
            throw new RuntimeException("只有处于计划中状态的计划才能启动");
        }
        
        // 更新计划状态
        plan.setStatus(Plan.PlanStatus.IN_PROGRESS);
        Plan updatedPlan = planRepository.save(plan);
        
        // 创建执行阶段
        DoDTO doDTO = new DoDTO();
        doDTO.setTitle(plan.getTitle() + " - 执行阶段");
        doDTO.setDescription("计划 '" + plan.getTitle() + "' 的执行阶段");
        doDTO.setPlanId(plan.getId());
        doDTO.setStartTime(plan.getStartTime());
        doDTO.setEndTime(plan.getEndTime());
        doDTO.setExecutorId(starter.getId());
        doDTO.setStatus(DoPhase.DoStatus.IN_PROGRESS);
        
        doService.createDo(doDTO, starter);
        
        return updatedPlan;
    }

    @Override
    @Transactional
    public Plan completePlan(Long planId, User completer) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("计划不存在"));
        
        // 检查计划状态
        if (plan.getStatus() != Plan.PlanStatus.IN_PROGRESS) {
            throw new RuntimeException("只有进行中的计划才能完成");
        }
        
        // 更新计划状态
        plan.setStatus(Plan.PlanStatus.COMPLETED);
        Plan updatedPlan = planRepository.save(plan);
        
        // 更新所有相关任务状态为已完成
        List<Task> tasks = plan.getTasks();
        tasks.forEach(task -> {
            if (task.getStatus() != Task.TaskStatus.COMPLETED) {
                task.setStatus(Task.TaskStatus.COMPLETED);
            }
        });
        taskRepository.saveAll(tasks);
        
        return updatedPlan;
    }

    @Override
    public Page<Plan> getPagedPlansByUser(User user, Pageable pageable) {
        return planRepository.findByUserRelated(user, pageable);
    }

    @Override
    public Page<Plan> getPagedPlansByUserAndStatus(User user, Plan.PlanStatus status, Pageable pageable) {
        return planRepository.findByUserRelatedAndStatus(user, status, pageable);
    }
} 