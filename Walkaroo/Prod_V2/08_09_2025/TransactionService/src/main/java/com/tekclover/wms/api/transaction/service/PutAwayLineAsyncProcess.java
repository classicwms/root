package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.transaction.repository.PutAwayHeaderV2Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PutAwayLineAsyncProcess {


    @Autowired
    PutAwayHeaderV2Repository putAwayHeaderV2Repository;

    @Autowired
    PutAwayLineService putAwayLineService;


    @Async("asyncExecutor")
    public void createPutAwayLine(List<PutAwayLineV2> putAwayLineV2s, String loginUserID) {

        log.info("PutAwayLine Async process Started -------->");
        try {
            List<PutAwayLineV2> putAwayLineV2List = putAwayLineService.putAwayLineConfirmNonCBMV3(putAwayLineV2s, loginUserID);
            log.info("PutAwayLine Async process Started --------> Size is {} ", putAwayLineV2List.size());
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }


}
