package com.example.pdca.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 执行记录实体类
 * 记录执行阶段的具体操作和进展
 */
@Data
@Entity
@Table(name = "pdca_do_record")
public class DoRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "记录内容不能为空")
    @Column(length = 1000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "do_id")
    private DoPhase doPhase;

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
        PROGRESS,      // 进展记录
        PROBLEM,       // 问题记录
        SOLUTION,      // 解决方案
        MILESTONE      // 里程碑
    }
} 