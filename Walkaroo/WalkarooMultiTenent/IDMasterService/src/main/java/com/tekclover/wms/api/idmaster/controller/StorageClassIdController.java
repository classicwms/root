package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.storageclassid.AddStorageClassId;
import com.tekclover.wms.api.idmaster.model.storageclassid.FindStorageClassId;
import com.tekclover.wms.api.idmaster.model.storageclassid.StorageClassId;
import com.tekclover.wms.api.idmaster.model.storageclassid.UpdateStorageClassId;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.StorageClassIdService;
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
@Api(tags = {"StorageClassId"}, value = "StorageClassId  Operations related to StorageClassIdController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StorageClassId ", description = "Operations related to StorageClassId ")})
@RequestMapping("/storageclassid")
@RestController
public class StorageClassIdController {
    @Autowired
    private CompanyIdRepository companyIdRepository;

    @Autowired
    StorageClassIdService storageclassidService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = StorageClassId.class, value = "Get all StorageClassId details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<StorageClassId> storageclassidList = storageclassidService.getStorageClassIds();
        return new ResponseEntity<>(storageclassidList, HttpStatus.OK);
    }

    @ApiOperation(response = StorageClassId.class, value = "Get a StorageClassId") // label for swagger 
    @GetMapping("/{storageClassId}")
    public ResponseEntity<?> getStorageClassId(@RequestParam String warehouseId, @PathVariable Long storageClassId, @RequestParam String companyCodeId,
                                               @RequestParam String languageId, @RequestParam String plantId) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageClassId storageclassid =
                    storageclassidService.getStorageClassId(warehouseId, storageClassId, companyCodeId, languageId, plantId);
            log.info("StorageClassId : " + storageclassid);
            return new ResponseEntity<>(storageclassid, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

//	@ApiOperation(response = StorageClassId.class, value = "Search StorageClassId") // label for swagger
//	@PostMapping("/findStorageClassId")
//	public List<StorageClassId> findStorageClassId(@RequestBody SearchStorageClassId searchStorageClassId)
//			throws Exception {
//		return storageclassidService.findStorageClassId(searchStorageClassId);
//	}

    @ApiOperation(response = StorageClassId.class, value = "Create StorageClassId") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postStorageClassId(@Valid @RequestBody AddStorageClassId newStorageClassId,
                                                @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(newStorageClassId.getCompanyCodeId(), newStorageClassId.getPlantId(), newStorageClassId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageClassId createdStorageClassId = storageclassidService.createStorageClassId(newStorageClassId, loginUserID);
            return new ResponseEntity<>(createdStorageClassId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageClassId.class, value = "Update StorageClassId") // label for swagger
    @PatchMapping("/{storageClassId}")
    public ResponseEntity<?> patchStorageClassId(@RequestParam String warehouseId, @PathVariable Long storageClassId,
                                                 @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId,
                                                 @Valid @RequestBody UpdateStorageClassId updateStorageClassId, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageClassId createdStorageClassId =
                    storageclassidService.updateStorageClassId(warehouseId, storageClassId, companyCodeId, languageId, plantId, loginUserID, updateStorageClassId);
            return new ResponseEntity<>(createdStorageClassId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageClassId.class, value = "Delete StorageClassId") // label for swagger
    @DeleteMapping("/{storageClassId}")
    public ResponseEntity<?> deleteStorageClassId(@RequestParam String warehouseId, @PathVariable Long storageClassId,
                                                  @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId, @RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            storageclassidService.deleteStorageClassId(warehouseId, storageClassId, companyCodeId, languageId, plantId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Search
    @ApiOperation(response = StorageClassId.class, value = "Find StorageClassId") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findStorageClassId(@Valid @RequestBody FindStorageClassId findStorageClassId) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(findStorageClassId.getCompanyCodeId(), findStorageClassId.getPlantId(), findStorageClassId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<StorageClassId> createdStorageClassId = storageclassidService.findStorageClassId(findStorageClassId);
            return new ResponseEntity<>(createdStorageClassId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}