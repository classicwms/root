package com.tekclover.wms.api.enterprise.controller;

import com.tekclover.wms.api.enterprise.model.itemtype.AddItemType;
import com.tekclover.wms.api.enterprise.model.itemtype.ItemType;
import com.tekclover.wms.api.enterprise.model.itemtype.SearchItemType;
import com.tekclover.wms.api.enterprise.model.itemtype.UpdateItemType;
import com.tekclover.wms.api.enterprise.service.ItemTypeService;
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
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"ItemType "}, value = "ItemType  Operations related to ItemTypeController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ItemType ", description = "Operations related to ItemType ")})
@RequestMapping("/itemtype")
@RestController
public class ItemTypeController {

    @Autowired
    ItemTypeService itemtypeService;

    @ApiOperation(response = ItemType.class, value = "Get all ItemType details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<ItemType> itemtypeList = itemtypeService.getItemTypes();
        return new ResponseEntity<>(itemtypeList, HttpStatus.OK);
    }

    @ApiOperation(response = ItemType.class, value = "Get a ItemType")
    @GetMapping("/{itemTypeId}")
    public ResponseEntity<?> getItemType(@PathVariable Long itemTypeId, @RequestParam String warehouseId, @RequestParam String companyId,
                                         @RequestParam String plantId, @RequestParam String languageId) {
        ItemType itemtype = itemtypeService.getItemType(warehouseId, itemTypeId, companyId, languageId, plantId);
        log.info("ItemType : " + itemtype);
        return new ResponseEntity<>(itemtype, HttpStatus.OK);
    }

    @ApiOperation(response = ItemType.class, value = "Search ItemType") // label for swagger
    @PostMapping("/findItemType")
    public List<ItemType> findItemType(@RequestBody SearchItemType searchItemType) {
        return itemtypeService.findItemType(searchItemType);
    }

    @ApiOperation(response = ItemType.class, value = "Create ItemType") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postItemType(@Valid @RequestBody AddItemType newItemType, @RequestParam String loginUserID) {
        ItemType createdItemType = itemtypeService.createItemType(newItemType, loginUserID);
        return new ResponseEntity<>(createdItemType, HttpStatus.OK);
    }

    @ApiOperation(response = ItemType.class, value = "Update ItemType") // label for swagger
    @PatchMapping("/{itemTypeId}")
    public ResponseEntity<?> patchItemType(@PathVariable Long itemTypeId, @RequestParam String warehouseId, @RequestParam String companyId,
                                           @RequestParam String plantId, @RequestParam String languageId,
                                           @Valid @RequestBody UpdateItemType updateItemType, @RequestParam String loginUserID) {
        ItemType createdItemType = itemtypeService.updateItemType(warehouseId, itemTypeId, companyId, languageId, plantId, updateItemType, loginUserID);
        return new ResponseEntity<>(createdItemType, HttpStatus.OK);
    }

    @ApiOperation(response = ItemType.class, value = "Delete ItemType") // label for swagger
    @DeleteMapping("/{itemTypeId}")
    public ResponseEntity<?> deleteItemType(@PathVariable Long itemTypeId, @RequestParam String warehouseId, @RequestParam String companyId,
                                            @RequestParam String plantId, @RequestParam String languageId, @RequestParam String loginUserID) {
        itemtypeService.deleteItemType(warehouseId, itemTypeId, companyId, languageId, plantId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}