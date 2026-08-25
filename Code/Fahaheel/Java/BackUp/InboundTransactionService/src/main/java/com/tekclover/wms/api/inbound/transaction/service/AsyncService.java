package com.tekclover.wms.api.inbound.transaction.service;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.AddGrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.mnc.AddInhouseTransferHeader;
import com.tekclover.wms.api.inbound.transaction.model.mnc.InhouseTransferHeaderEntity;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.repository.GrHeaderV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.GrLineV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.StagingLineV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AsyncService {

    @Autowired
    private GrLineService grlineService;

    @Autowired
    private DbConfigRepository dbConfigRepository;

    @Autowired
    GrHeaderV2Repository grHeaderV2Repository;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    PutAwayLineService putawaylineService;

    @Autowired
    InhouseTransferHeaderService inHouseTransferHeaderService;

    @Autowired
    GrLineV2Repository grLineV2Repository;

    @Autowired
    InventoryService inventoryService;

    String statusDescription = null;

//    @Async("asyncExecutor")
//    public void processGrLineAsync(List<AddGrLineV2> newGrLines, String loginUserID) throws Exception {
//        if (newGrLines == null || newGrLines.isEmpty()) {
//            log.info("There are no GrLines to be Processed");
//            return;
//        }
//
//        DataBaseContextHolder.setCurrentDb("MT");
//        String profile = dbConfigRepository.getDbName(
//                newGrLines.get(0).getCompanyCode(),
//                newGrLines.get(0).getPlantId(),
//                newGrLines.get(0).getWarehouseId()
//        );
//
//        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb(profile);
//
//        if (profile != null) {
//
//            switch (profile) {
//                case "FAHAHEEL":
//                case "AUTO_LAP":
//                    grlineService.createGrLineNonCBMV2(newGrLines, loginUserID);
//                    break;
//                case "NAMRATHA":
//                    grlineService.createGrLineNonCBMV4(newGrLines, loginUserID);
//                    break;
//                case "REEFERON":
//                    grlineService.createGrLineNonCBMV5(newGrLines, loginUserID);
//                    break;
//                case "KNOWELL":
//                    grlineService.createGrLineNonCBMV7(newGrLines, loginUserID);
//                    break;
//                case "BP":
//                    grlineService.createGrLineNonCBMV6(newGrLines, loginUserID);
//                    break;
//            }
//
//            DataBaseContextHolder.clear();
//        }
//    }

//    @Async("asyncExecutor")
//    public void processPutAwayLineAsync(List<PutAwayLineV2> newPutAwayLine, String loginUserID) throws Exception {
//        if (newPutAwayLine == null || newPutAwayLine.isEmpty()) {
//            log.info("There are no PutAwayLines to be Processed");
//            return;
//        }
//
//        DataBaseContextHolder.setCurrentDb("MT");
//        String profile = dbConfigRepository.getDbName(
//                newPutAwayLine.get(0).getCompanyCode(),
//                newPutAwayLine.get(0).getPlantId(),
//                newPutAwayLine.get(0).getWarehouseId()
//        );
//
//        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb(profile);
//
//        List<PutAwayLineV2> createdPutAwayLine = null;
//
//        if (profile != null) {
//            switch (profile) {
//                case "FAHAHEEL":
//                case "AUTO_LAP":
//                    putawaylineService.putAwayLineConfirmV2(newPutAwayLine, loginUserID);
//                    break;
//                case "NAMRATHA":
//                    putawaylineService.putAwayLineConfirmNonCBMV4(newPutAwayLine, loginUserID);
//                    break;
//                case "REEFERON":
//                    putawaylineService.putAwayLineConfirmNonCBMV5(newPutAwayLine, loginUserID);
//                    break;
//                case "KNOWELL":
//                    putawaylineService.putAwayLineConfirmNonCBMV7(newPutAwayLine, loginUserID);
//                    break;
//                case "BP":
//                    putawaylineService.putAwayLineConfirmNonCBMV7(newPutAwayLine, loginUserID);
//                    break;
//            }
//
//            DataBaseContextHolder.clear();
//        }
//    }

    @Async("asyncExecutor")
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
            InhouseTransferHeaderEntity createdInHouseTransferHeader =
                    inHouseTransferHeaderService.createInHouseTransferHeader(newInHouseTransferHeader, loginUserID);

            log.info("createdInHouseTransferHeader through ASYNC -----> {}", createdInHouseTransferHeader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

}
