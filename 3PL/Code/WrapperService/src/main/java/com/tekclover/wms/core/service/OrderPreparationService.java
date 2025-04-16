package com.tekclover.wms.core.service;

import com.tekclover.wms.core.exception.BadRequestException;
import com.tekclover.wms.core.model.transaction.InhouseTransferHeader;
import com.tekclover.wms.core.model.transaction.InhouseTransferLine;
import com.tekclover.wms.core.model.transaction.InhouseTransferUpload;
import com.tekclover.wms.core.model.transaction.StockAdjustment;
import com.tekclover.wms.core.model.warehouse.inbound.almailem.*;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.*;
import com.tekclover.wms.core.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.*;

@Slf4j
@Service
public class OrderPreparationService {

    private Path fileStorageLocation = null;
    private static final String UOM = "EACH";

    //============================================OUTBOUND====================================================================

    /**
     * @param allRowsList
     * @return
     */
    public List<ShipmentOrderV2> prepSOData(List<List<String>> allRowsList) {
        List<ShipmentOrderV2> shipmentOrderList = new ArrayList<>();

        for (List<String> listUploadedData : allRowsList) {
            Set<SOHeaderV2> setSOHeader = new HashSet<>();
            List<SOLineV2> soLines = new ArrayList<>();

            // Header
            SOHeaderV2 soHeader = null;
            boolean oneTimeAllow = true;
            for (String column : listUploadedData) {
                if (oneTimeAllow) {
                    soHeader = new SOHeaderV2();
                    soHeader.setRequiredDeliveryDate(listUploadedData.get(0));
                    soHeader.setStoreID(listUploadedData.get(1));
                    soHeader.setStoreName(listUploadedData.get(2));
                    soHeader.setTransferOrderNumber(listUploadedData.get(3));
                    soHeader.setWarehouseId(listUploadedData.get(4));
                    setSOHeader.add(soHeader);
                }
                oneTimeAllow = false;

                // Line
                SOLineV2 soLine = new SOLineV2();
                soLine.setLineReference(Long.valueOf(listUploadedData.get(5)));
                soLine.setOrderType(listUploadedData.get(6));
                soLine.setOrderedQty(Double.valueOf(listUploadedData.get(7)));
                soLine.setSku(listUploadedData.get(8));
                soLine.setSkuDescription(listUploadedData.get(9));
                soLine.setUom(listUploadedData.get(10));
                soLines.add(soLine);
            }

            ShipmentOrderV2 shipmentOrder = new ShipmentOrderV2();
            shipmentOrder.setSoHeader(soHeader);
            shipmentOrder.setSoLine(soLines);
            shipmentOrderList.add(shipmentOrder);
        }
        return shipmentOrderList;
    }

    /**
     * @param allRowsList
     * @return
     */
    public List<SalesOrderV2> prepSalesOrderData(List<List<String>> allRowsList) {
        List<SalesOrderV2> salesOrderList = new ArrayList<>();
        SalesOrderHeaderV2 soHeader = null;
        List<SalesOrderLineV2> soLines = new ArrayList<>();
        boolean oneTimeAllow = true;
        for (List<String> listUploadedData : allRowsList) {
//			Set<SalesOrderHeaderV2> setSOHeader = new HashSet<>();

            // Header

//			for (String column : listUploadedData) {
            if (oneTimeAllow) {
                soHeader = new SalesOrderHeaderV2();
                soHeader.setCompanyCode(listUploadedData.get(0));
                soHeader.setStoreID(listUploadedData.get(1));
                soHeader.setStoreName(listUploadedData.get(2));
                if (listUploadedData.get(3) != null && !listUploadedData.get(3).isBlank()) {
                    soHeader.setLanguageId(listUploadedData.get(3));
                }
                soHeader.setWarehouseId(listUploadedData.get(4));
                soHeader.setRequiredDeliveryDate(listUploadedData.get(5));
                soHeader.setPickListNumber(listUploadedData.get(6));
                soHeader.setSalesOrderNumber(listUploadedData.get(7));
                soHeader.setTokenNumber(listUploadedData.get(8));
                soHeader.setOrderType(listUploadedData.get(9));
//					setSOHeader.add(soHeader);
            }
            oneTimeAllow = false;

            // Line
            SalesOrderLineV2 soLine = new SalesOrderLineV2();
            soLine.setOrderType(listUploadedData.get(9));
            soLine.setLineReference(Long.valueOf(listUploadedData.get(10)));
            soLine.setOrderedQty(Double.valueOf(listUploadedData.get(11)));
            soLine.setSku(listUploadedData.get(12));
            soLine.setSkuDescription(listUploadedData.get(13));
            soLine.setUom(listUploadedData.get(14));
            soLine.setStorageSectionId(listUploadedData.get(15));
            soLine.setManufacturerName(listUploadedData.get(16));
            soLine.setManufacturerCode(listUploadedData.get(16));
            soLine.setPickListNo(listUploadedData.get(6));
            soLine.setSalesOrderNo(listUploadedData.get(7));
            soLines.add(soLine);
        }

        SalesOrderV2 salesOrder = new SalesOrderV2();
        salesOrder.setSalesOrderHeader(soHeader);
        salesOrder.setSalesOrderLine(soLines);
        salesOrderList.add(salesOrder);
//		}
        return salesOrderList;
    }

