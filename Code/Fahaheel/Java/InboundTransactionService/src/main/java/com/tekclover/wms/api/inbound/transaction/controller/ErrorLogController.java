package com.tekclover.wms.api.inbound.transaction.controller;

import com.tekclover.wms.api.inbound.transaction.model.errorlog.ErrorLog;
import com.tekclover.wms.api.inbound.transaction.model.errorlog.FindErrorLog;
import com.tekclover.wms.api.inbound.transaction.service.ErrorLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated

@Api(tags = {"ErrorLog"}, value = "ErrorLog  Operations related to ErrorLogController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ErrorLog ",description = "Operations related to ErrorLog ")})
@RequestMapping("/errorlog")
@RestController
public class ErrorLogController {

    @Autowired
    ErrorLogService errorLogService;

    @ApiOperation(response = ErrorLog.class, value = "Find in ErrorLog")
    @PostMapping("/find")
    public ResponseEntity<?> findErrorLog(@RequestBody FindErrorLog findErrorLog){
        List<ErrorLog> errorLog = errorLogService.findErrorlog(findErrorLog);
        return new ResponseEntity<>(errorLog, HttpStatus.OK);
    }

}
