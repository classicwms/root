package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.auth.AuthToken;
import com.tekclover.wms.api.masters.model.idmaster.ServiceTypeId;
import com.tekclover.wms.api.masters.model.threepl.pricelist.*;
import com.tekclover.wms.api.masters.repository.PriceListRepository;
import com.tekclover.wms.api.masters.repository.specification.PriceListSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
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
    public List<PriceList> getAllPriceListDetails() {
        try {
            List<PriceList> priceLists = priceListRepository.findAll();
            priceLists = priceLists.stream().filter(i -> i.getDeletionIndicator() == 0).collect(Collectors.toList());
            List<PriceList> newPriceList = new ArrayList<>();
            for (PriceList dbPriceList : priceLists) {
                if (dbPriceList.getCompanyIdAndDescription() != null && dbPriceList.getPlantIdAndDescription() != null && dbPriceList.getWarehouseIdAndDescription() != null) {
                    IKeyValuePair iKeyValuePair = priceListRepository.getCompanyIdAndDescription(dbPriceList.getCompanyCodeId(), dbPriceList.getLanguageId());
                    IKeyValuePair iKeyValuePair1 = priceListRepository.getPlantIdAndDescription(dbPriceList.getPlantId(), dbPriceList.getLanguageId(), dbPriceList.getCompanyCodeId());
                    IKeyValuePair iKeyValuePair2 = priceListRepository.getWarehouseIdAndDescription(dbPriceList.getWarehouseId(), dbPriceList.getLanguageId(), dbPriceList.getCompanyCodeId(), dbPriceList.getPlantId());
                    IKeyValuePair iKeyValuePair3 = priceListRepository.getServiceTypeIdAndDescription(dbPriceList.getServiceTypeId(), dbPriceList.getLanguageId(), dbPriceList.getCompanyCodeId(), dbPriceList.getPlantId(), dbPriceList.getModuleId(), dbPriceList.getWarehouseId());
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param warehouseId
     * @param moduleId
     * @param priceListId
     * @param serviceTypeId
     * @param chargeRangeId
     * @param companyCodeId
     * @param languageId
     * @param plantId
     * @return
     */
    public PriceList getPriceList(String warehouseId, String moduleId, Long priceListId, Long serviceTypeId,
                                  Long chargeRangeId, String companyCodeId, String languageId, String plantId) {
        try {
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
                        ", moduleId - " + moduleId +
                        ", priceListId - " + priceListId +
                        ", serviceTypeId-" + serviceTypeId +
                        " and chargeRangeId-" + chargeRangeId +
                        " doesn't exists");
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }


    /**
     * createPriceListId
     *
     * @param newPriceListId
     * @param loginUserID
     * @return
     */
    public PriceList createPriceList(AddPriceList newPriceListId, String loginUserID) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
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
     */
    public PriceList updatePriceList(String warehouseId, String moduleId, Long priceListId, Long serviceTypeId,
                                     Long chargeRangeId, String companyCodeId, String languageId, String plantId,
                                     String loginUserID, UpdatePriceList updatePriceList) {
        try {
            PriceList dbPriceList = getPriceList(warehouseId, moduleId, priceListId, serviceTypeId, chargeRangeId, companyCodeId, languageId, plantId);
            BeanUtils.copyProperties(updatePriceList, dbPriceList, CommonUtils.getNullPropertyNames(updatePriceList));
            dbPriceList.setUpdatedBy(loginUserID);
            dbPriceList.setUpdatedOn(new Date());
            return priceListRepository.save(dbPriceList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * deletePriceListId
     *
     * @param loginUserID
     * @param moduleId
     * @param priceListId
     * @param serviceTypeId
     */
    public void deletePriceList(String warehouseId, String moduleId, Long priceListId, Long serviceTypeId,
                                Long chargeRangeId, String companyCodeId, String languageId, String plantId, String loginUserID) {
        try {
            PriceList dbPriceList = getPriceList(warehouseId, moduleId, priceListId, serviceTypeId, chargeRangeId, companyCodeId, languageId, plantId);
            if (dbPriceList != null) {
                dbPriceList.setDeletionIndicator(1L);
                dbPriceList.setUpdatedBy(loginUserID);
                priceListRepository.save(dbPriceList);
            } else {
                throw new EntityNotFoundException("Error in deleting priceListId: " + priceListId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    //Find PriceList

    /**
     * @param findPriceList
     * @return
     */
    public List<PriceList> findPriceList(FindPriceList findPriceList) {

        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /*----------------------------------------------CRUD-Apis'_list--------------------------------------------------*/

    /**
     * Get Multiple PriceLists
     *
     * @param priceListInput
     * @return
     */
    public List<PriceList> getPriceListBulk(PriceListInput priceListInput) {
        List<PriceList> priceLists =
                priceListRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPriceListIdAndServiceTypeIdAndModuleIdAndChargeRangeIdAndDeletionIndicator(
                        priceListInput.getLanguageId(), priceListInput.getCompanyCodeId(), priceListInput.getPlantId(),
                        priceListInput.getWarehouseId(), priceListInput.getPriceListId(), priceListInput.getServiceTypeId(),
                        priceListInput.getModuleId(), priceListInput.getChargeRangeId(), 0L);
        if (priceLists.isEmpty()) {
            throw new BadRequestException("The given values : languageId - " + priceListInput.getLanguageId()
                    + ", companyCodeId - " + priceListInput.getCompanyCodeId() + ", plantId - " + priceListInput.getPlantId()
                    + ", warehouseId - " + priceListInput.getWarehouseId() + ", priceListId - " + priceListInput.getPriceListId()
                    + ", serviceTypeId - " + priceListInput.getServiceTypeId() + ", moduleId - " + priceListInput.getModuleId()
                    + "and chargeRangeId - " + priceListInput.getChargeRangeId() + " doesn't exists.");
        }
        return priceLists;
    }

    /**
     * Create Multiple PriceLists
     *
     * @param addPriceLists
     * @param loginUserID
     * @return
     */
    public List<PriceList> createPriceListBulk(List<AddPriceList> addPriceLists, String loginUserID) {
        log.info("new PriceLists --> " + addPriceLists);
        try {
            List<PriceList> newPriceLists = new ArrayList<>();
            for (AddPriceList addPriceList : addPriceLists) {

                Optional<PriceList> duplicatePriceList = priceListRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndPriceListIdAndServiceTypeIdAndChargeRangeIdAndLanguageIdAndDeletionIndicator(
                        addPriceList.getCompanyCodeId(), addPriceList.getPlantId(), addPriceList.getWarehouseId(), addPriceList.getModuleId(),
                        addPriceList.getPriceListId(), addPriceList.getServiceTypeId(), addPriceList.getChargeRangeId(), addPriceList.getLanguageId(), 0L);

                if (duplicatePriceList.isPresent()) {
                    throw new EntityNotFoundException("Record is Getting Duplicated with the given values : priceListId - " + addPriceList.getPriceListId());
                } else {
                    AuthToken authTokenForIdMasterService = authTokenService.getIDMasterServiceAuthToken();
                    ServiceTypeId dbServiceTypeId = idMasterService.getServiceTypeId(addPriceList.getWarehouseId(), addPriceList.getModuleId(), addPriceList.getServiceTypeId(), addPriceList.getCompanyCodeId(),
                            addPriceList.getLanguageId(), addPriceList.getPlantId(), authTokenForIdMasterService.getAccess_token());

                    log.info("newPriceListId : " + addPriceList);
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
                    PriceList savedPriceList = priceListRepository.save(dbPriceList);
                    log.info("new PriceList created --> " + savedPriceList);
                    newPriceLists.add(savedPriceList);
                }
            }
            return newPriceLists;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Update Multiple PriceLists
     *
     * @param updatePriceLists
     * @param loginUserID
     * @return
     */
    public List<PriceList> updatePriceListBulk(List<AddPriceList> updatePriceLists, String loginUserID) {
        try {
            List<PriceList> updatedPriceLists = new ArrayList<>();
            for (AddPriceList updatePriceList : updatePriceLists) {
                PriceList dbPriceList = getPriceList(updatePriceList.getWarehouseId(), updatePriceList.getModuleId(),
                        updatePriceList.getPriceListId(), updatePriceList.getServiceTypeId(), updatePriceList.getChargeRangeId(),
                        updatePriceList.getCompanyCodeId(), updatePriceList.getLanguageId(), updatePriceList.getPlantId());
                BeanUtils.copyProperties(updatePriceList, dbPriceList, CommonUtils.getNullPropertyNames(updatePriceList));
                dbPriceList.setUpdatedBy(loginUserID);
                dbPriceList.setUpdatedOn(new Date());
                PriceList priceList = priceListRepository.save(dbPriceList);
                log.info("updated PriceList --> " + priceList);
                updatedPriceLists.add(priceList);
            }
            return updatedPriceLists;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete Multiple PriceLists
     *
     * @param priceListInputList
     * @param loginUserID
     */
    public void deletePriceListBulk(List<PriceListInput> priceListInputList, String loginUserID) {

        if (priceListInputList != null || !priceListInputList.isEmpty()) {
            for (PriceListInput priceListInput : priceListInputList) {
                PriceList dbPriceList = getPriceList(priceListInput.getWarehouseId(), priceListInput.getModuleId(),
                        priceListInput.getPriceListId(), priceListInput.getServiceTypeId(), priceListInput.getChargeRangeId(),
                        priceListInput.getCompanyCodeId(), priceListInput.getLanguageId(), priceListInput.getPlantId());
                if (dbPriceList != null) {
                    dbPriceList.setDeletionIndicator(1L);
                    dbPriceList.setUpdatedBy(loginUserID);
                    dbPriceList.setUpdatedOn(new Date());
                    priceListRepository.save(dbPriceList);
                } else {
                    throw new BadRequestException("Error in deleting priceListId - " + priceListInput.getPriceListId());
                }
            }
        }
    }


}