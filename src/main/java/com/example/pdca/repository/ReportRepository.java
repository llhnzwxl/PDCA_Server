package com.example.pdca.repository;

import com.example.pdca.model.Report;
import com.example.pdca.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 报告数据访问接口
 * 提供基本的数据库操作方法
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByCreator(User creator);
    List<Report> findByType(Report.ReportType type);
    
    // 修改分页查询方法
    @Query(value = "SELECT DISTINCT r FROM Report r WHERE r.creator = :user",
           countQuery = "SELECT COUNT(DISTINCT r) FROM Report r WHERE r.creator = :user")
    Page<Report> findPageByCreator(@Param("user") User creator, Pageable pageable);
    
    @Query(value = "SELECT DISTINCT r FROM Report r WHERE r.type = :type",
           countQuery = "SELECT COUNT(DISTINCT r) FROM Report r WHERE r.type = :type")
    Page<Report> findPageByType(@Param("type") Report.ReportType type, Pageable pageable);
    
    @Query(value = "SELECT DISTINCT r FROM Report r WHERE r.status = :status",
           countQuery = "SELECT COUNT(DISTINCT r) FROM Report r WHERE r.status = :status")
    Page<Report> findPageByStatus(@Param("status") Report.ReportStatus status, Pageable pageable);
} 