package com.tekclover.wms.api.inbound.orders.scheduler;

import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.inbound.orders.repository.HhtNotificationRepository;
import com.tekclover.wms.api.inbound.orders.service.ScheduleAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class BatchJobScheduler {

    @Autowired
    ScheduleAsyncService scheduleAsyncService;


    @Autowired
    HhtNotificationRepository notificationRepository;

    //-------------------------------------------------------------------------------------------

    @Scheduled(fixedDelay = 20000)
    public void scheduleJob() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {
        CompletableFuture<WarehouseApiResponse> amgharaOutboundOrder = scheduleAsyncService.processAmgharaOutboundOrder();
//        CompletableFuture<WarehouseApiResponse> outboundFailedOrder = scheduleAsyncService.processOutboundFailedOrder();
    }


    // Delete HhtNotification_Records
    public void deleteRecords() {
        System.out.println("Truncating table at " + ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
        notificationRepository.truncateTable();
    }

//    @Scheduled(cron = "0 0 1 * * *")
//    public void schedulehttnotification() {
//
//        log.info("Starting schedulerhttnotification job at 1 AM");
//        deleteRecords();
//        log.info("DataBase cleaned successfully.");
//
//    }

    @Scheduled(cron = "0 0 1 * * *")
    public void schedulehttnotification() {

        log.info("Starting schedulerhttnotification job at 1 AM");

        DataBaseContextHolder.setCurrentDb("NAMRATHA");
        deleteRecords();
        log.info("Namratha DataBase cleaned successfully.");
        DataBaseContextHolder.clear();

        DataBaseContextHolder.setCurrentDb("KNOWELL");
        deleteRecords();
        log.info("Knowell DataBase cleaned successfully.");
        DataBaseContextHolder.clear();

        DataBaseContextHolder.setCurrentDb("ALM");
        deleteRecords();
        log.info("Alm DataBase cleaned successfully.");
        DataBaseContextHolder.clear();

        DataBaseContextHolder.setCurrentDb("IMPEX");
        deleteRecords();
        log.info("Impex DataBase cleaned successfully.");
        DataBaseContextHolder.clear();

        DataBaseContextHolder.setCurrentDb("WK");
        deleteRecords();
        log.info("WK DataBase cleaned successfully.");
        DataBaseContextHolder.clear();

        DataBaseContextHolder.setCurrentDb("FAHAHEEL");
        deleteRecords();
        log.info("Fahaheel DataBase cleaned successfully.");
        DataBaseContextHolder.clear();

        DataBaseContextHolder.setCurrentDb("REEFERON");
        deleteRecords();
        log.info("Reeferon DataBase cleaned successfully.");
        DataBaseContextHolder.clear();
    }

}