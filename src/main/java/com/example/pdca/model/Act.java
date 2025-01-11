package com.example.pdca.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 行动（Act）实体类
 * 定义 PDCA 循环中的行动阶段
 */
@Data
@Entity
@Table(name = "pdca_act")
public class Act {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "行动标题不能为空")
    private String title;

    @NotBlank(message = "行动描述不能为空")
    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "check_id")
    private Check checkPhase;

    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private ActStatus status = ActStatus.IN_PROGRESS;

    @OneToMany(mappedBy = "actPhase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActRecord> records;

    /**
     * 行动状态枚举
     */
    public enum ActStatus {
        NOT_STARTED,   // 未开始
        IN_PROGRESS,   // 进行中
        COMPLETED,     // 已完成
        SUSPENDED      // 已暂停
    }
} 