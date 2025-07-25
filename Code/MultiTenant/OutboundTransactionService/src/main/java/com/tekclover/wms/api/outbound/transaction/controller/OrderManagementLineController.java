package com.tekclover.wms.api.outbound.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.v2.AssignPickerV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.v2.OrderManagementLineImpl;
import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.v2.SearchOrderManagementLineV2;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.service.OrderManagementLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.AddOrderManagementLine;
import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.AssignPicker;
import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.OrderManagementLine;
import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.SearchOrderManagementLine;
import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.UpdateOrderManagementLine;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"OrderManagementLine"}, value = "OrderManagementLine  Operations related to OrderManagementLineController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "OrderManagementLine ", description = "Operations related to OrderManagementLine ")})
@RequestMapping("/ordermanagementline")
@RestController
public class OrderManagementLineController {

    @Autowired
    OrderManagementLineService ordermangementlineService;
    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = OrderManagementLine.class, value = "Get all OrderManagementLine details")
    // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<OrderManagementLine> ordermangementlineList = ordermangementlineService.getOrderManagementLines();
        return new ResponseEntity<>(ordermangementlineList, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementLine.class, value = "Get a OrderManagementLine") // label for swagger 
    @GetMapping("/{refDocNumber}")
    public ResponseEntity<?> getOrderManagementLine(@PathVariable String refDocNumber, @RequestParam String warehouseId,
                                                    @RequestParam String preOutboundNo, @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                                    @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackCode) {

        OrderManagementLine ordermangementline =
                ordermangementlineService.getOrderManagementLine(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        lineNumber, itemCode, proposedStorageBin, proposedPackCode);
        log.info("OrderManagementLine : " + ordermangementline);
        return new ResponseEntity<>(ordermangementline, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementLine.class, value = "Search OrderManagementLine") // label for swagger
    @PostMapping("/findOrderManagementLine")
    public List<OrderManagementLine> findOrderManagementLine(@RequestBody SearchOrderManagementLine searchOrderManagementLine)
            throws Exception {
        return ordermangementlineService.findOrderManagementLine(searchOrderManagementLine);
    }

    //Streaming
    @ApiOperation(response = OrderManagementLine.class, value = "Search OrderManagementLine New") // label for swagger
    @PostMapping("/findOrderManagementLineNew")
    public Stream<OrderManagementLine> findOrderManagementLineNew(@RequestBody SearchOrderManagementLine searchOrderManagementLine)
            throws Exception {

        return ordermangementlineService.findOrderManagementLineNew(searchOrderManagementLine);
    }

    @ApiOperation(response = OrderManagementLine.class, value = "Create OrderManagementLine") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postOrderManagementLine(@Valid @RequestBody AddOrderManagementLine newOrderManagementLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newOrderManagementLine.getCompanyCodeId(), newOrderManagementLine.getPlantId(), newOrderManagementLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            OrderManagementLine createdOrderManagementLine =
                    ordermangementlineService.createOrderManagementLine(newOrderManagementLine, loginUserID);
            return new ResponseEntity<>(createdOrderManagementLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OrderManagementLine.class, value = "UnAllocate") // label for swagger
    @PatchMapping("/unallocate")
    public ResponseEntity<?> unallocateOrderManagementLine(@RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                           @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                                           @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackBarCode,
                                                           @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbByWarehouse(warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            OrderManagementLine updatedOrderManagementLine =
                    ordermangementlineService.doUnAllocation(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                            itemCode, proposedStorageBin, proposedPackBarCode, loginUserID);
            return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
        }  finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OrderManagementLine.class, value = "Allocate") // label for swagger
    @PatchMapping("/allocate")
    public ResponseEntity<?> allocateOrderManagementLine(@RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                         @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                                         @RequestParam String itemCode, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbByWarehouse(warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            OrderManagementLine updatedOrderManagementLine =
                    ordermangementlineService.doAllocation(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                            itemCode, loginUserID);
            return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
        }  finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OrderManagementLine.class, value = "Allocate") // label for swagger
    @PatchMapping("/assignPicker")
    public ResponseEntity<?> assignPicker(@RequestBody List<AssignPicker> assignPicker, @RequestParam String assignedPickerId,
                                          @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {

        List<OrderManagementLine> updatedOrderManagementLine =
                ordermangementlineService.doAssignPicker(assignPicker, assignedPickerId, loginUserID);
        return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
    }


    @ApiOperation(response = OrderManagementLine.class, value = "Update OrderMangementLine") // label for swagger
    @PatchMapping("/{refDocNumber}")
    public ResponseEntity<?> patchOrderMangementLine(@PathVariable String refDocNumber,
                                                     @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                     @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                                     @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackCode,
                                                     @Valid @RequestBody UpdateOrderManagementLine updateOrderMangementLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(updateOrderMangementLine.getCompanyCodeId(), updateOrderMangementLine.getPlantId(),
                    warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            OrderManagementLine createdOrderMangementLine =
                    ordermangementlineService.updateOrderManagementLine(warehouseId, preOutboundNo, refDocNumber,
                            partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID, updateOrderMangementLine);
            return new ResponseEntity<>(createdOrderMangementLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OrderManagementLine.class, value = "Delete OrderManagementLine") // label for swagger
    @DeleteMapping("/{refDocNumber}")
    public ResponseEntity<?> deleteOrderManagementLine(@PathVariable String refDocNumber,
                                                       @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                       @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                                       @RequestParam String itemCode, @RequestParam String proposedStorageBin,
                                                       @RequestParam String proposedPackCode, @RequestParam String loginUserID) {
        ordermangementlineService.deleteOrderManagementLine(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //----------------------Update--Ref9&10------------------------------------------------------------
    @ApiOperation(response = OrderManagementLine.class, value = "Get a OrderManagementLine") // label for swagger 
    @GetMapping("/updateRefFields")
    public ResponseEntity<?> updateRefFields() {
        ordermangementlineService.updateRef9ANDRef10();
        return new ResponseEntity<>(HttpStatus.OK);
    }

//=======================================================V2============================================================================================

    @ApiOperation(response = OrderManagementLineV2.class, value = "Get all OrderManagementLine details")
    // label for swagger
    @GetMapping("/v2")
    public ResponseEntity<?> getAllOrderManagementLine() {
        List<OrderManagementLineV2> ordermangementlineList = ordermangementlineService.getOrderManagementLinesV2();
        return new ResponseEntity<>(ordermangementlineList, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "Get a OrderManagementLine") // label for swagger
    @GetMapping("/v2/{refDocNumbber}")
    public ResponseEntity<?> getOrderManagementLineV2(@PathVariable String refDocNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                      @RequestParam String languageId, @RequestParam String warehouseId,
                                                      @RequestParam String preOutboundNo, @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                                      @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackCode) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            OrderManagementLineV2 ordermangementline =
                    ordermangementlineService.getOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                            lineNumber, itemCode, proposedStorageBin, proposedPackCode);
            log.info("OrderManagementLine : " + ordermangementline);
            return new ResponseEntity<>(ordermangementline, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Streaming
    @ApiOperation(response = OrderManagementLineV2.class, value = "Search OrderManagementLine New") // label for swagger
    @PostMapping("/v2/findOrderManagementLine")
    public Stream<OrderManagementLineV2> findOrderManagementLineV2(@RequestBody SearchOrderManagementLineV2 searchOrderManagementLine)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchOrderManagementLine.getCompanyCodeId(), searchOrderManagementLine.getPlantId(), searchOrderManagementLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return ordermangementlineService.findOrderManagementLineV2(searchOrderManagementLine);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "Create OrderManagementLine") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postOrderManagementLineV2(@Valid @RequestBody OrderManagementLineV2 newOrderManagementLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newOrderManagementLine.getCompanyCodeId(), newOrderManagementLine.getPlantId(), newOrderManagementLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            OrderManagementLineV2 createdOrderManagementLine =
                    ordermangementlineService.createOrderManagementLineV2(newOrderManagementLine, loginUserID);
            return new ResponseEntity<>(createdOrderManagementLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "UnAllocate") // label for swagger
    @PatchMapping("/v2/unallocate/patch")
    public ResponseEntity<?> unallocateOrderManagementLineV2(@Valid @RequestBody List<OrderManagementLineV2> orderManagementLineV2, @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            for (OrderManagementLineV2 orderManagementlineV2 : orderManagementLineV2) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(orderManagementlineV2.getCompanyCodeId(), orderManagementlineV2.getPlantId(), orderManagementlineV2.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            List<OrderManagementLineV2> updatedOrderManagementLine =
                    ordermangementlineService.doUnAllocationV2(orderManagementLineV2, loginUserID);
            return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "UnAllocate") // label for swagger
    @PatchMapping("/v2/unallocate")
    public ResponseEntity<?> unallocateOrderManagementLineV2(@RequestParam String warehouseId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                             @RequestParam String languageId, @RequestParam String preOutboundNo,
                                                             @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                                             @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackBarCode,
                                                             @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            OrderManagementLineV2 updatedOrderManagementLine =
                    ordermangementlineService.doUnAllocationV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                            itemCode, proposedStorageBin, proposedPackBarCode, loginUserID);
            return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "Allocate")
    @PatchMapping("/v2/allocate/patch")
    public ResponseEntity<?> allocateOrderManagementLineV2(@Valid @RequestBody List<OrderManagementLineV2> orderManagementLineV2,
                                                           @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            for (OrderManagementLineV2 orderManagementlineV : orderManagementLineV2) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(orderManagementlineV.getCompanyCodeId(), orderManagementlineV.getPlantId(), orderManagementlineV.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            List<OrderManagementLineV2> updatedOrderManagementLine =
                    ordermangementlineService.doAllocationV2(orderManagementLineV2, loginUserID);
            return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "Allocate") // label for swagger
    @PatchMapping("/v2/allocate")
    public ResponseEntity<?> allocateOrderManagementLineV2(@RequestParam String warehouseId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                           @RequestParam String languageId, @RequestParam String preOutboundNo,
                                                           @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                                           @RequestParam String itemCode, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            OrderManagementLineV2 updatedOrderManagementLine =
                    ordermangementlineService.doAllocationV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                            itemCode, loginUserID);
            return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "Allocate") // label for swagger
    @PatchMapping("/v2/assignPicker")
    public ResponseEntity<?> assignPickerV2(@RequestBody List<AssignPickerV2> assignPicker, @RequestParam String assignedPickerId,
                                            @RequestParam String loginUserID) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(assignPicker.get(0).getCompanyCodeId(), assignPicker.get(0).getPlantId(), assignPicker.get(0).getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<OrderManagementLineV2> updatupdatedOrderManagementLine = null;
            if (routingDb != null) {
                switch (routingDb) {
                    case "FAHAHEEL":
                    case "AUTO_LAP":
                        updatupdatedOrderManagementLine = ordermangementlineService.doAssignPickerV2(assignPicker,assignedPickerId,loginUserID);
                        break;
                    case "REEFERON":
                        updatupdatedOrderManagementLine = ordermangementlineService.doAssignPickerV5(assignPicker, assignedPickerId, loginUserID);
                        break;
                    case "NAMRATHA":
                        updatupdatedOrderManagementLine = ordermangementlineService.doAssignPickerV2(assignPicker, assignedPickerId, loginUserID);
                        break;
                    case "KNOWELL":
                        updatupdatedOrderManagementLine = ordermangementlineService.doAssignPickerV7(assignPicker, assignedPickerId, loginUserID);
                        break;
//                        default:
//                            updatupdatedOrderManagementLine = ordermangementlineService.doAssignPickerV2(assignPicker, assignedPickerId, loginUserID);
                }
            }
            return new ResponseEntity<>(updatupdatedOrderManagementLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "Update OrderMangementLine") // label for swagger
    @PatchMapping("/v2/{refDocNumber}")
    public ResponseEntity<?> patchOrderMangementLineV2(@PathVariable String refDocNumber, @RequestParam String companyCodeId,
                                                       @RequestParam String plantId, @RequestParam String languageId,
                                                       @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                       @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                                       @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackCode,
                                                       @Valid @RequestBody OrderManagementLineV2 updateOrderMangementLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            OrderManagementLine createdOrderMangementLine =
                    ordermangementlineService.updateOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                            partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID, updateOrderMangementLine);
            return new ResponseEntity<>(createdOrderMangementLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "Delete OrderManagementLine") // label for swagger
    @DeleteMapping("/v2/{refDocNumber}")
    public ResponseEntity<?> OrderManagementLineV2(@PathVariable String refDocNumber, @RequestParam String companyCodeId,
                                                   @RequestParam String plantId, @RequestParam String languageId,
                                                   @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                   @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                                   @RequestParam String itemCode, @RequestParam String proposedStorageBin,
                                                   @RequestParam String proposedPackCode, @RequestParam String loginUserID) throws ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            ordermangementlineService.deleteOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                    lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "roll back") // label for swagger
    @PostMapping("/v2/rollBack")
    public ResponseEntity<?> rollBackOutboundOrder(@RequestParam String warehouseId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                   @RequestParam String languageId, @RequestParam String refDocNumber,
                                                   @RequestParam Long outboundOrderTypeId) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            ordermangementlineService.rollback(companyCodeId, plantId, languageId, warehouseId, refDocNumber, outboundOrderTypeId);
            return new ResponseEntity<>(HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //----------------------Update--Ref9&10------------------------------------------------------------
    @ApiOperation(response = OrderManagementLineV2.class, value = "Get a OrderManagementLine") // label for swagger
    @GetMapping("/v2/updateRefFields")
    public ResponseEntity<?> updateRefFieldsV2() {
        ordermangementlineService.updateRef9ANDRef10();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //==========================================================================================================================
    //find Order management line for assignment tab
    @ApiOperation(response = OrderManagementLineImpl.class, value = "Search OrderManagementLineV2 for assignment tab")
    // label for swagger
    @PostMapping("/v2/findOrderManagementLines")
    public List<OrderManagementLineImpl> findOrderManagementLinesV2(@RequestBody SearchOrderManagementLineV2 searchOrderManagementLine) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchOrderManagementLine.getCompanyCodeId(), searchOrderManagementLine.getPlantId(), searchOrderManagementLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return ordermangementlineService.findOrderManagementLinesV2(searchOrderManagementLine);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}