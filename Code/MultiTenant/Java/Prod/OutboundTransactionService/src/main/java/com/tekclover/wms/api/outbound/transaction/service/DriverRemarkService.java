package com.tekclover.wms.api.outbound.transaction.service;


import com.tekclover.wms.api.outbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.outbound.transaction.model.driverremark.DriverRemark;
import com.tekclover.wms.api.outbound.transaction.model.driverremark.SearchDriverRemark;
import com.tekclover.wms.api.outbound.transaction.repository.DriverRemarkRepository;
import com.tekclover.wms.api.outbound.transaction.repository.specification.DriverRemarkSpecification;
import com.tekclover.wms.api.outbound.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class DriverRemarkService extends BaseService {

    @Autowired
    DriverRemarkRepository driverRemarkRepository;


    /**
     * getDriverRemark
     * @return
     */
    public List<DriverRemark> getDriverRemarks () {
        return driverRemarkRepository.findAll();
    }

    /**
     *
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public DriverRemark getDriverRemark (String preOutboundNo, String refDocNumber) {
        DriverRemark company = driverRemarkRepository.findByPreOutboundNoAndRefDocNumber(preOutboundNo, refDocNumber).orElse(null);
        if (company == null) {
            throw new BadRequestException("The given ID doesn't exist : " + preOutboundNo);
        }
        return company;
    }

    /**
     *
     * @param newDriverRemark
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public DriverRemark createDriverRemark(DriverRemark newDriverRemark, String loginUserID)
            throws Exception {
        DriverRemark dbDriverRemark = new DriverRemark();

        NUMBER_RANGE_CODE = 225L;
        String driverRemarkNumber = getNextRangeNumber(NUMBER_RANGE_CODE, newDriverRemark.getCompanyCodeId(), newDriverRemark.getPlantId(),
                newDriverRemark.getLanguageId(), newDriverRemark.getWarehouseId());

        BeanUtils.copyProperties(newDriverRemark, dbDriverRemark, CommonUtils.getNullPropertyNames(newDriverRemark));
        dbDriverRemark.setDriverRemarkNo(driverRemarkNumber);
        dbDriverRemark.setCreatedBy(loginUserID);
        dbDriverRemark.setCreatedOn(new Date());

        driverRemarkRepository.updateDriverRemarks(dbDriverRemark.getPreOutboundNo(), dbDriverRemark.getRefDocNumber(),
                dbDriverRemark.getDriverName(), dbDriverRemark.getRemarks(), dbDriverRemark.getVehicleNO(),dbDriverRemark.getReferenceField5());

        driverRemarkRepository.updateDriverRemarkModify(dbDriverRemark.getPreOutboundNo(), dbDriverRemark.getRefDocNumber(),
                dbDriverRemark.getDriverName(), dbDriverRemark.getRemarks(), dbDriverRemark.getVehicleNO(),dbDriverRemark.getReferenceField5());

        return driverRemarkRepository.save(dbDriverRemark);
    }

    /**
     *
     * @param updateDriverRemark
     * @param preOutboundNo
     * @param refDocNumber
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public DriverRemark updateDriverRemark(DriverRemark updateDriverRemark, String preOutboundNo, String refDocNumber, String loginUserID)
            throws Exception {
        DriverRemark dbDriverRemark = getDriverRemark(preOutboundNo, refDocNumber);
        BeanUtils.copyProperties(updateDriverRemark, dbDriverRemark, CommonUtils.getNullPropertyNames(updateDriverRemark));
        dbDriverRemark.setUpdatedBy(loginUserID);
        dbDriverRemark.setUpdatedOn(new Date());
        return driverRemarkRepository.save(dbDriverRemark);
    }

    /**
     *
     * @param deleteDriverRemark
     * @param preOutboundNo
     * @param refDocNumber
     */
    public void deleteDriverRemark (DriverRemark deleteDriverRemark , String preOutboundNo, String refDocNumber ) {
        DriverRemark dbDriverRemark = getDriverRemark(preOutboundNo, refDocNumber);
        if ( dbDriverRemark != null) {
           driverRemarkRepository.delete(dbDriverRemark);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + dbDriverRemark);
        }
    }


    /**
     *
     * @param searchDriverRemark
     * @return
     * @throws ParseException
     */
    public List<DriverRemark> findDriverRemark(SearchDriverRemark searchDriverRemark) throws ParseException {

        DriverRemarkSpecification spec = new DriverRemarkSpecification(searchDriverRemark);
        log.info("Input value " + searchDriverRemark);
        List<DriverRemark> results = driverRemarkRepository.findAll(spec);
        log.info("results: " + results);
        return results;
    }

}
