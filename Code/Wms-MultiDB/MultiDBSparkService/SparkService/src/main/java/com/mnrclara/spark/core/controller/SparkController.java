package com.mnrclara.spark.core.controller;

import java.util.List;
import java.util.Optional;

import com.mnrclara.spark.core.model.*;
import com.mnrclara.spark.core.model.Almailem.SearchImBasicData1;
import com.mnrclara.spark.core.service.*;
import com.mnrclara.spark.core.service.almailem.SparkImBasicData1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = {"User"}, value = "User Operations related to UserController")
@SwaggerDefinition(tags = {@Tag(name = "User", description = "Operations related to User")})
@RequestMapping("/spark")
public class SparkController {

    @Autowired
    PutAwayHeaderService putAwayHeaderService;
//    @Autowired
//    ImBasicData1Service imBasicData1Service;

//    @Autowired
//    AccountingService accountingService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    InboundHeaderService inboundHeaderService;
//    @Autowired
//    InventoryMovementServiceV2 inventoryMovementServiceV2;

    @Autowired
    InventoryMovementService inventoryMovementService;

    @Autowired
    ControlGroupService controlGroupService;

//    @Autowired
//    InvoiceService invoiceService;

    @Autowired
    PickupHeaderService pickupHeaderService;

    @Autowired
    QualityHeaderService qualityHeaderService;

    @Autowired
    StagingHeaderService stagingHeaderService;

    @Autowired
    GrHeaderService grHeaderService;

    @Autowired
    PreOutboundHeaderService preOutboundHeaderService;

    @Autowired
    OrderManagementLineService orderManagementLineService;

    @Autowired
    PreInboundHeaderService preInboundHeaderService;

    @Autowired
    OutBoundHeaderService outBoundHeaderService;

    //	 @Autowired
//	 OutBoundReversalService outBoundReversalService;
    @Autowired
    OutBoundReversalService outBoundReversalService;

    @Autowired
    ContainerReceiptService containerReceiptService;
//	@Autowired
//	ImBasicData1Service imBasicData1Service;

    @Autowired
    OutBoundOrderHeaderService outBoundOrderHeaderService;

    @Autowired
    SparkImBasicData1Service almSparkImBasicData1Service;



//    @ApiOperation(response = Optional.class, value = "Get all Users")
//	@GetMapping("/user")
//	public ResponseEntity<?> getAll() {
//		List<User> userList = userService.getUsers();
//		return new ResponseEntity<>(userList, HttpStatus.OK);
//	}
//
//    @ApiOperation(response = Optional.class, value = "Get a User")
//	@GetMapping("/user/{id}")
//	public ResponseEntity<?> getUser(@PathVariable Long id) {
//    	List<User> userList = userService.getUsers();
//    	User user = userList.stream()
//    			.filter(u -> u.getUserId() == id)
//    			.findFirst()
//                .orElse(null);
//    	log.info("User : " + user);
//		return new ResponseEntity<>(user, HttpStatus.OK);
//	}
//
//    @ApiOperation(response = Optional.class, value = "Create User")
//	@PostMapping("/user")
//	public ResponseEntity<?> addUser(@RequestBody User newUser) {
//    	log.info("User name: " + newUser.getUserName());
//    	User createdUser = userService.createUser(newUser);
//		return new ResponseEntity<>(createdUser , HttpStatus.OK);
//	}
//
//    @ApiOperation(response = Optional.class, value = "Patch User")
//	@PatchMapping("/user")
//	public ResponseEntity<?> patchUser(@RequestBody User modifiedUser) {
//    	log.info("User name: " + modifiedUser.getUserName());
//    	User updatedUser = userService.patchUser(modifiedUser);
//		return new ResponseEntity<>(updatedUser , HttpStatus.OK);
//	}
//
//    @ApiOperation(response = Optional.class, value = "Delete User")
//	@DeleteMapping("/user/{id}")
//	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
//    	userService.deleteUser(id);
//		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//	}
//
//    /////////////////////////////////////////////////////////////////////////////////////////////
//
//    @ApiOperation(response = Optional.class, value = "Spark Test")
//   	@GetMapping("/spark")
//   	public ResponseEntity<?> sparkTest() throws Exception {
//    	List<Client> client = userService.sparkProcess();
//   		return new ResponseEntity<>(client, HttpStatus.OK);
//   	}

//    @ApiOperation(response = Optional.class, value = "Spark Test")
//    @PostMapping("/invoiceHeader")
//    public ResponseEntity<?> findInvoiceHeaders(@RequestBody SearchInvoiceHeader searchInvoiceHeader) throws Exception {
//    	List<Client> client = userService.sparkProcess();
//        List<InvoiceHeader> invoiceHeaders = invoiceService.findInvoiceHeaders(searchInvoiceHeader);
//        return new ResponseEntity<>(invoiceHeaders, HttpStatus.OK);
//    }

