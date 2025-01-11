package com.example.pdca.dto;

import com.example.pdca.model.Check;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 检查数据传输对象
 * 用于创建和更新检查阶段信息
 */
@Data
@ApiModel(description = "检查阶段数据传输对象")
public class CheckDTO {
    @ApiModelProperty(value = "检查阶段ID", example = "1")
    private Long id;

    @NotBlank(message = "检查标题不能为空")
    @ApiModelProperty(value = "检查标题", required = true, example = "项目初期开发检查")
    private String title;

    @NotBlank(message = "检查描述不能为空")
    @ApiModelProperty(value = "检查描述", required = true, example = "评估系统架构设计和基础功能开发情况")
    private String description;

    @NotNull(message = "关联执行阶段ID不能为空")
    @ApiModelProperty(value = "关联执行阶段ID", required = true, example = "1")
    private Long doPhaseId;

    @NotNull(message = "检查人ID不能为空")
    @ApiModelProperty(value = "检查人ID", required = true, example = "1")
    private Long checkerId;

    @NotNull(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间", required = true, example = "2023-06-15T10:00:00")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间", example = "2023-06-16T18:00:00")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "检查状态", allowableValues = "NOT_STARTED, IN_PROGRESS, COMPLETED, SUSPENDED")
    private Check.CheckStatus status;

    @ApiModelProperty(value = "检查结果列表")
    private List<CheckResultDTO> results;
} 