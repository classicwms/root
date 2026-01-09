package com.tekclover.wms.api.inbound.transaction.controller;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.mnc.AddInhouseTransferLine;
import com.tekclover.wms.api.inbound.transaction.model.mnc.InhouseTransferLine;
import com.tekclover.wms.api.inbound.transaction.model.mnc.SearchInhouseTransferLine;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.service.BaseService;
import com.tekclover.wms.api.inbound.transaction.service.InhouseTransferLineService;
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
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"InhouseTransferLine"}, value = "InhouseTransferLine  Operations related to InhouseTransferLineController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "InhouseTransferLine ", description = "Operations related to InhouseTransferLine ")})
@RequestMapping("/inhousetransferline")
@RestController
public class InhouseTransferLineController {

    @Autowired
    InhouseTransferLineService inhouseTransferLineService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = InhouseTransferLine.class, value = "Get all InhouseTransferLine details")
    // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<InhouseTransferLine> InhouseTransferLineList = inhouseTransferLineService.getInhouseTransferLines();
        return new ResponseEntity<>(InhouseTransferLineList, HttpStatus.OK);
    }

    @ApiOperation(response = InhouseTransferLine.class, value = "Get a InhouseTransferLine") // label for swagger 
    @GetMapping("/{transferNumber}")
    public ResponseEntity<?> getInhouseTransferLine(@PathVariable String transferNumber, @RequestParam String warehouseId,
                                                    @RequestParam String sourceItemCode) {
        InhouseTransferLine inhouseTransferLine =
                inhouseTransferLineService.getInhouseTransferLine(warehouseId, transferNumber, sourceItemCode);
        log.info("InhouseTransferLine : " + inhouseTransferLine);
        return new ResponseEntity<>(inhouseTransferLine, HttpStatus.OK);
    }

    @ApiOperation(response = InhouseTransferLine.class, value = "Search InhouseTransferLine") // label for swagger
    @PostMapping("/findInhouseTransferLine")
    public ResponseEntity<?> findInhouseTransferLine(@RequestBody SearchInhouseTransferLine searchInhouseTransferLine)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchInhouseTransferLine.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<InhouseTransferLine> results = inhouseTransferLineService.findInhouseTransferLine(searchInhouseTransferLine);
            return new ResponseEntity<>(results, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InhouseTransferLine.class, value = "Create InhouseTransferLine") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postInhouseTransferLine(@Valid @RequestBody AddInhouseTransferLine newInhouseTransferLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(newInhouseTransferLine.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InhouseTransferLine createdInhouseTransferLine = inhouseTransferLineService.createInhouseTransferLine(newInhouseTransferLine, loginUserID);
            return new ResponseEntity<>(createdInhouseTransferLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}