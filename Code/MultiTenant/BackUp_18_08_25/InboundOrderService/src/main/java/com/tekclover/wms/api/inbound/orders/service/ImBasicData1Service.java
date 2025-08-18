package com.tekclover.wms.api.inbound.orders.service;


import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.inbound.orders.repository.ImBasicData1V2Repository;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class ImBasicData1Service {


    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;

    /**
     *
     * @param newImBasicData1
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    public ImBasicData1V2 createImBasicData1V2(ImBasicData1V2 newImBasicData1, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        ImBasicData1V2 dbImBasicData1 = new ImBasicData1V2();
        Optional<ImBasicData1V2> duplicateImBasicData1 = imBasicData1V2Repository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndManufacturerPartNoAndLanguageIdAndDeletionIndicator(newImBasicData1.getCompanyCodeId(),
                newImBasicData1.getPlantId(), newImBasicData1.getWarehouseId(), newImBasicData1.getItemCode(),
                newImBasicData1.getUomId(), newImBasicData1.getManufacturerPartNo(), newImBasicData1.getLanguageId(), 0L);

        if (!duplicateImBasicData1.isEmpty()) {
            // Exception Log
//            createImBasicData1Log2(newImBasicData1, "Record is getting Duplicated.");
            throw new EntityNotFoundException("Record is Getting Duplicated");
        } else {
            BeanUtils.copyProperties(newImBasicData1, dbImBasicData1, CommonUtils.getNullPropertyNames(newImBasicData1));
            if(newImBasicData1.getCapacityCheck() != null) {
                dbImBasicData1.setCapacityCheck(newImBasicData1.getCapacityCheck());
            }
            if(newImBasicData1.getCapacityCheck() == null) {
                dbImBasicData1.setCapacityCheck(false);
            }
            if(newImBasicData1.getItemType() == null) {
                dbImBasicData1.setItemType(1L);
            }
            if(newImBasicData1.getManufacturerName() == null) {
                dbImBasicData1.setManufacturerName(newImBasicData1.getManufacturerPartNo());
            }
            if(newImBasicData1.getManufacturerCode() == null) {
                dbImBasicData1.setManufacturerCode(newImBasicData1.getManufacturerPartNo());
            }
            if(newImBasicData1.getManufacturerFullName() == null) {
                dbImBasicData1.setManufacturerFullName(newImBasicData1.getManufacturerPartNo());
            }

            log.info("Id: " + newImBasicData1.getCompanyCodeId() + ", " + newImBasicData1.getPlantId() + ", " + newImBasicData1.getWarehouseId() + ", " + newImBasicData1.getLanguageId());
            IKeyValuePair description = imBasicData1V2Repository.getDescription(newImBasicData1.getCompanyCodeId(),
                    newImBasicData1.getLanguageId(),
                    newImBasicData1.getPlantId(),
                    newImBasicData1.getWarehouseId());
            log.info("Description: " + description);
            if(description != null) {
                log.info("Description: " + description.getCompanyDesc() + ", " + description.getPlantDesc() + ", " + description.getWarehouseDesc());
                dbImBasicData1.setCompanyDescription(description.getCompanyDesc());
                dbImBasicData1.setPlantDescription(description.getPlantDesc());
                dbImBasicData1.setWarehouseDescription(description.getWarehouseDesc());
            }

            dbImBasicData1.setDeletionIndicator(0L);
            dbImBasicData1.setCreatedBy(loginUserID);
            dbImBasicData1.setUpdatedBy(loginUserID);
            dbImBasicData1.setCreatedOn(new Date());
            dbImBasicData1.setUpdatedOn(new Date());
            return imBasicData1V2Repository.save(dbImBasicData1);
        }
    }


}
