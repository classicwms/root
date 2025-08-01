package com.tekclover.wms.api.outbound.transaction.service;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.mnc.AddInhouseTransferHeader;
import com.tekclover.wms.api.outbound.transaction.model.mnc.InhouseTransferHeaderEntity;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.AddPickupLine;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AsyncService {

    @Autowired
    private DbConfigRepository dbConfigRepository;

    @Autowired
    InhouseTransferHeaderService inHouseTransferHeaderService;

    @Autowired
    PickupLineService pickuplineService;

    @Autowired
    PickupHeaderService pickupheaderService;

    @Async("asyncTaskExecutor")
    public void processPickupLineAsync(List<AddPickupLine> newPickupLine, String loginUserID) throws Exception {
        if (newPickupLine == null || newPickupLine.isEmpty()) {
            log.info("There are no PickupLines to be Processed");
            return;
        }

        try {
            log.info("AddPickupLine -----> {}", newPickupLine);
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(String.valueOf(newPickupLine.get(0).getCompanyCodeId()), newPickupLine.get(0).getPlantId(), newPickupLine.get(0).getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            List<PickupLineV2> createdPickupLine = null;
            if (routingDb != null) {
                switch (routingDb) {
                    case "FAHAHEEL":
                    case "AUTO_LAP":
                        createdPickupLine = pickuplineService.createPickupLineNonCBMV2(newPickupLine, loginUserID);
                        break;
                    case "NAMRATHA":
                        createdPickupLine = pickuplineService.createPickupLineNonCBMV4(newPickupLine, loginUserID);
                        break;
                    case "REEFERON":
                        createdPickupLine = pickuplineService.createPickupLineV5(newPickupLine, loginUserID);
                        break;
                    case "KNOWELL":
                        createdPickupLine = pickuplineService.createPickupLineNonCBMV7(newPickupLine, loginUserID);
                        break;
                }
            }
            log.info("CreatedPickupLine through Async ------> {}", createdPickupLine);

        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @Async("asyncTaskExecutor")
    public void processInhouseTransferHeaderAsync(AddInhouseTransferHeader newInHouseTransferHeader, String loginUserID) throws Exception {
        if (newInHouseTransferHeader == null) {
            log.info("There are no InhouseTransferHeader to be Processed");
            return;
        }

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newInHouseTransferHeader.getCompanyCodeId(), newInHouseTransferHeader.getPlantId(), newInHouseTransferHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            InhouseTransferHeaderEntity transferHeaderEntity = null;

            if (routingDb != null){
                switch (routingDb){
                    case "REEFERON":
                        transferHeaderEntity = inHouseTransferHeaderService.createInHouseTransferHeaderV5(newInHouseTransferHeader, loginUserID);
                        break;
                    case "KNOWELL":
                        transferHeaderEntity= inHouseTransferHeaderService.createInHouseTransferHeaderV2(newInHouseTransferHeader, loginUserID);
                        break;
                    default:
                        transferHeaderEntity= inHouseTransferHeaderService.createInHouseTransferHeaderV2(newInHouseTransferHeader, loginUserID);
                }
            }

            log.info("createdInHouseTransferHeader through ASYNC -----> {}", transferHeaderEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

}
