package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.packingmaterial.PackingMaterial;
import com.tekclover.wms.api.masters.model.workcenter.AddWorkCenter;
import com.tekclover.wms.api.masters.model.workcenter.SearchWorkCenter;
import com.tekclover.wms.api.masters.model.workcenter.UpdateWorkCenter;
import com.tekclover.wms.api.masters.model.workcenter.WorkCenter;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.WorkCenterService;
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
@Api(tags = {"WorkCenter"}, value = "WorkCenter  Operations related to WorkCenterController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "WorkCenter ",description = "Operations related to WorkCenter ")})
@RequestMapping("/workcenter")
@RestController
public class WorkCenterController {
    @Autowired
    WorkCenterService workCenterService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = WorkCenter.class, value = "Get a WorkCenter") // label for swagger
    @GetMapping("/{workCenterId}")
    public ResponseEntity<?> getWorkCenter(@PathVariable Long workCenterId,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String warehouseId,@RequestParam String languageId,@RequestParam String workCenterType) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId,warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        WorkCenter workCenter = workCenterService.getWorkCenter(workCenterId,companyCodeId,plantId,languageId,warehouseId,workCenterType);
        log.info("workCenter : " + workCenter);
        return new ResponseEntity<>(workCenter, HttpStatus.OK);
    }

        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = WorkCenter.class, value = "Search WorkCenter") // label for swagger
    @PostMapping("/findWorkCenter")
    public List<WorkCenter> findWorkCenter(@RequestBody SearchWorkCenter searchWorkCenter)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbNameList(searchWorkCenter.getCompanyCodeId(), searchWorkCenter.getPlantId(),searchWorkCenter.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        return workCenterService.findWorkCenter(searchWorkCenter);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = WorkCenter.class, value = "Create WorkCenter") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postWorkCenter(@Valid @RequestBody AddWorkCenter newWorkCenter, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newWorkCenter.getCompanyCodeId(), newWorkCenter.getPlantId(), newWorkCenter.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        WorkCenter createdWorkCenter = workCenterService.createWorkCenter(newWorkCenter, loginUserID);
        return new ResponseEntity<>(createdWorkCenter , HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = WorkCenter.class, value = "Update WorkCenter") // label for swagger
    @PatchMapping("/{workCenterId}")
    public ResponseEntity<?> patchWorkCenter(@PathVariable Long workCenterId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String languageId,@RequestParam String workCenterType,
                                             @Valid @RequestBody UpdateWorkCenter updateWorkCenter, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId,warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        WorkCenter createdWorkCenter = workCenterService.updateWorkCenter(workCenterId,companyCodeId,plantId,warehouseId,workCenterType,languageId,updateWorkCenter, loginUserID);
        return new ResponseEntity<>(createdWorkCenter , HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = WorkCenter.class, value = "Delete WorkCenter") // label for swagger
    @DeleteMapping("/{workCenterId}")
    public ResponseEntity<?> deleteWorkCenter(@PathVariable Long workCenterId, @RequestParam String loginUserID, @RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String workCenterType,@RequestParam String languageId) throws ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId,warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        workCenterService.deleteWorkCenterId(workCenterId,companyCodeId,plantId,warehouseId,workCenterType,languageId,loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
}
