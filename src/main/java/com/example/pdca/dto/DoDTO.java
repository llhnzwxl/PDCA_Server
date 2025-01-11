package com.example.pdca.dto;

import com.example.pdca.model.DoPhase;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DoDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long planId;
    private Long executorId;
    private DoPhase.DoStatus status;
} 