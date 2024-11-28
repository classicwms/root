package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.model.packingmaterial.AddPackingMaterial;
import com.tekclover.wms.api.masters.model.packingmaterial.PackingMaterial;
import com.tekclover.wms.api.masters.model.packingmaterial.SearchPackingMaterial;
import com.tekclover.wms.api.masters.model.packingmaterial.UpdatePackingMaterial;
import com.tekclover.wms.api.masters.service.PackingMaterialService;
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
@Api(tags = {"PackingMaterial"}, value = "PackingMaterial  Operations related to PackingMaterialController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PackingMaterial ", description = "Operations related to PackingMaterial ")})
@RequestMapping("/packingmaterial")
@RestController
public class PackingMaterialController {

    @Autowired
    PackingMaterialService packingmaterialService;

    @ApiOperation(response = PackingMaterial.class, value = "Get all PackingMaterial details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<PackingMaterial> packingmaterialList = packingmaterialService.getPackingMaterials();
        return new ResponseEntity<>(packingmaterialList, HttpStatus.OK);
    }

    @ApiOperation(response = PackingMaterial.class, value = "Get a PackingMaterial") // label for swagger 
    @GetMapping("/{packingMaterialNo}")
    public ResponseEntity<?> getPackingMaterial(@PathVariable String packingMaterialNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                @RequestParam String warehouseId, @RequestParam String languageId) {
        PackingMaterial packingmaterial = packingmaterialService.getPackingMaterial(packingMaterialNo, companyCodeId, plantId, languageId, warehouseId);
        log.info("PackingMaterial : " + packingmaterial);
        return new ResponseEntity<>(packingmaterial, HttpStatus.OK);
    }

    @ApiOperation(response = PackingMaterial.class, value = "Search PackingMaterial") // label for swagger
    @PostMapping("/findPackingMaterial")
    public List<PackingMaterial> findPackingMaterial(@RequestBody SearchPackingMaterial searchPackingMaterial) {
        return packingmaterialService.findPackingMaterial(searchPackingMaterial);
    }

    @ApiOperation(response = PackingMaterial.class, value = "Create PackingMaterial") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPackingMaterial(@Valid @RequestBody AddPackingMaterial newPackingMaterial, @RequestParam String loginUserID) {
        PackingMaterial createdPackingMaterial = packingmaterialService.createPackingMaterial(newPackingMaterial, loginUserID);
        return new ResponseEntity<>(createdPackingMaterial, HttpStatus.OK);
    }

    @ApiOperation(response = PackingMaterial.class, value = "Update PackingMaterial") // label for swagger
    @PatchMapping("/{packingMaterialNo}")
    public ResponseEntity<?> patchPackingMaterial(@PathVariable String packingMaterialNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                  @RequestParam String warehouseId, @RequestParam String languageId,
                                                  @Valid @RequestBody UpdatePackingMaterial updatePackingMaterial, @RequestParam String loginUserID) {
        PackingMaterial createdPackingMaterial = packingmaterialService.updatePackingMaterial(packingMaterialNo, companyCodeId, plantId, warehouseId, languageId, updatePackingMaterial, loginUserID);
        return new ResponseEntity<>(createdPackingMaterial, HttpStatus.OK);
    }

    @ApiOperation(response = PackingMaterial.class, value = "Delete PackingMaterial") // label for swagger
    @DeleteMapping("/{packingMaterialNo}")
    public ResponseEntity<?> deletePackingMaterial(@PathVariable String packingMaterialNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                   @RequestParam String warehouseId, @RequestParam String languageId, @RequestParam String loginUserID) {
        packingmaterialService.deletePackingMaterial(packingMaterialNo, companyCodeId, plantId, warehouseId, languageId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}