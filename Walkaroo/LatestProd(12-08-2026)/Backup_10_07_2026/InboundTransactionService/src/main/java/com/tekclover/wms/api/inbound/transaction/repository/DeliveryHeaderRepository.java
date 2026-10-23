package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.deliveryheader.DeliveryHeader;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface DeliveryHeaderRepository extends JpaRepository<DeliveryHeader, String>,
        JpaSpecificationExecutor<DeliveryHeader>, StreamableJpaSpecificationRepository<DeliveryHeader> {

    Optional<DeliveryHeader> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDeliveryNoAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, Long deliveryNo, String languageId, Long deletionIndicator);

    List<DeliveryHeader> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndDeliveryNoAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, Long deliveryNo, Long deletionIndicator);


    @Query(value = "select MAX(DLV_NO)+1 \n" +
            "from tbldeliveryheader",nativeQuery = true)
    public Long getDeliveryNo();

    List<DeliveryHeader> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String refDocNumber, String languageId, Long deletionIndicator);
}
