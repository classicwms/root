package com.tekclover.wms.api.idmaster.controller;


import com.tekclover.wms.api.idmaster.model.outboundheader.PreOutboundHeader;
import com.tekclover.wms.api.idmaster.model.pickerdenial.PickerDenialReport;
import com.tekclover.wms.api.idmaster.model.pickerdenial.SearchPickupLine;
import com.tekclover.wms.api.idmaster.service.FileStorageService;
import com.tekclover.wms.api.idmaster.service.PickupDenialService;
import com.tekclover.wms.api.idmaster.service.TransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Api(tags = {"EMail"}, value = "EMail  Operations related to EMailController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "EMail",description = "Operations related to EMail ")})
@RequestMapping("/pickupDenial")
@RestController

public class PickupDenialController {

    @Autowired
    PickupDenialService PickupDenialService;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    TransactionService transactionService;

//    @Scheduled(cron = "*/30 * * * * ?")
    @ApiOperation(response = Optional.class, value = "Email Sending")
    @PostMapping("/send-report")
    public ResponseEntity<?> sendReport() throws Exception {

        SearchPickupLine searchPickupLine  = new SearchPickupLine ();

        // Set the startOrderDate to 12:00 AM and endOrderDate to 11:59 PM of the current day
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();

        // Move to the previous day
        calendar.add(Calendar.DATE, 0);
        calendar1.add(Calendar.DATE, 0);

        Date startOrderDate;
        // Set startOrderDate to 12:00 AM
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        startOrderDate = calendar.getTime();

        // Set startOrderDate to 12:00 AM
        calendar.set(Calendar.YEAR, 2019);
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        searchPickupLine.setFromPickConfirmedOn(calendar.getTime());

        // Set endOrderDate to 11:59 PM
        calendar1.set(Calendar.HOUR_OF_DAY, 23);
        calendar1.set(Calendar.MINUTE, 59);
        calendar1.set(Calendar.SECOND, 59);
        calendar1.set(Calendar.MILLISECOND, 999);
        searchPickupLine.setToPickConfirmedOn(calendar1.getTime());

        log.info("StartCreatedOn ------> {}", searchPickupLine.getFromPickConfirmedOn());
        log.info("EndCreatedOn ------> {}", searchPickupLine.getFromPickConfirmedOn());

        searchPickupLine.setWarehouseId(Collections.singletonList("110"));

        // Generate the PDF report for WH_ID 110
        PickerDenialReport pickerDenialReport = transactionService.pickerDenialReport(searchPickupLine);
        log.info("PickupLines ---------------> {}", pickerDenialReport);

        File pdfFile = new File("WMS_Daily_Order_Report_110.pdf");
        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            PickupDenialService.export(fos, pickerDenialReport, searchPickupLine, startOrderDate); // Adjust `export` to accept OutputStream
        }

        String fileName1 = "WMS_Daily_Order_Report_110.pdf";

        // Convert the File to MultipartFile
        try (FileInputStream fileInputStream = new FileInputStream(pdfFile)) {
            MultipartFile multipartFile = new MockMultipartFile(
                    fileName1,                 // Original file name
                    fileName1,                 // File name
                    "application/pdf",        // Content type
                    fileInputStream           // File content as InputStream
            );

            // Use the existing storeFile method
            fileStorageService.storeFile(multipartFile);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to store the file.");
        }

//        sendMailService.sendTvReportMail(fileName1, fileName2);

        return ResponseEntity.ok("Email sent successfully: " + fileName1);
    }

}
