package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.threepl.billingfrequencyid.AddBillingFrequencyId;
import com.tekclover.wms.api.idmaster.model.threepl.billingfrequencyid.BillingFrequencyId;
import com.tekclover.wms.api.idmaster.model.threepl.billingfrequencyid.FindBillingFrequencyId;
import com.tekclover.wms.api.idmaster.model.threepl.billingfrequencyid.UpdateBillingFrequencyId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.repository.LanguageIdRepository;
import com.tekclover.wms.api.idmaster.service.BillingFrequencyIdService;
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
@Api(tags = {"BillingFrequencyId"}, value = "BillingFrequencyId Operations related to BillingFrequencyIdController")
@SwaggerDefinition(tags = {@Tag(name = "BillingFrequencyId", description = "Operations related to BillingFrequencyId")})
@RequestMapping("/billingfrequencyid")
@RestController
public class BillingFrequencyIdController {
    @Autowired
    private LanguageIdRepository languageIdRepository;
    @Autowired
    BillingFrequencyIdService billingFrequencyIdService;
    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = BillingFrequencyId.class, value = "Get all BillingFrequencyId details")
    // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<BillingFrequencyId> BillingFrequencyIdList = billingFrequencyIdService.getBillingFrequencyIds();
        return new ResponseEntity<>(BillingFrequencyIdList, HttpStatus.OK);
    }

    @ApiOperation(response = BillingFrequencyId.class, value = "Get a BillingFrequencyId") // label for swagger
    @GetMapping("/{billFrequencyId}")
    public ResponseEntity<?> getBillingFrequencyId(@RequestParam String warehouseId, @PathVariable Long billFrequencyId, @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            BillingFrequencyId BillingFrequencyId =
                    billingFrequencyIdService.getBillingFrequencyId(warehouseId, billFrequencyId, companyCodeId, languageId, plantId);
            log.info("BillFrequencyId : " + BillingFrequencyId);
            return new ResponseEntity<>(BillingFrequencyId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = BillingFrequencyId.class, value = "Create BillingFrequencyId") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postBillingFrequencyId(@Valid @RequestBody AddBillingFrequencyId newBillingFrequencyId,
                                                    @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(newBillingFrequencyId.getCompanyCodeId(), newBillingFrequencyId.getPlantId(), newBillingFrequencyId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            BillingFrequencyId createdBillingFrequencyId = billingFrequencyIdService.createBillingFrequencyId(newBillingFrequencyId, loginUserID);
            return new ResponseEntity<>(createdBillingFrequencyId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = BillingFrequencyId.class, value = "Update BillingFrequencyId") // label for swagger
    @PatchMapping("/{billFrequencyId}")
    public ResponseEntity<?> patchBillingFrequencyId(@RequestParam String warehouseId, @PathVariable Long billFrequencyId, @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId,
                                                     @Valid @RequestBody UpdateBillingFrequencyId updateBillingFrequencyId, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            BillingFrequencyId createdBillingFrequencyId =
                    billingFrequencyIdService.updateBillingFrequencyId(warehouseId, billFrequencyId, companyCodeId, languageId, plantId, loginUserID, updateBillingFrequencyId);
            return new ResponseEntity<>(createdBillingFrequencyId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = BillingFrequencyId.class, value = "Delete BillingFrequencyId") // label for swagger
    @DeleteMapping("/{billFrequencyId}")
    public ResponseEntity<?> deleteBillingFrequencyId(@PathVariable Long billFrequencyId,
                                                      @RequestParam String warehouseId, @RequestParam String companyCodeId,
                                                      @RequestParam String languageId, @RequestParam String plantId, @RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            billingFrequencyIdService.deleteBillingFrequencyId(warehouseId, billFrequencyId, companyCodeId, languageId, plantId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Search
    @ApiOperation(response = BillingFrequencyId.class, value = "Find BillingFrequencyId") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findBillingFrequencyId(@Valid @RequestBody FindBillingFrequencyId findBillingFrequencyId) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(findBillingFrequencyId.getCompanyCodeId(), findBillingFrequencyId.getPlantId(), findBillingFrequencyId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<BillingFrequencyId> createdBillingFrequencyId = billingFrequencyIdService.findBillingFrequencyId(findBillingFrequencyId);
            return new ResponseEntity<>(createdBillingFrequencyId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}
