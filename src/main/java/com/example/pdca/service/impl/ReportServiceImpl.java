package com.example.pdca.service.impl;

import com.example.pdca.dto.ReportDTO;
import com.example.pdca.model.*;
import com.example.pdca.repository.*;
import com.example.pdca.service.ReportService;
import com.example.pdca.util.ExcelReportGenerator;
import com.example.pdca.util.PDFReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;

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
        // 获取计划信息
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("计划不存在"));
        
        // 获取相关的执行阶段
        List<DoPhase> doPhases = doRepository.findByPlan(plan);
        
        // 获取相关的检查阶段
        List<Check> checkPhases = checkRepository.findByDoPhase(
            doPhases.isEmpty() ? null : doPhases.get(0));
        
        // 获取相关的行动阶段
        List<Act> actPhases = actRepository.findByCheckPhase(
            checkPhases.isEmpty() ? null : checkPhases.get(0));
        
        // 创建报告
        Report report = new Report();
        report.setTitle(String.format("%s - PDCA循环总结报告", plan.getTitle()));
        report.setSummary(String.format("计划「%s」的PDCA循环总结报告", plan.getTitle()));
        report.setCreator(creator);
        report.setPlan(plan);
        report.setType(Report.ReportType.PDCA_CYCLE);
        report.setStatus(Report.ReportStatus.DRAFT);
        
        // 生成计划阶段分析
        report.setPlanningAnalysis(generatePlanningAnalysis(plan));
        
        // 生成执行阶段分析
        report.setDoingAnalysis(generateDoingAnalysis(doPhases));
        
        // 生成检查阶段分析
        report.setCheckingAnalysis(generateCheckingAnalysis(checkPhases));
        
        // 生成行动阶段分析
        report.setActingAnalysis(generateActingAnalysis(actPhases));
        
        return reportRepository.save(report);
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
} 