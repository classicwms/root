package com.tekclover.wms.api.inbound.orders.service;

import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.numberrange.NumberRange;
import com.tekclover.wms.api.inbound.orders.repository.NumberRangeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class NumberRangeService {

	@Autowired
    NumberRangeRepository numberRangeRepository;

    public String getNextNumberRange(Long numberRangeCode, String warehouseId, String companyCodeId,
                                     String plantId, String languageId) {

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("FAHAHEEL");
        log.info(" | " + companyCodeId + " | " + plantId + " | " + warehouseId + " | ");
        Optional<NumberRange> optNumberRange = numberRangeRepository.getNextNumberRange(companyCodeId, plantId, warehouseId, numberRangeCode, languageId);
        log.info("getNextNumberRange---1----> " + numberRangeCode + "," + warehouseId);
        log.info("getNextNumberRange---2----> " + optNumberRange);

        if (optNumberRange.isEmpty()) {
            log.info(" | " + companyCodeId + " | " + plantId + " | " + warehouseId + " | ");
            optNumberRange = numberRangeRepository.getNextNumberRange(companyCodeId, plantId, warehouseId, numberRangeCode, languageId);
            if (optNumberRange.isEmpty()) {
                // Exception Log
//                createNumberRangeLog(numberRangeCode, languageId, companyCodeId, plantId, warehouseId,
//                        "The given NumberRangeCode-" + numberRangeCode + " and warehouseId-" + warehouseId + " doesn't exists.");
                throw new BadRequestException("The given numberRangeCode : " + numberRangeCode + ", warehouseId: "
                        + warehouseId + " doesn't exist.");
            }
        }

        NumberRange numberRange = optNumberRange.get();
        String strCurrentValue = numberRange.getNumberRangeCurrent();
        log.info("New currentValue generated : " + strCurrentValue);
        Long currentValue = 0L;
        if (strCurrentValue != null && strCurrentValue.trim().startsWith("A")) { // Increment logic for AuditLog Insert
            strCurrentValue = strCurrentValue.substring(2); // AL1000002
            currentValue = Long.valueOf(strCurrentValue);
            currentValue++;
            strCurrentValue = "A" + String.valueOf(currentValue);
            numberRange.setNumberRangeCurrent(strCurrentValue);
            log.info("currentValue of A: " + currentValue);
        } else {
            strCurrentValue = strCurrentValue.trim();
            currentValue = Long.valueOf(strCurrentValue);
            currentValue++;
            log.info("currentValue : " + currentValue);
            numberRange.setNumberRangeCurrent(String.valueOf(currentValue));
            strCurrentValue = String.valueOf(currentValue);
        }

        log.info("New value numberRange: " + numberRange);
        numberRangeRepository.save(numberRange);

        log.info("New value has been updated in NumberRangeCurrent column");
        return strCurrentValue;
    }

}
