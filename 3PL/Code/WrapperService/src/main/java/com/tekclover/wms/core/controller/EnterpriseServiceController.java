package com.tekclover.wms.core.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.tekclover.wms.core.model.enterprise.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.core.service.EnterpriseSetupService;
import com.tekclover.wms.core.service.RegisterService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/wms-enterprise-service")
@Api(tags = {"Enterprise Service"}, value = "Enterprise Service Operations") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "User", description = "Operations related to Enterprise Modules")})
public class EnterpriseServiceController {

    @Autowired
    EnterpriseSetupService enterpriseSetupService;

    @Autowired
    RegisterService registerService;

    /*
     * -----------------------------ENTERPRISE---BARCODE----------------------------
     */
    @ApiOperation(response = Barcode.class, value = "Get all Barcode") // label for swagger
    @RequestMapping(value = "/barcode", method = RequestMethod.GET)
    public ResponseEntity<?> getBarcodes(@RequestParam String authToken) {
        Barcode[] barcode = enterpriseSetupService.getBarcodes(authToken);
        log.info("Barcode : " + barcode);
        return new ResponseEntity<>(barcode, HttpStatus.OK);
    }

    @ApiOperation(response = Barcode.class, value = "Get a Barcode")
    @GetMapping("/barcode/{barcodeTypeId}")
    public ResponseEntity<?> getBarcode(@PathVariable Long barcodeTypeId, @RequestParam String warehouseId,
                                        @RequestParam String method, @RequestParam Long barcodeSubTypeId, @RequestParam Long levelId,
                                        @RequestParam String levelReference, @RequestParam Long processId, @RequestParam String companyId,
                                        @RequestParam String languageId, @RequestParam String plantId, @RequestParam String authToken) {
        Barcode barcode = enterpriseSetupService.getBarcode(warehouseId, method, barcodeTypeId,
                barcodeSubTypeId, levelId, levelReference, processId, companyId, plantId, languageId, authToken);
        log.info("Barcode : " + barcode);
        return new ResponseEntity<>(barcode, HttpStatus.OK);
    }

    @ApiOperation(response = Barcode.class, value = "Search Barcode") // label for swagger
    @PostMapping("/barcode/findBarcode")
    public Barcode[] findBarcode(@RequestBody SearchBarcode searchBarcode,
                                 @RequestParam String authToken) throws Exception {
        return enterpriseSetupService.findBarcode(searchBarcode, authToken);
    }

