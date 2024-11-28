package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.dto.Strategies;
import com.tekclover.wms.api.transaction.repository.StrategiesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class StrategiesService {

    @Autowired
    private StrategiesRepository strategiesRepository;

    /**
     * @param companyCodeId
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param strategyTypeId
     * @param sequenceIndicator
     * @return
     */
    public Strategies getStrategies(String companyCodeId, String languageId, String plantId, String warehouseId, Long strategyTypeId, Long sequenceIndicator) {
        Optional<Strategies> strategies =
                strategiesRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStrategyTypeIdAndSequenceIndicatorAndDeletionIndicator(
                        languageId,
                        companyCodeId,
                        plantId,
                        warehouseId,
                        strategyTypeId,
                        sequenceIndicator,
                        0L);
        if (strategies.isEmpty()) {
            throw new BadRequestException("The given company : " + companyCodeId +
                    " plantId" + plantId + "warehouseId" + warehouseId + " doesn't exist.");
        }
        return strategies.get();
    }

}