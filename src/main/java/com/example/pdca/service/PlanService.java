package com.example.pdca.service;

import com.example.pdca.dto.PlanDTO;
import com.example.pdca.model.Plan;
import com.example.pdca.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 计划服务接口
 * 定义计划相关的业务逻辑
 */
public interface PlanService {
    /**
     * 创建计划
     * @param planDTO 计划数据传输对象
     * @param creator 创建者
     * @return 创建的计划
     */
    Plan createPlan(PlanDTO planDTO, User creator);

    /**
     * 更新计划
     * @param planDTO 计划数据传输对象
     * @return 更新后的计划
     */
    Plan updatePlan(PlanDTO planDTO);

    /**
     * 删除计划
     * @param planId 计划ID
     */
    void deletePlan(Long planId);

    /**
     * 根据ID获取计划
     * @param planId 计划ID
     * @return 计划
     */
    Plan getPlanById(Long planId);

    /**
     * 获取用户创建的所有计划
     * @param creator 创建者
     * @return 计划列表
     */
    List<Plan> getPlansByCreator(User creator);

    /**
     * 根据状态获取计划
     * @param status 计划状态
     * @return 计划列表
     */
    List<Plan> getPlansByStatus(Plan.PlanStatus status);

    /**
     * 获取分页的计划列表
     * @param pageable 分页参数
     * @return 计划分页对象
     */
    Page<Plan> getPagedPlans(Pageable pageable);

    /**
     * 根据状态获取分页的计划列表
     * @param status 计划状态
     * @param pageable 分页参数
     * @return 计划分页对象
     */
    Page<Plan> getPagedPlansByStatus(Plan.PlanStatus status, Pageable pageable);

    /**
     * 根据优先级获取分页的计划列表
     * @param priority 优先级
     * @param pageable 分页参数
     * @return 计划分页对象
     */
    Page<Plan> getPagedPlansByPriority(Plan.PriorityLevel priority, Pageable pageable);

    /**
     * 根据状态和优先级获取分页的计划列表
     * @param status 计划状态
     * @param priority 优先级
     * @param pageable 分页参数
     * @return 计划分页对象
     */
    Page<Plan> getPagedPlansByStatusAndPriority(Plan.PlanStatus status, Plan.PriorityLevel priority, Pageable pageable);

    /**
     * 启动计划
     * @param planId 计划ID
     * @param starter 启动人
     * @return 更新后的计划
     */
    Plan startPlan(Long planId, User starter);

    /**
     * 完成计划
     * @param planId 计划ID
     * @param completer 完成人
     * @return 更新后的计划
     */
    Plan completePlan(Long planId, User completer);

    /**
     * 获取用户相关的计划分页列表
     * @param user 用户
     * @param pageable 分页参数
     * @return 计划分页对象
     */
    Page<Plan> getPagedPlansByUser(User user, Pageable pageable);

    /**
     * 根据状态获取用户相关的计划分页列表
     * @param user 用户
     * @param status 计划状态
     * @param pageable 分页参数
     * @return 计划分页对象
     */
    Page<Plan> getPagedPlansByUserAndStatus(User user, Plan.PlanStatus status, Pageable pageable);
} 