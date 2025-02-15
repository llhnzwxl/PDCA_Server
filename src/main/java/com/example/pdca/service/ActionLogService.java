package com.example.pdca.service;

import com.example.pdca.model.ActionLog;
import com.example.pdca.dto.ActionLogDTO;
import com.example.pdca.model.Plan;
import com.example.pdca.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActionLogService {
    ActionLog createLog(ActionLogDTO logDTO);
    void deleteLog(Long logId);
    ActionLog getLogById(Long logId);
    List<ActionLog> getLogsByPlan(Long planId);
    List<ActionLog> getLogsByTask(Long taskId);
    Page<ActionLog> getPagedLogsByPlan(Long planId, Pageable pageable);
    Page<ActionLog> getPagedLogsByTask(Long taskId, Pageable pageable);
    List<ActionLog> getLogsByPlanWithTasks(Long planId);
} 