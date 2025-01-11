package com.example.pdca.dto;

import com.example.pdca.model.ActionLog;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "操作日志简要信息")
public class ActionLogSimpleDTO {
    @ApiModelProperty(value = "日志类型")
    private ActionLog.LogType logType;

    @ApiModelProperty(value = "日志内容")
    private String content;

    @ApiModelProperty(value = "附件URL")
    private String attachmentUrl;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建者用户名")
    private String creatorName;

    @ApiModelProperty(value = "关联任务ID")
    private Long taskId;
} 