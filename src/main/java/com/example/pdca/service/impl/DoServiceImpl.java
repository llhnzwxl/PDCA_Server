package com.example.pdca.service.impl;

import com.example.pdca.dto.DoDTO;
import com.example.pdca.dto.DoRecordDTO;
import com.example.pdca.model.DoPhase;
import com.example.pdca.model.DoRecord;
import com.example.pdca.model.Plan;
import com.example.pdca.model.User;
import com.example.pdca.repository.DoRepository;
import com.example.pdca.repository.DoRecordRepository;
import com.example.pdca.repository.PlanRepository;
import com.example.pdca.repository.UserRepository;
import com.example.pdca.service.DoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DoServiceImpl implements DoService {
    
    private final DoRepository doRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final DoRecordRepository doRecordRepository;
    
    @Autowired
    public DoServiceImpl(DoRepository doRepository, 
                        PlanRepository planRepository,
                        UserRepository userRepository,
                        DoRecordRepository doRecordRepository) {
        this.doRepository = doRepository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
        this.doRecordRepository = doRecordRepository;
    }

    @Override
    public DoPhase createDo(DoDTO doDTO, User creator) {
        DoPhase doPhase = new DoPhase();
        doPhase.setTitle(doDTO.getTitle());
        doPhase.setDescription(doDTO.getDescription());
        doPhase.setStartTime(doDTO.getStartTime());
        doPhase.setEndTime(doDTO.getEndTime());
        
        Plan plan = planRepository.findById(doDTO.getPlanId())
            .orElseThrow(() -> new RuntimeException("关联的计划不存在"));
        doPhase.setPlan(plan);
        
        User executor = userRepository.findById(doDTO.getExecutorId())
            .orElseThrow(() -> new RuntimeException("指定的执行人不存在"));
        doPhase.setExecutor(executor);
        
        doPhase.setStatus(doDTO.getStatus() != null ? doDTO.getStatus() : DoPhase.DoStatus.IN_PROGRESS);
        
        return doRepository.save(doPhase);
    }

    @Override
    public DoPhase updateDo(DoDTO doDTO) {
        DoPhase doPhase = doRepository.findById(doDTO.getId())
            .orElseThrow(() -> new RuntimeException("执行阶段不存在"));
            
        doPhase.setTitle(doDTO.getTitle());
        doPhase.setDescription(doDTO.getDescription());
        doPhase.setStartTime(doDTO.getStartTime());
        doPhase.setEndTime(doDTO.getEndTime());
        
        if (doDTO.getExecutorId() != null) {
            User executor = userRepository.findById(doDTO.getExecutorId())
                .orElseThrow(() -> new RuntimeException("指定的执行人不存在"));
            doPhase.setExecutor(executor);
        }
        
        if (doDTO.getStatus() != null) {
            doPhase.setStatus(doDTO.getStatus());
        }
        
        return doRepository.save(doPhase);
    }

    @Override
    public DoPhase addDoRecord(DoRecordDTO doRecordDTO, User recorder) {
        DoPhase doPhase = doRepository.findById(doRecordDTO.getDoId())
            .orElseThrow(() -> new RuntimeException("执行阶段不存在"));
            
        DoRecord record = new DoRecord();
        record.setContent(doRecordDTO.getContent());
        record.setType(doRecordDTO.getType());
        record.setRecorder(recorder);
        record.setRecordTime(LocalDateTime.now());
        record.setDoPhase(doPhase);
        
        doRecordRepository.save(record);
        return doPhase;
    }

    @Override
    public void deleteDo(Long doId) {
        doRepository.deleteById(doId);
    }

    @Override
    public DoPhase getDoById(Long doId) {
        return doRepository.findById(doId)
            .orElseThrow(() -> new RuntimeException("执行阶段不存在"));
    }

    @Override
    public List<DoPhase> getDosByExecutor(User executor) {
        return doRepository.findByExecutor(executor);
    }

    @Override
    public List<DoPhase> getDosByStatus(DoPhase.DoStatus status) {
        return doRepository.findByStatus(status);
    }

    @Override
    public List<DoPhase> getDosByPlan(Plan plan) {
        return doRepository.findByPlan(plan);
    }

    @Override
    public Page<DoPhase> getPagedDoPhases(Pageable pageable) {
        return doRepository.findAll(pageable);
    }

    @Override
    public Page<DoPhase> getPagedDoPhasesByStatus(DoPhase.DoStatus status, Pageable pageable) {
        return doRepository.findByStatus(status, pageable);
    }
} 