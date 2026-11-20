package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.hhtuser.*;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.HhtUserService;
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
@Api(tags = {"HhtUser"}, value = "HhtUser  Operations related to HhtUserController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "HhtUser ", description = "Operations related to HhtUser ")})
@RequestMapping("/hhtuser")
@RestController
public class HhtUserController {

    @Autowired
    HhtUserService hhtuserService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = HhtUserOutput.class, value = "Get all HhtUser details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<HhtUserOutput> hhtuserList = hhtuserService.getHhtUsers();
        return new ResponseEntity<>(hhtuserList, HttpStatus.OK);
    }

    @ApiOperation(response = HhtUserOutput.class, value = "Get a HhtUser") // label for swagger
    @GetMapping("/{userId}")
    public ResponseEntity<?> getHhtUser(@PathVariable String userId, @RequestParam String warehouseId,
                                        @RequestParam String companyCodeId, @RequestParam String languageId,
                                        @RequestParam String plantId) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            HhtUserOutput hhtuser = hhtuserService.getHhtUser(userId, warehouseId, companyCodeId, plantId, languageId);
            log.info("HhtUser : " + hhtuser);
            return new ResponseEntity<>(hhtuser, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = HhtUser.class, value = "Delete HhtUserId") // label for swagger
    @PostMapping("/delete/list/v2")
    public ResponseEntity<?> deleteHhtUserId(@Valid @RequestBody List<HhtUser> deleteHhtUser, @RequestParam String loginUserID) {
        try {
            for (HhtUser hhtUser : deleteHhtUser) {
                DataBaseContextHolder.setCurrentDb("WK");
                String routingDb = dbConfigRepository.getDbName(hhtUser.getCompanyCodeId(), hhtUser.getPlantId(), hhtUser.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            hhtuserService.deleteHhtUserV2(deleteHhtUser, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = HhtUserOutput.class, value = "Get HhtUsers") // label for swagger
    @GetMapping("/{warehouseId}/hhtUser")
    public ResponseEntity<?> getHhtUser(@PathVariable String warehouseId) {
        List<HhtUserOutput> hhtuser = hhtuserService.getHhtUser(warehouseId);
        log.info("HhtUser : " + hhtuser);
        return new ResponseEntity<>(hhtuser, HttpStatus.OK);
    }

    @ApiOperation(response = HhtUser.class, value = "Create HhtUser") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postHhtUser(@Valid @RequestBody AddHhtUser newHhtUser, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(newHhtUser.getCompanyCodeId(), newHhtUser.getPlantId(), newHhtUser.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            HhtUser createdHhtUser = hhtuserService.createHhtUser(newHhtUser, loginUserID);
            return new ResponseEntity<>(createdHhtUser, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = HhtUser.class, value = "Update HhtUser") // label for swagger
    @PatchMapping("/{userId}")
    public ResponseEntity<?> patchHhtUser(@PathVariable String userId, @RequestParam String warehouseId,
                                          @RequestParam String companyCodeId, @RequestParam String plantId,
                                          @RequestParam String languageId, @Valid @RequestBody UpdateHhtUser updateHhtUser,
                                          @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            HhtUser createdHhtUser =
                    hhtuserService.updateHhtUser(warehouseId, userId, companyCodeId, languageId,
                            plantId, updateHhtUser, loginUserID);
            return new ResponseEntity<>(createdHhtUser, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    @ApiOperation(response = HhtUser.class, value = "Update HttUserId") // label for swagger
    @PostMapping("/update/list/v2")
    public ResponseEntity<?> patchHttUserId(@Valid @RequestBody List<UpdateHhtUser> updateHhtUserV2, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            for (UpdateHhtUser hhtUser : updateHhtUserV2) {
                DataBaseContextHolder.setCurrentDb("WK");
                String routingDb = dbConfigRepository.getDbName(hhtUser.getCompanyCodeId(), hhtUser.getPlantId(), hhtUser.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            List<HhtUser> updatedHhtUserList =
                    hhtuserService.updateHhtUserV2(updateHhtUserV2, loginUserID);
            return new ResponseEntity<>(updatedHhtUserList, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    @ApiOperation(response = HhtUser.class, value = "Delete HhtUser") // label for swagger
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteHhtUser(@PathVariable String userId, @RequestParam String companyCodeId,
                                           @RequestParam String languageId, @RequestParam String plantId,
                                           @RequestParam String warehouseId, @RequestParam String loginUserID) throws ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            hhtuserService.deleteHhtUser(warehouseId, userId, companyCodeId, plantId, languageId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Search
    @ApiOperation(response = HhtUserOutput.class, value = "Find HhtUser") // label for swagger
    @PostMapping("/findHhtUser")
    public ResponseEntity<?> findHhtUser(@Valid @RequestBody FindHhtUser findHhtUser) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbNameList(findHhtUser.getCompanyCodeId(), findHhtUser.getPlantId(), findHhtUser.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<HhtUserOutput> createdHhtUser = hhtuserService.findHhtUser(findHhtUser);
            return new ResponseEntity<>(createdHhtUser, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}