package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.threepl.paymenttermid.AddPaymentTermId;
import com.tekclover.wms.api.idmaster.model.threepl.paymenttermid.FindPaymentTermId;
import com.tekclover.wms.api.idmaster.model.threepl.paymenttermid.PaymentTermId;
import com.tekclover.wms.api.idmaster.model.threepl.paymenttermid.UpdatePaymentTermId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.PaymentTermIdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
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
@Api(tags={"PaymentTermId"},value = "PaymentTermId Operations related to PaymentTermIdController")
@SwaggerDefinition(tags={@Tag(name="PaymentTermId",description = "Operations related to PaymentTermId")})
@RequestMapping("/paymenttermid")
@RestController
public class PaymentTermIdController {
    @Autowired
    PaymentTermIdService paymentTermIdService;

    @Autowired
    DbConfigRepository dbConfigRepository;
    @ApiOperation(response = PaymentTermId.class, value = "Get all PaymentTermId details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<PaymentTermId> PaymentTermIdList = paymentTermIdService.getPaymentTermIds();
        return new ResponseEntity<>(PaymentTermIdList, HttpStatus.OK);
    }
    @ApiOperation(response = PaymentTermId.class, value = "Get a PaymentTermId") // label for swagger
    @GetMapping("/{paymentTermId}")
    public ResponseEntity<?> getPaymentTermId(@RequestParam String warehouseId, @PathVariable Long paymentTermId,@RequestParam String companyCodeId,
                                              @RequestParam String languageId,@RequestParam String plantId) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        PaymentTermId PaymentTermId =
                paymentTermIdService.getPaymentTermId(warehouseId,paymentTermId,companyCodeId,languageId,plantId);
        log.info("PaymentTermId : " + PaymentTermId);
        return new ResponseEntity<>(PaymentTermId, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
    @ApiOperation(response = PaymentTermId.class, value = "Create PaymentTermId") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPaymentTermId(@Valid @RequestBody AddPaymentTermId newPaymentTermId,
                                           @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newPaymentTermId.getCompanyCodeId(), newPaymentTermId.getPlantId(), newPaymentTermId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        PaymentTermId createdPaymentTermId = paymentTermIdService.createPaymentTermId(newPaymentTermId, loginUserID);
        return new ResponseEntity<>(createdPaymentTermId , HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
    @ApiOperation(response = PaymentTermId.class, value = "Update PaymentTermId") // label for swagger
    @PatchMapping("/{paymentTermId}")
    public ResponseEntity<?> patchPaymentTermId(@RequestParam String warehouseId, @PathVariable Long paymentTermId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
                                                @Valid @RequestBody UpdatePaymentTermId updatePaymentTermId, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        PaymentTermId createdPaymentTermId =
                paymentTermIdService.updatePaymentTermId(warehouseId, paymentTermId,companyCodeId,languageId,plantId,loginUserID, updatePaymentTermId);
        return new ResponseEntity<>(createdPaymentTermId , HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
    @ApiOperation(response = PaymentTermId.class, value = "Delete PaymentTermId") // label for swagger
    @DeleteMapping("/{paymentTermId}")
    public ResponseEntity<?> deletePaymentTermId(@PathVariable Long paymentTermId,
                                             @RequestParam String warehouseId,@RequestParam String companyCodeId,
                                                 @RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        paymentTermIdService.deletePaymentTermId(warehouseId,paymentTermId,companyCodeId,languageId,plantId,loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
    //Search
    @ApiOperation(response = PaymentTermId.class, value = "Find PaymentTermId") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findPaymentTermId(@Valid @RequestBody FindPaymentTermId findPaymentTermId) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findPaymentTermId.getCompanyCodeId(), findPaymentTermId.getPlantId(), findPaymentTermId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        List<PaymentTermId> createdPaymentTermId = paymentTermIdService.findPaymentTermId(findPaymentTermId);
        return new ResponseEntity<>(createdPaymentTermId, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
}
