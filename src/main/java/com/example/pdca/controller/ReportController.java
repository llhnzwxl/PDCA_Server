package com.example.pdca.controller;

import com.example.pdca.dto.ReportDTO;
import com.example.pdca.model.Report;
import com.example.pdca.model.User;
import com.example.pdca.model.Plan;
import com.example.pdca.model.Task;
import com.example.pdca.service.ReportService;
import com.example.pdca.service.UserService;
import com.example.pdca.service.PlanService;
import com.example.pdca.exception.BusinessException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.HttpStatus;

/**
 * 报告管理控制器
 * 提供报告相关的 REST API
 */
@RestController
@RequestMapping("/api/reports")
@Api(tags = "报告管理")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @Autowired
    private PlanService planService;

    @PostMapping
    @ApiOperation("创建报告")
    public ResponseEntity<Report> createReport(
        @Valid @RequestBody ReportDTO reportDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User creator = userService.findByUsername(username);

        Report createdReport = reportService.createReport(reportDTO, creator);
        return ResponseEntity.ok(createdReport);
    }

    @PostMapping("/generate/{planId}")
    @ApiOperation(value = "自动生成PDCA循环总结报告", notes = "根据计划ID自动生成PDCA报告，需要所有任务都已评分")
    @ApiResponses({
        @ApiResponse(code = 200, message = "生成成功"),
        @ApiResponse(code = 400, message = "存在未评分的任务"),
        @ApiResponse(code = 404, message = "计划不存在")
    })
    public ResponseEntity<ReportDTO> generateReport(
        @ApiParam(value = "计划ID", required = true) @PathVariable Long planId) {
        
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        // 获取计划信息
        Plan plan = planService.getPlanById(planId);
        
        // 检查所有任务是否已评分
        List<Task> unEvaluatedTasks = plan.getTasks().stream()
            .filter(task -> task.getScore() == null)
            .collect(Collectors.toList());
        
        if (!unEvaluatedTasks.isEmpty()) {
            throw new BusinessException("存在未评分的任务，无法生成报告。未评分任务数量：" + unEvaluatedTasks.size());
        }
        
        // 创建报告DTO
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setTitle(plan.getTitle() + " - PDCA循环总结报告");
        reportDTO.setPlanId(planId);
        reportDTO.setType(Report.ReportType.PDCA_CYCLE);
        
        // 生成报告内容
        StringBuilder summary = new StringBuilder();
        summary.append("本报告总结了计划「").append(plan.getTitle()).append("」的PDCA循环执行情况。\n");
        summary.append("计划开始时间：").append(plan.getStartTime()).append("\n");
        summary.append("计划结束时间：").append(plan.getEndTime()).append("\n");
        summary.append("任务完成情况：已完成 ").append(plan.getTasks().size()).append(" 个任务\n");
        reportDTO.setSummary(summary.toString());
        
        // 计算平均分
        double avgScore = plan.getTasks().stream()
            .mapToInt(Task::getScore)
            .average()
            .orElse(0.0);
        
        // 生成任务评价分析
        StringBuilder planningAnalysis = new StringBuilder();
        planningAnalysis.append("任务平均得分：").append(String.format("%.2f", avgScore)).append("\n");
        planningAnalysis.append("任务评价详情：\n");
        plan.getTasks().forEach(task -> {
            planningAnalysis.append("- ").append(task.getName())
                .append("（得分：").append(task.getScore()).append("）：")
                .append(task.getEvaluation()).append("\n");
        });
        reportDTO.setPlanningAnalysis(planningAnalysis.toString());
        
        // 创建报告
        Report report = reportService.createReport(reportDTO, currentUser);
        return ResponseEntity.ok(convertToDetailDTO(report));
    }

    @GetMapping("/export/pdf/{reportId}")
    @ApiOperation("导出报告为 PDF")
    public ResponseEntity<ByteArrayResource> exportReportToPDF(
        @PathVariable Long reportId) {
        ByteArrayOutputStream pdfStream = reportService.exportReportToPDF(reportId);
        
        ByteArrayResource resource = new ByteArrayResource(pdfStream.toByteArray());
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=report.pdf")
            .body(resource);
    }

    @GetMapping("/export/excel/{reportId}")
    @ApiOperation("导出报告为 Excel")
    public ResponseEntity<ByteArrayResource> exportReportToExcel(
        @PathVariable Long reportId) {
        ByteArrayOutputStream excelStream = reportService.exportReportToExcel(reportId);
        
        ByteArrayResource resource = new ByteArrayResource(excelStream.toByteArray());
        
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=report.xlsx")
            .body(resource);
    }

    @GetMapping
    @ApiOperation(value = "获取报告分页列表", notes = "分页获取所有报告")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功")
    })
    public ResponseEntity<Page<ReportDTO>> getReports(
        @ApiParam(value = "页码", defaultValue = "0") @RequestParam(defaultValue = "0") int page,
        @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") int size,
        @ApiParam(value = "报告类型", required = false) @RequestParam(required = false) Report.ReportType type,
        @ApiParam(value = "报告状态", required = false) @RequestParam(required = false) Report.ReportStatus status) {
        
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        
        Page<Report> reports;
        if (type != null) {
            reports = reportService.getPagedReportsByType(type, pageRequest);
        } else if (status != null) {
            reports = reportService.getPagedReportsByStatus(status, pageRequest);
        } else {
            reports = reportService.getPagedReportsByCreator(currentUser, pageRequest);
        }
        
        return ResponseEntity.ok(reports.map(this::convertToDTO));
    }

    private ReportDTO convertToDTO(Report report) {
        ReportDTO dto = new ReportDTO();
        dto.setId(report.getId());
        dto.setTitle(report.getTitle());
        dto.setSummary(report.getSummary());
        dto.setPlanningAnalysis(report.getPlanningAnalysis());
        dto.setDoingAnalysis(report.getDoingAnalysis());
        dto.setCheckingAnalysis(report.getCheckingAnalysis());
        dto.setActingAnalysis(report.getActingAnalysis());
        dto.setType(report.getType());
        dto.setStatus(report.getStatus());
        
        if (report.getPlan() != null) {
            dto.setPlanId(report.getPlan().getId());
        }
        
        return dto;
    }

    @GetMapping("/plan/{planId}")
    @ApiOperation(value = "获取计划的报告详情", notes = "通过计划ID获取PDCA循环总结报告")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "计划不存在"),
        @ApiResponse(code = 400, message = "计划尚未生成报告")
    })
    public ResponseEntity<ReportDTO> getReportByPlanId(
        @ApiParam(value = "计划ID", required = true) @PathVariable Long planId) {
        
        // 先检查计划是否存在
        Plan plan = planService.getPlanById(planId);
        if (plan == null) {
            throw new BusinessException("计划不存在", HttpStatus.NOT_FOUND);
        }
        
        try {
            Report report = reportService.getReportByPlanId(planId);
            return ResponseEntity.ok(convertToDetailDTO(report));
        } catch (BusinessException e) {
            // 如果是因为报告不存在而抛出的异常，返回特定的响应
            Map<String, Object> response = new HashMap<>();
            response.put("planId", planId);
            response.put("planTitle", plan.getTitle());
            response.put("message", "计划尚未生成PDCA循环总结报告");
            response.put("canGenerate", plan.getTasks().stream().allMatch(task -> task.getScore() != null));
            
            return ResponseEntity.status(HttpStatus.OK)
                .body(new ReportDTO() {{
                    setPlanId(planId);
                    setPlanTitle(plan.getTitle());
                    setStatus(Report.ReportStatus.NOT_GENERATED);
                    setCanGenerate(response.get("canGenerate").toString());
                    setMessage(response.get("message").toString());
                }});
        }
    }

    private ReportDTO convertToDetailDTO(Report report) {
        ReportDTO dto = new ReportDTO();
        dto.setId(report.getId());
        dto.setTitle(report.getTitle());
        dto.setSummary(report.getSummary());
        dto.setPlanningAnalysis(report.getPlanningAnalysis());
        dto.setDoingAnalysis(report.getDoingAnalysis());
        dto.setCheckingAnalysis(report.getCheckingAnalysis());
        dto.setActingAnalysis(report.getActingAnalysis());
        dto.setType(report.getType());
        dto.setStatus(report.getStatus());
        
        // 设置计划信息
        if (report.getPlan() != null) {
            dto.setPlanId(report.getPlan().getId());
            // 可以添加更多计划相关信息
        }
        
        // 设置创建者信息
        if (report.getCreator() != null) {
            dto.setCreatorId(report.getCreator().getId());
            dto.setCreatorUsername(report.getCreator().getUsername());
        }
        
        return dto;
    }

    @PutMapping("/{reportId}")
    @ApiOperation(value = "更新报告", notes = "根据报告ID更新报告信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 404, message = "报告不存在"),
        @ApiResponse(code = 400, message = "请求参数无效")
    })
    public ResponseEntity<ReportDTO> updateReport(
        @ApiParam(value = "报告ID", required = true) @PathVariable Long reportId,
        @Valid @RequestBody ReportDTO reportDTO) {
        
        // 确保路径ID和请求体ID匹配
        if (!reportId.equals(reportDTO.getId())) {
            throw new IllegalArgumentException("路径ID与请求体ID不匹配");
        }
        
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        // 检查权限（只有创建者或管理员可以更新）
        Report existingReport = reportService.getReportById(reportId);
        if (!existingReport.getCreator().getId().equals(currentUser.getId()) && 
            currentUser.getRole() != User.UserRole.ADMIN) {
            throw new RuntimeException("没有权限更新此报告");
        }
        
        Report updatedReport = reportService.updateReport(reportDTO);
        return ResponseEntity.ok(convertToDetailDTO(updatedReport));
    }

    @PostMapping("/{reportId}/submit")
    @ApiOperation(value = "提交报告", notes = "将报告状态更改为已完成")
    @ApiResponses({
        @ApiResponse(code = 200, message = "提交成功"),
        @ApiResponse(code = 404, message = "报告不存在"),
        @ApiResponse(code = 400, message = "报告状态不允许提交")
    })
    public ResponseEntity<ReportDTO> submitReport(
        @ApiParam(value = "报告ID", required = true) @PathVariable Long reportId) {
        
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        
        Report submittedReport = reportService.submitReport(reportId, currentUser);
        return ResponseEntity.ok(convertToDetailDTO(submittedReport));
    }

    // 其他 CRUD 方法类似
} 