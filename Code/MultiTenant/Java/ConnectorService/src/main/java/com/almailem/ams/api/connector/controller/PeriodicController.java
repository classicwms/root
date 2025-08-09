package com.almailem.ams.api.connector.controller;

import com.almailem.ams.api.connector.model.periodic.FindPeriodicHeader;
import com.almailem.ams.api.connector.model.periodic.FindPeriodicLine;
import com.almailem.ams.api.connector.model.periodic.PeriodicHeader;
import com.almailem.ams.api.connector.model.periodic.PeriodicLine;
import com.almailem.ams.api.connector.model.wms.UpdateStockCountLine;
import com.almailem.ams.api.connector.model.wms.WarehouseApiResponse;
import com.almailem.ams.api.connector.service.PeriodicService;
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

import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"Periodic"}, value = "Periodic Operations related to PeriodicController")
@SwaggerDefinition(tags = {@Tag(name = "Periodic", description = "Operations related to Periodic")})
@RequestMapping("/periodic")
@RestController
public class PeriodicController {

    @Autowired
    PeriodicService periodicService;

    //Get All StockCount Periodic Details
    @ApiOperation(response = PeriodicHeader.class, value = "Get All Periodic Details")
    @GetMapping("")
    public ResponseEntity<?> getAllPeriodic() {
        List<PeriodicHeader> periodic = periodicService.getAllPeriodicDetails();
        return new ResponseEntity<>(periodic, HttpStatus.OK);
    }

    @ApiOperation(response = WarehouseApiResponse.class, value = "Update Periodic Line Counted Qty")
    @PatchMapping("/updateCountedQty")
    public ResponseEntity<?> patchPeriodicLine(@RequestBody List<UpdateStockCountLine> updateStockCountLine) {
        WarehouseApiResponse perpetuals = periodicService.updatePeriodicStockCount(updateStockCountLine);
        return new ResponseEntity<>(perpetuals, HttpStatus.OK);
    }

    // Find PeriodicHeader
    @ApiOperation(response = PeriodicHeader.class, value = "Find PeriodicHeader") // label for Swagger
    @PostMapping("/findPeriodicHeader")
    public ResponseEntity<?> findPeriodicHeader(@RequestBody FindPeriodicHeader findPeriodicHeader) throws ParseException {
        List<PeriodicHeader> periodicHeaders = periodicService.findPeriodicHeader(findPeriodicHeader);
        return new ResponseEntity<>(periodicHeaders, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicLine.class, value = "Find PeriodicLine") // label for Swagger
    @PostMapping("/findPeriodicLine")
    public ResponseEntity<?> findPeriodicLine(@RequestBody FindPeriodicLine findPeriodicLine) throws ParseException {
        List<PeriodicLine> periodicLines = periodicService.findPeriodicLine(findPeriodicLine);
        return new ResponseEntity<>(periodicLines, HttpStatus.OK);
    }

}
