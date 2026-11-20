package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.storagetypeid.AddStorageTypeId;
import com.tekclover.wms.api.idmaster.model.storagetypeid.FindStorageTypeId;
import com.tekclover.wms.api.idmaster.model.storagetypeid.StorageTypeId;
import com.tekclover.wms.api.idmaster.model.storagetypeid.UpdateStorageTypeId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.repository.LanguageIdRepository;
import com.tekclover.wms.api.idmaster.service.StorageTypeIdService;
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
@Api(tags = {"StorageTypeId"}, value = "StorageTypeId  Operations related to StorageTypeIdController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StorageTypeId ", description = "Operations related to StorageTypeId ")})
@RequestMapping("/storagetypeid")
@RestController
public class StorageTypeIdController {
    @Autowired
    private LanguageIdRepository languageIdRepository;

    @Autowired
    StorageTypeIdService storagetypeidService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = StorageTypeId.class, value = "Get all StorageTypeId details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<StorageTypeId> storagetypeidList = storagetypeidService.getStorageTypeIds();
        return new ResponseEntity<>(storagetypeidList, HttpStatus.OK);
    }

    @ApiOperation(response = StorageTypeId.class, value = "Get a StorageTypeId") // label for swagger 
    @GetMapping("/{storageTypeId}")
    public ResponseEntity<?> getStorageTypeId(@RequestParam String warehouseId, @RequestParam Long storageClassId, @PathVariable Long storageTypeId,
                                              @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageTypeId storagetypeid =
                    storagetypeidService.getStorageTypeId(warehouseId, storageClassId, storageTypeId, companyCodeId, languageId, plantId);
            log.info("StorageTypeId : " + storagetypeid);
            return new ResponseEntity<>(storagetypeid, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();

        }
    }
//	@ApiOperation(response = StorageTypeId.class, value = "Search StorageTypeId") // label for swagger
//	@PostMapping("/findStorageTypeId")
//	public List<StorageTypeId> findStorageTypeId(@RequestBody SearchStorageTypeId searchStorageTypeId)
//			throws Exception {
//		return storagetypeidService.findStorageTypeId(searchStorageTypeId);
//	}

    @ApiOperation(response = StorageTypeId.class, value = "Create StorageTypeId") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postStorageTypeId(@Valid @RequestBody AddStorageTypeId newStorageTypeId,
                                               @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(newStorageTypeId.getCompanyCodeId(), newStorageTypeId.getPlantId(), newStorageTypeId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageTypeId createdStorageTypeId = storagetypeidService.createStorageTypeId(newStorageTypeId, loginUserID);
            return new ResponseEntity<>(createdStorageTypeId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageTypeId.class, value = "Update StorageTypeId") // label for swagger
    @PatchMapping("/{storageTypeId}")
    public ResponseEntity<?> patchStorageTypeId(@RequestParam String warehouseId, @RequestParam Long storageClassId, @PathVariable Long storageTypeId, @RequestParam String companyCodeId,
                                                @RequestParam String languageId, @RequestParam String plantId,
                                                @Valid @RequestBody UpdateStorageTypeId updateStorageTypeId, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageTypeId createdStorageTypeId =
                    storagetypeidService.updateStorageTypeId(warehouseId, storageClassId, storageTypeId, companyCodeId, languageId, plantId, loginUserID, updateStorageTypeId);
            return new ResponseEntity<>(createdStorageTypeId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageTypeId.class, value = "Delete StorageTypeId") // label for swagger
    @DeleteMapping("/{storageTypeId}")
    public ResponseEntity<?> deleteStorageTypeId(@RequestParam String warehouseId, @RequestParam Long storageClassId, @PathVariable Long storageTypeId, @RequestParam String companyCodeId,
                                                 @RequestParam String languageId, @RequestParam String plantId, @RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            storagetypeidService.deleteStorageTypeId(warehouseId, storageClassId, storageTypeId, companyCodeId, languageId, plantId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Search
    @ApiOperation(response = StorageTypeId.class, value = "Find StorageTypeId") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findStorageTypeId(@Valid @RequestBody FindStorageTypeId findStorageTypeId) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(findStorageTypeId.getCompanyCodeId(), findStorageTypeId.getPlantId(), findStorageTypeId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<StorageTypeId> createdStorageTypeId = storagetypeidService.findStorageTypeId(findStorageTypeId);
            return new ResponseEntity<>(createdStorageTypeId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}