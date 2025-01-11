package com.example.pdca.controller;

import com.example.pdca.dto.ActionLogDTO;
import com.example.pdca.dto.ActionLogSimpleDTO;
import com.example.pdca.model.ActionLog;
import com.example.pdca.service.ActionLogService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
@Api(tags = "操作日志管理")
public class ActionLogController {

    @Autowired
    private ActionLogService actionLogService;

    @PostMapping
    @ApiOperation("创建日志")
    public ResponseEntity<ActionLog> createLog(@Valid @RequestBody ActionLogDTO logDTO) {
        return ResponseEntity.ok(actionLogService.createLog(logDTO));
    }

    @GetMapping("/plan/{planId}")
    @ApiOperation("获取计划相关的日志")
    public ResponseEntity<List<ActionLogSimpleDTO>> getLogsByPlan(@PathVariable Long planId) {
        List<ActionLog> logs = actionLogService.getLogsByPlanWithTasks(planId);
        
        List<ActionLogSimpleDTO> dtos = logs.stream()
            .map(this::convertToSimpleDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    private ActionLogSimpleDTO convertToSimpleDTO(ActionLog log) {
        ActionLogSimpleDTO dto = new ActionLogSimpleDTO();
        dto.setLogType(log.getLogType());
        dto.setContent(log.getContent());
//        dto.setAttachmentUrl(log.getAttachmentUrl());
        dto.setCreateTime(log.getCreateTime());
        
        if (log.getCreator() != null) {
            dto.setCreatorName(log.getCreator().getUsername());
        }
        
        if (log.getTask() != null) {
            dto.setTaskId(log.getTask().getId());
        }
        
        return dto;
    }

    @GetMapping("/task/{taskId}")
    @ApiOperation("获取任务相关的日志")
    public ResponseEntity<List<ActionLog>> getLogsByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(actionLogService.getLogsByTask(taskId));
    }
} 