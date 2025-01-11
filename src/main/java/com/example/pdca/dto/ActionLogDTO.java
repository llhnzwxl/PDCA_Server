package com.example.pdca.dto;

import com.example.pdca.model.ActionLog;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@ApiModel(description = "操作日志数据传输对象")
public class ActionLogDTO {
    
    @ApiModelProperty(value = "日志类型")
    private ActionLog.LogType logType;

    @ApiModelProperty(value = "日志内容")
    private String content;

    @ApiModelProperty(value = "关联的计划ID")
    private Long planId;

    @ApiModelProperty(value = "关联的任务ID")
    private Long taskId;

    @ApiModelProperty(value = "创建者ID")
    private Long creatorId;
} 