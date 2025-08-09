package com.tekclover.wms.api.outbound.transaction.controller;


import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.deliveryline.DeliveryLine;
import com.tekclover.wms.api.outbound.transaction.model.driverremark.DriverRemark;
import com.tekclover.wms.api.outbound.transaction.model.driverremark.SearchDriverRemark;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.service.DriverRemarkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"DriverRemark"}, value = "DriverRemark  Operations related to DriverRemarkController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "DriverRemark ",description = "Operations related to DriverRemark ")})
@RequestMapping("/driverRemark")
@RestController
public class DriverRemarkController {

    @Autowired
    DriverRemarkService driverRemarkService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = DriverRemark.class, value = "Get all DriverRemark details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<DriverRemark> driverRemarkList = driverRemarkService.getDriverRemarks();
        return new ResponseEntity<>(driverRemarkList, HttpStatus.OK);
    }

    @ApiOperation(response = DriverRemark.class, value = "Get a DriverRemark") // label for swagger
    @GetMapping("/get")
    public ResponseEntity<?> getDriverRemark(@RequestParam String preOutboundNo , @RequestParam String refDocNumber) {
        DriverRemark driverremark = driverRemarkService.getDriverRemark(preOutboundNo , refDocNumber);
        log.info("driverRemark : " + preOutboundNo , refDocNumber);
        return new ResponseEntity<>( driverremark, HttpStatus.OK);
    }

    @ApiOperation(response = DriverRemark.class, value = "Create DriverRemark") // label for swagger
    @PostMapping("/create")
    public ResponseEntity<?> postDriverRemark(@Valid @RequestBody DriverRemark newDriverRemark,
                                           @RequestParam String loginUserID) throws Exception {

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newDriverRemark.getCompanyCodeId(), newDriverRemark.getPlantId(), newDriverRemark.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            DriverRemark createdDriverRemark = driverRemarkService.createDriverRemark(newDriverRemark, loginUserID);
            return new ResponseEntity<>(createdDriverRemark , HttpStatus.OK);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }



    @ApiOperation(response = DriverRemark.class, value = "Update DriverRemark") // label for swagger
    @PatchMapping("/update")
    public ResponseEntity<?> patchDriverRemark(@RequestBody DriverRemark driverRemark, @RequestParam String preOutboundNo , @RequestParam String refDocNumber, @RequestParam String loginUserID)
            throws Exception {

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(driverRemark.getCompanyCodeId(), driverRemark.getPlantId(), driverRemark.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            DriverRemark createdDriverRemark =
                    driverRemarkService.updateDriverRemark(driverRemark, preOutboundNo, refDocNumber, loginUserID);
            return new ResponseEntity<>(createdDriverRemark , HttpStatus.OK);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = DriverRemark.class, value = "Delete DriverRemark") // label for swagger
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteDriverRemark(@RequestBody DriverRemark driverRemark, @RequestParam String preOutboundNo , @RequestParam String refDocNumber) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(driverRemark.getCompanyCodeId(), driverRemark.getPlantId(), driverRemark.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            driverRemarkService.deleteDriverRemark(driverRemark, preOutboundNo, refDocNumber);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = DriverRemark.class, value = "Find DriverRemark") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findDriverRemark(@RequestBody SearchDriverRemark searchDriverRemark) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchDriverRemark.getCompanyCodeId(), searchDriverRemark.getPlantId(), searchDriverRemark.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<DriverRemark> createdDriverRemark = driverRemarkService.findDriverRemark(searchDriverRemark);
            return new ResponseEntity<>(createdDriverRemark, HttpStatus.OK);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }
}
