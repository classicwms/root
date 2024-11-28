package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.model.imstrategies.AddImStrategies;
import com.tekclover.wms.api.masters.model.imstrategies.ImStrategies;
import com.tekclover.wms.api.masters.model.imstrategies.SearchImStrategies;
import com.tekclover.wms.api.masters.model.imstrategies.UpdateImStrategies;
import com.tekclover.wms.api.masters.service.ImStrategiesService;
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
@Api(tags = {"ImStrategies"}, value = "ImStrategies  Operations related to ImStrategiesController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ImStrategies ", description = "Operations related to ImStrategies ")})
@RequestMapping("/imstrategies")
@RestController
public class ImStrategiesController {

    @Autowired
    ImStrategiesService imstrategiesService;

    @ApiOperation(response = ImStrategies.class, value = "Get all ImStrategies details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<ImStrategies> imstrategiesList = imstrategiesService.getALlImStrategyTypeId();
        return new ResponseEntity<>(imstrategiesList, HttpStatus.OK);
    }

    @ApiOperation(response = ImStrategies.class, value = "Get a ImStrategies") // label for swagger 
    @GetMapping("/{strategyTypeId}")
    public ResponseEntity<?> getImStrategies(@PathVariable Long strategyTypeId, @RequestParam String companyCodeId,
                                             @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String itemCode,
                                             @RequestParam Long sequenceIndicator, @RequestParam String languageId) {

        ImStrategies imstrategies =
                imstrategiesService.getImStrategies(strategyTypeId, companyCodeId, plantId, warehouseId, itemCode, sequenceIndicator, languageId);
        return new ResponseEntity<>(imstrategies, HttpStatus.OK);
    }

    @ApiOperation(response = ImStrategies.class, value = "Search ImStrategies") // label for swagger
    @PostMapping("/findImStrategies")
    public List<ImStrategies> findImStrategies(@RequestBody SearchImStrategies searchImStrategies) {
        return imstrategiesService.findImStrategies(searchImStrategies);
    }

    @ApiOperation(response = ImStrategies.class, value = "Create ImStrategies") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postImStrategies(@Valid @RequestBody AddImStrategies newImStrategies, @RequestParam String loginUserID) {
        ImStrategies createdImStrategies = imstrategiesService.createImStrategies(newImStrategies, loginUserID);
        return new ResponseEntity<>(createdImStrategies, HttpStatus.OK);
    }

    @ApiOperation(response = ImStrategies.class, value = "Update ImStrategies") // label for swagger
    @PatchMapping("/{strategyTypeId}")
    public ResponseEntity<?> patchImStrategies(@PathVariable Long strategyTypeId, @RequestParam String companyCodeId,
                                               @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String itemCode,
                                               @RequestParam Long sequenceIndicator, @RequestParam String languageId,
                                               @Valid @RequestBody UpdateImStrategies updateImStrategies, @RequestParam String loginUserID) {

        ImStrategies createdImStrategies =
                imstrategiesService.updateImStrategies(strategyTypeId, companyCodeId, plantId, warehouseId, itemCode,
                        sequenceIndicator, languageId, updateImStrategies, loginUserID);
        return new ResponseEntity<>(createdImStrategies, HttpStatus.OK);
    }

    @ApiOperation(response = ImStrategies.class, value = "Delete ImStrategies") // label for swagger
    @DeleteMapping("/{strategyTypeId}")
    public ResponseEntity<?> deleteImStrategies(@PathVariable Long strategyTypeId, @RequestParam String companyCodeId,
                                                @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String itemCode,
                                                @RequestParam Long sequenceIndicator, @RequestParam String languageId, @RequestParam String loginUserID) {

        imstrategiesService.deleteImStrategies(strategyTypeId, companyCodeId, plantId, warehouseId, itemCode, sequenceIndicator, languageId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}