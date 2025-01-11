package com.example.pdca.controller;

import com.example.pdca.model.Task;
import com.example.pdca.service.TaskService;
import com.example.pdca.dto.TaskDTO;
import com.example.pdca.model.User;
import com.example.pdca.service.UserService;
import com.example.pdca.dto.TaskEvaluationDTO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务管理控制器
 * 提供任务相关的 REST API
 */
@RestController
@RequestMapping("/api/tasks")
@Api(tags = "任务管理")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @PatchMapping("/{taskId}")
    @ApiOperation(value = "部分更新任务", notes = "根据任务ID部分更新任务信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 400, message = "参数无效"),
        @ApiResponse(code = 404, message = "任务不存在")
    })
    public ResponseEntity<Task> partialUpdateTask(
        @ApiParam(value = "任务ID", required = true) @PathVariable Long taskId,
        @ApiParam(value = "更新字段", required = true) @RequestBody Map<String, Object> updates) {
        
        Task updatedTask = taskService.partialUpdateTask(taskId, updates);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{taskId}/assignee")
    @ApiOperation(value = "分配任务负责人", notes = "根据任务ID和负责人ID分配任务")
    @ApiResponses({
        @ApiResponse(code = 200, message = "分配成功"),
        @ApiResponse(code = 404, message = "任务或用户不存在"),
        @ApiResponse(code = 400, message = "参数无效")
    })
    public ResponseEntity<TaskDTO> assignTask(
        @PathVariable Long taskId,
        @RequestBody Map<String, Long> request) {
        
        Long assigneeId = request.get("assigneeId");
        if (assigneeId == null) {
            throw new IllegalArgumentException("assigneeId 不能为空");
        }
        
        Task updatedTask = taskService.assignTask(taskId, assigneeId);
        return ResponseEntity.ok(convertToDTO(updatedTask));
    }

    @PatchMapping("/{taskId}/status")
    @ApiOperation(value = "更新任务状态", notes = "根据任务ID更新任务状态")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 404, message = "任务不存在"),
        @ApiResponse(code = 400, message = "无效的状态")
    })
    public ResponseEntity<Task> updateTaskStatus(
        @ApiParam(value = "任务ID", required = true) @PathVariable Long taskId,
        @ApiParam(value = "新的任务状态", required = true, 
                  allowableValues = "TODO,IN_PROGRESS,BLOCKED,COMPLETED") 
        @RequestParam Task.TaskStatus status) {
        Task updatedTask = taskService.updateTaskStatus(taskId, status);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping
    @ApiOperation(value = "获取任务分页列表", notes = "分页获取当前用户相关的所有任务")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功")
    })
    public ResponseEntity<Page<TaskDTO>> getTasks(
        @ApiParam(value = "页码", defaultValue = "0") @RequestParam(defaultValue = "0") int page,
        @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") int size,
        @ApiParam(value = "任务状态", required = false) @RequestParam(required = false) Task.TaskStatus status) {
        
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        
        Page<Task> tasks;
        if (status != null) {
            tasks = taskService.getPagedTasksByUserAndStatus(currentUser, status, pageRequest);
        } else {
            tasks = taskService.getPagedTasksByUser(currentUser, pageRequest);
        }
        
        return ResponseEntity.ok(tasks.map(this::convertToDTO));
    }

    @GetMapping("/my-tasks")
    @ApiOperation(value = "获取我的任务", notes = "获取当前登录用户负责的所有任务")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权")
    })
    public ResponseEntity<List<TaskDTO>> getMyTasks(
        @ApiParam(value = "任务状态", required = false) 
        @RequestParam(required = false) Task.TaskStatus status,
        @ApiParam(value = "页码", example = "0") 
        @RequestParam(defaultValue = "0") Integer page,
        @ApiParam(value = "每页大小", example = "10") 
        @RequestParam(defaultValue = "10") Integer size) {
        
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        // 创建分页请求
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        
        // 获取任务列表
        Page<Task> taskPage;
        if (status != null) {
            taskPage = taskService.getPagedTasksByUserAndStatus(currentUser, status, pageRequest);
        } else {
            taskPage = taskService.getPagedTasksByUser(currentUser, pageRequest);
        }
        
        // 转换为 DTO
        List<TaskDTO> taskDTOs = taskPage.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/{taskId}")
    @ApiOperation("获取任务详情")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "任务不存在")
    })
    public ResponseEntity<TaskDTO> getTaskById(
        @ApiParam(value = "任务ID", required = true) @PathVariable Long taskId) {
        
        Task task = taskService.getTaskById(taskId);
        TaskDTO dto = convertToDTO(task);
        
        // 添加评价相关信息到响应中
        dto.setScore(task.getScore());
        dto.setEvaluation(task.getEvaluation());
        dto.setEvaluateTime(task.getEvaluateTime());
        
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{taskId}/evaluate")
    @ApiOperation("评价任务")
    @ApiResponses({
        @ApiResponse(code = 200, message = "评价成功"),
        @ApiResponse(code = 400, message = "参数无效"),
        @ApiResponse(code = 403, message = "没有评价权限"),
        @ApiResponse(code = 404, message = "任务不存在")
    })
    public ResponseEntity<TaskDTO> evaluateTask(
        @ApiParam(value = "任务ID", required = true) @PathVariable Long taskId,
        @ApiParam(value = "评价信息", required = true) @Valid @RequestBody TaskEvaluationDTO evaluationDTO) {
        
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);

        Task evaluatedTask = taskService.evaluateTask(taskId, evaluationDTO, currentUser);
        return ResponseEntity.ok(convertToDTO(evaluatedTask));
    }

    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setStartTime(task.getStartTime());
        dto.setEndTime(task.getEndTime());
        dto.setStatus(task.getStatus());
        dto.setCreateTime(task.getCreateTime());
        dto.setCompleteTime(task.getCompleteTime());
        
        // 设置计划信息
        if (task.getPlan() != null) {
            dto.setPlanId(task.getPlan().getId());
            dto.setPlanTitle(task.getPlan().getTitle());
        }
        
        // 设置负责人信息
        if (task.getAssignee() != null) {
            dto.setAssigneeId(task.getAssignee().getId());
            dto.setAssigneeUsername(task.getAssignee().getUsername());
        }
        
        // 设置评价相关信息
        dto.setScore(task.getScore());
        dto.setEvaluation(task.getEvaluation());
        dto.setEvaluateTime(task.getEvaluateTime());
        
        return dto;
    }
} 