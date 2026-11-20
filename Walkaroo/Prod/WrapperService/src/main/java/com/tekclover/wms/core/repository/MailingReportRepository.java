package com.tekclover.wms.core.repository;


import com.tekclover.wms.core.model.idmaster.MailingReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Repository
@Transactional
public interface MailingReportRepository extends JpaRepository<MailingReport,Long>{

    Optional<MailingReport> findBycompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndFileNameAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String languageId, String fileName, Long deletionIndicator );
    Long countByReportDateAndWarehouseId(String reportDate, String warehouseId);
}


