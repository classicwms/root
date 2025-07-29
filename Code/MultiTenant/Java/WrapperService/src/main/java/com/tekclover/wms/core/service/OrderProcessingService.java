package com.tekclover.wms.core.service;


import com.tekclover.wms.core.model.warehouse.inbound.almailem.*;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.*;
import com.tekclover.wms.core.util.CommonUtils;
import com.tekclover.wms.core.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderProcessingService {

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param allRowsList
     * @return
     */
    public List<SalesOrderV2> prepSalesOrderMultipleData(String companyCodeId, String plantId, String languageId,
                                                         String warehouseId, String loginUserId, List<List<String>> allRowsList) {
        List<SalesOrderV2> salesOrderList = new ArrayList<>();
        SalesOrderHeaderV2 soHeader = null;
        List<SalesOrderLineV2> soLines = new ArrayList<>();
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        String orderNumber = null;
        int i = 1;
        for (List<String> listUploadedData : allRowsList) {
            if (orderNumber != null) {
                isSameOrder = orderNumber.equalsIgnoreCase(listUploadedData.get(0));
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
                orderNumber = listUploadedData.get(0);
                // Header
                if (oneTimeAllow) {
                    soHeader = new SalesOrderHeaderV2();
                    soHeader.setCompanyCode(companyCodeId);
                    soHeader.setBranchCode(plantId);
                    soHeader.setStoreID(plantId);
                    soHeader.setLanguageId(languageId);
                    soHeader.setWarehouseId(warehouseId);
                    soHeader.setLoginUserId(loginUserId);
                    soHeader.setPickListNumber(listUploadedData.get(0));
                    soHeader.setSalesOrderNumber(listUploadedData.get(0));
                    soHeader.setCustomerId(listUploadedData.get(1));
                    soHeader.setCustomerName(listUploadedData.get(2));
                    soHeader.setRequiredDeliveryDate(listUploadedData.get(8));
//                    if (listUploadedData.size() > 13 && listUploadedData.get(13) != null && !listUploadedData.get(13).isBlank()) {
//                        soHeader.setTokenNumber(listUploadedData.get(13));
//                    }
//                    if (listUploadedData.size() > 14 && listUploadedData.get(14) != null && !listUploadedData.get(14).isBlank()) {
//                        soHeader.setOrderType(listUploadedData.get(14));
//                    } else {
//                        soHeader.setOrderType("3");
//                    }
                    soHeader.setOrderType("3");
                }
                oneTimeAllow = false;

                // Line
                SalesOrderLineV2 soLine = new SalesOrderLineV2();
//                if (listUploadedData.size() > 14 && listUploadedData.get(14) != null && !listUploadedData.get(14).isBlank()) {
//                    soLine.setOrderType(listUploadedData.get(14));
//                } else {
//                    soLine.setOrderType("3");
//                }
                soLine.setOrderType("3");
                soLine.setLineReference(Long.valueOf(listUploadedData.get(3)));
//                soLine.setBarcodeId(listUploadedData.get(5));
                soLine.setSku(listUploadedData.get(4));
                soLine.setSkuDescription(listUploadedData.get(5));
                soLine.setOrderedQty(Double.valueOf(listUploadedData.get(6)));
                soLine.setNoBags(Double.valueOf(listUploadedData.get(7)));
//                if (listUploadedData.size() > 10 && listUploadedData.get(10) != null && !listUploadedData.get(10).isBlank()) {
//                    soLine.setUom(listUploadedData.get(10));
//                } else {
//                    soLine.setUom(UOM);
//                }
//                if (listUploadedData.size() > 11 && listUploadedData.get(11) != null && !listUploadedData.get(11).isBlank()) {
//                    soLine.setManufacturerName(listUploadedData.get(11));
//                }
//                if (listUploadedData.size() > 12 && listUploadedData.get(12) != null && !listUploadedData.get(12).isBlank()) {
//                    soLine.setStorageSectionId(listUploadedData.get(12));
//                }
                soLine.setUom("PIECE");
                soLine.setPickListNo(listUploadedData.get(0));
                soLine.setSalesOrderNo(listUploadedData.get(0));
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
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param allRowsList
     * @return
     */
    public List<InterWarehouseTransferInV2> prepInterwareHouseInDataV4(String companyCodeId, String plantId,
                                                                       String languageId, String warehouseId, String loginUserId,
                                                                       List<InboundOrderProcessV4> allRowsList) {
        List<InterWarehouseTransferInV2> whOrderList = new ArrayList<>();
        String orderNumber = null;
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        int i = 1;
        InterWarehouseTransferInHeaderV2 header = null;
        List<InterWarehouseTransferInLineV2> listWHLines = new ArrayList<>();
        for (InboundOrderProcessV4 listUploadedData : allRowsList) {
            if (orderNumber != null) {
                isSameOrder = orderNumber.equalsIgnoreCase(listUploadedData.getTransferOrderNumber());
            }
            // Header
            if (!isSameOrder) {
                InterWarehouseTransferInV2 orders = new InterWarehouseTransferInV2();
                orders.setInterWarehouseTransferInHeader(header);
                orders.setInterWarehouseTransferInLine(listWHLines);
                whOrderList.add(orders);

                //reset to create new order
                oneTimeAllow = true;
                isSameOrder = true;
                orderNumber = null;
                listWHLines = new ArrayList<>();
            }

            if (isSameOrder) {
                orderNumber = listUploadedData.getTransferOrderNumber();
                if (oneTimeAllow) {
                    header = new InterWarehouseTransferInHeaderV2();

                    header.setToBranchCode(plantId);
                    header.setToCompanyCode(companyCodeId);
                    header.setLanguageId(languageId);
                    header.setWarehouseId(warehouseId);
                    header.setLoginUserId(loginUserId);
                    header.setTransferOrderNumber(listUploadedData.getTransferOrderNumber());
                    header.setInboundOrderTypeId(listUploadedData.getInboundOrderTypeId());
                }
                oneTimeAllow = false;

                // Line
                InterWarehouseTransferInLineV2 line = new InterWarehouseTransferInLineV2();
                BeanUtils.copyProperties(listUploadedData, line, CommonUtils.getNullPropertyNames(listUploadedData));

                listWHLines.add(line);
            }

            if (allRowsList.size() == i) {
                InterWarehouseTransferInV2 orders = new InterWarehouseTransferInV2();
                orders.setInterWarehouseTransferInHeader(header);
                orders.setInterWarehouseTransferInLine(listWHLines);
                whOrderList.add(orders);
            }
            i++;
        }
        return whOrderList;
    }


    /**
     * ForKnowell
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param allRowsList
     * @return
     */
    public List<InterWarehouseTransferInV2> prepInterwareHouseInDataV7(String companyCodeId, String plantId,
                                                                       String languageId, String warehouseId, String loginUserId,
                                                                       List<InboundOrderProcessV4> allRowsList) {
        List<InterWarehouseTransferInV2> whOrderList = new ArrayList<>();
        String orderNumber = null;
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        int i = 1;
        InterWarehouseTransferInHeaderV2 header = null;
        List<InterWarehouseTransferInLineV2> listWHLines = new ArrayList<>();
        for (InboundOrderProcessV4 listUploadedData : allRowsList) {
            if (orderNumber != null) {
                isSameOrder = orderNumber.equalsIgnoreCase(listUploadedData.getTransferOrderNumber());
            }
            // Header
            if (!isSameOrder) {
                InterWarehouseTransferInV2 orders = new InterWarehouseTransferInV2();
                orders.setInterWarehouseTransferInHeader(header);
                orders.setInterWarehouseTransferInLine(listWHLines);
                whOrderList.add(orders);

                //reset to create new order
                oneTimeAllow = true;
                isSameOrder = true;
                orderNumber = null;
                listWHLines = new ArrayList<>();
            }

            if (isSameOrder) {
                orderNumber = listUploadedData.getTransferOrderNumber();
                if (oneTimeAllow) {
                    header = new InterWarehouseTransferInHeaderV2();

                    header.setToBranchCode(plantId);
                    header.setSourceBranchCode(listUploadedData.getSourceBranchCode());
                    header.setToCompanyCode(companyCodeId);
                    header.setSourceCompanyCode(listUploadedData.getSourceCompanyCode());
                    header.setLanguageId(languageId);
                    header.setWarehouseId(warehouseId);
                    header.setSourceWarehouseCode(warehouseId);
                    header.setLoginUserId(loginUserId);
                    header.setTransferOrderNumber(listUploadedData.getTransferOrderNumber());
                    header.setInboundOrderTypeId(listUploadedData.getInboundOrderTypeId());
                }
                oneTimeAllow = false;

                // Line
                InterWarehouseTransferInLineV2 line = new InterWarehouseTransferInLineV2();
                BeanUtils.copyProperties(listUploadedData, line, CommonUtils.getNullPropertyNames(listUploadedData));
                line.setFromCompanyCode(listUploadedData.getSourceCompanyCode());
                line.setFromBranchCode(listUploadedData.getSourceBranchCode());
                line.setSku(listUploadedData.getSku());
                line.setSkuDescription(listUploadedData.getSkuDescription());
                line.setNoBags(listUploadedData.getNoBags());
                line.setExpectedQty(listUploadedData.getExpectedQty());
                line.setExpectedQtyInPieces(listUploadedData.getOrderQty());
                line.setExpectedQtyInCases(listUploadedData.getExpectedQtyInCases());

                listWHLines.add(line);
            }

            if (allRowsList.size() == i) {
                InterWarehouseTransferInV2 orders = new InterWarehouseTransferInV2();
                orders.setInterWarehouseTransferInHeader(header);
                orders.setInterWarehouseTransferInLine(listWHLines);
                whOrderList.add(orders);
            }
            i++;
        }
        return whOrderList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param allRowsList
     * @return
     */
    public List<InterWarehouseTransferInV2> prepInterwareHouseInDataV8(String companyCodeId, String plantId,
                                                                       String languageId, String warehouseId, String loginUserId,
                                                                       List<OutboundOrderProcessV4> allRowsList) {
        List<InterWarehouseTransferInV2> whOrderList = new ArrayList<>();
        String orderNumber = null;
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        int i = 1;
        InterWarehouseTransferInHeaderV2 header = null;
        List<InterWarehouseTransferInLineV2> listWHLines = new ArrayList<>();
        for (OutboundOrderProcessV4 listUploadedData : allRowsList) {
            if (orderNumber != null) {
                isSameOrder = orderNumber.equalsIgnoreCase(listUploadedData.getTransferOrderNumber());
            }
            // Header
            if (!isSameOrder) {
                InterWarehouseTransferInV2 orders = new InterWarehouseTransferInV2();
                orders.setInterWarehouseTransferInHeader(header);
                orders.setInterWarehouseTransferInLine(listWHLines);
                whOrderList.add(orders);

                //reset to create new order
                oneTimeAllow = true;
                isSameOrder = true;
                orderNumber = null;
                listWHLines = new ArrayList<>();
            }

            if (isSameOrder) {
                orderNumber = listUploadedData.getTransferOrderNumber();
                if (oneTimeAllow) {
                    header = new InterWarehouseTransferInHeaderV2();

                    header.setToBranchCode(listUploadedData.getToBranchCode());
                    header.setSourceBranchCode(plantId);
                    header.setToCompanyCode(listUploadedData.getToCompanyCode());
                    header.setSourceCompanyCode(companyCodeId);
                    header.setLanguageId(languageId);
                    header.setWarehouseId(warehouseId);
                    header.setSourceWarehouseCode(warehouseId);
                    header.setLoginUserId(loginUserId);
                    header.setTransferOrderNumber(listUploadedData.getTransferOrderNumber());
                    header.setOutboundOrderTypeId(listUploadedData.getOutboundOrderTypeId());
                }
                oneTimeAllow = false;

                // Line
                InterWarehouseTransferInLineV2 line = new InterWarehouseTransferInLineV2();
                BeanUtils.copyProperties(listUploadedData, line, CommonUtils.getNullPropertyNames(listUploadedData));
                line.setSku(listUploadedData.getSku());
                line.setSkuDescription(listUploadedData.getSkuDescription());
                line.setLineReference(listUploadedData.getLineReference());
                line.setExpectedQty(listUploadedData.getOrderedQty());
                line.setExpectedQtyInCases(listUploadedData.getQtyInCase());
                line.setUom(listUploadedData.getUom());
                line.setExpectedQtyInPieces(listUploadedData.getQtyInPiece());

                listWHLines.add(line);
            }

            if (allRowsList.size() == i) {
                InterWarehouseTransferInV2 orders = new InterWarehouseTransferInV2();
                orders.setInterWarehouseTransferInHeader(header);
                orders.setInterWarehouseTransferInLine(listWHLines);
                whOrderList.add(orders);
            }
            i++;
        }
        return whOrderList;
    }

    /**
     * ForKnowell
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param allRowsList
     * @return
     */
    public List<InterWarehouseTransferInV2> prepInterwareHouseInDataV5(String companyCodeId, String plantId,
                                                                       String languageId, String warehouseId, String loginUserId,
                                                                       List<InboundOrderProcessV4> allRowsList) {
        List<InterWarehouseTransferInV2> whOrderList = new ArrayList<>();
        String orderNumber = null;
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        int i = 1;
        InterWarehouseTransferInHeaderV2 header = null;
        List<InterWarehouseTransferInLineV2> listWHLines = new ArrayList<>();
        for (InboundOrderProcessV4 listUploadedData : allRowsList) {
            if (orderNumber != null) {
                isSameOrder = orderNumber.equalsIgnoreCase(listUploadedData.getTransferOrderNumber());
            }
            // Header
            if (!isSameOrder) {
                InterWarehouseTransferInV2 orders = new InterWarehouseTransferInV2();
                orders.setInterWarehouseTransferInHeader(header);
                orders.setInterWarehouseTransferInLine(listWHLines);
                whOrderList.add(orders);

                //reset to create new order
                oneTimeAllow = true;
                isSameOrder = true;
                orderNumber = null;
                listWHLines = new ArrayList<>();
            }

            if (isSameOrder) {
                orderNumber = listUploadedData.getTransferOrderNumber();
                if (oneTimeAllow) {
                    header = new InterWarehouseTransferInHeaderV2();

                    header.setToBranchCode(plantId);
                    header.setSourceBranchCode(plantId);
                    header.setToCompanyCode(companyCodeId);
                    header.setSourceCompanyCode(companyCodeId);
                    header.setLanguageId(languageId);
                    header.setWarehouseId(warehouseId);
                    header.setSourceWarehouseCode(warehouseId);
                    header.setLoginUserId(loginUserId);
                    header.setTransferOrderNumber(listUploadedData.getTransferOrderNumber());
                    header.setInboundOrderTypeId(listUploadedData.getInboundOrderTypeId());
                }
                oneTimeAllow = false;

                // Line
                InterWarehouseTransferInLineV2 line = new InterWarehouseTransferInLineV2();
                BeanUtils.copyProperties(listUploadedData, line, CommonUtils.getNullPropertyNames(listUploadedData));

                listWHLines.add(line);
            }

            if (allRowsList.size() == i) {
                InterWarehouseTransferInV2 orders = new InterWarehouseTransferInV2();
                orders.setInterWarehouseTransferInHeader(header);
                orders.setInterWarehouseTransferInLine(listWHLines);
                whOrderList.add(orders);
            }
            i++;
        }
        return whOrderList;
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param list
     * @return
     */
    public List<ASNV2> prepAsnMultipleDataV4(String companyCodeId, String plantId,
                                             String languageId, String warehouseId, String loginUserId,
                                             List<InboundOrderProcessV4> list) {
        List<InboundOrderProcessV4> allRowsList = list.stream().sorted(Comparator.comparing(InboundOrderProcessV4::getAsnNumber)).collect(Collectors.toList());
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
                    header.setCustomerId(listUploadedData.getCustomerId());
                    header.setCustomerName(listUploadedData.getCustomerName());
                }
                oneTimeAllow = false;

                // Line
                ASNLineV2 line = new ASNLineV2();
                BeanUtils.copyProperties(listUploadedData, line, CommonUtils.getNullPropertyNames(listUploadedData));

                line.setBranchCode(plantId);
                line.setCompanyCode(companyCodeId);
                line.setLineReference(lineReference);
                line.setNoPairs(listUploadedData.getNoPairs());
                line.setUom(listUploadedData.getUom());
                line.setSkuDescription(listUploadedData.getSkuDescription());
                line.setExpectedDate(DateUtils.date2String_YYYYMMDD(new Date()));
                line.setVehicleNo(listUploadedData.getVehicleNo());
                line.setVehicleUnloadingDate(listUploadedData.getVehicleUnloadingDate());
                line.setVehicleReportingDate(listUploadedData.getVehicleReportingDate());
                line.setCustomerId(listUploadedData.getCustomerId());
                line.setCustomerName(listUploadedData.getCustomerName());
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

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param list
     * @return
     */
    public List<ASNV2> prepAsnMultipleDataV5(String companyCodeId, String plantId,
                                             String languageId, String warehouseId, String loginUserId,
                                             List<InboundOrderProcessV4> list) {
        List<InboundOrderProcessV4> allRowsList = list.stream().sorted(Comparator.comparing(InboundOrderProcessV4::getAsnNumber)).collect(Collectors.toList());
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
                line.setUom(listUploadedData.getUom());
                line.setSkuDescription(listUploadedData.getSkuDescription());
                line.setExpectedDate(DateUtils.date2String_YYYYMMDD(new Date()));
                line.setVehicleNo(listUploadedData.getVehicleNo());
                line.setVehicleUnloadingDate(listUploadedData.getVehicleUnloadingDate());
                line.setVehicleReportingDate(listUploadedData.getVehicleReportingDate());
                line.setCustomerId(listUploadedData.getCustomerId());

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

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param list
     * @return
     */
    public List<SalesOrderV2> prepSalesOrderDataV5(String companyCodeId, String plantId, String languageId,
                                                   String warehouseId, String loginUserId, List<OutboundOrderProcessV4> list) {
        List<OutboundOrderProcessV4> allRowsList = list.stream().sorted(Comparator.comparing(OutboundOrderProcessV4::getPickListNumber)).collect(Collectors.toList());
        List<SalesOrderV2> salesOrderList = new ArrayList<>();
        SalesOrderHeaderV2 soHeader = null;
        List<SalesOrderLineV2> soLines = new ArrayList<>();
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        String orderNumber = null;
        int i = 1;
        String salesOrderNo = String.valueOf(System.currentTimeMillis());
        for (OutboundOrderProcessV4 listUploadedData : allRowsList) {
            String pickListNumber = listUploadedData.getPickListNumber();
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
                soLine.setSku(listUploadedData.getSku());
                soLine.setLineReference(listUploadedData.getItm());
                soLine.setMtoNumber(listUploadedData.getMtoNumber());
                soLine.setSpecialStock(listUploadedData.getSpecialStock());
                soLine.setShipToCode(listUploadedData.getShipToCode());
                soLine.setShipToParty(listUploadedData.getShipToParty());
                soLine.setQtyInCase(listUploadedData.getQtyInCase());
                soLine.setQtyInPiece(listUploadedData.getQtyInPiece());

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
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param list
     * @return
     */
    public List<SalesOrderV2> emptyCrateV5(String companyCodeId, String plantId, String languageId,
                                                   String warehouseId, String loginUserId, List<OutboundOrderProcessV4> list) {
        List<OutboundOrderProcessV4> allRowsList = list.stream().sorted(Comparator.comparing(OutboundOrderProcessV4::getPickListNumber)).collect(Collectors.toList());
        List<SalesOrderV2> salesOrderList = new ArrayList<>();
        SalesOrderHeaderV2 soHeader = null;
        List<SalesOrderLineV2> soLines = new ArrayList<>();
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        String orderNumber = null;
        int i = 1;
        String salesOrderNo = String.valueOf(System.currentTimeMillis());
        for (OutboundOrderProcessV4 listUploadedData : allRowsList) {
            String pickListNumber = listUploadedData.getPickListNumber();
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
                soLine.setSku(listUploadedData.getSku());
                soLine.setUom("Crate");
                soLine.setLineReference(listUploadedData.getItm());
                soLine.setMtoNumber(listUploadedData.getMtoNumber());
                soLine.setSpecialStock(listUploadedData.getSpecialStock());
                soLine.setShipToCode(listUploadedData.getShipToCode());
                soLine.setShipToParty(listUploadedData.getShipToParty());
                soLine.setQtyInCase(listUploadedData.getQtyInCase());
                soLine.setQtyInPiece(listUploadedData.getQtyInPiece());

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
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param list
     * @return
     */
    public List<ReturnPOV2> purchaseReturn(String companyCodeId, String plantId, String languageId,
                                           String warehouseId, String loginUserId, List<OutboundOrderProcessV4> list) {
        List<OutboundOrderProcessV4> allRowsList = list.stream().sorted(Comparator.comparing(OutboundOrderProcessV4::getPickListNumber)).collect(Collectors.toList());
        List<ReturnPOV2> returnPOList = new ArrayList<>();
        ReturnPOHeaderV2 poHeader = null;
        List<ReturnPOLineV2> poLines = new ArrayList<>();
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        String orderNumber = null;
        int i = 1;
        String poOrderNo = String.valueOf(System.currentTimeMillis());
        for (OutboundOrderProcessV4 listUploadedData : allRowsList) {
            String pickListNumber = listUploadedData.getPickListNumber();
            if (orderNumber != null) {
                isSameOrder = orderNumber.equalsIgnoreCase(pickListNumber);
            }
            if (!isSameOrder) {
                ReturnPOV2 returnPOV2 = new ReturnPOV2();
                returnPOV2.setReturnPOHeader(poHeader);
                returnPOV2.setReturnPOLine(poLines);
                returnPOList.add(returnPOV2);

                //reset to create new order
                oneTimeAllow = true;
                isSameOrder = true;
                orderNumber = null;
                poLines = new ArrayList<>();
            }
            if (isSameOrder) {
                orderNumber = pickListNumber;
                // Header
                if (oneTimeAllow) {
                    poHeader = new ReturnPOHeaderV2();
                    BeanUtils.copyProperties(listUploadedData, poHeader, CommonUtils.getNullPropertyNames(listUploadedData));
                    poHeader.setCompanyCode(companyCodeId);
                    poHeader.setBranchCode(plantId);
                    poHeader.setStoreID(plantId);
                    poHeader.setLanguageId(languageId);
                    poHeader.setWareHouseId(warehouseId);
                    poHeader.setPickListNumber(pickListNumber);
                    poHeader.setCustomerId(listUploadedData.getCustomerId());
                    poHeader.setRequiredDeliveryDate(listUploadedData.getRequiredDeliveryDate());
                    poHeader.setRequiredDeliveryDate(DateUtils.date2String_YYYYMMDD(new Date()));
                }
                oneTimeAllow = false;

                // Line
                ReturnPOLineV2 poLineV2 = new ReturnPOLineV2();
                BeanUtils.copyProperties(listUploadedData, poLineV2, CommonUtils.getNullPropertyNames(listUploadedData));
                poLineV2.setSku(listUploadedData.getSku());
                poLineV2.setLineReference(listUploadedData.getItm());
                poLineV2.setSupplierName(listUploadedData.getSupplierName());
                poLineV2.setSkuDescription(listUploadedData.getSkuDescription());
                poLineV2.setUom(listUploadedData.getUom());
                poLineV2.setReturnQty(listUploadedData.getOrderedQty());
                poLineV2.setSupplierInvoiceNo(listUploadedData.getSupplierInvoiceNo());
                poLineV2.setReturnOrderNo(listUploadedData.getSupplierInvoiceNo());
                poLineV2.setBarcodeId(listUploadedData.getBarcodeId());
                poLines.add(poLineV2);
            }

            if (allRowsList.size() == i) {
                ReturnPOV2 returnPOV2 = new ReturnPOV2();
                returnPOV2.setReturnPOHeader(poHeader);
                returnPOV2.setReturnPOLine(poLines);
                returnPOList.add(returnPOV2);
            }
            i++;
        }
        return returnPOList;
    }


    /**
     * Upload Multiple Order - PickList / SalesOrder
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param list
     * @return
     */
    public List<SalesOrderV2> prepSalesOrderDataV7(String companyCodeId, String plantId, String languageId,
                                                         String warehouseId, String loginUserId, List<OutboundOrderProcessV4> list) {
        List<OutboundOrderProcessV4> allRowsList = list.stream().sorted(Comparator.comparing(OutboundOrderProcessV4::getPickListNumber)).collect(Collectors.toList());
        List<SalesOrderV2> salesOrderList = new ArrayList<>();
        SalesOrderHeaderV2 soHeader = null;
        List<SalesOrderLineV2> soLines = new ArrayList<>();
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        String orderNumber = null;
        int i = 1;
//        String salesOrderNo = String.valueOf(System.currentTimeMillis());
        for (OutboundOrderProcessV4 listUploadedData : allRowsList) {
            String pickListNumber = listUploadedData.getPickListNumber();
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
                    soHeader.setSalesOrderNumber(listUploadedData.getSalesOrderNumber());
                    soHeader.setRequiredDeliveryDate(DateUtils.date2String_YYYYMMDD(new Date()));
                }
                oneTimeAllow = false;

                // Line
                SalesOrderLineV2 soLine = new SalesOrderLineV2();
                BeanUtils.copyProperties(listUploadedData, soLine, CommonUtils.getNullPropertyNames(listUploadedData));
                soLine.setPickListNo(pickListNumber);
                soLine.setSalesOrderNo(listUploadedData.getSalesOrderNumber());
                soLine.setSku(listUploadedData.getSku());
                soLine.setLineReference(listUploadedData.getLineReference());
                soLine.setMtoNumber(listUploadedData.getMtoNumber());
                soLine.setSpecialStock(listUploadedData.getSpecialStock());
                soLine.setShipToCode(listUploadedData.getShipToCode());
                soLine.setShipToParty(listUploadedData.getShipToParty());
                soLine.setQtyInCase(listUploadedData.getQtyInCase());
                soLine.setQtyInPiece(listUploadedData.getQtyInPiece());
                soLine.setExpectedQtyInCases(listUploadedData.getQtyInCase());
                soLine.setExpectedQtyInPieces(listUploadedData.getQtyInPiece());
                soLine.setManufacturerName(listUploadedData.getManufacturerName());
                soLine.setManufacturerCode(listUploadedData.getManufacturerName());
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


    //==========================================Knowell=====================================//
    /**
     * knowell
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param list
     * @return
     */
    public List<ASNV2> prepAsnMultipleDataV7(String companyCodeId, String plantId,
                                             String languageId, String warehouseId, String loginUserId,
                                             List<InboundOrderProcessV4> list) {
        List<InboundOrderProcessV4> allRowsList = list.stream().sorted(Comparator.comparing(InboundOrderProcessV4::getAsnNumber)).collect(Collectors.toList());
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
                line.setUom("PIECE");
                line.setSkuDescription(listUploadedData.getSkuDescription());
                line.setExpectedDate(DateUtils.date2String_YYYYMMDD(new Date()));
                line.setManufacturerName(line.getManufacturerName());
                line.setManufacturerCode(line.getManufacturerName());
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

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param allRowsList
     * @return
     */
    public List<SaleOrderReturnV2> prepSaleOrderReturnDataV4(String companyCodeId, String plantId,
                                                             String languageId, String warehouseId, String loginUserId,
                                                             List<InboundOrderProcessV4> allRowsList) {
        List<SaleOrderReturnV2> orderList = new ArrayList<>();
        String orderNumber = null;
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        int i = 1;
        long lineReference = 1;
        SOReturnHeaderV2 header = null;
        List<SOReturnLineV2> listLines = new ArrayList<>();
        for (InboundOrderProcessV4 listUploadedData : allRowsList) {
            if (orderNumber != null) {
                isSameOrder = orderNumber.equalsIgnoreCase(listUploadedData.getTransferOrderNumber());
            }
            // Header
            if (!isSameOrder) {
                SaleOrderReturnV2 orders = new SaleOrderReturnV2();
                orders.setSoReturnHeader(header);
                orders.setSoReturnLine(listLines);
                orderList.add(orders);

                //reset to create new order
                oneTimeAllow = true;
                isSameOrder = true;
                orderNumber = null;
                listLines = new ArrayList<>();
            }

            if (isSameOrder) {
                orderNumber = listUploadedData.getTransferOrderNumber();
                if (oneTimeAllow) {
                    header = new SOReturnHeaderV2();

                    header.setBranchCode(plantId);
                    header.setCompanyCode(companyCodeId);
                    header.setLanguageId(languageId);
                    header.setWarehouseId(warehouseId);
                    header.setLoginUserId(loginUserId);
                    header.setTransferOrderNumber(listUploadedData.getTransferOrderNumber());
                    header.setInboundOrderTypeId(listUploadedData.getInboundOrderTypeId());
                    header.setAsnNumber(listUploadedData.getAsnNumber());
                }
                oneTimeAllow = false;

                // Line
                SOReturnLineV2 line = new SOReturnLineV2();
                BeanUtils.copyProperties(listUploadedData, line, CommonUtils.getNullPropertyNames(listUploadedData));
                line.setSku(listUploadedData.getSku());
                line.setSkuDescription(listUploadedData.getSkuDescription());
                line.setLineReference(lineReference);
                line.setExpectedDate(DateUtils.date2String_YYYYMMDD(new Date()));
                line.setExpectedQty(listUploadedData.getExpectedQtyInPieces());
                line.setExpectedQtyInPieces(listUploadedData.getExpectedQtyInPieces());
                line.setExpectedQtyInCases(listUploadedData.getExpectedQtyInCases());
                line.setManufacturerName(line.getManufacturerName());
                line.setManufacturerCode(line.getManufacturerName());
                line.setUom("PIECE");

                lineReference++;
                listLines.add(line);
            }

            if (allRowsList.size() == i) {
                SaleOrderReturnV2 orders = new SaleOrderReturnV2();
                orders.setSoReturnHeader(header);
                orders.setSoReturnLine(listLines);
                orderList.add(orders);
            }
            i++;
        }
        return orderList;
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
    public List<SaleOrderReturnV2> prepSaleOrderReturnDataV5(String companyCodeId, String plantId,
                                                             String languageId, String warehouseId, String loginUserId,
                                                             List<InboundOrderProcessV4> allRowsList) {
        List<SaleOrderReturnV2> orderList = new ArrayList<>();
        String orderNumber = null;
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        int i = 1;
        long lineReference = 1;
        SOReturnHeaderV2 header = null;
        List<SOReturnLineV2> listLines = new ArrayList<>();
        for (InboundOrderProcessV4 listUploadedData : allRowsList) {
            if (orderNumber != null) {
                isSameOrder = orderNumber.equalsIgnoreCase(listUploadedData.getTransferOrderNumber());
            }
            // Header
            if (!isSameOrder) {
                SaleOrderReturnV2 orders = new SaleOrderReturnV2();
                orders.setSoReturnHeader(header);
                orders.setSoReturnLine(listLines);
                orderList.add(orders);

                //reset to create new order
                oneTimeAllow = true;
                isSameOrder = true;
                orderNumber = null;
                listLines = new ArrayList<>();
            }

            if (isSameOrder) {
                orderNumber = listUploadedData.getTransferOrderNumber();
                if (oneTimeAllow) {
                    header = new SOReturnHeaderV2();

                    header.setBranchCode(plantId);
                    header.setCompanyCode(companyCodeId);
                    header.setLanguageId(languageId);
                    header.setWarehouseId(warehouseId);
                    header.setLoginUserId(loginUserId);
                    header.setTransferOrderNumber(listUploadedData.getSalesOrderNumber());
//                    header.setInboundOrderTypeId(listUploadedData.getInboundOrderTypeId());
                    header.setInboundOrderTypeId(header.getInboundOrderTypeId());
                    header.setAsnNumber(listUploadedData.getAsnNumber());
                    header.setCustomerId(listUploadedData.getCustomerId());
                    header.setCustomerName(listUploadedData.getCustomerName());
                }
                oneTimeAllow = false;

                // Line
                SOReturnLineV2 line = new SOReturnLineV2();
                BeanUtils.copyProperties(listUploadedData, line, CommonUtils.getNullPropertyNames(listUploadedData));


                line.setLineReference(lineReference);
                line.setNoPairs(listUploadedData.getNoPairs());
                line.setUom(listUploadedData.getUom());
                line.setSkuDescription(listUploadedData.getSkuDescription());
                line.setExpectedDate(DateUtils.date2String_YYYYMMDD(new Date()));
                line.setVehicleNo(listUploadedData.getVehicleNo());
                line.setVehicleUnloadingDate(listUploadedData.getVehicleUnloadingDate());
                line.setVehicleReportingDate(listUploadedData.getVehicleReportingDate());
                line.setCustomerId(listUploadedData.getCustomerId());
                line.setCustomerName(listUploadedData.getCustomerName());

                lineReference++;
                listLines.add(line);
            }

            if (allRowsList.size() == i) {
                SaleOrderReturnV2 orders = new SaleOrderReturnV2();
                orders.setSoReturnHeader(header);
                orders.setSoReturnLine(listLines);
                orderList.add(orders);
            }
            i++;
        }
        return orderList;
    }
}
