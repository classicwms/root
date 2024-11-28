package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.model.imalternateuom.AddImAlternateUom;
import com.tekclover.wms.api.masters.model.imalternateuom.ImAlternateUom;
import com.tekclover.wms.api.masters.model.imalternateuom.SearchImAlternateUom;
import com.tekclover.wms.api.masters.model.imalternateuom.UpdateImAlternateUom;
import com.tekclover.wms.api.masters.service.ImAlternateUomService;
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
import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"ImAlternateUom"}, value = "ImAlternateUom  Operations related to ImAlternateUomController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ImAlternateUom ", description = "Operations related to ImAlternateUom ")})
@RequestMapping("/imalternateuom")
@RestController
public class ImAlternateUomController {

    @Autowired
    ImAlternateUomService imalternateuomService;

    @ApiOperation(response = ImAlternateUom.class, value = "Get all ImAlternateUom details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<ImAlternateUom> imalternateuomList = imalternateuomService.getImAlternateUoms();
        return new ResponseEntity<>(imalternateuomList, HttpStatus.OK);
    }

    @ApiOperation(response = ImAlternateUom.class, value = "Get a ImAlternateUom") // label for swagger 
    @GetMapping("/{uomId}")
    public ResponseEntity<?> getImAlternateUom(@PathVariable String uomId, @RequestParam String companyCodeId,
                                               @RequestParam String plantId, @RequestParam String warehouseId,
                                               @RequestParam String itemCode, @RequestParam String alternateUom, @RequestParam String languageId) {
        List<ImAlternateUom> imalternateuom = imalternateuomService.getImAlternateUom(alternateUom, companyCodeId, plantId, warehouseId, itemCode, uomId, languageId);
        log.info("ImAlternateUom : " + imalternateuom);
        return new ResponseEntity<>(imalternateuom, HttpStatus.OK);
    }

    @ApiOperation(response = ImAlternateUom.class, value = "Create ImAlternateUom") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postImAlternateUom(@Valid @RequestBody List<AddImAlternateUom> newImAlternateUom, @RequestParam String loginUserID) {
        List<ImAlternateUom> createdImAlternateUom = imalternateuomService.createImAlternateUom(newImAlternateUom, loginUserID);
        return new ResponseEntity<>(createdImAlternateUom, HttpStatus.OK);
    }

    @ApiOperation(response = ImAlternateUom.class, value = "Update ImAlternateUom") // label for swagger
    @PatchMapping("/{uomId}")
    public ResponseEntity<?> patchImAlternateUom(@PathVariable String uomId, @RequestParam String companyCodeId,
                                                 @RequestParam String plantId, @RequestParam String warehouseId,
                                                 @RequestParam String itemCode, @RequestParam String alternateUom,
                                                 @RequestParam String languageId, @RequestParam String loginUserID,
                                                 @Valid @RequestBody List<UpdateImAlternateUom> updateImAlternateUom) {

        List<ImAlternateUom> createdImAlternateUom = imalternateuomService.updateImAlternateUom(alternateUom, companyCodeId, plantId,
                warehouseId, itemCode, uomId, languageId, updateImAlternateUom, loginUserID);
        return new ResponseEntity<>(createdImAlternateUom, HttpStatus.OK);
    }

    @ApiOperation(response = ImAlternateUom.class, value = "Delete ImAlternateUom") // label for swagger
    @DeleteMapping("/{uomId}")
    public ResponseEntity<?> deleteImAlternateUom(@PathVariable String uomId, @RequestParam String companyCodeId,
                                                  @RequestParam String plantId, @RequestParam String warehouseId,
                                                  @RequestParam String itemCode, @RequestParam String alternateUom,
                                                  @RequestParam String languageId, @RequestParam String loginUserID) throws ParseException {
        imalternateuomService.deleteImAlternateUom(alternateUom, companyCodeId, plantId, warehouseId, itemCode, uomId, languageId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(response = ImAlternateUom.class, value = "Find ImAlternateUom") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findImAlternateUom(@Valid @RequestBody SearchImAlternateUom findImAlternateUom) {
        List<ImAlternateUom> createdImAlternateUom = imalternateuomService.findImAlternateUom(findImAlternateUom);
        return new ResponseEntity<>(createdImAlternateUom, HttpStatus.OK);
    }
}