package com.tekclover.wms.api.inbound.transaction.controller;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.dto.PreInboundLineOutputReport;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.AddPreInboundLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.PreInboundLineEntity;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.UpdatePreInboundLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundLineOutputV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.SearchPreInboundLineV2;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.service.BaseService;
import com.tekclover.wms.api.inbound.transaction.service.PreInboundLineService;
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
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"PreInboundLine"}, value = "PreInboundLine  Operations related to PreInboundLineController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PreInboundLine ", description = "Operations related to PreInboundLine ")})
@RequestMapping("/preinboundline")
@RestController
public class PreInboundLineController {

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    PreInboundLineService preinboundlineService;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = PreInboundLineEntity.class, value = "Get all PreInboundLine details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<PreInboundLineEntity> preinboundlineList = preinboundlineService.getPreInboundLines();
        return new ResponseEntity<>(preinboundlineList, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundLineEntity.class, value = "Get a PreInboundLine") // label for swagger 
    @GetMapping("/{preInboundNo}")
    public ResponseEntity<?> getPreInboundLine(@PathVariable String preInboundNo) {
        List<PreInboundLineEntity> preinboundline = preinboundlineService.getPreInboundLine(preInboundNo);
//    	log.info("PreInboundLine : " + preinboundline);
        return new ResponseEntity<>(preinboundline, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundLineEntity.class, value = "Create PreInboundLine") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPreInboundLine(@Valid @RequestBody AddPreInboundLine newPreInboundLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(newPreInboundLine.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            PreInboundLineEntity createdPreInboundLine = preinboundlineService.createPreInboundLine(newPreInboundLine, loginUserID);
            return new ResponseEntity<>(createdPreInboundLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PreInboundLineEntity.class, value = "Insertion of BOM item") // label for swagger
    @PostMapping("/bom")
    public ResponseEntity<?> postPreInboundLineBOM(@RequestParam String preInboundNo, @RequestParam String warehouseId,
                                                   @RequestParam String refDocNumber, @RequestParam String itemCode, @RequestParam Long lineNo, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<PreInboundLineEntity> createdPreInboundLine =
                preinboundlineService.createPreInboundLine(preInboundNo, warehouseId, refDocNumber, itemCode, lineNo, loginUserID);
        return new ResponseEntity<>(createdPreInboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundLineEntity.class, value = "Update PreInboundLine") // label for swagger
    @PatchMapping("/{preInboundNo}")
    public ResponseEntity<?> patchPreInboundLine(@PathVariable String preInboundNo, @RequestParam String warehouseId,
                                                 @RequestParam String refDocNumber, @RequestParam Long lineNo, @RequestParam String itemCode,
                                                 @Valid @RequestBody UpdatePreInboundLine updatePreInboundLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(updatePreInboundLine.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            PreInboundLineEntity createdPreInboundLine =
                    preinboundlineService.updatePreInboundLine(preInboundNo, warehouseId, refDocNumber, lineNo, itemCode, updatePreInboundLine, loginUserID);
            return new ResponseEntity<>(createdPreInboundLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PreInboundLineEntity.class, value = "Delete PreInboundLine") // label for swagger
    @DeleteMapping("/{preInboundNo}")
    public ResponseEntity<?> deletePreInboundLine(@PathVariable String preInboundNo, @RequestParam String warehouseId,
                                                  @RequestParam String refDocNumber, @RequestParam Long lineNo, @RequestParam String itemCode, @RequestParam String loginUserID) {
        preinboundlineService.deletePreInboundLine(preInboundNo, warehouseId, refDocNumber, lineNo, itemCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //=============================================================V2==================================================================//

    @ApiOperation(response = PreInboundLineEntityV2.class, value = "Get a PreInboundLine V2") // label for swagger
    @GetMapping("/v2/{preInboundNo}")
    public ResponseEntity<?> getPreInboundLineV2(@PathVariable String preInboundNo) {
        List<PreInboundLineEntityV2> preinboundline = preinboundlineService.getPreInboundLineV2(preInboundNo);
//    	log.info("PreInboundLine : " + preinboundline);
        return new ResponseEntity<>(preinboundline, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundLineEntityV2.class, value = "Get a PreInboundLine V2") // label for swagger
    @GetMapping("/{preInboundNo}/v2")
    public ResponseEntity<?> getPreInboundLineV2(@PathVariable String preInboundNo, @RequestParam String companyCode,
                                                 @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                                 @RequestParam String refDocNumber, @RequestParam String itemCode,
                                                 @RequestParam Long lineNo) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            PreInboundLineEntityV2 preinboundline = preinboundlineService.getPreInboundLineV2(companyCode, plantId, languageId, preInboundNo, warehouseId, refDocNumber, lineNo, itemCode);
//    	log.info("PreInboundLine : " + preinboundline);
            return new ResponseEntity<>(preinboundline, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PreInboundLineEntityV2.class, value = "Create PreInboundLine V2") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postPreInboundLineV2(@Valid @RequestBody PreInboundLineEntityV2 newPreInboundLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newPreInboundLine.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            PreInboundLineEntityV2 createdPreInboundLine = preinboundlineService.createPreInboundLineV2(newPreInboundLine, loginUserID);
            return new ResponseEntity<>(createdPreInboundLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PreInboundLineEntityV2.class, value = "Insertion of BOM item V2") // label for swagger
    @PostMapping("/bom/v2")
    public ResponseEntity<?> postPreInboundLineBOMV2(@RequestParam String preInboundNo, @RequestParam String warehouseId,
                                                     @RequestParam String companyCode, @RequestParam String plantId, @RequestParam String languageId,
                                                     @RequestParam String refDocNumber, @RequestParam String itemCode,
                                                     @RequestParam Long lineNo, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<PreInboundLineEntityV2> createdPreInboundLine =
                    preinboundlineService.createPreInboundLineV2(companyCode, plantId, languageId,
                            preInboundNo, warehouseId, refDocNumber,
                            itemCode, lineNo, loginUserID);
            return new ResponseEntity<>(createdPreInboundLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PreInboundLineEntityV2.class, value = "Update PreInboundLine V2") // label for swagger
    @PatchMapping("/v2/{preInboundNo}")
    public ResponseEntity<?> patchPreInboundLineV2(@PathVariable String preInboundNo, @RequestParam String warehouseId, @RequestParam String companyCode,
                                                   @RequestParam String plantId, @RequestParam String languageId,
                                                   @RequestParam String refDocNumber, @RequestParam Long lineNo, @RequestParam String itemCode,
                                                   @Valid @RequestBody PreInboundLineEntityV2 updatePreInboundLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            PreInboundLineEntityV2 createdPreInboundLine =
                    preinboundlineService.updatePreInboundLineV2(companyCode, plantId, languageId, preInboundNo, warehouseId,
                            refDocNumber, lineNo, itemCode, updatePreInboundLine, loginUserID);
            return new ResponseEntity<>(createdPreInboundLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PreInboundLineEntityV2.class, value = "Delete PreInboundLine V2") // label for swagger
    @DeleteMapping("/v2/{preInboundNo}")
    public ResponseEntity<?> deletePreInboundLineV2(@PathVariable String preInboundNo, @RequestParam String warehouseId, @RequestParam String companyCode,
                                                    @RequestParam String plantId, @RequestParam String languageId, @RequestParam String refDocNumber,
                                                    @RequestParam Long lineNo, @RequestParam String itemCode, @RequestParam String loginUserID) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            preinboundlineService.deletePreInboundLineV2(companyCode, plantId, languageId, preInboundNo, warehouseId, refDocNumber, lineNo, itemCode, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PreInboundLineOutputV2.class, value = "Search PreInboundLine V2") // label for swagger
    @PostMapping("/findPreInboundLine/v2")
    public List<PreInboundLineOutputV2> findPreInboundLineV2(@RequestBody SearchPreInboundLineV2 searchPreInboundLine)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchPreInboundLine.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return preinboundlineService.findPreInboundLineV2(searchPreInboundLine);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PreInboundLineOutputReport.class, value = "Search PreInboundLine V2") // label for swagger
    @PostMapping("/findPreInboundLine/report")
    public List<PreInboundLineOutputReport> findPreInboundLineReport(@RequestBody SearchPreInboundLineV2 searchPreInboundLine)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchPreInboundLine.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return preinboundlineService.findPreInboundLineReport(searchPreInboundLine);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}