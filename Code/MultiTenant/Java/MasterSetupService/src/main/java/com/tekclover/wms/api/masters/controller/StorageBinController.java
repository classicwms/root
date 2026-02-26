package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.dto.LikeSearchInput;
import com.tekclover.wms.api.masters.model.impl.StorageBinListImpl;
import com.tekclover.wms.api.masters.model.masters.Warehouse;
import com.tekclover.wms.api.masters.model.storagebin.*;
import com.tekclover.wms.api.masters.model.storagebin.v2.StorageBinV2;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.repository.WarehouseRepository;
import com.tekclover.wms.api.masters.service.StorageBinService;
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
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"StorageBin"}, value = "StorageBin  Operations related to StorageBinController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StorageBin ", description = "Operations related to StorageBin ")})
@RequestMapping("/storagebin")
@RestController
public class StorageBinController {

    @Autowired
    StorageBinService storagebinService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @ApiOperation(response = StorageBin.class, value = "Get all StorageBin details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<StorageBin> storagebinList = storagebinService.getStorageBins();
        return new ResponseEntity<>(storagebinList, HttpStatus.OK);
    }

    @ApiOperation(response = StorageBin.class, value = "Get a StorageBin") // label for swagger 
    @GetMapping("/{storageBin}")
    public ResponseEntity<?> getStorageBin(@PathVariable String storageBin, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String languageId) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBin storagebin = storagebinService.getStorageBin(storageBin, companyCodeId, plantId, warehouseId, languageId);
            log.info("StorageBin : " + storagebin);
            return new ResponseEntity<>(storagebin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBin.class, value = "Get a StorageBin") // label for swagger 
    @GetMapping("/{storageBin}/warehouseId")
    public ResponseEntity<?> getStorageBin(@PathVariable String storageBin, @RequestParam String warehouseId) {

        StorageBin storagebin = storagebinService.getStorageBin(warehouseId, storageBin);
        log.info("StorageBin : " + storagebin);
        return new ResponseEntity<>(storagebin, HttpStatus.OK);
    }

    @ApiOperation(response = StorageBin.class, value = "Get a StorageBin") // label for swagger 
    @GetMapping("/{warehouseId}/status")
    public ResponseEntity<?> getStorageBinByStatus(@PathVariable String warehouseId, @RequestParam Long statusId) {
        List<StorageBin> storagebin = storagebinService.getStorageBinByStatus(warehouseId, statusId);
        log.info("StorageBin : " + storagebin);
        return new ResponseEntity<>(storagebin, HttpStatus.OK);
    }

    @ApiOperation(response = StorageBin.class, value = "Get a StorageBin Status") // label for swagger 
    @GetMapping("/{warehouseId}/status-not-equal")
    public ResponseEntity<?> getStorageBinByStatusNotEqual(@PathVariable String warehouseId,
                                                           @RequestParam Long statusId) {
        List<StorageBin> storagebin = storagebinService.getStorageBinByStatusNotEqual(warehouseId, statusId);
        log.info("StorageBin : " + storagebin);
        return new ResponseEntity<>(storagebin, HttpStatus.OK);
    }

    @ApiOperation(response = StorageBin.class, value = "Like Search StorageBin") // label for swagger
    @GetMapping("/findStorageBinByLike")
    public List<StorageBinListImpl> getStorageBinLikeSearch(@RequestParam String likeSearchByStorageBinNDesc)
            throws Exception {
        return storagebinService.findStorageBinLikeSearch(likeSearchByStorageBinNDesc);
    }

