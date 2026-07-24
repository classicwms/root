package com.tekclover.wms.api.inbound.transaction.service;


import com.tekclover.wms.api.inbound.transaction.model.pickup.PickupHeader;
import com.tekclover.wms.api.inbound.transaction.model.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.inbound.transaction.repository.PickupHeaderV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class PickupHeaderService {


    @Autowired
    private PickupHeaderV2Repository pickupHeaderV2Repository;

    /**
     * @param warehouseId
     * @param orderTypeId
     * @return
     */
    public List<PickupHeaderV2> getPickupHeaderCount(String companyCodeId, String plantId, String languageId,
                                                     String warehouseId, String levelId, List<Long> orderTypeId) {
        List<PickupHeaderV2> header =
                pickupHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndLevelIdAndOutboundOrderTypeIdInAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, 48L, levelId, orderTypeId, 0L);
        return header;
    }
}
