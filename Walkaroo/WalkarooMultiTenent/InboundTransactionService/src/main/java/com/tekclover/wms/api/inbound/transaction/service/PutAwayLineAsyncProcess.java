package com.tekclover.wms.api.inbound.transaction.service;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.repository.PutAwayHeaderV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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


    @Async("asyncExecutor")
    public void createPutAwayLine(List<PutAwayLineV2> putAwayLineV2s, String loginUserID) {

        log.info("PutAwayLine Async process Started -------->");
        try {
            String db = baseService.getDataBase(putAwayLineV2s.get(0).getPlantId());
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
    }


    /**
     *
     * @param stagingLineEntityV2List stagingList
     */
    @Async("asyncExecutor")
    public void createPutawayHeaderV5(List<StagingLineEntityV2> stagingLineEntityV2List) {

        String idMasterAuthToken = getIDMasterAuthToken();
        long NUM_RAN_CODE_PA_NO = 7;

        if (stagingLineEntityV2List == null || stagingLineEntityV2List.isEmpty()) {
            return;
        }

            // Getting PA_NUMBER per Pallet Id
            String nextPANumber = getNextRangeNumber(NUM_RAN_CODE_PA_NO, stagingLineEntityV2List.get(0).getCompanyCode(),
                    stagingLineEntityV2List.get(0).getPlantId(), stagingLineEntityV2List.get(0).getLanguageId(), stagingLineEntityV2List.get(0).getWarehouseId(),
                    idMasterAuthToken);
            try {
                log.info("-----nextPANumber:{} ---->", nextPANumber);
                putAwayHeaderService.createPutAwayHeaderv4(nextPANumber, stagingLineEntityV2List);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

}
