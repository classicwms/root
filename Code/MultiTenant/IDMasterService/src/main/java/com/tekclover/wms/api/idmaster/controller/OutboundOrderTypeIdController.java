package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.outboundordertypeid.AddOutboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.outboundordertypeid.FindOutboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.outboundordertypeid.OutboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.outboundordertypeid.UpdateOutboundOrderTypeId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.OutboundOrderTypeIdService;
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
@Api(tags = {"OutboundOrderTypeId"}, value = "OutboundOrderTypeId  Operations related to OutboundOrderTypeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "OutboundOrderTypeId ",description = "Operations related to OutboundOrderTypeId ")})
@RequestMapping("/outboundordertypeid")
@RestController
public class OutboundOrderTypeIdController {
    @Autowired
    OutboundOrderTypeIdService outboundOrderTypeIdService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = OutboundOrderTypeId.class, value = "Get all OutboundOrderTypeId details")
    // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<OutboundOrderTypeId> outOutboundOrderTypeIdList = outboundOrderTypeIdService.getOutboundOrderTypeIds();
        return new ResponseEntity<>(outOutboundOrderTypeIdList, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundOrderTypeId.class, value = "Get a OutboundOrderTypeId") // label for swagger
    @GetMapping("/{outboundOrderTypeId}")
    public ResponseEntity<?> getOutboundOrderStatusId(@PathVariable String outboundOrderTypeId,
                                                      @RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            OutboundOrderTypeId OutboundOrderTypeId =
                    outboundOrderTypeIdService.getOutboundOrderTypeId(warehouseId, outboundOrderTypeId, companyCodeId, languageId, plantId);
            log.info("OutboundOrderTypeId : " + OutboundOrderTypeId);
            return new ResponseEntity<>(OutboundOrderTypeId, HttpStatus.OK);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OutboundOrderTypeId.class, value = "Create OutboundOrderTypeId") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postOutboundOrderTypeId(@Valid @RequestBody AddOutboundOrderTypeId newOutboundOrderTypeId,
                                                     @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(newOutboundOrderTypeId.getCompanyCodeId(), newOutboundOrderTypeId.getPlantId(), newOutboundOrderTypeId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            OutboundOrderTypeId createdOutboundOrderTypeId = outboundOrderTypeIdService.CreateOutboundOrderTypeId(newOutboundOrderTypeId, loginUserID);
            return new ResponseEntity<>(createdOutboundOrderTypeId, HttpStatus.OK);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OutboundOrderTypeId.class, value = "Update OutboundOrderTypeId") // label for swagger
    @PatchMapping("/{outboundOrderTypeId}")
    public ResponseEntity<?> patchOutboundOrderTypeId(@PathVariable String outboundOrderTypeId,
                                                      @RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
                                                      @Valid @RequestBody UpdateOutboundOrderTypeId  updateOutboundOrderTypeId, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            OutboundOrderTypeId createdOutboundOrderTypeId =
                    outboundOrderTypeIdService.updateOutboundOrderTypeId(warehouseId, outboundOrderTypeId, companyCodeId, languageId, plantId, loginUserID, updateOutboundOrderTypeId);
            return new ResponseEntity<>(createdOutboundOrderTypeId, HttpStatus.OK);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }
    @ApiOperation(response = OutboundOrderTypeId.class, value = "Delete OutboundOrderTypeId") // label for swagger
    @DeleteMapping("/{outboundOrderTypeId}")
    public ResponseEntity<?> deleteOutboundOrderTypeId(@RequestParam String warehouseId,@PathVariable String outboundOrderTypeId,@RequestParam String companyCodeId,
                                                       @RequestParam String languageId,@RequestParam String plantId,
                                                          @RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            outboundOrderTypeIdService.deleteOutboundOrderTypeId(warehouseId, outboundOrderTypeId, companyCodeId, languageId, plantId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }
    //Search
    @ApiOperation(response = OutboundOrderTypeId.class, value = "Find OutboundOrderTypeId") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findOutboundOrderTypeId(@Valid @RequestBody FindOutboundOrderTypeId findOutboundOrderTypeId) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(findOutboundOrderTypeId.getCompanyCodeId(), findOutboundOrderTypeId.getPlantId(), findOutboundOrderTypeId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<OutboundOrderTypeId> createdOutboundOrderTypeId = outboundOrderTypeIdService.findOutboundOrderTypeId(findOutboundOrderTypeId);
            return new ResponseEntity<>(createdOutboundOrderTypeId, HttpStatus.OK);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }
}


