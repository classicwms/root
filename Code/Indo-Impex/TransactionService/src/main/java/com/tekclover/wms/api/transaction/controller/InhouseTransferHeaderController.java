package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.model.mnc.*;
import com.tekclover.wms.api.transaction.service.InhouseTransferHeaderService;
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
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"InHouseTransferHeader"}, value = "InHouseTransferHeader  Operations related to InHouseTransferHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "InHouseTransferHeader ", description = "Operations related to InHouseTransferHeader ")})
@RequestMapping("/inhousetransferheader")
@RestController
public class InhouseTransferHeaderController {

    @Autowired
    InhouseTransferHeaderService inHouseTransferHeaderService;

    @ApiOperation(response = InhouseTransferHeader.class, value = "Get all InHouseTransferHeader details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<InhouseTransferHeader> inhousetransferheaderList =
                inHouseTransferHeaderService.getInHouseTransferHeaders();
        return new ResponseEntity<>(inhousetransferheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = InhouseTransferHeader.class, value = "Get a InHouseTransferHeader") // label for swagger 
    @GetMapping("/{transferNumber}")
    public ResponseEntity<?> getInHouseTransferHeader(@PathVariable String transferNumber, @RequestParam String warehouseId, @RequestParam Long transferTypeId) {
        InhouseTransferHeader inhousetransferheader = inHouseTransferHeaderService.getInHouseTransferHeader(warehouseId, transferNumber, transferTypeId);
        return new ResponseEntity<>(inhousetransferheader, HttpStatus.OK);
    }

    @ApiOperation(response = InhouseTransferHeader.class, value = "Search InHouseTransferHeader") // label for swagger
    @PostMapping("/findInHouseTransferHeader")
    public ResponseEntity<?> findInHouseTransferHeader(@RequestBody SearchInhouseTransferHeader searchInHouseTransferHeader)
            throws Exception {
        log.info("called...");
        List<InhouseTransferHeader> results = inHouseTransferHeaderService.findInHouseTransferHeader(searchInHouseTransferHeader);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    //Stream
    @ApiOperation(response = InhouseTransferHeader.class, value = "Search InHouseTransferHeader New") // label for swagger
    @PostMapping("/findInHouseTransferHeaderNew")
    public ResponseEntity<?> findInHouseTransferHeaderNew(@RequestBody SearchInhouseTransferHeader searchInHouseTransferHeader)
            throws Exception {
        Stream<InhouseTransferHeader> results = inHouseTransferHeaderService.findInHouseTransferHeaderNew(searchInHouseTransferHeader);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @ApiOperation(response = InhouseTransferHeader.class, value = "Create InHouseTransferHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postInHouseTransferHeader(@Valid @RequestBody AddInhouseTransferHeader newInHouseTransferHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        InhouseTransferHeaderEntity createdInHouseTransferHeader =
                inHouseTransferHeaderService.createInHouseTransferHeader(newInHouseTransferHeader, loginUserID);
        return new ResponseEntity<>(createdInHouseTransferHeader, HttpStatus.OK);
    }

    //===================================================================V2==================================================================================

    @ApiOperation(response = InhouseTransferHeader.class, value = "Create InHouseTransferHeader") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postInHouseTransferHeaderV2(@Valid @RequestBody AddInhouseTransferHeader newInHouseTransferHeader,
                                                         @RequestParam String loginUserID) throws Exception {
        InhouseTransferHeaderEntity createdInHouseTransferHeader =
                inHouseTransferHeaderService.createInHouseTransferHeaderV2(newInHouseTransferHeader, loginUserID);
        return new ResponseEntity<>(createdInHouseTransferHeader, HttpStatus.OK);
    }

    @ApiOperation(response = InhouseTransferHeader.class, value = "Create InHouseTransferHeader Upload") // label for swagger
    @PostMapping("/v2/upload")
    public ResponseEntity<?> postInHouseTransferHeaderUploadV2(@Valid @RequestBody List<InhouseTransferUpload> inhouseTransferUploadList, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        inHouseTransferHeaderService.createInHouseTransferHeaderUploadV2(inhouseTransferUploadList, loginUserID);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}