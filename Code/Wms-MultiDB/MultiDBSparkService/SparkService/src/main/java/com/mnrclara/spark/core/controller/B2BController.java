//package com.mnrclara.spark.core.controller;
//
//
//import com.mnrclara.spark.core.model.b2b.*;
//import com.mnrclara.spark.core.service.b2b.*;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.SwaggerDefinition;
//import io.swagger.annotations.Tag;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@Slf4j
//@RestController
//@Api(tags = {"User"}, value = "User Operations related to UserController")
//@SwaggerDefinition(tags = {@Tag(name = "User", description = "Operations related to User")})
//@RequestMapping("/b2b")
//public class B2BController {
//
//    @Autowired
//    ConsignmentService consignmentService;
//
//    @Autowired
//    ConsignmentServiceV2 consignmentServiceV2;
//
//    @Autowired
//    JntwebhookService jntwebhookService;
//
//    @Autowired
//    JntHubCodeService jntHubCodeService;
//
//    @Autowired
//    QatarPostService qatarPostService;
//
//    @ApiOperation(response = Consignment.class, value = "Spark Consignment")
//    @PostMapping("/consignment")
//    public ResponseEntity<?> findConsignment(@RequestBody FindConsignment findConsignment) throws Exception {
//        List<Consignment> consignmentList = consignmentService.findConsignment(findConsignment);
//        return new ResponseEntity<>(consignmentList, HttpStatus.OK);
//    }
//    @ApiOperation(response = ConsignmentV2.class, value = "Spark ConsignmentV2")
//    @PostMapping("/consignmentV2")
//    public ResponseEntity<?> findB2B(@RequestBody FindConsignmentV2 findConsignment) throws Exception {
//        List<ConsignmentV2> consignmentList = consignmentServiceV2.findB2B(findConsignment);
//        return new ResponseEntity<>(consignmentList, HttpStatus.OK);
//    }
//
//    @ApiOperation(response = Jntwebhook.class, value = "Spark Jntwebhook")
//    @PostMapping("/jntwebhook")
//    public ResponseEntity<?> findB2BJntwebhook(@RequestBody FindJntwebhook findJntwebhook) throws Exception {
//        List<Jntwebhook> jntwebhookList = jntwebhookService.findB2BJntwebhook(findJntwebhook);
//        return new ResponseEntity<>(jntwebhookList, HttpStatus.OK);
//    }
//
//    //JntHubCode
//    @ApiOperation(response = JntHubCode.class, value = "Spark JntHubCode")
//    @PostMapping("/jnthubcode")
//    public ResponseEntity<?> findJntHubCode(@RequestBody FindJntHubCode findJntHubCode) throws Exception{
//        List<JntHubCode> jntHubCodeList = jntHubCodeService.findJntWebHook(findJntHubCode);
//        return new ResponseEntity<>(jntHubCodeList, HttpStatus.OK);
//    }
//    @ApiOperation(response = QPWebhook.class, value = "Spark QPWebhook")
//    @PostMapping("/qpwebhook")
//    public ResponseEntity<?> findQPWebhook(@RequestBody FindQPWebhook findQPWebhook) throws Exception{
//        List<QPWebhook> qpwebhookList = qatarPostService.findQPWebhook(findQPWebhook);
//        return new ResponseEntity<>(qpwebhookList, HttpStatus.OK);
//    }
//}
