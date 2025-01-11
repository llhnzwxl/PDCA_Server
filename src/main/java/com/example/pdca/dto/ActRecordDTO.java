package com.example.pdca.dto;

import com.example.pdca.model.ActRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 行动记录数据传输对象
 * 用于创建和传输行动记录
 */
@Data
@ApiModel(description = "行动记录数据传输对象")
public class ActRecordDTO {
    @ApiModelProperty(value = "记录ID", example = "1")
    private Long id;

    @NotBlank(message = "记录内容不能为空")
    @ApiModelProperty(value = "记录内容", required = true, example = "优化数据库查询性能")
    private String content;

    @ApiModelProperty(value = "行动阶段ID", example = "1")
    private Long actId;

    @ApiModelProperty(value = "记录人ID", example = "1")
    private Long recorderId;

    @ApiModelProperty(value = "记录时间", example = "2023-06-25T14:30:00")
    private LocalDateTime recordTime;

    @ApiModelProperty(
        value = "记录类型", 
        allowableValues = "IMPROVEMENT, CORRECTION, PREVENTION, STANDARDIZATION",
        example = "IMPROVEMENT"
    )
    private ActRecord.RecordType type;
} 