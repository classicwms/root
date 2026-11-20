package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.threepl.billingmodeid.AddBillingModeId;
import com.tekclover.wms.api.idmaster.model.threepl.billingmodeid.BillingModeId;
import com.tekclover.wms.api.idmaster.model.threepl.billingmodeid.FindBillingModeId;
import com.tekclover.wms.api.idmaster.model.threepl.billingmodeid.UpdateBillingModeId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.BillingModeIdService;
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
@Api(tags = {"BillingModeId"}, value = "BillingModeId Operations related to BillingModeIdController")
@SwaggerDefinition(tags = {@Tag(name = "BillingModeId", description = "Operations related to BillingModeId")})
@RequestMapping("/billingmodeid")
@RestController
public class BillingModeIdController {
    @Autowired
    BillingModeIdService billingModeIdService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = BillingModeId.class, value = "Get all BillingModeId details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<BillingModeId> BillingModeIdList = billingModeIdService.getBillingModeIds();
        return new ResponseEntity<>(BillingModeIdList, HttpStatus.OK);
    }

    @ApiOperation(response = BillingModeId.class, value = "Get a BillingModeId") // label for swagger
    @GetMapping("/{billModeId}")
    public ResponseEntity<?> getBillingModeId(@RequestParam String warehouseId, @PathVariable Long billModeId,
                                              @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            BillingModeId BillingModeId =
                    billingModeIdService.getBillingModeId(warehouseId, billModeId, companyCodeId, languageId, plantId);
            log.info("BillingModeId : " + BillingModeId);
            return new ResponseEntity<>(BillingModeId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = BillingModeId.class, value = "Create BillingModeId") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postBillingModeId(@Valid @RequestBody AddBillingModeId newBillingModeId,
                                               @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(newBillingModeId.getCompanyCodeId(), newBillingModeId.getPlantId(), newBillingModeId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            BillingModeId createdBillingModeId = billingModeIdService.createBillingModeId(newBillingModeId, loginUserID);
            return new ResponseEntity<>(createdBillingModeId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = BillingModeId.class, value = "Update BillingModeId") // label for swagger
    @PatchMapping("/{billModeId}")
    public ResponseEntity<?> patchBillingModeId(@RequestParam String warehouseId, @PathVariable Long billModeId, @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId,
                                                @Valid @RequestBody UpdateBillingModeId updateBillingModeId, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            BillingModeId createdBillingModeId =
                    billingModeIdService.updateBillingModeId(warehouseId, billModeId, companyCodeId, languageId, plantId, loginUserID, updateBillingModeId);
            return new ResponseEntity<>(createdBillingModeId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = BillingModeId.class, value = "Delete BillingModeId") // label for swagger
    @DeleteMapping("/{billModeId}")
    public ResponseEntity<?> deleteBillingModeId(@PathVariable Long billModeId,
                                                 @RequestParam String warehouseId, @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId, @RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            billingModeIdService.deleteBillingModeId(warehouseId, billModeId, companyCodeId, languageId, plantId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Search
    @ApiOperation(response = BillingModeId.class, value = "Find BillingModeId") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findBillingModeId(@Valid @RequestBody FindBillingModeId findBillingModeId) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(findBillingModeId.getCompanyCodeId(), findBillingModeId.getPlantId(), findBillingModeId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<BillingModeId> createdBillingModeId = billingModeIdService.findBillingModeId(findBillingModeId);
            return new ResponseEntity<>(createdBillingModeId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}
