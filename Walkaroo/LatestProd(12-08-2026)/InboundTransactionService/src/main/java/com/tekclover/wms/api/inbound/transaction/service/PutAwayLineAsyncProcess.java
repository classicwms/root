package com.tekclover.wms.api.inbound.transaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.repository.GrHeaderV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.PutAwayHeaderV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PutAwayLineAsyncProcess extends BaseService {


    @Autowired
    PutAwayHeaderV2Repository putAwayHeaderV2Repository;

    @Autowired
    PutAwayLineService putAwayLineService;

    @Autowired
    BaseService baseService;

    @Autowired
    PutAwayHeaderService putAwayHeaderService;

    @Autowired
    GrHeaderV2Repository grHeaderV2Repository;
    @Autowired
    ODataService oDataService;


    @Async("asyncExecutor")
    public void createPutAwayLine(List<PutAwayLineV2> putAwayLineV2s, String loginUserID) {

        log.info("PutAwayLine Async process Started -------->");
        try {
            String db = baseService.getDataBase(putAwayLineV2s.get(0).getPlantId(),putAwayLineV2s.get(0).getWarehouseId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(db);
            log.info("Current DB -------------> "  + db);
            List<PutAwayLineV2> putAwayLineV2List = putAwayLineService.putAwayLineConfirmNonCBMV3(putAwayLineV2s, loginUserID);
            log.info("PutAwayLine Async process Completed --------> Size is {} ", putAwayLineV2List.size());
        } catch (Exception e) {
            log.info(e.getMessage());
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    /**
     *
     * @param stagingLineEntityV2List stagingList
     */
    @Async("asyncExecutor")
    public void createPutawayHeaderv4(List<StagingLineEntityV2> stagingLineEntityV2List) {

        try {
            String currentDB = baseService.getDataBase(stagingLineEntityV2List.get(0).getPlantId(), stagingLineEntityV2List.get(0).getWarehouseId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("PutAwayHeader Creation Process Started DB: {} ", currentDB);
            String idMasterAuthToken = getIDMasterAuthToken();
            long NUM_RAN_CODE_PA_NO = 7;
            /*
             * PutAway Creation
             */
            Map<String, List<StagingLineEntityV2>> groupedByPalletId =
                    stagingLineEntityV2List.stream()
                            .collect(Collectors.groupingBy(StagingLineEntityV2::getPalletId));

            for (Map.Entry<String, List<StagingLineEntityV2>> entry : groupedByPalletId.entrySet()) {
                String palletId = entry.getKey();
                List<StagingLineEntityV2> grLines = entry.getValue();

                // Getting PA_NUMBER per Pallet Id
                String nextPANumber = getNextRangeNumber(NUM_RAN_CODE_PA_NO, grLines.get(0).getCompanyCode(),
                        grLines.get(0).getPlantId(), grLines.get(0).getLanguageId(), grLines.get(0).getWarehouseId(),
                        idMasterAuthToken);
                try {
                    log.info("-----nextPANumber:{} | PalId: {} ---->", nextPANumber, palletId);
                    putAwayHeaderService.createPutAwayHeaderv4(nextPANumber, grLines);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    /**
     *
     * @param stagingLineEntityV2List stagingList
     */
//    @Async("asyncExecutor")
//    public void createPutawayHeaderV5(List<StagingLineEntityV2> stagingLineEntityV2List) {
//
//        String idMasterAuthToken = getIDMasterAuthToken();
//        long NUM_RAN_CODE_PA_NO = 7;
//
//        if (stagingLineEntityV2List == null || stagingLineEntityV2List.isEmpty()) {
//            return;
//        }
//
//            // Getting PA_NUMBER per Pallet Id
//            String nextPANumber = getNextRangeNumber(NUM_RAN_CODE_PA_NO, stagingLineEntityV2List.get(0).getCompanyCode(),
//                    stagingLineEntityV2List.get(0).getPlantId(), stagingLineEntityV2List.get(0).getLanguageId(), stagingLineEntityV2List.get(0).getWarehouseId(),
//                    idMasterAuthToken);
//            try {
//                log.info("-----nextPANumber:{} ---->", nextPANumber);
//                putAwayHeaderService.createPutAwayHeaderv4(nextPANumber, stagingLineEntityV2List);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//    }

    private final Set<String> runningProfiles = ConcurrentHashMap.newKeySet();
    /**
     *  Create PutAwayHeader In Schedule
     * @param profile currentDB
     */
    @Async("asyncExecutorPutAway")
    public void createPutAwayHeaderInSchedule(String profile) {

        if(!runningProfiles.add(profile)) {
            log.warn("PutAway already running for DB={}, skipping", profile);
            return;
        }

        long start = System.currentTimeMillis();
        try {
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(profile);
            log.info("PutAwayHeader Creation Process Started DB: {} ", profile);
            List<StagingLineEntityV2> listOfStaging = stagingLineV2Repository.findStagingLine();
            log.info("List of StagingLine Values : {} ", listOfStaging);
            if(!listOfStaging.isEmpty()) {
//                String orderText = "PutAway Created";
                String refDocNo = listOfStaging.get(0).getRefDocNumber();
                log.info("PutAwayHeader Creation in RefDocNo is -- : {}", refDocNo);
//                int stagingUpdate = stagingLineV2Repository.stagingUpdate(refDocNo, orderText);
//                log.info("Staging Update Count is : {}", stagingUpdate);

                createPutawayHeader(listOfStaging);
            }
        } catch (Exception e) {
            log.error("PutAway failed DB={}", profile, e);
        } finally {
            DataBaseContextHolder.clear();
            runningProfiles.remove(profile);
            log.info("PutAway completed DB={} in {} ms", profile, System.currentTimeMillis() - start);
        }
    }


    /**
     *
     * @param stagingLineEntityV2List stagingLineEntityV2List
     */
    public void createPutawayHeader(List<StagingLineEntityV2> stagingLineEntityV2List) {

        String idMasterAuthToken = getIDMasterAuthToken();
        long NUM_RAN_CODE_PA_NO = 7;
        /*
         * PutAway Creation
         */
        Map<String, List<StagingLineEntityV2>> groupedByPalletId =
                stagingLineEntityV2List.stream()
                        .collect(Collectors.groupingBy(StagingLineEntityV2::getPalletId));

        for (Map.Entry<String, List<StagingLineEntityV2>> entry : groupedByPalletId.entrySet()) {
            String palletId = entry.getKey();
            List<StagingLineEntityV2> grLines = entry.getValue();

            // Getting PA_NUMBER per Pallet Id
            String nextPANumber = getNextRangeNumber(NUM_RAN_CODE_PA_NO, grLines.get(0).getCompanyCode(),
                    grLines.get(0).getPlantId(), grLines.get(0).getLanguageId(), grLines.get(0).getWarehouseId(),
                    idMasterAuthToken);
            try {
                log.info("-----nextPANumber:{} | PalId: {} ---->", nextPANumber, palletId);
                putAwayHeaderService.createPutAwayHeaderInSchedule(nextPANumber, grLines);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *
     * @param stagingLineEntityV2List stagingLine
     * @throws JsonProcessingException exception
     */
    public void sapPushingStatus(List<StagingLineEntityV2> stagingLineEntityV2List) throws JsonProcessingException {
        // 2. Group by refDocNumber after all updates
        Map<String, List<StagingLineEntityV2>> groupedByRefDoc = stagingLineEntityV2List.stream()
                .collect(Collectors.groupingBy(StagingLineEntityV2::getRefDocNumber));

        for (Map.Entry<String, List<StagingLineEntityV2>> entry : groupedByRefDoc.entrySet()) {
            String refDocNumber = entry.getKey();
            log.info("refDocNumber --> {}", refDocNumber);
            List<StagingLineEntityV2> stagingLines = stagingLineV2Repository.findStagingLineList(refDocNumber);
            log.info("List of StagingLine Values: {} ", stagingLines);
            if(!stagingLines.isEmpty()) {
                String response = oDataService.postODataRequest(stagingLines, refDocNumber, "1", "X");
                System.out.println("RES ---> {}" + response);
                if (response.equals("0")) {
                    log.info("Sap Success RefDoc: {} ", refDocNumber);
                    int grCount = grHeaderV2Repository.updateGRHeader_SAP(refDocNumber, "0");
                    log.info("GrHeader Success Updated Rows: {}", grCount);
                } else {
                    log.info("Sap Failure RefDoc: {} ", refDocNumber);
                    int grCount = grHeaderV2Repository.updateGRHeader_SAP(refDocNumber, "1");
                    log.info("GrHeader Failure Updated Rows: {}", grCount);
                }
            }
        }
    }

}
