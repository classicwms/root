package com.tekclover.wms.api.inbound.transaction.scheduler;

import com.tekclover.wms.api.inbound.transaction.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BatchJobScheduler {

    @Autowired
    ScheduleAsyncService scheduleAsyncService;

    @Autowired
    PutAwayLineAsyncProcess putAwayService;


    //-------------------------------------------------------------------------------------------

    @Scheduled(fixedDelay = 20000)
    public void scheduleJob() throws Exception {

        // MDU
        scheduleAsyncService.processInboundOrder();
        // CMP
        scheduleAsyncService.processInboundOrderV2();
        // CHN
        scheduleAsyncService.processInboundOrderV3();
        //VGA
        scheduleAsyncService.processInboundOrderV4();
        //CCL
        scheduleAsyncService.processInboundOrderV5();
        //HYD
        scheduleAsyncService.processInboundOrderV6();
        //AHM
        scheduleAsyncService.processInboundOrderV7();
        //MUB
        scheduleAsyncService.processInboundOrderV8();
        // NGP1
        scheduleAsyncService.processInboundOrderV9();
        // NGP2
        scheduleAsyncService.processInboundOrderV10();
        //MYS
        scheduleAsyncService.processInboundOrderV11();
        //KNP
        scheduleAsyncService.processInboundOrderV12();
        //CTC
        scheduleAsyncService.processInboundOrderV13();
        //BDG
        scheduleAsyncService.processInboundOrderV14();


//        CompletableFuture<WarehouseApiResponse> inboundFailedOrder = scheduleAsyncService.processInboundFailedOrder();

    }

//    private final AtomicBoolean running = new AtomicBoolean(false);
//
//    @Scheduled(fixedDelay = 20000)
//    public void scheduleJob() {
//
//        if (!running.compareAndSet(false, true)) {
//            log.info("Previous inbound job still running, skipping...");
//            return;
//        }
//
//        try {
//            CompletableFuture<WarehouseApiResponse> mdu =
//                    scheduleAsyncService.processInboundOrder();
//
//            CompletableFuture<WarehouseApiResponse> cmp =
//                    scheduleAsyncService.processInboundOrderV2();
//
//            CompletableFuture.allOf(mdu, cmp).join(); //  wait for completion
//
//        } catch (Exception e) {
//            log.error("Scheduler exception", e);
//        } finally {
//            running.set(false); //  only after async completes
//        }
//    }

    @Scheduled(fixedDelay = 10000)
    public void scheduleInPutAway() throws Exception {

        long schedulerStart = System.currentTimeMillis();

        log.info("========== PUTAWAY SCHEDULER START ==========");

        // MDU
        putAwayService.createPutAwayHeaderInPalletWise("MDU");
        // CMP
        putAwayService.createPutAwayHeaderInPalletWise("CMP");
        // CHN
        putAwayService.createPutAwayHeaderInPalletWise("CHN");
        //VGA
        putAwayService.createPutAwayHeaderInPalletWise("VGA");
        //CCL
        putAwayService.createPutAwayHeaderInPalletWise("CCL");
        //HYD
        putAwayService.createPutAwayHeaderInPalletWise("HYD");
        //AHM
        putAwayService.createPutAwayHeaderInPalletWise("AHM");
        //MUB
        putAwayService.createPutAwayHeaderInPalletWise("MUB");
        // NGP1
        putAwayService.createPutAwayHeaderInPalletWise("NGP1");
        // NGP2
        putAwayService.createPutAwayHeaderInPalletWise("NGP2");
        //MYS
        putAwayService.createPutAwayHeaderInPalletWise("MYS");
        //KNP
        putAwayService.createPutAwayHeaderInPalletWise("KNP");
        //CTC
        putAwayService.createPutAwayHeaderInPalletWise("CTC");
        //BDG
        putAwayService.createPutAwayHeaderInPalletWise("BDG");
        
        log.info(
                "========== PUTAWAY SCHEDULER SUBMISSION COMPLETED in {} ms ==========",
                System.currentTimeMillis() - schedulerStart
        );
    }

}