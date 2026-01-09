package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface PickupLineV2Repository extends JpaRepository<PickupLineV2, Long>,
        JpaSpecificationExecutor<PickupLineV2>, StreamableJpaSpecificationRepository<PickupLineV2> {


    PickupLineV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicatorAndPickedStorageBinNotOrderByPickupConfirmedOnDesc(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, Long deletionIndicator, String storageBin);

    List<PickupLineV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicatorOrderByPickupConfirmedOnDesc(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, Long deletionIndicator);


    List<PickupLineV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPickedStorageBinAndStatusIdAndPickupCreatedOnBetweenAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode, String manufacturerName,
            String storageBin, Long statusId, Date stockCountDate, Date date, Long deletionIndicator);


    @Query(value = "SELECT * from tblpickupline " +
            "WHERE partner_item_barcode in (:barcodeId) and " +
            "(COALESCE(:itemCode, null) IS NULL OR (ITM_CODE IN (:itemCode))) ", nativeQuery = true)
    List<PickupLineV2> findByItemCodeAndBarcodeId(@Param("itemCode") String itemCode,
                                                            @Param("barcodeId") String barcodeId);
}
