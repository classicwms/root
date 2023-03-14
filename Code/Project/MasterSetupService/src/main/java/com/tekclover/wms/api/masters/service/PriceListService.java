package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.threepl.pricelist.*;
import com.tekclover.wms.api.masters.repository.PriceListRepository;
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
public class PriceListService extends BaseService{

    @Autowired
    private PriceListRepository priceListRepository;

    /**
     * getPriceLists
     * @return
     */
    public List<PriceList> getPriceLists () {
        List<PriceList> priceLists =  priceListRepository.findAll();
        priceLists = priceLists.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return priceLists;
    }

    /**
     * getPriceList
     * @param module
     * @param priceListId
     * @param serviceTypeId
     * @return
     */
    public PriceList getPriceList (String warehouseId, Long module, Long priceListId, Long serviceTypeId) {
        Optional<PriceList> dbPriceList =
                priceListRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleAndPriceListIdAndServiceTypeIdAndLanguageIdAndDeletionIndicator(
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        module,
                        priceListId,
                        serviceTypeId,
                        getLanguageId(),
                        0L
                );
        if (dbPriceList.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "module - " + module +
                    "priceListId - " + priceListId +
                    "serviceTypeId-"+serviceTypeId+
                    " doesn't exist.");
        }
        return dbPriceList.get();
    }

    /**
     * createPriceListId
     * @param newPriceListId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PriceList createPriceList (AddPriceList newPriceListId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PriceList dbPriceList = new PriceList();
        log.info("newPriceListId : " + newPriceListId);
        BeanUtils.copyProperties(newPriceListId, dbPriceList, CommonUtils.getNullPropertyNames(newPriceListId));
        dbPriceList.setCompanyCodeId(getCompanyCode());
        dbPriceList.setPlantId(getPlantId());
        dbPriceList.setDeletionIndicator(0L);
        dbPriceList.setCreatedBy(loginUserID);
        dbPriceList.setUpdatedBy(loginUserID);
        dbPriceList.setCreatedOn(new Date());
        dbPriceList.setUpdatedOn(new Date());
        return priceListRepository.save(dbPriceList);
    }

    /**
     * updatePriceListId
     * @param loginUserID
     * @param module
     * @param priceListId
     * @param serviceTypeId
     * @param updatePriceList
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PriceList updatePriceList (String warehouseId, Long module, Long priceListId, Long serviceTypeId, String loginUserID,
                                        UpdatePriceList updatePriceList)
            throws IllegalAccessException, InvocationTargetException {
        PriceList dbPriceList = getPriceList(warehouseId, module, priceListId,serviceTypeId);
        BeanUtils.copyProperties(updatePriceList, dbPriceList, CommonUtils.getNullPropertyNames(updatePriceList));
        dbPriceList.setUpdatedBy(loginUserID);
        dbPriceList.setUpdatedOn(new Date());
        return priceListRepository.save(dbPriceList);
    }

    /**
     * deletePriceListId
     * @param loginUserID
     * @param module
     * @param priceListId
     * @param serviceTypeId
     */
    public void deletePriceList (String warehouseId,Long module, Long priceListId,Long serviceTypeId, String loginUserID) {
        PriceList dbPriceList = getPriceList(warehouseId, module, priceListId,serviceTypeId);
        if ( dbPriceList != null) {
            dbPriceList.setDeletionIndicator(1L);
            dbPriceList.setUpdatedBy(loginUserID);
            priceListRepository.save(dbPriceList);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + priceListId);
        }
    }
}
