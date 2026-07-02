package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.controlprocessid.AddControlProcessId;
import com.tekclover.wms.api.idmaster.model.controlprocessid.ControlProcessId;
import com.tekclover.wms.api.idmaster.model.controlprocessid.FindControlProcessId;
import com.tekclover.wms.api.idmaster.model.controlprocessid.UpdateControlProcessId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.repository.LanguageIdRepository;
import com.tekclover.wms.api.idmaster.service.ControlProcessIdService;
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
@Api(tags = {"ControlProcessId"}, value = "ControlProcessId  Operations related to ControlProcessIdController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ControlProcessId ", description = "Operations related to ControlProcessId ")})
@RequestMapping("/controlprocessid")
@RestController
public class ControlProcessIdController {
    @Autowired
    private LanguageIdRepository languageIdRepository;

    @Autowired
    ControlProcessIdService controlprocessidService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = ControlProcessId.class, value = "Get all ControlProcessId details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<ControlProcessId> controlprocessidList = controlprocessidService.getControlProcessIds();
        return new ResponseEntity<>(controlprocessidList, HttpStatus.OK);
    }

    @ApiOperation(response = ControlProcessId.class, value = "Get a ControlProcessId") // label for swagger 
    @GetMapping("/{controlProcessId}")
    public ResponseEntity<?> getControlProcessId(@RequestParam String warehouseId, @PathVariable String controlProcessId,
                                                 @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            ControlProcessId controlprocessid =
                    controlprocessidService.getControlProcessId(warehouseId, controlProcessId, companyCodeId, languageId, plantId);
            log.info("ControlProcessId : " + controlprocessid);
            return new ResponseEntity<>(controlprocessid, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ControlProcessId.class, value = "Create ControlProcessId") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postControlProcessId(@Valid @RequestBody AddControlProcessId newControlProcessId,
                                                  @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(newControlProcessId.getCompanyCodeId(), newControlProcessId.getPlantId(), newControlProcessId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            ControlProcessId createdControlProcessId = controlprocessidService.createControlProcessId(newControlProcessId, loginUserID);
            return new ResponseEntity<>(createdControlProcessId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ControlProcessId.class, value = "Update ControlProcessId") // label for swagger
    @PatchMapping("/{controlProcessId}")
    public ResponseEntity<?> patchControlProcessId(@PathVariable String controlProcessId,
                                                   @RequestParam String warehouseId, @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId,
                                                   @Valid @RequestBody UpdateControlProcessId updateControlProcessId, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            ControlProcessId createdControlProcessId =
                    controlprocessidService.updateControlProcessId(warehouseId, controlProcessId, companyCodeId, languageId, plantId, loginUserID, updateControlProcessId);
            return new ResponseEntity<>(createdControlProcessId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ControlProcessId.class, value = "Delete ControlProcessId") // label for swagger
    @DeleteMapping("/{controlProcessId}")
    public ResponseEntity<?> deleteControlProcessId(@RequestParam String warehouseId, @PathVariable String controlProcessId, @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId,
                                                    @RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            controlprocessidService.deleteControlProcessId(warehouseId, controlProcessId, companyCodeId, languageId, plantId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Search
    @ApiOperation(response = ControlProcessId.class, value = "Find ControlProcessId") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findControlProcessId(@Valid @RequestBody FindControlProcessId findControlProcessId) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(findControlProcessId.getCompanyCodeId(), findControlProcessId.getPlantId(), findControlProcessId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<ControlProcessId> createdControlProcessId = controlprocessidService.findControlProcessId(findControlProcessId);
            return new ResponseEntity<>(createdControlProcessId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}