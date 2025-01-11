package com.example.pdca.dto;

import com.example.pdca.model.CheckResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 检查结果数据传输对象
 * 用于创建和传输检查结果
 */
@Data
@ApiModel(description = "检查结果数据传输对象")
public class CheckResultDTO {
    @ApiModelProperty(value = "结果ID", example = "1")
    private Long id;

    @NotBlank(message = "结果内容不能为空")
    @ApiModelProperty(value = "结果内容", required = true, example = "系统架构设计符合预期")
    private String content;

    @ApiModelProperty(value = "检查阶段ID", example = "1")
    private Long checkId;

    @ApiModelProperty(value = "记录人ID", example = "1")
    private Long recorderId;

    @ApiModelProperty(value = "记录时间", example = "2023-06-15T14:30:00")
    private LocalDateTime recordTime;

    @ApiModelProperty(
        value = "结果类型", 
        allowableValues = "ACHIEVEMENT, ISSUE, SUGGESTION, DEVIATION",
        example = "ACHIEVEMENT"
    )
    private CheckResult.ResultType type;
} 