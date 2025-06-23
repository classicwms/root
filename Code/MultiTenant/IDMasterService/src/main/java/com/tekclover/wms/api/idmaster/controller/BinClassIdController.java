package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.binclassid.FindBinClassId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
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

import com.tekclover.wms.api.idmaster.model.binclassid.AddBinClassId;
import com.tekclover.wms.api.idmaster.model.binclassid.BinClassId;
import com.tekclover.wms.api.idmaster.model.binclassid.UpdateBinClassId;
import com.tekclover.wms.api.idmaster.service.BinClassIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"BinClassId"}, value = "BinClassId  Operations related to BinClassIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "BinClassId ",description = "Operations related to BinClassId ")})
@RequestMapping("/binclassid")
@RestController
public class BinClassIdController {

	@Autowired
	BinClassIdService binClassIdService;

	@Autowired
	DbConfigRepository dbConfigRepository;

	@ApiOperation(response = BinClassId.class, value = "Get all BinClassId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<BinClassId> binclassidList = binClassIdService.getBinClassIds();
		return new ResponseEntity<>(binclassidList, HttpStatus.OK);
	}

	@ApiOperation(response = BinClassId.class, value = "Get a BinClassId") // label for swagger
	@GetMapping("/{binClassId}")
	public ResponseEntity<?> getBinClassId(@RequestParam String warehouseId, @PathVariable Long binClassId, @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			BinClassId inhousetransferline = binClassIdService.getBinClassId(warehouseId, binClassId, companyCodeId, languageId, plantId);
			log.info("BinClassId : " + inhousetransferline);
			return new ResponseEntity<>(inhousetransferline, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
	}

	@ApiOperation(response = BinClassId.class, value = "Create BinClassId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postBinClassId(@Valid @RequestBody AddBinClassId newBinClassId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(newBinClassId.getCompanyCodeId(), newBinClassId.getPlantId(), newBinClassId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			BinClassId createdBinClassId = binClassIdService.createBinClassId(newBinClassId, loginUserID);
			return new ResponseEntity<>(createdBinClassId, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
	}

	@ApiOperation(response = BinClassId.class, value = "Update BinClassId") // label for swagger
	@PatchMapping("/{binClassId}")
	public ResponseEntity<?> patchBinClassId(@RequestParam String warehouseId, @PathVariable Long binClassId, @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId,
											 @Valid @RequestBody UpdateBinClassId updateBinClassId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			BinClassId createdBinClassId =
					binClassIdService.updateBinClassId(warehouseId, binClassId, companyCodeId, languageId, plantId, loginUserID, updateBinClassId);
			return new ResponseEntity<>(createdBinClassId, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
	}

	@ApiOperation(response = BinClassId.class, value = "Delete BinClassId") // label for swagger
	@DeleteMapping("/{binClassId}")
	public ResponseEntity<?> deleteBinClassId(@RequestParam String warehouseId, @PathVariable Long binClassId, @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId,
											  @RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			binClassIdService.deleteBinClassId(warehouseId, binClassId, companyCodeId, languageId, plantId, loginUserID);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} finally {
			DataBaseContextHolder.clear();
		}
	}

	//Search
	@ApiOperation(response = BinClassId.class, value = "Find BinClassId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findBinClassId(@Valid @RequestBody FindBinClassId findBinClassId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(findBinClassId.getCompanyCodeId(), findBinClassId.getPlantId(), findBinClassId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<BinClassId> createdBinClassId = binClassIdService.findBinClassId(findBinClassId);
		return new ResponseEntity<>(createdBinClassId, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
}
}