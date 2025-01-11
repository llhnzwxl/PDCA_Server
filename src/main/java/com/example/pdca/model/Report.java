package com.example.pdca.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 报告实体类
 * 记录 PDCA 循环的总结报告
 */
@Data
@Entity
@Table(name = "pdca_report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "报告标题不能为空")
    private String title;

    @Column(length = 2000)
    private String summary;

    @Column(length = 2000)
    private String planningAnalysis;

    @Column(length = 2000)
    private String doingAnalysis;

    @Column(length = 2000)
    private String checkingAnalysis;

    @Column(length = 2000)
    private String actingAnalysis;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    private LocalDateTime createdTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private ReportType type = ReportType.PDCA_CYCLE;

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.DRAFT;

    /**
     * 报告类型枚举
     */
    public enum ReportType {
        PDCA_CYCLE,        // PDCA 循环报告
        PROJECT_SUMMARY,   // 项目总结报告
        PERFORMANCE        // 绩效报告
    }

    /**
     * 报告状态枚举
     */
    public enum ReportStatus {
        DRAFT,             // 草稿
        COMPLETED,         // 完成
        REVIEWED,          // 已审核
        PUBLISHED          // 已发布
    }
} 