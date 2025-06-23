package com.tekclover.wms.api.outbound.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.PeriodicHeader;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.v2.PeriodicHeaderEntityV2;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.v2.PeriodicLineV2;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.service.PeriodicHeaderService;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.v2.PeriodicHeaderV2;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.v2.SearchPeriodicHeaderV2;
import com.tekclover.wms.api.outbound.transaction.model.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.AddPeriodicHeader;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.PeriodicHeaderEntity;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.PeriodicLineEntity;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.SearchPeriodicHeader;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.UpdatePeriodicHeader;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PeriodicHeader"}, value = "PeriodicHeader  Operations related to PeriodicHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PeriodicHeader ",description = "Operations related to PeriodicHeader ")})
@RequestMapping("/periodicheader")
@RestController
public class PeriodicHeaderController {
	
	@Autowired
    PeriodicHeaderService periodicheaderService;
	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = PeriodicHeader.class, value = "Get all PeriodicHeader details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PeriodicHeaderEntity> periodicheaderList = periodicheaderService.getPeriodicHeaders();
		return new ResponseEntity<>(periodicheaderList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = PeriodicHeader.class, value = "Get a PeriodicHeader") // label for swagger 
	@GetMapping("/{cycleCountNo}")
	public ResponseEntity<?> getPeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, 
		@RequestParam String plantId, @RequestParam String warehouseId, @RequestParam Long cycleCountTypeId) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
    	PeriodicHeader periodicheader = 
    			periodicheaderService.getPeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo);
		return new ResponseEntity<>(periodicheader, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
	@ApiOperation(response = PeriodicHeader.class, value = "Search PeriodicHeader") // label for swagger
	@PostMapping("/findPeriodicHeader")
	public ResponseEntity<?> findPeriodicHeader(@RequestBody SearchPeriodicHeader searchPeriodicHeader)
			throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbList(searchPeriodicHeader.getCompanyCode(), searchPeriodicHeader.getPlantId()
                    , searchPeriodicHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<PeriodicHeaderEntity> page = periodicheaderService.findPeriodicHeader(searchPeriodicHeader);
            return new ResponseEntity<>(page, HttpStatus.OK);
        } finally {
			DataBaseContextHolder.clear();
        }
    }

	//Stream
	@ApiOperation(response = PeriodicHeader.class, value = "Search PeriodicHeader New") // label for swagger
	@PostMapping("/findPeriodicHeaderStream")
	public ResponseEntity<?> findPeriodicHeaderStream(@RequestBody SearchPeriodicHeader searchPeriodicHeader)
			throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbList(searchPeriodicHeader.getCompanyCode(), searchPeriodicHeader.getPlantId()
                    , searchPeriodicHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            Stream<PeriodicHeader> response = periodicheaderService.findPeriodicHeaderStream(searchPeriodicHeader);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }  finally {
			DataBaseContextHolder.clear();
        }
    }


	@ApiOperation(response = Inventory.class, value = "Search Inventory") // label for swagger
	@PostMapping("/run/pagination")
	public ResponseEntity<?> findInventory(@RequestParam String warehouseId, 
			@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "100") Integer pageSize,
			@RequestParam(defaultValue = "itemCode") String sortBy) 
			throws Exception {

		Page<PeriodicLineEntity> periodicLineEntity = 
				periodicheaderService.runPeriodicHeader(warehouseId, pageNo, pageSize, sortBy);
		return new ResponseEntity<>(periodicLineEntity , HttpStatus.OK);
	}
    
    @ApiOperation(response = PeriodicHeader.class, value = "Create PeriodicHeader") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPeriodicHeader(@Valid @RequestBody AddPeriodicHeader newPeriodicHeader, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(newPeriodicHeader.getCompanyCodeId(), newPeriodicHeader.getPalntId(), newPeriodicHeader.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			PeriodicHeaderEntity createdPeriodicHeader =
					periodicheaderService.createPeriodicHeader(newPeriodicHeader, loginUserID);
			return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = PeriodicHeader.class, value = "Update PeriodicHeader") // label for swagger
    @PatchMapping("/{cycleCountNo}")
	public ResponseEntity<?> patchPeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId, 
			@RequestParam Long cycleCountTypeId, @Valid @RequestBody UpdatePeriodicHeader updatePeriodicHeader, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(updatePeriodicHeader.getCompanyCodeId(), updatePeriodicHeader.getPalntId(), warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			PeriodicHeader createdPeriodicHeader =
					periodicheaderService.updatePeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo, loginUserID, updatePeriodicHeader);
			return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = PeriodicHeader.class, value = "Delete PeriodicHeader") // label for swagger
	@DeleteMapping("/{cycleCountNo}")
	public ResponseEntity<?> deletePeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, 
			@RequestParam String plantId, @RequestParam String warehouseId, @RequestParam Long cycleCountTypeId,
			@RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
    	periodicheaderService.deletePeriodicHeader(companyCodeId, plantId, warehouseId, cycleCountTypeId, cycleCountNo, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	//==========================================================V2=======================================================

	@ApiOperation(response = PeriodicHeaderEntityV2.class, value = "Get all PeriodicHeaderV2 details") // label for swagger
	@GetMapping("/v2")
	public ResponseEntity<?> getAllPerpetualHeaderV2() {
		List<PeriodicHeaderEntityV2> periodicHeaderEntity = periodicheaderService.getPeriodicHeadersV2();
		return new ResponseEntity<>(periodicHeaderEntity, HttpStatus.OK);
	}

	@ApiOperation(response = PeriodicHeaderEntityV2.class, value = "Get a PeriodicHeaderV2") // label for swagger
	@GetMapping("/v2/{cycleCountNo}")
	public ResponseEntity<?> getPeriodicHeaderV2(@PathVariable String cycleCountNo, @RequestParam String companyCode, @RequestParam String plantId,
												  @RequestParam String languageId, @RequestParam String warehouseId,
												  @RequestParam Long cycleCountTypeId) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		PeriodicHeaderEntityV2 periodicHeaderV2 =
				periodicheaderService.getPeriodicHeaderWithLineV2(companyCode, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo);
		return new ResponseEntity<>(periodicHeaderV2, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = PeriodicHeaderV2.class, value = "Search PeriodicHeader") // label for swagger
	@PostMapping("/v2/findPeriodicHeader")
	public List<PeriodicHeaderV2> findPeriodicHeader(@RequestBody SearchPeriodicHeaderV2 searchPeriodicHeader)
			throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbList(searchPeriodicHeader.getCompanyCodeId(), searchPeriodicHeader.getPlantId(), searchPeriodicHeader.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			return periodicheaderService.findPeriodicHeaderV2(searchPeriodicHeader);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}

	@ApiOperation(response = PeriodicHeaderEntityV2.class, value = "Search PeriodicHeader") // label for swagger
	@PostMapping("/v2/findPeriodicHeaderEntity")
	public List<PeriodicHeaderEntityV2> findPeriodicHeaderEntity(@RequestBody SearchPeriodicHeaderV2 searchPeriodicHeader)
			throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = null;
            if (searchPeriodicHeader.getCompanyCodeId() != null) {
                routingDb = dbConfigRepository.getDbList(searchPeriodicHeader.getCompanyCodeId(), searchPeriodicHeader.getPlantId(), searchPeriodicHeader.getWarehouseId());
            } else {
				routingDb = dbConfigRepository.getDbList(searchPeriodicHeader.getCompanyCode(), searchPeriodicHeader.getPlantId(), searchPeriodicHeader.getWarehouseId());
			}
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		return periodicheaderService.findPeriodicHeaderEntityV2(searchPeriodicHeader);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = PeriodicHeaderEntityV2.class, value = "Search PeriodicHeader") // label for swagger
	@PostMapping("/v6/findPeriodicHeaderEntity")
	public List<PeriodicHeaderEntityV2> findPeriodicHeaderEntityNamratha(@RequestBody SearchPeriodicHeaderV2 searchPeriodicHeader)
			throws Exception {
		try {
			log.info("searchPeriodicHeader v6 ----> {}", searchPeriodicHeader);
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbList(searchPeriodicHeader.getCompanyCode(), searchPeriodicHeader.getPlantId(), searchPeriodicHeader.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			return periodicheaderService.findPeriodicHeaderEntityNamratha(searchPeriodicHeader);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}

	@ApiOperation(response = PeriodicHeaderEntityV2.class, value = "Create PeriodicHeaderV2") // label for swagger
	@PostMapping("/v2")
	public ResponseEntity<?> postPeriodicHeaderV2(@Valid @RequestBody PeriodicHeaderEntityV2 newPerpetualHeader,
												   @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(newPerpetualHeader.getCompanyCode(), newPerpetualHeader.getPlantId(), newPerpetualHeader.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		PeriodicHeaderEntityV2 createdPeriodicHeader =
				periodicheaderService.createPeriodicHeaderV2(newPerpetualHeader, loginUserID);
		return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
	}
finally {
			DataBaseContextHolder.clear();
		}
		}
//	@ApiOperation(response = PeriodicLineEntity.class, value = "Create PerpetualHeader") // label for swagger
//	@PostMapping("/v2/run")
//	public ResponseEntity<?> postRunPerpetualHeaderV2(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader)
//			throws IllegalAccessException, InvocationTargetException, ParseException {
//		Set<PerpetualLineEntityImpl> inventoryMovements = perpetualheaderService.runPerpetualHeaderNewV2(runPerpetualHeader);
//		return new ResponseEntity<>(inventoryMovements, HttpStatus.OK);
//	}

	@ApiOperation(response = PeriodicHeaderV2.class, value = "Update PeriodicHeader") // label for swagger
	@PatchMapping("/v2/{cycleCountNo}")
	public ResponseEntity<?> patchPeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String companyCode,
												 @RequestParam String plantId, @RequestParam String languageId,
												 @RequestParam String warehouseId, @RequestParam Long cycleCountTypeId,
												 @Valid @RequestBody PeriodicHeaderEntityV2 updatePeriodicHeader, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		PeriodicHeaderV2 createPeriodicHeader =
				periodicheaderService.updatePeriodicHeaderV2(companyCode, plantId, languageId, warehouseId,
						cycleCountTypeId, cycleCountNo, loginUserID, updatePeriodicHeader);
		return new ResponseEntity<>(createPeriodicHeader, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = PeriodicHeaderV2.class, value = "Delete PeriodicHeader") // label for swagger
	@DeleteMapping("/v2/{cycleCountNo}")
	public ResponseEntity<?> deletePeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, @RequestParam String plantId,
													 @RequestParam String languageId, @RequestParam String warehouseId,
													 @RequestParam Long cycleCountTypeId, @RequestParam String loginUserID) throws ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		periodicheaderService.deletePeriodicHeaderV2(
				companyCodeId, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
finally {
			DataBaseContextHolder.clear();
		}
		}


	@ApiOperation(response = PeriodicHeaderEntityV2.class, value = "Create PeriodicHeaderV4") // label for swagger
	@PostMapping("/v4")
	public ResponseEntity<?> postPeriodicHeaderV4(@RequestParam String companyCodeId, @RequestParam String plantId,
												  @RequestParam String languageId, @RequestParam String warehouseId,
												  @Valid @RequestBody List<PeriodicLineV2> periodicLines,
												  @RequestParam String loginUserID) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            PeriodicHeaderEntityV2 createdPeriodicHeader =
                    periodicheaderService.processStockV4(companyCodeId, plantId, languageId, warehouseId, loginUserID, periodicLines);
            return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
        }  finally {
			DataBaseContextHolder.clear();
        }
    }
}