package com.example.pdca.dto;

import com.example.pdca.model.DoRecord;
import lombok.Data;

@Data
public class DoRecordDTO {
    private Long doId;
    private String content;
    private DoRecord.RecordType type;
} 