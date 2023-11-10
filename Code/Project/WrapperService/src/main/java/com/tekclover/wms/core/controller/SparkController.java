package com.tekclover.wms.core.controller;

import com.tekclover.wms.core.model.spark.ImBasicData1;
import com.tekclover.wms.core.service.SparkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/mnr-spark-service")
@Api(tags = { "Spark Services" }, value = "Spark Services") // label for swagger
@SwaggerDefinition(tags = { @Tag(name = "Spark", description = "Operations related to SparkService") })
public class SparkController {

    @Autowired
    private SparkService sparkService;

    @ApiOperation(response = ImBasicData1.class, value = "Get All ImBasicData1 details")
    @GetMapping("/imbasicdata1")
    public ResponseEntity<?> getAllImBasicData1() {
        ImBasicData1[] imBasicData1s = sparkService.getImBasicData1();
        return new ResponseEntity<>(imBasicData1s, HttpStatus.OK);
    }
}
