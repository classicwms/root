package com.almailem.ams.api.connector.controller;


import com.almailem.ams.api.connector.model.IntegrationLog.IntegrationLog;
import com.almailem.ams.api.connector.model.transferin.TransferInHeader;
import com.almailem.ams.api.connector.service.IntegrationLogService;
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
@Api(tags = {"IntegrationLog"}, value = "IntegrationLog Operations related to IntegrationLog")
@SwaggerDefinition(tags = {@Tag(name = "IntegrationLog", description = "Operations related to IntegrationLog")})
@RequestMapping("/integrationlog")
@RestController
public class IntegrationLogController {

    @Autowired
    IntegrationLogService integrationLogService;

    /**
     * Get All IntegrationLog
     *
     * @return
     */
    @ApiOperation(response = IntegrationLog.class, value = "Get All IntegrationLog Details")
    @GetMapping("")
    public ResponseEntity<?> getAllIntegrationLog(){
        List<IntegrationLog> integrationLogList = integrationLogService.getAllIntegrationLog();
        return new ResponseEntity<>(integrationLogList, HttpStatus.OK);
    }

}
