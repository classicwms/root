package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.auditlog.AuditLog;
import com.tekclover.wms.api.idmaster.service.AuditLogService;
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

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = {"AuditLog"}, value = "AuditLog Operations related to AuditLogController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "AuditLog", description = "Operations related to AuditLog")})
@RequestMapping("/auditLog")
@RestController
public class AuditLogController {

    @Autowired
    AuditLogService auditLogService;

    @ApiOperation(response = AuditLog.class, value = "Get all AuditLog details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<AuditLog> auditlogList = auditLogService.getAuditLogs();
        return new ResponseEntity<>(auditlogList, HttpStatus.OK);
    }

    @ApiOperation(response = AuditLog.class, value = "Get a AuditLog") // label for swagger 
    @GetMapping("/{auditLogNumber}")
    public ResponseEntity<?> getAuditLog(@PathVariable String auditLogNumber) {
        AuditLog auditlog = auditLogService.getAuditLog(auditLogNumber);
        log.info("AuditLog : " + auditlog);
        return new ResponseEntity<>(auditlog, HttpStatus.OK);
    }

    @ApiOperation(response = AuditLog.class, value = "Create AuditLog") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postAuditLog(@Valid @RequestBody AuditLog newAuditLog, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        AuditLog createdAuditLog = auditLogService.createAuditLog(newAuditLog, loginUserID);
        return new ResponseEntity<>(createdAuditLog, HttpStatus.OK);
    }

    @ApiOperation(response = AuditLog.class, value = "Delete AuditLog") // label for swagger
    @DeleteMapping("/{auditLogNumber}")
    public ResponseEntity<?> deleteAuditLog(@PathVariable String auditLogNumber) {
        auditLogService.deleteAuditLog(auditLogNumber);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}