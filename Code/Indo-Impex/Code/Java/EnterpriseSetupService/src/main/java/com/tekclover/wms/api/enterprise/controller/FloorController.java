package com.tekclover.wms.api.enterprise.controller;

import com.tekclover.wms.api.enterprise.model.floor.AddFloor;
import com.tekclover.wms.api.enterprise.model.floor.Floor;
import com.tekclover.wms.api.enterprise.model.floor.SearchFloor;
import com.tekclover.wms.api.enterprise.model.floor.UpdateFloor;
import com.tekclover.wms.api.enterprise.service.FloorService;
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
@Api(tags = {"Floor "}, value = "Floor  Operations related to FloorController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Floor ", description = "Operations related to Floor ")})
@RequestMapping("/floor")
@RestController
public class FloorController {

    @Autowired
    FloorService floorService;

    @ApiOperation(response = Floor.class, value = "Get all Floor details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<Floor> floorList = floorService.getFloors();
        return new ResponseEntity<>(floorList, HttpStatus.OK);
    }

    @ApiOperation(response = Floor.class, value = "Get a Floor")
    @GetMapping("/{floorId}")
    public ResponseEntity<?> getFloor(@PathVariable Long floorId, @RequestParam String companyId, @RequestParam String plantId,
                                      @RequestParam String languageId, @RequestParam String warehouseId) {
        Floor floor = floorService.getFloor(warehouseId, companyId, plantId, languageId, floorId);
        log.info("Floor : " + floor);
        return new ResponseEntity<>(floor, HttpStatus.OK);
    }

    @ApiOperation(response = Floor.class, value = "Search Floor") // label for swagger
    @PostMapping("/findFloor")
    public List<Floor> findFloor(@RequestBody SearchFloor searchFloor)
            throws Exception {
        return floorService.findFloor(searchFloor);
    }

    @ApiOperation(response = Floor.class, value = "Create Floor") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postFloor(@Valid @RequestBody AddFloor newFloor, @RequestParam String loginUserID) {
        Floor createdFloor = floorService.createFloor(newFloor, loginUserID);
        return new ResponseEntity<>(createdFloor, HttpStatus.OK);
    }

    @ApiOperation(response = Floor.class, value = "Update Floor") // label for swagger
    @PatchMapping("/{floorId}")
    public ResponseEntity<?> patchFloor(@PathVariable Long floorId, @RequestParam String warehouseId, @RequestParam String companyId, @RequestParam String plantId,
                                        @RequestParam String languageId, @Valid @RequestBody UpdateFloor updateFloor, @RequestParam String loginUserID) {
        Floor createdFloor = floorService.updateFloor(warehouseId, companyId, plantId, languageId, floorId, updateFloor, loginUserID);
        return new ResponseEntity<>(createdFloor, HttpStatus.OK);
    }

    @ApiOperation(response = Floor.class, value = "Delete Floor") // label for swagger
    @DeleteMapping("/{floorId}")
    public ResponseEntity<?> deleteFloor(@PathVariable Long floorId, @RequestParam String companyId, @RequestParam String plantId,
                                         @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID) {
        floorService.deleteFloor(warehouseId, companyId, plantId, languageId, floorId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}