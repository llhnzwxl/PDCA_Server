package com.example.pdca.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * 记录计划和任务的关键点日志
 */
@Data
@Entity
@Table(name = "pdca_action_log")
public class ActionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_type")
    private LogType logType;

    @Column(length = 1000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
    }

    public enum LogType {
        MILESTONE,  // 里程碑
        PROGRESS,   // 进展
        RISK,       // 风险
        ISSUE,      // 问题
        DECISION,   // 决策
        CHANGE      // 变更
    }
} 