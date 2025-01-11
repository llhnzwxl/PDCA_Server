package com.example.pdca.dto;

import com.example.pdca.model.Act;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 行动数据传输对象
 * 用于创建和更新行动阶段信息
 */
@Data
@ApiModel(description = "行动阶段数据传输对象")
public class ActDTO {
    @ApiModelProperty(value = "行动阶段ID", example = "1")
    private Long id;

    @NotBlank(message = "行动标题不能为空")
    @ApiModelProperty(value = "行动标题", required = true, example = "系统架构优化")
    private String title;

    @NotBlank(message = "行动描述不能为空")
    @ApiModelProperty(value = "行动描述", required = true, example = "根据检查结果优化系统架构")
    private String description;

    @NotNull(message = "关联检查阶段ID不能为空")
    @ApiModelProperty(value = "关联检查阶段ID", required = true, example = "1")
    private Long checkPhaseId;

    @NotNull(message = "执行人ID不能为空")
    @ApiModelProperty(value = "执行人ID", required = true, example = "1")
    private Long executorId;

    @NotNull(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间", required = true, example = "2023-06-20T10:00:00")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间", example = "2023-07-20T18:00:00")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "行动状态", allowableValues = "NOT_STARTED, IN_PROGRESS, COMPLETED, SUSPENDED")
    private Act.ActStatus status;

    @ApiModelProperty(value = "行动记录列表")
    private List<ActRecordDTO> records;
} 