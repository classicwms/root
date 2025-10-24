package com.tekclover.wms.api.outbound.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.PeriodicLine;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.PeriodicUpdateResponse;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.v2.PeriodicLineV2;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.v2.PeriodicUpdateResponseV2;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.v2.SearchPeriodicLineV2;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.AssignHHTUserCC;
import com.tekclover.wms.api.outbound.transaction.model.mnc.ExecuteStockCountInput;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.service.PeriodicLineService;
import com.tekclover.wms.api.outbound.transaction.service.PeriodicZeroStkLineService;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.v2.PeriodicZeroStockLine;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.WarehouseApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.SearchPeriodicLine;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.UpdatePeriodicLine;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PeriodicLine"}, value = "PeriodicLine  Operations related to PeriodicLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PeriodicLine ",description = "Operations related to PeriodicLine ")})
@RequestMapping("/periodicline")
@RestController
public class PeriodicLineController {
	
	@Autowired
    PeriodicLineService periodicLineService;

	@Autowired
    PeriodicZeroStkLineService periodicZeroStkLineService;

	@Autowired
	DbConfigRepository dbConfigRepository;
	
	@ApiOperation(response = PeriodicLine.class, value = "SearchPeriodicLine") // label for swagger
	@PostMapping("/findPeriodicLine")
	public List<PeriodicLine> findPeriodicLine (@RequestBody SearchPeriodicLine searchPeriodicLine)
			throws Exception {
		return periodicLineService.findPeriodicLine (searchPeriodicLine);
	}

