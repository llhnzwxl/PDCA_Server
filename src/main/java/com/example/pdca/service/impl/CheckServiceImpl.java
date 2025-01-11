package com.example.pdca.service.impl;

import com.example.pdca.dto.CheckDTO;
import com.example.pdca.dto.CheckResultDTO;
import com.example.pdca.model.Check;
import com.example.pdca.model.CheckResult;
import com.example.pdca.model.DoPhase;
import com.example.pdca.model.User;
import com.example.pdca.repository.CheckRepository;
import com.example.pdca.repository.CheckResultRepository;
import com.example.pdca.repository.DoPhaseRepository;
import com.example.pdca.repository.UserRepository;
import com.example.pdca.service.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 检查服务实现类
 * 提供检查阶段相关的业务逻辑实现
 */
@Service
public class CheckServiceImpl implements CheckService {

    @Autowired
    private CheckRepository checkRepository;

    @Autowired
    private DoPhaseRepository doPhaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CheckResultRepository checkResultRepository;

    @Override
    @Transactional
    public Check createCheck(CheckDTO checkDTO, User creator) {
        Check check = new Check();
        
        // 设置基本信息
        check.setTitle(checkDTO.getTitle());
        check.setDescription(checkDTO.getDescription());
        check.setStartTime(checkDTO.getStartTime());
        check.setEndTime(checkDTO.getEndTime());
        
        // 设置关联执行阶段
        DoPhase doPhase = doPhaseRepository.findById(checkDTO.getDoPhaseId())
            .orElseThrow(() -> new RuntimeException("关联的执行阶段不存在"));
        check.setDoPhase(doPhase);
        
        // 设置检查人
        User checker = userRepository.findById(checkDTO.getCheckerId())
            .orElseThrow(() -> new RuntimeException("指定的检查人不存在"));
        check.setChecker(checker);
        
        // 设置状态
        check.setStatus(checkDTO.getStatus() != null 
            ? checkDTO.getStatus() 
            : Check.CheckStatus.IN_PROGRESS);

        // 保存检查阶段
        return checkRepository.save(check);
    }

    @Override
    @Transactional
    public Check updateCheck(CheckDTO checkDTO) {
        Check existingCheck = checkRepository.findById(checkDTO.getId())
            .orElseThrow(() -> new RuntimeException("检查阶段不存在"));

        // 更新基本信息
        existingCheck.setTitle(checkDTO.getTitle());
        existingCheck.setDescription(checkDTO.getDescription());
        existingCheck.setStartTime(checkDTO.getStartTime());
        existingCheck.setEndTime(checkDTO.getEndTime());

        // 更新状态
        if (checkDTO.getStatus() != null) {
            existingCheck.setStatus(checkDTO.getStatus());
        }

        // 更新关联执行阶段（如果提供了新的执行阶段ID）
        if (checkDTO.getDoPhaseId() != null) {
            DoPhase doPhase = doPhaseRepository.findById(checkDTO.getDoPhaseId())
                .orElseThrow(() -> new RuntimeException("关联的执行阶段不存在"));
            existingCheck.setDoPhase(doPhase);
        }

        // 更新检查人（如果提供了新的检查人ID）
        if (checkDTO.getCheckerId() != null) {
            User checker = userRepository.findById(checkDTO.getCheckerId())
                .orElseThrow(() -> new RuntimeException("指定的检查人不存在"));
            existingCheck.setChecker(checker);
        }

        return checkRepository.save(existingCheck);
    }

    @Override
    @Transactional
    public Check addCheckResult(CheckResultDTO checkResultDTO, User recorder) {
        Check check = checkRepository.findById(checkResultDTO.getCheckId())
            .orElseThrow(() -> new RuntimeException("检查阶段不存在"));

        CheckResult checkResult = new CheckResult();
        checkResult.setContent(checkResultDTO.getContent());
        checkResult.setCheckPhase(check);
        checkResult.setRecorder(recorder);
        checkResult.setRecordTime(LocalDateTime.now());
        checkResult.setType(checkResultDTO.getType() != null 
            ? checkResultDTO.getType() 
            : CheckResult.ResultType.ACHIEVEMENT);

        checkResultRepository.save(checkResult);

        // 重新加载检查阶段以获取最新结果
        return checkRepository.findById(check.getId())
            .orElseThrow(() -> new RuntimeException("检查阶段不存在"));
    }

    @Override
    @Transactional
    public void deleteCheck(Long checkId) {
        Check check = checkRepository.findById(checkId)
            .orElseThrow(() -> new RuntimeException("检查阶段不存在"));
        
        checkRepository.delete(check);
    }

    @Override
    public Check getCheckById(Long checkId) {
        return checkRepository.findById(checkId)
            .orElseThrow(() -> new RuntimeException("检查阶段不存在"));
    }

    @Override
    public List<Check> getChecksByChecker(User checker) {
        return checkRepository.findByChecker(checker);
    }

    @Override
    public List<Check> getChecksByStatus(Check.CheckStatus status) {
        return checkRepository.findByStatus(status);
    }
} 