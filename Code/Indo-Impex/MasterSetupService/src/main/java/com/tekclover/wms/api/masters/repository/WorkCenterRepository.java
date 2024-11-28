package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.workcenter.WorkCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
@Transactional
public interface WorkCenterRepository extends JpaRepository<WorkCenter, Long>, JpaSpecificationExecutor<WorkCenter> {

    Optional<WorkCenter> findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndWorkCenterIdAndWorkCenterTypeAndDeletionIndicator(String companyCodeId, String languageId, String plantId,
                                                                                                                                      String warehouseId, Long workCenterId, String workCenterType, Long deletionIndicator);
}