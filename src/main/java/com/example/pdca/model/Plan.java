package com.example.pdca.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 计划（Plan）实体类
 * 定义 PDCA 循环中的计划阶段
 */
@Data
@Entity
@Table(name = "pdca_plan")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "计划标题不能为空")
    private String title;

    @NotBlank(message = "计划描述不能为空")
    @Column(length = 1000)
    private String description;

    @NotNull(message = "优先级不能为空")
    @Enumerated(EnumType.STRING)
    private PriorityLevel priority;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    @JsonIgnore
    private User creator;

    @Transient
    private String creatorUsername;

    public String getCreatorUsername() {
        return creator != null ? creator.getUsername() : null;
    }

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Task> tasks;

    @Column(length = 500)
    private String keyIndicators;  // 关键指标

    @Enumerated(EnumType.STRING)
    private PlanStatus status = PlanStatus.PLANNING;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        if (status == null) {
            status = PlanStatus.PLANNING;
        }
    }

    /**
     * 计划优先级枚举
     */
    public enum PriorityLevel {
        LOW,        // 低优先级
        MEDIUM,     // 中优先级
        HIGH,       // 高优先级
        CRITICAL    // 关键优先级
    }

    /**
     * 计划状态枚举
     */
    public enum PlanStatus {
        PLANNING,   // 计划中
        APPROVED,   // 已批准
        IN_PROGRESS,// 进行中
        COMPLETED,  // 已完成
        SUSPENDED   // 已暂停
    }
} 