package com.tekclover.wms.api.transaction.service;


import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.outbound.packing.FindPackingInventory;
import com.tekclover.wms.api.transaction.model.outbound.packing.PackingInventory;
import com.tekclover.wms.api.transaction.repository.PackingInventoryRepository;
import com.tekclover.wms.api.transaction.repository.specification.PackingInventorySpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PackingInventoryService {

    @Autowired
    private PackingInventoryRepository packingInventoryRepository;


    /**
     *  Get All PackingInventory
     * @return
     */
    public List<PackingInventory> getAllPackingInventory(){

        List<PackingInventory> packingInventoryList = packingInventoryRepository.findAll();
        packingInventoryList = packingInventoryList.stream().filter(n-> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return packingInventoryList;
    }


    // Get PackingInventory
    public PackingInventory getPackingInventory(String companyId, String plantId, String languageId, String warehouseId, String packingMaterialNo) {

        PackingInventory packingInventory = packingInventoryRepository.findByCompanyIdAndPlantIdAndLanguageIdAndWarehouseIdAndPackingMaterialNoAndDeletionIndicator(
                companyId, plantId, languageId, warehouseId, packingMaterialNo, 0L);

        if(packingInventory != null) {
            return packingInventory;
        }else {
            throw new BadRequestException("The Given values of CompanyId " + companyId +
                    " PlantId " + plantId +
                    " LanguageId " + languageId +
                    " WarehouseId " + warehouseId +
                    " PackingMaterialNo " + packingMaterialNo + "Doesn't exist");
        }
    }

    // Create PackingInventory
    public PackingInventory createPackingInventory(PackingInventory packingInventory, String loginUserID) throws IllegalAccessException, InvocationTargetException{

        PackingInventory dbPackingInventory = getPackingInventory(packingInventory.getCompanyId(),
                packingInventory.getPlantId(), packingInventory.getLanguageId(), packingInventory.getWarehouseId(),
                packingInventory.getPackingMaterialNo());

        if(dbPackingInventory != null){
            throw new RuntimeException(" PackingInventory Record is Duplicate");
        }
        PackingInventory newPackInventory = new PackingInventory();
        BeanUtils.copyProperties(packingInventory, newPackInventory, CommonUtils.getNullPropertyNames(packingInventory));
        newPackInventory.setDeletionIndicator(0L);
        newPackInventory.setCreatedBy(loginUserID);
        newPackInventory.setCreatedOn(new Date());
        newPackInventory.setUpdatedBy(loginUserID);
        newPackInventory.setUpdatedOn(new Date());
        return packingInventoryRepository.save(newPackInventory);
    }


    //UpdatePackingInventory
    public PackingInventory updatePackingInventory(String companyId, String plantId, String languageId, String warehouseId,
                                                   String packingMaterialNo, String loginUserID, PackingInventory packingInventory)
    throws IllegalAccessException, InvocationTargetException{

        PackingInventory dbInventory = getPackingInventory(companyId, plantId, languageId, warehouseId, packingMaterialNo);

        if(dbInventory != null){
            BeanUtils.copyProperties(packingInventory, dbInventory, CommonUtils.getNullPropertyNames(packingInventory));
            dbInventory.setUpdatedBy(loginUserID);
            dbInventory.setUpdatedOn(new Date());
            return packingInventoryRepository.save(dbInventory);
        }else {
            throw new RuntimeException("Given Values Doesn't exist companyId - " + companyId +
                    " plantId " + plantId + " languageId " + languageId + " warehouseId " + warehouseId +
                    " packingMaterialNo " + packingMaterialNo);
        }
    }


    // Delete PackingInventory
    public void deletePackingInventory(String companyId, String languageId, String plantId,
                                                   String warehouseId, String packingMaterialNo, String loginUserID){

        PackingInventory dbPackingInventory = getPackingInventory(companyId, plantId, languageId, warehouseId, packingMaterialNo);

        if(dbPackingInventory != null){
            dbPackingInventory.setDeletionIndicator(1L);
            dbPackingInventory.setUpdatedBy(loginUserID);
            dbPackingInventory.setUpdatedOn(new Date());
            packingInventoryRepository.save(dbPackingInventory);
        }else {
            throw new RuntimeException("Given Values Doesn't exist CompanyId " + companyId + " plantId " + plantId +
                    " languageId " + languageId + " warehouseId " + warehouseId + " packingMaterialNo " + packingMaterialNo );
        }
    }

    public List<PackingInventory> findPackingInventory(FindPackingInventory findPackingInventory)
            throws Exception {
//        if (searchPeriodicHeader.getStartCreatedOn() != null && searchPeriodicHeader.getStartCreatedOn() != null) {
//            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPeriodicHeader.getStartCreatedOn(),
//                    searchPeriodicHeader.getEndCreatedOn());
//            searchPeriodicHeader.setStartCreatedOn(dates[0]);
//            searchPeriodicHeader.setEndCreatedOn(dates[1]);
//        }
        PackingInventorySpecification spec = new PackingInventorySpecification(findPackingInventory);
        List<PackingInventory> packingInventoryList = packingInventoryRepository.findAll(spec);
        return packingInventoryList;
    }

}
