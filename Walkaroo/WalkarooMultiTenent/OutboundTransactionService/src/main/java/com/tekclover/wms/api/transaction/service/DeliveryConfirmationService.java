package com.tekclover.wms.api.transaction.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.model.deliveryconfirmation.DeliveryConfirmation;
import com.tekclover.wms.api.transaction.model.deliveryconfirmation.SearchDeliveryConfirmation;
import com.tekclover.wms.api.transaction.repository.DeliveryConfirmationRepository;
import com.tekclover.wms.api.transaction.repository.specification.DeliveryConfirmationSpecification;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DeliveryConfirmationService {

    @Autowired
    DeliveryConfirmationRepository deliveryConfirmationRepository;

    /**
     *
     * @param searchDeliveryConfirmation
     * @return
     * @throws Exception
     */
    public List<DeliveryConfirmation> findDeliveryConfirmation(SearchDeliveryConfirmation searchDeliveryConfirmation) throws Exception {
        if (searchDeliveryConfirmation.getFromDate() != null && searchDeliveryConfirmation.getToDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchDeliveryConfirmation.getFromDate(), searchDeliveryConfirmation.getToDate());
            searchDeliveryConfirmation.setFromDate(dates[0]);
            searchDeliveryConfirmation.setToDate(dates[1]);
        }
        log.info("Find Delivery Confirmation Input: " + searchDeliveryConfirmation);
        DeliveryConfirmationSpecification spec = new DeliveryConfirmationSpecification(searchDeliveryConfirmation);
        return deliveryConfirmationRepository.findAll(spec);
    }

    /**
     *
     * @param deliveryIds
     * @param processStatusId
     * @param remark
     * @param processedDate
     */
    public void updateRemarks (List<Long> deliveryIds, Long processStatusId, String remark, Date processedDate) {
        deliveryConfirmationRepository.updateFailedProcessStatusId(deliveryIds, processStatusId, remark, processedDate);
    }
}