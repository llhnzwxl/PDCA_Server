package com.example.pdca.controller;

import com.example.pdca.dto.ActDTO;
import com.example.pdca.dto.ActRecordDTO;
import com.example.pdca.model.Act;
import com.example.pdca.model.User;
import com.example.pdca.service.ActService;
import com.example.pdca.service.UserService;
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
import java.util.stream.Collectors;

/**
 * 行动阶段管理控制器
 * 提供行动阶段相关的 REST API
 */
@RestController
@RequestMapping("/api/act-phases")
@Api(tags = "行动阶段管理")
public class ActController {

    @Autowired
    private ActService actService;

    @Autowired
    private UserService userService;

    @PostMapping
    @ApiOperation("创建行动阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "创建成功"),
        @ApiResponse(code = 400, message = "参数验证失败"),
        @ApiResponse(code = 401, message = "未授权")
    })
    public ResponseEntity<Act> createActPhase(
        @Valid @RequestBody ActDTO actDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User creator = userService.findByUsername(username);

        Act createdActPhase = actService.createAct(actDTO, creator);
        return ResponseEntity.ok(createdActPhase);
    }

    @PutMapping("/{actPhaseId}")
    @ApiOperation("更新行动阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 400, message = "参数验证失败"),
        @ApiResponse(code = 404, message = "行动阶段未找到")
    })
    public ResponseEntity<Act> updateActPhase(
        @PathVariable Long actPhaseId, 
        @Valid @RequestBody ActDTO actDTO) {
        actDTO.setId(actPhaseId);
        Act updatedActPhase = actService.updateAct(actDTO);
        return ResponseEntity.ok(updatedActPhase);
    }

    @PostMapping("/{actPhaseId}/records")
    @ApiOperation("添加行动记录")
    @ApiResponses({
        @ApiResponse(code = 200, message = "记录添加成功"),
        @ApiResponse(code = 400, message = "参数验证失败"),
        @ApiResponse(code = 404, message = "行动阶段未找到")
    })
    public ResponseEntity<Act> addActRecord(
        @PathVariable Long actPhaseId, 
        @Valid @RequestBody ActRecordDTO actRecordDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User recorder = userService.findByUsername(username);

        actRecordDTO.setActId(actPhaseId);
        Act updatedActPhase = actService.addActRecord(actRecordDTO, recorder);
        return ResponseEntity.ok(updatedActPhase);
    }

    @DeleteMapping("/{actPhaseId}")
    @ApiOperation("删除行动阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 404, message = "行动阶段未找到")
    })
    public ResponseEntity<Void> deleteActPhase(@PathVariable Long actPhaseId) {
        actService.deleteAct(actPhaseId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{actPhaseId}")
    @ApiOperation("根据ID获取行动阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "行动阶段未找到")
    })
    public ResponseEntity<Act> getActPhaseById(@PathVariable Long actPhaseId) {
        Act actPhase = actService.getActById(actPhaseId);
        return ResponseEntity.ok(actPhase);
    }

    @GetMapping("/my-act-phases")
    @ApiOperation(value = "获取我的行动阶段分页列表", notes = "分页获取当前用户相关的所有行动阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功")
    })
    public ResponseEntity<Page<ActDTO>> getMyActPhases(
        @ApiParam(value = "页码", defaultValue = "0") @RequestParam(defaultValue = "0") int page,
        @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") int size,
        @ApiParam(value = "行动状态", required = false) @RequestParam(required = false) Act.ActStatus status) {
        
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        
        Page<Act> actPhases;
        if (status != null) {
            actPhases = actService.getPagedActPhasesByUserAndStatus(currentUser, status, pageRequest);
        } else {
            actPhases = actService.getPagedActPhasesByUser(currentUser, pageRequest);
        }
        
        return ResponseEntity.ok(actPhases.map(this::convertToDTO));
    }

    @GetMapping("/status/{status}")
    @ApiOperation("根据状态获取行动阶段")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 400, message = "无效的状态")
    })
    public ResponseEntity<List<Act>> getActPhasesByStatus(
        @PathVariable Act.ActStatus status) {
        List<Act> actPhases = actService.getActsByStatus(status);
        return ResponseEntity.ok(actPhases);
    }

    private ActDTO convertToDTO(Act act) {
        ActDTO dto = new ActDTO();
        dto.setId(act.getId());
        dto.setTitle(act.getTitle());
        dto.setDescription(act.getDescription());
        dto.setStartTime(act.getStartTime());
        dto.setEndTime(act.getEndTime());
        dto.setStatus(act.getStatus());
        
        if (act.getExecutor() != null) {
            dto.setExecutorId(act.getExecutor().getId());
        }
        
        if (act.getCheckPhase() != null) {
            dto.setCheckPhaseId(act.getCheckPhase().getId());
        }
        
        if (act.getRecords() != null) {
            dto.setRecords(act.getRecords().stream()
                .map(record -> {
                    ActRecordDTO recordDTO = new ActRecordDTO();
                    recordDTO.setActId(act.getId());
                    recordDTO.setContent(record.getContent());
                    recordDTO.setType(record.getType());
                    return recordDTO;
                })
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
} 