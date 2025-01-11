package com.example.pdca.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
@ApiModel(description = "任务评价数据传输对象")
public class TaskEvaluationDTO {
    
    @ApiModelProperty(value = "任务得分(0-10分)", required = true, example = "8")
    @Min(value = 0, message = "分数不能小于0")
    @Max(value = 10, message = "分数不能大于10")
    private Integer score;

    @ApiModelProperty(value = "评价内容", required = true)
    @Size(max = 500, message = "评价内容不能超过500字")
    private String evaluation;
} 