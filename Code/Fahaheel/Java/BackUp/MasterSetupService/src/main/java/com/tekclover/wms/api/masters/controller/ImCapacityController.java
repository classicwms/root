package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.imcapacity.AddImCapacity;
import com.tekclover.wms.api.masters.model.imcapacity.ImCapacity;
import com.tekclover.wms.api.masters.model.imcapacity.SearchImCapacity;
import com.tekclover.wms.api.masters.model.imcapacity.UpdateImCapacity;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.ImCapacityService;
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
@Api(tags = {"ImCapacity"}, value = "ImCapacity  Operations related to ImCapacity") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ImBatchSerial ",description = "Operations related to ImBatchSerial")})
@RequestMapping("/imcapacity")
@RestController
public class ImCapacityController {

    @Autowired
    ImCapacityService imCapacityService;


    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = ImCapacity.class, value = "Get all ImCapacity details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<ImCapacity> imCapacityList = imCapacityService.getAllImCapacity();
        return new ResponseEntity<>(imCapacityList, HttpStatus.OK);
    }

    @ApiOperation(response = ImCapacity.class, value = "Get a ImCapacity") // label for swagger
    @GetMapping("/{itemCode}")
    public ResponseEntity<?> getImBasicData2(@PathVariable String itemCode, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String languageId) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        ImCapacity imCapacity = imCapacityService.getImCapacity(warehouseId,companyCodeId,languageId,plantId,itemCode);
        log.info("imCapacity : " + imCapacity);
        return new ResponseEntity<>(imCapacity, HttpStatus.OK);
    }

        finally {
            DataBaseContextHolder.clear();
        }
        }
    @ApiOperation(response = ImCapacity.class, value = "Search ImCapacity") // label for swagger
    @PostMapping("/findImCapacity")
    public List<ImCapacity> findImCapacity(@RequestBody SearchImCapacity searchImCapacity)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbNameList(searchImCapacity.getCompanyCodeId(), searchImCapacity.getPlantId(), searchImCapacity.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        return imCapacityService.findImCapacity(searchImCapacity);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = ImCapacity.class, value = "Create ImCapacity") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postImCapacity(@Valid @RequestBody AddImCapacity newImCapacity, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newImCapacity.getCompanyCodeId(), newImCapacity.getPlantId(), newImCapacity.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        ImCapacity createdImCapacity = imCapacityService.createImCapacity(newImCapacity, loginUserID);
        return new ResponseEntity<>(createdImCapacity , HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = ImCapacity.class, value = "Update ImCapacity") // label for swagger
    @PatchMapping("/{itemCode}")
    public ResponseEntity<?> patchImCapacity(@PathVariable String itemCode, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String languageId,
                                             @Valid @RequestBody UpdateImCapacity updateImCapacity, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        ImCapacity createdImCapacity = imCapacityService.updateImCapacity(companyCodeId,plantId,warehouseId,languageId,itemCode,updateImCapacity, loginUserID);
        return new ResponseEntity<>(createdImCapacity , HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = ImCapacity.class, value = "Delete ImCapacity") // label for swagger
    @DeleteMapping("/{itemCode}")
    public ResponseEntity<?> deleteImCapacity(@PathVariable String itemCode,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String warehouseId,@RequestParam String languageId,@RequestParam String loginUserID) throws ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        imCapacityService.deleteImCapacity(companyCodeId,languageId,plantId,warehouseId,itemCode,loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
}
