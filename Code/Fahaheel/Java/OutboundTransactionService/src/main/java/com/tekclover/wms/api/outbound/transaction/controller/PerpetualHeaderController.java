package com.tekclover.wms.api.outbound.transaction.controller;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.*;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.v2.PerpetualHeaderEntityV2;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.v2.SearchPerpetualHeaderV2;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.service.PerpetualHeaderService;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.v2.PerpetualHeaderV2;
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
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"PerpetualHeader"}, value = "PerpetualHeader  Operations related to PerpetualHeaderController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PerpetualHeader ", description = "Operations related to PerpetualHeader ")})
@RequestMapping("/perpetualheader")
@RestController
public class PerpetualHeaderController {

    @Autowired
    PerpetualHeaderService perpetualheaderService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = PerpetualHeader.class, value = "Get all PerpetualHeader details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<PerpetualHeaderEntity> perpetualheaderList = perpetualheaderService.getPerpetualHeaders();
        return new ResponseEntity<>(perpetualheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualHeader.class, value = "Get a PerpetualHeader") // label for swagger 
    @GetMapping("/{cycleCountNo}")
    public ResponseEntity<?> getPerpetualHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
                                                @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId) {
        PerpetualHeader perpetualheader =
                perpetualheaderService.getPerpetualHeaderWithLine(warehouseId, cycleCountTypeId, cycleCountNo,
                        movementTypeId, subMovementTypeId);
        return new ResponseEntity<>(perpetualheader, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualHeaderEntity.class, value = "Search PerpetualHeader") // label for swagger
    @PostMapping("/findPerpetualHeader")
    public List<PerpetualHeader> findPerpetualHeader(@RequestBody SearchPerpetualHeader searchPerpetualHeader)
        throws Exception {

        return perpetualheaderService.findPerpetualHeader(searchPerpetualHeader);
    }


    @ApiOperation(response = PerpetualHeader.class, value = "Create PerpetualHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPerpetualHeader(@Valid @RequestBody AddPerpetualHeader newPerpetualHeader,
                                                 @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newPerpetualHeader.getCompanyCodeId(), newPerpetualHeader.getPlantId(), newPerpetualHeader.getPlantId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            PerpetualHeaderEntity createdPerpetualHeader =
                    perpetualheaderService.createPerpetualHeader(newPerpetualHeader, loginUserID);
            return new ResponseEntity<>(createdPerpetualHeader, HttpStatus.OK);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }

    /*
     * Pass From and To dates entered in Header screen into INVENOTRYMOVEMENT tables in IM_CTD_BY field
     * along with selected MVT_TYP_ID/SUB_MVT_TYP_ID values and fetch the below values
     */
    @ApiOperation(response = PerpetualLineEntity.class, value = "Create PerpetualHeader") // label for swagger
    @PostMapping("/run")
    public ResponseEntity<?> postRunPerpetualHeader(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader)
            throws IllegalAccessException, InvocationTargetException, ParseException {

        Set<PerpetualLineEntityImpl> inventoryMovements = perpetualheaderService.runPerpetualHeaderNew(runPerpetualHeader);
        return new ResponseEntity<>(inventoryMovements, HttpStatus.OK);
    }


    @ApiOperation(response = PerpetualLineEntity.class, value = "Create PerpetualHeader Stream") // label for swagger
    @PostMapping("/runStream")
    public ResponseEntity<?> postRunPerpetualHeaderNew(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader)
            throws IllegalAccessException, InvocationTargetException, ParseException {

        Set<PerpetualLineEntityImpl> inventoryMovements = perpetualheaderService.runPerpetualHeaderStream(runPerpetualHeader);
        return new ResponseEntity<>(inventoryMovements, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualHeader.class, value = "Update PerpetualHeader") // label for swagger
    @PatchMapping("/{cycleCountNo}")
    public ResponseEntity<?> patchPerpetualHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
                                                  @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
                                                  @Valid @RequestBody UpdatePerpetualHeader updatePerpetualHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(updatePerpetualHeader.getCompanyCodeId(), updatePerpetualHeader.getPlantId(), warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            PerpetualHeader createdPerpetualHeader =
                perpetualheaderService.updatePerpetualHeader(warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId,
                        subMovementTypeId, loginUserID, updatePerpetualHeader);
            return new ResponseEntity<>(createdPerpetualHeader, HttpStatus.OK);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PerpetualHeader.class, value = "Delete PerpetualHeader") // label for swagger
    @DeleteMapping("/{cycleCountNo}")
    public ResponseEntity<?> deletePerpetualHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
                                                   @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
                                                   @RequestParam String loginUserID) {

        perpetualheaderService.deletePerpetualHeader(warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId,
                subMovementTypeId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //==========================================================V2=======================================================

    @ApiOperation(response = PerpetualHeaderEntityV2.class, value = "Get all PerpetualHeader details") // label for swagger
    @GetMapping("/v2")
    public ResponseEntity<?> getAllPerpetualHeaderV2() {
        List<PerpetualHeaderEntityV2> perpetualheaderList = perpetualheaderService.getPerpetualHeadersV2();
        return new ResponseEntity<>(perpetualheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualHeaderEntityV2.class, value = "Get a PerpetualHeader") // label for swagger
    @GetMapping("/v2/{cycleCountNo}")
    public ResponseEntity<?> getPerpetualHeaderV2(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                  @RequestParam String languageId, @RequestParam String warehouseId,
                                                  @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId) {
        try {
            log.info("Get a PerpetualHeader Inputs : cycleCountNo -----> " + cycleCountNo + ", companyCodeId -----> " + companyCodeId + ", plantId -----> " + plantId +
                    ", languageId -----> " + languageId + ", warehouseId ------> " + warehouseId + ", cycleCountTypeId ------> " + cycleCountTypeId + ", movementTypeId -----> " +
                    movementTypeId + ", subMovementTypeId ------> " + subMovementTypeId);
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            PerpetualHeaderEntityV2 perpetualheader =
                perpetualheaderService.getPerpetualHeaderWithLineV2(companyCodeId, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo,
                        movementTypeId, subMovementTypeId);
            return new ResponseEntity<>(perpetualheader, HttpStatus.OK);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PerpetualHeaderV2.class, value = "Search PerpetualHeader") // label for swagger
    @PostMapping("/v2/findPerpetualHeader")
    public List<PerpetualHeaderV2> findPerpetualHeaderV2(@RequestBody SearchPerpetualHeaderV2 searchPerpetualHeader)
            throws Exception {
        try {
            log.info("SearchPerpetualHeaderV2 ------> {}", searchPerpetualHeader);
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchPerpetualHeader.getCompanyCodeId(), searchPerpetualHeader.getPlantId(), searchPerpetualHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return perpetualheaderService.findPerpetualHeaderV2(searchPerpetualHeader);
        }
            finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PerpetualHeaderEntityV2.class, value = "Search PerpetualHeader") // label for swagger
    @PostMapping("/v2/findPerpetualHeaderEntity")
    public List<PerpetualHeaderEntityV2> findPerpetualHeaderEntityV2(@RequestBody SearchPerpetualHeaderV2 searchPerpetualHeader)
            throws Exception {
        try {
            log.info("SearchPerpetualHeaderV2 ----> {}", searchPerpetualHeader);
            if (searchPerpetualHeader.getCompanyCodeId() != null && searchPerpetualHeader.getPlantId() != null && searchPerpetualHeader.getWarehouseId() != null) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbList(searchPerpetualHeader.getCompanyCodeId(), searchPerpetualHeader.getPlantId(), searchPerpetualHeader.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            } else {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbByWarehouseIn(searchPerpetualHeader.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            return perpetualheaderService.findPerpetualHeaderEntityV2(searchPerpetualHeader);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PerpetualHeaderEntityV2.class, value = "Create PerpetualHeader") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postPerpetualHeaderV2(@Valid @RequestBody PerpetualHeaderEntityV2 newPerpetualHeader,
                                                   @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            log.info("PerpetualHeaderEntityV2 ------> {}", newPerpetualHeader);
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newPerpetualHeader.getCompanyCodeId(), newPerpetualHeader.getPlantId(), newPerpetualHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            PerpetualHeaderEntityV2 createdPerpetualHeader =
                    perpetualheaderService.createPerpetualHeaderV2(newPerpetualHeader, loginUserID);
            return new ResponseEntity<>(createdPerpetualHeader, HttpStatus.OK);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }

    /*
     * Pass From and To dates entered in Header screen into INVENOTRYMOVEMENT tables in IM_CTD_BY field
     * along with selected MVT_TYP_ID/SUB_MVT_TYP_ID values and fetch the below values
     */
    @ApiOperation(response = PerpetualLineEntityImpl.class, value = "Create PerpetualHeader") // label for swagger
    @PostMapping("/v2/run")
    public ResponseEntity<?> postRunPerpetualHeaderV2(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader)
            throws IllegalAccessException, InvocationTargetException, ParseException {

        try {
            log.info("RunPerpetualHeader input ------> {}", runPerpetualHeader);
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(runPerpetualHeader.getCompanyCodeId(), runPerpetualHeader.getPlantId(), runPerpetualHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            Set<PerpetualLineEntityImpl> inventoryMovements = perpetualheaderService.runPerpetualHeaderNewV2(runPerpetualHeader);
            return new ResponseEntity<>(inventoryMovements, HttpStatus.OK);
        }  finally {
            DataBaseContextHolder.clear();
        }
    }

    // Api changed from stream to List for Fahaheel MT
    @ApiOperation(response = PerpetualHeaderEntityV2.class, value = "Create PerpetualHeader Stream")    // label for swagger
    @PostMapping("/v2/runStream")
    public ResponseEntity<?> postRunPerpetualHeaderNewV2(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            log.info("RunPerpetualHeader input ------> {}", runPerpetualHeader);
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(runPerpetualHeader.getCompanyCodeId(), runPerpetualHeader.getPlantId(), runPerpetualHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            Set<PerpetualLineEntityImpl> inventoryMovements = perpetualheaderService.runPerpetualHeaderStreamV2(runPerpetualHeader);
            return new ResponseEntity<>(inventoryMovements, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PerpetualHeaderV2.class, value = "Update PerpetualHeader") // label for swagger
    @PatchMapping("/v2/{cycleCountNo}")
    public ResponseEntity<?> patchPerpetualHeaderV2(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                    @RequestParam String languageId, @RequestParam String warehouseId,
                                                    @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
                                                    @Valid @RequestBody PerpetualHeaderEntityV2 updatePerpetualHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            log.info("Update PerpetualHeader Inputs : cycleCountNo -----> " + cycleCountNo + ", companyCodeId -----> " + companyCodeId + ", plantId -----> " + plantId +
                    ", languageId -----> " + languageId + ", warehouseId ------> " + warehouseId + ", cycleCountTypeId ------> " + cycleCountTypeId + ", movementTypeId -----> " +
                    movementTypeId + ", subMovementTypeId ------> " + subMovementTypeId);

            log.info("PerpetualHeaderEntityV2 Input -----> {}", updatePerpetualHeader);
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            PerpetualHeaderV2 createdPerpetualHeader =
                    perpetualheaderService.updatePerpetualHeaderV2(updatePerpetualHeader.getCompanyCodeId(), updatePerpetualHeader.getPlantId(), updatePerpetualHeader.getLanguageId(),
                            updatePerpetualHeader.getWarehouseId(), updatePerpetualHeader.getCycleCountTypeId(), updatePerpetualHeader.getCycleCountNo(),
                            updatePerpetualHeader.getMovementTypeId(), updatePerpetualHeader.getSubMovementTypeId(), loginUserID, updatePerpetualHeader);
            return new ResponseEntity<>(createdPerpetualHeader, HttpStatus.OK);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PerpetualHeader.class, value = "Delete PerpetualHeader") // label for swagger
    @DeleteMapping("/v2/{cycleCountNo}")
    public ResponseEntity<?> deletePerpetualHeaderV2(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                     @RequestParam String languageId, @RequestParam String warehouseId,
                                                     @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
                                                     @RequestParam String loginUserID) throws ParseException {
        try {
            log.info("Delete PerpetualHeader Inputs : cycleCountNo -----> " + cycleCountNo + ", companyCodeId -----> " + companyCodeId + ", plantId -----> " + plantId +
                    ", languageId -----> " + languageId + ", warehouseId ------> " + warehouseId + ", cycleCountTypeId ------> " + cycleCountTypeId + ", movementTypeId -----> " +
                    movementTypeId + ", subMovementTypeId ------> " + subMovementTypeId);
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            perpetualheaderService.deletePerpetualHeaderV2(companyCodeId, plantId, languageId, warehouseId, cycleCountTypeId,
                    cycleCountNo, movementTypeId, subMovementTypeId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }
}