package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.barcodesubtypeid.AddBarcodeSubTypeId;
import com.tekclover.wms.api.idmaster.model.barcodesubtypeid.BarcodeSubTypeId;
import com.tekclover.wms.api.idmaster.model.barcodesubtypeid.FindBarcodeSubTypeId;
import com.tekclover.wms.api.idmaster.model.barcodesubtypeid.UpdateBarcodeSubTypeId;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.BarcodeSubTypeIdService;
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
@Api(tags = {"BarcodeSubTypeId"}, value = "BarcodeSubTypeId  Operations related to BarcodeSubTypeIdController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "BarcodeSubTypeId ", description = "Operations related to BarcodeSubTypeId ")})
@RequestMapping("/barcodesubtypeid")
@RestController
public class BarcodeSubTypeIdController {
    @Autowired
    private CompanyIdRepository companyIdRepository;

    @Autowired
    BarcodeSubTypeIdService barcodesubtypeidService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = BarcodeSubTypeId.class, value = "Get all BarcodeSubTypeId details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<BarcodeSubTypeId> barcodesubtypeidList = barcodesubtypeidService.getBarcodeSubTypeIds();
        return new ResponseEntity<>(barcodesubtypeidList, HttpStatus.OK);
    }

    @ApiOperation(response = BarcodeSubTypeId.class, value = "Get a BarcodeSubTypeId") // label for swagger 
    @GetMapping("/{barcodeSubTypeId}")
    public ResponseEntity<?> getBarcodeSubTypeId(@RequestParam String warehouseId, @RequestParam Long barcodeTypeId, @PathVariable Long barcodeSubTypeId,
                                                 @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            BarcodeSubTypeId barcodesubtypeid =
                    barcodesubtypeidService.getBarcodeSubTypeId(warehouseId, barcodeTypeId, barcodeSubTypeId, companyCodeId, languageId, plantId);
            log.info("BarcodeSubTypeId : " + barcodesubtypeid);
            return new ResponseEntity<>(barcodesubtypeid, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

//	@ApiOperation(response = BarcodeSubTypeId.class, value = "Search BarcodeSubTypeId") // label for swagger
//	@PostMapping("/findBarcodeSubTypeId")
//	public List<BarcodeSubTypeId> findBarcodeSubTypeId(@RequestBody SearchBarcodeSubTypeId searchBarcodeSubTypeId)
//			throws Exception {
//		return barcodesubtypeidService.findBarcodeSubTypeId(searchBarcodeSubTypeId);
//	}

    @ApiOperation(response = BarcodeSubTypeId.class, value = "Create BarcodeSubTypeId") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postBarcodeSubTypeId(@Valid @RequestBody AddBarcodeSubTypeId newBarcodeSubTypeId,
                                                  @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(newBarcodeSubTypeId.getCompanyCodeId(), newBarcodeSubTypeId.getPlantId(), newBarcodeSubTypeId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            BarcodeSubTypeId createdBarcodeSubTypeId = barcodesubtypeidService.createBarcodeSubTypeId(newBarcodeSubTypeId, loginUserID);
            return new ResponseEntity<>(createdBarcodeSubTypeId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = BarcodeSubTypeId.class, value = "Update BarcodeSubTypeId") // label for swagger
    @PatchMapping("/{barcodeSubTypeId}")
    public ResponseEntity<?> patchBarcodeSubTypeId(@RequestParam String warehouseId, @RequestParam Long barcodeTypeId, @PathVariable Long barcodeSubTypeId,
                                                   @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId,
                                                   @Valid @RequestBody UpdateBarcodeSubTypeId updateBarcodeSubTypeId, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            BarcodeSubTypeId createdBarcodeSubTypeId =
                    barcodesubtypeidService.updateBarcodeSubTypeId(warehouseId, barcodeTypeId, barcodeSubTypeId, companyCodeId, languageId, plantId, loginUserID, updateBarcodeSubTypeId);
            return new ResponseEntity<>(createdBarcodeSubTypeId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = BarcodeSubTypeId.class, value = "Delete BarcodeSubTypeId") // label for swagger
    @DeleteMapping("/{barcodeSubTypeId}")
    public ResponseEntity<?> deleteBarcodeSubTypeId(@RequestParam String warehouseId, @RequestParam Long barcodeTypeId, @PathVariable Long barcodeSubTypeId, @RequestParam String companyCodeId,
                                                    @RequestParam String languageId, @RequestParam String plantId, @RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            barcodesubtypeidService.deleteBarcodeSubTypeId(warehouseId, barcodeTypeId, barcodeSubTypeId, companyCodeId, languageId, plantId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Search
    @ApiOperation(response = BarcodeSubTypeId.class, value = "Find BarcodeSubTypeId") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findBarcodeSubTypeId(@Valid @RequestBody FindBarcodeSubTypeId findBarcodeSubTypeId) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(findBarcodeSubTypeId.getCompanyCodeId(), findBarcodeSubTypeId.getPlantId(), findBarcodeSubTypeId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<BarcodeSubTypeId> createdBarcodeSubTypeId = barcodesubtypeidService.findBarcodeSubTypeId(findBarcodeSubTypeId);
            return new ResponseEntity<>(createdBarcodeSubTypeId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}