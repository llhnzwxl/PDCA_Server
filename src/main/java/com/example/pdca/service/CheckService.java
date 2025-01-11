package com.example.pdca.service;

import com.example.pdca.dto.CheckDTO;
import com.example.pdca.dto.CheckResultDTO;
import com.example.pdca.model.Check;
import com.example.pdca.model.User;

import java.util.List;

/**
 * 检查服务接口
 * 定义检查阶段相关的业务逻辑
 */
public interface CheckService {
    /**
     * 创建检查阶段
     * @param checkDTO 检查阶段数据传输对象
     * @param creator 创建者
     * @return 创建的检查阶段
     */
    Check createCheck(CheckDTO checkDTO, User creator);

    /**
     * 更新检查阶段
     * @param checkDTO 检查阶段数据传输对象
     * @return 更新后的检查阶段
     */
    Check updateCheck(CheckDTO checkDTO);

    /**
     * 添加检查结果
     * @param checkResultDTO 检查结果数据传输对象
     * @param recorder 记录人
     * @return 添加的检查阶段
     */
    Check addCheckResult(CheckResultDTO checkResultDTO, User recorder);

    /**
     * 删除检查阶段
     * @param checkId 检查阶段ID
     */
    void deleteCheck(Long checkId);

    /**
     * 根据ID获取检查阶段
     * @param checkId 检查阶段ID
     * @return 检查阶段
     */
    Check getCheckById(Long checkId);

    /**
     * 获取用户检查的所有阶段
     * @param checker 检查人
     * @return 检查阶段列表
     */
    List<Check> getChecksByChecker(User checker);

    /**
     * 根据状态获取检查阶段
     * @param status 检查阶段状态
     * @return 检查阶段列表
     */
    List<Check> getChecksByStatus(Check.CheckStatus status);
} 