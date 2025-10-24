package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.dto.LikeSearchInput;
import com.tekclover.wms.api.masters.model.imbasicdata1.AddImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.ImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.SearchImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.UpdateImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.v2.ImBasicData;
import com.tekclover.wms.api.masters.model.imbasicdata1.v2.ImBasicData1V2;
import com.tekclover.wms.api.masters.model.impl.ItemListImpl;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.ImBasicData1Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"ImBasicData1"}, value = "ImBasicData1 Operations related to ImBasicData1Controller") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ImBasicData1 ", description = "Operations related to ImBasicData1")})
@RequestMapping("/imbasicdata1")
@RestController
public class ImBasicData1Controller {

    @Autowired
    ImBasicData1Service imbasicdata1Service;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = ImBasicData1.class, value = "Get all ImBasicData1 details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        Iterable<ImBasicData1> imbasicdata1List = imbasicdata1Service.getImBasicData1s();
        return new ResponseEntity<>(imbasicdata1List, HttpStatus.OK);
    }

    @ApiOperation(response = ImBasicData1.class, value = "Get a ImBasicData1") // label for swagger 
    @GetMapping("/{itemCode}")
    public ResponseEntity<?> getImBasicData1ByItemCode(@PathVariable String itemCode, @RequestParam String companyCodeId,
                                                       @RequestParam String plantId, @RequestParam String languageId, @RequestParam String uomId,
                                                       @RequestParam String manufacturerPartNo, @RequestParam String warehouseId) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            ImBasicData1 imbasicdata1 = imbasicdata1Service.getImBasicData1(itemCode, warehouseId, companyCodeId, plantId, uomId, manufacturerPartNo, languageId);
            log.info("ImBasicData1 : " + imbasicdata1);
            return new ResponseEntity<>(imbasicdata1, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImBasicData1V2.class, value = "Get a ImBasicData1 V2") // label for swagger
    @GetMapping("/v2/{itemCode}")
    public ResponseEntity<?> getImBasicData1ByItemCodeV2(@PathVariable String itemCode, @RequestParam String companyCodeId,
                                                         @RequestParam String plantId, @RequestParam String languageId, @RequestParam String uomId,
                                                         @RequestParam String manufacturerPartNo, @RequestParam String warehouseId) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            ImBasicData1V2 imbasicdata1 = imbasicdata1Service.getaImBasicData1V2(itemCode, warehouseId, companyCodeId, plantId, uomId, manufacturerPartNo, languageId);
            log.info("ImBasicData1 : " + imbasicdata1);
            return new ResponseEntity<>(imbasicdata1, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImBasicData1.class, value = "Get a ImBasicData1 V2") // label for swagger
    @PostMapping("/v2/itemMaster")
    public ResponseEntity<?> getImBasicData1ByItemCode(@RequestBody ImBasicData imBasicData) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(imBasicData.getCompanyCodeId(), imBasicData.getPlantId(), imBasicData.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            ImBasicData1V2 imbasicdata1 = imbasicdata1Service.getImBasicData1V2(imBasicData);
            log.info("ImBasicData1 : " + imbasicdata1);
            return new ResponseEntity<>(imbasicdata1, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImBasicData1.class, value = "Search ImBasicData1") // label for swagger
    @PostMapping("/findImBasicData1/pagination")
    public Page<ImBasicData1> findImBasicData1(@RequestBody SearchImBasicData1 searchImBasicData1,
                                               @RequestParam(defaultValue = "0") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                               @RequestParam(defaultValue = "itemCode") String sortBy)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbNameList(searchImBasicData1.getCompanyCodeId(), searchImBasicData1.getPlantId(), searchImBasicData1.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return imbasicdata1Service.findImBasicData1(searchImBasicData1, pageNo, pageSize, sortBy);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImBasicData1.class, value = "Search ImBasicData1") // label for swagger
    @PostMapping("/findImBasicData1")
    public List<ImBasicData1> findImBasicData1(@RequestBody SearchImBasicData1 searchImBasicData1)
            throws Exception {
        log.info("SearchImBasicData1 ----> {}", searchImBasicData1);
        try {
            if (searchImBasicData1.getCompanyCodeId() != null && searchImBasicData1.getPlantId() != null && searchImBasicData1.getWarehouseId() != null) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbList(searchImBasicData1.getCompanyCodeId(), searchImBasicData1.getPlantId(), searchImBasicData1.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            } else {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbByWarehouseIn(searchImBasicData1.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            return imbasicdata1Service.findImBasicData1(searchImBasicData1);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Streaming Changed to List for Fahaheel MT
    @ApiOperation(response = ImBasicData1.class, value = "Search ImBasicData1 Stream") // label for swagger
    @PostMapping("/findImBasicData1Stream")
    public List<ImBasicData1> findImBasicData1StreamToList(@RequestBody SearchImBasicData1 searchImBasicData1)
            throws Exception {

        try {
            log.info("SearchImBasicData1 -----> {}", searchImBasicData1);
            DataBaseContextHolder.setCurrentDb("FAHAHEEL");
            //String routingDb = dbConfigRepository.getDbNameList(searchImBasicData1.getCompanyCodeId(), searchImBasicData1.getPlantId(), searchImBasicData1.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", DataBaseContextHolder.getCurrentDb());
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb(routingDb);
            return imbasicdata1Service.findImBasicData1StreamToList(searchImBasicData1);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Streaming
    @ApiOperation(response = ImBasicData1V2.class, value = "Search ImBasicData1 Stream") // label for swagger
    @PostMapping("/v2/findImBasicData1Stream")
    public Stream<ImBasicData1V2> findImBasicData1StreamV2(@RequestBody SearchImBasicData1 searchImBasicData1)
            throws Exception {

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbNameList(searchImBasicData1.getCompanyCodeId(), searchImBasicData1.getPlantId(), searchImBasicData1.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return imbasicdata1Service.findImBasicData1V2Stream(searchImBasicData1);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImBasicData1.class, value = "Like Search ImBasicData1") // label for swagger
    @GetMapping("/findItemCodeByLike")
    public List<ItemListImpl> getImBasicData1LikeSearch(@RequestParam String likeSearchByItemCodeNDesc)
            throws Exception {

        return imbasicdata1Service.findImBasicData1LikeSearch(likeSearchByItemCodeNDesc);
    }

    //Like Search filter ItemCode, Description, Company Code, Plant, Language and warehouse
    @ApiOperation(response = ImBasicData1.class, value = "Like Search ImBasicData1 New") // label for swagger
    @GetMapping("/findItemCodeByLikeNew")
    public List<ItemListImpl> getImBasicData1LikeSearchNew(@RequestParam String likeSearchByItemCodeNDesc,
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
            return imbasicdata1Service.findImBasicData1LikeSearchNew(likeSearchByItemCodeNDesc, companyCodeId, plantId, languageId, warehouseId);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Like Search filter ItemCode, Description, Company Code, Plant, Language and warehouse
    @ApiOperation(response = ImBasicData1.class, value = "Like Search ImBasicData1 New V2") // label for swagger
    @PostMapping("/v2/findItemCodeByLikeNew")
    public List<ItemListImpl> getImBasicData1LikeSearchNewV2(@Valid @RequestBody LikeSearchInput likeSearchInput)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(likeSearchInput.getCompanyCodeId(), likeSearchInput.getPlantId(), likeSearchInput.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
//            return imbasicdata1Service.findImBasicData1LikeSearchV2(likeSearchInput);

            List<ItemListImpl> itemLists = new ArrayList<>();

            if (routingDb != null) {
                switch (routingDb) {
                    case "FAHAHEEL":
                    case "AUTO_LAP":
                        itemLists = imbasicdata1Service.findImBasicData1LikeSearchV3(likeSearchInput);
                        break;
                    default:
                        itemLists = imbasicdata1Service.findImBasicData1LikeSearchV2(likeSearchInput);
                }
            }
            return itemLists;
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImBasicData1.class, value = "Create ImBasicData1") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postImBasicData1(@Valid @RequestBody AddImBasicData1 newImBasicData1, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newImBasicData1.getCompanyCodeId(), newImBasicData1.getPlantId(), newImBasicData1.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            ImBasicData1 createdImBasicData1 = imbasicdata1Service.createImBasicData1(newImBasicData1, loginUserID);
            return new ResponseEntity<>(createdImBasicData1, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImBasicData1V2.class, value = "Create ImBasicData1 V2") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postImBasicData1V2(@Valid @RequestBody ImBasicData1V2 newImBasicData1, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            ImBasicData1V2 createdImBasicData1 = null;
            String routingDb = dbConfigRepository.getDbName(newImBasicData1.getCompanyCodeId(), newImBasicData1.getPlantId(), newImBasicData1.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            if (routingDb != null) {
                switch (routingDb) {
                    case "REEFERON":
                        createdImBasicData1 = imbasicdata1Service.createImBasicData1V5(newImBasicData1, loginUserID);
                        break;
                    case "KNOWELL":
                        createdImBasicData1 = imbasicdata1Service.createImBasicData1V7(newImBasicData1, loginUserID);
                        break;
                    default:
                        createdImBasicData1 = imbasicdata1Service.createImBasicData1V2(newImBasicData1, loginUserID);
                        break;
                }

            }
            return new ResponseEntity<>(createdImBasicData1, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImBasicData1.class, value = "Update ImBasicData1") // label for swagger
    @PatchMapping("/{itemCode}")
    public ResponseEntity<?> patchImBasicData1(@PathVariable String itemCode, @RequestParam String warehouseId, @RequestParam String manufacturerPartNo,
                                               @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId,
                                               @RequestParam String uomId, @Valid @RequestBody UpdateImBasicData1 updateImBasicData1, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            ImBasicData1 createdImBasicData1 = imbasicdata1Service.updateImBasicData1(itemCode, companyCodeId, plantId, languageId, uomId,
                    warehouseId, manufacturerPartNo, updateImBasicData1, loginUserID);
            return new ResponseEntity<>(createdImBasicData1, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImBasicData1.class, value = "Update ImBasicData1 v2") // label for swagger
    @PatchMapping("/v2/{itemCode}")
    public ResponseEntity<?> patchImBasicData1V2(@PathVariable String itemCode, @RequestParam String warehouseId, @RequestParam String manufacturerPartNo,
                                                 @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId,
                                                 @RequestParam String uomId, @Valid @RequestBody ImBasicData1V2 updateImBasicData1, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            ImBasicData1 createdImBasicData1 = imbasicdata1Service.updateImBasicData1V2(itemCode, companyCodeId, plantId, languageId, uomId,
                    warehouseId, manufacturerPartNo, updateImBasicData1, loginUserID);
            return new ResponseEntity<>(createdImBasicData1, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImBasicData1.class, value = "Delete ImBasicData1") // label for swagger
    @DeleteMapping("/{itemCode}")
    public ResponseEntity<?> deleteImBasicData1(@PathVariable String itemCode, @RequestParam String warehouseId, @RequestParam String companyCodeId,
                                                @RequestParam String languageId, @RequestParam String plantId, @RequestParam String uomId,
                                                @RequestParam String manufacturerPartNo, @RequestParam String loginUserID) throws ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            imbasicdata1Service.deleteImBasicData1(itemCode, companyCodeId, plantId, languageId, uomId,
                    manufacturerPartNo, warehouseId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImBasicData1V2.class, value = "Delete ImBasicData1 V2") // label for swagger
    @DeleteMapping("/v2/{itemCode}")
    public ResponseEntity<?> deleteImBasicData1v2(@PathVariable String itemCode, @RequestParam String warehouseId, @RequestParam String companyCodeId,
                                                  @RequestParam String languageId, @RequestParam String plantId, @RequestParam String uomId,
                                                  @RequestParam String manufacturerPartNo, @RequestParam String loginUserID) throws ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            imbasicdata1Service.deleteImBasicData1V2(itemCode, companyCodeId, plantId, languageId, uomId,
                    manufacturerPartNo, warehouseId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImBasicData1V2.class, value = "Upload ImBasicData") // label for swagger
    @PostMapping("/upload/imbasicdata/")
    public ResponseEntity<?> uploadStorageBin(@Valid @RequestBody List<ImBasicData1V2> imBasicDataList){
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(imBasicDataList.get(0).getCompanyCodeId(), imBasicDataList.get(0).getPlantId(), imBasicDataList.get(0).getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            log.info("imBasicData" + imBasicDataList);
            List<ImBasicData1V2> imBasicData = imbasicdata1Service.imBasicData1Upload(imBasicDataList);
            return new ResponseEntity<>(imBasicData, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}