package com.example.pdca.dto;

import com.example.pdca.model.Plan;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 计划数据传输对象
 * 用于创建和更新计划
 */
@Data
public class PlanDTO {
    private Long id;

    @NotBlank(message = "计划标题不能为空")
    private String title;

    @NotBlank(message = "计划描述不能为空")
    private String description;

    @NotNull(message = "优先级不能为空")
    private Plan.PriorityLevel priority;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    private Long creatorId;
    private String creatorName;

    private List<TaskDTO> tasks;

    private String keyIndicators;

    private Plan.PlanStatus status;
} 