package com.example.pdca.util;

import com.example.pdca.model.Report;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

/**
 * PDF 报告生成器
 * 使用 iText 库生成 PDF 报告
 */
@Component
public class PDFReportGenerator {

    public ByteArrayOutputStream generatePDF(Report report) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            
            // 添加标题
            document.add(new Paragraph(report.getTitle())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20));
            
            // 添加摘要
            document.add(new Paragraph("摘要：" + report.getSummary())
                .setFontSize(12));
            
            // 创建 PDCA 分析表格
            Table table = new Table(2);
            table.addCell("计划阶段分析");
            table.addCell(report.getPlanningAnalysis());
            table.addCell("执行阶段分析");
            table.addCell(report.getDoingAnalysis());
            table.addCell("检查阶段分析");
            table.addCell(report.getCheckingAnalysis());
            table.addCell("行动阶段分析");
            table.addCell(report.getActingAnalysis());
            
            document.add(table);
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
        }
        
        return baos;
    }
} 