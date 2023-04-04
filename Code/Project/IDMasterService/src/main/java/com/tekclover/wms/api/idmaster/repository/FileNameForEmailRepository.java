package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.email.*;
import com.tekclover.wms.api.idmaster.repository.Specification.FileNameForEmailSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface FileNameForEmailRepository extends JpaRepository<FileNameForEmail,String>, JpaSpecificationExecutor<FileNameForEmail> {

    public List<FileNameForEmail> findAll();

//    public FileNameForEmail find(FileNameForEmailSpecification fileNameForEmailSpecification);
   Optional<FileNameForEmail> findByFileNameIdAndDeletionIndicator(Long fileNameId, Long deletionIndicator);
   Optional<FileNameForEmail> findByFileNameId(Long fileNameId);

   Optional<FileNameForEmail> findByReportDate(String reportDate);
    Optional<FileNameForEmail> findByReportDateAndDeletionIndicator(String reportDate, Long deletionIndicator);

    @Query(value = "select * \n" +
            "from tblfilenameforemail tfn \n" +
            " where \n" +
            "(COALESCE(:reportDate,null) IS NULL OR (tfn.report_date IN (:reportDate))) and\n"+
            "tfn.is_deleted=0 ",nativeQuery = true)
    public FileNameForEmail getFileNameForEmail(@Param("reportDate") String reportDate);
}
