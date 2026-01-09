package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.menuid.AddMenuId;
import com.tekclover.wms.api.idmaster.model.menuid.FindMenuId;
import com.tekclover.wms.api.idmaster.model.menuid.MenuId;
import com.tekclover.wms.api.idmaster.model.menuid.UpdateMenuId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.MenuIdService;
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
@Api(tags = {"MenuId"}, value = "MenuId  Operations related to MenuIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "MenuId ", description = "Operations related to MenuId ")})
@RequestMapping("/menuid")
@RestController
public class MenuIdController {

    @Autowired
    MenuIdService menuidService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = MenuId.class, value = "Get all MenuId details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<MenuId> menuidList = menuidService.getMenuIds();
        return new ResponseEntity<>(menuidList, HttpStatus.OK);
    }

    @ApiOperation(response = MenuId.class, value = "Get a MenuId") // label for swagger 
    @GetMapping("/{menuId}")
    public ResponseEntity<?> getMenuId(@PathVariable Long menuId, @RequestParam String warehouseId,
                                       @RequestParam Long subMenuId, @RequestParam Long authorizationObjectId, @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            MenuId menuid = menuidService.getMenuId(warehouseId, menuId, subMenuId, authorizationObjectId, companyCodeId, languageId, plantId);
            log.info("MenuId : " + menuid);
            return new ResponseEntity<>(menuid, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

//	@ApiOperation(response = MenuId.class, value = "Search MenuId") // label for swagger
//	@PostMapping("/findMenuId")
//	public List<MenuId> findMenuId(@RequestBody SearchMenuId searchMenuId)
//			throws Exception {
//		return menuidService.findMenuId(searchMenuId);
//	}

    @ApiOperation(response = MenuId.class, value = "Create MenuId") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postMenuId(@Valid @RequestBody AddMenuId newMenuId,
                                        @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(newMenuId.getCompanyCodeId(), newMenuId.getPlantId(), newMenuId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            MenuId createdMenuId = menuidService.createMenuId(newMenuId, loginUserID);
            return new ResponseEntity<>(createdMenuId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //controller-bulk
    @ApiOperation(response = MenuId.class, value = "Create bulk MenuId") // label for swagger
    @PostMapping("/bulk")
    public ResponseEntity<?> postLeadCustomer(@Valid @RequestBody List<AddMenuId> newMenuId,
                                              @RequestParam String loginUserID) throws Exception {
        try {
            for (AddMenuId addMenuId : newMenuId) {
                DataBaseContextHolder.setCurrentDb("WK");
                String routingDb = dbConfigRepository.getDbName(addMenuId.getCompanyCodeId(), addMenuId.getPlantId(), addMenuId.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            List<MenuId> createdMenuId = menuidService.creatMenuIdBulk(newMenuId, loginUserID);
            return new ResponseEntity<>(createdMenuId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = MenuId.class, value = "Update MenuId") // label for swagger
    @PatchMapping("/{menuId}")
    public ResponseEntity<?> patchMenuId(@PathVariable Long menuId, @RequestParam String warehouseId,
                                         @RequestParam Long subMenuId, @RequestParam Long authorizationObjectId,
                                         @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId, @Valid @RequestBody UpdateMenuId updateMenuId,
                                         @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            MenuId createdMenuId =
                    menuidService.updateMenuId(warehouseId, menuId, subMenuId, authorizationObjectId, companyCodeId, languageId, plantId, loginUserID, updateMenuId);
            return new ResponseEntity<>(createdMenuId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = MenuId.class, value = "Delete MenuId") // label for swagger
    @DeleteMapping("/{menuId}")
    public ResponseEntity<?> deleteMenuId(@PathVariable Long menuId, @RequestParam String warehouseId,
                                          @RequestParam Long subMenuId, @RequestParam Long authorizationObjectId, @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId,
                                          @RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            menuidService.deleteMenuId(warehouseId, menuId, subMenuId, authorizationObjectId, companyCodeId, languageId, plantId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Search
    @ApiOperation(response = MenuId.class, value = "Find MenuId") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findMenuId(@Valid @RequestBody FindMenuId findMenuId) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(findMenuId.getCompanyCodeId(), findMenuId.getPlantId(), findMenuId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<MenuId> createdMenuId = menuidService.findMenuId(findMenuId);
            return new ResponseEntity<>(createdMenuId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}