package com.tekclover.wms.api.inbound.orders.service;

import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.unallocatedorder.SearchUnallocatedOrderLineV2;
import com.tekclover.wms.api.inbound.orders.model.unallocatedorder.UnallocatedOrderLineV2;
import com.tekclover.wms.api.inbound.orders.repository.UnallocatedOrderLineRepository;
import com.tekclover.wms.api.inbound.orders.repository.specification.UnallocatedOrderLineV2Specification;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import com.tekclover.wms.api.inbound.orders.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UnallocatedOrderLineService {

    @Autowired
    UnallocatedOrderLineRepository unallocatedOrderLineRepository;

    //-------------------------------------CreateUnallocatedOrder Table ---------------------------------//

    public UnallocatedOrderLineV2 getUnallocatedOrderLineV2(String warehouseId, String preOutboundNo, String refDocNumber,
                                                            String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackCode) {
        UnallocatedOrderLineV2 orderManagementHeader = unallocatedOrderLineRepository
                .findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, proposedStorageBin,
                        proposedPackCode, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId
                + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                + ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + ",proposedStorageBin:" + proposedStorageBin
                + ",proposedPackCode:" + proposedPackCode + " doesn't exist.");
    }

    /**
     * Create Unallocated Order Table
     * @param orderManagementLineV2
     */
    public void createUnallocatedOrderLine(OrderManagementLineV2 orderManagementLineV2) {
        try {
            log.info("Unallocated Order Line Created Started !!");
            UnallocatedOrderLineV2 unallocatedOrderLineV2 = new UnallocatedOrderLineV2();

            BeanUtils.copyProperties(orderManagementLineV2, unallocatedOrderLineV2, CommonUtils.getNullPropertyNames(orderManagementLineV2));

            unallocatedOrderLineRepository.save(unallocatedOrderLineV2);
            log.info("Unallocated Order Line Created Completed !!");
        } catch (Exception e) {
            throw new BadRequestException("There is a Error occured while Creating Unallocated Order Line");
        }
    }

    /**
     *
     * @param updatedUnallocatedOrderLineV2
     * @return
     */
    public UnallocatedOrderLineV2 updateUnallocatedOrderLine(UnallocatedOrderLineV2 updatedUnallocatedOrderLineV2) {
        try {
            log.info("Unallocated Order Line Update Started !!");
            UnallocatedOrderLineV2 dbUnallocatedOrderLineV2 = getUnallocatedOrderLineV2(updatedUnallocatedOrderLineV2.getWarehouseId(),
                    updatedUnallocatedOrderLineV2.getPreOutboundNo(), updatedUnallocatedOrderLineV2.getRefDocNumber(), updatedUnallocatedOrderLineV2.getPartnerCode(),
                    updatedUnallocatedOrderLineV2.getLineNumber(), updatedUnallocatedOrderLineV2.getItemCode(), updatedUnallocatedOrderLineV2.getProposedStorageBin(),
                    updatedUnallocatedOrderLineV2.getProposedPackBarCode());

            if (dbUnallocatedOrderLineV2 == null) {
                throw new BadRequestException("There is no UnallocatedOrder Found for given request");
            }

            BeanUtils.copyProperties(updatedUnallocatedOrderLineV2, dbUnallocatedOrderLineV2, CommonUtils.getNullPropertyNames(updatedUnallocatedOrderLineV2));
            unallocatedOrderLineRepository.save(dbUnallocatedOrderLineV2);
            log.info("Unallocated Order Line Update Completed !!");
            return dbUnallocatedOrderLineV2;
        } catch (Exception e) {
            throw new BadRequestException("There is a Error occured while Updating Unallocated Order Line");
        }
    }

    /**
     *
     * @param searchUnallocatedOrderLineV2
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public List<UnallocatedOrderLineV2> findUnallocatedOrderLineV2(SearchUnallocatedOrderLineV2 searchUnallocatedOrderLineV2)
            throws ParseException, java.text.ParseException {

        if (searchUnallocatedOrderLineV2.getStartRequiredDeliveryDate() != null
                && searchUnallocatedOrderLineV2.getEndRequiredDeliveryDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchUnallocatedOrderLineV2.getStartRequiredDeliveryDate(),
                    searchUnallocatedOrderLineV2.getEndRequiredDeliveryDate());
            searchUnallocatedOrderLineV2.setStartRequiredDeliveryDate(dates[0]);
            searchUnallocatedOrderLineV2.setEndRequiredDeliveryDate(dates[1]);
        }

        if (searchUnallocatedOrderLineV2.getStartOrderDate() != null
                && searchUnallocatedOrderLineV2.getEndOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchUnallocatedOrderLineV2.getStartOrderDate(),
                    searchUnallocatedOrderLineV2.getEndOrderDate());
            searchUnallocatedOrderLineV2.setStartOrderDate(dates[0]);
            searchUnallocatedOrderLineV2.setEndOrderDate(dates[1]);
        }
        UnallocatedOrderLineV2Specification spec = new UnallocatedOrderLineV2Specification(searchUnallocatedOrderLineV2);
        List<UnallocatedOrderLineV2> searchResults = unallocatedOrderLineRepository.findAll(spec);
        return searchResults;
    }
}
