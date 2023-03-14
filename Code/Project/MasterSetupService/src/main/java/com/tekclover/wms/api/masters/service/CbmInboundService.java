package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.threepl.cbminbound.*;
import com.tekclover.wms.api.masters.repository.CbmInboundRepository;
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
public class CbmInboundService extends BaseService {

    @Autowired
    private CbmInboundRepository cbmInboundRepository;

    /**
     * getCbmInbounds
     * @return
     */
    public List<CbmInbound> getCbmInbounds () {
        List<CbmInbound>CbmInboundList =  cbmInboundRepository.findAll();
        CbmInboundList = CbmInboundList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return CbmInboundList;
    }

    /**
     * getCbmInbound
     * @param itemCode
     * @return
     */
    public CbmInbound getCbmInbound (String warehouseId, String itemCode) {
        Optional<CbmInbound> dbCbmInbound =
                cbmInboundRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndLanguageIdAndDeletionIndicator(
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        itemCode,
                        getLanguageId(),
                        0L
                );
        if (dbCbmInbound.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "itemCode - " + itemCode +
                    " doesn't exist.");
        }
        return dbCbmInbound.get();
    }

    /**
     * createCbmInbound
     * @param newCbmInbound
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public CbmInbound createCbmInbound (AddCbmInbound newCbmInbound, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        CbmInbound dbCbmInbound = new CbmInbound();
        log.info("newCbmInbound : " + newCbmInbound);
        BeanUtils.copyProperties(newCbmInbound, dbCbmInbound, CommonUtils.getNullPropertyNames(newCbmInbound));
        dbCbmInbound.setCompanyCodeId(getCompanyCode());
        dbCbmInbound.setPlantId(getPlantId());
        dbCbmInbound.setDeletionIndicator(0L);
        dbCbmInbound.setCreatedBy(loginUserID);
        dbCbmInbound.setUpdatedBy(loginUserID);
        dbCbmInbound.setCreatedOn(new Date());
        dbCbmInbound.setUpdatedOn(new Date());
        return cbmInboundRepository.save(dbCbmInbound);
    }

    /**
     * updateCbmInbound
     * @param loginUserID
     * @param itemCode
     * @param updateCbmInbound
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public CbmInbound updateCbmInbound(String warehouseId, String itemCode, String loginUserID,
                                 UpdateCbmInbound updateCbmInbound)
            throws IllegalAccessException, InvocationTargetException {
        CbmInbound dbCbmInbound = getCbmInbound(warehouseId,itemCode);
        BeanUtils.copyProperties(updateCbmInbound, dbCbmInbound, CommonUtils.getNullPropertyNames(updateCbmInbound));
        dbCbmInbound.setUpdatedBy(loginUserID);
        dbCbmInbound.setUpdatedOn(new Date());
        return cbmInboundRepository.save(dbCbmInbound);
    }

    /**
     * deleteCbmInbound
     * @param loginUserID
     * @param itemCode
     */
    public void deleteCbmInbound(String warehouseId,String itemCode, String loginUserID) {
        CbmInbound dbCbmInbound = getCbmInbound(warehouseId, itemCode);
        if ( dbCbmInbound != null) {
            dbCbmInbound.setDeletionIndicator(1L);
            dbCbmInbound.setUpdatedBy(loginUserID);
            cbmInboundRepository.save(dbCbmInbound);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + itemCode);
        }
    }
}
