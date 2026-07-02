package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.itemgroupid.AddItemGroupId;
import com.tekclover.wms.api.idmaster.model.itemgroupid.FindItemGroupId;
import com.tekclover.wms.api.idmaster.model.itemgroupid.ItemGroupId;
import com.tekclover.wms.api.idmaster.model.itemgroupid.UpdateItemGroupId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.ItemGroupIdService;
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
@Api(tags = {"ItemGroupId"}, value = "ItemGroupId  Operations related to ItemGroupIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ItemGroupId ", description = "Operations related to ItemGroupId ")})
@RequestMapping("/itemgroupid")
@RestController
public class ItemGroupIdController {

    @Autowired
    ItemGroupIdService itemgroupidService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = ItemGroupId.class, value = "Get all ItemGroupId details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<ItemGroupId> itemgroupidList = itemgroupidService.getItemGroupIds();
        return new ResponseEntity<>(itemgroupidList, HttpStatus.OK);
    }

    @ApiOperation(response = ItemGroupId.class, value = "Get a ItemGroupId") // label for swagger 
    @GetMapping("/{itemGroupId}")
    public ResponseEntity<?> getItemGroupId(@PathVariable Long itemGroupId,
                                            @RequestParam String warehouseId, @RequestParam Long itemTypeId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            ItemGroupId itemgroupid =
                    itemgroupidService.getItemGroupId(warehouseId, itemTypeId, itemGroupId, companyCodeId, plantId, languageId);
            log.info("ItemGroupId : " + itemgroupid);
            return new ResponseEntity<>(itemgroupid, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

//	@ApiOperation(response = ItemGroupId.class, value = "Search ItemGroupId") // label for swagger
//	@PostMapping("/findItemGroupId")
//	public List<ItemGroupId> findItemGroupId(@RequestBody SearchItemGroupId searchItemGroupId)
//			throws Exception {
//		return itemgroupidService.findItemGroupId(searchItemGroupId);
//	}

    @ApiOperation(response = ItemGroupId.class, value = "Create ItemGroupId") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postItemGroupId(@Valid @RequestBody AddItemGroupId newItemGroupId,
                                             @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(newItemGroupId.getCompanyCodeId(), newItemGroupId.getPlantId(), newItemGroupId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            ItemGroupId createdItemGroupId = itemgroupidService.createItemGroupId(newItemGroupId, loginUserID);
            return new ResponseEntity<>(createdItemGroupId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ItemGroupId.class, value = "Update ItemGroupId") // label for swagger
    @PatchMapping("/{itemGroupId}")
    public ResponseEntity<?> patchItemGroupId(@PathVariable Long itemGroupId,
                                              @RequestParam String warehouseId, @RequestParam Long itemTypeId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                              @Valid @RequestBody UpdateItemGroupId updateItemGroupId, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            ItemGroupId createdItemGroupId =
                    itemgroupidService.updateItemGroupId(warehouseId, itemTypeId, itemGroupId, companyCodeId, plantId, languageId, loginUserID, updateItemGroupId);
            return new ResponseEntity<>(createdItemGroupId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ItemGroupId.class, value = "Delete ItemGroupId") // label for swagger
    @DeleteMapping("/{itemGroupId}")
    public ResponseEntity<?> deleteItemGroupId(@PathVariable Long itemGroupId,
                                               @RequestParam String warehouseId, @RequestParam Long itemTypeId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId, @RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            itemgroupidService.deleteItemGroupId(warehouseId, itemTypeId, itemGroupId, companyCodeId, plantId, languageId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Search
    @ApiOperation(response = ItemGroupId.class, value = "Find ItemGroupId") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findItemGroupId(@Valid @RequestBody FindItemGroupId findItemGroupId) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(findItemGroupId.getCompanyCodeId(), findItemGroupId.getPlantId(), findItemGroupId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<ItemGroupId> createdItemGroupId = itemgroupidService.findItemGroupId(findItemGroupId);
            return new ResponseEntity<>(createdItemGroupId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}