    //Like Search filter ItemCode, Description, Company Code, Plant, Language and warehouse
    @ApiOperation(response = StorageBin.class, value = "Like Search StorageBin New") // label for swagger
    @GetMapping("/findStorageBinByLikeNew")
    public List<StorageBinListImpl> getStorageBinLikeSearchNew(@RequestParam String likeSearchByStorageBinNDesc,
                                                               @RequestParam String companyCodeId,
                                                               @RequestParam String plantId,
                                                               @RequestParam String languageId,
                                                               @RequestParam String warehouseId)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return storagebinService.findStorageBinLikeSearchNew(likeSearchByStorageBinNDesc, companyCodeId, plantId, languageId, warehouseId);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Like Search filter ItemCode, Description, Company Code, Plant, Language and warehouse
    @ApiOperation(response = StorageBin.class, value = "Like Search StorageBin New") // label for swagger
    @PostMapping("/v2/findStorageBinByLikeNew")
    public List<StorageBinListImpl> getStorageBinLikeSearchV2(@Valid @RequestBody LikeSearchInput likeSearchInput)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(likeSearchInput.getCompanyCodeId(), likeSearchInput.getPlantId(), likeSearchInput.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return storagebinService.findStorageBinLikeSearchV2(likeSearchInput);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBin.class, value = "Get a StorageBin") // label for swagger
    @PostMapping("/putaway")
    public ResponseEntity<?> getStorageBin(@RequestBody StorageBinPutAway storageBinPutAway) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(storageBinPutAway.getCompanyCodeId(), storageBinPutAway.getPlantId(), storageBinPutAway.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<StorageBin> storagebin = storagebinService.getStorageBin(storageBinPutAway);
            log.info("StorageBin : " + storagebin);
            return new ResponseEntity<>(storagebin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBin.class, value = "Get a StorageBin") // label for swagger
    @GetMapping("/sectionId")
    public ResponseEntity<?> getStorageBinByStorageSectionId(@RequestParam String warehouseId, @RequestParam List<String> stSectionIds) {

        List<StorageBin> storagebin = storagebinService.getStorageBin(warehouseId, stSectionIds);
        log.info("StorageBin : " + storagebin);
        return new ResponseEntity<>(storagebin, HttpStatus.OK);
    }

    @ApiOperation(response = StorageBin.class, value = "Get a StorageBin") // label for swagger 
    @GetMapping("/{warehouseId}/bins/{binClassId}")
    public ResponseEntity<?> getStorageBin(@PathVariable String warehouseId, @PathVariable Long binClassId) {
        StorageBin storagebin = storagebinService.getStorageBin(warehouseId, binClassId);
        log.info("StorageBin : " + storagebin);
        return new ResponseEntity<>(storagebin, HttpStatus.OK);
    }

    @ApiOperation(response = StorageBin.class, value = "Search StorageBin") // label for swagger
    @PostMapping("/findStorageBin")
    public List<StorageBin> findStorageBin(@RequestBody SearchStorageBin searchStorageBin)
            throws Exception {
        try {
            log.info("SearchStorageBin ------> {}", searchStorageBin);
            String routingDb = null;
            DataBaseContextHolder.setCurrentDb("MT");
            Warehouse warehouseName = warehouseRepository.findTop1ByWarehouseIdAndDeletionIndicator(searchStorageBin.getWarehouseId().get(0), 0L);
            routingDb = dbConfigRepository.getDbName(warehouseName.getCompanyCodeId(), warehouseName.getPlantId(), warehouseName.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return storagebinService.findStorageBin(searchStorageBin);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Streaming
    @ApiOperation(response = StorageBin.class, value = "Search StorageBin") // label for swagger
    @PostMapping("/findStorageBinStream")
    public Stream<StorageBin> findStorageBinStream(@RequestBody SearchStorageBin searchStorageBin)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbNameList(searchStorageBin.getCompanyCodeId(), searchStorageBin.getPlantId(), searchStorageBin.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return storagebinService.findStorageBinStream(searchStorageBin);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBin.class, value = "Create StorageBin") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postStorageBin(@Valid @RequestBody AddStorageBin newStorageBin, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newStorageBin.getCompanyCodeId(), newStorageBin.getPlantId(), newStorageBin.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBin createdStorageBin = storagebinService.createStorageBin(newStorageBin, loginUserID);
            return new ResponseEntity<>(createdStorageBin, HttpStatus.OK);
        } finally {

            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBin.class, value = "Update StorageBin") // label for swagger
    @PatchMapping("/{storageBin}")
    public ResponseEntity<?> patchStorageBin(@PathVariable String storageBin, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String languageId,
                                             @Valid @RequestBody UpdateStorageBin updateStorageBin, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBin createdStorageBin = storagebinService.updateStorageBin(storageBin, companyCodeId, plantId, warehouseId, languageId, updateStorageBin, loginUserID);
            return new ResponseEntity<>(createdStorageBin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //--------------------------------------------------------V2------------------------------------------------
    @ApiOperation(response = StorageBinV2.class, value = "Get a StorageBin V2") // label for swagger
    @GetMapping("/v2/{storageBin}/warehouseId")
    public ResponseEntity<?> getStorageBinV2(@PathVariable String storageBin, @RequestParam String warehouseId) {
        StorageBinV2 storagebin = storagebinService.getStorageBinV2(warehouseId, storageBin);
        log.info("StorageBin : " + storagebin);
        return new ResponseEntity<>(storagebin, HttpStatus.OK);
    }

    @ApiOperation(response = StorageBinV2.class, value = "Get a StorageBin V2") // label for swagger
    @GetMapping("/{storageBin}/warehouseId/v2")
    public ResponseEntity<?> getStorageBinV2(@PathVariable String storageBin, @RequestParam String warehouseId,
                                             @RequestParam String companyCode, @RequestParam String plantId,
                                             @RequestParam String languageId) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinV2 storagebin = storagebinService.getStorageBinV2(storageBin, companyCode, plantId, warehouseId, languageId);
            log.info("StorageBin : " + storagebin);
            return new ResponseEntity<>(storagebin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    @ApiOperation(response = StorageBinV2.class, value = "Get a StorageBin V2") // label for swagger
    @GetMapping("/{warehouseId}/status/v2")
    public ResponseEntity<?> getStorageBinByStatusV2(@PathVariable String warehouseId, @RequestParam Long statusId,
                                                     @RequestParam String companyCode, @RequestParam String plantId,
                                                     @RequestParam String languageId) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<StorageBinV2> storagebin = storagebinService.getStorageBinByStatusV2(companyCode, plantId, languageId, warehouseId, statusId);
            log.info("StorageBin : " + storagebin);
            return new ResponseEntity<>(storagebin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBin.class, value = "Get a StorageBin") // label for swagger
    @GetMapping("/v2/{warehouseId}/bins/{binClassId}")
    public ResponseEntity<?> getStorageBinV2(@PathVariable String warehouseId, @PathVariable Long binClassId,
                                             @RequestParam String companyCode, @RequestParam String plantId,
                                             @RequestParam String languageId) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBin storageBin = null;
            if (routingDb != null) {
                switch (routingDb) {
                    case "AUTO_LAP":
                    case "FAHAHEEL":
                        storageBin = storagebinService.getStorageBinByBinClassIdV2(warehouseId, binClassId, companyCode, plantId, languageId);
                        break;
                    case "REEFERON":
                        storageBin = storagebinService.getStorageBinByBinClassIdV5(warehouseId, binClassId, companyCode, plantId, languageId);
                        break;
                    case "NAMRATHA":
                        storageBin = storagebinService.getStorageBinByBinClassIdV2(warehouseId, binClassId, companyCode, plantId, languageId);
                        break;
                    case "BF":
                        storageBin = storagebinService.getStorageBinByBinClassIdV9(warehouseId, binClassId, companyCode, plantId, languageId);
                        break;
                    case "KKF":
                        storageBin = storagebinService.getStorageBinByBinClassIdV9(warehouseId, binClassId, companyCode, plantId, languageId);
                        break;
                }
            }
            log.info("StorageBin : " + storageBin);
            return new ResponseEntity<>(storageBin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBinV2.class, value = "Get a StorageBin V2") // label for swagger
    @PostMapping("/putaway/v2")
    public ResponseEntity<?> getStorageBinV2(@RequestBody StorageBinPutAway storageBinPutAway) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(storageBinPutAway.getCompanyCodeId(), storageBinPutAway.getPlantId(), storageBinPutAway.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<StorageBinV2> storagebin = storagebinService.getStorageBinV2(storageBinPutAway);
            log.info("StorageBin : " + storagebin);
            return new ResponseEntity<>(storagebin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBinV2.class, value = "Get a StorageBin V2") // label for swagger
    @PostMapping("/bin/v2")
    public ResponseEntity<?> getaStorageBinV2(@RequestBody StorageBinPutAway storageBinPutAway) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(storageBinPutAway.getCompanyCodeId(), storageBinPutAway.getPlantId(), storageBinPutAway.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinV2 storagebin = storagebinService.getaStorageBinV2(storageBinPutAway);
            log.info("StorageBin : " + storagebin);
            return new ResponseEntity<>(storagebin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBinV2.class, value = "Get a CBM StorageBin V2") // label for swagger
    @PostMapping("/putaway/cbm/v2")
    public ResponseEntity<?> getStorageBinCBMV2(@RequestBody StorageBinPutAway storageBinPutAway) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(storageBinPutAway.getCompanyCodeId(), storageBinPutAway.getPlantId(), storageBinPutAway.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinV2 storagebin = storagebinService.getProposedStorageBinCBM(storageBinPutAway);
            log.info("StorageBin : " + storagebin);
            return new ResponseEntity<>(storagebin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBinV2.class, value = "Get a CBM Per Qty StorageBin V2") // label for swagger
    @PostMapping("/putaway/cbmPerQty/v2")
    public ResponseEntity<?> getStorageBinCbmPerQtyV2(@RequestBody StorageBinPutAway storageBinPutAway) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(storageBinPutAway.getCompanyCodeId(), storageBinPutAway.getPlantId(), storageBinPutAway.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinV2 storagebin = storagebinService.getProposedStorageBinCbmPerQty(storageBinPutAway);
            log.info("StorageBin : " + storagebin);
            return new ResponseEntity<>(storagebin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBinV2.class, value = "Get a NON - CBM StorageBin V2") // label for swagger
    @PostMapping("/putaway/nonCbm/v2")
    public ResponseEntity<?> getStorageBinNonCbm(@RequestBody StorageBinPutAway storageBinPutAway) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(storageBinPutAway.getCompanyCodeId(), storageBinPutAway.getPlantId(), storageBinPutAway.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinV2 storagebin = storagebinService.getProposedStorageBinNonCBM(storageBinPutAway);
            log.info("StorageBin : " + storagebin);
            return new ResponseEntity<>(storagebin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBinV2.class, value = "Get a NON - CBM StorageBin V2") // label for swagger
    @PostMapping("/putaway/nonCbm/existing/v2")
    public ResponseEntity<?> getExistingStorageBinNonCbm(@RequestBody StorageBinPutAway storageBinPutAway) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(storageBinPutAway.getCompanyCodeId(), storageBinPutAway.getPlantId(), storageBinPutAway.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinV2 storagebin = storagebinService.getExistingProposedStorageBinNonCBM(storageBinPutAway);
            log.info("StorageBin : " + storagebin);
            return new ResponseEntity<>(storagebin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBinV2.class, value = "Get a NON - CBM Last Picked StorageBin V2")
    // label for swagger
    @PostMapping("/putaway/nonCbm/lastPicked/v2")
    public ResponseEntity<?> getStorageBinNonCbmLastPicked(@RequestBody StorageBinPutAway storageBinPutAway) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(storageBinPutAway.getCompanyCodeId(), storageBinPutAway.getPlantId(), storageBinPutAway.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinV2 storagebin = storagebinService.getProposedStorageBinNonCBMLastPicked(storageBinPutAway);
            log.info("StorageBin : " + storagebin);
            return new ResponseEntity<>(storagebin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBinV2.class, value = "Get a CBM Last Picked StorageBin V2")    // label for swagger
    @PostMapping("/putaway/cbm/lastPicked/v2")
    public ResponseEntity<?> getProposedStorageBinCBMLastPicked(@RequestBody StorageBinPutAway storageBinPutAway) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(storageBinPutAway.getCompanyCodeId(), storageBinPutAway.getPlantId(), storageBinPutAway.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinV2 storagebin = storagebinService.getProposedStorageBinCBMLastPicked(storageBinPutAway);
            log.info("StorageBin : " + storagebin);
            return new ResponseEntity<>(storagebin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBinV2.class, value = "Get a CBMPerQty Last Picked StorageBin V2")
    // label for swagger
    @PostMapping("/putaway/cbmPerQty/lastPicked/v2")
    public ResponseEntity<?> getProposedStorageBinCBMPerQtyLastPicked(@RequestBody StorageBinPutAway storageBinPutAway) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(storageBinPutAway.getCompanyCodeId(), storageBinPutAway.getPlantId(), storageBinPutAway.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinV2 storagebin = storagebinService.getProposedStorageBinCBMPerQtyLastPicked(storageBinPutAway);
            log.info("StorageBin : " + storagebin);
            return new ResponseEntity<>(storagebin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBinV2.class, value = "Get a BinClass 7 StorageBin V2") // label for swagger
    @PostMapping("/putaway/binClass/v2")
    public ResponseEntity<?> getStorageBinBinClassId7(@RequestBody StorageBinPutAway storageBinPutAway) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(storageBinPutAway.getCompanyCodeId(), storageBinPutAway.getPlantId(), storageBinPutAway.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinV2 storagebin = storagebinService.getStorageBinBinClassId7(storageBinPutAway);
            log.info("StorageBin : " + storagebin);
            return new ResponseEntity<>(storagebin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    @ApiOperation(response = StorageBinV2.class, value = "Get a BinClass 1 StorageBin V2") // label for swagger
    @PostMapping("/putaway/binClassId/v2")
    public ResponseEntity<?> getStorageBinBinClassId1(@RequestBody StorageBinPutAway storageBinPutAway) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(storageBinPutAway.getCompanyCodeId(), storageBinPutAway.getPlantId(), storageBinPutAway.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinV2 storagebin = storagebinService.getStorageBinBinClassId1(storageBinPutAway);
            log.info("StorageBin : " + storagebin);
            return new ResponseEntity<>(storagebin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBinV2.class, value = "Create StorageBin V2") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postStorageBinV2(@Valid @RequestBody StorageBinV2 newStorageBin, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newStorageBin.getCompanyCodeId(), newStorageBin.getPlantId(), newStorageBin.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinV2 createdStorageBin = storagebinService.createStorageBinV2(newStorageBin, loginUserID);
            return new ResponseEntity<>(createdStorageBin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBinV2.class, value = "Update StorageBin V2") // label for swagger
    @PatchMapping("/v2/{storageBin}")
    public ResponseEntity<?> patchStorageBinV2(@PathVariable String storageBin, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String languageId,
                                               @Valid @RequestBody StorageBinV2 updateStorageBin, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinV2 createdStorageBin = storagebinService.updateStorageBinV2(storageBin, companyCodeId,
                    plantId, warehouseId, languageId, updateStorageBin, loginUserID);
            return new ResponseEntity<>(createdStorageBin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBin.class, value = "Delete StorageBin") // label for swagger
    @DeleteMapping("/{storageBin}")
    public ResponseEntity<?> deleteStorageBin(@PathVariable String storageBin, @RequestParam String loginUserID, @RequestParam String warehouseId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId) throws ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            storagebinService.deleteStorageBin(storageBin, companyCodeId, plantId, warehouseId, languageId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    // GET STORAGE BIN V2
    @ApiOperation(response = StorageBinV2.class, value = "Get a StorageBin V2") // label for swagger
    @GetMapping("/v2/{storageBin}")
    public ResponseEntity<?> getStoreBinV2(@PathVariable String storageBin, @RequestParam String warehouseId,
                                           @RequestParam String companyCodeId, @RequestParam String plantId,
                                           @RequestParam String languageId) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinV2 storagebin = storagebinService.getStoreBinV2(storageBin, companyCodeId, plantId, warehouseId, languageId);
            log.info("StorageBin : " + storagebin);
            return new ResponseEntity<>(storagebin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    // UPDATE STORAGE BIN V2
    @ApiOperation(response = StorageBinV2.class, value = "Update StorageBin V2") // label for swagger
    @PatchMapping("/storageBinV2/{storageBin}")
    public ResponseEntity<?> patchStoreBin(@PathVariable String storageBin, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String languageId,
                                           @Valid @RequestBody StorageBinV2 storageBinV2, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinV2 createdStorageBin = storagebinService.updateStoreBinV2(storageBin, companyCodeId, plantId, warehouseId, languageId, storageBinV2, loginUserID);
            return new ResponseEntity<>(createdStorageBin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    // DELETE STORAGE BIN V2
    @ApiOperation(response = StorageBinV2.class, value = "Delete StorageBinV2") // label for swagger
    @DeleteMapping("/v2/{storageBin}")
    public ResponseEntity<?> deleteStoreBin(@PathVariable String storageBin, @RequestParam String loginUserID,
                                            @RequestParam String warehouseId, @RequestParam String companyCodeId,
                                            @RequestParam String plantId, @RequestParam String languageId) throws ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            storagebinService.deleteStoreBinV2(storageBin, companyCodeId, plantId, warehouseId, languageId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBinV2.class, value = "Upload StorageBin") // label for swagger
    @PostMapping("/upload/storagebin")
    public ResponseEntity<?> uploadStorageBin(@Valid @RequestBody List<StorageBinV2> storageBinV2){
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(storageBinV2.get(0).getCompanyCodeId(), storageBinV2.get(0).getPlantId(), storageBinV2.get(0).getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<StorageBinV2> storageBin = storagebinService.storageBinUpload(storageBinV2);
            return new ResponseEntity<>(storageBin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

}