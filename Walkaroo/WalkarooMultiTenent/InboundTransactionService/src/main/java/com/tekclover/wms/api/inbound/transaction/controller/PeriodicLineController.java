package com.tekclover.wms.api.inbound.transaction.controller;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.cyclecount.periodic.PeriodicLine;
import com.tekclover.wms.api.inbound.transaction.model.cyclecount.periodic.PeriodicUpdateResponse;
import com.tekclover.wms.api.inbound.transaction.model.cyclecount.periodic.SearchPeriodicLine;
import com.tekclover.wms.api.inbound.transaction.model.cyclecount.periodic.UpdatePeriodicLine;
import com.tekclover.wms.api.inbound.transaction.model.cyclecount.periodic.v2.PeriodicLineV2;
import com.tekclover.wms.api.inbound.transaction.model.cyclecount.periodic.v2.PeriodicUpdateResponseV2;
import com.tekclover.wms.api.inbound.transaction.model.cyclecount.periodic.v2.PeriodicZeroStockLine;
import com.tekclover.wms.api.inbound.transaction.model.cyclecount.periodic.v2.SearchPeriodicLineV2;
import com.tekclover.wms.api.inbound.transaction.model.cyclecount.perpetual.AssignHHTUserCC;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.service.BaseService;
import com.tekclover.wms.api.inbound.transaction.service.PeriodicLineService;
import com.tekclover.wms.api.inbound.transaction.service.PeriodicZeroStkLineService;
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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"PeriodicLine"}, value = "PeriodicLine  Operations related to PeriodicLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PeriodicLine ", description = "Operations related to PeriodicLine ")})
@RequestMapping("/periodicline")
@RestController
public class PeriodicLineController {

    @Autowired
    PeriodicLineService periodicLineService;

    @Autowired
    PeriodicZeroStkLineService periodicZeroStkLineService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = PeriodicLine.class, value = "SearchPeriodicLine") // label for swagger
    @PostMapping("/findPeriodicLine")
    public List<PeriodicLine> findPeriodicLine(@RequestBody SearchPeriodicLine searchPeriodicLine)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchPeriodicLine.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return periodicLineService.findPeriodicLine(searchPeriodicLine);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Stream
    @ApiOperation(response = PeriodicLine.class, value = "SearchPeriodicLine Stream") // label for swagger
    @PostMapping("/findPeriodicLineStream")
    public Stream<PeriodicLine> findPeriodicLineStream(@RequestBody SearchPeriodicLine searchPeriodicLine)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchPeriodicLine.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return periodicLineService.findPeriodicLineStream(searchPeriodicLine);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PeriodicLine[].class, value = "AssignHHTUser") // label for swagger
    @PatchMapping("/assigingHHTUser")
    public ResponseEntity<?> patchAssingHHTUser(@RequestBody List<AssignHHTUserCC> assignHHTUser,
                                                @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(assignHHTUser.get(0).getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<PeriodicLine> updatedPeriodicLine = periodicLineService.updateAssingHHTUser(assignHHTUser, loginUserID);
            return new ResponseEntity<>(updatedPeriodicLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PeriodicLine.class, value = "Update PeriodicLine") // label for swagger
    @PatchMapping("/{cycleCountNo}")
    public ResponseEntity<?> patchPeriodicLine(@PathVariable String cycleCountNo,
                                               @RequestBody List<UpdatePeriodicLine> updatePeriodicLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(updatePeriodicLine.get(0).getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            PeriodicUpdateResponse createdPeriodicLine =
                    periodicLineService.updatePeriodicLine(cycleCountNo, updatePeriodicLine, loginUserID);
            return new ResponseEntity<>(createdPeriodicLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    //=========================================================V2===============================================

    @ApiOperation(response = PeriodicLineV2.class, value = "SearchPeriodicLineV2") // label for swagger
    @PostMapping("/v2/findPeriodicLine")
    public List<PeriodicLineV2> findPeriodicLineV2(@RequestBody SearchPeriodicLineV2 searchPeriodicLineV2)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchPeriodicLineV2.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return periodicLineService.findPeriodicLineStreamV2(searchPeriodicLineV2);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PeriodicLineV2[].class, value = "AssignHHTUser") // label for swagger
    @PatchMapping("/v2/assigingHHTUser")
    public ResponseEntity<?> patchAssingHHTUserV2(@RequestBody List<AssignHHTUserCC> assignHHTUser,
                                                  @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(assignHHTUser.get(0).getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<PeriodicLineV2> createdPeriodicLine = periodicLineService.updateAssingHHTUserV2(assignHHTUser, loginUserID);
            return new ResponseEntity<>(createdPeriodicLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PeriodicLineV2.class, value = "Update PeriodicLineV2") // label for swagger
    @PatchMapping("/v2/{cycleCountNo}")
    public ResponseEntity<?> patchPeriodicLineV2(@PathVariable String cycleCountNo,
                                                 @RequestBody List<PeriodicLineV2> updatePeriodicLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(updatePeriodicLine.get(0).getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            PeriodicUpdateResponseV2 createdPeriodicResponse =
                    periodicLineService.updatePeriodicLineV2(cycleCountNo, updatePeriodicLine, loginUserID);
            return new ResponseEntity<>(createdPeriodicResponse, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = WarehouseApiResponse.class, value = "Update PeriodicLine") // label for swagger
    @PatchMapping("/v2/confirm/{cycleCountNo}")
    public ResponseEntity<?> patchPeriodicLineConfirmV2(@PathVariable String cycleCountNo,
                                                        @RequestBody List<PeriodicLineV2> updatePerpetualLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(updatePerpetualLine.get(0).getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            WarehouseApiResponse createdPerpetualLine =
                    periodicLineService.updatePeriodicLineConfirmV2(cycleCountNo, updatePerpetualLine, loginUserID);
            return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    //=================================================Zero Stock to Create Inventory===========================================================
    @ApiOperation(response = PeriodicLineV2.class, value = "Update PeriodicLine Zero Stock") // label for swagger
    @PostMapping("/v2/createPeriodicFromZeroStk")
    public ResponseEntity<?> createPeriodicLineV2(@RequestBody List<PeriodicZeroStockLine> updatePeriodicLine, @RequestParam String loginUserID) {
        try {
            String currentDB = baseService.getDataBase(updatePeriodicLine.get(0).getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<PeriodicLineV2> createdPeriodicLine =
                    periodicZeroStkLineService.updatePeriodicZeroStkLine(updatePeriodicLine, loginUserID);
            return new ResponseEntity<>(createdPeriodicLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}