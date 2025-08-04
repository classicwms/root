package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.ASNHeaderV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.ASNLineV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.ASNV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InboundOrderProcessV4;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.SalesOrderHeaderV2;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.SalesOrderLineV2;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v2.SalesOrderV2;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.v3.*;
import com.tekclover.wms.api.transaction.repository.InboundOrderProcessRepository;
import com.tekclover.wms.api.transaction.repository.OutboundOrderProcessRepository;
import com.tekclover.wms.api.transaction.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class OrderPreparationService {

    @Autowired
    InboundOrderProcessRepository inboundOrderProcessRepository;

    @Autowired
    OutboundOrderProcessRepository outboundOrderProcessRepository;

    /**
     *
     * @param inboundOrderProcessList
     */
    public List<InboundOrderProcessV4> saveInboundProcess(List<InboundOrderProcessV4> inboundOrderProcessList) {
        if (inboundOrderProcessList != null && !inboundOrderProcessList.isEmpty()) {
            try {
                List<InboundOrderProcessV4> createInboundOrderProcessList = inboundOrderProcessList.stream().map(inboundOrderProcess -> {
                    InboundOrderProcessV4 inboundLine = new InboundOrderProcessV4();
                    BeanUtils.copyProperties(inboundOrderProcess, inboundLine, CommonUtils.getNullPropertyNames(inboundOrderProcess));
                    return inboundLine;
                }).collect(toList());
                return inboundOrderProcessRepository.saveAll(createInboundOrderProcessList).stream().sorted(Comparator.comparing(InboundOrderProcessV4::getAsnNumber)).collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Exception while InboundProcess Create : " + e);
                throw e;
            }
        }
        return null;
    }

    /**
     *
     * @param outboundOrderProcessList
     */
    public List<OutboundOrderProcessV4> saveOutboundProcess(List<OutboundOrderProcessV4> outboundOrderProcessList) {
        if (outboundOrderProcessList != null && !outboundOrderProcessList.isEmpty()) {
            try {
                List<OutboundOrderProcessV4> createOutboundOrderProcessList = outboundOrderProcessList.stream().map(outboundOrderProcess -> {
                    OutboundOrderProcessV4 outbound = new OutboundOrderProcessV4();
                    BeanUtils.copyProperties(outboundOrderProcess, outbound, CommonUtils.getNullPropertyNames(outboundOrderProcess));
                    return outbound;
                }).collect(toList());
                return outboundOrderProcessRepository.saveAll(createOutboundOrderProcessList).stream().sorted(Comparator.comparing(OutboundOrderProcessV4::getPickListNumber)).collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Exception while OutboundProcess Create : " + e);
                throw e;
            }
        }
        return null;
    }


    //=======================================================INBOUND===============================================================

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param allRowsList
     * @return
     */
    public List<ASNV2> prepAsnMultipleDataV4(String companyCodeId, String plantId,
                                             String languageId, String warehouseId, String loginUserId,
                                             List<InboundOrderProcessV4> allRowsList) {
        List<ASNV2> orderList = new ArrayList<>();
        String orderNumber = null;
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        int i = 1;
        long lineReference = 1;
        ASNHeaderV2 header = null;
        List<ASNLineV2> lisAsnLine = new ArrayList<>();
        for (InboundOrderProcessV4 listUploadedData : allRowsList) {
            if (orderNumber != null) {
                isSameOrder = orderNumber.equalsIgnoreCase(listUploadedData.getAsnNumber());
            }

            if (!isSameOrder) {
                ASNV2 orders = new ASNV2();
                orders.setAsnHeader(header);
                orders.setAsnLine(lisAsnLine);
                orderList.add(orders);

                //reset to create new order
                oneTimeAllow = true;
                isSameOrder = true;
                orderNumber = null;
                lisAsnLine = new ArrayList<>();
                lineReference = 1;
            }

            if (isSameOrder) {
                orderNumber = listUploadedData.getAsnNumber();
                if (oneTimeAllow) {
                    header = new ASNHeaderV2();

                    header.setBranchCode(plantId);
                    header.setCompanyCode(companyCodeId);
                    header.setLanguageId(languageId);
                    header.setWarehouseId(warehouseId);
                    header.setLoginUserId(loginUserId);
                    header.setAsnNumber(listUploadedData.getAsnNumber());
                    header.setInboundOrderTypeId(listUploadedData.getInboundOrderTypeId());
                }
                oneTimeAllow = false;

                // Line
                ASNLineV2 line = new ASNLineV2();
                BeanUtils.copyProperties(listUploadedData, line, CommonUtils.getNullPropertyNames(listUploadedData));

                line.setBranchCode(plantId);
                line.setCompanyCode(companyCodeId);
                line.setLineReference(lineReference);
                line.setNoPairs(listUploadedData.getNoPairs());
                line.setExpectedDate(DateUtils.date2String_YYYYMMDD(new Date()));
                lineReference++;
                lisAsnLine.add(line);
            }
            if (allRowsList.size() == i) {
                ASNV2 orders = new ASNV2();
                orders.setAsnHeader(header);
                orders.setAsnLine(lisAsnLine);
                orderList.add(orders);
            }
            i++;
        }
        return orderList;
    }

    //=======================================================OUTBOUND===============================================================

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param allRowsList
     * @return
     */
    public List<SalesOrderV2> prepSalesOrderDataV4(String companyCodeId, String plantId, String languageId,
                                                   String warehouseId, String loginUserId, List<OutboundOrderProcessV4> allRowsList) {
        List<SalesOrderV2> salesOrderList = new ArrayList<>();
        SalesOrderHeaderV2 soHeader = null;
        List<SalesOrderLineV2> soLines = new ArrayList<>();
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        String orderNumber = null;
        int i = 1;
        for (OutboundOrderProcessV4 listUploadedData : allRowsList) {
            String pickListNumber = listUploadedData.getPickListNumber();
            String salesOrderNo = String.valueOf(System.currentTimeMillis());
            if (orderNumber != null) {
                isSameOrder = orderNumber.equalsIgnoreCase(pickListNumber);
            }
            if (!isSameOrder) {
                SalesOrderV2 salesOrder = new SalesOrderV2();
                salesOrder.setSalesOrderHeader(soHeader);
                salesOrder.setSalesOrderLine(soLines);
                salesOrderList.add(salesOrder);

                //reset to create new order
                oneTimeAllow = true;
                isSameOrder = true;
                orderNumber = null;
                soLines = new ArrayList<>();
            }
            if (isSameOrder) {
                orderNumber = pickListNumber;
                // Header
                if (oneTimeAllow) {
                    soHeader = new SalesOrderHeaderV2();
                    BeanUtils.copyProperties(listUploadedData, soHeader, CommonUtils.getNullPropertyNames(listUploadedData));
                    soHeader.setCompanyCode(companyCodeId);
                    soHeader.setBranchCode(plantId);
                    soHeader.setStoreID(plantId);
                    soHeader.setLanguageId(languageId);
                    soHeader.setWarehouseId(warehouseId);
                    soHeader.setLoginUserId(loginUserId);
                    soHeader.setPickListNumber(pickListNumber);
                    soHeader.setCustomerId(listUploadedData.getCustomerId());
                    soHeader.setCustomerName(listUploadedData.getCustomerName());
                    soHeader.setRequiredDeliveryDate(listUploadedData.getRequiredDeliveryDate());
                    soHeader.setOrderType(listUploadedData.getOrderType());
                    soHeader.setTokenNumber(listUploadedData.getTokenNumber());
                    soHeader.setSalesOrderNumber(salesOrderNo);
                    soHeader.setRequiredDeliveryDate(DateUtils.date2String_YYYYMMDD(new Date()));
                }
                oneTimeAllow = false;

                // Line
                SalesOrderLineV2 soLine = new SalesOrderLineV2();
                BeanUtils.copyProperties(listUploadedData, soLine, CommonUtils.getNullPropertyNames(listUploadedData));
                soLine.setPickListNo(pickListNumber);
                soLine.setSalesOrderNo(salesOrderNo);
                soLine.setLineReference(listUploadedData.getItm());
                soLine.setMtoNumber(listUploadedData.getMtoNumber());
                soLine.setSpecialStock(listUploadedData.getSpecialStock());
                soLine.setShipToCode(listUploadedData.getShipToCode());
                soLine.setShipToParty(listUploadedData.getShipToParty());

                soLines.add(soLine);
            }

            if (allRowsList.size() == i) {
                SalesOrderV2 salesOrder = new SalesOrderV2();
                salesOrder.setSalesOrderHeader(soHeader);
                salesOrder.setSalesOrderLine(soLines);
                salesOrderList.add(salesOrder);
            }
            i++;
        }
        return salesOrderList;
    }

    /**
     * --------------------------------------------------------------------------------------------------------------------------------
     *
     * @param allRowsList
     * @return
     */
    public DeliveryConfirmationV3 prepDeliveryConfirmationV3(String companyCodeId, String plantId, String languageId,
                                                             String warehouseId, String loginUserId, List<List<String>> allRowsList) {
        DeliveryConfirmationV3 deliveryConfirmationV3 = new DeliveryConfirmationV3();
        List<DeliveryConfirmationLineV3> deliveryLines = new ArrayList<>();
        for (List<String> listUploadedData : allRowsList) {
            DeliveryConfirmationLineV3 line = new DeliveryConfirmationLineV3();
            line.setOutbound(listUploadedData.get(0));
            line.setHuSerialNo(listUploadedData.get(1));
            line.setMaterial(listUploadedData.get(2));
            line.setPriceSegement(listUploadedData.get(3));
            line.setPlant(listUploadedData.get(4));
            line.setStorageLocation(listUploadedData.get(5));
            line.setSkuCode(listUploadedData.get(6));
            line.setPickedQty(1D);
            deliveryLines.add(line);
        }
        deliveryConfirmationV3.setCompanyCodeId(companyCodeId);
        deliveryConfirmationV3.setPlantId(plantId);
        deliveryConfirmationV3.setLanguageId(languageId);
        deliveryConfirmationV3.setWarehouseId(warehouseId);
        deliveryConfirmationV3.setLoginUserId(loginUserId);
        deliveryConfirmationV3.setLines(deliveryLines);
        return deliveryConfirmationV3;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param allRowsList
     * @return
     */
    public ReversalV3 prepareReversalV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                        String loginUserId, List<List<String>> allRowsList) {
        ReversalV3 reversal = new ReversalV3();
        List<ReversalLineV3> lines = new ArrayList<>();
        for (List<String> listUploadedData : allRowsList) {
            ReversalLineV3 line = new ReversalLineV3();
            line.setOrderNumber(listUploadedData.get(0));
            line.setHuSerialNo(listUploadedData.get(1));
            line.setMaterial(listUploadedData.get(2));
            line.setPriceSegement(listUploadedData.get(3));
            line.setPlant(listUploadedData.get(4));
            line.setStorageLocation(listUploadedData.get(5));
            line.setSkuCode(listUploadedData.get(6));
            lines.add(line);
        }
        reversal.setCompanyCodeId(companyCodeId);
        reversal.setPlantId(plantId);
        reversal.setLanguageId(languageId);
        reversal.setWarehouseId(warehouseId);
        reversal.setLoginUserId(loginUserId);
        reversal.setLines(lines);
        return reversal;
    }
}