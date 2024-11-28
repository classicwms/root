package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.businesspartner.AddBusinessPartner;
import com.tekclover.wms.api.masters.model.businesspartner.BusinessPartner;
import com.tekclover.wms.api.masters.model.businesspartner.SearchBusinessPartner;
import com.tekclover.wms.api.masters.model.businesspartner.UpdateBusinessPartner;
import com.tekclover.wms.api.masters.model.businesspartner.v2.BusinessPartnerV2;
import com.tekclover.wms.api.masters.repository.BusinessPartnerRepository;
import com.tekclover.wms.api.masters.repository.BusinessPartnerV2Repository;
import com.tekclover.wms.api.masters.repository.WarehouseRepository;
import com.tekclover.wms.api.masters.repository.specification.BusinessPartnerSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BusinessPartnerService {
    @Autowired
    private BusinessPartnerV2Repository businessPartnerV2Repository;

    @Autowired
    AuthTokenService authTokenService;
    @Autowired
    IDMasterService idMasterService;
    @Autowired
    private BusinessPartnerRepository businesspartnerRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    /**
     * getBusinessPartners
     *
     * @return
     */
    public List<BusinessPartner> getBusinessPartners() {
        try {
            List<BusinessPartner> businesspartnerList = businesspartnerRepository.findAll();
            //log.info("businesspartnerList : " + businesspartnerList);
            businesspartnerList = businesspartnerList.stream()
                    .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                    .collect(Collectors.toList());
            return businesspartnerList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * getBusinessPartner
     *
     * @param partnerCode
     * @return
     */
    public BusinessPartner getBusinessPartner(String partnerCode, String companyCodeId, String plantId,
                                              String warehouseId, String languageId, Long businessPartnerType) {
        try {
            Optional<BusinessPartner> businesspartner =
                    businesspartnerRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndBusinessPartnerTypeAndLanguageIdAndDeletionIndicator(
                            companyCodeId,
                            plantId,
                            warehouseId,
                            partnerCode,
                            businessPartnerType,
                            languageId,
                            0L);
            if (businesspartner.isEmpty()) {
                throw new BadRequestException("The given values:" +
                        "partnerCode " + partnerCode +
                        "companyCodeId" + companyCodeId +
                        "plantId" + plantId + " doesn't exist.");
            }
            return businesspartner.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param searchBusinessPartner
     * @return
     */
    public List<BusinessPartner> findBusinessPartner(SearchBusinessPartner searchBusinessPartner) {
        try {
            if (searchBusinessPartner.getStartCreatedOn() != null && searchBusinessPartner.getEndCreatedOn() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchBusinessPartner.getStartCreatedOn(), searchBusinessPartner.getEndCreatedOn());
                searchBusinessPartner.setStartCreatedOn(dates[0]);
                searchBusinessPartner.setEndCreatedOn(dates[1]);
            }

            if (searchBusinessPartner.getStartUpdatedOn() != null && searchBusinessPartner.getEndUpdatedOn() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchBusinessPartner.getStartUpdatedOn(), searchBusinessPartner.getEndUpdatedOn());
                searchBusinessPartner.setStartUpdatedOn(dates[0]);
                searchBusinessPartner.setEndUpdatedOn(dates[1]);
            }

            BusinessPartnerSpecification spec = new BusinessPartnerSpecification(searchBusinessPartner);
            List<BusinessPartner> results = businesspartnerRepository.findAll(spec);
//        log.info("results: " + results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * createBusinessPartner
     *
     * @param newBusinessPartner
     * @return
     */
    public BusinessPartner createBusinessPartner(AddBusinessPartner newBusinessPartner, String loginUserID) {
        try {
            Optional<BusinessPartner> duplicateBusinessPartner =
                    businesspartnerRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndBusinessPartnerTypeAndLanguageIdAndDeletionIndicator(
                            newBusinessPartner.getCompanyCodeId(), newBusinessPartner.getPlantId(), newBusinessPartner.getWarehouseId(),
                            newBusinessPartner.getPartnerCode(), newBusinessPartner.getBusinessPartnerType(), newBusinessPartner.getLanguageId(), 0L);
            BusinessPartner dbBusinessPartner = new BusinessPartner();
            if (!duplicateBusinessPartner.isEmpty()) {
                throw new EntityNotFoundException("Record is Getting Duplicated");
            } else {
                BeanUtils.copyProperties(newBusinessPartner, dbBusinessPartner, CommonUtils.getNullPropertyNames(newBusinessPartner));
                dbBusinessPartner.setDeletionIndicator(0L);
                dbBusinessPartner.setCreatedBy(loginUserID);
                dbBusinessPartner.setUpdatedBy(loginUserID);
                dbBusinessPartner.setCreatedOn(new Date());
                dbBusinessPartner.setUpdatedOn(new Date());
                return businesspartnerRepository.save(dbBusinessPartner);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param partnerCode
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @param businessPartnerType
     * @param updateBusinessPartner
     * @param loginUserID
     * @return
     */
    public BusinessPartner updateBusinessPartner(String partnerCode, String companyCodeId, String plantId, String warehouseId,
                                                 String languageId, Long businessPartnerType,
                                                 UpdateBusinessPartner updateBusinessPartner, String loginUserID) {
        try {
            BusinessPartner dbBusinessPartner = getBusinessPartner(partnerCode, companyCodeId, plantId, warehouseId, languageId, businessPartnerType);
            BeanUtils.copyProperties(updateBusinessPartner, dbBusinessPartner, CommonUtils.getNullPropertyNames(updateBusinessPartner));
            dbBusinessPartner.setUpdatedBy(loginUserID);
            dbBusinessPartner.setUpdatedOn(new Date());
            return businesspartnerRepository.save(dbBusinessPartner);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param partnerCode
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @param businessPartnerType
     * @param loginUserID
     */
    public void deleteBusinessPartner(String partnerCode, String companyCodeId, String plantId, String warehouseId,
                                      String languageId, Long businessPartnerType, String loginUserID) {
        try {
            BusinessPartner businesspartner = getBusinessPartner(partnerCode, companyCodeId, plantId, warehouseId, languageId, businessPartnerType);
            if (businesspartner != null) {
                businesspartner.setDeletionIndicator(1L);
                businesspartner.setUpdatedBy(loginUserID);
                businesspartner.setUpdatedOn(new Date());
                businesspartnerRepository.save(businesspartner);
            } else {
                throw new EntityNotFoundException("Error in deleting Id:" + partnerCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    //========================================================================V2====================================================================

    public BusinessPartnerV2 getBusinessPartnerV2(String partnerCode, String companyCodeId, String plantId,
                                                  String warehouseId, String languageId, Long businessPartnerType) {
        try {
            Optional<BusinessPartnerV2> businesspartner =
                    businessPartnerV2Repository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndBusinessPartnerTypeAndLanguageIdAndDeletionIndicator(
                            companyCodeId,
                            plantId,
                            warehouseId,
                            partnerCode,
                            businessPartnerType,
                            languageId,
                            0L);
            if (businesspartner.isEmpty()) {
                throw new BadRequestException("The given values:" +
                        "partnerCode " + partnerCode +
                        "companyCodeId" + companyCodeId +
                        "plantId" + plantId + " doesn't exist.");
            }
            return businesspartner.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param newBusinessPartner
     * @return
     */
    public BusinessPartnerV2 createBusinessPartnerV2(BusinessPartnerV2 newBusinessPartner, String loginUserId) {

        try {
            Optional<BusinessPartnerV2> duplicateBusinessPartner =
                    businessPartnerV2Repository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndBusinessPartnerTypeAndLanguageIdAndDeletionIndicator(
                            newBusinessPartner.getCompanyCodeId(), newBusinessPartner.getPlantId(),
                            newBusinessPartner.getWarehouseId(), newBusinessPartner.getPartnerCode(),
                            newBusinessPartner.getBusinessPartnerType(), newBusinessPartner.getLanguageId(), 0L);
            BusinessPartnerV2 dbBusinessPartner = new BusinessPartnerV2();
            if (!duplicateBusinessPartner.isEmpty()) {
                throw new EntityNotFoundException("Record is Getting Duplicated");
            } else {
                BeanUtils.copyProperties(newBusinessPartner, dbBusinessPartner, CommonUtils.getNullPropertyNames(newBusinessPartner));
                dbBusinessPartner.setDeletionIndicator(0L);
                dbBusinessPartner.setCreatedBy(loginUserId);
                dbBusinessPartner.setUpdatedBy(loginUserId);
                dbBusinessPartner.setCreatedOn(new Date());
                dbBusinessPartner.setUpdatedOn(new Date());
                return businessPartnerV2Repository.save(dbBusinessPartner);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param partnerCode
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @param businessPartnerType
     * @param updateBusinessPartner
     * @param loginUserID
     * @return
     */
    public BusinessPartnerV2 updateBusinessPartnerV2(String partnerCode, String companyCodeId, String plantId,
                                                     String warehouseId, String languageId, Long businessPartnerType,
                                                     BusinessPartnerV2 updateBusinessPartner, String loginUserID) {

        try {
            BusinessPartnerV2 dbBusinessPartner = businessPartnerV2Repository.
                    findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPartnerCodeAndDeletionIndicator(
                            companyCodeId, plantId, languageId, warehouseId, partnerCode, 0L);
            BeanUtils.copyProperties(updateBusinessPartner, dbBusinessPartner, CommonUtils.getNullPropertyNames(updateBusinessPartner));
            dbBusinessPartner.setUpdatedBy(loginUserID);
            dbBusinessPartner.setUpdatedOn(new Date());
            return businessPartnerV2Repository.save(dbBusinessPartner);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param partnerCode
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @param businessPartnerType
     */
    public Boolean deleteBusinessPartnerV2(String partnerCode, String companyCodeId, String plantId,
                                           String warehouseId, String languageId, Long businessPartnerType) {
        try {
            BusinessPartnerV2 businesspartner = getBusinessPartnerV2(partnerCode, companyCodeId, plantId, warehouseId, languageId, businessPartnerType);
            if (businesspartner != null) {
                businessPartnerV2Repository.delete(businesspartner);
                return true;
            } else {
                throw new EntityNotFoundException("Error in deleting Id:" + partnerCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }
}