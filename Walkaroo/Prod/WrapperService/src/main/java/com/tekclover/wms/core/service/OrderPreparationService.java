package com.tekclover.wms.core.service;

import com.tekclover.wms.core.exception.BadRequestException;
import com.tekclover.wms.core.model.IKeyValuePair;
import com.tekclover.wms.core.model.auth.AuthToken;
import com.tekclover.wms.core.model.transaction.InhouseTransferHeader;
import com.tekclover.wms.core.model.transaction.InhouseTransferLine;
import com.tekclover.wms.core.model.transaction.InhouseTransferUpload;
import com.tekclover.wms.core.model.transaction.PreOutboundHeader;
import com.tekclover.wms.core.model.transaction.SearchPreOutboundHeader;
import com.tekclover.wms.core.model.transaction.StockAdjustment;
import com.tekclover.wms.core.model.warehouse.inbound.almailem.*;
import com.tekclover.wms.core.model.warehouse.inbound.walkaroo.ReversalLineV3;
import com.tekclover.wms.core.model.warehouse.inbound.walkaroo.ReversalV3;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.*;
import com.tekclover.wms.core.model.warehouse.outbound.walkaroo.DeliveryConfirmationLineV3;
import com.tekclover.wms.core.model.warehouse.outbound.walkaroo.DeliveryConfirmationSAP;
import com.tekclover.wms.core.model.warehouse.outbound.walkaroo.DeliveryConfirmationV3;
import com.tekclover.wms.core.repository.UserRepository;
import com.tekclover.wms.core.util.CommonUtils;
import com.tekclover.wms.core.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderPreparationService {

    private static final String UOM = "EACH";

    @Autowired
    UserRepository userRepository;
    
	@Autowired
	AuthTokenService authTokenService;

    //=======================================================INBOUND===============================================================

    /**
     * @param allRowsList
     * @return
     */
    public List<ASNV2> prepAsnData(List<List<String>> allRowsList) {
        List<ASNV2> orderList = new ArrayList<>();
        boolean oneTimeAllow = true;
        ASNHeaderV2 header = null;
        List<ASNLineV2> lisAsnLine = new ArrayList<>();
        for (List<String> listUploadedData : allRowsList) {
//			Set<ASNHeaderV2> setWHHeader = new HashSet<>();
//			List<ASNLineV2> lisAsnLine = new ArrayList<>();

            // Header
//			ASNHeaderV2 header = null;
//			boolean oneTimeAllow = true;
//			for (String column : listUploadedData) {
            if (oneTimeAllow) {
                header = new ASNHeaderV2();
                /*
                 * branchCode
                 * companyCode
                 * asnNumber
                 */
                header.setBranchCode(listUploadedData.get(0));
                header.setCompanyCode(listUploadedData.get(1));
                header.setAsnNumber(listUploadedData.get(2));
                if (listUploadedData.get(18) != null && !listUploadedData.get(18).isBlank()) {
                    header.setInboundOrderTypeId(Long.valueOf(listUploadedData.get(18)));
                }
//					setWHHeader.add(header);
            }
            oneTimeAllow = false;

            /*
             * lineReference
             * sku
             * skuDescription
             * containerNumber
             * supplierCode
             * supplierPartNumber
             * manufacturerName
             * manufacturerCode
             * expectedDate
             * expectedQty
             * uom
             * origin
             * supplierName
             * Brand
             *packQty
             */
            // Line
            ASNLineV2 line = new ASNLineV2();
            line.setLineReference(Long.valueOf(listUploadedData.get(3)));
            line.setSku(listUploadedData.get(4));
            line.setSkuDescription(listUploadedData.get(5));
            line.setContainerNumber(listUploadedData.get(6));
            line.setSupplierCode(listUploadedData.get(7));
            line.setSupplierPartNumber(listUploadedData.get(8));
            line.setManufacturerName(listUploadedData.get(9));
            line.setManufacturerCode(listUploadedData.get(10));
            line.setExpectedDate(listUploadedData.get(11));
            line.setExpectedQty(Double.valueOf(listUploadedData.get(12)));
            line.setUom(listUploadedData.get(13));
            line.setOrigin(listUploadedData.get(14));
            line.setSupplierName(listUploadedData.get(15));
            line.setBrand(listUploadedData.get(16));

            if (!listUploadedData.get(17).trim().isEmpty()) {
                line.setPackQty(Double.valueOf(listUploadedData.get(17)));
            }
            if (listUploadedData.get(18) != null && !listUploadedData.get(18).isBlank()) {
                line.setInboundOrderTypeId(Long.valueOf(listUploadedData.get(18)));
            }
            if (listUploadedData.get(19) != null && !listUploadedData.get(19).isBlank()) {
                line.setBatchSerialNumber(listUploadedData.get(19));
            }

            lisAsnLine.add(line);
//			}

//			ASNV2 orders = new ASNV2();
//			orders.setAsnHeader(header);
//			orders.setAsnLine(lisAsnLine);
//			orderList.add(orders);
        }
        ASNV2 orders = new ASNV2();
        orders.setAsnHeader(header);
        orders.setAsnLine(lisAsnLine);
        orderList.add(orders);
        return orderList;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param allRowsList
     * @return
     */
    public List<ASNV2> prepAsnData(String companyCodeId, String plantId,
                                   String languageId, String warehouseId, String loginUserId,
                                   List<List<String>> allRowsList) {
        List<ASNV2> orderList = new ArrayList<>();
        boolean oneTimeAllow = true;
        ASNHeaderV2 header = null;
        List<ASNLineV2> lisAsnLine = new ArrayList<>();
        for (List<String> listUploadedData : allRowsList) {
//			Set<ASNHeaderV2> setWHHeader = new HashSet<>();
//			List<ASNLineV2> lisAsnLine = new ArrayList<>();

            // Header
//			ASNHeaderV2 header = null;
//			boolean oneTimeAllow = true;
//			for (String column : listUploadedData) {
            if (oneTimeAllow) {
                header = new ASNHeaderV2();
                /*
                 * branchCode
                 * companyCode
                 * asnNumber
                 */
                header.setBranchCode(plantId);
                header.setCompanyCode(companyCodeId);
                header.setLanguageId(languageId);
                header.setWarehouseId(warehouseId);
                header.setLoginUserId(loginUserId);
                header.setAsnNumber(listUploadedData.get(2));
                if (listUploadedData.get(18) != null && !listUploadedData.get(18).isBlank()) {
                    header.setInboundOrderTypeId(Long.valueOf(listUploadedData.get(18)));
                }
//					setWHHeader.add(header);
            }
            oneTimeAllow = false;

            /*
             * lineReference
             * sku
             * skuDescription
             * containerNumber
             * supplierCode
             * supplierPartNumber
             * manufacturerName
             * manufacturerCode
             * expectedDate
             * expectedQty
             * uom
             * origin
             * supplierName
             * Brand
             *packQty
             */
            // Line
            ASNLineV2 line = new ASNLineV2();
            line.setLineReference(Long.valueOf(listUploadedData.get(3)));
            line.setSku(listUploadedData.get(4));
            line.setSkuDescription(listUploadedData.get(5));
            line.setContainerNumber(listUploadedData.get(6));
            line.setSupplierCode(listUploadedData.get(7));
            line.setSupplierPartNumber(listUploadedData.get(8));
            line.setManufacturerName(listUploadedData.get(9));
            line.setManufacturerCode(listUploadedData.get(10));
            line.setExpectedDate(listUploadedData.get(11));
            line.setExpectedQty(Double.valueOf(listUploadedData.get(12)));
            line.setUom(listUploadedData.get(13));
            line.setOrigin(listUploadedData.get(14));
            line.setSupplierName(listUploadedData.get(15));
            line.setBrand(listUploadedData.get(16));

            if (!listUploadedData.get(17).trim().isEmpty()) {
                line.setPackQty(Double.valueOf(listUploadedData.get(17)));
            }
            if (listUploadedData.get(18) != null && !listUploadedData.get(18).isBlank()) {
                line.setInboundOrderTypeId(Long.valueOf(listUploadedData.get(18)));
            }
            if (listUploadedData.get(19) != null && !listUploadedData.get(19).isBlank()) {
                line.setBatchSerialNumber(listUploadedData.get(19));
            }

            lisAsnLine.add(line);
//			}

//			ASNV2 orders = new ASNV2();
//			orders.setAsnHeader(header);
//			orders.setAsnLine(lisAsnLine);
//			orderList.add(orders);
        }
        ASNV2 orders = new ASNV2();
        orders.setAsnHeader(header);
        orders.setAsnLine(lisAsnLine);
        orderList.add(orders);
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
    public List<ASNV2> prepAsnMultipleDataV3(String companyCodeId, String plantId,
                                             String languageId, String warehouseId, String loginUserId,
                                             List<List<String>> allRowsList) {
        List<ASNV2> orderList = new ArrayList<>();
        String orderNumber = null;
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        int i = 1;
        ASNHeaderV2 header = null;
        List<ASNLineV2> lisAsnLine = new ArrayList<>();
        for (List<String> listUploadedData : allRowsList) {
            if (orderNumber != null) {
                isSameOrder = orderNumber.equalsIgnoreCase(listUploadedData.get(0));
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
                orderNumber = listUploadedData.get(0);
                if (oneTimeAllow) {
                    header = new ASNHeaderV2();

                    header.setBranchCode(plantId);
                    header.setCompanyCode(companyCodeId);
                    header.setLanguageId(languageId);
                    header.setWarehouseId(warehouseId);
                    header.setLoginUserId(loginUserId);
                    header.setAsnNumber(listUploadedData.get(0));
                    if (listUploadedData.size() > 15 && listUploadedData.get(15) != null && !listUploadedData.get(15).isBlank()) {
                        header.setInboundOrderTypeId(Long.valueOf(listUploadedData.get(15)));
                    } else {
                        header.setInboundOrderTypeId(1L);
                    }
                }
                oneTimeAllow = false;

                // Line
                ASNLineV2 line = new ASNLineV2();
                line.setLineReference(Long.valueOf(listUploadedData.get(1)));
                line.setSku(listUploadedData.get(2));
                line.setBarcodeId(listUploadedData.get(3));
                line.setMaterialNo(listUploadedData.get(4));
                line.setPriceSegment(listUploadedData.get(5));
                line.setArticleNo(listUploadedData.get(6));
                line.setGender(listUploadedData.get(7));
                line.setColor(listUploadedData.get(8));
                line.setSize(listUploadedData.get(9));
                line.setNoPairs(listUploadedData.get(10));
                line.setSupplierCode(listUploadedData.get(11));
                line.setExpectedDate(listUploadedData.get(12));
                line.setExpectedQty(Double.valueOf(listUploadedData.get(13)));

                line.setBranchCode(plantId);
                line.setCompanyCode(companyCodeId);

                if (listUploadedData.size() > 14 && !listUploadedData.get(14).trim().isEmpty()) {
                    line.setUom(listUploadedData.get(14));
                } else {
                    line.setUom(UOM);
                }
                if (listUploadedData.size() > 15 && listUploadedData.get(15) != null && !listUploadedData.get(15).isBlank()) {
                    line.setInboundOrderTypeId(Long.valueOf(listUploadedData.get(15)));
                } else {
                    header.setInboundOrderTypeId(1L);
                }

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
     * @param allRowsList
     * @return
     */
    public List<InhouseTransferUpload> prepInHouseTransferHeaderV2(List<List<String>> allRowsList) {
        List<InhouseTransferUpload> orderList = new ArrayList<>();
        for (List<String> listUploadedData : allRowsList) {
            Set<InhouseTransferHeader> setInhouseTransferHeader = new HashSet<>();
            List<InhouseTransferLine> listInhouseTransferLine = new ArrayList<>();

            // Header
            InhouseTransferHeader header = null;
            boolean oneTimeAllow = true;

            if (oneTimeAllow) {
                header = new InhouseTransferHeader();
                /*
                 * companyCodeId
                 * plantId
                 * languageId
                 * warehouseId
                 * TransferTypeId
                 */
                header.setCompanyCodeId(listUploadedData.get(0));
                header.setPlantId(listUploadedData.get(1));
                header.setLanguageId(listUploadedData.get(2));
                header.setWarehouseId(listUploadedData.get(3));
                header.setTransferMethod("ONESTEP");
                if (listUploadedData.get(4) != null) {
                    header.setTransferTypeId(Long.valueOf(listUploadedData.get(4)));
                } else {
                    header.setTransferTypeId(3L);
                }

                setInhouseTransferHeader.add(header);
            }
            oneTimeAllow = false;

            /*
             * itemCode
             * manufacturerName
             * sourceStorageBin
             * targetStorageBin
             * transferOrderQty
             * transferConfirmQty
             * transferUOM
             * stockTypeId
             * specialStockIndicatorId
             * palletcode
             * casecode
             * packbarcode
             */
            // Line
            InhouseTransferLine line = new InhouseTransferLine();
            line.setCompanyCodeId(listUploadedData.get(0));
            line.setPlantId(listUploadedData.get(1));
            line.setLanguageId(listUploadedData.get(2));
            line.setWarehouseId(listUploadedData.get(3));
            line.setSourceItemCode(listUploadedData.get(5));
            line.setTargetItemCode(listUploadedData.get(5));
            line.setManufacturerName(listUploadedData.get(6));
            if (listUploadedData.get(7).equalsIgnoreCase(listUploadedData.get(8))) {
                throw new BadRequestException("Source and Target Storage Bin cannot be same");
            }
            line.setSourceStorageBin(listUploadedData.get(7));
            line.setTargetStorageBin(listUploadedData.get(8));
            if (listUploadedData.get(9) == null) {
                throw new BadRequestException("Transfer Qty must not be null");
            }
            if (Double.valueOf(listUploadedData.get(9)) <= 0D) {
                throw new BadRequestException("Transfer Qty must be greater than zero");
            }
            if (!listUploadedData.get(9).trim().isEmpty()) {
                line.setTransferOrderQty(Double.valueOf(listUploadedData.get(9)));
                line.setTransferConfirmedQty(Double.valueOf(listUploadedData.get(9)));
            }
            line.setTransferUom(listUploadedData.get(10));
            line.setSourceStockTypeId(Long.valueOf(listUploadedData.get(11)));
            line.setTargetStockTypeId(Long.valueOf(listUploadedData.get(11)));
            line.setSpecialStockIndicatorId(Long.valueOf(listUploadedData.get(12)));
            line.setPalletCode(listUploadedData.get(13));
            line.setCaseCode(listUploadedData.get(14));
            line.setPackBarcodes(listUploadedData.get(15));

            listInhouseTransferLine.add(line);

            InhouseTransferUpload inhouseTransferUpload = new InhouseTransferUpload();
            inhouseTransferUpload.setInhouseTransferHeader(header);
            inhouseTransferUpload.setInhouseTransferLine(listInhouseTransferLine);
            orderList.add(inhouseTransferUpload);
        }
        return orderList;
    }

    /**
     * @param allRowsList
     * @return
     */
    public List<StockAdjustment> prepStockAdjustment(List<List<String>> allRowsList) {
        List<StockAdjustment> orderList = new ArrayList<>();
        for (List<String> listUploadedData : allRowsList) {

            /*
             * companyCodeId
             * plantId
             * warehouseId
             * date of adjustment
             * is cycle count
             * is damage
             * itemCode
             * itemDescription
             * manufacturerName
             * ManufacturerCode
             * UOM
             * adjustmentQty
             */
            StockAdjustment header = new StockAdjustment();
            header.setCompanyCode(listUploadedData.get(0));
            header.setBranchCode(listUploadedData.get(1));
            header.setWarehouseId(listUploadedData.get(2));
            header.setDateOfAdjustment(new Date());
            header.setIsCycleCount(listUploadedData.get(3));
            header.setIsDamage(listUploadedData.get(4));
            header.setItemCode(listUploadedData.get(5));
            header.setItemDescription(listUploadedData.get(6));
            header.setManufacturerName(listUploadedData.get(7));
            header.setManufacturerCode(listUploadedData.get(8));
            header.setUnitOfMeasure(listUploadedData.get(9));
            if (listUploadedData.get(10) != null) {
                header.setAdjustmentQty(Double.valueOf(listUploadedData.get(10)));
            }
            orderList.add(header);
        }
        return orderList;
    }

    /**
     * @param allRowsList
     * @return
     */
    public List<InterWarehouseTransferInV2> prepInterwareHouseInData(List<List<String>> allRowsList) {
        List<InterWarehouseTransferInV2> whOrderList = new ArrayList<>();
        for (List<String> listUploadedData : allRowsList) {
            Set<InterWarehouseTransferInHeaderV2> setWHHeader = new HashSet<>();
            List<InterWarehouseTransferInLineV2> listWHLines = new ArrayList<>();

            // Header
            InterWarehouseTransferInHeaderV2 header = null;
            boolean oneTimeAllow = true;
            for (String column : listUploadedData) {
                if (oneTimeAllow) {
                    header = new InterWarehouseTransferInHeaderV2();
                    /*
                     * transferOrderNumber
                     * toCompanyCode
                     * toBranchCode
                     */
                    header.setTransferOrderNumber(listUploadedData.get(0));
                    header.setToCompanyCode(listUploadedData.get(1));
                    header.setToBranchCode(listUploadedData.get(2));
                    setWHHeader.add(header);
                }
                oneTimeAllow = false;

                /*
                 * fromCompanyCode
                 * origin
                 * supplierName
                 * manufacturerCode
                 * Brand
                 * fromBranchCode
                 * lineReference
                 * sku
                 * skuDescription
                 * supplierPartNumber
                 * manufacturerName
                 * expectedDate
                 * expectedQty
                 * uom
                 * packQty
                 */
                // Line
                InterWarehouseTransferInLineV2 line = new InterWarehouseTransferInLineV2();
                line.setFromCompanyCode(listUploadedData.get(3));
                line.setOrigin(listUploadedData.get(4));
                line.setSupplierName(listUploadedData.get(5));
                line.setManufacturerCode(listUploadedData.get(6));
                line.setBrand(listUploadedData.get(7));
                line.setFromBranchCode(listUploadedData.get(8));
                line.setLineReference(Long.valueOf(listUploadedData.get(9)));
                line.setSku(listUploadedData.get(10));
                line.setSkuDescription(listUploadedData.get(11));
                line.setSupplierPartNumber(listUploadedData.get(12));
                line.setManufacturerName(listUploadedData.get(13));
                line.setExpectedDate(listUploadedData.get(14));
                line.setExpectedQty(Double.valueOf(listUploadedData.get(15)));
                line.setUom(listUploadedData.get(16));

                if (!listUploadedData.get(17).trim().isEmpty()) {
                    line.setPackQty(Double.valueOf(listUploadedData.get(17)));
                }

                listWHLines.add(line);
            }

            InterWarehouseTransferInV2 whOrder = new InterWarehouseTransferInV2();
            whOrder.setInterWarehouseTransferInHeader(header);
            whOrder.setInterWarehouseTransferInLine(listWHLines);
            whOrderList.add(whOrder);
        }
        return whOrderList;
    }

    /**
     * 0 - requiredDeliveryDate
     * 1 - storeID
     * 2 - storeName
     * 3 - transferOrderNumber
     * 4 - wareHouseId
     * 5 - lineReference
     * 6 - orderType
     * 7 - orderedQty
     * 8 - sku
     * 9 - skuDescription
     * 10 - uom
     *
     * @param allRowsList
     * @return
     */
    List<ShipmentOrderV2> prepSOData(List<List<String>> allRowsList) {
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
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param allRowsList
     * @return
     */
    List<SalesOrderV2> prepSalesOrderData(String companyCodeId, String plantId, String languageId,
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
    List<SalesOrderV2> prepSalesOrderDataV3(String companyCodeId, String plantId, String languageId,
                                            String warehouseId, String loginUserId, List<List<String>> allRowsList) {
        List<SalesOrderV2> salesOrderList = new ArrayList<>();
        SalesOrderHeaderV2 soHeader = null;
        List<SalesOrderLineV2> soLines = new ArrayList<>();
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        String orderNumber = null;
        int i = 1;
        String orderGroupByUpload = String.valueOf(System.currentTimeMillis());
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
                    soHeader.setCustomerId(listUploadedData.get(2));
                    soHeader.setCustomerName(listUploadedData.get(3));
                    soHeader.setRequiredDeliveryDate(listUploadedData.get(12));
                    if (listUploadedData.size() > 15 && listUploadedData.get(15) != null && !listUploadedData.get(15).isBlank()) {
                        soHeader.setOrderType(listUploadedData.get(15));
                    } else {
                        soHeader.setOrderType("3");
                    }
                    soHeader.setSalesOrderNumber(orderGroupByUpload);
                }
                oneTimeAllow = false;

                // Line
                SalesOrderLineV2 soLine = new SalesOrderLineV2();
                if (listUploadedData.size() > 15 && listUploadedData.get(15) != null && !listUploadedData.get(15).isBlank()) {
                    soLine.setOrderType(listUploadedData.get(9));
                } else {
                    soLine.setOrderType("3");
                }
                soLine.setLineReference(Long.valueOf(listUploadedData.get(1)));
                soLine.setOrderedQty(Double.valueOf(listUploadedData.get(13)));
                soLine.setSku(listUploadedData.get(4));
                if (listUploadedData.size() > 14 && listUploadedData.get(14) != null && !listUploadedData.get(14).isBlank()) {
                    soLine.setUom(listUploadedData.get(14));
                } else {
                    soLine.setUom(UOM);
                }
                soLine.setPickListNo(listUploadedData.get(0));
                soLine.setSalesOrderNo(orderGroupByUpload);
                soLine.setMaterialNo(listUploadedData.get(5));
                soLine.setPriceSegment(listUploadedData.get(6));
                soLine.setArticleNo(listUploadedData.get(7));
                soLine.setGender(listUploadedData.get(8));
                soLine.setColor(listUploadedData.get(9));
                soLine.setSize(listUploadedData.get(10));
                soLine.setNoPairs(listUploadedData.get(11));
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

    //==============================================================================================================================

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
                    header.setIsSapOrder(false); // NOT FROM SAP, it is called via Upload         
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
                    header.setTransferOrderNumber(listUploadedData.getAsnNumber());
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

    //=======================================================OUTBOUND===============================================================

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param list
     * @return
     */
    public List<SalesOrderV2> prepSalesOrderDataV4(String companyCodeId, String plantId, String languageId,
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

    /**
     * --------------------------Walkaroo changes----------------------------------------------------------------------
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
     * 
     * @param deliveryConfirmationSAPList
     * @return
     */
//	public DeliveryConfirmationV3 prepDeliveryConfirmationV3(List<DeliveryConfirmationSAP> deliveryConfirmationSAPList) {
//        log.info("deliveryConfirmationSAPList -----> {}", deliveryConfirmationSAPList);
//		DeliveryConfirmationV3 deliveryConfirmationV3 = new DeliveryConfirmationV3();
//		List<DeliveryConfirmationLineV3> deliveryLines = new ArrayList<>();
//		String languageId = null;
//		String companyCode = null;
//		String plantId = null;
//		String warehouseId = null;
//		
//		AuthToken transServiceAuthToken = authTokenService.getTransactionServiceAuthToken();
//		for (DeliveryConfirmationSAP sapData : deliveryConfirmationSAPList) {
//			SearchPreOutboundHeader searchPreOutboundHeader = new SearchPreOutboundHeader();
//			ArrayList<String> searchList = new ArrayList<String>();
//			searchList.add(sapData.getSalesOrderNumber());
//			searchPreOutboundHeader.setSoNumber(searchList);
//			
//			try {
//				PreOutboundHeader[] preOutboundHeaderArr = 
//						transactionService.findPreOutboundHeaderNew(searchPreOutboundHeader, transServiceAuthToken.getAccess_token());
//				if (preOutboundHeaderArr != null) {
//					companyCode = preOutboundHeaderArr[0].getCompanyCodeId();
//					warehouseId = preOutboundHeaderArr[0].getWarehouseId();
//					languageId = preOutboundHeaderArr[0].getLanguageId();
//					log.info("companyCode -> {}, warehouseId -> {}, languageId ->{} ", companyCode, warehouseId, languageId);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//			plantId = sapData.getBranchCode();
//			
//			DeliveryConfirmationLineV3 line = new DeliveryConfirmationLineV3();
//			line.setOutbound(sapData.getSalesOrderNumber());
//			line.setHuSerialNo(sapData.getBarcodeId());
//			line.setMaterial(sapData.getMaterialNo());
//			line.setPriceSegement(sapData.getPriceSegement());
//			line.setPlant(sapData.getBranchCode());
//			line.setSkuCode(sapData.getSku());
//			line.setPickedQty(1D);
//			deliveryLines.add(line);
//		}
//		deliveryConfirmationV3.setCompanyCodeId(companyCode);
//		deliveryConfirmationV3.setPlantId(plantId);
//		deliveryConfirmationV3.setLanguageId(languageId);
//		deliveryConfirmationV3.setWarehouseId(warehouseId);
//		deliveryConfirmationV3.setLines(deliveryLines);
//		return deliveryConfirmationV3;
//	}

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

    /**
     *
     * @param list
     * @return
     */

    public List<ASNV2> prepAsnMultipleData(List<InboundOrderProcessV4> list) {
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

                    header.setBranchCode(listUploadedData.getBranchCode());
                    header.setCompanyCode(listUploadedData.getCompanyCode());
                    header.setLanguageId(listUploadedData.getLanguageId());
                    header.setWarehouseId(listUploadedData.getWarehouseId());
                    header.setLoginUserId(listUploadedData.getLoginUserId());
                    header.setAsnNumber(listUploadedData.getAsnNumber());
                    if (listUploadedData.getReversalFlag() != null) {
                        if (listUploadedData.getReversalFlag().equalsIgnoreCase("X") || listUploadedData.getReversalFlag().equalsIgnoreCase("x")) {
                            header.setInboundOrderTypeId(10L);
                        } else {
                            header.setInboundOrderTypeId(listUploadedData.getInboundOrderTypeId());
                        }
                    }
                    header.setReversalFlag(listUploadedData.getReversalFlag());
                    /*
                     * SAP Order
                     * -------InboundUpload Vs SAP API -------Differentiator
                     */
                	header.setIsSapOrder(true);
                }
                oneTimeAllow = false;

                // Line
                ASNLineV2 line = new ASNLineV2();
                BeanUtils.copyProperties(listUploadedData, line, CommonUtils.getNullPropertyNames(listUploadedData));

                line.setBranchCode(listUploadedData.getBranchCode());
                line.setCompanyCode(listUploadedData.getCompanyCode());
                line.setLineReference(lineReference);
                line.setNoPairs(listUploadedData.getNoPairs());
                line.setExpectedDate(DateUtils.date2String_YYYYMMDD(new Date()));
                if (listUploadedData.getReversalFlag() != null) {
                    if (listUploadedData.getReversalFlag().equalsIgnoreCase("X") || listUploadedData.getReversalFlag().equalsIgnoreCase("x")) {
                        line.setInboundOrderTypeId(10L);
                    }
                }
                line.setReversalFlag(listUploadedData.getReversalFlag());
                line.setMtoNumber(listUploadedData.getMtoNumber());
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
     * 
     * @param list
     * @return
     */
    public List<SaleOrderReturnV2> prepSoReturnMultipleData(List<InboundOrderProcessV4> list) {
        List<InboundOrderProcessV4> allRowsList = list.stream().sorted(Comparator.comparing(InboundOrderProcessV4::getAsnNumber)).collect(Collectors.toList());
        List<SaleOrderReturnV2> orderList = new ArrayList<>();
        String orderNumber = null;
        boolean oneTimeAllow = true;
        boolean isSameOrder = true;
        int i = 1;
        long lineReference = 1;
        SOReturnHeaderV2 header = null;
        List<SOReturnLineV2> lisAsnLine = new ArrayList<>();
        for (InboundOrderProcessV4 listUploadedData : allRowsList) {
            if (orderNumber != null) {
                isSameOrder = orderNumber.equalsIgnoreCase(listUploadedData.getAsnNumber());
            }

            if (!isSameOrder) {
                SaleOrderReturnV2 orders = new SaleOrderReturnV2();
                orders.setSoReturnHeader(header);
                orders.setSoReturnLine(lisAsnLine);
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
                    header = new SOReturnHeaderV2();

                    header.setBranchCode(listUploadedData.getBranchCode());
                    header.setCompanyCode(listUploadedData.getCompanyCode());
                    header.setLanguageId(listUploadedData.getLanguageId());
                    header.setWarehouseId(listUploadedData.getWarehouseId());
                    header.setLoginUserId(listUploadedData.getLoginUserId());
                    header.setAsnNumber(listUploadedData.getAsnNumber());
                    header.setTransferOrderNumber(listUploadedData.getAsnNumber());
                    header.setInboundOrderTypeId(listUploadedData.getInboundOrderTypeId());
                }
                oneTimeAllow = false;

                // Line
                SOReturnLineV2 line = new SOReturnLineV2();
                BeanUtils.copyProperties(listUploadedData, line, CommonUtils.getNullPropertyNames(listUploadedData));

                line.setBranchCode(listUploadedData.getBranchCode());
                line.setCompanyCode(listUploadedData.getCompanyCode());
                line.setSalesOrderReference(String.valueOf(lineReference));
                line.setLineReference(lineReference);
                line.setArticleNo(listUploadedData.getArticleNo());
                line.setGender(listUploadedData.getGender());
                line.setBarcodeId(listUploadedData.getBarcodeId());
                line.setSku(listUploadedData.getSku());
                line.setSkuDescription(listUploadedData.getSkuDescription());
                line.setColor(listUploadedData.getColor());
                line.setSize(listUploadedData.getSize());
                line.setNoPairs(listUploadedData.getNoPairs());
                line.setNoPairs(listUploadedData.getNoPairs());
                line.setExpectedQty(listUploadedData.getExpectedQty());
                line.setPackQty(listUploadedData.getExpectedQty());
                line.setExpectedDate(DateUtils.date2String_YYYYMMDD(new Date()));
                line.setReversalFlag(listUploadedData.getReversalFlag());
                lineReference++;
                lisAsnLine.add(line);
            }
            if (allRowsList.size() == i) {
                SaleOrderReturnV2 orders = new SaleOrderReturnV2();
                orders.setSoReturnHeader(header);
                orders.setSoReturnLine(lisAsnLine);
                orderList.add(orders);
            }
            i++;
        }
        return orderList;
    }
    
    /**
     * 
     * @param list
     * @return
     */
    public List<SalesOrderV2> prepSalesOrderV2 (List<OutboundOrderProcessV4> list) {
    	List<OutboundOrderProcessV4> allRowsList = 
    			list.stream().sorted(Comparator.comparing(OutboundOrderProcessV4::getPickListNumber)).collect(Collectors.toList());
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
                    soHeader.setSalesOrderNumber(pickListNumber);
                    soHeader.setCompanyCode(listUploadedData.getCompanyCode());
                    soHeader.setBranchCode(listUploadedData.getBranchCode());
                    soHeader.setLanguageId(listUploadedData.getLanguageId());
                    soHeader.setWarehouseId(listUploadedData.getWarehouseId());
                    soHeader.setPickListNumber(pickListNumber);
                    soHeader.setCustomerId(listUploadedData.getCustomerCode());
                    soHeader.setCustomerName(listUploadedData.getCustomer());
                    soHeader.setRequiredDeliveryDate(DateUtils.getCurrentDateTime());
                    soHeader.setOrderType(listUploadedData.getOrderType());
                    soHeader.setTokenNumber(listUploadedData.getTokenNumber());
                    soHeader.setSalesOrderNumber(salesOrderNo);
                    soHeader.setRequiredDeliveryDate(DateUtils.getCurrentDateTime());
                }
                oneTimeAllow = false;

                // Line
                SalesOrderLineV2 soLine = new SalesOrderLineV2();
                BeanUtils.copyProperties(listUploadedData, soLine, CommonUtils.getNullPropertyNames(listUploadedData));
                soLine.setPickListNo(pickListNumber);
                soLine.setSalesOrderNo(salesOrderNo);
                soLine.setSku(listUploadedData.getSkuCode());
                soLine.setLineReference(listUploadedData.getItm());
                soLine.setMtoNumber(listUploadedData.getMtoNumber());
                soLine.setSpecialStock(listUploadedData.getSpecialStock());
                soLine.setShipToCode(listUploadedData.getShipToCode());
                soLine.setShipToParty(listUploadedData.getShipToParty());
                soLine.setOrderedQty(listUploadedData.getOrderedQty());
                soLine.setLineReference(listUploadedData.getLineReference());
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
        log.info("--------salesOrderNo--------> : " + salesOrderNo);;
        return salesOrderList;
    }
}