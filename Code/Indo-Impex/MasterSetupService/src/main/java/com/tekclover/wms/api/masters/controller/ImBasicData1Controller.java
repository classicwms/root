package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.model.dto.ImBasicData1V2Impl;
import com.tekclover.wms.api.masters.model.dto.LikeSearchInput;
import com.tekclover.wms.api.masters.model.imbasicdata1.AddImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.ImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.SearchImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.UpdateImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.v2.ImBasicData;
import com.tekclover.wms.api.masters.model.imbasicdata1.v2.ImBasicData1V2;
import com.tekclover.wms.api.masters.model.impl.ItemListImpl;
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
        ImBasicData1 imbasicdata1 = imbasicdata1Service.getImBasicData1(itemCode, warehouseId, companyCodeId, plantId, uomId, manufacturerPartNo, languageId);
        log.info("ImBasicData1 : " + imbasicdata1);
        return new ResponseEntity<>(imbasicdata1, HttpStatus.OK);
    }

    @ApiOperation(response = ImBasicData1V2.class, value = "Get a ImBasicData1 V2") // label for swagger
    @GetMapping("/v2/{itemCode}")
    public ResponseEntity<?> getImBasicData1ByItemCodeV2(@PathVariable String itemCode, @RequestParam String companyCodeId,
                                                         @RequestParam String plantId, @RequestParam String languageId, @RequestParam String uomId,
                                                         @RequestParam String manufacturerPartNo, @RequestParam String warehouseId) {
        ImBasicData1V2 imbasicdata1 = imbasicdata1Service.getaImBasicData1V2(itemCode, warehouseId, companyCodeId, plantId, uomId, manufacturerPartNo, languageId);
        log.info("ImBasicData1 : " + imbasicdata1);
        return new ResponseEntity<>(imbasicdata1, HttpStatus.OK);
    }

    @ApiOperation(response = ImBasicData1.class, value = "Get a ImBasicData1 V2") // label for swagger
    @PostMapping("/v2/itemMaster")
    public ResponseEntity<?> getImBasicData1ByItemCode(@RequestBody ImBasicData imBasicData) {
        ImBasicData1V2 imbasicdata1 = imbasicdata1Service.getImBasicData1V2(imBasicData);
        log.info("ImBasicData1 : " + imbasicdata1);
        return new ResponseEntity<>(imbasicdata1, HttpStatus.OK);
    }

    @ApiOperation(response = ImBasicData1.class, value = "Search ImBasicData1") // label for swagger
    @PostMapping("/findImBasicData1/pagination")
    public Page<ImBasicData1> findImBasicData1(@RequestBody SearchImBasicData1 searchImBasicData1,
                                               @RequestParam(defaultValue = "0") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                               @RequestParam(defaultValue = "itemCode") String sortBy) {
        return imbasicdata1Service.findImBasicData1(searchImBasicData1, pageNo, pageSize, sortBy);
    }

    @ApiOperation(response = ImBasicData1.class, value = "Search ImBasicData1") // label for swagger
    @PostMapping("/findImBasicData1")
    public List<ImBasicData1> findImBasicData1(@RequestBody SearchImBasicData1 searchImBasicData1) {
        return imbasicdata1Service.findImBasicData1(searchImBasicData1);
    }

    //Streaming
    @ApiOperation(response = ImBasicData1.class, value = "Search ImBasicData1 Stream") // label for swagger
    @PostMapping("/findImBasicData1Stream")
    public Stream<ImBasicData1> findImBasicData1Stream(@RequestBody SearchImBasicData1 searchImBasicData1) {
        return imbasicdata1Service.findImBasicData1Stream(searchImBasicData1);
    }

    //IMF
    @ApiOperation(response = ImBasicData1V2Impl.class, value = "Search ImBasicData1 IMF") // label for swagger
    @PostMapping("/imf/findImBasicData1")
    public List<ImBasicData1V2Impl> findImfImBasicData1(@RequestBody SearchImBasicData1 searchImBasicData1) {
        return imbasicdata1Service.findImfImBasicData1(searchImBasicData1);
    }

    //Streaming
    @ApiOperation(response = ImBasicData1V2.class, value = "Search ImBasicData1 Stream") // label for swagger
    @PostMapping("/v2/findImBasicData1Stream")
    public Stream<ImBasicData1V2> findImBasicData1StreamV2(@RequestBody SearchImBasicData1 searchImBasicData1) {
        return imbasicdata1Service.findImBasicData1V2Stream(searchImBasicData1);
    }

    @ApiOperation(response = ImBasicData1.class, value = "Like Search ImBasicData1") // label for swagger
    @GetMapping("/findItemCodeByLike")
    public List<ItemListImpl> getImBasicData1LikeSearch(@RequestParam String likeSearchByItemCodeNDesc) {
        return imbasicdata1Service.findImBasicData1LikeSearch(likeSearchByItemCodeNDesc);
    }

    //Like Search filter ItemCode, Description, Company Code, Plant, Language and warehouse
    @ApiOperation(response = ImBasicData1.class, value = "Like Search ImBasicData1 New") // label for swagger
    @GetMapping("/findItemCodeByLikeNew")
    public List<ItemListImpl> getImBasicData1LikeSearchNew(@RequestParam String likeSearchByItemCodeNDesc,
                                                           @RequestParam String companyCodeId,
                                                           @RequestParam String plantId,
                                                           @RequestParam String languageId,
                                                           @RequestParam String warehouseId) {
        return imbasicdata1Service.findImBasicData1LikeSearchNew(likeSearchByItemCodeNDesc, companyCodeId, plantId, languageId, warehouseId);
    }

    //Like Search filter ItemCode, Description, Company Code, Plant, Language and warehouse
    @ApiOperation(response = ImBasicData1.class, value = "Like Search ImBasicData1 New V2") // label for swagger
    @PostMapping("/v2/findItemCodeByLikeNew")
    public List<ItemListImpl> getImBasicData1LikeSearchNewV2(@Valid @RequestBody LikeSearchInput likeSearchInput) {
        return imbasicdata1Service.findImBasicData1LikeSearchV2(likeSearchInput);
    }

    @ApiOperation(response = ImBasicData1.class, value = "Create ImBasicData1") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postImBasicData1(@Valid @RequestBody AddImBasicData1 newImBasicData1, @RequestParam String loginUserID) {
        ImBasicData1 createdImBasicData1 = imbasicdata1Service.createImBasicData1(newImBasicData1, loginUserID);
        return new ResponseEntity<>(createdImBasicData1, HttpStatus.OK);
    }

    @ApiOperation(response = ImBasicData1V2.class, value = "Create ImBasicData1 V2") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postImBasicData1V2(@Valid @RequestBody ImBasicData1V2 newImBasicData1, @RequestParam String loginUserID) {
        ImBasicData1V2 createdImBasicData1 = imbasicdata1Service.createImBasicData1V2(newImBasicData1, loginUserID);
        return new ResponseEntity<>(createdImBasicData1, HttpStatus.OK);
    }

    @ApiOperation(response = ImBasicData1.class, value = "Update ImBasicData1") // label for swagger
    @PatchMapping("/{itemCode}")
    public ResponseEntity<?> patchImBasicData1(@PathVariable String itemCode, @RequestParam String warehouseId, @RequestParam String manufacturerPartNo,
                                               @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId,
                                               @RequestParam String uomId, @Valid @RequestBody UpdateImBasicData1 updateImBasicData1, @RequestParam String loginUserID) {

        ImBasicData1 createdImBasicData1 = imbasicdata1Service.updateImBasicData1(itemCode, companyCodeId, plantId, languageId, uomId,
                warehouseId, manufacturerPartNo, updateImBasicData1, loginUserID);
        return new ResponseEntity<>(createdImBasicData1, HttpStatus.OK);
    }

    @ApiOperation(response = ImBasicData1.class, value = "Update ImBasicData1 v2") // label for swagger
    @PatchMapping("/v2/{itemCode}")
    public ResponseEntity<?> patchImBasicData1V2(@PathVariable String itemCode, @RequestParam String warehouseId, @RequestParam String manufacturerPartNo,
                                                 @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId,
                                                 @RequestParam String uomId, @Valid @RequestBody ImBasicData1V2 updateImBasicData1, @RequestParam String loginUserID) {

        ImBasicData1 createdImBasicData1 = imbasicdata1Service.updateImBasicData1V2(itemCode, companyCodeId, plantId, languageId, uomId,
                warehouseId, manufacturerPartNo, updateImBasicData1, loginUserID);
        return new ResponseEntity<>(createdImBasicData1, HttpStatus.OK);
    }

    @ApiOperation(response = ImBasicData1.class, value = "Delete ImBasicData1") // label for swagger
    @DeleteMapping("/{itemCode}")
    public ResponseEntity<?> deleteImBasicData1(@PathVariable String itemCode, @RequestParam String warehouseId, @RequestParam String companyCodeId,
                                                @RequestParam String languageId, @RequestParam String plantId, @RequestParam String uomId,
                                                @RequestParam String manufacturerPartNo, @RequestParam String loginUserID) {

        imbasicdata1Service.deleteImBasicData1(itemCode, companyCodeId, plantId, languageId, uomId,
                manufacturerPartNo, warehouseId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(response = ImBasicData1V2.class, value = "Delete ImBasicData1 V2") // label for swagger
    @DeleteMapping("/v2/{itemCode}")
    public ResponseEntity<?> deleteImBasicData1v2(@PathVariable String itemCode, @RequestParam String warehouseId, @RequestParam String companyCodeId,
                                                  @RequestParam String languageId, @RequestParam String plantId, @RequestParam String uomId,
                                                  @RequestParam String manufacturerPartNo, @RequestParam String loginUserID) {

        imbasicdata1Service.deleteImBasicData1V2(itemCode, companyCodeId, plantId, languageId, uomId,
                manufacturerPartNo, warehouseId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //=========================================Impex=================================================================
    @ApiOperation(response = ImBasicData1V2.class, value = "Update ImBasicData1 Description") // label for swagger
    @GetMapping("/updateDescriptionFields")
    public ResponseEntity<?> updateDescription() {
        imbasicdata1Service.updateImBasicDataDescription();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}