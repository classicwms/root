package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.moduleid.*;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.ModuleIdService;
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
@Api(tags = {"ModuleId"}, value = "ModuleId  Operations related to ModuleIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ModuleId ",description = "Operations related to ModuleId ")})
@RequestMapping("/moduleid")
@RestController
public class ModuleIdController {

	@Autowired
	ModuleIdService moduleidService;

	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = ModuleId.class, value = "Get all ModuleId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ModuleId> moduleidList = moduleidService.getModuleIds();
		return new ResponseEntity<>(moduleidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ModuleId.class, value = "Get a ModuleId") // label for swagger 
	@GetMapping("/{moduleId}")
	public ResponseEntity<?> getModuleIds(@PathVariable String moduleId,
			@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			List<ModuleId> moduleid =
					moduleidService.getModuleIdList(warehouseId, moduleId, companyCodeId, languageId, plantId);
			log.info("ModuleId : " + moduleid);
			return new ResponseEntity<>(moduleid, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
    /*@ApiOperation(response = ModuleId.class, value = "Create ModuleId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postModuleId(@Valid @RequestBody AddModuleId newModuleId,
										  @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		ModuleId createdModuleId = moduleidService.createModuleId(newModuleId, loginUserID);
		return new ResponseEntity<>(createdModuleId , HttpStatus.OK);
	} */

	@ApiOperation(response = ModuleId.class, value = "Create ModuleId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postModuleId(@Valid @RequestBody List<AddModuleId> newModuleId,
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			for(AddModuleId moduleIds : newModuleId) {
				DataBaseContextHolder.setCurrentDb("IMF");
				String routingDb = dbConfigRepository.getDbName(moduleIds.getCompanyCodeId(), moduleIds.getPlantId(), moduleIds.getWarehouseId());
				log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
				DataBaseContextHolder.clear();
				DataBaseContextHolder.setCurrentDb(routingDb);
			}
			List<ModuleId> createdModuleId = moduleidService.createModuleId(newModuleId, loginUserID);
			return new ResponseEntity<>(createdModuleId, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
  /*  @ApiOperation(response = ModuleId.class, value = "Update ModuleId") // label for swagger
    @PatchMapping("/{moduleId}")
	public ResponseEntity<?> patchModuleId(@PathVariable String moduleId,
			@RequestParam String warehouseId, @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody UpdateModuleId updateModuleId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ModuleId createdModuleId = 
				moduleidService.updateModuleId(warehouseId, moduleId,companyCodeId,languageId,plantId,loginUserID, updateModuleId);
		return new ResponseEntity<>(createdModuleId , HttpStatus.OK);
	}*/

	@ApiOperation(response = ModuleId.class, value = "Update ModuleId") // label for swagger
    @PatchMapping("/{moduleId}")
	public ResponseEntity<?> patchModuleId(@PathVariable String moduleId,
			@RequestParam String warehouseId, @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody List<UpdateModuleId> updateModuleId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			List<ModuleId> createdModuleId =
					moduleidService.updateModuleId(warehouseId, moduleId, companyCodeId, languageId, plantId, loginUserID, updateModuleId);
			return new ResponseEntity<>(createdModuleId, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = ModuleId.class, value = "Delete ModuleId") // label for swagger
	@DeleteMapping("/{moduleId}")
	public ResponseEntity<?> deleteModuleId(@PathVariable String moduleId,
			@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,
											@RequestParam String plantId,@RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			moduleidService.deleteModuleId(warehouseId, moduleId, companyCodeId, languageId, plantId, loginUserID);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}

	//Search
	@ApiOperation(response = ModuleId.class, value = "Find ModuleId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findModuleId(@Valid @RequestBody FindModuleId findModuleId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(findModuleId.getCompanyCodeId(), findModuleId.getPlantId(), findModuleId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			List<ModuleId> createdUomId = moduleidService.findModuleId(findModuleId);
			return new ResponseEntity<>(createdUomId, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}

}