package com.tekclover.wms.api.inbound.transaction.controller;

import com.tekclover.wms.api.inbound.transaction.model.errorlog.ErrorLog;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@Api(tags = {"ExceptionLog"}, value = "ExceptionLog Operations related to ExceptionLog")
@SwaggerDefinition(tags = {@Tag(name = "ExceptionLog", description = "Operations related to ExceptionLog")})
@RequestMapping("/exceptionlog")
@RestController
public class ExceptionLogController {

    @Autowired
    ErrorLogService errorLogService;

    // Get All Exception Log Details
    @ApiOperation(response = ErrorLog.class, value = "Get All Exception Log Details")
    @GetMapping("/all")
    public ResponseEntity<?> getAllExceptionLogDetails() {
        List<ErrorLog> errorLogList = errorLogService.getAllExceptionLogs();
        return new ResponseEntity<>(errorLogList, HttpStatus.OK);
    }

}
