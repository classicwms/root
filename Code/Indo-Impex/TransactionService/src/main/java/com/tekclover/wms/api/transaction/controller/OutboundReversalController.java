package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.model.outbound.SearchOutboundReversal;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.AddOutboundReversal;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.OutboundReversal;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.v2.OutboundReversalV2;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.v2.SearchOutboundReversalV2;
import com.tekclover.wms.api.transaction.service.OutboundReversalService;
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
@Api(tags = {"OutboundReversal"}, value = "OutboundReversal  Operations related to OutboundReversalController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "OutboundReversal ", description = "Operations related to OutboundReversal ")})
@RequestMapping("/outboundreversal")
@RestController
public class OutboundReversalController {

    @Autowired
    OutboundReversalService outboundreversalService;

    @ApiOperation(response = OutboundReversal.class, value = "Get all OutboundReversal details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<OutboundReversal> outboundreversalList = outboundreversalService.getOutboundReversals();
        return new ResponseEntity<>(outboundreversalList, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundReversal.class, value = "Search OutboundReversal") // label for swagger
    @PostMapping("/findOutboundReversal")
    public List<OutboundReversal> findOutboundReversal(@RequestBody SearchOutboundReversal searchOutboundReversal)
            throws Exception {
        return outboundreversalService.findOutboundReversal(searchOutboundReversal);
    }

    //Stream
    @ApiOperation(response = OutboundReversal.class, value = "Search OutboundReversal New") // label for swagger
    @PostMapping("/findOutboundReversalNew")
    public Stream<OutboundReversal> findOutboundReversalNew(@RequestBody SearchOutboundReversal searchOutboundReversal)
            throws Exception {
        return outboundreversalService.findOutboundReversalNew(searchOutboundReversal);
    }

    @ApiOperation(response = OutboundReversal.class, value = "Create OutboundReversal") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postOutboundReversal(@Valid @RequestBody AddOutboundReversal newOutboundReversal, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        OutboundReversal createdOutboundReversal =
                outboundreversalService.createOutboundReversal(newOutboundReversal, loginUserID);
        return new ResponseEntity<>(createdOutboundReversal, HttpStatus.OK);
    }

    //=======================================================V2============================================================
    @ApiOperation(response = OutboundReversalV2.class, value = "Get all OutboundReversal details") // label for swagger
    @GetMapping("/v2")
    public ResponseEntity<?> getAllOutboundReversalV2() {
        List<OutboundReversalV2> outboundreversalList = outboundreversalService.getOutboundReversalsV2();
        return new ResponseEntity<>(outboundreversalList, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundReversalV2.class, value = "Search OutboundReversal") // label for swagger
    @PostMapping("/v2/findOutboundReversal")
    public List<OutboundReversalV2> findOutboundReversalV2(@RequestBody SearchOutboundReversalV2 searchOutboundReversal)
            throws Exception {
        return outboundreversalService.findOutboundReversalV2(searchOutboundReversal);
    }

    //Stream
    @ApiOperation(response = OutboundReversalV2.class, value = "Search OutboundReversal New") // label for swagger
    @PostMapping("/v2/findOutboundReversalNew")
    public Stream<OutboundReversalV2> findOutboundReversalNewV2(@RequestBody SearchOutboundReversalV2 searchOutboundReversal)
            throws Exception {
        return outboundreversalService.findOutboundReversalNewV2(searchOutboundReversal);
    }

    @ApiOperation(response = OutboundReversalV2.class, value = "Create OutboundReversal") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postOutboundReversalV2(@Valid @RequestBody OutboundReversalV2 newOutboundReversal, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        OutboundReversalV2 createdOutboundReversal =
                outboundreversalService.createOutboundReversalV2(newOutboundReversal, loginUserID);
        return new ResponseEntity<>(createdOutboundReversal, HttpStatus.OK);
    }
}