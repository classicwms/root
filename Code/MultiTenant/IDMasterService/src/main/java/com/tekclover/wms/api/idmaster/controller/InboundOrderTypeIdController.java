package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.inboundordertypeid.AddInboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.inboundordertypeid.FindInboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.inboundordertypeid.InboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.inboundordertypeid.UpdateInboundOrderTypeId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.repository.LanguageIdRepository;
import com.tekclover.wms.api.idmaster.service.InboundOrderTypeIdService;
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
@Api(tags = {"InboundOrderTypeId"}, value = "InboundOrderTypeId  Operations related to InboundOrderTypeId") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "InboundOrderTypeId ",description = "Operations related to InboundOrderTypeId ")})
@RequestMapping("/inboundordertypeid")
@RestController
public class InboundOrderTypeIdController {
    @Autowired
    private LanguageIdRepository languageIdRepository;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    private InboundOrderTypeIdService inboundOrderTypeIdService;

    @ApiOperation(response = InboundOrderTypeId.class, value = "Get all InboundOrderTypeId details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<InboundOrderTypeId> inboundOrderTypeIdList = inboundOrderTypeIdService.getInboundOrderTypeIds();
        return new ResponseEntity<>(inboundOrderTypeIdList, HttpStatus.OK);
    }

    @ApiOperation(response = InboundOrderTypeId.class, value = "Get a InboundOrderTypeId") // label for swagger
    @GetMapping("/{inboundOrderTypeId}")
    public ResponseEntity<?> getInboundOrderTypeId(@RequestParam String warehouseId,@PathVariable String inboundOrderTypeId,@RequestParam String companyCodeId,
                                                   @RequestParam String languageId,@RequestParam String plantId) {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            InboundOrderTypeId InboundOrderTypeId =
                    inboundOrderTypeIdService.getInboundOrderTypeId(warehouseId, inboundOrderTypeId, companyCodeId, languageId, plantId);
            log.info("InboundOrderTypeId : " + InboundOrderTypeId);
            return new ResponseEntity<>(InboundOrderTypeId, HttpStatus.OK);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundOrderTypeId.class, value = "Create InboundOrderTypeId") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postInboundOrderTypeId(@Valid @RequestBody AddInboundOrderTypeId newInboundOrderTypeId,
                                                      @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(newInboundOrderTypeId.getCompanyCodeId(), newInboundOrderTypeId.getPlantId(), newInboundOrderTypeId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            InboundOrderTypeId createdInboundOrderTypeId = inboundOrderTypeIdService.createInboundOrderTypeId(newInboundOrderTypeId, loginUserID);
            return new ResponseEntity<>(createdInboundOrderTypeId, HttpStatus.OK);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundOrderTypeId.class, value = "Update InboundOrderTypeId") // label for swagger
    @PatchMapping("/{inboundOrderTypeId}")
    public ResponseEntity<?> patchInboundOrderStatusId(@RequestParam String warehouseId,@PathVariable String inboundOrderTypeId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId, @RequestParam String loginUserID,
                                                       @Valid @RequestBody UpdateInboundOrderTypeId updateInboundOrderTypeId)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            InboundOrderTypeId createdInboundOrderTypeId =
                    inboundOrderTypeIdService.updateInboundOrderTypeId(warehouseId, inboundOrderTypeId, companyCodeId, languageId, plantId, loginUserID, updateInboundOrderTypeId);
            return new ResponseEntity<>(createdInboundOrderTypeId, HttpStatus.OK);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InboundOrderTypeId.class, value = "Delete InboundOrderTypeId") // label for swagger
    @DeleteMapping("/{inboundOrderTypeId}")
    public ResponseEntity<?> deleteInboundOrderStatusId(@RequestParam String warehouseId,@PathVariable String inboundOrderTypeId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
                                                         @RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            inboundOrderTypeIdService.deleteInboundOrderTypeId(warehouseId, inboundOrderTypeId, companyCodeId, languageId, plantId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }

    //Search
    @ApiOperation(response = InboundOrderTypeId.class, value = "Find InboundOrderTypeId") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findInboundOrderTypeId(@Valid @RequestBody FindInboundOrderTypeId findInboundOrderTypeId) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(findInboundOrderTypeId.getCompanyCodeId(), findInboundOrderTypeId.getPlantId(), findInboundOrderTypeId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<InboundOrderTypeId> createdInboundOrderTypeId = inboundOrderTypeIdService.findInboundOrderTypeId(findInboundOrderTypeId);
            return new ResponseEntity<>(createdInboundOrderTypeId, HttpStatus.OK);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }
}