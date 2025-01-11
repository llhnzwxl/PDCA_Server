package com.example.pdca.service.impl;

import com.example.pdca.model.ActionLog;
import com.example.pdca.dto.ActionLogDTO;
import com.example.pdca.model.Plan;
import com.example.pdca.model.Task;
import com.example.pdca.model.User;
import com.example.pdca.repository.ActionLogRepository;
import com.example.pdca.repository.PlanRepository;
import com.example.pdca.repository.TaskRepository;
import com.example.pdca.repository.UserRepository;
import com.example.pdca.service.ActionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ActionLogServiceImpl implements ActionLogService {

    @Autowired
    private ActionLogRepository actionLogRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ActionLog createLog(ActionLogDTO logDTO) {
        ActionLog log = new ActionLog();
        log.setLogType(logDTO.getLogType());
        log.setContent(logDTO.getContent());

        if (logDTO.getPlanId() != null) {
            Plan plan = planRepository.findById(logDTO.getPlanId())
                .orElseThrow(() -> new RuntimeException("计划不存在"));
            log.setPlan(plan);
        }

        if (logDTO.getTaskId() != null) {
            Task task = taskRepository.findById(logDTO.getTaskId())
                .orElseThrow(() -> new RuntimeException("任务不存在"));
            log.setTask(task);
        }

        User creator = userRepository.findById(logDTO.getCreatorId())
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        log.setCreator(creator);

        return actionLogRepository.save(log);
    }

    @Override
    public void deleteLog(Long logId) {
        ActionLog log = actionLogRepository.findById(logId)
            .orElseThrow(() -> new RuntimeException("日志不存在"));
        actionLogRepository.delete(log);
    }

    @Override
    public ActionLog getLogById(Long logId) {
        return actionLogRepository.findById(logId)
            .orElseThrow(() -> new RuntimeException("日志不存在"));
    }

    @Override
    public List<ActionLog> getLogsByPlan(Long planId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("计划不存在"));
        return actionLogRepository.findByPlanOrderByCreateTimeDesc(plan);
    }

    @Override
    public List<ActionLog> getLogsByTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("任务不存在"));
        return actionLogRepository.findByTaskOrderByCreateTimeDesc(task);
    }

    @Override
    public Page<ActionLog> getPagedLogsByPlan(Long planId, Pageable pageable) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("计划不存在"));
        return actionLogRepository.findByPlanOrderByCreateTimeDesc(plan, pageable);
    }

    @Override
    public Page<ActionLog> getPagedLogsByTask(Long taskId, Pageable pageable) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("任务不存在"));
        return actionLogRepository.findByTaskOrderByCreateTimeDesc(task, pageable);
    }

    @Override
    public List<ActionLog> getLogsByPlanWithTasks(Long planId) {
        planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("计划不存在"));
        
        return actionLogRepository.findByPlanIdWithTasksOrderByCreateTimeDesc(planId);
    }
} 