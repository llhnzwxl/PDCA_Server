package com.example.pdca.dto;

import com.example.pdca.model.Report;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 报告数据传输对象
 * 用于创建和传输报告信息
 */
@Data
@ApiModel(description = "报告数据传输对象")
public class ReportDTO {
    @ApiModelProperty(value = "报告ID", example = "1")
    private Long id;

    @NotBlank(message = "报告标题不能为空")
    @ApiModelProperty(value = "报告标题", required = true, example = "2023年Q2项目PDCA循环总结")
    private String title;

    @ApiModelProperty(value = "报告摘要", example = "对本季度项目进行全面回顾和总结")
    private String summary;

    @ApiModelProperty(value = "计划阶段分析")
    private String planningAnalysis;

    @ApiModelProperty(value = "执行阶段分析")
    private String doingAnalysis;

    @ApiModelProperty(value = "检查阶段分析")
    private String checkingAnalysis;

    @ApiModelProperty(value = "行动阶段分析")
    private String actingAnalysis;

    @ApiModelProperty(value = "关联计划ID", example = "1")
    private Long planId;

    @ApiModelProperty(value = "报告类型", allowableValues = "PDCA_CYCLE, PROJECT_SUMMARY, PERFORMANCE")
    private Report.ReportType type;

    @ApiModelProperty(value = "报告状态", allowableValues = "DRAFT, COMPLETED, REVIEWED, PUBLISHED")
    private Report.ReportStatus status;

    @ApiModelProperty(value = "创建者ID")
    private Long creatorId;

    @ApiModelProperty(value = "创建者用户名")
    private String creatorUsername;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "关联计划标题")
    private String planTitle;

    @ApiModelProperty(value = "关联计划描述")
    private String planDescription;

    @ApiModelProperty(value = "是否可以生成报告")
    private String canGenerate;

    @ApiModelProperty(value = "提示信息")
    private String message;
} 