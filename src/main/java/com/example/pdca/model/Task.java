package com.example.pdca.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 任务实体类
 * 定义计划中的具体任务
 */
@Data
@Entity
@Table(name = "pdca_task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "任务名称不能为空")
    private String name;

    @Column(length = 1000)
    private String description;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    @JsonBackReference
    private Plan plan;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.TODO;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    @Column(name = "score")
    private Integer score;  // 任务得分（0-100）

    @Column(name = "evaluation", length = 500)
    private String evaluation;  // 任务评价内容

    @Column(name = "evaluate_time")
    private LocalDateTime evaluateTime;  // 评价时间

    @PrePersist
    public void prePersist() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        if (status == null) {
            status = TaskStatus.TODO;
        }
    }

    /**
     * 任务状态枚举
     */
    public enum TaskStatus {
        TODO,           // 待办
        IN_PROGRESS,    // 进行中
        COMPLETED,      // 已完成
        BLOCKED         // 已阻塞
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        if (status == TaskStatus.COMPLETED) {
            this.completeTime = LocalDateTime.now();
        }
    }
} 