package com.example.pdca.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(description = "登录响应")
public class LoginResponse {
    @ApiModelProperty(value = "JWT令牌")
    private String token;
} 