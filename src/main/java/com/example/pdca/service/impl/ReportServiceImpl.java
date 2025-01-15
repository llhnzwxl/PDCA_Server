package com.example.pdca.service.impl;

import com.example.pdca.dto.ReportDTO;
import com.example.pdca.model.*;
import com.example.pdca.repository.*;
import com.example.pdca.service.ReportService;
import com.example.pdca.service.PlanService;
import com.example.pdca.exception.BusinessException;
import com.example.pdca.util.ExcelReportGenerator;
import com.example.pdca.util.PDFReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 报告服务实现类
 * 提供报告相关的业务逻辑实现
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private DoRepository doRepository;

    @Autowired
    private CheckRepository checkRepository;

    @Autowired
    private ActRepository actRepository;

    @Autowired
    private PDFReportGenerator pdfReportGenerator;

    @Autowired
    private ExcelReportGenerator excelReportGenerator;

    @Autowired
    private PlanService planService;

    @Override
    @Transactional
    public Report createReport(ReportDTO reportDTO, User creator) {
        Report report = new Report();
        report.setTitle(reportDTO.getTitle());
        report.setSummary(reportDTO.getSummary());
        report.setPlanningAnalysis(reportDTO.getPlanningAnalysis());
        report.setDoingAnalysis(reportDTO.getDoingAnalysis());
        report.setCheckingAnalysis(reportDTO.getCheckingAnalysis());
        report.setActingAnalysis(reportDTO.getActingAnalysis());
        report.setCreator(creator);
        report.setType(reportDTO.getType() != null 
            ? reportDTO.getType() 
            : Report.ReportType.PDCA_CYCLE);
        report.setStatus(reportDTO.getStatus() != null 
            ? reportDTO.getStatus() 
            : Report.ReportStatus.DRAFT);

        if (reportDTO.getPlanId() != null) {
            Plan plan = planRepository.findById(reportDTO.getPlanId())
                .orElseThrow(() -> new RuntimeException("关联的计划不存在"));
            report.setPlan(plan);
        }

        return reportRepository.save(report);
    }

    @Override
    @Transactional
    public Report updateReport(ReportDTO reportDTO) {
        Report existingReport = reportRepository.findById(reportDTO.getId())
            .orElseThrow(() -> new RuntimeException("报告不存在"));

        // 更新基本信息
        existingReport.setTitle(reportDTO.getTitle());
        existingReport.setSummary(reportDTO.getSummary());
        existingReport.setPlanningAnalysis(reportDTO.getPlanningAnalysis());
        existingReport.setDoingAnalysis(reportDTO.getDoingAnalysis());
        existingReport.setCheckingAnalysis(reportDTO.getCheckingAnalysis());
        existingReport.setActingAnalysis(reportDTO.getActingAnalysis());
        
        // 更新类型和状态
        if (reportDTO.getType() != null) {
            existingReport.setType(reportDTO.getType());
        }
        
        if (reportDTO.getStatus() != null) {
            existingReport.setStatus(reportDTO.getStatus());
        }
        
        // 更新关联的计划
        if (reportDTO.getPlanId() != null) {
            Plan plan = planRepository.findById(reportDTO.getPlanId())
                .orElseThrow(() -> new RuntimeException("关联的计划不存在"));
            existingReport.setPlan(plan);
        }

        Report updatedReport = reportRepository.save(existingReport);
        
        // 确保关联数据被加载
        if (updatedReport.getPlan() != null) {
            updatedReport.getPlan().getId();
            updatedReport.getPlan().getTitle();
            updatedReport.getPlan().getDescription();
        }
        
        if (updatedReport.getCreator() != null) {
            updatedReport.getCreator().getId();
            updatedReport.getCreator().getUsername();
        }
        
        return updatedReport;
    }

    @Override
    @Transactional
    public void deleteReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("报告不存在"));
        
        reportRepository.delete(report);
    }

    @Override
    @Transactional(readOnly = true)
    public Report getReportById(Long reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("报告不存在: " + reportId));
        
        // 确保关联数据被加载
        if (report.getPlan() != null) {
            report.getPlan().getId(); // 触发懒加载
            report.getPlan().getTitle(); // 加载计划标题
            report.getPlan().getDescription(); // 加载计划描述
        }
        
        if (report.getCreator() != null) {
            report.getCreator().getId(); // 触发懒加载
            report.getCreator().getUsername(); // 加载创建者用户名
        }
        
        return report;
    }

    @Override
    public List<Report> getReportsByCreator(User creator) {
        return reportRepository.findByCreator(creator);
    }

    @Override
    public List<Report> getReportsByType(Report.ReportType type) {
        return reportRepository.findByType(type);
    }

    @Override
    @Transactional
    public Report generatePDCAReport(Long planId, User creator) {
        // 检查是否已经生成过报告
        List<Report> existingReports = reportRepository.findByPlanAndType(planId, Report.ReportType.PDCA_CYCLE);
        if (!existingReports.isEmpty()) {
            throw new BusinessException("该计划已生成PDCA循环总结报告，不能重复生成");
        }

        // 检查未评分的任务
        List<Task> unEvaluatedTasks = checkUnEvaluatedTasks(planId);
        if (!unEvaluatedTasks.isEmpty()) {
            throw new BusinessException("存在未评分的任务，无法生成报告。未评分任务数量：" + unEvaluatedTasks.size());
        }

        // 获取计划信息
        Plan plan = planService.getPlanById(planId);
        
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
        
        // 创建并返回报告
        return createReport(reportDTO, creator);
    }

    private String generatePlanningAnalysis(Plan plan) {
        return String.format("计划标题：%s\n" +
            "计划描述：%s\n" +
            "优先级：%s\n" +
            "创建者：%s\n" +
            "开始时间：%s\n" +
            "结束时间：%s\n" +
            "状态：%s\n" +
            "关键指标：%s\n",
            plan.getTitle(),
            plan.getDescription(),
            plan.getPriority(),
            plan.getCreator().getUsername(),
            plan.getStartTime(),
            plan.getEndTime(),
            plan.getStatus(),
            plan.getKeyIndicators()
        );
    }

    private String generateDoingAnalysis(List<DoPhase> doPhases) {
        if (doPhases.isEmpty()) {
            return "无执行阶段记录";
        }
        
        DoPhase doPhase = doPhases.get(0);
        StringBuilder analysis = new StringBuilder();
        analysis.append(String.format("执行标题：%s\n" +
            "执行描述：%s\n" +
            "执行人：%s\n" +
            "开始时间：%s\n" +
            "结束时间：%s\n" +
            "状态：%s\n",
            doPhase.getTitle(),
            doPhase.getDescription(),
            doPhase.getExecutor().getUsername(),
            doPhase.getStartTime(),
            doPhase.getEndTime(),
            doPhase.getStatus()
        ));
        
        // 添加执行记录
        if (doPhase.getRecords() != null && !doPhase.getRecords().isEmpty()) {
            analysis.append("\n执行记录：\n");
            doPhase.getRecords().forEach(record -> 
                analysis.append(String.format("- [%s] %s (%s)\n",
                    record.getType(),
                    record.getContent(),
                    record.getRecordTime()
                ))
            );
        }
        
        return analysis.toString();
    }

    private String generateCheckingAnalysis(List<Check> checkPhases) {
        if (checkPhases.isEmpty()) {
            return "无检查阶段记录";
        }
        
        Check checkPhase = checkPhases.get(0);
        StringBuilder analysis = new StringBuilder();
        analysis.append(String.format("检查标题：%s\n" +
            "检查描述：%s\n" +
            "检查人：%s\n" +
            "开始时间：%s\n" +
            "结束时间：%s\n" +
            "状态：%s\n",
            checkPhase.getTitle(),
            checkPhase.getDescription(),
            checkPhase.getChecker().getUsername(),
            checkPhase.getStartTime(),
            checkPhase.getEndTime(),
            checkPhase.getStatus()
        ));
        
        // 添加检查结果
        if (checkPhase.getResults() != null && !checkPhase.getResults().isEmpty()) {
            analysis.append("\n检查结果：\n");
            checkPhase.getResults().forEach(result -> 
                analysis.append(String.format("- [%s] %s (%s)\n",
                    result.getType(),
                    result.getContent(),
                    result.getRecordTime()
                ))
            );
        }
        
        return analysis.toString();
    }

    private String generateActingAnalysis(List<Act> actPhases) {
        if (actPhases.isEmpty()) {
            return "无行动阶段记录";
        }
        
        Act actPhase = actPhases.get(0);
        StringBuilder analysis = new StringBuilder();
        analysis.append(String.format("行动标题：%s\n" +
            "行动描述：%s\n" +
            "执行人：%s\n" +
            "开始时间：%s\n" +
            "结束时间：%s\n" +
            "状态：%s\n",
            actPhase.getTitle(),
            actPhase.getDescription(),
            actPhase.getExecutor().getUsername(),
            actPhase.getStartTime(),
            actPhase.getEndTime(),
            actPhase.getStatus()
        ));
        
        // 添加行动记录
        if (actPhase.getRecords() != null && !actPhase.getRecords().isEmpty()) {
            analysis.append("\n行动记录：\n");
            actPhase.getRecords().forEach(record -> 
                analysis.append(String.format("- [%s] %s (%s)\n",
                    record.getType(),
                    record.getContent(),
                    record.getRecordTime()
                ))
            );
        }
        
        return analysis.toString();
    }

    @Override
    public ByteArrayOutputStream exportReportToPDF(Long reportId) {
        Report report = getReportById(reportId);
        return pdfReportGenerator.generatePDF(report);
    }

    @Override
    public ByteArrayOutputStream exportReportToExcel(Long reportId) {
        Report report = getReportById(reportId);
        return excelReportGenerator.generateExcel(report);
    }

    @Override
    public Page<Report> getPagedReports(Pageable pageable) {
        return reportRepository.findAll(pageable);
    }

    @Override
    public Page<Report> getPagedReportsByCreator(User creator, Pageable pageable) {
        Page<Report> reports = reportRepository.findPageByCreator(creator, pageable);
        // 手动加载关联的计划数据
        reports.getContent().forEach(report -> {
            if (report.getPlan() != null) {
                report.getPlan().getId(); // 触发懒加载
            }
        });
        return reports;
    }

    @Override
    public Page<Report> getPagedReportsByType(Report.ReportType type, Pageable pageable) {
        return reportRepository.findPageByType(type, pageable);
    }

    @Override
    public Page<Report> getPagedReportsByStatus(Report.ReportStatus status, Pageable pageable) {
        return reportRepository.findPageByStatus(status, pageable);
    }

    @Override
    @Transactional
    public Report submitReport(Long reportId, User submitter) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("报告不存在"));
        
        // 检查权限
        if (!report.getCreator().getId().equals(submitter.getId()) && 
            submitter.getRole() != User.UserRole.ADMIN) {
            throw new RuntimeException("没有权限提交此报告");
        }
        
        // 检查状态
        if (report.getStatus() != Report.ReportStatus.DRAFT) {
            throw new RuntimeException("只有草稿状态的报告可以提交");
        }
        
        // 更新状态
        report.setStatus(Report.ReportStatus.COMPLETED);
        
        Report updatedReport = reportRepository.save(report);
        
        // 确保关联数据被加载
        if (updatedReport.getPlan() != null) {
            updatedReport.getPlan().getId();
            updatedReport.getPlan().getTitle();
        }
        
        return updatedReport;
    }

    @Override
    public List<Task> checkUnEvaluatedTasks(Long planId) {
        // 获取计划信息
        Plan plan = planService.getPlanById(planId);
        
        // 过滤出未评分的任务
        return plan.getTasks().stream()
            .filter(task -> task.getScore() == null)
            .collect(Collectors.toList());
    }

    @Override
    public Report getReportByPlanId(Long planId) {
        List<Report> reports = reportRepository.findByPlanAndType(planId, Report.ReportType.PDCA_CYCLE);
        if (reports.isEmpty()) {
            throw new BusinessException("该计划尚未生成PDCA循环总结报告");
        }
        
        Report report = reports.get(0);
        
        // 确保关联数据被加载
        if (report.getPlan() != null) {
            report.getPlan().getId();
            report.getPlan().getTitle();
            if (report.getPlan().getTasks() != null) {
                report.getPlan().getTasks().size();
            }
        }
        
        if (report.getCreator() != null) {
            report.getCreator().getId();
            report.getCreator().getUsername();
        }
        
        return report;
    }
} 