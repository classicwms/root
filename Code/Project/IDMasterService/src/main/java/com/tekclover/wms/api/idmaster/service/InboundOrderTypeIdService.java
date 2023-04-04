package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.inboundordertypeid.AddInboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.inboundordertypeid.InboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.inboundordertypeid.UpdateInboundOrderTypeId;
import com.tekclover.wms.api.idmaster.repository.InboundOrderTypeIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
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
public class InboundOrderTypeIdService extends BaseService {
    @Autowired
    private InboundOrderTypeIdRepository inboundOrderTypeIdRepository;
    /**
     * getInboundOrderTypeIds
     * @return
     */
    public List<InboundOrderTypeId> getInboundOrderTypeIds () {
        List<InboundOrderTypeId> inboundOrderTypeIdList =  inboundOrderTypeIdRepository.findAll();
        inboundOrderTypeIdList = inboundOrderTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return inboundOrderTypeIdList;
    }
    /**
     * getInboundOrderTypeId
     * @param inboundOrderTypeId
     * @return
     */
    public InboundOrderTypeId getInboundOrderTypeId(String warehouseId, String inboundOrderTypeId) {
        Optional<InboundOrderTypeId> dbInboundOrderTypeId =
                inboundOrderTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInboundOrderTypeIdAndLanguageIdAndDeletionIndicator(
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        inboundOrderTypeId,
                        getLanguageId(),
                        0L
                );
        if (dbInboundOrderTypeId.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "inboundOrderTypeId - " + inboundOrderTypeId +
                    " doesn't exist.");

        }
        return dbInboundOrderTypeId.get();
    }
    /**
     * createInboundOrderStatusId
     * @param newInboundOrderTypeId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InboundOrderTypeId createInboundOrderTypeId (AddInboundOrderTypeId newInboundOrderTypeId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        InboundOrderTypeId dbnewInboundOrderTypeId = new InboundOrderTypeId();
        log.info("newInboundOrderStatusId : " + newInboundOrderTypeId);
        BeanUtils.copyProperties(newInboundOrderTypeId, dbnewInboundOrderTypeId, CommonUtils.getNullPropertyNames(newInboundOrderTypeId));
        dbnewInboundOrderTypeId.setCompanyCodeId(getCompanyCode());
        dbnewInboundOrderTypeId.setPlantId(getPlantId());
        dbnewInboundOrderTypeId.setDeletionIndicator(0L);
        dbnewInboundOrderTypeId.setCreatedBy(loginUserID);
        dbnewInboundOrderTypeId.setUpdatedBy(loginUserID);
        dbnewInboundOrderTypeId.setCreatedOn(new Date());
        dbnewInboundOrderTypeId.setUpdatedOn(new Date());
        return inboundOrderTypeIdRepository.save(dbnewInboundOrderTypeId);
    }
    /**
     * updateInboundOrderStatusId
     * @param loginUserID
     * @param inboundOrderTypeId
     * @param updateInboundOrderTypeId
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InboundOrderTypeId updateInboundOrderTypeId (String warehouseId, String inboundOrderTypeId, String loginUserID,
                                                            UpdateInboundOrderTypeId updateInboundOrderTypeId)
            throws IllegalAccessException, InvocationTargetException {
        InboundOrderTypeId dbInboundOrderTypeId = getInboundOrderTypeId( warehouseId, inboundOrderTypeId);
        BeanUtils.copyProperties(updateInboundOrderTypeId, dbInboundOrderTypeId, CommonUtils.getNullPropertyNames(updateInboundOrderTypeId));
        dbInboundOrderTypeId.setUpdatedBy(loginUserID);
        dbInboundOrderTypeId.setUpdatedOn(new Date());
        return inboundOrderTypeIdRepository.save(dbInboundOrderTypeId);
    }

    /**
     * deleteInboundOrderStatusId
     * @param loginUserID
     * @param inboundOrderTypeId
     */
    public void deleteInboundOrderTypeId (String warehouseId, String inboundOrderTypeId, String loginUserID) {
        InboundOrderTypeId dbinboundOrderTypeId=getInboundOrderTypeId( warehouseId, inboundOrderTypeId);
        if ( dbinboundOrderTypeId != null) {
            dbinboundOrderTypeId.setDeletionIndicator(1L);
            dbinboundOrderTypeId.setUpdatedBy(loginUserID);
            inboundOrderTypeIdRepository.save(dbinboundOrderTypeId);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + inboundOrderTypeId);
        }
    }






}
