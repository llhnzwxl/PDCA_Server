package com.example.pdca.dto;

import com.example.pdca.model.Task;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * 任务数据传输对象
 * 用于创建和更新任务
 */
@Data
@ApiModel(description = "任务数据传输对象")
public class TaskDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Task.TaskStatus status;
    private LocalDateTime createTime;
    private LocalDateTime completeTime;
    
    // 计划相关信息
    private Long planId;
    private String planTitle;
    
    // 负责人信息（仅返回必要字段）
    private Long assigneeId;
    private String assigneeUsername;

    @ApiModelProperty(value = "任务得分")
    private Integer score;

    @ApiModelProperty(value = "评价内容")
    private String evaluation;

    @ApiModelProperty(value = "评价时间")
    private LocalDateTime evaluateTime;
} 