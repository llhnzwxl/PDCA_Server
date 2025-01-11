package com.example.pdca.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 执行（Do）实体类
 * 定义 PDCA 循环中的执行阶段
 */
@Data
@Entity
@Table(name = "pdca_do")
public class DoPhase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "执行标题不能为空")
    private String title;

    @NotBlank(message = "执行描述不能为空")
    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private DoStatus status = DoStatus.IN_PROGRESS;

    @OneToMany(mappedBy = "doPhase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoRecord> records;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        if (status == null) {
            status = DoStatus.IN_PROGRESS;
        }
    }

    /**
     * 执行状态枚举
     */
    public enum DoStatus {
        NOT_STARTED,   // 未开始
        IN_PROGRESS,   // 进行中
        COMPLETED,     // 已完成
        SUSPENDED      // 已暂停
    }
} 