package com.tekclover.wms.api.outbound.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.outbound.preoutbound.AddPreOutboundLine;
import com.tekclover.wms.api.outbound.transaction.model.outbound.preoutbound.SearchPreOutboundLine;
import com.tekclover.wms.api.outbound.transaction.model.outbound.preoutbound.UpdatePreOutboundLine;
import com.tekclover.wms.api.outbound.transaction.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.preoutbound.v2.SearchPreOutboundLineV2;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.service.PreOutboundLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.tekclover.wms.api.outbound.transaction.model.outbound.preoutbound.PreOutboundLine;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PreOutboundLine"}, value = "PreOutboundLine  Operations related to PreOutboundLineController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PreOutboundLine ", description = "Operations related to PreOutboundLine ")})
@RequestMapping("/preoutboundline")
@RestController
public class PreOutboundLineController {

    @Autowired
    PreOutboundLineService preoutboundlineService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = PreOutboundLine.class, value = "Get all PreOutboundLine details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<PreOutboundLine> preoutboundlineList = preoutboundlineService.getPreOutboundLines();
        return new ResponseEntity<>(preoutboundlineList, HttpStatus.OK);
    }

    @ApiOperation(response = PreOutboundLine.class, value = "Search PreOutboundLine") // label for swagger
    @PostMapping("/findPreOutboundLine")
    public List<PreOutboundLine> findPreOutboundLine(@RequestBody SearchPreOutboundLine searchPreOutboundLine)
            throws Exception {

        return preoutboundlineService.findPreOutboundLine(searchPreOutboundLine);
    }

    @ApiOperation(response = PreOutboundLine.class, value = "Create PreOutboundLine") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPreOutboundLine(@Valid @RequestBody AddPreOutboundLine newPreOutboundLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newPreOutboundLine.getCompanyCodeId(), newPreOutboundLine.getPlantId(), newPreOutboundLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        PreOutboundLine createdPreOutboundLine = preoutboundlineService.createPreOutboundLine(newPreOutboundLine, loginUserID);
        return new ResponseEntity<>(createdPreOutboundLine, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

//    @ApiOperation(response = PreOutboundLine.class, value = "Update PreOutboundLine") // label for swagger
//    @PatchMapping("/{lineNumber}")
//	public ResponseEntity<?> patchPreOutboundLine(@PathVariable String lineNumber, @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String preOutboundNo, @RequestParam String partnerCode, @RequestParam String itemCode,
//			@Valid @RequestBody UpdatePreOutboundLine updatePreOutboundLine, @RequestParam String loginUserID) 
//			throws IllegalAccessException, InvocationTargetException {
//		PreOutboundLine createdPreOutboundLine = 
//				preoutboundlineService.updatePreOutboundLine(languageId, companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo, partnerCode, lineNumber, itemCode, loginUserID);
//		return new ResponseEntity<>(createdPreOutboundLine , HttpStatus.OK);
//	}
//    
//    @ApiOperation(response = PreOutboundLine.class, value = "Delete PreOutboundLine") // label for swagger
//	@DeleteMapping("/{lineNumber}")
//	public ResponseEntity<?> deletePreOutboundLine(@PathVariable String lineNumber, @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String preOutboundNo, @RequestParam String partnerCode, @RequestParam String itemCode, @RequestParam String loginUserID) {
//    	preoutboundlineService.deletePreOutboundLine(languageId, companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo, partnerCode, lineNumber, itemCode, loginUserID);
//		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//	}

    //=======================================================V2============================================================
    @ApiOperation(response = PreOutboundLineV2.class, value = "Get all PreOutboundLine details") // label for swagger
    @GetMapping("/v2")
    public ResponseEntity<?> getAllPreOutboundLineV2() {
        List<PreOutboundLineV2> preoutboundlineList = preoutboundlineService.getPreOutboundLinesV2();
        return new ResponseEntity<>(preoutboundlineList, HttpStatus.OK);
    }

    @ApiOperation(response = PreOutboundLineV2.class, value = "Search PreOutboundLine") // label for swagger
    @PostMapping("/v2/findPreOutboundLine")
    public Stream<PreOutboundLineV2> findPreOutboundLineV2(@RequestBody SearchPreOutboundLineV2 searchPreOutboundLine)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchPreOutboundLine.getCompanyCodeId(), searchPreOutboundLine.getPlantId(), searchPreOutboundLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        return preoutboundlineService.findPreOutboundLineV2(searchPreOutboundLine);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = PreOutboundLineV2.class, value = "Create PreOutboundLine") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postPreOutboundLineV2(@Valid @RequestBody PreOutboundLineV2 newPreOutboundLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newPreOutboundLine.getCompanyCodeId(), newPreOutboundLine.getPlantId(), newPreOutboundLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        PreOutboundLineV2 createdPreOutboundLine = preoutboundlineService.createPreOutboundLineV2(newPreOutboundLine, loginUserID);
        return new ResponseEntity<>(createdPreOutboundLine, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = PreOutboundLineV2.class, value = "Update PreOutboundLine") // label for swagger
    @PatchMapping("/v2/{lineNumber}")
    public ResponseEntity<?> patchPreOutboundLineV2(@PathVariable Long lineNumber, @RequestParam String languageId, @RequestParam String companyCodeId,
                                                    @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                    @RequestParam String preOutboundNo, @RequestParam String partnerCode, @RequestParam String itemCode,
                                                    @Valid @RequestBody UpdatePreOutboundLine updatePreOutboundLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        PreOutboundLineV2 createdPreOutboundLine =
                preoutboundlineService.updatePreOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber,
                        preOutboundNo, partnerCode, lineNumber, itemCode, loginUserID, updatePreOutboundLine);
        return new ResponseEntity<>(createdPreOutboundLine, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = PreOutboundLineV2.class, value = "Delete PreOutboundLine") // label for swagger
    @DeleteMapping("/v2/{lineNumber}")
    public ResponseEntity<?> deletePreOutboundLineV2(@PathVariable Long lineNumber, @RequestParam String languageId, @RequestParam String companyCodeId,
                                                     @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                     @RequestParam String preOutboundNo, @RequestParam String partnerCode, @RequestParam String itemCode,
                                                     @RequestParam String loginUserID) throws ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        preoutboundlineService.deletePreOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber,
                preOutboundNo, partnerCode, lineNumber, itemCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
}