package com.tekclover.wms.api.idmaster.service;


import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.outboundordertypeid.AddOutboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.outboundordertypeid.OutboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.outboundordertypeid.UpdateOutboundOrderTypeId;
import com.tekclover.wms.api.idmaster.repository.OutboundOrderTypeIdRepository;
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
public class OutboundOrderTypeIdService extends BaseService {


    @Autowired
    private OutboundOrderTypeIdRepository outboundOrderTypeIdRepository;

    /**
     * getOutboundOrderTypeIds
     * @return
     */
    public List<OutboundOrderTypeId> getOutboundOrderTypeIds () {
        List<OutboundOrderTypeId> OutboundOrderTypeIdList = outboundOrderTypeIdRepository.findAll();

        OutboundOrderTypeIdList = OutboundOrderTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return OutboundOrderTypeIdList;
    }

    /**
     * getOutboundOrderTypeId
     * @param outboundOrderTypeId
     * @return
     */
    public OutboundOrderTypeId getOutboundOrderTypeId(String warehouseId,String outboundOrderTypeId) {
        Optional<OutboundOrderTypeId> dbOutboundOrderTypeId = outboundOrderTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndOutboundOrderTypeIdAndLanguageIdAndDeletionIndicator(
                getCompanyCode(),
                getPlantId(),
                warehouseId,
                outboundOrderTypeId,
                getLanguageId(),
                0L
        );
        if (dbOutboundOrderTypeId.isEmpty()) {
            throw new BadRequestException("The given values : " +
                    "warehouseId - " + warehouseId +
                    "dbOutboundOrderTypeId - " + outboundOrderTypeId +
                    " doesn't exist.");
        }
        return dbOutboundOrderTypeId.get();
    }
    /**
     * CreateOutboundOrderTypeId
     * @param newOutboundOrderTypeId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */

    public OutboundOrderTypeId CreateOutboundOrderTypeId(AddOutboundOrderTypeId newOutboundOrderTypeId, String loginUserID){
        OutboundOrderTypeId dbOutboundOrderTypeIdId = new OutboundOrderTypeId();
        log.info("newOutboundOrderTypeId : " + newOutboundOrderTypeId);
        BeanUtils.copyProperties(newOutboundOrderTypeId, dbOutboundOrderTypeIdId, CommonUtils.getNullPropertyNames(newOutboundOrderTypeId));
        dbOutboundOrderTypeIdId.setCompanyCodeId(getCompanyCode());
        dbOutboundOrderTypeIdId.setPlantId(getPlantId());
        dbOutboundOrderTypeIdId.setDeletionIndicator(0L);
        dbOutboundOrderTypeIdId.setCreatedBy(loginUserID);
        dbOutboundOrderTypeIdId.setUpdatedBy(loginUserID);
        dbOutboundOrderTypeIdId.setCreatedOn(new Date());
        dbOutboundOrderTypeIdId.setUpdatedOn(new Date());
        return outboundOrderTypeIdRepository.save(dbOutboundOrderTypeIdId);
    }

    /**
     * updateOutboundOrderTypeId
     * @param loginUserID
     * @param outboundOrderTypeId
     * @param updateOutboundOrderTypeId
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OutboundOrderTypeId updateOutboundOrderTypeId (String warehouseId, String outboundOrderTypeId, String loginUserID,
                                                              UpdateOutboundOrderTypeId updateOutboundOrderTypeId)
            throws IllegalAccessException, InvocationTargetException {
        OutboundOrderTypeId dbOutboundOrderTypeId = getOutboundOrderTypeId( warehouseId, outboundOrderTypeId);
        BeanUtils.copyProperties(updateOutboundOrderTypeId, dbOutboundOrderTypeId, CommonUtils.getNullPropertyNames(updateOutboundOrderTypeId));
        dbOutboundOrderTypeId.setUpdatedBy(loginUserID);
        dbOutboundOrderTypeId.setUpdatedOn(new Date());
        return outboundOrderTypeIdRepository.save(dbOutboundOrderTypeId);
    }
    /**
     * deleteOutboundOrderTypeId
     * @param loginUserID
     * @param outboundOrderTypeId
     */
    public void deleteOutboundOrderTypeId(String warehouseId,String outboundOrderTypeId,String loginUserID){
        OutboundOrderTypeId dbOutboundOrderTypeId=getOutboundOrderTypeId(warehouseId,outboundOrderTypeId);
            if(dbOutboundOrderTypeId !=null) {
                dbOutboundOrderTypeId.setDeletionIndicator(1l);
                dbOutboundOrderTypeId.setUpdatedBy(loginUserID);
                outboundOrderTypeIdRepository.save(dbOutboundOrderTypeId);
            }
            else{
                throw new EntityNotFoundException("Error in deleting Id: " + outboundOrderTypeId);
            }

    }

}