	//Stream
	@ApiOperation(response = PeriodicLine.class, value = "SearchPeriodicLine Stream") // label for swagger
	@PostMapping("/findPeriodicLineStream")
	public Stream<PeriodicLine> findPeriodicLineStream (@RequestBody SearchPeriodicLine searchPeriodicLine)
			throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(searchPeriodicLine.getCompanyCode(), searchPeriodicLine.getPlantId(), searchPeriodicLine.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		return periodicLineService.findPeriodicLineStream (searchPeriodicLine);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	
	@ApiOperation(response = PeriodicLine[].class, value = "AssignHHTUser") // label for swagger
    @PatchMapping("/assigingHHTUser")
	public ResponseEntity<?> patchAssingHHTUser (@RequestBody List<AssignHHTUserCC> assignHHTUser,
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		try {
			for (AssignHHTUserCC assignHHTUserc : assignHHTUser) {
				DataBaseContextHolder.setCurrentDb("MT");
				String routingDb = dbConfigRepository.getDbName(assignHHTUserc.getCompanyCodeId(), assignHHTUserc.getPlantId(), assignHHTUserc.getWarehouseId());
				log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
				DataBaseContextHolder.clear();
				DataBaseContextHolder.setCurrentDb(routingDb);
			}
			List<PeriodicLine> updatedPeriodicLine = periodicLineService.updateAssingHHTUser(assignHHTUser, loginUserID);
			return new ResponseEntity<>(updatedPeriodicLine, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}

	@ApiOperation(response = PeriodicLineV2[].class, value = "AssignHHTUser") // label for swagger
	@PatchMapping("/v6/assigingHHTUser")
	public ResponseEntity<?> patchAssingHHTUserNamratha(@RequestBody List<AssignHHTUserCC> assignHHTUser,
												  @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
        try {
            for (AssignHHTUserCC assignHHTUserc : assignHHTUser) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(assignHHTUserc.getCompanyCodeId(), assignHHTUserc.getPlantId(), assignHHTUserc.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            List<PeriodicLineV2> createdPeriodicLine = periodicLineService.updateAssingHHTUserNamratha(assignHHTUser, loginUserID);
            return new ResponseEntity<>(createdPeriodicLine, HttpStatus.OK);
        }  finally {
			DataBaseContextHolder.clear();
        }
    }
	
	@ApiOperation(response = PeriodicLine.class, value = "Update PeriodicLine") // label for swagger
    @PatchMapping("/{cycleCountNo}")
	public ResponseEntity<?> patchPeriodicLine (@PathVariable String cycleCountNo, 
			@RequestBody List<UpdatePeriodicLine> updatePeriodicLine, @RequestParam String loginUserID) 
					throws IllegalAccessException, InvocationTargetException {
		try {
			log.info("updatePeriodicLine -----> {}", updatePeriodicLine);
			for (UpdatePeriodicLine updatePeriodicLine1:updatePeriodicLine) {
				DataBaseContextHolder.setCurrentDb("MT");
				String routingDb = dbConfigRepository.getDbName(updatePeriodicLine1.getCompanyCode(), updatePeriodicLine1.getPlantId(), updatePeriodicLine1.getWarehouseId());
				log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
				DataBaseContextHolder.clear();
				DataBaseContextHolder.setCurrentDb(routingDb);
			}
			PeriodicUpdateResponse createdPeriodicLine =
					periodicLineService.updatePeriodicLine(cycleCountNo, updatePeriodicLine, loginUserID);
			return new ResponseEntity<>(createdPeriodicLine, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}

	@ApiOperation(response = PeriodicLineV2.class, value = "Update PeriodicLineV2") // label for swagger
	@PatchMapping("/v6/{cycleCountNo}")
	public ResponseEntity<?> patchPeriodicLineV6(@PathVariable String cycleCountNo,
												 @RequestBody List<PeriodicLineV2> updatePeriodicLine,
												 @RequestParam String loginUserID) throws Exception {
        try {
            for (PeriodicLineV2 updatePeriodicLine1:updatePeriodicLine) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(updatePeriodicLine1.getCompanyCode(), updatePeriodicLine1.getPlantId(), updatePeriodicLine1.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            PeriodicUpdateResponseV2 createdPeriodicResponse =
                    periodicLineService.updatePeriodicLineV4(cycleCountNo, updatePeriodicLine, loginUserID);
            return new ResponseEntity<>(createdPeriodicResponse, HttpStatus.OK);
        }  finally {
			DataBaseContextHolder.clear();
        }
    }



	//=========================================================V2===============================================

	@ApiOperation(response = PeriodicLineV2.class, value = "SearchPeriodicLineV2") // label for swagger
	@PostMapping("/v2/findPeriodicLine")
	public List<PeriodicLineV2> findPeriodicLineV2(@RequestBody SearchPeriodicLineV2 searchPeriodicLineV2)
			throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbList(searchPeriodicLineV2.getCompanyCode(), searchPeriodicLineV2.getPlantId(), searchPeriodicLineV2.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		return periodicLineService.findPeriodicLineStreamV2(searchPeriodicLineV2);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}

	@ApiOperation(response = PeriodicLineV2[].class, value = "AssignHHTUser") // label for swagger
	@PatchMapping("/v2/assigingHHTUser")
	public ResponseEntity<?> patchAssingHHTUserV2(@RequestBody List<AssignHHTUserCC> assignHHTUser,
												  @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		try {
			for (AssignHHTUserCC assignHHTUsercc : assignHHTUser) {
				DataBaseContextHolder.setCurrentDb("MT");
				String routingDb = dbConfigRepository.getDbName(assignHHTUsercc.getCompanyCodeId(), assignHHTUsercc.getPlantId(), assignHHTUsercc.getWarehouseId());
				log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
				DataBaseContextHolder.clear();
				DataBaseContextHolder.setCurrentDb(routingDb);
			}
		List<PeriodicLineV2> createdPeriodicLine = periodicLineService.updateAssingHHTUserV2(assignHHTUser, loginUserID);
		return new ResponseEntity<>(createdPeriodicLine, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = PeriodicLineV2.class, value = "Update PeriodicLineV2") // label for swagger
	@PatchMapping("/v2/{cycleCountNo}")
	public ResponseEntity<?> patchPeriodicLineV2(@PathVariable String cycleCountNo,
												  @RequestBody List<PeriodicLineV2> updatePeriodicLine, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			for (PeriodicLineV2 updatePeriodicline :updatePeriodicLine) {
				DataBaseContextHolder.setCurrentDb("MT");
				String routingDb = dbConfigRepository.getDbName(updatePeriodicline.getCompanyCode(), updatePeriodicline.getPlantId(), updatePeriodicline.getWarehouseId());
				log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
				DataBaseContextHolder.clear();
				DataBaseContextHolder.setCurrentDb(routingDb);
			}
		PeriodicUpdateResponseV2 createdPeriodicResponse =
				periodicLineService.updatePeriodicLineV2(cycleCountNo, updatePeriodicLine, loginUserID);
		return new ResponseEntity<>(createdPeriodicResponse, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = WarehouseApiResponse.class, value = "Update PeriodicLine") // label for swagger
	@PatchMapping("/v2/confirm/{cycleCountNo}")
	public ResponseEntity<?> patchPeriodicLineConfirmV2(@PathVariable String cycleCountNo,
														 @RequestBody List<PeriodicLineV2> updatePerpetualLine, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		try {
			for (PeriodicLineV2 updatePerpetualline :updatePerpetualLine) {
				DataBaseContextHolder.setCurrentDb("MT");
				String routingDb = dbConfigRepository.getDbName(updatePerpetualline.getCompanyCode(), updatePerpetualline.getPlantId(), updatePerpetualline.getWarehouseId());
				log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
				DataBaseContextHolder.clear();
				DataBaseContextHolder.setCurrentDb(routingDb);
			}
		WarehouseApiResponse createdPerpetualLine =
				periodicLineService.updatePeriodicLineConfirmV2(cycleCountNo, updatePerpetualLine, loginUserID);
		return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}


	//=================================================Zero Stock to Create Inventory===========================================================
	@ApiOperation(response = PeriodicLineV2.class, value = "Update PeriodicLine Zero Stock") // label for swagger
	@PostMapping("/v2/createPeriodicFromZeroStk")
	public ResponseEntity<?> createPeriodicLineV2(@RequestBody List<PeriodicZeroStockLine> updatePeriodicLine, @RequestParam String loginUserID) {
		try {
			for (PeriodicZeroStockLine updatePeriodicline :updatePeriodicLine) {
				DataBaseContextHolder.setCurrentDb("MT");
				String routingDb = dbConfigRepository.getDbName(updatePeriodicline.getCompanyCode(), updatePeriodicline.getPlantId(), updatePeriodicline.getWarehouseId());
				log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
				DataBaseContextHolder.clear();
				DataBaseContextHolder.setCurrentDb(routingDb);
			}
		List<PeriodicLineV2> createdPeriodicLine =
				periodicZeroStkLineService.updatePeriodicZeroStkLine(updatePeriodicLine, loginUserID);
		return new ResponseEntity<>(createdPeriodicLine, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}


	//=================================================PeriodicLine V4 Confirm =========================================//
//	@ApiOperation(response = WarehouseApiResponse.class, value = "confirm PeriodicLines") // label for swagger
//	@PatchMapping("/v4/confirm/{cycleCountNo}")
//	public ResponseEntity<?> patchPeriodicLineConfirmV4(@PathVariable String cycleCountNo, @RequestBody List<PeriodicLineV2> updatePerpetualLine,
//														@RequestParam String loginUserID) throws Exception {
//
////		for (PeriodicLineV2 updatePerpetualline :updatePerpetualLine) {
//		DataBaseContextHolder.setCurrentDb("MT");
//		String routingDb = dbConfigRepository.getDbName(updatePerpetualLine.get(0).getCompanyCode(), updatePerpetualLine.get(0).getPlantId(), updatePerpetualLine.get(0).getWarehouseId());
//		log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//		DataBaseContextHolder.clear();
//		DataBaseContextHolder.setCurrentDb(routingDb);
////		}
//
//		WarehouseApiResponse createdPerpetualLine = new WarehouseApiResponse();
//
//		if (routingDb != null) {
//			switch (routingDb) {
////				case "REEFERON":
////					createdPerpetualLine =
////							periodicLineService.updatePeriodicLineConfirmV5(cycleCountNo, updatePerpetualLine, loginUserID);
////					break;
//				default:
//					createdPerpetualLine =
//							periodicLineService.updatePeriodicLineConfirmV4(cycleCountNo, updatePerpetualLine, loginUserID);
//			}
//		}

//		WarehouseApiResponse createdPerpetualLine =
//				periodicLineService.updatePeriodicLineConfirmV4(cycleCountNo, updatePerpetualLine, loginUserID);

//		return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
//	}

	@ApiOperation(response = PeriodicLineV2.class, value = "ExecuteStockCount") // label for swagger
	@PostMapping("/v4/executeStockCount")
	public List<PeriodicLineV2> executeStockCount(@RequestParam String companyCodeId, @RequestParam String plantId,
												  @RequestParam String languageId, @RequestParam String warehouseId,
												  @RequestBody ExecuteStockCountInput executeStockCountInput) throws Exception {
		DataBaseContextHolder.setCurrentDb("MT");
		String routingDb = dbConfigRepository.getDbList(executeStockCountInput.getCompanyCodeId(), executeStockCountInput.getPlantId(), executeStockCountInput.getWarehouseId());
		log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
		DataBaseContextHolder.clear();
		DataBaseContextHolder.setCurrentDb(routingDb);
		return periodicLineService.executeStockCount(companyCodeId, plantId, languageId, warehouseId, executeStockCountInput);
	}
}