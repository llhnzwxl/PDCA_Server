package com.example.pdca.service.impl;

import com.example.pdca.dto.ActDTO;
import com.example.pdca.dto.ActRecordDTO;
import com.example.pdca.model.Act;
import com.example.pdca.model.ActRecord;
import com.example.pdca.model.Check;
import com.example.pdca.model.User;
import com.example.pdca.repository.ActRepository;
import com.example.pdca.repository.ActRecordRepository;
import com.example.pdca.repository.CheckRepository;
import com.example.pdca.repository.UserRepository;
import com.example.pdca.service.ActService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 行动服务实现类
 * 提供行动阶段相关的业务逻辑实现
 */
@Service
public class ActServiceImpl implements ActService {

    @Autowired
    private ActRepository actRepository;

    @Autowired
    private CheckRepository checkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActRecordRepository actRecordRepository;

    @Override
    @Transactional
    public Act createAct(ActDTO actDTO, User creator) {
        Act act = new Act();
        
        // 设置基本信息
        act.setTitle(actDTO.getTitle());
        act.setDescription(actDTO.getDescription());
        act.setStartTime(actDTO.getStartTime());
        act.setEndTime(actDTO.getEndTime());
        
        // 设置关联检查阶段
        Check checkPhase = checkRepository.findById(actDTO.getCheckPhaseId())
            .orElseThrow(() -> new RuntimeException("关联的检查阶段不存在"));
        act.setCheckPhase(checkPhase);
        
        // 设置执行人
        User executor = userRepository.findById(actDTO.getExecutorId())
            .orElseThrow(() -> new RuntimeException("指定的执行人不存在"));
        act.setExecutor(executor);
        
        // 设置状态
        act.setStatus(actDTO.getStatus() != null 
            ? actDTO.getStatus() 
            : Act.ActStatus.IN_PROGRESS);

        // 保存行动阶段
        return actRepository.save(act);
    }

    @Override
    @Transactional
    public Act updateAct(ActDTO actDTO) {
        Act existingAct = actRepository.findById(actDTO.getId())
            .orElseThrow(() -> new RuntimeException("行动阶段不存在"));

        // 更新基本信息
        existingAct.setTitle(actDTO.getTitle());
        existingAct.setDescription(actDTO.getDescription());
        existingAct.setStartTime(actDTO.getStartTime());
        existingAct.setEndTime(actDTO.getEndTime());

        // 更新状态
        if (actDTO.getStatus() != null) {
            existingAct.setStatus(actDTO.getStatus());
        }

        // 更新关联检查阶段（如果提供了新的检查阶段ID）
        if (actDTO.getCheckPhaseId() != null) {
            Check checkPhase = checkRepository.findById(actDTO.getCheckPhaseId())
                .orElseThrow(() -> new RuntimeException("关联的检查阶段不存在"));
            existingAct.setCheckPhase(checkPhase);
        }

        // 更新执行人（如果提供了新的执行人ID）
        if (actDTO.getExecutorId() != null) {
            User executor = userRepository.findById(actDTO.getExecutorId())
                .orElseThrow(() -> new RuntimeException("指定的执行人不存在"));
            existingAct.setExecutor(executor);
        }

        return actRepository.save(existingAct);
    }

    @Override
    @Transactional
    public Act addActRecord(ActRecordDTO actRecordDTO, User recorder) {
        Act act = actRepository.findById(actRecordDTO.getActId())
            .orElseThrow(() -> new RuntimeException("行动阶段不存在"));

        ActRecord actRecord = new ActRecord();
        actRecord.setContent(actRecordDTO.getContent());
        actRecord.setActPhase(act);
        actRecord.setRecorder(recorder);
        actRecord.setRecordTime(LocalDateTime.now());
        actRecord.setType(actRecordDTO.getType() != null 
            ? actRecordDTO.getType() 
            : ActRecord.RecordType.IMPROVEMENT);

        actRecordRepository.save(actRecord);

        // 重新加载行动阶段以获取最新记录
        return actRepository.findById(act.getId())
            .orElseThrow(() -> new RuntimeException("行动阶段不存在"));
    }

    @Override
    @Transactional
    public void deleteAct(Long actId) {
        Act act = actRepository.findById(actId)
            .orElseThrow(() -> new RuntimeException("行动阶段不存在"));
        
        actRepository.delete(act);
    }

    @Override
    public Act getActById(Long actId) {
        return actRepository.findById(actId)
            .orElseThrow(() -> new RuntimeException("行动阶段不存在"));
    }

    @Override
    public List<Act> getActsByExecutor(User executor) {
        return actRepository.findByExecutor(executor);
    }

    @Override
    public List<Act> getActsByStatus(Act.ActStatus status) {
        return actRepository.findByStatus(status);
    }

    @Override
    public Page<Act> getPagedActPhasesByUser(User user, Pageable pageable) {
        Page<Act> acts = actRepository.findByUserRelated(user, pageable);
        // 确保关联数据被加载
        acts.getContent().forEach(act -> {
            if (act.getCheckPhase() != null) {
                act.getCheckPhase().getId();
                if (act.getCheckPhase().getDoPhase() != null) {
                    act.getCheckPhase().getDoPhase().getId();
                    if (act.getCheckPhase().getDoPhase().getPlan() != null) {
                        act.getCheckPhase().getDoPhase().getPlan().getId();
                    }
                }
            }
        });
        return acts;
    }

    @Override
    public Page<Act> getPagedActPhasesByUserAndStatus(User user, Act.ActStatus status, Pageable pageable) {
        return actRepository.findByUserRelatedAndStatus(user, status, pageable);
    }
} 