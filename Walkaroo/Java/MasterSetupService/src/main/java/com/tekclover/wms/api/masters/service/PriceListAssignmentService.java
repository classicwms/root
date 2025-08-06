package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.auth.AuthToken;
import com.tekclover.wms.api.masters.model.businesspartner.BusinessPartner;
import com.tekclover.wms.api.masters.model.idmaster.WarehouseId;
import com.tekclover.wms.api.masters.model.threepl.pricelist.PriceList;
import com.tekclover.wms.api.masters.model.threepl.pricelistassignment.*;
import com.tekclover.wms.api.masters.repository.BusinessPartnerRepository;
import com.tekclover.wms.api.masters.repository.PriceListAssignmentRepository;
import com.tekclover.wms.api.masters.repository.PriceListRepository;
import com.tekclover.wms.api.masters.repository.specification.PriceListAssignmentSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;
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
public class PriceListAssignmentService {

    @Autowired
    private IDMasterService idMasterService;
    @Autowired
    private AuthTokenService authTokenService;
    @Autowired
    private PriceListRepository priceListRepository;

    @Autowired
    private BusinessPartnerRepository businessPartnerRepository;
    @Autowired
    private PriceListService priceListService;
    @Autowired
    private BusinessPartnerService businessPartnerService;
    @Autowired
    private PriceListAssignmentRepository priceListAssignmentRepository;

