package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.putawaystrategy.AddPutAwayStrategy;
import com.tekclover.wms.api.masters.model.putawaystrategy.FindPutAwayStrategy;
import com.tekclover.wms.api.masters.model.putawaystrategy.PutAwayStrategy;
import com.tekclover.wms.api.masters.model.putawaystrategy.UpdatePutAwayStrategy;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.BaseService;
import com.tekclover.wms.api.masters.service.PutAwayStrategyService;
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
import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"PutAwayStrategy"}, value = "PutAwayStrategy  Operations related to PutAwayStrategyController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PutAwayStrategy ", description = "Operations related to PutAwayStrategy ")})
@RequestMapping("/putAwayStrategy")
@RestController
public class PutAwayStrategyController {

    @Autowired
    private PutAwayStrategyService putAwayStrategyService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = PutAwayStrategy.class, value = "Get a PutAwayStrategy") // label for swagger
    @GetMapping("/get")
    public ResponseEntity<?> getPutAwayStrategy(@RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                @RequestParam String warehouseId, @RequestParam String brand, @RequestParam String article, @RequestParam String category) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            PutAwayStrategy dbPutAwayStrategy = putAwayStrategyService.getPutAwayStrategy(languageId, companyCodeId, plantId, warehouseId, brand, article, category);
            return new ResponseEntity<>(dbPutAwayStrategy, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    @ApiOperation(response = PutAwayStrategy.class, value = "Create PutAwayStrategy") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPutAwayStrategy(@Valid @RequestBody AddPutAwayStrategy newPutAwayStrategy, @RequestParam String loginUserID)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(newPutAwayStrategy.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            PutAwayStrategy createdPutAwayStrategy = putAwayStrategyService.createPutAwayStrategy(newPutAwayStrategy, loginUserID);
            return new ResponseEntity<>(createdPutAwayStrategy, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PutAwayStrategy.class, value = "Update PutAwayStrategy") // label for swagger
    @PatchMapping("/update")
    public ResponseEntity<?> patchPutAwayStrategy(@RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                  @RequestParam String warehouseId, @RequestParam String brand, @RequestParam String article, @RequestParam String category,
                                                  @Valid @RequestBody UpdatePutAwayStrategy updatePutAwayStrategy,
                                                  @RequestParam String loginUserID)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            PutAwayStrategy updatedPutAwayStrategy =
                    putAwayStrategyService.updatePutAwayStrategy(languageId, companyCodeId, plantId, warehouseId, brand, article, category, updatePutAwayStrategy, loginUserID);
            return new ResponseEntity<>(updatedPutAwayStrategy, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PutAwayStrategy.class, value = "Delete PutAwayStrategy") // label for swagger
    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePutAwayStrategy(@RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                   @RequestParam String warehouseId, @RequestParam String brand, @RequestParam String article, @RequestParam String category, @RequestParam String loginUserID) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            putAwayStrategyService.deletePutAwayStrategy(languageId, companyCodeId, plantId, warehouseId, brand, article, category, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PutAwayStrategy.class, value = "Find PutAwayStrategy") // label for swagger
    @PostMapping("/findPutAwayStrategy")
    public ResponseEntity<?> findPutAwayStrategy(@Valid @RequestBody FindPutAwayStrategy findPutAwayStrategy) throws Exception {
        try {
            String currentDB = baseService.getDataBase(findPutAwayStrategy.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<PutAwayStrategy> putAwayStrategyList = putAwayStrategyService.findPutAwayStrategy(findPutAwayStrategy);
            return new ResponseEntity<>(putAwayStrategyList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}
