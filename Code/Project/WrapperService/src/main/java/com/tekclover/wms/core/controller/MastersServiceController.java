package com.tekclover.wms.core.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.core.model.masters.AuditLog;
import com.tekclover.wms.core.model.masters.BOM;
import com.tekclover.wms.core.model.masters.BomHeader;
import com.tekclover.wms.core.model.masters.BomLine;
import com.tekclover.wms.core.model.masters.BusinessPartner;
import com.tekclover.wms.core.model.masters.HandlingEquipment;
import com.tekclover.wms.core.model.masters.HandlingUnit;
import com.tekclover.wms.core.model.masters.ImAlternateUom;
import com.tekclover.wms.core.model.masters.ImBasicData1;
import com.tekclover.wms.core.model.masters.ImBasicData2;
import com.tekclover.wms.core.model.masters.ImPacking;
import com.tekclover.wms.core.model.masters.ImPartner;
import com.tekclover.wms.core.model.masters.ImStrategies;
import com.tekclover.wms.core.model.masters.PackingMaterial;
import com.tekclover.wms.core.model.masters.SearchBomHeader;
import com.tekclover.wms.core.model.masters.SearchBusinessPartner;
import com.tekclover.wms.core.model.masters.SearchHandlingEquipment;
import com.tekclover.wms.core.model.masters.SearchHandlingUnit;
import com.tekclover.wms.core.model.masters.SearchImBasicData1;
import com.tekclover.wms.core.model.masters.SearchPackingMaterial;
import com.tekclover.wms.core.model.masters.SearchStorageBin;
import com.tekclover.wms.core.model.masters.StorageBin;
import com.tekclover.wms.core.model.transaction.PaginatedResponse;
import com.tekclover.wms.core.service.MastersService;
import com.tekclover.wms.core.service.RegisterService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/wms-masters-service")
@Api(tags = {"Masters Service"}, value = "Masters Service Operations") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "User",description = "Operations related to Masters Modules")})
public class MastersServiceController { 
	
	@Autowired
	MastersService mastersService;
	
	@Autowired
	RegisterService registerService;
	
	/* -----------------------------MASTERS---BomHeader---------------------------------------------------------------*/
	@ApiOperation(response = BomHeader.class, value = "Get all BomHeaders") // label for swagger
	@GetMapping("/bomheader")
	public ResponseEntity<?> getBomHeaders(@RequestParam String authToken) {
		BomHeader[] parentItemCodeList = mastersService.getBomHeaders(authToken);
		return new ResponseEntity<>(parentItemCodeList, HttpStatus.OK);
	}

	@ApiOperation(response = BomHeader.class, value = "Get a BomHeader") // label for swagger
	@GetMapping("/bomheader/{parentItemCode}")
	public ResponseEntity<?> getBomHeader(@PathVariable String parentItemCode, @RequestParam String warehouseId, @RequestParam String authToken) {
		BomHeader dbBomHeader = mastersService.getBomHeader(warehouseId, parentItemCode, authToken);
		log.info("BomHeader : " + dbBomHeader);
		return new ResponseEntity<>(dbBomHeader, HttpStatus.OK);
	}
	
	@ApiOperation(response = BomHeader.class, value = "Search BomHeader") // label for swagger
	@PostMapping("/bomheader/findBomHeader")
	public BomHeader[] findBomHeader(@RequestBody SearchBomHeader searchBomHeader,
			@RequestParam String authToken) throws Exception {
		return mastersService.findBomHeader(searchBomHeader, authToken);
	}

	@ApiOperation(response = BomHeader.class, value = "Create BomHeader") // label for swagger
	@PostMapping("/bomheader")
	public ResponseEntity<?> postBomHeader(@Valid @RequestBody BomHeader newBomHeader, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		BomHeader createdBomHeader = mastersService.createBomHeader(newBomHeader, loginUserID, authToken);
		return new ResponseEntity<>(createdBomHeader, HttpStatus.OK);
	}

