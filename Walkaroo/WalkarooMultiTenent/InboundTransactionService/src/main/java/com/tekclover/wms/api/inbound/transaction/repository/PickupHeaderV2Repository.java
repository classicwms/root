package com.tekclover.wms.api.inbound.transaction.repository;


import com.tekclover.wms.api.inbound.transaction.model.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PickupHeaderV2Repository extends JpaRepository<PickupHeaderV2, Long>,
        JpaSpecificationExecutor<PickupHeaderV2>, StreamableJpaSpecificationRepository<PickupHeaderV2> {

    @Query(value = "select token_id from tblhhtnotification where \n" +
            "c_id = :companyId and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId and is_deleted = 0", nativeQuery = true)
    public List<String> getDeviceToken(@Param("companyId")String companyId,
                                       @Param("plantId")String plantId,
                                       @Param("languageId")String languageId,
                                       @Param("warehouseId")String warehouseId);

    public List<PickupHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndLevelIdAndOutboundOrderTypeIdInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, Long statusId, String levelId, List<Long> outboundOrderTypeId, Long deletionIndicator);

}
