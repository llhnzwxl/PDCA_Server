package com.example.pdca.service;

import com.example.pdca.dto.ReportDTO;
import com.example.pdca.model.Report;
import com.example.pdca.model.User;
import com.example.pdca.model.Task;

import java.io.ByteArrayOutputStream;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 报告服务接口
 * 定义报告相关的业务逻辑
 */
public interface ReportService {
    /**
     * 创建报告
     * @param reportDTO 报告数据传输对象
     * @param creator 创建者
     * @return 创建的报告
     */
    Report createReport(ReportDTO reportDTO, User creator);

    /**
     * 更新报告
     * @param reportDTO 报告数据传输对象
     * @return 更新后的报告
     */
    Report updateReport(ReportDTO reportDTO);

    /**
     * 删除报告
     * @param reportId 报告ID
     */
    void deleteReport(Long reportId);

    /**
     * 根据ID获取报告
     * @param reportId 报告ID
     * @return 报告
     */
    Report getReportById(Long reportId);

    /**
     * 获取用户创建的所有报告
     * @param creator 创建者
     * @return 报告列表
     */
    List<Report> getReportsByCreator(User creator);

    /**
     * 获取指定类型的报告
     * @param type 报告类型
     * @return 报告列表
     */
    List<Report> getReportsByType(Report.ReportType type);

    /**
     * 自动生成 PDCA 循环总结报告
     * @param planId 计划ID
     * @param creator 创建者
     * @return 生成的报告
     */
    Report generatePDCAReport(Long planId, User creator);

    /**
     * 导出报告为 PDF
     * @param reportId 报告ID
     * @return PDF 文件的字节数组
     */
    ByteArrayOutputStream exportReportToPDF(Long reportId);

    /**
     * 导出报告为 Excel
     * @param reportId 报告ID
     * @return Excel 文件的字节数组
     */
    ByteArrayOutputStream exportReportToExcel(Long reportId);

    /**
     * 获取报告分页列表
     * @param pageable 分页参数
     * @return 报告分页对象
     */
    Page<Report> getPagedReports(Pageable pageable);

    /**
     * 获取用户创建的报告分页列表
     * @param creator 创建者
     * @param pageable 分页参数
     * @return 报告分页对象
     */
    Page<Report> getPagedReportsByCreator(User creator, Pageable pageable);

    /**
     * 根据类型获取报告分页列表
     * @param type 报告类型
     * @param pageable 分页参数
     * @return 报告分页对象
     */
    Page<Report> getPagedReportsByType(Report.ReportType type, Pageable pageable);

    /**
     * 根据状态获取报告分页列表
     * @param status 报告状态
     * @param pageable 分页参数
     * @return 报告分页对象
     */
    Page<Report> getPagedReportsByStatus(Report.ReportStatus status, Pageable pageable);

    /**
     * 提交报告
     * @param reportId 报告ID
     * @param submitter 提交人
     * @return 更新后的报告
     */
    Report submitReport(Long reportId, User submitter);

    /**
     * 检查计划的任务评分情况
     * @param planId 计划ID
     * @return 未评分的任务列表
     */
    List<Task> checkUnEvaluatedTasks(Long planId);

    /**
     * 通过计划ID获取报告
     * @param planId 计划ID
     * @return 报告信息
     */
    Report getReportByPlanId(Long planId);
} 