    /**
     * Upload Multiple Order - PickList / SalesOrder
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
                    soHeader.setStoreID(plantId);
                    soHeader.setLanguageId(languageId);
                    soHeader.setWarehouseId(warehouseId);
                    soHeader.setLoginUserId(loginUserId);
                    soHeader.setPickListNumber(listUploadedData.get(0));
                    soHeader.setSalesOrderNumber(listUploadedData.get(1));
                    soHeader.setCustomerId(listUploadedData.get(2));
                    soHeader.setCustomerName(listUploadedData.get(3));
                    soHeader.setRequiredDeliveryDate(listUploadedData.get(9));
                    if (listUploadedData.size() > 13 && listUploadedData.get(13) != null && !listUploadedData.get(13).isBlank()) {
                        soHeader.setTokenNumber(listUploadedData.get(13));
                    }
                    if (listUploadedData.size() > 14 && listUploadedData.get(14) != null && !listUploadedData.get(14).isBlank()) {
                        soHeader.setOrderType(listUploadedData.get(14));
                    } else {
                        soHeader.setOrderType("3");
                    }
                }
                oneTimeAllow = false;

                // Line
                SalesOrderLineV2 soLine = new SalesOrderLineV2();
                if (listUploadedData.size() > 14 && listUploadedData.get(14) != null && !listUploadedData.get(14).isBlank()) {
                    soLine.setOrderType(listUploadedData.get(14));
                } else {
                    soLine.setOrderType("3");
                }
                soLine.setLineReference(Long.valueOf(listUploadedData.get(4)));
                soLine.setBarcodeId(listUploadedData.get(5));
                soLine.setSku(listUploadedData.get(6));
                soLine.setSkuDescription(listUploadedData.get(7));
                soLine.setOrderedQty(Double.valueOf(listUploadedData.get(8)));
                if (listUploadedData.size() > 10 && listUploadedData.get(10) != null && !listUploadedData.get(10).isBlank()) {
                    soLine.setUom(listUploadedData.get(10));
                } else {
                    soLine.setUom(UOM);
                }
                if (listUploadedData.size() > 11 && listUploadedData.get(11) != null && !listUploadedData.get(11).isBlank()) {
                    soLine.setManufacturerName(listUploadedData.get(11));
                }
                if (listUploadedData.size() > 12 && listUploadedData.get(12) != null && !listUploadedData.get(12).isBlank()) {
                    soLine.setStorageSectionId(listUploadedData.get(12));
                }
                soLine.setPickListNo(listUploadedData.get(0));
                soLine.setSalesOrderNo(listUploadedData.get(1));
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
     * @param allRowsList
     * @return
     */
    public List<SalesOrderV2> prepSalesOrderData(String companyCodeId, String plantId, String languageId,
                                                  String warehouseId, String loginUserId, List<List<String>> allRowsList) {
        List<SalesOrderV2> salesOrderList = new ArrayList<>();
        SalesOrderHeaderV2 soHeader = null;
        List<SalesOrderLineV2> soLines = new ArrayList<>();
        boolean oneTimeAllow = true;
        for (List<String> listUploadedData : allRowsList) {
//			Set<SalesOrderHeaderV2> setSOHeader = new HashSet<>();

            // Header

//			for (String column : listUploadedData) {
            if (oneTimeAllow) {
                soHeader = new SalesOrderHeaderV2();
                soHeader.setCompanyCode(companyCodeId);
                soHeader.setStoreID(plantId);
                soHeader.setStoreName(listUploadedData.get(2));
//					if(listUploadedData.get(3) != null && !listUploadedData.get(3).isBlank()) {
                soHeader.setLanguageId(languageId);
//					}
                soHeader.setWarehouseId(warehouseId);
                soHeader.setLoginUserId(loginUserId);
                soHeader.setRequiredDeliveryDate(listUploadedData.get(5));
                soHeader.setPickListNumber(listUploadedData.get(6));
                soHeader.setSalesOrderNumber(listUploadedData.get(7));
                soHeader.setTokenNumber(listUploadedData.get(8));
                soHeader.setOrderType(listUploadedData.get(9));
                if (listUploadedData.get(17) != null && !listUploadedData.get(17).isBlank()) {
                    soHeader.setCustomerId(listUploadedData.get(17));
                }
                if (listUploadedData.get(18) != null && !listUploadedData.get(18).isBlank()) {
                    soHeader.setCustomerName(listUploadedData.get(18));
                }
//					setSOHeader.add(soHeader);
            }
            oneTimeAllow = false;

            // Line
            SalesOrderLineV2 soLine = new SalesOrderLineV2();
            soLine.setOrderType(listUploadedData.get(9));
            soLine.setLineReference(Long.valueOf(listUploadedData.get(10)));
            soLine.setOrderedQty(Double.valueOf(listUploadedData.get(11)));
            soLine.setSku(listUploadedData.get(12));
            soLine.setSkuDescription(listUploadedData.get(13));
            soLine.setUom(listUploadedData.get(14));
            soLine.setStorageSectionId(listUploadedData.get(15));
            soLine.setManufacturerName(listUploadedData.get(16));
            soLine.setManufacturerCode(listUploadedData.get(16));
            soLine.setPickListNo(listUploadedData.get(6));
            soLine.setSalesOrderNo(listUploadedData.get(7));
            soLines.add(soLine);
        }

        SalesOrderV2 salesOrder = new SalesOrderV2();
        salesOrder.setSalesOrderHeader(soHeader);
        salesOrder.setSalesOrderLine(soLines);
        salesOrderList.add(salesOrder);
//		}
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
    public List<SalesOrderV2> prepSalesOrderDataV3(String companyCodeId, String plantId, String languageId,
                                                    String warehouseId, String loginUserId, List<List<String>> allRowsList) {
        List<SalesOrderV2> salesOrderList = new ArrayList<>();
        SalesOrderHeaderV2 soHeader = null;
        List<SalesOrderLineV2> soLines = new ArrayList<>();
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        String orderNumber = null;
        int i = 1;
//		String orderGroupByUpload = String.valueOf(System.currentTimeMillis());
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
                    soHeader.setStoreID(plantId);
                    soHeader.setBranchCode(plantId);
                    soHeader.setLanguageId(languageId);
                    soHeader.setWarehouseId(warehouseId);
                    soHeader.setLoginUserId(loginUserId);
                    soHeader.setPickListNumber(listUploadedData.get(0));
                    soHeader.setCustomerId(listUploadedData.get(2));
                    soHeader.setCustomerName(listUploadedData.get(3));
                    soHeader.setRequiredDeliveryDate(listUploadedData.get(9));
                    if (listUploadedData.size() > 13 && listUploadedData.get(14) != null && !listUploadedData.get(14).isBlank()) {
                        soHeader.setOrderType(listUploadedData.get(14));
                    } else {
                        soHeader.setOrderType("3");
                    }
                    if (listUploadedData.size() > 12 && listUploadedData.get(13) != null && !listUploadedData.get(13).isBlank()) {
                        soHeader.setTokenNumber(listUploadedData.get(13));
                    }
                    soHeader.setSalesOrderNumber(listUploadedData.get(1));
                }
                oneTimeAllow = false;

                // Line
                SalesOrderLineV2 soLine = new SalesOrderLineV2();
                if (listUploadedData.size() > 13 && listUploadedData.get(14) != null && !listUploadedData.get(14).isBlank()) {
                    soLine.setOrderType(listUploadedData.get(14));
                } else {
                    soLine.setOrderType("3");
                }
                soLine.setLineReference(Long.valueOf(listUploadedData.get(4)));
                soLine.setOrderedQty(Double.valueOf(listUploadedData.get(8)));
                soLine.setSku(listUploadedData.get(6));
                if (listUploadedData.size() > 9 && listUploadedData.get(10) != null && !listUploadedData.get(10).isBlank()) {
                    soLine.setUom(listUploadedData.get(10));
                } else {
                    soLine.setUom(UOM);
                }
                soLine.setPickListNo(listUploadedData.get(0));
                soLine.setSalesOrderNo(listUploadedData.get(1));
                soLine.setSkuDescription(listUploadedData.get(7));
                soLine.setBarcodeId(listUploadedData.get(5));
                if (listUploadedData.size() > 10 && listUploadedData.get(11) != null && !listUploadedData.get(11).isBlank()) {
                    soLine.setManufacturerName(listUploadedData.get(11));
                    soLine.setManufacturerCode(listUploadedData.get(11));
                }
                if (listUploadedData.size() > 11 && listUploadedData.get(12) != null && !listUploadedData.get(12).isBlank()) {
                    soLine.setStorageSectionId(listUploadedData.get(12));
                }
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

    //==================================================IMPEX - V4=====INBOUND===============================================================

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

    //==================================================IMPEX - V4=====OUTBOUND===============================================================

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
            String pickListNumber = listUploadedData.getPickListNumber() != null ? listUploadedData.getPickListNumber() : listUploadedData.getSalesOrderNumber();
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
                }
                oneTimeAllow = false;

