package com.example.pdca.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 行动记录实体类
 * 记录行动阶段的具体操作和进展
 */
@Data
@Entity
@Table(name = "pdca_act_record")
public class ActRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "记录内容不能为空")
    @Column(length = 1000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "act_id")
    private Act actPhase;

    @ManyToOne
    @JoinColumn(name = "recorder_id")
    private User recorder;

    private LocalDateTime recordTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private RecordType type;

    /**
     * 记录类型枚举
     */
    public enum RecordType {
        IMPROVEMENT,   // 改进措施
        CORRECTION,    // 纠正行动
        PREVENTION,    // 预防措施
        STANDARDIZATION // 标准化
    }
} 