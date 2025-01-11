package com.example.pdca.service;

import com.example.pdca.dto.ActDTO;
import com.example.pdca.dto.ActRecordDTO;
import com.example.pdca.model.Act;
import com.example.pdca.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 行动服务接口
 * 定义行动阶段相关的业务逻辑
 */
public interface ActService {
    /**
     * 创建行动阶段
     * @param actDTO 行动阶段数据传输对象
     * @param creator 创建者
     * @return 创建的行动阶段
     */
    Act createAct(ActDTO actDTO, User creator);

    /**
     * 更新行动阶段
     * @param actDTO 行动阶段数据传输对象
     * @return 更新后的行动阶段
     */
    Act updateAct(ActDTO actDTO);

    /**
     * 添加行动记录
     * @param actRecordDTO 行动记录数据传输对象
     * @param recorder 记录人
     * @return 添加的行动阶段
     */
    Act addActRecord(ActRecordDTO actRecordDTO, User recorder);

    /**
     * 删除行动阶段
     * @param actId 行动阶段ID
     */
    void deleteAct(Long actId);

    /**
     * 根据ID获取行动阶段
     * @param actId 行动阶段ID
     * @return 行动阶段
     */
    Act getActById(Long actId);

    /**
     * 获取用户执行的所有行动阶段
     * @param executor 执行人
     * @return 行动阶段列表
     */
    List<Act> getActsByExecutor(User executor);

    /**
     * 根据状态获取行动阶段
     * @param status 行动阶段状态
     * @return 行动阶段列表
     */
    List<Act> getActsByStatus(Act.ActStatus status);

    /**
     * 获取用户相关的行动阶段分页列表
     * @param user 用户
     * @param pageable 分页参数
     * @return 行动阶段分页对象
     */
    Page<Act> getPagedActPhasesByUser(User user, Pageable pageable);

    /**
     * 根据状态获取用户相关的行动阶段分页列表
     * @param user 用户
     * @param status 行动状态
     * @param pageable 分页参数
     * @return 行动阶段分页对象
     */
    Page<Act> getPagedActPhasesByUserAndStatus(User user, Act.ActStatus status, Pageable pageable);
} 