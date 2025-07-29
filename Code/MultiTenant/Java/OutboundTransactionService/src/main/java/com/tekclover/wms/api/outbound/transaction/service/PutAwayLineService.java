package com.tekclover.wms.api.outbound.transaction.service;



import com.tekclover.wms.api.outbound.transaction.repository.PickupLineRepository;
import com.tekclover.wms.api.outbound.transaction.repository.PutAwayLineV2Repository;
import com.tekclover.wms.api.outbound.transaction.model.outbound.v2.PutAwayLineV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class PutAwayLineService {

    @Autowired
    private PickupLineRepository pickupLineRepository;

    @Autowired
    private PutAwayLineV2Repository putAwayLineV2Repository;


    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param storageBin
     * @return
     */
    public List<PutAwayLineV2> getPutAwayLineForPerpetualCountV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                                 String itemCode, String manufacturerName, String storageBin, Date stockCountDate) {
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndProposedStorageBinAndStatusIdAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        itemCode,
                        manufacturerName,
                        storageBin,
                        20L,
                        0L);
        if (putAwayLine == null || putAwayLine.isEmpty()) {
            return null;
        }

        return putAwayLine;
    }



}
