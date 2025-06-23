package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.threepl.servicetypeid.AddServiceTypeId;
import com.tekclover.wms.api.idmaster.model.threepl.servicetypeid.FindServiceTypeId;
import com.tekclover.wms.api.idmaster.model.threepl.servicetypeid.ServiceTypeId;
import com.tekclover.wms.api.idmaster.model.threepl.servicetypeid.UpdateServiceTypeId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.ServiceTypeIdService;
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
@Api(tags={"ServiceTypeId"},value = "ServiceTypeId Operations related to ServiceTypeIdController")
@SwaggerDefinition(tags={@Tag(name="ServiceTypeId",description = "Operations related to ServiceTypeId")})
@RequestMapping("/servicetypeid")
@RestController
public class ServiceTypeIdController {
        @Autowired
        ServiceTypeIdService serviceTypeIdService;
        @Autowired
        DbConfigRepository dbConfigRepository;

        @ApiOperation(response = ServiceTypeId.class, value = "Get all ServiceTypeId details") // label for swagger
        @GetMapping("")
        public ResponseEntity<?> getAll() {
            List<ServiceTypeId> serviceTypeIdList = serviceTypeIdService.getServiceTypeIds();
            return new ResponseEntity<>(serviceTypeIdList, HttpStatus.OK);
        }
        @ApiOperation(response = ServiceTypeId.class, value = "Get a ServiceTypeId") // label for swagger
        @GetMapping("/{serviceTypeId}")
        public ResponseEntity<?> getServiceTypeId(@RequestParam String warehouseId,@RequestParam String moduleId,
                                                  @PathVariable Long serviceTypeId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
            try {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            ServiceTypeId ServiceTypeId =
                    serviceTypeIdService.getServiceTypeId(warehouseId, moduleId, serviceTypeId,companyCodeId,languageId,plantId);
            log.info("ServiceTypeId : " + ServiceTypeId);
            return new ResponseEntity<>(ServiceTypeId, HttpStatus.OK);
        }
            finally {
                DataBaseContextHolder.clear();
            }
            }
        @ApiOperation(response = ServiceTypeId.class, value = "Create ServiceTypeId") // label for swagger
        @PostMapping("")
        public ResponseEntity<?> postServiceTypeId(@Valid @RequestBody AddServiceTypeId newServiceTypeId,
                                                   @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
            try {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(newServiceTypeId.getCompanyCodeId(), newServiceTypeId.getPlantId(), newServiceTypeId.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            ServiceTypeId createdServiceTypeId = serviceTypeIdService.createServiceTypeId(newServiceTypeId, loginUserID);
            return new ResponseEntity<>(createdServiceTypeId, HttpStatus.OK);
        }
            finally {
                DataBaseContextHolder.clear();
            }
            }
        @ApiOperation(response = ServiceTypeId.class, value = "Update ServiceTypeId") // label for swagger
        @PatchMapping("/{serviceTypeId}")
        public ResponseEntity<?> patchServiceTypeId(@RequestParam String warehouseId, @RequestParam String moduleId, @PathVariable Long serviceTypeId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
                                                    @Valid @RequestBody UpdateServiceTypeId updateServiceTypeId, @RequestParam String loginUserID)
                throws IllegalAccessException, InvocationTargetException, ParseException {
            try {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            ServiceTypeId createdServiceTypeId =
                    serviceTypeIdService.updateServiceTypeId(warehouseId, moduleId, serviceTypeId,companyCodeId,languageId,plantId,loginUserID, updateServiceTypeId);
            return new ResponseEntity<>(createdServiceTypeId, HttpStatus.OK);
        }
            finally {
                DataBaseContextHolder.clear();
            }
            }
        @ApiOperation(response = ServiceTypeId.class, value = "Delete ServiceTypeId") // label for swagger
        @DeleteMapping("/{serviceTypeId}")
        public ResponseEntity<?> deleteServiceTypeId(@PathVariable Long serviceTypeId,
                                                     @RequestParam String warehouseId, @RequestParam String moduleId,@RequestParam String companyCodeId,
                                                     @RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID) {
            try {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            serviceTypeIdService.deleteServiceTypeId(warehouseId, moduleId, serviceTypeId,companyCodeId,languageId,plantId,loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
            finally {
                DataBaseContextHolder.clear();
            }
            }
    //Search
    @ApiOperation(response = ServiceTypeId.class, value = "Find ServiceTypeId") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findServiceTypeId(@Valid @RequestBody FindServiceTypeId findServiceTypeId) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findServiceTypeId.getCompanyCodeId(), findServiceTypeId.getPlantId(), findServiceTypeId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<ServiceTypeId> createdServiceTypeId = serviceTypeIdService.findServiceTypeId(findServiceTypeId);
        return new ResponseEntity<>(createdServiceTypeId, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
}