                // Line
                SalesOrderLineV2 soLine = new SalesOrderLineV2();
                BeanUtils.copyProperties(listUploadedData, soLine, CommonUtils.getNullPropertyNames(listUploadedData));
                soLine.setPickListNo(pickListNumber);
                soLine.setSalesOrderNo(listUploadedData.getSalesOrderNumber());
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
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param allRowsList
     * @return
     */
    public List<InterWarehouseTransferOutV2> prepInterWarehouseOutDataV4(String companyCodeId, String plantId,
                                                                         String languageId, String warehouseId, String loginUserId,
                                                                         List<OutboundOrderProcessV4> allRowsList) {
        List<InterWarehouseTransferOutV2> whOrderList = new ArrayList<>();
        String orderNumber = null;
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        int i = 1;
        InterWarehouseTransferOutHeaderV2 header = null;
        List<InterWarehouseTransferOutLineV2> listWHLines = new ArrayList<>();
        for (OutboundOrderProcessV4 listUploadedData : allRowsList) {
            if (orderNumber != null) {
                isSameOrder = orderNumber.equalsIgnoreCase(listUploadedData.getTransferOrderNumber());
            }
            // Header
            if (!isSameOrder) {
                InterWarehouseTransferOutV2 orders = new InterWarehouseTransferOutV2();
                orders.setInterWarehouseTransferOutHeader(header);
                orders.setInterWarehouseTransferOutLine(listWHLines);
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
                    header = new InterWarehouseTransferOutHeaderV2();
                    BeanUtils.copyProperties(listUploadedData, header, CommonUtils.getNullPropertyNames(listUploadedData));
                    header.setFromCompanyCode(companyCodeId);
                    header.setFromBranchCode(plantId);
                    header.setLanguageId(languageId);
                    header.setWarehouseId(warehouseId);
                    header.setLoginUserId(loginUserId);
                }
                oneTimeAllow = false;

                // Line
                InterWarehouseTransferOutLineV2 line = new InterWarehouseTransferOutLineV2();
                BeanUtils.copyProperties(listUploadedData, line, CommonUtils.getNullPropertyNames(listUploadedData));

                listWHLines.add(line);
            }

            if (allRowsList.size() == i) {
                InterWarehouseTransferOutV2 orders = new InterWarehouseTransferOutV2();
                orders.setInterWarehouseTransferOutHeader(header);
                orders.setInterWarehouseTransferOutLine(listWHLines);
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
    public List<ReturnPOV2> prepReturnOrderDataV4(String companyCodeId, String plantId, String languageId,
                                                  String warehouseId, String loginUserId, List<OutboundOrderProcessV4> allRowsList) {
        List<ReturnPOV2> orderList = new ArrayList<>();
        ReturnPOHeaderV2 header = null;
        List<ReturnPOLineV2> lines = new ArrayList<>();
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        String orderNumber = null;
        int i = 1;
        for (OutboundOrderProcessV4 listUploadedData : allRowsList) {
            if (orderNumber != null) {
                isSameOrder = orderNumber.equalsIgnoreCase(listUploadedData.getPoNumber());
            }
            if (!isSameOrder) {
                ReturnPOV2 order = new ReturnPOV2();
                order.setReturnPOHeader(header);
                order.setReturnPOLine(lines);
                orderList.add(order);

                //reset to create new order
                oneTimeAllow = true;
                isSameOrder = true;
                orderNumber = null;
                lines = new ArrayList<>();
            }
            if (isSameOrder) {
                orderNumber = listUploadedData.getPoNumber();
                // Header
                if (oneTimeAllow) {
                    header = new ReturnPOHeaderV2();
                    BeanUtils.copyProperties(listUploadedData, header, CommonUtils.getNullPropertyNames(listUploadedData));
                    header.setCompanyCode(companyCodeId);
                    header.setStoreID(plantId);
                    header.setBranchCode(plantId);
                    header.setLanguageId(languageId);
                    header.setWareHouseId(warehouseId);
                    header.setLoginUserId(loginUserId);
                }
                oneTimeAllow = false;

                // Line
                ReturnPOLineV2 line = new ReturnPOLineV2();
                BeanUtils.copyProperties(listUploadedData, line, CommonUtils.getNullPropertyNames(listUploadedData));
                line.setFromCompanyCode(companyCodeId);
                line.setSourceBranchCode(plantId);
                line.setCountryOfOrigin(listUploadedData.getOrigin());
                lines.add(line);
            }

            if (allRowsList.size() == i) {
                ReturnPOV2 order = new ReturnPOV2();
                order.setReturnPOHeader(header);
                order.setReturnPOLine(lines);
                orderList.add(order);
            }
            i++;
        }
        return orderList;
    }

}