    /**
     * getPriceListAssignments
     *
     * @return
     */
    public List<PriceListAssignment> getPriceListAssignments() {
        List<PriceListAssignment> PriceListAssignmentList = priceListAssignmentRepository.findAll();
        PriceListAssignmentList = PriceListAssignmentList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        List<PriceListAssignment> newPriceListAssignment = new ArrayList<>();
        for (PriceListAssignment dbPriceListAssignment : PriceListAssignmentList) {
            if (dbPriceListAssignment.getCompanyIdAndDescription() != null && dbPriceListAssignment.getPlantIdAndDescription() != null && dbPriceListAssignment.getWarehouseIdAndDescription() != null) {
                IKeyValuePair iKeyValuePair = priceListRepository.getCompanyIdAndDescription(dbPriceListAssignment.getCompanyCodeId(), dbPriceListAssignment.getLanguageId());
                IKeyValuePair iKeyValuePair1 = priceListRepository.getPlantIdAndDescription(dbPriceListAssignment.getPlantId(), dbPriceListAssignment.getLanguageId(), dbPriceListAssignment.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2 = priceListRepository.getWarehouseIdAndDescription(dbPriceListAssignment.getWarehouseId(), dbPriceListAssignment.getLanguageId(), dbPriceListAssignment.getCompanyCodeId(), dbPriceListAssignment.getPlantId());

                IKeyValuePair iKeyValuePair3 = priceListRepository.getPriceListIdAndDescription(dbPriceListAssignment.getPriceListId(), dbPriceListAssignment.getLanguageId(), dbPriceListAssignment.getCompanyCodeId(), dbPriceListAssignment.getPlantId(), dbPriceListAssignment.getWarehouseId(),
                        dbPriceListAssignment.getModuleId(),dbPriceListAssignment.getServiceTypeId(),dbPriceListAssignment.getChargeRangeId());

                IKeyValuePair iKeyValuePair4 = businessPartnerRepository.getPartnerCodeAndDescription(dbPriceListAssignment.getPartnerCode(), dbPriceListAssignment.getLanguageId(), dbPriceListAssignment.getCompanyCodeId(), dbPriceListAssignment.getPlantId(), dbPriceListAssignment.getWarehouseId(),dbPriceListAssignment.getBusinessPartnerType());

                if (iKeyValuePair != null) {
                    dbPriceListAssignment.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbPriceListAssignment.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbPriceListAssignment.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
                if (iKeyValuePair3 != null) {
                    dbPriceListAssignment.setPriceListIdAndDescription(iKeyValuePair3.getPriceListId() + "-" + iKeyValuePair3.getDescription());
                }
                if (iKeyValuePair4 != null) {
                    dbPriceListAssignment.setPartnerCodeAndDescription(iKeyValuePair4.getPartnerCode() + "-" + iKeyValuePair4.getDescription());
                }
            }
            newPriceListAssignment.add(dbPriceListAssignment);
        }
        return newPriceListAssignment;
    }

    /**
     * getPriceListAssignment
     *
     * @param partnerCode
     * @param priceListId
     * @return
     */
    public PriceListAssignment getPriceListAssignment(String warehouseId, String partnerCode, Long priceListId, String companyCodeId, String languageId, String plantId) {
        Optional<PriceListAssignment> dbPriceListAssignment =
                priceListAssignmentRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndPriceListIdAndLanguageIdAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        warehouseId,
                        partnerCode,
                        priceListId,
                        languageId,
                        0L
                );
        if (dbPriceListAssignment.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "partnerCode - " + partnerCode +
                    "priceListId-" + priceListId +
                    " doesn't exist.");

        }
        PriceListAssignment newPriceListAssignMent = new PriceListAssignment();
        BeanUtils.copyProperties(dbPriceListAssignment.get(), newPriceListAssignMent, CommonUtils.getNullPropertyNames(dbPriceListAssignment));
        IKeyValuePair iKeyValuePair = priceListRepository.getCompanyIdAndDescription(companyCodeId,plantId);
        IKeyValuePair iKeyValuePair1 = priceListRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
        IKeyValuePair iKeyValuePair2 = priceListRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
        IKeyValuePair iKeyValuePair3 = priceListRepository.getPriceListIdAndDescription(priceListId,languageId,companyCodeId,plantId,warehouseId, newPriceListAssignMent.getModuleId(),
                newPriceListAssignMent.getServiceTypeId(), newPriceListAssignMent.getChargeRangeId());

        IKeyValuePair iKeyValuePair4 = businessPartnerRepository.getPartnerCodeAndDescription(partnerCode,languageId,companyCodeId,plantId,warehouseId, newPriceListAssignMent.getBusinessPartnerType());

        if (iKeyValuePair != null) {
            newPriceListAssignMent.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
        }
        if (iKeyValuePair1 != null) {
            newPriceListAssignMent.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
        }
        if (iKeyValuePair2 != null) {
            newPriceListAssignMent.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
        }
        if (iKeyValuePair3 != null) {
            newPriceListAssignMent.setPriceListIdAndDescription(iKeyValuePair3.getPriceListId() + "-" + iKeyValuePair3.getDescription());
        }
        if (iKeyValuePair4 != null) {
            newPriceListAssignMent.setPartnerCodeAndDescription(iKeyValuePair4.getPartnerCode() + "-" + iKeyValuePair4.getDescription());
        }

        return dbPriceListAssignment.get();
    }

