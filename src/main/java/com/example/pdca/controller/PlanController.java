package com.example.pdca.controller;

import com.example.pdca.dto.PlanDTO;
import com.example.pdca.dto.TaskDTO;
import com.example.pdca.model.Plan;
import com.example.pdca.model.User;
import com.example.pdca.service.PlanService;
import com.example.pdca.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
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
import java.util.stream.Collectors;

/**
 * 计划管理控制器
 * 提供计划相关的 REST API
 */
@RestController
@RequestMapping("/api/plans")
@Api(tags = "计划管理")
public class PlanController {

    @Autowired
    private PlanService planService;

    @Autowired
    private UserService userService;

    @PostMapping
    @ApiOperation("创建计划")
    public ResponseEntity<Plan> createPlan(@Valid @RequestBody PlanDTO planDTO) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User creator = userService.findByUsername(username);

        Plan createdPlan = planService.createPlan(planDTO, creator);
        return ResponseEntity.ok(createdPlan);
    }

    @PutMapping("/{planId}")
    @ApiOperation("更新计划")
    public ResponseEntity<Plan> updatePlan(
            @PathVariable Long planId, 
            @Valid @RequestBody PlanDTO planDTO) {
        planDTO.setId(planId);
        Plan updatedPlan = planService.updatePlan(planDTO);
        return ResponseEntity.ok(updatedPlan);
    }

    @DeleteMapping("/{planId}")
    @ApiOperation("删除计划")
    public ResponseEntity<Void> deletePlan(@PathVariable Long planId) {
        planService.deletePlan(planId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{planId}")
    @ApiOperation("根据ID获取计划")
    public ResponseEntity<Plan> getPlanById(@PathVariable Long planId) {
        Plan plan = planService.getPlanById(planId);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/my-plans")
    @ApiOperation("获取当前用户的计划")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权")
    })
    public ResponseEntity<Page<PlanDTO>> getMyPlans(
        @ApiParam(value = "计划状态", required = false) 
        @RequestParam(required = false) Plan.PlanStatus status,
        @ApiParam(value = "页码", defaultValue = "0") @RequestParam(defaultValue = "0") int page,
        @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        // 创建分页请求
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        
        // 根据状态参数决定调用哪个服务方法
        Page<Plan> plans;
        if (status != null) {
            plans = planService.getPagedPlansByUserAndStatus(currentUser, status, pageRequest);
        } else {
            plans = planService.getPagedPlansByUser(currentUser, pageRequest);
        }
        
        // 将 Plan 转换为 PlanDTO
        return ResponseEntity.ok(plans.map(this::convertToDTO));
    }

    @GetMapping("/status/{status}")
    @ApiOperation("根据状态获取计划")
    public ResponseEntity<List<Plan>> getPlansByStatus(@PathVariable Plan.PlanStatus status) {
        List<Plan> plans = planService.getPlansByStatus(status);
        return ResponseEntity.ok(plans);
    }

    @GetMapping
    @ApiOperation(value = "获取计划分页列表", notes = "分页获取当前用户相关的所有计划")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功")
    })
    public ResponseEntity<Page<PlanDTO>> getPlans(
        @ApiParam(value = "页码", defaultValue = "0") @RequestParam(defaultValue = "0") int page,
        @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") int size,
        @ApiParam(value = "计划状态", required = false) @RequestParam(required = false) Plan.PlanStatus status) {
        
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        
        Page<Plan> plans;
        if (status != null) {
            plans = planService.getPagedPlansByUserAndStatus(currentUser, status, pageRequest);
        } else {
            plans = planService.getPagedPlansByUser(currentUser, pageRequest);
        }
        
        return ResponseEntity.ok(plans.map(this::convertToDTO));
    }

    @PatchMapping("/{planId}/status")
    @ApiOperation("更新计划状态")
    public ResponseEntity<Plan> updatePlanStatus(
            @PathVariable Long planId,
            @RequestParam Plan.PlanStatus status) {
        Plan plan = planService.getPlanById(planId);
        plan.setStatus(status);
        return ResponseEntity.ok(planService.updatePlan(convertToDTO(plan)));
    }

    @PostMapping("/{planId}/start")
    @ApiOperation(value = "启动计划", notes = "将计划状态从PLANNING更新为IN_PROGRESS")
    @ApiResponses({
        @ApiResponse(code = 200, message = "启动成功"),
        @ApiResponse(code = 400, message = "计划状态不允许启动"),
        @ApiResponse(code = 404, message = "计划不存在")
    })
    public ResponseEntity<Plan> startPlan(
        @ApiParam(value = "计划ID", required = true) @PathVariable Long planId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        Plan plan = planService.startPlan(planId, currentUser);
        return ResponseEntity.ok(plan);
    }

    @PostMapping("/{planId}/complete")
    @ApiOperation(value = "完成计划", notes = "将计划状态从IN_PROGRESS更新为COMPLETED")
    @ApiResponses({
        @ApiResponse(code = 200, message = "完成成功"),
        @ApiResponse(code = 400, message = "计划状态不允许完成"),
        @ApiResponse(code = 404, message = "计划不存在")
    })
    public ResponseEntity<Plan> completePlan(
        @ApiParam(value = "计划ID", required = true) @PathVariable Long planId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        Plan plan = planService.completePlan(planId, currentUser);
        return ResponseEntity.ok(plan);
    }

    private PlanDTO convertToDTO(Plan plan) {
        PlanDTO dto = new PlanDTO();
        dto.setId(plan.getId());
        dto.setTitle(plan.getTitle());
        dto.setDescription(plan.getDescription());
        dto.setPriority(plan.getPriority());
        dto.setStartTime(plan.getStartTime());
        dto.setEndTime(plan.getEndTime());
        dto.setStatus(plan.getStatus());
        dto.setKeyIndicators(plan.getKeyIndicators());
        
        // 设置创建者信息
        if (plan.getCreator() != null) {
            dto.setCreatorId(plan.getCreator().getId());
            dto.setCreatorName(plan.getCreator().getUsername());  // 添加创建者名称
        }
        
        // 设置任务信息
        if (plan.getTasks() != null) {
            List<TaskDTO> taskDTOs = plan.getTasks().stream()
                .map(task -> {
                    TaskDTO taskDTO = new TaskDTO();
                    taskDTO.setId(task.getId());
                    taskDTO.setName(task.getName());
                    taskDTO.setDescription(task.getDescription());
                    taskDTO.setStartTime(task.getStartTime());
                    taskDTO.setEndTime(task.getEndTime());
                    taskDTO.setStatus(task.getStatus());
                    taskDTO.setCompleteTime(task.getCompleteTime());  // 添加完成时间
                    
                    // 设置任务负责人信息
                    if (task.getAssignee() != null) {
                        taskDTO.setAssigneeId(task.getAssignee().getId());
                        taskDTO.setAssigneeUsername(task.getAssignee().getUsername());
                    }
                    
                    return taskDTO;
                })
                .collect(Collectors.toList());
            dto.setTasks(taskDTOs);
        }
        
        return dto;
    }
} 