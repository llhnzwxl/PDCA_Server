package com.example.pdca.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 检查结果实体类
 * 记录检查阶段的具体结果和发现
 */
@Data
@Entity
@Table(name = "pdca_check_result")
public class CheckResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "结果内容不能为空")
    @Column(length = 1000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "check_id")
    private Check checkPhase;

    @ManyToOne
    @JoinColumn(name = "recorder_id")
    private User recorder;

    private LocalDateTime recordTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private ResultType type;

    /**
     * 结果类型枚举
     */
    public enum ResultType {
        ACHIEVEMENT,   // 成就
        ISSUE,         // 问题
        SUGGESTION,    // 建议
        DEVIATION      // 偏差
    }
} 