	@ApiOperation(response = BomHeader.class, value = "Update BomHeader") // label for swagger
	@RequestMapping(value = "/bomheader", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchBomHeader(@RequestParam String parentItemCode, @RequestParam String warehouseId, 
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody BomHeader updateBomHeader)
			throws IllegalAccessException, InvocationTargetException {
		BomHeader updatedBomHeader = 
				mastersService.updateBomHeader(warehouseId, parentItemCode, loginUserID, updateBomHeader, authToken);
		return new ResponseEntity<>(updatedBomHeader, HttpStatus.OK);
	}

	@ApiOperation(response = BomHeader.class, value = "Delete BomHeader") // label for swagger
	@DeleteMapping("/bomheader/{parentItemCode}")
	public ResponseEntity<?> deleteBomHeader(@PathVariable String parentItemCode, @RequestParam String warehouseId, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
		mastersService.deleteBomHeader(warehouseId, parentItemCode, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
    /* -----------------------------MASTERS---BUSINESSPARTNER---------------------------------------------------------------*/
	@ApiOperation(response = BusinessPartner.class, value = "Get all BusinessPartners") // label for swagger
	@RequestMapping(value = "/businesspartner", method = RequestMethod.GET)
   	public ResponseEntity<?> getBusinessPartners(@RequestParam String authToken) {
		BusinessPartner[] businesspartner = mastersService.getBusinessPartners(authToken);
    	log.info("BusinessPartner : " + businesspartner);
		return new ResponseEntity<>(businesspartner, HttpStatus.OK); 
	}
    
    @ApiOperation(response = BusinessPartner.class, value = "Get a BusinessPartner") // label for swagger 
	@RequestMapping(value = "/businesspartner/{partnerCode}", method = RequestMethod.GET)
	public ResponseEntity<?> getBusinessPartner(@PathVariable String partnerCode, @RequestParam String authToken) {
    	BusinessPartner businesspartner = mastersService.getBusinessPartner(partnerCode, authToken);
    	log.info("BusinessPartner : " + businesspartner);
		return new ResponseEntity<>(businesspartner, HttpStatus.OK);
	}
    
	@ApiOperation(response = BusinessPartner.class, value = "Search BusinessPartner") // label for swagger
	@PostMapping("/businesspartner/findBusinessPartner")
	public BusinessPartner[] findBusinessPartner(@RequestBody SearchBusinessPartner searchBusinessPartner,
			@RequestParam String authToken) throws Exception {
		return mastersService.findBusinessPartner(searchBusinessPartner, authToken);
	}
    
    @ApiOperation(response = Optional.class, value = "Create BusinessPartner") // label for swagger
    @RequestMapping(value = "/businesspartner", method = RequestMethod.POST)
	public ResponseEntity<?> createBusinessPartner(@RequestBody BusinessPartner newBusinessPartner, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
			BusinessPartner createdBusinessPartner = 
					mastersService.addBusinessPartner(newBusinessPartner, loginUserID, authToken);
		return new ResponseEntity<>(createdBusinessPartner , HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Update BusinessPartner") // label for swagger
    @RequestMapping(value = "/businesspartner/{partnerCode}", method = RequestMethod.PATCH)
	public ResponseEntity<?> updateBusinessPartner(@PathVariable String partnerCode, 
			@RequestBody BusinessPartner updatedBusinessPartner, @RequestParam String loginUserID, @RequestParam String authToken) {			
		BusinessPartner modifiedBusinessPartner = 
				mastersService.updateBusinessPartner(partnerCode, updatedBusinessPartner, loginUserID, authToken);
		return new ResponseEntity<>(modifiedBusinessPartner , HttpStatus.OK);
	}
    
    @ApiOperation(response = BusinessPartner.class, value = "Delete BusinessPartner") // label for swagger
	@RequestMapping(value = "/businesspartner", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteBusinessPartner(@RequestParam String authToken, @RequestParam String loginUserID, 
			@RequestParam String partnerCode) {
    	mastersService.deleteBusinessPartner(partnerCode, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

    /* -----------------------------MASTERS---HANDLINGEQUIPMENT---------------------------------------------------------------*/
	@ApiOperation(response = HandlingEquipment.class, value = "Get all HandlingEquipment") // label for swagger
	@RequestMapping(value = "/handlingequipment", method = RequestMethod.GET)
   	public ResponseEntity<?> getHandlingEquipments(@RequestParam String authToken) {
		HandlingEquipment[] handlingequipment = mastersService.getHandlingEquipments(authToken);
    	log.info("HandlingEquipment : " + handlingequipment);
		return new ResponseEntity<>(handlingequipment, HttpStatus.OK); 
	}
    
    @ApiOperation(response = HandlingEquipment.class, value = "Get a HandlingEquipment") // label for swagger 
	@RequestMapping(value = "/handlingequipment/{handlingEquipmentId}", method = RequestMethod.GET)
	public ResponseEntity<?> getHandlingEquipment(@PathVariable String handlingEquipmentId, @RequestParam String authToken) {
    	HandlingEquipment handlingequipment = mastersService.getHandlingEquipment(handlingEquipmentId, authToken);
    	log.info("HandlingEquipment : " + handlingequipment);
		return new ResponseEntity<>(handlingequipment, HttpStatus.OK);
	}
    
    @ApiOperation(response = HandlingEquipment.class, value = "Get HandlingEquipment by Barcode") // label for swagger 
   	@GetMapping("/handlingequipment/{heBarcode}/barCode")
   	public ResponseEntity<?> getHandlingEquipment(@PathVariable String heBarcode, @RequestParam String warehouseId,
   			@RequestParam String authToken) {
       	HandlingEquipment handlingequipment = mastersService.getHandlingEquipment(warehouseId, heBarcode, authToken);
       	log.info("HandlingEquipment : " + handlingequipment);
   		return new ResponseEntity<>(handlingequipment, HttpStatus.OK);
   	}
    
	@ApiOperation(response = HandlingEquipment.class, value = "Search HandlingEquipment") // label for swagger
	@PostMapping("/handlingequipment/findHandlingEquipment")
	public HandlingEquipment[] findHandlingEquipment(@RequestBody SearchHandlingEquipment searchHandlingEquipment,
			@RequestParam String authToken) throws Exception {
		return mastersService.findHandlingEquipment(searchHandlingEquipment, authToken);
	}
    
    @ApiOperation(response = Optional.class, value = "Create HandlingEquipment") // label for swagger
    @RequestMapping(value = "/handlingequipment", method = RequestMethod.POST)
	public ResponseEntity<?> createHandlingEquipment(@RequestBody HandlingEquipment newHandlingEquipment, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
			HandlingEquipment createdHandlingEquipment = 
					mastersService.addHandlingEquipment(newHandlingEquipment, loginUserID, authToken);
		return new ResponseEntity<>(createdHandlingEquipment , HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Update HandlingEquipment") // label for swagger
      @RequestMapping(value = "/handlingequipment/{handlingEquipmentId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> updateHandlingEquipment(@PathVariable String handlingEquipmentId, 
			@RequestBody HandlingEquipment updatedHandlingEquipment, @RequestParam String loginUserID, 
			@RequestParam String authToken) {			
		HandlingEquipment modifiedHandlingEquipment = 
				mastersService.updateHandlingEquipment(handlingEquipmentId, updatedHandlingEquipment, loginUserID, authToken);
		return new ResponseEntity<>(modifiedHandlingEquipment , HttpStatus.OK);
	}
    
    @ApiOperation(response = HandlingEquipment.class, value = "Delete HandlingEquipment") // label for swagger
	@RequestMapping(value = "/handlingequipment", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteHandlingEquipment(@RequestParam String handlingEquipmentId, @RequestParam String loginUserID,
			@RequestParam String authToken) {
    	mastersService.deleteHandlingEquipment(handlingEquipmentId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

    /* -----------------------------MASTERS---HANDLINGUNIT---------------------------------------------------------------*/
	@ApiOperation(response = HandlingUnit.class, value = "Get all HandlingUnit") // label for swagger
	@RequestMapping(value = "/handlingunit", method = RequestMethod.GET)
   	public ResponseEntity<?> getHandlingUnits(@RequestParam String authToken) {
		HandlingUnit[] handlingunit = mastersService.getHandlingUnits(authToken);
    	log.info("HandlingUnit : " + handlingunit);
		return new ResponseEntity<>(handlingunit, HttpStatus.OK); 
	}
    
    @ApiOperation(response = HandlingUnit.class, value = "Get a HandlingUnit") // label for swagger 
	@RequestMapping(value = "/handlingunit/{handlingUnit}", method = RequestMethod.GET)
	public ResponseEntity<?> getHandlingUnit(@PathVariable String handlingUnit, @RequestParam String authToken) {
    	HandlingUnit handlingunit = mastersService.getHandlingUnit (handlingUnit, authToken);
    	log.info("HandlingUnit : " + handlingunit);
		return new ResponseEntity<>(handlingunit, HttpStatus.OK);
	}
    
	@ApiOperation(response = HandlingUnit.class, value = "Search HandlingUnit") // label for swagger
	@PostMapping("/handlingunit/findHandlingUnit")
	public HandlingUnit[] findHandlingUnit(@RequestBody SearchHandlingUnit searchHandlingUnit,
			@RequestParam String authToken) throws Exception {
		return mastersService.findHandlingUnit(searchHandlingUnit, authToken);
	}
    
    @ApiOperation(response = Optional.class, value = "Create HandlingUnit") // label for swagger
    @RequestMapping(value = "/handlingunit", method = RequestMethod.POST)
	public ResponseEntity<?> createHandlingUnit(@RequestBody HandlingUnit newHandlingUnit, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
			HandlingUnit createdHandlingUnit = mastersService.addHandlingUnit(newHandlingUnit, loginUserID, authToken);
		return new ResponseEntity<>(createdHandlingUnit , HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Update HandlingUnit") // label for swagger
      @RequestMapping(value = "/handlingunit/{handlingUnit}", method = RequestMethod.PATCH)
	public ResponseEntity<?> updateHandlingUnit(@PathVariable String handlingUnit, 
			@RequestBody HandlingUnit updatedHandlingUnit, @RequestParam String loginUserID, 
			@RequestParam String authToken) {		
		HandlingUnit modifiedHandlingUnit = 
				mastersService.updateHandlingUnit(handlingUnit, updatedHandlingUnit, loginUserID, authToken);
		return new ResponseEntity<>(modifiedHandlingUnit , HttpStatus.OK);
	}
    
    @ApiOperation(response = HandlingUnit.class, value = "Delete HandlingUnit") // label for swagger
	@RequestMapping(value = "/handlingunit", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteHandlingUnit(@RequestParam String authToken, @RequestParam String loginUserID, 
			@RequestParam String handlingUnit) {
    	mastersService.deleteHandlingUnit(handlingUnit, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

    /* -----------------------------MASTERS---IMALTERNATEUOM---------------------------------------------------------------*/
	@ApiOperation(response = ImAlternateUom.class, value = "Get all ImAlternateUom") // label for swagger
	@RequestMapping(value = "/imalternateuom", method = RequestMethod.GET)
   	public ResponseEntity<?> getImAlternateUoms(@RequestParam String authToken) {
		ImAlternateUom[] imalternateuom = mastersService.getImAlternateUoms(authToken);
    	log.info("ImAlternateUom : " + imalternateuom);
		return new ResponseEntity<>(imalternateuom, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ImAlternateUom.class, value = "Get a ImAlternateUom") // label for swagger 
	@RequestMapping(value = "/imalternateuom/{alternateUom}", method = RequestMethod.GET)
	public ResponseEntity<?> getImAlternateUom(@PathVariable String alternateUom, @RequestParam String authToken) {
    	ImAlternateUom imalternateuom = mastersService.getImAlternateUom(alternateUom, authToken);
    	log.info("ImAlternateUom : " + imalternateuom);
		return new ResponseEntity<>(imalternateuom, HttpStatus.OK);
	}
    
	@ApiOperation(response = ImBasicData1.class, value = "Search ImBasicData1") // label for swagger
	@PostMapping("/imbasicdata1/findImBasicData1/pagination")
	public PaginatedResponse<ImBasicData1> findImBasicData1(@RequestBody SearchImBasicData1 searchImBasicData1,
			@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "itemCode") String sortBy,
			@RequestParam String authToken) throws Exception {
		return mastersService.findImBasicData11(searchImBasicData1, pageNo, pageSize, sortBy, authToken);
	}
	
	@ApiOperation(response = ImBasicData1.class, value = "Search ImBasicData1") // label for swagger
	@PostMapping("/imbasicdata1/findImBasicData1")
	public ImBasicData1[] findImBasicData1(@RequestBody SearchImBasicData1 searchImBasicData1,
			@RequestParam String authToken) throws Exception {
		return mastersService.findImBasicData1(searchImBasicData1, authToken);
	}
    
    @ApiOperation(response = Optional.class, value = "Create ImAlternateUom") // label for swagger
    @RequestMapping(value = "/imalternateuom", method = RequestMethod.POST)
	public ResponseEntity<?> createImAlternateUom(@RequestBody ImAlternateUom newImAlternateUom, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
			ImAlternateUom createdImAlternateUom = mastersService.addImAlternateUom(newImAlternateUom, loginUserID, authToken);
		return new ResponseEntity<>(createdImAlternateUom , HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Update ImAlternateUom") // label for swagger
      @RequestMapping(value = "/imalternateuom/{alternateUom}", method = RequestMethod.PATCH)
	public ResponseEntity<?> updateImAlternateUom(@PathVariable String alternateUom, 
			@RequestBody ImAlternateUom updatedImAlternateUom, @RequestParam String loginUserID, @RequestParam String authToken) {			
		ImAlternateUom modifiedImAlternateUom = 
				mastersService.updateImAlternateUom(alternateUom, updatedImAlternateUom, loginUserID, authToken);
		return new ResponseEntity<>(modifiedImAlternateUom , HttpStatus.OK);
	}
    
    @ApiOperation(response = ImAlternateUom.class, value = "Delete ImAlternateUom") // label for swagger
	@RequestMapping(value = "/imalternateuom", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteImAlternateUom(@RequestParam String authToken, @RequestParam String loginUserID, 
			@RequestParam String alternateUom) {
    	mastersService.deleteImAlternateUom(alternateUom, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

    /* -----------------------------MASTERS---IMBASICDATA1---------------------------------------------------------------*/
	@ApiOperation(response = ImBasicData1.class, value = "Get all ImBasicData1") // label for swagger
	@RequestMapping(value = "/imbasicdata1", method = RequestMethod.GET)
   	public ResponseEntity<?> getImBasicData1s(@RequestParam String authToken) {
		ImBasicData1[] imbasicdata1 = mastersService.getImBasicData1s(authToken);
    	log.info("ImBasicData1 : " + imbasicdata1);
		return new ResponseEntity<>(imbasicdata1, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ImBasicData1.class, value = "Get a ImBasicData1") // label for swagger 
	@RequestMapping(value = "/imbasicdata1/{itemCode}", method = RequestMethod.GET)
	public ResponseEntity<?> getImBasicData1(@PathVariable String itemCode, @RequestParam String warehouseId, 
			@RequestParam String authToken) {
    	ImBasicData1 imbasicdata1 = mastersService.getImBasicData1(itemCode, warehouseId, authToken);
    	log.info("ImBasicData1 : " + imbasicdata1);
		return new ResponseEntity<>(imbasicdata1, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Create ImBasicData1") // label for swagger
    @RequestMapping(value = "/imbasicdata1", method = RequestMethod.POST)
	public ResponseEntity<?> createImBasicData1(@RequestBody ImBasicData1 newImBasicData1, @RequestParam String loginUserID, 
			@RequestParam String authToken) {
			ImBasicData1 createdImBasicData1 = mastersService.addImBasicData1(newImBasicData1, loginUserID, authToken);
		return new ResponseEntity<>(createdImBasicData1 , HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Update ImBasicData1") // label for swagger
      @RequestMapping(value = "/imbasicdata1/{itemCode}", method = RequestMethod.PATCH)
	public ResponseEntity<?> updateImBasicData1(@PathVariable String itemCode, @RequestParam String warehouseId,
			@RequestBody ImBasicData1 updatedImBasicData1, @RequestParam String loginUserID, 
			@RequestParam String authToken) {		
		ImBasicData1 modifiedImBasicData1 = 
				mastersService.updateImBasicData1(itemCode, warehouseId, updatedImBasicData1, loginUserID, authToken);
		return new ResponseEntity<>(modifiedImBasicData1 , HttpStatus.OK);
	}
    
    @ApiOperation(response = ImBasicData1.class, value = "Delete ImBasicData1") // label for swagger
	@RequestMapping(value = "/imbasicdata1", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteImBasicData1(@RequestParam String authToken, @RequestParam String loginUserID, 
			@RequestParam String uomId) {
    	mastersService.deleteImBasicData1(uomId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

    /* -----------------------------MASTERS---IMBASICDATA2---------------------------------------------------------------*/
	@ApiOperation(response = ImBasicData2.class, value = "Get all ImBasicData2") // label for swagger
	@RequestMapping(value = "/imbasicdata2", method = RequestMethod.GET)
   	public ResponseEntity<?> getImBasicData2s(@RequestParam String authToken) {
		ImBasicData2[] imbasicdata2 = mastersService.getImBasicData2s(authToken);
    	log.info("ImBasicData2 : " + imbasicdata2);
		return new ResponseEntity<>(imbasicdata2, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ImBasicData2.class, value = "Get a ImBasicData2") // label for swagger 
	@RequestMapping(value = "/imbasicdata2/{itemCode}", method = RequestMethod.GET)
	public ResponseEntity<?> getImBasicData2(@PathVariable String itemCode, @RequestParam String authToken) {
    	ImBasicData2 imbasicdata2 = mastersService.getImBasicData2(itemCode, authToken);
    	log.info("ImBasicData2 : " + imbasicdata2);
		return new ResponseEntity<>(imbasicdata2, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Create ImBasicData2") // label for swagger
    @RequestMapping(value = "/imbasicdata2", method = RequestMethod.POST)
	public ResponseEntity<?> createImBasicData2(@RequestBody ImBasicData2 newImBasicData2, @RequestParam String loginUserID, 
			@RequestParam String authToken) {
			ImBasicData2 createdImBasicData2 = mastersService.addImBasicData2(newImBasicData2, loginUserID, authToken);
		return new ResponseEntity<>(createdImBasicData2 , HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Update ImBasicData2") // label for swagger
    @RequestMapping(value = "/imbasicdata2/{itemCode}", method = RequestMethod.PATCH)
	public ResponseEntity<?> updateImBasicData2(@PathVariable String itemCode, 
			@RequestBody ImBasicData2 updatedImBasicData2, @RequestParam String loginUserID, 
			@RequestParam String authToken) {
		ImBasicData2 modifiedImBasicData2 = 
				mastersService.updateImBasicData2 (itemCode, updatedImBasicData2, loginUserID, authToken);
		return new ResponseEntity<>(modifiedImBasicData2 , HttpStatus.OK);
	}
    
    @ApiOperation(response = ImBasicData2.class, value = "Delete ImBasicData2") // label for swagger
	@RequestMapping(value = "/imbasicdata2", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteImBasicData2(@RequestParam String authToken, @RequestParam String loginUserID, 
			@RequestParam String itemCode) {
    	mastersService.deleteImBasicData2(itemCode, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

    /* -----------------------------MASTERS---IMPACKING---------------------------------------------------------------*/
	@ApiOperation(response = ImPacking.class, value = "Get all ImPacking") // label for swagger
	@RequestMapping(value = "/impacking", method = RequestMethod.GET)
   	public ResponseEntity<?> getImPackings(@RequestParam String authToken) {
		ImPacking[] impacking = mastersService.getImPackings(authToken);
    	log.info("ImPacking : " + impacking);
		return new ResponseEntity<>(impacking, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ImPacking.class, value = "Get a ImPacking") // label for swagger 
	@RequestMapping(value = "/impacking/{packingMaterialNo}", method = RequestMethod.GET)
	public ResponseEntity<?> getImPacking(@PathVariable String packingMaterialNo, @RequestParam String authToken) {
    	ImPacking impacking = mastersService.getImPacking(packingMaterialNo, authToken);
    	log.info("ImPacking : " + impacking);
		return new ResponseEntity<>(impacking, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Create ImPacking") // label for swagger
    @RequestMapping(value = "/impacking", method = RequestMethod.POST)
	public ResponseEntity<?> createImPacking(@RequestBody ImPacking newImPacking, @RequestParam String loginUserID, 
			@RequestParam String authToken) {
			ImPacking createdImPacking = mastersService.addImPacking(newImPacking, loginUserID, authToken);
		return new ResponseEntity<>(createdImPacking , HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Update ImPacking") // label for swagger
    @RequestMapping(value = "/impacking/{packingMaterialNo}", method = RequestMethod.PATCH)
	public ResponseEntity<?> updateImPacking(@PathVariable String packingMaterialNo, 
			@RequestBody ImPacking updatedImPacking, @RequestParam String loginUserID, @RequestParam String authToken) {			
		ImPacking modifiedImPacking = 
				mastersService.updateImPacking (packingMaterialNo, updatedImPacking, loginUserID, authToken);
		return new ResponseEntity<>(modifiedImPacking , HttpStatus.OK);
	}
    
    @ApiOperation(response = ImPacking.class, value = "Delete ImPacking") // label for swagger
	@RequestMapping(value = "/impacking", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteImPacking(@RequestParam String authToken, @RequestParam String loginUserID, 
			@RequestParam String packingMaterialNo) {
    	mastersService.deleteImPacking(packingMaterialNo, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

    /* -----------------------------MASTERS---IMPARTNER---------------------------------------------------------------*/
	@ApiOperation(response = ImPartner.class, value = "Get all ImPartner") // label for swagger
	@RequestMapping(value = "/impartner", method = RequestMethod.GET)
   	public ResponseEntity<?> getImPartners(@RequestParam String authToken) {
		ImPartner[] impartner = mastersService.getImPartners(authToken);
    	log.info("ImPartner : " + impartner);
		return new ResponseEntity<>(impartner, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ImPartner.class, value = "Get a ImPartner") // label for swagger 
	@RequestMapping(value = "/impartner/{businessPartnerCode}", method = RequestMethod.GET)
	public ResponseEntity<?> getImPartner(@PathVariable String businessPartnerCode, @RequestParam String authToken) {
    	ImPartner impartner = mastersService.getImPartner(businessPartnerCode, authToken);
    	log.info("ImPartner : " + impartner);
		return new ResponseEntity<>(impartner, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Create ImPartner") // label for swagger
    @RequestMapping(value = "/impartner", method = RequestMethod.POST)
	public ResponseEntity<?> createImPartner(@RequestBody ImPartner newImPartner, @RequestParam String loginUserID, 
			@RequestParam String authToken) {
			ImPartner createdImPartner = mastersService.addImPartner(newImPartner, loginUserID, authToken);
		return new ResponseEntity<>(createdImPartner , HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Update ImPartner") // label for swagger
    @RequestMapping(value = "/impartner/{businessPartnerCode}", method = RequestMethod.PATCH)
	public ResponseEntity<?> updateImPartner(@PathVariable String businessPartnerCode, 
			@RequestBody ImPartner updatedImPartner, @RequestParam String loginUserID, @RequestParam String authToken) {		
		ImPartner modifiedImPartner = 
				mastersService.updateImPartner(businessPartnerCode, updatedImPartner, loginUserID, authToken);
		return new ResponseEntity<>(modifiedImPartner , HttpStatus.OK);
	}
    
    @ApiOperation(response = ImPartner.class, value = "Delete ImPartner") // label for swagger
	@RequestMapping(value = "/impartner", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteImPartner(@RequestParam String authToken, @RequestParam String loginUserID, 
			@RequestParam String businessPartnerCode) {
    	mastersService.deleteImPartner(businessPartnerCode, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

    /* -----------------------------MASTERS---IMSTRATEGIES---------------------------------------------------------------*/
	@ApiOperation(response = ImStrategies.class, value = "Get all ImStrategies") // label for swagger
	@RequestMapping(value = "/imstrategies", method = RequestMethod.GET)
   	public ResponseEntity<?> getImStrategiess(@RequestParam String authToken) {
		ImStrategies[] imstrategies = mastersService.getImStrategiess(authToken);
    	log.info("ImStrategies : " + imstrategies);
		return new ResponseEntity<>(imstrategies, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ImStrategies.class, value = "Get a ImStrategies") // label for swagger 
	@RequestMapping(value = "/imstrategies/{strategeyTypeId}", method = RequestMethod.GET)
	public ResponseEntity<?> getImStrategies(@PathVariable String strategeyTypeId, @RequestParam String authToken) {
    	ImStrategies imstrategies = mastersService.getImStrategies(strategeyTypeId, authToken);
    	log.info("ImStrategies : " + imstrategies);
		return new ResponseEntity<>(imstrategies, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Create ImStrategies") // label for swagger
    @RequestMapping(value = "/imstrategies", method = RequestMethod.POST)
	public ResponseEntity<?> createImStrategies(@RequestBody ImStrategies newImStrategies, @RequestParam String loginUserID, 
			@RequestParam String authToken) {
			ImStrategies createdImStrategies = mastersService.addImStrategies(newImStrategies, loginUserID, authToken);
		return new ResponseEntity<>(createdImStrategies , HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Update ImStrategies") // label for swagger
      @RequestMapping(value = "/imstrategies/{strategeyTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> updateImStrategies(@PathVariable String strategeyTypeId, 
			@RequestBody ImStrategies updatedImStrategies, @RequestParam String loginUserID, @RequestParam String authToken) {
		ImStrategies modifiedImStrategies = 
				mastersService.updateImStrategies(strategeyTypeId, updatedImStrategies, loginUserID, authToken);
		return new ResponseEntity<>(modifiedImStrategies , HttpStatus.OK);
	}
    
    @ApiOperation(response = ImStrategies.class, value = "Delete ImStrategies") // label for swagger
	@RequestMapping(value = "/imstrategies", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteImStrategies(@RequestParam String authToken, @RequestParam String loginUserID, 
			@RequestParam String strategeyTypeId) {
    	mastersService.deleteImStrategies(strategeyTypeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

    /* -----------------------------MASTERS---PACKINGMATERIAL---------------------------------------------------------------*/
	@ApiOperation(response = PackingMaterial.class, value = "Get all PackingMaterial") // label for swagger
	@RequestMapping(value = "/packingmaterial", method = RequestMethod.GET)
   	public ResponseEntity<?> getPackingMaterials(@RequestParam String authToken) {
		PackingMaterial[] packingmaterial = mastersService.getPackingMaterials(authToken);
    	log.info("PackingMaterial : " + packingmaterial);
		return new ResponseEntity<>(packingmaterial, HttpStatus.OK); 
	}
    
    @ApiOperation(response = PackingMaterial.class, value = "Get a PackingMaterial") // label for swagger 
	@RequestMapping(value = "/packingmaterial/{packingMaterialNo}", method = RequestMethod.GET)
	public ResponseEntity<?> getPackingMaterial(@PathVariable String packingMaterialNo, @RequestParam String authToken) {
    	PackingMaterial packingmaterial = mastersService.getPackingMaterial(packingMaterialNo, authToken);
    	log.info("PackingMaterial : " + packingmaterial);
		return new ResponseEntity<>(packingmaterial, HttpStatus.OK);
	}
    
	@ApiOperation(response = PackingMaterial.class, value = "Search PackingMaterial") // label for swagger
	@PostMapping("/packingmaterial/findPackingMaterial")
	public PackingMaterial[] findPackingMaterial(@RequestBody SearchPackingMaterial searchPackingMaterial,
			@RequestParam String authToken) throws Exception {
		return mastersService.findPackingMaterial(searchPackingMaterial, authToken);
	}
    
    @ApiOperation(response = Optional.class, value = "Create PackingMaterial") // label for swagger
    @RequestMapping(value = "/packingmaterial", method = RequestMethod.POST)
	public ResponseEntity<?> createPackingMaterial(@RequestBody PackingMaterial newPackingMaterial, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
			PackingMaterial createdPackingMaterial = 
					mastersService.addPackingMaterial(newPackingMaterial, loginUserID, authToken);
		return new ResponseEntity<>(createdPackingMaterial , HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Update PackingMaterial") // label for swagger
      @RequestMapping(value = "/packingmaterial/{packingMaterialNo}", method = RequestMethod.PATCH)
	public ResponseEntity<?> updatePackingMaterial(@PathVariable String packingMaterialNo, 
			@RequestBody PackingMaterial updatedPackingMaterial, @RequestParam String loginUserID, 
			@RequestParam String authToken) {			
		PackingMaterial modifiedPackingMaterial = 
				mastersService.updatePackingMaterial(packingMaterialNo, updatedPackingMaterial, loginUserID, authToken);
		return new ResponseEntity<>(modifiedPackingMaterial , HttpStatus.OK);
	}
    
    @ApiOperation(response = PackingMaterial.class, value = "Delete PackingMaterial") // label for swagger
	@RequestMapping(value = "/packingmaterial", method = RequestMethod.DELETE)
	public ResponseEntity<?> deletePackingMaterial(@RequestParam String authToken, @RequestParam String loginUserID, 
			@RequestParam String packingMaterialNo) {
    	mastersService.deletePackingMaterial(packingMaterialNo, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

    /* -----------------------------MASTERS---STORAGEBIN---------------------------------------------------------------*/
	@ApiOperation(response = StorageBin.class, value = "Get all StorageBin") // label for swagger
	@RequestMapping(value = "/storagebin", method = RequestMethod.GET)
   	public ResponseEntity<?> getStorageBins(@RequestParam String authToken) {
		StorageBin[] storagebin = mastersService.getStorageBins(authToken);
    	log.info("StorageBin : " + storagebin);
		return new ResponseEntity<>(storagebin, HttpStatus.OK); 
	}
    
    @ApiOperation(response = StorageBin.class, value = "Get a StorageBin") // label for swagger 
	@RequestMapping(value = "/storagebin/{storageBin}", method = RequestMethod.GET)
	public ResponseEntity<?> getStorageBin(@PathVariable String storageBin, @RequestParam String authToken) {
    	StorageBin storagebin = mastersService.getStorageBin (storageBin, authToken);
    	log.info("StorageBin : " + storagebin);
		return new ResponseEntity<>(storagebin, HttpStatus.OK);
	}
    
	@ApiOperation(response = StorageBin.class, value = "Search StorageBin") // label for swagger
	@PostMapping("/storagebin/findStorageBin")
	public StorageBin[] findStorageBin(@RequestBody SearchStorageBin searchStorageBin,
			@RequestParam String authToken) throws Exception {
		return mastersService.findStorageBin(searchStorageBin, authToken);
	}
    
    @ApiOperation(response = Optional.class, value = "Create StorageBin") // label for swagger
    @RequestMapping(value = "/storagebin", method = RequestMethod.POST)
	public ResponseEntity<?> createStorageBin(@RequestBody StorageBin newStorageBin, @RequestParam String loginUserID, 
			@RequestParam String authToken) {
			StorageBin createdStorageBin = mastersService.addStorageBin(newStorageBin, loginUserID, authToken);
		return new ResponseEntity<>(createdStorageBin , HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Update StorageBin") // label for swagger
    @RequestMapping(value = "/storagebin/{storageBin}", method = RequestMethod.PATCH)
	public ResponseEntity<?> updateStorageBin(@PathVariable String storageBin, 
			@RequestBody StorageBin updatedStorageBin, @RequestParam String loginUserID, @RequestParam String authToken) {			
		StorageBin modifiedStorageBin = mastersService.updateStorageBin(storageBin, updatedStorageBin, loginUserID, authToken);
		return new ResponseEntity<>(modifiedStorageBin , HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageBin.class, value = "Delete StorageBin") // label for swagger
	@RequestMapping(value = "/storagebin", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteStorageBin(@RequestParam String authToken, @RequestParam String loginUserID, 
			@RequestParam String storageBin) {
    	mastersService.deleteStorageBin(storageBin, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

    /* -----------------------------MASTERS---AUDITLOG---------------------------------------------------------------*/
	@ApiOperation(response = AuditLog.class, value = "Get all AuditLog") // label for swagger
	@RequestMapping(value = "/auditlog", method = RequestMethod.GET)
   	public ResponseEntity<?> getAuditLogs(@RequestParam String authToken) {
		AuditLog[] auditlog = mastersService.getAuditLogs(authToken);
    	log.info("AuditLog : " + auditlog);
		return new ResponseEntity<>(auditlog, HttpStatus.OK); 
	}
    
    @ApiOperation(response = AuditLog.class, value = "Get a AuditLog") // label for swagger 
	@RequestMapping(value = "/auditlog/{auditFileNumber}", method = RequestMethod.GET)
	public ResponseEntity<?> getAuditLog(@PathVariable String auditFileNumber, @RequestParam String authToken) {
    	AuditLog auditlog = mastersService.getAuditLog(auditFileNumber, authToken);
    	log.info("AuditLog : " + auditlog);
		return new ResponseEntity<>(auditlog, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Create AuditLog") // label for swagger
    @RequestMapping(value = "/auditlog", method = RequestMethod.POST)
	public ResponseEntity<?> createAuditLog(@RequestBody AuditLog newAuditLog, @RequestParam String authToken) {
			AuditLog createdAuditLog = mastersService.addAuditLog(newAuditLog, authToken);
		return new ResponseEntity<>(createdAuditLog , HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Update AuditLog") // label for swagger
    @RequestMapping(value = "/auditlog/{auditFileNumber}", method = RequestMethod.PATCH)
	public ResponseEntity<?> updateAuditLog(@PathVariable String auditFileNumber, 
			@RequestBody AuditLog updatedAuditLog, @RequestParam String authToken) {		
		AuditLog modifiedAuditLog = mastersService.updateAuditLog(auditFileNumber, updatedAuditLog, authToken);
		return new ResponseEntity<>(modifiedAuditLog , HttpStatus.OK);
	}
    
    @ApiOperation(response = AuditLog.class, value = "Delete AuditLog") // label for swagger
	@RequestMapping(value = "/auditlog", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteAuditLog(@RequestParam String authToken, @RequestParam String auditFileNumber) {
    	mastersService.deleteAuditLog(auditFileNumber, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
