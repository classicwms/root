package com.tekclover.wms.api.masters.repository;


import com.tekclover.wms.api.masters.model.cyclecountscheduler.CycleCountScheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface CycleCountSchedulerRepository extends JpaRepository<CycleCountScheduler,Long>, JpaSpecificationExecutor<CycleCountScheduler> {


    Optional<CycleCountScheduler> findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndSchedulerNumberAndLevelIdAndDeletionIndicator(String companyCodeId,String languageId,String plantId,String warehouseId,Long cycleCountTypeId,String schedulerNumber,Long levelId,Long deletionIndicator);
}
