package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.threepl.billingformatid.AddBillingFormatId;
import com.tekclover.wms.api.idmaster.model.threepl.billingformatid.BillingFormatId;
import com.tekclover.wms.api.idmaster.model.threepl.billingformatid.FindBillingFormatId;
import com.tekclover.wms.api.idmaster.model.threepl.billingformatid.UpdateBillingFormatId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.BillingFormatIdService;
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
@Api(tags={"BillingFormatId"},value = "BillingFormatId Operations related to BillingFormatIdController")
@SwaggerDefinition(tags={@Tag(name="BillingFormatId",description = "Operations related to BillingFormatId")})
@RequestMapping("/billingformatid")
@RestController
public class BillingFormatIdController {
    @Autowired
    BillingFormatIdService billingFormatIdService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = BillingFormatId.class, value = "Get all BillingFormatId details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<BillingFormatId> BillingFormatIdList = billingFormatIdService.getBillingFormatIds();
        return new ResponseEntity<>(BillingFormatIdList, HttpStatus.OK);
    }
    @ApiOperation(response = BillingFormatId.class, value = "Get a BillingFormatId") // label for swagger
    @GetMapping("/{billFormatId}")
    public ResponseEntity<?> getBillingFormatId(@RequestParam String warehouseId, @PathVariable Long billFormatId,@RequestParam String companyCodeId,
                                                @RequestParam String languageId,String plantId) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        BillingFormatId BillingFormatId =
                billingFormatIdService.getBillingFormatId(warehouseId,billFormatId,companyCodeId,languageId,plantId);
        log.info("BillingFormatId : " + BillingFormatId);
        return new ResponseEntity<>(BillingFormatId, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
    @ApiOperation(response = BillingFormatId.class, value = "Create BillingFormatId") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postBillingFormatId(@Valid @RequestBody AddBillingFormatId newBillingFormatId,
                                               @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newBillingFormatId.getCompanyCodeId(), newBillingFormatId.getPlantId(), newBillingFormatId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        BillingFormatId createdBillingFormatId = billingFormatIdService.createBillingFormatId(newBillingFormatId, loginUserID);
        return new ResponseEntity<>(createdBillingFormatId , HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
    @ApiOperation(response = BillingFormatId.class, value = "Update BillingFormatId") // label for swagger
    @PatchMapping("/{billFormatId}")
    public ResponseEntity<?> patchBillingFormatId(@RequestParam String warehouseId, @PathVariable Long billFormatId,@RequestParam String companyCodeId,
                                                  @RequestParam String languageId,@RequestParam String plantId,
                                                  @Valid @RequestBody UpdateBillingFormatId updateBillingFormatId, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        BillingFormatId createdBillingFormatId =
                billingFormatIdService.updateBillingFormatId(warehouseId, billFormatId,companyCodeId,languageId,plantId,loginUserID, updateBillingFormatId);
        return new ResponseEntity<>(createdBillingFormatId , HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
    @ApiOperation(response = BillingFormatId.class, value = "Delete BillingFormatId") // label for swagger
    @DeleteMapping("/{billFormatId}")
    public ResponseEntity<?> deleteBillingFormatId(@PathVariable Long billFormatId,
                                                 @RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId, @RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        billingFormatIdService.deleteBillingFormatId(warehouseId,billFormatId,companyCodeId,languageId,plantId,loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
    //Search
    @ApiOperation(response = BillingFormatId.class, value = "Find BillingFormatId") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findBillingFormatId(@Valid @RequestBody FindBillingFormatId findBillingFormatId) throws Exception {

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findBillingFormatId.getCompanyCodeId(), findBillingFormatId.getPlantId(), findBillingFormatId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        List<BillingFormatId> createdBillingFormatId = billingFormatIdService.findBillingFormatId(findBillingFormatId);
        return new ResponseEntity<>(createdBillingFormatId, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
}
