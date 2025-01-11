package com.example.pdca.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 检查（Check）实体类
 * 定义 PDCA 循环中的检查阶段
 */
@Data
@Entity
@Table(name = "pdca_check")
public class Check {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "检查标题不能为空")
    private String title;

    @NotBlank(message = "检查描述不能为空")
    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "do_phase_id")
    private DoPhase doPhase;

    @ManyToOne
    @JoinColumn(name = "checker_id")
    private User checker;

    @NotNull(message = "检查开始时间不能为空")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private CheckStatus status = CheckStatus.IN_PROGRESS;

    @OneToMany(mappedBy = "checkPhase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CheckResult> results;

    /**
     * 检查状态枚举
     */
    public enum CheckStatus {
        NOT_STARTED,   // 未开始
        IN_PROGRESS,   // 进行中
        COMPLETED,     // 已完成
        SUSPENDED      // 已暂停
    }
} 