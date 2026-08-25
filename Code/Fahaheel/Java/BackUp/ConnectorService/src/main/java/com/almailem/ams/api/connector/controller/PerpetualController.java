package com.almailem.ams.api.connector.controller;

import com.almailem.ams.api.connector.model.perpetual.FindPerpetualHeader;
import com.almailem.ams.api.connector.model.perpetual.FindPerpetualLine;
import com.almailem.ams.api.connector.model.perpetual.PerpetualHeader;
import com.almailem.ams.api.connector.model.perpetual.PerpetualLine;
import com.almailem.ams.api.connector.model.wms.UpdateStockCountLine;
import com.almailem.ams.api.connector.model.wms.WarehouseApiResponse;
import com.almailem.ams.api.connector.service.PerpetualService;
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
@Api(tags = {"Perpetual"}, value = "Perpetual Operations related to PerpetualController")
@SwaggerDefinition(tags = {@Tag(name = "Perpetual", description = "Operations related to Perpetual")})
@RequestMapping("/perpetual")
@RestController
public class PerpetualController {

    @Autowired
    PerpetualService perpetualService;

    //Get All StockCount Perpetual Details
    @ApiOperation(response = PerpetualHeader.class, value = "Get All Perpetual Details")
    @GetMapping("")
    public ResponseEntity<?> getAllPerpetuals() {
        List<PerpetualHeader> perpetuals = perpetualService.getAllPerpetualDetails();
        return new ResponseEntity<>(perpetuals, HttpStatus.OK);
    }

    @ApiOperation(response = WarehouseApiResponse.class, value = "Update Perpetual Line Counted Qty")
    @PatchMapping("/updateCountedQty")
    public ResponseEntity<?> patchPerpetualLine(@RequestBody List<UpdateStockCountLine> updateStockCountLine) {
        WarehouseApiResponse perpetuals = perpetualService.updatePerpetualStockCount(updateStockCountLine);
        return new ResponseEntity<>(perpetuals, HttpStatus.OK);
    }

    // Find PerpetualHeader
    @ApiOperation(response = PerpetualHeader.class, value = "Find PerpetualHeader") // label for Swagger
    @PostMapping("/findPerpetualHeader")
    public ResponseEntity<?> findItemMaster(@RequestBody FindPerpetualHeader findPerpetualHeader) throws ParseException {
        List<PerpetualHeader> perpetualHeader = perpetualService.findPerpetualHeader(findPerpetualHeader);
        return new ResponseEntity<>(perpetualHeader, HttpStatus.OK);
    }

    // Find PerpetualLine
    @ApiOperation(response = PerpetualLine.class, value = "Find PerpetualLine") // label for Swagger
    @PostMapping("/findPerpetualLine")
    public ResponseEntity<?> findPerpetualLine(@RequestBody FindPerpetualLine findPerpetualLine) throws ParseException {
        List<PerpetualLine> perpetualLine= perpetualService.findPerpetualLine(findPerpetualLine);
        return new ResponseEntity<>(perpetualLine, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualHeader.class, value = "Update PerpetualHeader")
    @PatchMapping("/updatePerpetualHeader")
    public ResponseEntity<?> patchPerpetualHeader(@RequestBody List<PerpetualHeader> perpetualHeaders) {
        List<PerpetualHeader> perpetualHeadersList = perpetualService.updatePerpetualHeader(perpetualHeaders);
        return new ResponseEntity<>(perpetualHeadersList, HttpStatus.OK);
    }

}
