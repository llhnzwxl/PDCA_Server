package com.example.pdca.controller;

import com.example.pdca.dto.DoDTO;
import com.example.pdca.dto.DoRecordDTO;
import com.example.pdca.model.DoPhase;
import com.example.pdca.model.Plan;
import com.example.pdca.model.User;
import com.example.pdca.service.DoService;
import com.example.pdca.service.PlanService;
import com.example.pdca.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 执行阶段管理控制器
 * 提供执行阶段相关的 REST API
 */
@RestController
@RequestMapping("/api/do-phases")
@Api(tags = "执行阶段管理")
public class DoController {

    @Autowired
    private DoService doService;

    @Autowired
    private UserService userService;

    @Autowired
    private PlanService planService;

    @PostMapping
    @ApiOperation(value = "创建执行阶段", notes = "为计划创建一个新的执行阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "创建成功"),
        @ApiResponse(code = 400, message = "参数验证失败"),
        @ApiResponse(code = 404, message = "关联的计划或执行人不存在")
    })
    public ResponseEntity<DoPhase> createDoPhase(
        @ApiParam(value = "执行阶段详情", required = true) @Valid @RequestBody DoDTO doDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User creator = userService.findByUsername(username);
        
        DoPhase doPhase = doService.createDo(doDTO, creator);
        return ResponseEntity.ok(doPhase);
    }

    @PutMapping("/{doPhaseId}")
    @ApiOperation("更新执行阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 400, message = "参数验证失败"),
        @ApiResponse(code = 404, message = "执行阶段未找到")
    })
    public ResponseEntity<DoPhase> updateDoPhase(
        @PathVariable Long doPhaseId, 
        @Valid @RequestBody DoDTO doDTO) {
        doDTO.setId(doPhaseId);
        DoPhase updatedDoPhase = doService.updateDo(doDTO);
        return ResponseEntity.ok(updatedDoPhase);
    }

    @PostMapping("/{doPhaseId}/records")
    @ApiOperation("添加执行记录")
    @ApiResponses({
        @ApiResponse(code = 200, message = "记录添加成功"),
        @ApiResponse(code = 400, message = "参数验证失败"),
        @ApiResponse(code = 404, message = "执行阶段未找到")
    })
    public ResponseEntity<DoPhase> addDoRecord(
        @PathVariable Long doPhaseId, 
        @Valid @RequestBody DoRecordDTO doRecordDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User recorder = userService.findByUsername(username);

        doRecordDTO.setDoId(doPhaseId);
        DoPhase updatedDoPhase = doService.addDoRecord(doRecordDTO, recorder);
        return ResponseEntity.ok(updatedDoPhase);
    }

    @DeleteMapping("/{doPhaseId}")
    @ApiOperation("删除执行阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 404, message = "执行阶段未找到")
    })
    public ResponseEntity<Void> deleteDoPhase(@PathVariable Long doPhaseId) {
        doService.deleteDo(doPhaseId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{doPhaseId}")
    @ApiOperation("根据ID获取执行阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "执行阶段未找到")
    })
    public ResponseEntity<DoPhase> getDoPhaseById(@PathVariable Long doPhaseId) {
        DoPhase doPhase = doService.getDoById(doPhaseId);
        return ResponseEntity.ok(doPhase);
    }

    @GetMapping("/my-do-phases")
    @ApiOperation("获取当前用户的执行阶段")
    public ResponseEntity<List<DoPhase>> getMyDoPhases() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User executor = userService.findByUsername(username);

        List<DoPhase> doPhases = doService.getDosByExecutor(executor);
        return ResponseEntity.ok(doPhases);
    }

    @GetMapping("/status/{status}")
    @ApiOperation("根据状态获取执行阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 400, message = "无效的状态")
    })
    public ResponseEntity<List<DoPhase>> getDoPhasesByStatus(
        @PathVariable DoPhase.DoStatus status) {
        List<DoPhase> doPhases = doService.getDosByStatus(status);
        return ResponseEntity.ok(doPhases);
    }

    @GetMapping("/plan/{planId}")
    @ApiOperation(value = "获取计划的执行阶段", notes = "根据计划ID获取所有相关的执行阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "计划不存在")
    })
    public ResponseEntity<List<DoPhase>> getDoPhasesByPlan(
        @ApiParam(value = "计划ID", required = true) @PathVariable Long planId) {
        Plan plan = planService.getPlanById(planId);
        List<DoPhase> doPhases = doService.getDosByPlan(plan);
        return ResponseEntity.ok(doPhases);
    }

    @GetMapping
    @ApiOperation(value = "获取执行阶段分页列表", notes = "分页获取所有执行阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功")
    })
    public ResponseEntity<Page<DoPhase>> getDoPhases(
        @ApiParam(value = "页码", defaultValue = "0") @RequestParam(defaultValue = "0") int page,
        @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") int size,
        @ApiParam(value = "执行阶段状态", required = false) @RequestParam(required = false) DoPhase.DoStatus status) {
        
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        
        Page<DoPhase> doPhases;
        if (status != null) {
            doPhases = doService.getPagedDoPhasesByStatus(status, pageRequest);
        } else {
            doPhases = doService.getPagedDoPhases(pageRequest);
        }
        
        return ResponseEntity.ok(doPhases);
    }
} 