package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.auth.AuthToken;
import com.tekclover.wms.api.masters.model.idmaster.ServiceTypeId;
import com.tekclover.wms.api.masters.model.threepl.pricelist.AddPriceList;
import com.tekclover.wms.api.masters.model.threepl.pricelist.FindPriceList;
import com.tekclover.wms.api.masters.model.threepl.pricelist.PriceList;
import com.tekclover.wms.api.masters.model.threepl.pricelist.UpdatePriceList;
import com.tekclover.wms.api.masters.repository.PriceListRepository;
import com.tekclover.wms.api.masters.repository.specification.PriceListSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PriceListService {

    @Autowired
    private IDMasterService idMasterService;
    @Autowired
    private AuthTokenService authTokenService;
    @Autowired
    private PriceListRepository priceListRepository;

    /**
     * getPriceLists
     *
     * @return
     */
    public List<PriceList> getPriceLists() {
        List<PriceList> priceLists = priceListRepository.findAll();
        priceLists = priceLists.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
//        List<PriceList> newPriceList = new ArrayList<>();
//        for (PriceList dbPriceList : priceLists) {
//            if (dbPriceList.getCompanyIdAndDescription() != null && dbPriceList.getPlantIdAndDescription() != null && dbPriceList.getWarehouseIdAndDescription() != null) {
//                IKeyValuePair iKeyValuePair = priceListRepository.getCompanyIdAndDescription(dbPriceList.getCompanyCodeId(), dbPriceList.getLanguageId());
//                IKeyValuePair iKeyValuePair1 = priceListRepository.getPlantIdAndDescription(dbPriceList.getPlantId(), dbPriceList.getLanguageId(), dbPriceList.getCompanyCodeId());
//                IKeyValuePair iKeyValuePair2 = priceListRepository.getWarehouseIdAndDescription(dbPriceList.getWarehouseId(), dbPriceList.getLanguageId(), dbPriceList.getCompanyCodeId(), dbPriceList.getPlantId());
//                IKeyValuePair iKeyValuePair3 = priceListRepository.getServiceTypeIdAndDescription(dbPriceList.getServiceTypeId(), dbPriceList.getLanguageId(), dbPriceList.getCompanyCodeId(), dbPriceList.getPlantId(), dbPriceList.getModuleId(), dbPriceList.getWarehouseId());
//                IKeyValuePair iKeyValuePair4 = priceListRepository.getModuleIdAndDescription(dbPriceList.getModuleId(), dbPriceList.getLanguageId(), dbPriceList.getCompanyCodeId(), dbPriceList.getPlantId(), dbPriceList.getWarehouseId());
//                if (iKeyValuePair != null) {
//                    dbPriceList.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
//                }
//                if (iKeyValuePair1 != null) {
//                    dbPriceList.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
//                }
//                if (iKeyValuePair2 != null) {
//                    dbPriceList.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
//                }
//                if (iKeyValuePair3 != null) {
//                    dbPriceList.setServiceTypeIdAndDescription(iKeyValuePair3.getServiceTypeId() + "-" + iKeyValuePair3.getDescription());
//                }
//                if (iKeyValuePair4 != null) {
//                    dbPriceList.setModuleIdAndDescription(iKeyValuePair4.getModuleId() + "-" + iKeyValuePair4.getDescription());
//                }
//            }
//            newPriceList.add(dbPriceList);
//        }
        return priceLists;
    }


    public PriceList getPriceList(String warehouseId, String moduleId, Long priceListId, Long serviceTypeId, Long chargeRangeId, String companyCodeId, String languageId, String plantId) {
        Optional<PriceList> dbPriceList =
                priceListRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndPriceListIdAndServiceTypeIdAndChargeRangeIdAndLanguageIdAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        moduleId,
                        priceListId,
                        serviceTypeId,
                        chargeRangeId,
                        languageId,
                        0L
                );
        if (dbPriceList.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "moduleId - " + moduleId +
                    "priceListId - " + priceListId +
                    "serviceTypeId-" + serviceTypeId +
                    "chargeRangeId-" + chargeRangeId +
                    " doesn't exist.");
        }
        PriceList newPriceList = new PriceList();
        BeanUtils.copyProperties(dbPriceList.get(), newPriceList, CommonUtils.getNullPropertyNames(dbPriceList));
        IKeyValuePair iKeyValuePair = priceListRepository.getCompanyIdAndDescription(companyCodeId, languageId);
        IKeyValuePair iKeyValuePair1 = priceListRepository.getPlantIdAndDescription(plantId, languageId, companyCodeId);
        IKeyValuePair iKeyValuePair2 = priceListRepository.getWarehouseIdAndDescription(warehouseId, languageId, companyCodeId, plantId);
        IKeyValuePair iKeyValuePair3 = priceListRepository.getServiceTypeIdAndDescription(serviceTypeId, languageId, companyCodeId, plantId, moduleId, warehouseId);
        IKeyValuePair iKeyValuePair4 = priceListRepository.getModuleIdAndDescription(moduleId, languageId, companyCodeId, plantId, warehouseId);
        if (iKeyValuePair != null) {
            newPriceList.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
        }
        if (iKeyValuePair1 != null) {
            newPriceList.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
        }
        if (iKeyValuePair2 != null) {
            newPriceList.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
        }
        if (iKeyValuePair3 != null) {
            newPriceList.setServiceTypeIdAndDescription(iKeyValuePair3.getServiceTypeId() + "-" + iKeyValuePair3.getDescription());
        }
        if (iKeyValuePair4 != null) {
            newPriceList.setModuleIdAndDescription(iKeyValuePair4.getModuleId() + "-" + iKeyValuePair4.getDescription());
        }
        return newPriceList;

    }


    /**
     * createPriceListId
     *
     * @param newPriceListId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PriceList createPriceList(AddPriceList newPriceListId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        PriceList dbPriceList = new PriceList();
        Optional<PriceList> duplicatePriceListId = priceListRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndPriceListIdAndServiceTypeIdAndChargeRangeIdAndLanguageIdAndDeletionIndicator(newPriceListId.getCompanyCodeId(), newPriceListId.getPlantId(),
                newPriceListId.getWarehouseId(), newPriceListId.getModuleId(), newPriceListId.getPriceListId(), newPriceListId.getServiceTypeId(), newPriceListId.getChargeRangeId(), newPriceListId.getLanguageId(), 0L);

        if (!duplicatePriceListId.isEmpty()) {
            throw new EntityNotFoundException("Record is Getting Duplicated");
        } else {
            AuthToken authTokenForIdMasterService = authTokenService.getIDMasterServiceAuthToken();
            ServiceTypeId dbServiceTypeId = idMasterService.getServiceTypeId(newPriceListId.getWarehouseId(), newPriceListId.getModuleId(), newPriceListId.getServiceTypeId(), newPriceListId.getCompanyCodeId(),
                    newPriceListId.getLanguageId(), newPriceListId.getPlantId(), authTokenForIdMasterService.getAccess_token());

            log.info("newPriceListId : " + newPriceListId);
            BeanUtils.copyProperties(newPriceListId, dbPriceList, CommonUtils.getNullPropertyNames(newPriceListId));

            dbPriceList.setDeletionIndicator(0L);
            dbPriceList.setCompanyIdAndDescription(dbServiceTypeId.getCompanyIdAndDescription());
            dbPriceList.setPlantIdAndDescription(dbServiceTypeId.getPlantIdAndDescription());
            dbPriceList.setWarehouseIdAndDescription(dbServiceTypeId.getWarehouseIdAndDescription());
            dbPriceList.setModuleIdAndDescription(dbServiceTypeId.getModuleIdAndDescription());
            dbPriceList.setServiceTypeIdAndDescription(dbServiceTypeId.getServiceTypeId() + "-" + dbServiceTypeId.getServiceTypeDescription());
            dbPriceList.setCreatedBy(loginUserID);
            dbPriceList.setUpdatedBy(loginUserID);
            dbPriceList.setCreatedOn(new Date());
            dbPriceList.setUpdatedOn(new Date());
            return priceListRepository.save(dbPriceList);
        }
    }


    /**
     * updatePriceListId
     *
     * @param loginUserID
     * @param moduleId
     * @param priceListId
     * @param serviceTypeId
     * @param updatePriceList
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PriceList updatePriceList(String warehouseId, String moduleId, Long priceListId, Long serviceTypeId, Long chargeRangeId, String companyCodeId, String languageId, String plantId, String loginUserID,
                                     UpdatePriceList updatePriceList)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        PriceList dbPriceList = getPriceList(warehouseId, moduleId, priceListId, serviceTypeId, chargeRangeId, companyCodeId, languageId, plantId);
        BeanUtils.copyProperties(updatePriceList, dbPriceList, CommonUtils.getNullPropertyNames(updatePriceList));
        dbPriceList.setUpdatedBy(loginUserID);
        dbPriceList.setUpdatedOn(new Date());
        return priceListRepository.save(dbPriceList);
    }

    /**
     *
     * @param deletePriceList
     * @param loginUserID
     */
    public void deletePriceLists(List<PriceList> deletePriceList, String loginUserID) {
      if(deletePriceList != null && !deletePriceList.isEmpty()){
          log.info("given values to delete Price List---> {}", deletePriceList);
          deletePriceList.parallelStream().forEach(deleteInput -> {
              PriceList dbPriceList = getPriceList(deleteInput.getWarehouseId(), deleteInput.getModuleId(), deleteInput.getPriceListId(),
                      deleteInput.getServiceTypeId(), deleteInput.getChargeRangeId(), deleteInput.getCompanyCodeId(), deleteInput.getLanguageId(), deleteInput.getPlantId());
              dbPriceList.setDeletionIndicator(1L);
              dbPriceList.setUpdatedBy(loginUserID);
              priceListRepository.save(dbPriceList);
          });
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + deletePriceList);
        }
    }

    //Find PriceList
    public List<PriceList> findPriceList(FindPriceList findPriceList) throws ParseException {

        PriceListSpecification spec = new PriceListSpecification(findPriceList);
        List<PriceList> results = priceListRepository.findAll(spec);
        results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        log.info("results: " + results);
        List<PriceList> newPriceList = new ArrayList<>();
        for (PriceList dbPriceList : results) {
            if (dbPriceList.getCompanyIdAndDescription() != null && dbPriceList.getPlantIdAndDescription() != null && dbPriceList.getWarehouseIdAndDescription() != null) {
                IKeyValuePair iKeyValuePair = priceListRepository.getCompanyIdAndDescription(dbPriceList.getCompanyCodeId(), dbPriceList.getLanguageId());
                IKeyValuePair iKeyValuePair1 = priceListRepository.getPlantIdAndDescription(dbPriceList.getPlantId(), dbPriceList.getLanguageId(), dbPriceList.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2 = priceListRepository.getWarehouseIdAndDescription(dbPriceList.getWarehouseId(), dbPriceList.getLanguageId(), dbPriceList.getCompanyCodeId(), dbPriceList.getPlantId());
                IKeyValuePair iKeyValuePair3 = priceListRepository.getServiceTypeIdAndDescription(dbPriceList.getServiceTypeId(), dbPriceList.getLanguageId(), dbPriceList.getCompanyCodeId(),
                        dbPriceList.getPlantId(), dbPriceList.getModuleId(), dbPriceList.getWarehouseId());
                IKeyValuePair iKeyValuePair4 = priceListRepository.getModuleIdAndDescription(dbPriceList.getModuleId(), dbPriceList.getLanguageId(), dbPriceList.getCompanyCodeId(), dbPriceList.getPlantId(), dbPriceList.getWarehouseId());
                if (iKeyValuePair != null) {
                    dbPriceList.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbPriceList.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbPriceList.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
                if (iKeyValuePair3 != null) {
                    dbPriceList.setServiceTypeIdAndDescription(iKeyValuePair3.getServiceTypeId() + "-" + iKeyValuePair3.getDescription());
                }
                if (iKeyValuePair4 != null) {
                    dbPriceList.setModuleIdAndDescription(iKeyValuePair4.getModuleId() + "-" + iKeyValuePair4.getDescription());
                }
            }
            newPriceList.add(dbPriceList);
        }
        return newPriceList;
    }

    /**
     * createPriceListId
     *
     * @param addPriceLists
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<PriceList> createPriceLists(List<AddPriceList> addPriceLists, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        List<PriceList> priceLists = new ArrayList<>();
        try {
            for (AddPriceList addPriceList : addPriceLists) {
                Optional<PriceList> duplicatePriceListId = priceListRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndPriceListIdAndServiceTypeIdAndChargeRangeIdAndLanguageIdAndDeletionIndicator(addPriceList.getCompanyCodeId(), addPriceList.getPlantId(),
                        addPriceList.getWarehouseId(), addPriceList.getModuleId(), addPriceList.getPriceListId(), addPriceList.getServiceTypeId(), addPriceList.getChargeRangeId(), addPriceList.getLanguageId(), 0L);
                if (!duplicatePriceListId.isEmpty()) {
                    throw new EntityNotFoundException("Record is Getting Duplicated");
                } else {
                    AuthToken authTokenForIdMasterService = authTokenService.getIDMasterServiceAuthToken();
                    ServiceTypeId dbServiceTypeId = idMasterService.getServiceTypeId(addPriceList.getWarehouseId(), addPriceList.getModuleId(), addPriceList.getServiceTypeId(),
                            addPriceList.getCompanyCodeId(), addPriceList.getLanguageId(), addPriceList.getPlantId(), authTokenForIdMasterService.getAccess_token());
//                    if(dbServiceTypeId == null){
//                        throw new BadRequestException("The given values : languageId - " + addPriceList.getLanguageId() + " plantId - " + addPriceList.getPlantId() +
//                       "warehouseId - " + addPriceList.getWarehouseId() +  "companyCodeId - " + addPriceList.getCompanyCodeId()+ " ModuleId - " + addPriceList.getModuleId()+
//                              "ServiceTypeId - " + addPriceList.getServiceTypeId()+ " doesn't exist in ServiceTypeId");
//                    }
                    log.info("addPriceList : " + addPriceList);
                    PriceList dbPriceList = new PriceList();
                    BeanUtils.copyProperties(addPriceList, dbPriceList, CommonUtils.getNullPropertyNames(addPriceList));

                    dbPriceList.setDeletionIndicator(0L);
                    dbPriceList.setCompanyIdAndDescription(dbServiceTypeId.getCompanyIdAndDescription());
                    dbPriceList.setPlantIdAndDescription(dbServiceTypeId.getPlantIdAndDescription());
                    dbPriceList.setWarehouseIdAndDescription(dbServiceTypeId.getWarehouseIdAndDescription());
                    dbPriceList.setModuleIdAndDescription(dbServiceTypeId.getModuleIdAndDescription());
                    dbPriceList.setServiceTypeIdAndDescription(dbServiceTypeId.getServiceTypeId() + "-" + dbServiceTypeId.getServiceTypeDescription());
                    dbPriceList.setCreatedBy(loginUserID);
                    dbPriceList.setUpdatedBy(loginUserID);
                    dbPriceList.setCreatedOn(new Date());
                    dbPriceList.setUpdatedOn(new Date());
                    priceListRepository.save(dbPriceList);
                    priceLists.add(dbPriceList);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return priceLists;
    }

    /**
     *
     * @param updatePriceLists
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    public List<PriceList> updatePriceLists(List<AddPriceList> updatePriceLists, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        List<PriceList> priceLists = new ArrayList<>();
        for (AddPriceList updatePrice : updatePriceLists) {
            priceListRepository.deletepriceList(updatePrice.getPriceListId(), updatePrice.getLanguageId(), updatePrice.getCompanyCodeId(),
                    updatePrice.getPlantId(), updatePrice.getWarehouseId(), updatePrice.getModuleId(), updatePrice.getServiceTypeId(), updatePrice.getChargeRangeId());
            log.info("PriceList Delete SuccessFully " + updatePrice.getPriceListId());
        }
        for (AddPriceList updatePriceList : updatePriceLists) {
                PriceList newPriceList = new PriceList();
                BeanUtils.copyProperties(updatePriceList, newPriceList, CommonUtils.getNullPropertyNames(updatePriceList));
                newPriceList.setUpdatedBy(loginUserID);
                newPriceList.setDeletionIndicator(0L);
                newPriceList.setUpdatedOn(new Date());
                priceListRepository.save(newPriceList);
                priceLists.add(newPriceList);
            }
        return priceLists;
    }
}
