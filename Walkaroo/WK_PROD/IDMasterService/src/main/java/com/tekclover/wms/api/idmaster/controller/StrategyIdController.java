package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.strategyid.AddStrategyId;
import com.tekclover.wms.api.idmaster.model.strategyid.FindStrategyId;
import com.tekclover.wms.api.idmaster.model.strategyid.StrategyId;
import com.tekclover.wms.api.idmaster.model.strategyid.UpdateStrategyId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.StrategyIdService;
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
@Api(tags = {"StrategyId"}, value = "StrategyId  Operations related to StrategyIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StrategyId ", description = "Operations related to StrategyId ")})
@RequestMapping("/strategyid")
@RestController
public class StrategyIdController {

    @Autowired
    StrategyIdService strategyidService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = StrategyId.class, value = "Get all StrategyId details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<StrategyId> strategyidList = strategyidService.getStrategyIds();
        return new ResponseEntity<>(strategyidList, HttpStatus.OK);
    }

    @ApiOperation(response = StrategyId.class, value = "Get a StrategyId") // label for swagger 
    @GetMapping("/{strategyNo}")
    public ResponseEntity<?> getStrategyId(@RequestParam String warehouseId, @RequestParam Long strategyTypeId,
                                           @PathVariable String strategyNo,
                                           @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StrategyId strategyid =
                    strategyidService.getStrategyId(warehouseId, strategyTypeId, strategyNo, companyCodeId, languageId, plantId);
            log.info("StrategyId : " + strategyid);
            return new ResponseEntity<>(strategyid, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
//	@ApiOperation(response = StrategyId.class, value = "Search StrategyId") // label for swagger
//	@PostMapping("/findStrategyId")
//	public List<StrategyId> findStrategyId(@RequestBody SearchStrategyId searchStrategyId)
//			throws Exception {
//		return strategyidService.findStrategyId(searchStrategyId);
//	}

    @ApiOperation(response = StrategyId.class, value = "Create StrategyId") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postStrategyId(@Valid @RequestBody AddStrategyId newStrategyId,
                                            @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(newStrategyId.getCompanyCodeId(), newStrategyId.getPlantId(), newStrategyId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StrategyId createdStrategyId = strategyidService.createStrategyId(newStrategyId, loginUserID);
            return new ResponseEntity<>(createdStrategyId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StrategyId.class, value = "Update StrategyId") // label for swagger
    @PatchMapping("/{strategyNo}")
    public ResponseEntity<?> patchStrategyId(@RequestParam String warehouseId, @RequestParam Long strategyTypeId, @PathVariable String strategyNo,
                                             @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId, @RequestParam String loginUserID,
                                             @Valid @RequestBody UpdateStrategyId updateStrategyId)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StrategyId createdStrategyId =
                    strategyidService.updateStrategyId(warehouseId, strategyTypeId, strategyNo, companyCodeId, languageId, plantId, loginUserID, updateStrategyId);
            return new ResponseEntity<>(createdStrategyId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StrategyId.class, value = "Delete StrategyId") // label for swagger
    @DeleteMapping("/{strategyNo}")
    public ResponseEntity<?> deleteStrategyId(@RequestParam String warehouseId, @RequestParam Long strategyTypeId, @PathVariable String strategyNo, @RequestParam String companyCodeId,
                                              @RequestParam String languageId, @RequestParam String plantId, @RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            strategyidService.deleteStrategyId(warehouseId, strategyTypeId, strategyNo, companyCodeId, languageId, plantId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Search
    @ApiOperation(response = StrategyId.class, value = "Find StrategyId") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findStrategyId(@Valid @RequestBody FindStrategyId findStrategyId) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(findStrategyId.getCompanyCodeId(), findStrategyId.getPlantId(), findStrategyId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<StrategyId> createdStrategyId = strategyidService.findStrategyId(findStrategyId);
            return new ResponseEntity<>(createdStrategyId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}