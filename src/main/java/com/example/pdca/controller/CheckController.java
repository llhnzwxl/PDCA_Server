package com.example.pdca.controller;

import com.example.pdca.dto.CheckDTO;
import com.example.pdca.dto.CheckResultDTO;
import com.example.pdca.model.Check;
import com.example.pdca.model.User;
import com.example.pdca.service.CheckService;
import com.example.pdca.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 检查阶段管理控制器
 * 提供检查阶段相关的 REST API
 */
@RestController
@RequestMapping("/api/check-phases")
@Api(tags = "检查阶段管理")
public class CheckController {

    @Autowired
    private CheckService checkService;

    @Autowired
    private UserService userService;

    @PostMapping
    @ApiOperation("创建检查阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "创建成功"),
        @ApiResponse(code = 400, message = "参数验证失败"),
        @ApiResponse(code = 401, message = "未授权")
    })
    public ResponseEntity<Check> createCheckPhase(
        @Valid @RequestBody CheckDTO checkDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User creator = userService.findByUsername(username);

        Check createdCheckPhase = checkService.createCheck(checkDTO, creator);
        return ResponseEntity.ok(createdCheckPhase);
    }

    @PutMapping("/{checkPhaseId}")
    @ApiOperation("更新检查阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 400, message = "参数验证失败"),
        @ApiResponse(code = 404, message = "检查阶段未找到")
    })
    public ResponseEntity<Check> updateCheckPhase(
        @PathVariable Long checkPhaseId, 
        @Valid @RequestBody CheckDTO checkDTO) {
        checkDTO.setId(checkPhaseId);
        Check updatedCheckPhase = checkService.updateCheck(checkDTO);
        return ResponseEntity.ok(updatedCheckPhase);
    }

    @PostMapping("/{checkPhaseId}/results")
    @ApiOperation("添加检查结果")
    @ApiResponses({
        @ApiResponse(code = 200, message = "结果添加成功"),
        @ApiResponse(code = 400, message = "参数验证失败"),
        @ApiResponse(code = 404, message = "检查阶段未找到")
    })
    public ResponseEntity<Check> addCheckResult(
        @PathVariable Long checkPhaseId, 
        @Valid @RequestBody CheckResultDTO checkResultDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User recorder = userService.findByUsername(username);

        checkResultDTO.setCheckId(checkPhaseId);
        Check updatedCheckPhase = checkService.addCheckResult(checkResultDTO, recorder);
        return ResponseEntity.ok(updatedCheckPhase);
    }

    @DeleteMapping("/{checkPhaseId}")
    @ApiOperation("删除检查阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 404, message = "检查阶段未找到")
    })
    public ResponseEntity<Void> deleteCheckPhase(@PathVariable Long checkPhaseId) {
        checkService.deleteCheck(checkPhaseId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{checkPhaseId}")
    @ApiOperation("根据ID获取检查阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "检查阶段未找到")
    })
    public ResponseEntity<Check> getCheckPhaseById(@PathVariable Long checkPhaseId) {
        Check checkPhase = checkService.getCheckById(checkPhaseId);
        return ResponseEntity.ok(checkPhase);
    }

    @GetMapping("/my-check-phases")
    @ApiOperation("获取当前用户的检查阶段")
    public ResponseEntity<List<Check>> getMyCheckPhases() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User checker = userService.findByUsername(username);

        List<Check> checkPhases = checkService.getChecksByChecker(checker);
        return ResponseEntity.ok(checkPhases);
    }

    @GetMapping("/status/{status}")
    @ApiOperation("根据状态获取检查阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 400, message = "无效的状态")
    })
    public ResponseEntity<List<Check>> getCheckPhasesByStatus(
        @PathVariable Check.CheckStatus status) {
        List<Check> checkPhases = checkService.getChecksByStatus(status);
        return ResponseEntity.ok(checkPhases);
    }
} 