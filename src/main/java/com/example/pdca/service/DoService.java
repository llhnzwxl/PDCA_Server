package com.example.pdca.service;

import com.example.pdca.dto.DoDTO;
import com.example.pdca.dto.DoRecordDTO;
import com.example.pdca.model.DoPhase;
import com.example.pdca.model.Plan;
import com.example.pdca.model.User;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Do阶段服务接口
 * 定义执行阶段相关的业务逻辑
 */
public interface DoService {
    /**
     * 创建执行阶段
     * @param doDTO 执行阶段数据传输对象
     * @param creator 创建者
     * @return 创建的执行阶段
     */
    DoPhase createDo(DoDTO doDTO, User creator);

    /**
     * 更新执行阶段
     * @param doDTO 执行阶段数据传输对象
     * @return 更新后的执行阶段
     */
    DoPhase updateDo(DoDTO doDTO);

    /**
     * 添加执行记录
     * @param doRecordDTO 执行记录数据传输对象
     * @param recorder 记录人
     * @return 添加的执行记录
     */
    DoPhase addDoRecord(DoRecordDTO doRecordDTO, User recorder);

    /**
     * 删除执行阶段
     * @param doId 执行阶段ID
     */
    void deleteDo(Long doId);

    /**
     * 根据ID获取执行阶段
     * @param doId 执行阶段ID
     * @return 执行阶段
     */
    DoPhase getDoById(Long doId);

    /**
     * 获取用户执行的所有阶段
     * @param executor 执行人
     * @return 执行阶段列表
     */
    List<DoPhase> getDosByExecutor(User executor);

    /**
     * 根据状态获取执行阶段
     * @param status 执行阶段状态
     * @return 执行阶段列表
     */
    List<DoPhase> getDosByStatus(DoPhase.DoStatus status);

    /**
     * 获取计划的所有执行阶段
     * @param plan 计划
     * @return 执行阶段列表
     */
    List<DoPhase> getDosByPlan(Plan plan);

    /**
     * 获取执行阶段分页列表
     * @param pageable 分页参数
     * @return 执行阶段分页对象
     */
    Page<DoPhase> getPagedDoPhases(Pageable pageable);

    /**
     * 根据状态获取执行阶段分页列表
     * @param status 执行阶段状态
     * @param pageable 分页参数
     * @return 执行阶段分页对象
     */
    Page<DoPhase> getPagedDoPhasesByStatus(DoPhase.DoStatus status, Pageable pageable);
} 