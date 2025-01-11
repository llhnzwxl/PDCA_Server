package com.example.pdca.util;

import com.example.pdca.model.Report;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Excel 报告生成器
 * 使用 Apache POI 库生成 Excel 报告
 */
@Component
public class ExcelReportGenerator {

    public ByteArrayOutputStream generateExcel(Report report) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("PDCA报告");
            
            // 创建标题行
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(report.getTitle());
            
            // 创建摘要行
            Row summaryRow = sheet.createRow(1);
            Cell summaryCell = summaryRow.createCell(0);
            summaryCell.setCellValue("摘要：" + report.getSummary());
            
            // 创建 PDCA 分析行
            String[][] analysisData = {
                {"计划阶段分析", report.getPlanningAnalysis()},
                {"执行阶段分析", report.getDoingAnalysis()},
                {"检查阶段分析", report.getCheckingAnalysis()},
                {"行动阶段分析", report.getActingAnalysis()}
            };
            
            for (int i = 0; i < analysisData.length; i++) {
                Row row = sheet.createRow(i + 2);
                row.createCell(0).setCellValue(analysisData[i][0]);
                row.createCell(1).setCellValue(analysisData[i][1]);
            }
            
            workbook.write(baos);
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
        }
        
        return baos;
    }
} 