    @ApiOperation(response = Optional.class, value = "Spark Test")
    @PostMapping("/controlgroup")
    public ResponseEntity<?> findControlGroup(@RequestBody FindControlGroup findControlGroup) throws Exception {
        List<ControlGroupType> controlGroupTypes = controlGroupService.findControlGroupType(findControlGroup);
        return new ResponseEntity<>(controlGroupTypes, HttpStatus.OK);
    }

//    @ApiOperation(response = Optional.class, value = "Spark Test")
//    @PostMapping("/inventorymovement")
//    public ResponseEntity<?> findInventoryMovement(@RequestBody FindInventoryMovement findInventoryMovement) throws Exception {
//        List<InventoryMovement> inventoryMovements = inventoryMovementService.findInventoryMovement(findInventoryMovement);
//        return new ResponseEntity<>(inventoryMovements, HttpStatus.OK);
//    }

    @ApiOperation(response = Optional.class, value = "Calculate Total Language ID Sum")
    @PostMapping("/calculateTotalLanguageId")
    public ResponseEntity<?> calculateTotalLanguageId(@RequestBody FindResult findResult) throws Exception {
        ControlGroupResponse totalLanguageId = controlGroupService.calculateTotalLanguageId(findResult);
        return new ResponseEntity<>(totalLanguageId, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Calculate Total Company ID Sum")
    @PostMapping("/calculateTotalCompanyId")
    public ResponseEntity<?> calculateTotalCompanyId(@RequestBody FindResult findResult) throws Exception {
        ControlGroupResponse totalCompanyId = controlGroupService.calculateTotalCompanyId(findResult);
        return new ResponseEntity<>(totalCompanyId, HttpStatus.OK);
    }

//    @ApiOperation(response = Optional.class, value = "Calculate Total Company ID Sum")
//    @PostMapping("/billedHoursPaid")
//    public ResponseEntity<?> billedHoursPaid(@RequestBody BilledHoursPaid billedHoursPaid) throws Exception {
//        List<BilledHoursPaidReport> totalCompanyId = accountingService.getBilledHoursPaidReport(billedHoursPaid);
//        return new ResponseEntity<>(totalCompanyId, HttpStatus.OK);
//    }

    @ApiOperation(response = Optional.class, value = "Spark Inventory")
    @PostMapping("/inventory")
    public ResponseEntity<?> findInventory(@RequestBody FindInventory findInventory) throws Exception {
        List<Inventory> inventory = inventoryService.findInventory(findInventory);
        return new ResponseEntity<>(inventory, HttpStatus.OK);
    }

//    @ApiOperation(response = Optional.class, value = "Spark Test")
//    @PostMapping("/inventory/inventorymovement")
//    public ResponseEntity<?> findInventoryMvt(@RequestBody FindInventoryMovement findInventoryMovement) throws Exception {
//        List<InventoryMovementV2> inventoryMvts = inventoryMovementServiceV2.findInventoryMovement(findInventoryMovement);
//        return new ResponseEntity<>(inventoryMvts, HttpStatus.OK);
//    }

//    @ApiOperation(response = ImBasicData1.class, value = "Spark ImBasicData1")
//    @PostMapping("/imbasicData1")
//    public ResponseEntity<?> findImBasicData1(FindImBasicData1 findImBasicData1) throws Exception {
//        List<ImBasicData1> imBasicData1List = imBasicData1Service.findImBasicData1(findImBasicData1);
//        return new ResponseEntity<>(imBasicData1List, HttpStatus.OK);
//    }

    @ApiOperation(response = PutAwayHeader.class, value = "Spark PutawayHeader")
    @PostMapping("/putaway")
    public ResponseEntity<?> findPutAwayHeader(@RequestBody FindPutAwayHeader findPutAwayHeader) throws Exception {
        List<PutAwayHeader> putAwayHeaderList = putAwayHeaderService.findPutAwayHeader(findPutAwayHeader);
        return new ResponseEntity<>(putAwayHeaderList, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeader.class, value = "Spark InboundHeader")
    @PostMapping("/inboundHeader")
    public ResponseEntity<?> findInboundHeader(@RequestBody FindInboundHeader findInboundHeader) throws Exception {
        List<InboundHeader> inboundHeaderList = inboundHeaderService.findInboundHeader(findInboundHeader);
        return new ResponseEntity<>(inboundHeaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PickupHeader.class, value = "Spark Pickup Header")
    @PostMapping("/pickupHeader")
    public ResponseEntity<?> findPickupHeader(@RequestBody FindPickupHeader findPickupHeader) throws Exception {
        List<PickupHeader> pickupHeaderList = pickupHeaderService.findPickupHeader(findPickupHeader);
        return new ResponseEntity<>(pickupHeaderList, HttpStatus.OK);
    }

    @ApiOperation(response = QualityHeader.class, value = "Spark Quality Header")
    @PostMapping("/qualityHeader")
    public ResponseEntity<?> findQualityHeader(@RequestBody FindQualityHeader findQualityHeader) throws Exception {
        List<QualityHeader> qualityHeaderList = qualityHeaderService.findQualityHeader(findQualityHeader);
        return new ResponseEntity<>(qualityHeaderList, HttpStatus.OK);
    }

    //Find StagingHeader
    @ApiOperation(response = StagingHeader.class, value = "Spark Find StagingHeader")
    @PostMapping("/stagingheader/find")
    public ResponseEntity<?> searchStagingHeader(@RequestBody FindStagingHeader findStagingHeader) throws Exception {
        List<StagingHeader> stList = stagingHeaderService.findStagingHeader(findStagingHeader);
        return new ResponseEntity<>(stList, HttpStatus.OK);
    }


    //Find GrHeader
    @ApiOperation(response = GrHeader.class, value = "Spark Find GrHeader")
    @PostMapping("/grheader/find")
    public ResponseEntity<?> searchGrHeader(@RequestBody FindGrHeader findGrHeader) throws Exception {
        List<GrHeader> grList = grHeaderService.findGrHeader(findGrHeader);
        return new ResponseEntity<>(grList, HttpStatus.OK);
    }


    //Find PreOutboundHeader
    @ApiOperation(response = PreOutboundHeader.class, value = "Spark Find PreOutboundHeader")
    @PostMapping("/preoutboundheader/find")
    public ResponseEntity<?> searchPreOutboundHeader(@RequestBody FindPreOutboundHeader findPreObHeader) throws Exception {
        List<PreOutboundHeader> preObList = preOutboundHeaderService.findPreOutboundHeader(findPreObHeader);
        return new ResponseEntity<>(preObList, HttpStatus.OK);
    }


    //Find OrderManagementLine
    @ApiOperation(response = OrderManagementLine.class, value = "Spark Find OrderManagementLine")
    @PostMapping("/ordermanagementline/find")
    public ResponseEntity<?> searchOrderManagementLine(@RequestBody FindOrderManagementLine findOrderManagementLine)
            throws Exception {
        List<OrderManagementLine> omlList = orderManagementLineService.findOrderManagementLine(findOrderManagementLine);
        return new ResponseEntity<>(omlList, HttpStatus.OK);
    }

    //Find PreInboundHeader
    @ApiOperation(response = PreInboundHeader.class, value = "Spark Find PreInboundHeader")
    @PostMapping("/preinboundheader/find")
    public ResponseEntity<?> searchPreInboundHeader(@RequestBody FindPreInboundHeader findPreInboundHeader)
            throws Exception {
        List<PreInboundHeader> omlList = preInboundHeaderService.findPreInboundHeader(findPreInboundHeader);
        return new ResponseEntity<>(omlList, HttpStatus.OK);
    }

    @ApiOperation(response = OutBoundHeader.class, value = "Spark Find OutboundHeader")
    @PostMapping("/outboundheader/find")
    public ResponseEntity<?> searchOutBoundHeader(@RequestBody FindOutBoundHeader findOutBoundHeader)
            throws Exception {
        List<OutBoundHeader> omlList = outBoundHeaderService.findOutBoundHeader(findOutBoundHeader);
        return new ResponseEntity<>(omlList, HttpStatus.OK);
    }

    @ApiOperation(response = OutBoundReversal.class, value = "Spark Find OutboundReversal")
    @PostMapping("/outboundreversal/find")
    public ResponseEntity<?> searchOutBoundReversal(@RequestBody FindOutBoundReversal findOutBoundReversal)
            throws Exception {
        List<OutBoundReversal> outBoundReversal = outBoundReversalService.findOutBoundReversal(findOutBoundReversal);
        return new ResponseEntity<>(outBoundReversal, HttpStatus.OK);
    }

    @ApiOperation(response = ContainerReceipt.class, value = "Spark Container Receipt")
    @PostMapping("/containerReceipt/find")
    public ResponseEntity<?> findContainerReceipt(@RequestBody FindContainerReceipt findContainerReceipt) throws Exception {
        List<ContainerReceipt> containerReceiptList = containerReceiptService.findContainerReceipt(findContainerReceipt);
        return new ResponseEntity<>(containerReceiptList, HttpStatus.OK);
    }


    //Find OrderManagementLine
    @ApiOperation(response = OutboundHeaderOutput.class, value = "Spark Find OutBoundOrderOutput")
    @PostMapping("/outboundorderoutput/find")
    public ResponseEntity<?> searchOutBoundOrderOutput(@RequestBody FindOutBoundHeader findOutBoundHeader)
            throws Exception {
        List<OutboundHeaderOutput> outboundHeaderOutputs = outBoundOrderHeaderService.findOutBoundHeader(findOutBoundHeader);
        return new ResponseEntity<>(outboundHeaderOutputs, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Spark Test")
    @PostMapping("/imbasicdata1/findImBasicData1")
    public ResponseEntity<?> findImBasicData1Almailem(@RequestBody SearchImBasicData1 searchImBasicData1) throws Exception {
        List<com.mnrclara.spark.core.model.Almailem.ImBasicData1> imBasicData1s = almSparkImBasicData1Service.searchImBasicData1(searchImBasicData1);
        return new ResponseEntity<>(imBasicData1s, HttpStatus.OK);
    }

}