    @ApiOperation(response = Optional.class, value = "Create New Barcode") // label for swagger
    @RequestMapping(value = "/barcode", method = RequestMethod.POST)
    public ResponseEntity<?> createBarcode(@RequestBody Barcode newBarcode, @RequestParam String loginUserID,
                                           @RequestParam String authToken) {
        Barcode createdBarcode = enterpriseSetupService.addBarcode(newBarcode, loginUserID, authToken);
        return new ResponseEntity<>(createdBarcode, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Update Barcode") // label for swagger
    @RequestMapping(value = "/barcode/{barcodeTypeId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateBarcode(@PathVariable Long barcodeTypeId, @RequestParam String warehouseId,
                                           @RequestParam String method, @RequestParam Long barcodeSubTypeId, @RequestParam Long levelId,
                                           @RequestParam String companyId, @RequestParam String languageId, @RequestParam String plantId,
                                           @RequestParam String levelReference, @RequestParam Long processId,
                                           @RequestBody Barcode updatedBarcode, @RequestParam String loginUserID, @RequestParam String authToken) {
        Barcode modifiedBarcode =
                enterpriseSetupService.updateBarcode(warehouseId, method, barcodeTypeId, barcodeSubTypeId, companyId, plantId, languageId, levelId,
                        levelReference, processId, updatedBarcode, loginUserID, authToken);
        return new ResponseEntity<>(modifiedBarcode, HttpStatus.OK);
    }

    @ApiOperation(response = Barcode.class, value = "Delete Barcode") // label for swagger
    @RequestMapping(value = "/barcode/{barcodeTypeId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteBarcode(@PathVariable Long barcodeTypeId, @RequestParam String warehouseId,
                                           @RequestParam String method, @RequestParam Long barcodeSubTypeId, @RequestParam Long levelId,
                                           @RequestParam String levelReference, @RequestParam Long processId, @RequestParam String companyId,
                                           @RequestParam String plantId, @RequestParam String languageId, @RequestParam String loginUserID,
                                           @RequestParam String authToken) {
        enterpriseSetupService.deleteBarcode(warehouseId, method, barcodeTypeId, barcodeSubTypeId, companyId, languageId, plantId, levelId,
                levelReference, processId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * -----------------------------ENTERPRISE---COMPANY----------------------------
     */
    @ApiOperation(response = Company.class, value = "Get all Company") // label for swagger
    @RequestMapping(value = "/company", method = RequestMethod.GET)
    public ResponseEntity<?> getCompanys(@RequestParam String authToken) {
        Company[] companyMaster = enterpriseSetupService.getCompanies(authToken);
        log.info("Company : " + companyMaster);
        return new ResponseEntity<>(companyMaster, HttpStatus.OK);
    }

    @ApiOperation(response = Company.class, value = "Get a Company") // label for swagger
    @RequestMapping(value = "/company/{companyId}", method = RequestMethod.GET)
    public ResponseEntity<?> getCompany(@PathVariable String companyId, @RequestParam String languageId, @RequestParam String authToken) {
        Company companyMaster = enterpriseSetupService.getCompany(companyId, languageId, authToken);
        log.info("Company : " + companyMaster);
        return new ResponseEntity<>(companyMaster, HttpStatus.OK);
    }

    @ApiOperation(response = Company.class, value = "Search Company") // label for swagger
    @PostMapping("/company/findCompany")
    public Company[] findCompany(@RequestBody SearchCompany searchCompany, @RequestParam String authToken)
            throws ParseException {
        return enterpriseSetupService.findCompany(searchCompany, authToken);
    }

    @ApiOperation(response = Company.class, value = "Create Company") // label for swagger
    @RequestMapping(value = "/company", method = RequestMethod.POST)
    public ResponseEntity<?> createCompany(@RequestBody Company newCompany, @RequestParam String loginUserID,
                                           @RequestParam String authToken) {
        Company createdCompany = enterpriseSetupService.addCompany(newCompany, loginUserID, authToken);
        return new ResponseEntity<>(createdCompany, HttpStatus.OK);
    }

    @ApiOperation(response = Company.class, value = "Update Company") // label for swagger
    @RequestMapping(value = "/company/{companyId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateCompany(@PathVariable String companyId, @RequestParam String languageId, @RequestParam String loginUserID,
                                           @RequestBody Company updatedCompany,
                                           @RequestParam String authToken) {
        Company modifiedCompany = enterpriseSetupService.updateCompany(companyId, languageId, loginUserID, updatedCompany, authToken);
        return new ResponseEntity<>(modifiedCompany, HttpStatus.OK);
    }

    @ApiOperation(response = Company.class, value = "Delete Company") // label for swagger
    @RequestMapping(value = "/company", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCompany(@RequestParam String authToken, @RequestParam String languageId, @RequestParam String companyId,
                                           @RequestParam String loginUserID) {
        enterpriseSetupService.deleteCompany(companyId, languageId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * -----------------------------ENTERPRISE---BATCHSERIAL------------------------
     */
    @ApiOperation(response = BatchSerial.class, value = "Get all BatchSerial") // label for swagger
    @RequestMapping(value = "/batchserial", method = RequestMethod.GET)
    public ResponseEntity<?> getBatchSerials(@RequestParam String authToken) {
        BatchSerial[] batchserial = enterpriseSetupService.getBatchSerials(authToken);
        log.info("BatchSerial : " + batchserial);
        return new ResponseEntity<>(batchserial, HttpStatus.OK);
    }

    @ApiOperation(response = BatchSerial.class, value = "Get a BatchSerial") // label for swagger
    @RequestMapping(value = "/batchserial/{storageMethod}", method = RequestMethod.GET)
    public ResponseEntity<?> getBatchSerial(@PathVariable String storageMethod, @RequestParam String companyId,
                                            @RequestParam String languageId, @RequestParam String plantId,
                                            @RequestParam String warehouseId, @RequestParam String maintenance,
                                            @RequestParam Long levelId, @RequestParam String authToken) {

        BatchSerial[] batchserial = enterpriseSetupService.getBatchSerial(storageMethod, companyId, languageId, plantId,
                warehouseId, levelId, maintenance, authToken);
        log.info("BatchSerial : " + batchserial);
        return new ResponseEntity<>(batchserial, HttpStatus.OK);
    }

    @ApiOperation(response = BatchSerial.class, value = "Search BatchSerial") // label for swagger
    @PostMapping("/batchserial/findBatchSerial")
    public BatchSerial[] findBatchSerial(@RequestBody SearchBatchSerial searchBatchSerial,
                                         @RequestParam String authToken)
            throws Exception {
        return enterpriseSetupService.findBatchSerial(searchBatchSerial, authToken);
    }

    @ApiOperation(response = Optional.class, value = "Create New BatchSerial") // label for swagger
    @RequestMapping(value = "/batchserial", method = RequestMethod.POST)
    public ResponseEntity<?> createBatchSerial(@RequestBody List<AddBatchSerial> newBatchSerial, @RequestParam String loginUserID,
                                               @RequestParam String authToken) {
        BatchSerial[] createdBatchSerial = enterpriseSetupService.addBatchSerial(newBatchSerial, loginUserID, authToken);
        return new ResponseEntity<>(createdBatchSerial, HttpStatus.OK);
    }

//	@ApiOperation(response = Optional.class, value = "Create New BatchSerial") // label for swagger
//	@RequestMapping(value = "/batchserial", method = RequestMethod.POST)
//	public ResponseEntity<?> createBatchSerial(@RequestBody BatchSerial newBatchSerial, @RequestParam String loginUserID,
//											   @RequestParam String authToken) {
//		BatchSerial createdBatchSerial = enterpriseSetupService.addBatchSerial(newBatchSerial, loginUserID, authToken);
//		return new ResponseEntity<>(createdBatchSerial , HttpStatus.OK);
//	}

    @ApiOperation(response = Optional.class, value = "Update BatchSerial") // label for swagger
    @RequestMapping(value = "/batchserial/{storageMethod}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateBatchSerial(@PathVariable String storageMethod, @RequestParam String companyId,
                                               @RequestParam String plantId, @RequestParam String warehouseId,
                                               @RequestParam String languageId, @RequestParam Long levelId, @RequestParam String maintenance,
                                               @Valid @RequestBody List<UpdateBatchSerial> updatedBatchSerial,
                                               @RequestParam String loginUserID, @RequestParam String authToken) {

        BatchSerial[] modifiedBatchSerial = enterpriseSetupService.updateBatchSerial(storageMethod, companyId, languageId, maintenance,
                plantId, warehouseId, levelId, updatedBatchSerial, loginUserID, authToken);
        return new ResponseEntity<>(modifiedBatchSerial, HttpStatus.OK);
    }
//	@ApiOperation(response = Optional.class, value = "Update BatchSerial") // label for swagger
//    @RequestMapping(value = "/batchserial/{storageMethod}", method = RequestMethod.PATCH)
//	public ResponseEntity<?> updateBatchSerial(@PathVariable String storageMethod,
//			@Valid @RequestBody BatchSerial updatedBatchSerial, @RequestParam String loginUserID, @RequestParam String authToken) {
//		BatchSerial modifiedBatchSerial = enterpriseSetupService.updateBatchSerial(storageMethod, updatedBatchSerial, loginUserID, authToken);
//		return new ResponseEntity<>(modifiedBatchSerial, HttpStatus.OK);
//	}

    @ApiOperation(response = BatchSerial.class, value = "Delete BatchSerial") // label for swagger
    @RequestMapping(value = "/batchserial/{storageMethod}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteBatchSerial(@RequestParam String storageMethod, @RequestParam String companyId, @RequestParam String plantId,
                                               @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam Long levelId,
                                               @RequestParam String loginUserID, @RequestParam String maintenance, @RequestParam String authToken) {
        enterpriseSetupService.deleteBatchSerial(storageMethod, companyId, plantId, warehouseId, maintenance, levelId, languageId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * -----------------------------ENTERPRISE---FLOOR------------------------------
     */

    @ApiOperation(response = Floor.class, value = "Get all Floor") // label for swagger
    @RequestMapping(value = "/floor", method = RequestMethod.GET)
    public ResponseEntity<?> getFloors(@RequestParam String authToken) {
        Floor[] floor = enterpriseSetupService.getFloors(authToken);
        log.info("Floor : " + floor);
        return new ResponseEntity<>(floor, HttpStatus.OK);
    }

    @ApiOperation(response = Floor.class, value = "Get a Floor") // label for swagger
    @RequestMapping(value = "/floor/{floorId}", method = RequestMethod.GET)
    public ResponseEntity<?> getFloor(@PathVariable Long floorId, @RequestParam String companyId, @RequestParam String plantId,
                                      @RequestParam String languageId, @RequestParam String warehouseId,
                                      @RequestParam String authToken) {
        Floor floor = enterpriseSetupService.getFloor(floorId, warehouseId, companyId, plantId, languageId, authToken);
        log.info("Floor : " + floor);
        return new ResponseEntity<>(floor, HttpStatus.OK);
    }

    @ApiOperation(response = Floor.class, value = "Search Floor") // label for swagger
    @PostMapping("/floor/findFloor")
    public Floor[] findFloor(@RequestBody SearchFloor searchFloor, @RequestParam String authToken)
            throws Exception {
        return enterpriseSetupService.findFloor(searchFloor, authToken);
    }

    @ApiOperation(response = Optional.class, value = "Create New Floor") // label for swagger
    @RequestMapping(value = "/floor", method = RequestMethod.POST)
    public ResponseEntity<?> createFloor(@RequestBody Floor newFloor, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        Floor createdFloor = enterpriseSetupService.addFloor(newFloor, loginUserID, authToken);
        return new ResponseEntity<>(createdFloor, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Update Floor") // label for swagger
    @RequestMapping(value = "/floor/{floorId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateFloor(@PathVariable Long floorId, @RequestParam String companyId, @RequestParam String plantId, @RequestParam String languageId,
                                         @RequestBody Floor updatedFloor, @RequestParam String loginUserID, @RequestParam String warehouseId,
                                         @RequestParam String authToken) {
        Floor modifiedFloor = enterpriseSetupService.updateFloor(floorId, warehouseId, companyId, plantId, languageId, updatedFloor, loginUserID, authToken);
        return new ResponseEntity<>(modifiedFloor, HttpStatus.OK);
    }

    @ApiOperation(response = Floor.class, value = "Delete Floor") // label for swagger
    @RequestMapping(value = "/floor/{floorId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFloor(@PathVariable Long floorId, @RequestParam String companyId, @RequestParam String plantId,
                                         @RequestParam String languageId, @RequestParam String warehouseId,
                                         @RequestParam String loginUserID, @RequestParam String authToken) {
        enterpriseSetupService.deleteFloor(floorId, warehouseId, companyId, plantId, languageId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * -----------------------------ENTERPRISE---EMPLOYEE--------------------------
     */
    @ApiOperation(response = Employee.class, value = "Get all Employee") // label for swagger
    @RequestMapping(value = "/employee", method = RequestMethod.GET)
    public ResponseEntity<?> getEmployees(@RequestParam String authToken) {
        Employee[] employee = enterpriseSetupService.getEMployees(authToken);
        log.info("ItemGroup : " + employee);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @ApiOperation(response = Employee.class, value = "Get a Employee")
    @GetMapping("/employee/{employee}")
    public ResponseEntity<?> getEmployee(@PathVariable String employee, @RequestParam String authToken) {
        Employee employee1 = enterpriseSetupService.getEmployee(employee, authToken);
        log.info("ItemGroup : " + employee1);
        return new ResponseEntity<>(employee1, HttpStatus.OK);
    }

//	@ApiOperation(response = Employee.class, value = "Search ItemGroup") // label for swagger
//	@PostMapping("/employee/findEmployee")
//	public Employee[] findEmployee(@RequestBody search searchItemGroup, @RequestParam String authToken)
//			throws Exception {
//		return enterpriseSetupService.findItemGroup(searchItemGroup, authToken);
//	}

    @ApiOperation(response = Employee.class, value = "Create New Employee") // label for swagger
    @RequestMapping(value = "/employee", method = RequestMethod.POST)
    public ResponseEntity<?> createEmployee(@RequestBody Employee newEmployee, @RequestParam String loginUserID,
                                            @RequestParam String authToken) {
        Employee createdEmployee = enterpriseSetupService.addEmployee(newEmployee, loginUserID, authToken);
        return new ResponseEntity<>(createdEmployee, HttpStatus.OK);
    }

    @ApiOperation(response = Employee.class, value = "Update Employee") // label for swagger
    @RequestMapping(value = "/employee/{employeeId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateEmployee(@PathVariable String employee, @Valid @RequestBody Employee updateEmployee,
                                            @RequestParam String loginUserID, @RequestParam String authToken) {
        Employee modifiedEmployee =
                enterpriseSetupService.updateEmployee(employee, updateEmployee, loginUserID, authToken);
        return new ResponseEntity<>(modifiedEmployee, HttpStatus.OK);
    }

    @ApiOperation(response = Employee.class, value = "Delete Employee") // label for swagger
    @RequestMapping(value = "/employee/{employee}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteItemGroup(@PathVariable String employee, @RequestParam String loginUserID,
                                             @RequestParam String authToken) {
        enterpriseSetupService.deleteEmployee(employee, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * -----------------------------ENTERPRISE---ITEMGROUP--------------------------
     */
    @ApiOperation(response = ItemGroup.class, value = "Get all ItemGroup") // label for swagger
    @RequestMapping(value = "/itemgroup", method = RequestMethod.GET)
    public ResponseEntity<?> getItemGroups(@RequestParam String authToken) {
        ItemGroup[] itemgroup = enterpriseSetupService.getItemGroups(authToken);
        log.info("ItemGroup : " + itemgroup);
        return new ResponseEntity<>(itemgroup, HttpStatus.OK);
    }

    @ApiOperation(response = ItemGroup.class, value = "Get a ItemGroup")
    @GetMapping("/itemgroup/{itemTypeId}")
    public ResponseEntity<?> getItemGroup(@PathVariable Long itemTypeId, @RequestParam String warehouseId,
                                          @RequestParam String companyId, @RequestParam String languageId,
                                          @RequestParam String plantId, @RequestParam String authToken) {

        ItemGroup[] itemgroup = enterpriseSetupService.getItemGroup(companyId, languageId, plantId, warehouseId, itemTypeId, authToken);
        log.info("ItemGroup : " + itemgroup);
        return new ResponseEntity<>(itemgroup, HttpStatus.OK);
    }

    @ApiOperation(response = ItemGroup.class, value = "Search ItemGroup") // label for swagger
    @PostMapping("/itemgroup/findItemGroup")
    public ItemGroup[] findItemGroup(@RequestBody SearchItemGroup searchItemGroup, @RequestParam String authToken)
            throws Exception {
        return enterpriseSetupService.findItemGroup(searchItemGroup, authToken);
    }

    @ApiOperation(response = Optional.class, value = "Create New ItemGroup") // label for swagger
    @RequestMapping(value = "/itemgroup", method = RequestMethod.POST)
    public ResponseEntity<?> createItemGroup(@RequestBody List<ItemGroup> newItemGroup, @RequestParam String loginUserID,
                                             @RequestParam String authToken) {
        ItemGroup[] createdItemGroup = enterpriseSetupService.addItemGroup(newItemGroup, loginUserID, authToken);
        return new ResponseEntity<>(createdItemGroup, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Update ItemGroup") // label for swagger
    @RequestMapping(value = "/itemgroup/{itemTypeId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateItemGroup(@PathVariable Long itemTypeId, @RequestParam String warehouseId,
                                             @RequestParam String companyId, @RequestParam String languageId,
                                             @RequestParam String plantId, @Valid @RequestBody List<ItemGroup> updateItemGroup,
                                             @RequestParam String loginUserID, @RequestParam String authToken) {
        ItemGroup[] modifiedItemGroup =
                enterpriseSetupService.updateItemGroup(warehouseId, itemTypeId, updateItemGroup, companyId,
                        languageId, plantId, loginUserID, authToken);
        return new ResponseEntity<>(modifiedItemGroup, HttpStatus.OK);
    }

    @ApiOperation(response = ItemGroup.class, value = "Delete ItemGroup") // label for swagger
    @RequestMapping(value = "/itemgroup/{itemTypeId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteItemGroup(@PathVariable Long itemTypeId, @RequestParam String warehouseId,
                                             @RequestParam String companyId, @RequestParam String languageId,
                                             @RequestParam String plantId, @RequestParam String loginUserID, @RequestParam String authToken) {

        enterpriseSetupService.deleteItemGroup(warehouseId, itemTypeId, companyId, languageId, plantId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * -----------------------------ENTERPRISE---ITEMTYPE---------------------------
     */
    @ApiOperation(response = ItemType.class, value = "Get all ItemType") // label for swagger
    @RequestMapping(value = "/itemtype", method = RequestMethod.GET)
    public ResponseEntity<?> getItemTypes(@RequestParam String authToken) {
        ItemType[] itemtype = enterpriseSetupService.getItemTypes(authToken);
        log.info("ItemType : " + itemtype);
        return new ResponseEntity<>(itemtype, HttpStatus.OK);
    }

    @ApiOperation(response = ItemType.class, value = "Get a ItemType")
    @GetMapping("/itemtype/{itemTypeId}")
    public ResponseEntity<?> getItemType(@PathVariable Long itemTypeId, @RequestParam String warehouseId,
                                         @RequestParam String companyId, @RequestParam String plantId, @RequestParam String languageId,
                                         @RequestParam String authToken) {
        ItemType itemtype = enterpriseSetupService.getItemType(warehouseId, itemTypeId, companyId, plantId, languageId, authToken);
        log.info("ItemType : " + itemtype);
        return new ResponseEntity<>(itemtype, HttpStatus.OK);
    }

    @ApiOperation(response = ItemType.class, value = "Search ItemType") // label for swagger
    @PostMapping("/itemtype/findItemType")
    public ItemType[] findItemType(@RequestBody SearchItemType searchItemType, @RequestParam String authToken)
            throws Exception {
        return enterpriseSetupService.findItemType(searchItemType, authToken);
    }

    @ApiOperation(response = Optional.class, value = "Create New ItemType") // label for swagger
    @RequestMapping(value = "/itemtype", method = RequestMethod.POST)
    public ResponseEntity<?> createItemType(@RequestBody ItemType newItemType, @RequestParam String loginUserID, @RequestParam String authToken) {
        ItemType createdItemType = enterpriseSetupService.addItemType(newItemType, loginUserID, authToken);
        return new ResponseEntity<>(createdItemType, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Update ItemType") // label for swagger
    @RequestMapping(value = "/itemtype/{itemTypeId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateItemType(@PathVariable Long itemTypeId, @RequestParam String warehouseId, @RequestParam String companyId,
                                            @RequestParam String plantId, @RequestParam String languageId,
                                            @RequestBody ItemType updatedItemType, @RequestParam String loginUserID, @RequestParam String authToken) {
        ItemType modifiedItemType =
                enterpriseSetupService.updateItemType(warehouseId, itemTypeId, companyId, plantId, languageId, updatedItemType, loginUserID, authToken);
        return new ResponseEntity<>(modifiedItemType, HttpStatus.OK);
    }

    @ApiOperation(response = ItemType.class, value = "Delete ItemType") // label for swagger
    @RequestMapping(value = "/itemtype/{itemTypeId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteItemType(@PathVariable Long itemTypeId, @RequestParam String companyId, @RequestParam String plantId,
                                            @RequestParam String languageId, @RequestParam String warehouseId,
                                            @RequestParam String loginUserID,
                                            @RequestParam String authToken) {
        enterpriseSetupService.deleteItemType(warehouseId, itemTypeId, companyId, plantId, languageId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * -----------------------------ENTERPRISE---PLANT------------------------------
     */
    @ApiOperation(response = Plant.class, value = "Get all Plant") // label for swagger
    @RequestMapping(value = "/plant", method = RequestMethod.GET)
    public ResponseEntity<?> getPlants(@RequestParam String authToken) {
        Plant[] plant = enterpriseSetupService.getPlants(authToken);
        return new ResponseEntity<>(plant, HttpStatus.OK);
    }

    @ApiOperation(response = Plant.class, value = "Get a Plant") // label for swagger
    @RequestMapping(value = "/plant/{plantId}", method = RequestMethod.GET)
    public ResponseEntity<?> getPlant(@PathVariable String plantId, @RequestParam String companyId,
                                      @RequestParam String languageId, @RequestParam String authToken) {
        Plant plant = enterpriseSetupService.getPlant(plantId, companyId, languageId, authToken);
        return new ResponseEntity<>(plant, HttpStatus.OK);
    }

    @ApiOperation(response = Plant.class, value = "Search Plant") // label for swagger
    @PostMapping("/plant/findPlant")
    public Plant[] findPlant(@RequestBody SearchPlant searchPlant, @RequestParam String authToken)
            throws Exception {
        return enterpriseSetupService.findPlant(searchPlant, authToken);
    }

    @ApiOperation(response = Optional.class, value = "Create New Plant") // label for swagger
    @RequestMapping(value = "/plant", method = RequestMethod.POST)
    public ResponseEntity<?> createPlant(@RequestBody Plant newPlant, @RequestParam String loginUserID,
                                         @RequestParam String authToken) {
        Plant createdPlant = enterpriseSetupService.addPlant(newPlant, loginUserID, authToken);
        return new ResponseEntity<>(createdPlant, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Update Plant") // label for swagger
    @RequestMapping(value = "/plant/{plantId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updatePlant(@PathVariable String plantId, @RequestParam String companyId, @RequestParam String languageId,
                                         @RequestBody Plant updatedPlant, @RequestParam String loginUserID, @RequestParam String authToken) {
        Plant modifiedPlant = enterpriseSetupService.updatePlant(plantId, updatedPlant, companyId, languageId, loginUserID, authToken);
        return new ResponseEntity<>(modifiedPlant, HttpStatus.OK);
    }

    @ApiOperation(response = Plant.class, value = "Delete Plant") // label for swagger
    @RequestMapping(value = "/plant/{plantId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePlant(@PathVariable String plantId, @RequestParam String companyId,
                                         @RequestParam String languageId, @RequestParam String loginUserID,
                                         @RequestParam String authToken) {
        enterpriseSetupService.deletePlant(plantId, companyId, languageId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * -----------------------------ENTERPRISE---STORAGEBINTYPE---------------------
     */
    @ApiOperation(response = StorageBinType.class, value = "Get all StorageBinType") // label for swagger
    @RequestMapping(value = "/storagebintype", method = RequestMethod.GET)
    public ResponseEntity<?> getStorageBinTypes(@RequestParam String authToken) {
        StorageBinType[] storageBinType = enterpriseSetupService.getStorageBinTypes(authToken);
        log.info("StorageBinType : " + storageBinType);
        return new ResponseEntity<>(storageBinType, HttpStatus.OK);
    }

    @ApiOperation(response = StorageBinType.class, value = "Get a StorageBinType")
    @GetMapping("/storagebintype/{storageBinTypeId}")
    public ResponseEntity<?> getStorageBinType(@PathVariable Long storageBinTypeId, @RequestParam String warehouseId, @RequestParam Long storageClassId,
                                               @RequestParam Long storageTypeId, @RequestParam String companyId,
                                               @RequestParam String languageId, @RequestParam String plantId, @RequestParam String authToken) {
        StorageBinType storagebintype =
                enterpriseSetupService.getStorageBinType(warehouseId, storageTypeId, storageClassId, storageBinTypeId, companyId, languageId, plantId, authToken);
        log.info("StorageBinType : " + storagebintype);
        return new ResponseEntity<>(storagebintype, HttpStatus.OK);
    }

    @ApiOperation(response = StorageBinType.class, value = "Search StorageBinType") // label for swagger
    @PostMapping("/storagebintype/findStorageBinType")
    public StorageBinType[] findStorageBinType(@RequestBody SearchStorageBinType searchStorageBinType,
                                               @RequestParam String authToken)
            throws Exception {
        return enterpriseSetupService.findStorageBinType(searchStorageBinType, authToken);
    }

    @ApiOperation(response = Optional.class, value = "Create New StorageBinType") // label for swagger
    @RequestMapping(value = "/storagebintype", method = RequestMethod.POST)
    public ResponseEntity<?> createStorageBinType(@RequestBody StorageBinType newStorageBinType,
                                                  @RequestParam String loginUserID,
                                                  @RequestParam String authToken) {
        StorageBinType createdStorageBinType = enterpriseSetupService.addStorageBinType(newStorageBinType, loginUserID, authToken);
        return new ResponseEntity<>(createdStorageBinType, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Update StorageBinType") // label for swagger
    @RequestMapping(value = "/storagebintype/{storageBinTypeId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateStorageBinType(@PathVariable Long storageBinTypeId, @RequestParam String companyId, @RequestParam Long storageClassId, @RequestParam String languageId, @RequestParam String plantId, @RequestParam String warehouseId,
                                                  @RequestParam Long storageTypeId, @RequestBody StorageBinType updatedStorageBinType,
                                                  @RequestParam String loginUserID, @RequestParam String authToken) {
        StorageBinType modifiedStorageBinType =
                enterpriseSetupService.updateStorageBinType(warehouseId, storageTypeId, storageClassId, storageBinTypeId, companyId, languageId, plantId,
                        updatedStorageBinType, loginUserID, authToken);
        return new ResponseEntity<>(modifiedStorageBinType, HttpStatus.OK);
    }

    @ApiOperation(response = StorageBinType.class, value = "Delete StorageBinType") // label for swagger
    @RequestMapping(value = "/storagebintype/{storageBinTypeId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteStorageBinType(@PathVariable Long storageBinTypeId, @RequestParam String warehouseId,
                                                  @RequestParam Long storageTypeId, @RequestParam String loginUserID, @RequestParam String companyId, @RequestParam Long storageClassId, @RequestParam String languageId, @RequestParam String plantId, @RequestParam String authToken) {
        enterpriseSetupService.deleteStorageBinType(warehouseId, storageTypeId, storageClassId, storageBinTypeId, companyId, languageId, plantId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * -----------------------------ENTERPRISE---STORAGECLASS-----------------------
     */
    @ApiOperation(response = StorageClass.class, value = "Get all StorageClass") // label for swagger
    @RequestMapping(value = "/storageclass", method = RequestMethod.GET)
    public ResponseEntity<?> getStorageClasss(@RequestParam String authToken) {
        StorageClass[] storageClass = enterpriseSetupService.getStorageClasss(authToken);
        log.info("StorageClass : " + storageClass);
        return new ResponseEntity<>(storageClass, HttpStatus.OK);
    }

    @ApiOperation(response = StorageClass.class, value = "Get a StorageClass")
    @GetMapping("/storageclass/{storageClassId}")
    public ResponseEntity<?> getStorageClass(@PathVariable Long storageClassId, @RequestParam String warehouseId,
                                             @RequestParam String companyId, @RequestParam String languageId, @RequestParam String plantId,
                                             @RequestParam String authToken) {
        StorageClass storageclass = enterpriseSetupService.getStorageClass(warehouseId, storageClassId, companyId, plantId, languageId, authToken);
        log.info("StorageClass : " + storageclass);
        return new ResponseEntity<>(storageclass, HttpStatus.OK);
    }

    @ApiOperation(response = StorageClass.class, value = "Search StorageClass") // label for swagger
    @PostMapping("/storageclass/findStorageClass")
    public StorageClass[] findStorageClass(@RequestBody SearchStorageClass searchStorageClass,
                                           @RequestParam String authToken)
            throws Exception {
        return enterpriseSetupService.findStorageClass(searchStorageClass, authToken);
    }

    @ApiOperation(response = Optional.class, value = "Create New StorageClass") // label for swagger
    @RequestMapping(value = "/storageclass", method = RequestMethod.POST)
    public ResponseEntity<?> createStorageClass(@RequestBody StorageClass newStorageClass, @RequestParam String loginUserID,
                                                @RequestParam String authToken) {
        StorageClass createdStorageClass = enterpriseSetupService.addStorageClass(newStorageClass, loginUserID, authToken);
        return new ResponseEntity<>(createdStorageClass, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Update StorageClass") // label for swagger
    @RequestMapping(value = "/storageclass/{storageClassId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateStorageClass(@PathVariable Long storageClassId, @RequestParam String warehouseId,
                                                @RequestParam String companyId, @RequestParam String plantId, @RequestParam String languageId,
                                                @RequestBody StorageClass updatedStorageClass, @RequestParam String loginUserID, @RequestParam String authToken) {
        StorageClass modifiedStorageClass =
                enterpriseSetupService.updateStorageClass(warehouseId, storageClassId, companyId, languageId, plantId, updatedStorageClass, loginUserID, authToken);
        return new ResponseEntity<>(modifiedStorageClass, HttpStatus.OK);
    }

    @ApiOperation(response = StorageClass.class, value = "Delete StorageClass") // label for swagger
    @RequestMapping(value = "/storageclass/{storageClassId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteStorageClass(@PathVariable Long storageClassId, @RequestParam String warehouseId, @RequestParam String companyId,
                                                @RequestParam String plantId, @RequestParam String languageId,
                                                @RequestParam String loginUserID, @RequestParam String authToken) {
        enterpriseSetupService.deleteStorageClass(warehouseId, storageClassId, companyId, plantId, languageId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * -----------------------------ENTERPRISE---STORAGESECTION---------------------
     */
    @ApiOperation(response = StorageSection.class, value = "Get all StorageSection") // label for swagger
    @RequestMapping(value = "/storagesection", method = RequestMethod.GET)
    public ResponseEntity<?> getStorageSections(@RequestParam String authToken) {
        StorageSection[] storageSection = enterpriseSetupService.getStorageSections(authToken);
        log.info("StorageSection : " + storageSection);
        return new ResponseEntity<>(storageSection, HttpStatus.OK);
    }

    @ApiOperation(response = StorageSection.class, value = "Get a StorageSection")
    @GetMapping("/storagesection/{storageSectionId}")
    public ResponseEntity<?> getStorageSection(@PathVariable String storageSectionId, @RequestParam String companyId, @RequestParam String plantId,
                                               @RequestParam String languageId, @RequestParam String warehouseId,
                                               @RequestParam Long floorId, @RequestParam String authToken) {
        StorageSection storagesection = enterpriseSetupService.getStorageSection(warehouseId, floorId, storageSectionId, companyId, languageId, plantId, authToken);
        log.info("StorageSection : " + storagesection);
        return new ResponseEntity<>(storagesection, HttpStatus.OK);
    }

    @ApiOperation(response = StorageSection.class, value = "Search StorageSection") // label for swagger
    @PostMapping("/storagesection/findStorageSection")
    public StorageSection[] findStorageSection(@RequestBody SearchStorageSection searchStorageSection,
                                               @RequestParam String authToken) throws Exception {
        return enterpriseSetupService.findStorageSection(searchStorageSection, authToken);
    }

    @ApiOperation(response = Optional.class, value = "Create New StorageSection") // label for swagger
    @RequestMapping(value = "/storagesection", method = RequestMethod.POST)
    public ResponseEntity<?> createStorageSection(@RequestBody StorageSection newStorageSection,
                                                  @RequestParam String loginUserID, @RequestParam String authToken) {
        StorageSection createdStorageSection = enterpriseSetupService.addStorageSection(newStorageSection, loginUserID, authToken);
        return new ResponseEntity<>(createdStorageSection, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Update StorageSection") // label for swagger
    @RequestMapping(value = "/storagesection/{storageSectionId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateStorageSection(@PathVariable String storageSectionId, @RequestParam String warehouseId,
                                                  @RequestParam Long floorId, @RequestBody StorageSection updatedStorageSection, @RequestParam String loginUserID,
                                                  @RequestParam String companyId, @RequestParam String languageId,
                                                  @RequestParam String plantId, @RequestParam String authToken) {
        StorageSection modifiedStorageSection =
                enterpriseSetupService.updateStorageSection(warehouseId, floorId, storageSectionId, companyId, languageId, plantId,
                        updatedStorageSection, loginUserID, authToken);
        return new ResponseEntity<>(modifiedStorageSection, HttpStatus.OK);
    }

    @ApiOperation(response = StorageSection.class, value = "Delete StorageSection") // label for swagger
    @RequestMapping(value = "/storagesection/{storageSectionId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteStorageSection(@PathVariable String storageSectionId, @RequestParam String warehouseId,
                                                  @RequestParam Long floorId, @RequestParam String companyId, @RequestParam String languageId,
                                                  @RequestParam String plantId, @RequestParam String loginUserID,
                                                  @RequestParam String authToken) {
        enterpriseSetupService.deleteStorageSection(warehouseId, floorId, storageSectionId, companyId, plantId, languageId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * -----------------------------ENTERPRISE---STORAGETYPE------------------------
     */
    @ApiOperation(response = StorageType.class, value = "Get all StorageType") // label for swagger
    @RequestMapping(value = "/storagetype", method = RequestMethod.GET)
    public ResponseEntity<?> getStorageTypes(@RequestParam String authToken) {
        StorageType[] storagetype = enterpriseSetupService.getStorageTypes(authToken);
        log.info("StorageType : " + storagetype);
        return new ResponseEntity<>(storagetype, HttpStatus.OK);
    }

    @ApiOperation(response = StorageType.class, value = "Get a StorageType")
    @GetMapping("/storagetype/{storageTypeId}")
    public ResponseEntity<?> getStorageType(@PathVariable Long storageTypeId, @RequestParam String warehouseId, @RequestParam String companyId,
                                            @RequestParam String languageId, @RequestParam String plantId,
                                            @RequestParam Long storageClassId, @RequestParam String authToken) {
        StorageType storagetype = enterpriseSetupService.getStorageType(warehouseId, storageClassId, storageTypeId, companyId, languageId, plantId, authToken);
        log.info("StorageType : " + storagetype);
        return new ResponseEntity<>(storagetype, HttpStatus.OK);
    }

    @ApiOperation(response = StorageType.class, value = "Search StorageType") // label for swagger
    @PostMapping("/storagetype/findStorageType")
    public StorageType[] findStorageType(@RequestBody SearchStorageType searchStorageType, @RequestParam String authToken)
            throws Exception {
        return enterpriseSetupService.findStorageType(searchStorageType, authToken);
    }

    @ApiOperation(response = Optional.class, value = "Create New StorageType") // label for swagger
    @RequestMapping(value = "/storagetype", method = RequestMethod.POST)
    public ResponseEntity<?> createStorageType(@RequestBody StorageType newStorageType, @RequestParam String loginUserID,
                                               @RequestParam String authToken) {
        StorageType createdStorageType = enterpriseSetupService.addStorageType(newStorageType, loginUserID, authToken);
        return new ResponseEntity<>(createdStorageType, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Update StorageType") // label for swagger
    @RequestMapping(value = "/storagetype/{storageTypeId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateStorageType(@PathVariable Long storageTypeId, @RequestParam String warehouseId,
                                               @RequestParam Long storageClassId, @RequestParam String companyId, @RequestParam String plantId,
                                               @RequestParam String languageId, @RequestBody StorageType updatedStorageType,
                                               @RequestParam String loginUserID, @RequestParam String authToken) {
        StorageType modifiedStorageType =
                enterpriseSetupService.updateStorageType(warehouseId, storageClassId, storageTypeId, updatedStorageType, companyId, languageId, plantId, loginUserID, authToken);
        return new ResponseEntity<>(modifiedStorageType, HttpStatus.OK);
    }

    @ApiOperation(response = StorageType.class, value = "Delete StorageType") // label for swagger
    @RequestMapping(value = "/storagetype/{storageTypeId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteStorageType(@PathVariable Long storageTypeId, @RequestParam String warehouseId, @RequestParam String companyId,
                                               @RequestParam String plantId, @RequestParam String languageId,
                                               @RequestParam Long storageClassId, @RequestParam String loginUserID,
                                               @RequestParam String authToken) {
        enterpriseSetupService.deleteStorageType(warehouseId, storageClassId, storageTypeId, companyId, languageId, plantId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * -----------------------------ENTERPRISE---STRATEGIES-------------------------
     */
    @ApiOperation(response = Strategies.class, value = "Get all Strategies") // label for swagger
    @RequestMapping(value = "/strategies", method = RequestMethod.GET)
    public ResponseEntity<?> getStrategiess(@RequestParam String authToken) {
        Strategies[] strategies = enterpriseSetupService.getStrategiess(authToken);
        log.info("Strategies : " + strategies);
        return new ResponseEntity<>(strategies, HttpStatus.OK);
    }

    @ApiOperation(response = Strategies.class, value = "Get a Strategies")
    @GetMapping("/strategies/get")
    public ResponseEntity<?> getStrategies(@RequestParam String warehouseId, @RequestParam String companyId,
                                           @RequestParam String languageId, @RequestParam String plantId,
                                           @RequestParam String authToken) {

        Strategies[] strategies = enterpriseSetupService.getStrategies(companyId, languageId, plantId, warehouseId, authToken);
        log.info("Strategies : " + strategies);
        return new ResponseEntity<>(strategies, HttpStatus.OK);
    }

    @ApiOperation(response = Strategies.class, value = "Search Strategies") // label for swagger
    @PostMapping("/strategies/findStrategies")
    public Strategies[] findStrategies(@RequestBody SearchStrategies searchStrategies, @RequestParam String authToken)
            throws Exception {
        return enterpriseSetupService.findStrategies(searchStrategies, authToken);
    }

    @ApiOperation(response = Optional.class, value = "Create New Strategies") // label for swagger
    @RequestMapping(value = "/strategies", method = RequestMethod.POST)
    public ResponseEntity<?> createStrategies(@RequestBody List<Strategies> newStrategies, @RequestParam String loginUserID,
                                              @RequestParam String authToken) {
        Strategies[] createdStrategies = enterpriseSetupService.addStrategies(newStrategies, loginUserID, authToken);
        return new ResponseEntity<>(createdStrategies, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Update Strategies") // label for swagger
    @RequestMapping(value = "/strategies/update", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateStrategies(@RequestParam String warehouseId,
                                              @RequestParam String companyId, @RequestParam String languageId,
                                              @RequestParam String plantId, @RequestBody List<Strategies> updatedStrategies,
                                              @RequestParam String loginUserID, @RequestParam String authToken) {

        Strategies[] modifiedStrategies = enterpriseSetupService.updateStrategies(companyId, languageId, plantId,
                warehouseId, updatedStrategies, loginUserID, authToken);
        return new ResponseEntity<>(modifiedStrategies, HttpStatus.OK);
    }

    @ApiOperation(response = Strategies.class, value = "Delete Strategies") // label for swagger
    @RequestMapping(value = "/strategies/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteStrategies(@RequestParam String warehouseId, @RequestParam String companyId,
                                              @RequestParam String languageId, @RequestParam String plantId,
                                              @RequestParam String loginUserID, @RequestParam String authToken) {

        enterpriseSetupService.deleteStrategies(companyId, languageId, plantId, warehouseId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * -----------------------------ENTERPRISE---VARIANT----------------------------
     */
    @ApiOperation(response = Variant.class, value = "Get all Variant") // label for swagger
    @RequestMapping(value = "/variant", method = RequestMethod.GET)
    public ResponseEntity<?> getVariants(@RequestParam String authToken) {
        Variant[] variant = enterpriseSetupService.getVariants(authToken);
        log.info("Variant : " + variant);
        return new ResponseEntity<>(variant, HttpStatus.OK);
    }

    @ApiOperation(response = Variant.class, value = "Get a Variant") // label for swagger
    @RequestMapping(value = "/variant/{variantId}", method = RequestMethod.GET)
    public ResponseEntity<?> getVariant(@PathVariable String variantId, @RequestParam String companyId,
                                        @RequestParam String plantId, @RequestParam String languageId, @RequestParam String variantSubId,
                                        @RequestParam Long levelId, @RequestParam String warehouseId, @RequestParam String authToken) {
        Variant[] variant = enterpriseSetupService.getVariant(variantId, companyId, languageId, plantId, levelId, warehouseId, variantSubId, authToken);
        log.info("Variant : " + variant);
        return new ResponseEntity<>(variant, HttpStatus.OK);
    }

    @ApiOperation(response = Variant.class, value = "Search Variant") // label for swagger
    @PostMapping("/variant/findVariant")
    public Variant[] findVariant(@RequestBody SearchVariant searchVariant, @RequestParam String authToken)
            throws Exception {
        return enterpriseSetupService.findVariant(searchVariant, authToken);
    }

    @ApiOperation(response = Optional.class, value = "Create New Variant") // label for swagger
    @RequestMapping(value = "/variant", method = RequestMethod.POST)
    public ResponseEntity<?> createVariant(@RequestBody List<AddVariant> newVariant, @RequestParam String loginUserID,
                                           @RequestParam String authToken) {
        Variant[] createdVariant = enterpriseSetupService.addVariant(newVariant, loginUserID, authToken);
        return new ResponseEntity<>(createdVariant, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Update Variant") // label for swagger
    @RequestMapping(value = "/variant/{variantId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateVariant(@PathVariable String variantId, @RequestParam String companyId,
                                           @RequestParam String plantId, @RequestParam String languageId,
                                           @RequestParam String warehouseId, @RequestParam Long levelId,
                                           @RequestParam String variantSubId, @RequestBody List<UpdateVariant> updatedVariant,
                                           @RequestParam String loginUserID, @RequestParam String authToken) {

        Variant[] modifiedVariant = enterpriseSetupService.updateVariant(variantId, updatedVariant, companyId,
                plantId, warehouseId, languageId, levelId, variantSubId, loginUserID, authToken);
        return new ResponseEntity<>(modifiedVariant, HttpStatus.OK);
    }

    @ApiOperation(response = Variant.class, value = "Delete Variant") // label for swagger
    @RequestMapping(value = "/variant", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteVariant(@RequestParam String authToken, @RequestParam String companyId,
                                           @RequestParam String languageId, @RequestParam String plantId,
                                           @RequestParam String warehouseId, @RequestParam Long levelId, @RequestParam String variantId,
                                           @RequestParam String variantSubId, @RequestParam String loginUserID) {

        enterpriseSetupService.deleteVariant(variantId, companyId, plantId, warehouseId,
                variantSubId, languageId, levelId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * -----------------------------ENTERPRISE---WAREHOUSE--------------------------
     */
    @ApiOperation(response = Warehouse.class, value = "Get all Warehouse") // label for swagger
    @RequestMapping(value = "/warehouse", method = RequestMethod.GET)
    public ResponseEntity<?> getWarehouses(@RequestParam String authToken) {
        Warehouse[] warehouse = enterpriseSetupService.getWarehouses(authToken);
        log.info("Warehouse : " + warehouse);
        return new ResponseEntity<>(warehouse, HttpStatus.OK);
    }

    @ApiOperation(response = Warehouse.class, value = "Get a Warehouse") // label for swagger
    @RequestMapping(value = "/warehouse/{warehouseId}", method = RequestMethod.GET)
    public ResponseEntity<?> getWarehouse(@PathVariable String warehouseId, @RequestParam String modeOfImplementation,
                                          @RequestParam String companyId, @RequestParam String plantId, @RequestParam String languageId,
                                          @RequestParam Long warehouseTypeId, @RequestParam String authToken) {
        Warehouse warehouse = enterpriseSetupService.getWarehouse(warehouseId, modeOfImplementation, warehouseTypeId, companyId, plantId, languageId, authToken);
        log.info("Warehouse : " + warehouse);
        return new ResponseEntity<>(warehouse, HttpStatus.OK);
    }

    @ApiOperation(response = Warehouse.class, value = "Search Warehouse") // label for swagger
    @PostMapping("/warehouse/findWarehouse")
    public Warehouse[] findWarehouse(@RequestBody SearchWarehouse searchWarehouse, @RequestParam String authToken)
            throws Exception {
        return enterpriseSetupService.findWarehouse(searchWarehouse, authToken);
    }

    @ApiOperation(response = Optional.class, value = "Create New Warehouse") // label for swagger
    @RequestMapping(value = "/warehouse", method = RequestMethod.POST)
    public ResponseEntity<?> createWarehouse(@RequestBody List<Warehouse> newWarehouse, @RequestParam String loginUserID, @RequestParam String authToken) {
        Warehouse[] createdWarehouse = enterpriseSetupService.addWarehouse(newWarehouse, loginUserID, authToken);
        return new ResponseEntity<>(createdWarehouse, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Update Warehouse") // label for swagger
    @RequestMapping(value = "/warehouse/{warehouseId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateWarehouse(@PathVariable String warehouseId, @RequestParam String modeOfImplementation, @RequestParam Long warehouseTypeId,
                                             @RequestParam String companyId, @RequestParam String plantId, @RequestParam String languageId,
                                             @RequestBody Warehouse updatedWarehouse, @RequestParam String loginUserID, @RequestParam String authToken) {
        Warehouse modifiedWarehouse = enterpriseSetupService.updateWarehouse(warehouseId, modeOfImplementation, warehouseTypeId, companyId, plantId, languageId, updatedWarehouse, loginUserID, authToken);
        return new ResponseEntity<>(modifiedWarehouse, HttpStatus.OK);
    }

    @ApiOperation(response = Warehouse.class, value = "Delete Warehouse") // label for swagger
    @RequestMapping(value = "/warehouse/{warehouseId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteWarehouse(@PathVariable String warehouseId, @RequestParam String modeOfImplementation,
                                             @RequestParam Long warehouseTypeId, @RequestParam String companyId,
                                             @RequestParam String plantId, @RequestParam String languageId, @RequestParam String loginUserID,
                                             @RequestParam String authToken) {
        enterpriseSetupService.deleteWarehouse(warehouseId, modeOfImplementation, warehouseTypeId, companyId, plantId, languageId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}