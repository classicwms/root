package com.tekclover.wms.api.outbound.transaction.controller;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.v2.PeriodicZeroStockLine;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.*;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.v2.PerpetualZeroStockLine;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.v2.SearchPerpetualLineV2;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.service.PerpetualLineService;
import com.tekclover.wms.api.outbound.transaction.service.PerpetualZeroStkLineService;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.v2.PerpetualLineV2;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.v2.PerpetualUpdateResponseV2;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.WarehouseApiResponse;
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
@Api(tags = {"PerpetualLine"}, value = "PerpetualLine  Operations related to PerpetualLineController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PerpetualLine ", description = "Operations related to PerpetualLine ")})
@RequestMapping("/perpetualline")
@RestController
public class PerpetualLineController {

    @Autowired
    PerpetualLineService perpetualLineService;

    @Autowired
    PerpetualZeroStkLineService perpetualZeroStkLineService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = PerpetualLine.class, value = "SearchPerpetualLine") // label for swagger
    @PostMapping("/findPerpetualLine")
    public List<PerpetualLine> findPerpetualLine(@RequestBody SearchPerpetualLine searchPerpetualLine)
            throws Exception {
        return perpetualLineService.findPerpetualLine(searchPerpetualLine);
    }

    //Stream
    @ApiOperation(response = PerpetualLine.class, value = "SearchPerpetualLineStream") // label for swagger
    @PostMapping("/findPerpetualLineStream")
    public Stream<PerpetualLine> findPerpetualLineStream(@RequestBody SearchPerpetualLine searchPerpetualLine)
            throws Exception {

            return perpetualLineService.findPerpetualLineStream(searchPerpetualLine);
        }

    @ApiOperation(response = PerpetualLine[].class, value = "AssignHHTUser") // label for swagger
    @PatchMapping("/assigingHHTUser")
    public ResponseEntity<?> patchAssingHHTUser(@RequestBody List<AssignHHTUserCC> assignHHTUser,
                                                @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        try {
            for (AssignHHTUserCC assignHHTuser :assignHHTUser){
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(assignHHTuser.getCompanyCodeId(), assignHHTuser.getPlantId(), assignHHTuser.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        }
        List<PerpetualLine> createdPerpetualLine = perpetualLineService.updateAssingHHTUser(assignHHTUser, loginUserID);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = PerpetualLine.class, value = "Update PerpetualLine") // label for swagger
    @PatchMapping("/{cycleCountNo}")
    public ResponseEntity<?> patchPerpetualLine(@PathVariable String cycleCountNo,
                                                @RequestBody List<UpdatePerpetualLine> updatePerpetualLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            for (UpdatePerpetualLine updatePerpetualLine1 : updatePerpetualLine){
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(updatePerpetualLine1.getCompanyCodeId(), updatePerpetualLine1.getPlantId(), updatePerpetualLine1.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
        PerpetualUpdateResponse createdPerpetualLine =
                perpetualLineService.updatePerpetualLine(cycleCountNo, updatePerpetualLine, loginUserID);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    //=========================================================V2===============================================

    @ApiOperation(response = PerpetualLineV2.class, value = "SearchPerpetualLine") // label for swagger
    @PostMapping("/v2/findPerpetualLine")
    public List<PerpetualLineV2> findPerpetualLineV2(@RequestBody SearchPerpetualLineV2 searchPerpetualLine)
            throws Exception {
        log.info("SearchPerpetualLineV2 ----> {}", searchPerpetualLine);
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbByWarehouse(searchPerpetualLine.getWarehouseId());
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        return perpetualLineService.findPerpetualLineV2(searchPerpetualLine);
    }

    @ApiOperation(response = PerpetualLineV2[].class, value = "AssignHHTUser") // label for swagger
    @PatchMapping("/v2/assigingHHTUser")
    public ResponseEntity<?> patchAssingHHTUserV2(@RequestBody List<AssignHHTUserCC> assignHHTUser,
                                                  @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        try {
            for (AssignHHTUserCC assignHHTuser :assignHHTUser){
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(assignHHTuser.getCompanyCodeId(), assignHHTuser.getPlantId(), assignHHTuser.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
        List<PerpetualLineV2> createdPerpetualLine = perpetualLineService.updateAssingHHTUserV2(assignHHTUser, loginUserID);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = PerpetualLineV2.class, value = "Update PerpetualLine") // label for swagger
    @PatchMapping("/v2/{cycleCountNo}")
    public ResponseEntity<?> patchPerpetualLineV2(@PathVariable String cycleCountNo,
                                                  @RequestBody List<PerpetualLineV2> updatePerpetualLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            for (PerpetualLineV2 updatePerpetualLine1 : updatePerpetualLine){
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(updatePerpetualLine1.getCompanyCodeId(), updatePerpetualLine1.getPlantId(), updatePerpetualLine1.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
        PerpetualUpdateResponseV2 createdPerpetualLine =
                perpetualLineService.updatePerpetualLineV2(cycleCountNo, updatePerpetualLine, loginUserID);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
    //=================================================Zero Stock to Create Inventory===========================================================
    @ApiOperation(response = PerpetualLineV2.class, value = "Update PerpetualLine") // label for swagger
    @PostMapping("/v2/createPerpetualFromZeroStk")
    public ResponseEntity<?> createPerpetualLineV2(@RequestBody List<PerpetualZeroStockLine> updatePerpetualLine, @RequestParam String loginUserID) {


            List<PerpetualLineV2> createdPerpetualLine =
                perpetualZeroStkLineService.updatePerpetualZeroStkLine(updatePerpetualLine, loginUserID);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }


    @ApiOperation(response = WarehouseApiResponse.class, value = "Update PerpetualLine") // label for swagger
    @PatchMapping("/v2/confirm/{cycleCountNo}")
    public ResponseEntity<?> patchPerpetualLineConfirmV2(@PathVariable String cycleCountNo,
                                                         @RequestBody List<PerpetualLineV2> updatePerpetualLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            for (PerpetualLineV2 updatePerpetualLine1 :updatePerpetualLine){
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(updatePerpetualLine1.getCompanyCodeId(), updatePerpetualLine1.getPlantId(), updatePerpetualLine1.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
        WarehouseApiResponse createdPerpetualLine =
                perpetualLineService.updatePerpetualLineConfirmV2(cycleCountNo, updatePerpetualLine, loginUserID);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
}