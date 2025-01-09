package com.mnrclara.spark.core.controller;

import com.mnrclara.spark.core.model.overc360.Consignment;
import com.mnrclara.spark.core.model.overc360.prealert.FindPreAlert;
import com.mnrclara.spark.core.model.overc360.prealert.PreAlert;
import com.mnrclara.spark.core.service.overc360.ConsignmentSparkService;
import com.mnrclara.spark.core.service.overc360.PreAlertService;
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
@RestController
@Api(tags = {"OverC"}, value = "OverC Operations related to Controller")
@SwaggerDefinition(tags = {@Tag(name = "User", description = "Operation related to OverC")})
@RequestMapping("/spark/overc")
public class OverC360Controller {

    @Autowired
    private ConsignmentSparkService consignmentSparkService;

    @Autowired
    PreAlertService preAlertService;

    // Find Consignments
    @ApiOperation(response = Consignment.class, value = "Spark FindConsignment")
    @PostMapping("/consignment/find")
    public ResponseEntity<?> findConsignmentEntity() {
        List<Consignment> consignments = consignmentSparkService.fetchConsignments();
        return new ResponseEntity<>(consignments, HttpStatus.OK);
    }



    @ApiOperation(response = PreAlert.class, value = "PreAlertService")
    @PostMapping("/preAlert")
    public ResponseEntity<?> getGrLine(@RequestBody FindPreAlert findPreAlert) {
        List<PreAlert> findGrLine = preAlertService.findPreAlert(findPreAlert);
        return new ResponseEntity<>(findGrLine, HttpStatus.OK);
    }

}
