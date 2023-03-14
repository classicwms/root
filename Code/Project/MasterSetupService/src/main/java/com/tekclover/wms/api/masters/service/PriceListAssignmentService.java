package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.threepl.pricelistassignment.*;
import com.tekclover.wms.api.masters.repository.PriceListAssignmentRepository;
import com.tekclover.wms.api.masters.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PriceListAssignmentService extends BaseService{


    @Autowired
    private PriceListAssignmentRepository priceListAssignmentRepository;

    /**
     * getPriceListAssignments
     * @return
     */
    public List<PriceListAssignment> getPriceListAssignments () {
        List<PriceListAssignment> PriceListAssignmentList =  priceListAssignmentRepository.findAll();
        PriceListAssignmentList = PriceListAssignmentList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return PriceListAssignmentList;
    }

    /**
     * getPriceListAssignment
     * @param partnerCode
     * @param priceListId
     * @return
     */
    public PriceListAssignment getPriceListAssignment (String warehouseId,String partnerCode,Long priceListId) {
        Optional<PriceListAssignment> dbPriceListAssignment =
                priceListAssignmentRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndPriceListIdAndLanguageIdAndDeletionIndicator(
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        partnerCode,
                        priceListId,
                        getLanguageId(),
                        0L
                );
        if (dbPriceListAssignment.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "partnerCode - " + partnerCode +
                    "priceListId-"+priceListId+
                    " doesn't exist.");

        }
        return dbPriceListAssignment.get();
    }

    /**
     * createPriceListAssignment
     * @param newPriceListAssignment
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PriceListAssignment createPriceListAssignment (AddPriceListAssignment newPriceListAssignment, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PriceListAssignment dbPriceListAssignment = new PriceListAssignment();
        log.info("newPriceListAssignment : " + newPriceListAssignment);
        BeanUtils.copyProperties(newPriceListAssignment, dbPriceListAssignment, CommonUtils.getNullPropertyNames(newPriceListAssignment));
        dbPriceListAssignment.setCompanyCodeId(getCompanyCode());
        dbPriceListAssignment.setPlantId(getPlantId());
        dbPriceListAssignment.setDeletionIndicator(0L);
        dbPriceListAssignment.setCreatedBy(loginUserID);
        dbPriceListAssignment.setUpdatedBy(loginUserID);
        dbPriceListAssignment.setCreatedOn(new Date());
        dbPriceListAssignment.setUpdatedOn(new Date());
        return priceListAssignmentRepository.save(dbPriceListAssignment);
    }

    /**
     * updatePriceListAssignment
     * @param loginUserID
     * @param partnerCode
     * @param updatePriceListAssignment
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PriceListAssignment updatePriceListAssignment(String warehouseId, Long priceListId ,String partnerCode, String loginUserID,
                                 UpdatePriceListAssignment updatePriceListAssignment)//String partnerCode,Long priceListId
            throws IllegalAccessException, InvocationTargetException {
        PriceListAssignment dbPriceListAssignment = getPriceListAssignment(warehouseId,partnerCode, priceListId);
        BeanUtils.copyProperties(updatePriceListAssignment, dbPriceListAssignment, CommonUtils.getNullPropertyNames(updatePriceListAssignment));
        dbPriceListAssignment.setUpdatedBy(loginUserID);
        dbPriceListAssignment.setUpdatedOn(new Date());
        return priceListAssignmentRepository.save(dbPriceListAssignment);
    }

    /**
     * deletePriceListAssignment
     * @param loginUserID
     * @param priceListId
     * @param partnerCode
     */
    public void deletePriceListAssignment(String warehouseId,Long priceListId,String partnerCode, String loginUserID) {
        PriceListAssignment dbPriceListAssignment= getPriceListAssignment(warehouseId, partnerCode,priceListId);
        if ( dbPriceListAssignment != null) {
            dbPriceListAssignment.setDeletionIndicator(1L);
            dbPriceListAssignment.setUpdatedBy(loginUserID);
            priceListAssignmentRepository.save(dbPriceListAssignment);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + priceListId);
        }
    }
}