    /**
     * createPriceListAssignment
     *
     * @param newPriceListAssignment
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PriceListAssignment createPriceListAssignment(AddPriceListAssignment newPriceListAssignment, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        PriceListAssignment dbPriceListAssignment = new PriceListAssignment();
        Optional<PriceListAssignment> duplicatePriceListAssignment = priceListAssignmentRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndPriceListIdAndLanguageIdAndDeletionIndicator(newPriceListAssignment.getCompanyCodeId(), newPriceListAssignment.getPlantId(), newPriceListAssignment.getWarehouseId(), newPriceListAssignment.getPartnerCode(), newPriceListAssignment.getPriceListId(), newPriceListAssignment.getLanguageId(), 0L);
        if (!duplicatePriceListAssignment.isEmpty()) {
            throw new EntityNotFoundException("Record is Getting Duplicated");
        } else {

            IKeyValuePair iKeyValuePair = businessPartnerRepository.getPartnerCodeAndDescription(newPriceListAssignment.getPartnerCode(), newPriceListAssignment.getLanguageId(), newPriceListAssignment.getCompanyCodeId(), newPriceListAssignment.getPlantId(), newPriceListAssignment.getWarehouseId(), newPriceListAssignment.getBusinessPartnerType());
            IKeyValuePair iKeyValuePair1 = priceListRepository.getCompanyIdAndDescription(newPriceListAssignment.getCompanyCodeId(), newPriceListAssignment.getLanguageId());
            IKeyValuePair iKeyValuePair2 = priceListRepository.getPlantIdAndDescription(newPriceListAssignment.getPlantId(), newPriceListAssignment.getLanguageId(), newPriceListAssignment.getCompanyCodeId());
            IKeyValuePair iKeyValuePair3 = priceListRepository.getWarehouseIdAndDescription(newPriceListAssignment.getWarehouseId(), newPriceListAssignment.getLanguageId(), newPriceListAssignment.getCompanyCodeId(), newPriceListAssignment.getPlantId());
            IKeyValuePair iKeyValuePair4 = priceListRepository.getPriceListIdAndDescription(newPriceListAssignment.getPriceListId(), newPriceListAssignment.getLanguageId(), newPriceListAssignment.getCompanyCodeId(), newPriceListAssignment.getPlantId(), newPriceListAssignment.getWarehouseId(), newPriceListAssignment.getModuleId(),
                    newPriceListAssignment.getServiceTypeId(), newPriceListAssignment.getChargeRangeId());

            log.info("newPriceListAssignment : " + newPriceListAssignment);
            BeanUtils.copyProperties(newPriceListAssignment, dbPriceListAssignment, CommonUtils.getNullPropertyNames(newPriceListAssignment));
            dbPriceListAssignment.setDeletionIndicator(0L);
            dbPriceListAssignment.setCompanyIdAndDescription(iKeyValuePair1.getCompanyCodeId()+"-"+iKeyValuePair1.getDescription());
            dbPriceListAssignment.setPlantIdAndDescription(iKeyValuePair2.getPlantId()+"-"+iKeyValuePair2.getDescription());
            dbPriceListAssignment.setWarehouseIdAndDescription(iKeyValuePair3.getWarehouseId()+"-"+iKeyValuePair3.getDescription());
            dbPriceListAssignment.setPriceListIdAndDescription(iKeyValuePair4.getPriceListId() + "-" + iKeyValuePair4.getDescription());
            dbPriceListAssignment.setPartnerCodeAndDescription(iKeyValuePair.getPartnerCode() + "-" + iKeyValuePair.getDescription());
            dbPriceListAssignment.setCreatedBy(loginUserID);
            dbPriceListAssignment.setUpdatedBy(loginUserID);
            dbPriceListAssignment.setCreatedOn(new Date());
            dbPriceListAssignment.setUpdatedOn(new Date());
            return priceListAssignmentRepository.save(dbPriceListAssignment);
        }
    }

    /**
     * updatePriceListAssignment
     *
     * @param loginUserID
     * @param partnerCode
     * @param updatePriceListAssignment
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PriceListAssignment updatePriceListAssignment(String warehouseId, Long priceListId, String partnerCode,
                                                         String companyCodeId, String languageId, String plantId, String loginUserID,
                                                         UpdatePriceListAssignment updatePriceListAssignment)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        PriceListAssignment dbPriceListAssignment = getPriceListAssignment(warehouseId, partnerCode, priceListId, companyCodeId, languageId, plantId);
        BeanUtils.copyProperties(updatePriceListAssignment, dbPriceListAssignment, CommonUtils.getNullPropertyNames(updatePriceListAssignment));
        dbPriceListAssignment.setUpdatedBy(loginUserID);
        dbPriceListAssignment.setUpdatedOn(new Date());
        return priceListAssignmentRepository.save(dbPriceListAssignment);
    }

    /**
     * deletePriceListAssignment
     *
     * @param loginUserID
     * @param priceListId
     * @param partnerCode
     */
    public void deletePriceListAssignment(String warehouseId, Long priceListId, String partnerCode, String companyCodeId, String languageId, String plantId, String loginUserID) {
        PriceListAssignment dbPriceListAssignment = getPriceListAssignment(warehouseId, partnerCode, priceListId, companyCodeId, languageId, plantId);
        if (dbPriceListAssignment != null) {
            dbPriceListAssignment.setDeletionIndicator(1L);
            dbPriceListAssignment.setUpdatedBy(loginUserID);
            priceListAssignmentRepository.save(dbPriceListAssignment);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + priceListId);
        }
    }

    //Find PriceListAssignment
    public List<PriceListAssignment> findPriceListAssignment(FindPriceListAssignment findPriceListAssignment) throws ParseException {

        PriceListAssignmentSpecification spec = new PriceListAssignmentSpecification(findPriceListAssignment);
        List<PriceListAssignment> results = priceListAssignmentRepository.findAll(spec);
        results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        log.info("results: " + results);
        List<PriceListAssignment> newPriceListAssignment = new ArrayList<>();
        for (PriceListAssignment dbPriceListAssignment : results) {

            if (dbPriceListAssignment.getCompanyIdAndDescription() != null && dbPriceListAssignment.getPlantIdAndDescription() != null && dbPriceListAssignment.getWarehouseIdAndDescription() != null) {
                IKeyValuePair iKeyValuePair = priceListRepository.getCompanyIdAndDescription(dbPriceListAssignment.getCompanyCodeId(), dbPriceListAssignment.getLanguageId());
                IKeyValuePair iKeyValuePair1 = priceListRepository.getPlantIdAndDescription(dbPriceListAssignment.getPlantId(), dbPriceListAssignment.getLanguageId(), dbPriceListAssignment.getCompanyCodeId());
                IKeyValuePair iKeyValuePair2 = priceListRepository.getWarehouseIdAndDescription(dbPriceListAssignment.getWarehouseId(), dbPriceListAssignment.getLanguageId(), dbPriceListAssignment.getCompanyCodeId(), dbPriceListAssignment.getPlantId());
                IKeyValuePair iKeyValuePair3 = priceListRepository.getPriceListIdAndDescription(dbPriceListAssignment.getPriceListId(), dbPriceListAssignment.getLanguageId(), dbPriceListAssignment.getCompanyCodeId(), dbPriceListAssignment.getPlantId(), dbPriceListAssignment.getWarehouseId(),
                        dbPriceListAssignment.getModuleId(), dbPriceListAssignment.getServiceTypeId(), dbPriceListAssignment.getChargeRangeId());

                IKeyValuePair iKeyValuePair4 = businessPartnerRepository.getPartnerCodeAndDescription(dbPriceListAssignment.getPartnerCode(), dbPriceListAssignment.getLanguageId(), dbPriceListAssignment.getCompanyCodeId(), dbPriceListAssignment.getPlantId(), dbPriceListAssignment.getWarehouseId(), dbPriceListAssignment.getBusinessPartnerType());
                if (iKeyValuePair != null) {
                    dbPriceListAssignment.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
                }
                if (iKeyValuePair1 != null) {
                    dbPriceListAssignment.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
                }
                if (iKeyValuePair2 != null) {
                    dbPriceListAssignment.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
                }
                if (iKeyValuePair3 != null) {
                    dbPriceListAssignment.setPriceListIdAndDescription(iKeyValuePair3.getPriceListId() + "-" + iKeyValuePair3.getDescription());
                }
                if (iKeyValuePair4 != null) {
                    dbPriceListAssignment.setPartnerCodeAndDescription(iKeyValuePair4.getPartnerCode() + "-" + iKeyValuePair4.getDescription());
                }
            }
            newPriceListAssignment.add(dbPriceListAssignment);
        }
        return newPriceListAssignment;
    }
}
