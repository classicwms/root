package com.tekclover.wms.api.enterprise.controller;

import com.tekclover.wms.api.enterprise.model.itemgroup.AddItemGroup;
import com.tekclover.wms.api.enterprise.model.itemgroup.ItemGroup;
import com.tekclover.wms.api.enterprise.model.itemgroup.SearchItemGroup;
import com.tekclover.wms.api.enterprise.service.ItemGroupService;
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
@Api(tags = {"ItemGroup "}, value = "ItemGroup  Operations related to ItemGroupController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ItemGroup ", description = "Operations related to ItemGroup ")})
@RequestMapping("/itemgroup")
@RestController
public class ItemGroupController {

    @Autowired
    ItemGroupService itemgroupService;

    @ApiOperation(response = ItemGroup.class, value = "Get all ItemGroup details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<ItemGroup> itemgroupList = itemgroupService.getItemGroups();
        return new ResponseEntity<>(itemgroupList, HttpStatus.OK);
    }

    @ApiOperation(response = ItemGroup.class, value = "Get a ItemGroup")
    @GetMapping("/{itemTypeId}")
    public ResponseEntity<?> getItemGroup(@PathVariable Long itemTypeId, @RequestParam String companyId,
                                          @RequestParam String languageId, @RequestParam String plantId,
                                          @RequestParam String warehouseId) {

        List<ItemGroup> itemgroup = itemgroupService.getItemGroup(companyId, languageId, plantId, warehouseId, itemTypeId);
        log.info("ItemGroup : " + itemgroup);
        return new ResponseEntity<>(itemgroup, HttpStatus.OK);
    }

    @ApiOperation(response = ItemGroup.class, value = "Search ItemGroup") // label for swagger
    @PostMapping("/findItemGroup")
    public List<ItemGroup> findItemGroup(@RequestBody SearchItemGroup searchItemGroup) {
        return itemgroupService.findItemGroup(searchItemGroup);
    }

    @ApiOperation(response = ItemGroup.class, value = "Create ItemGroup") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postItemGroup(@Valid @RequestBody List<AddItemGroup> newItemGroup, @RequestParam String loginUserID) {
        List<ItemGroup> createdItemGroup = itemgroupService.createItemGroup(newItemGroup, loginUserID);
        return new ResponseEntity<>(createdItemGroup, HttpStatus.OK);
    }

    @ApiOperation(response = ItemGroup.class, value = "Update ItemGroup") // label for swagger
    @PatchMapping("/{itemTypeId}")
    public ResponseEntity<?> patchItemGroup(@PathVariable Long itemTypeId, @RequestParam String companyId,
                                            @RequestParam String languageId, @RequestParam String plantId,
                                            @RequestParam String warehouseId, @Valid @RequestBody List<AddItemGroup> updateItemGroup,
                                            @RequestParam String loginUserID) {

        List<ItemGroup> createdItemGroup = itemgroupService.updateItemGroup(companyId, languageId, plantId, warehouseId,
                itemTypeId, updateItemGroup, loginUserID);
        return new ResponseEntity<>(createdItemGroup, HttpStatus.OK);
    }

    @ApiOperation(response = ItemGroup.class, value = "Delete ItemGroup") // label for swagger
    @DeleteMapping("/{itemTypeId}")
    public ResponseEntity<?> deleteItemGroup(@PathVariable Long itemTypeId, @RequestParam String companyId,
                                             @RequestParam String languageId, @RequestParam String plantId,
                                             @RequestParam String warehouseId, @RequestParam String loginUserID) {

        itemgroupService.deleteItemGroup(companyId, languageId, plantId, warehouseId, itemTypeId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}