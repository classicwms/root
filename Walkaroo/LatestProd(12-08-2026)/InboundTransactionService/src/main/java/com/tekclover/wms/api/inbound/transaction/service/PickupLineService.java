package com.tekclover.wms.api.inbound.transaction.service;


import com.tekclover.wms.api.inbound.transaction.model.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.inbound.transaction.repository.PickupLineV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.tekclover.wms.api.inbound.transaction.service.BaseService.DIRECT_STOCK_BIN;

@Slf4j
@Service
public class PickupLineService {

    @Autowired
    private PickupLineV2Repository pickupLineV2Repository;

    public PickupLineV2 getPickupLineForLastBinCheck(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                     String itemCode, String manufacturerName) {
        PickupLineV2 pickupLine = pickupLineV2Repository
                .findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicatorAndPickedStorageBinNotOrderByPickupConfirmedOnDesc(
                        companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, 0L, DIRECT_STOCK_BIN);
        if (pickupLine != null) {
            return pickupLine;
        }
        return null;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param manufacturerName
     * @param itemCode
     * @return
     */
    public List<PickupLineV2> getPickupLineForLastBinCheckV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                             String itemCode, String manufacturerName) {
        List<PickupLineV2> pickupLine = pickupLineV2Repository
                .findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicatorOrderByPickupConfirmedOnDesc(
                        companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, 0L);
        if (pickupLine != null && !pickupLine.isEmpty()) {
            return pickupLine;
        }
        return null;
    }

    public List<PickupLineV2> getPickupLineForPerpetualCountV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                               String itemCode, String manufacturerName, String storageBin, Date stockCountDate) {
        List<PickupLineV2> pickupLine = pickupLineV2Repository
                .findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPickedStorageBinAndStatusIdAndPickupCreatedOnBetweenAndDeletionIndicator(
                        languageId, companyCodeId, plantId, warehouseId, itemCode, manufacturerName, storageBin, 50L, stockCountDate, new Date(), 0L);
//        List<PickupLineV2> pickupLine = pickupLineV2Repository
//                .findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPickedStorageBinAndStatusIdAndDeletionIndicator(
//                        languageId, companyCodeId, plantId, warehouseId, itemCode, manufacturerName, storageBin, 50L, 0L);
        if (pickupLine != null && !pickupLine.isEmpty()) {
            log.info("PickUpline Status 50 ---> " + pickupLine);
            return pickupLine;
        }
        return null;
    }
}
