package com.mnrclara.spark.core.controller;


import com.mnrclara.spark.core.model.wmscorev2.FindPreInboundHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.PreInboundHeaderV2;
import com.mnrclara.spark.core.service.ImpexDevService;
import com.mnrclara.spark.core.service.test.TestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@Api(tags = {"Mdb"}, value = "Mdb Operations related to TestController")
@SwaggerDefinition(tags = {@Tag(name = "Test", description = "Operations related to Mdb")})
@RequestMapping("/testMDB")
public class TestController {


   @Autowired
   TestService testService;

    @ApiOperation(response = Optional.class, value = "Spark Test FindPreInboundHeader")
    @PostMapping("/test")
    public ResponseEntity<?> findPreinboundHeaderv2(@RequestBody FindPreInboundHeaderV2 findPreInboundHeaderV2) throws Exception {
        List<PreInboundHeaderV2> preInboundHeaderv2 = testService.findPreInboundHeaderv2(findPreInboundHeaderV2);
        return new ResponseEntity<>(preInboundHeaderv2, HttpStatus.OK);
    }
}
