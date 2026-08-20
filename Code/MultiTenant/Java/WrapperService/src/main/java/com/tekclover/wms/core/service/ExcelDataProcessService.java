package com.tekclover.wms.core.service;


import com.tekclover.wms.core.model.masters.BusinessPartnerV2;
import com.tekclover.wms.core.model.masters.ImAlternateUom;
import com.tekclover.wms.core.model.transaction.InventoryV2;
import com.tekclover.wms.core.model.warehouse.inbound.almailem.InboundOrderProcessV4;
import com.tekclover.wms.core.model.warehouse.mastersorder.ImBasicData1V2;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.OutboundOrderProcessV4;
import com.tekclover.wms.core.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

import static org.apache.http.client.utils.DateUtils.parseDate;

@Service
@Slf4j
public class ExcelDataProcessService {


    public List<InboundOrderProcessV4> inboundReadExcelFile(String companyCodeId, String plantId, String languageId, String warehouseId, Long orderTypeId, String loginUserId, MultipartFile file) throws IOException {
        List<InboundOrderProcessV4> inboundOrderList = new ArrayList<>();
        // Create Workbook for Excel file
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        //Get the header row first row
        Row headerRow = sheet.getRow(0);
        // Map column names their corresponding index
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            columnIndexMap.put(cell.getStringCellValue().toLowerCase().trim(), cell.getColumnIndex());
        }

        // Iterate through rows (skip the header row)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                InboundOrderProcessV4 inboundOrderProcess = new InboundOrderProcessV4();

                // Set the fields dynamically based on column name
                setFieldByColumnName(inboundOrderProcess, row, columnIndexMap);
                inboundOrderProcess.setCompanyCode(companyCodeId);
                inboundOrderProcess.setToCompanyCode(companyCodeId);
                inboundOrderProcess.setBranchCode(plantId);
                inboundOrderProcess.setToBranchCode(plantId);
                inboundOrderProcess.setLanguageId(languageId);
                inboundOrderProcess.setWarehouseId(warehouseId);
                inboundOrderProcess.setInboundOrderTypeId(orderTypeId);
                inboundOrderProcess.setLoginUserId(loginUserId);

                // Add the mapped delivery object to the list
                inboundOrderList.add(inboundOrderProcess);
            }
        }

        // Close the workbook to free resource
        workbook.close();
        return inboundOrderList;
    }
    private String getCellValueAsString(Cell cell) {
        // Check if the cell is of numeric type and not a date
        if (cell.getCellType() == CellType.NUMERIC && !DateUtil.isCellDateFormatted(cell)) {
            // Format as a whole number to avoid scientific notation
            return new BigDecimal(cell.getNumericCellValue()).toPlainString();
        } else {
            // Otherwise, use the default string representation
            cell.setCellType(CellType.STRING);
            return cell.getStringCellValue().trim();
        }
    }

    private Double getCellValueAsDouble(Cell cell) {
        return cell != null && cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : 0.0;
    }


    /**
     *
     * @param inboundOrderProcess
     * @param row
     * @param columnIntexMap
     */
    public void setFieldByColumnName(InboundOrderProcessV4 inboundOrderProcess, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "inbound":
                    case "asnnumber":
                        invokeSetter(inboundOrderProcess, "setAsnNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "huserialnumber":
                        invokeSetter(inboundOrderProcess, "setBarcodeId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "material":
                        invokeSetter(inboundOrderProcess, "setMaterialNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "pricesegment":
                        invokeSetter(inboundOrderProcess, "setPriceSegment", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "plant":
                        invokeSetter(inboundOrderProcess, "setPlant", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storagelocation":
                        invokeSetter(inboundOrderProcess, "setStorageLocation", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "skucode":
                    case "sku":
                        invokeSetter(inboundOrderProcess, "setSku", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "articlenumber":
                        invokeSetter(inboundOrderProcess, "setArticleNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "noofpairs":
                        invokeSetter(inboundOrderProcess, "setNoPairs", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "gender":
                        invokeSetter(inboundOrderProcess, "setGender", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "color":
                        invokeSetter(inboundOrderProcess, "setColor", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "size":
                        invokeSetter(inboundOrderProcess, "setSize", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "qty":
                    case "expectedqty":
                        invokeSetter(inboundOrderProcess, "setExpectedQty", getCellValueAsDouble(cell));
                        break;
                    case "returnordernumber":
                        invokeSetter(inboundOrderProcess, "setTransferOrderNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "refordernumber":
                        invokeSetter(inboundOrderProcess, "setAsnNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "purchaseordernumber":
                        invokeSetter(inboundOrderProcess, "setPurchaseOrderNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "suppliercode":
                        invokeSetter(inboundOrderProcess, "setSupplierCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "transferordernumber":
                        invokeSetter(inboundOrderProcess, "setTransferOrderNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "sourcecompanycode":
                        invokeSetter(inboundOrderProcess, "setSourceCompanyCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "sourcebranchcode":
                        invokeSetter(inboundOrderProcess, "setSourceBranchCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "transferorderdate":
                        invokeSetter(inboundOrderProcess, "setTransferOrderDate", getCellValueAsDate(cell));
                        break;
                    case "linereference":
                        invokeSetter(inboundOrderProcess, "setLineReference", getCellValueAsLong(cell));
                        break;
                    case "skudescription":
                        invokeSetter(inboundOrderProcess, "setSkuDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "uom":
                        invokeSetter(inboundOrderProcess, "setUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturername":
                        invokeSetter(inboundOrderProcess, "setManufacturerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturercode":
                        invokeSetter(inboundOrderProcess, "setManufacturerCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturerfullname":
                        invokeSetter(inboundOrderProcess, "setManufacturerFullName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "containernumber":
                        invokeSetter(inboundOrderProcess, "setContainerNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "supplierpartnumber":
                        invokeSetter(inboundOrderProcess, "setSupplierPartNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "expecteddate":
                        invokeSetter(inboundOrderProcess, "setExpectedDate", getCellValueAsDate(cell));
                        break;
                    case "date":
                        invokeSetter(inboundOrderProcess, "setExpectedDate", getCellValueAsDate(cell));
                        break;
                    case "receiveddate":
                        invokeSetter(inboundOrderProcess, "setReceivedDate", getCellValueAsDate(cell));
                        break;
                    case "receivedqty":
                        invokeSetter(inboundOrderProcess, "setReceivedQty", getCellValueAsDouble(cell));
                        break;
                    case "packqty":
                        invokeSetter(inboundOrderProcess, "setPackQty", getCellValueAsDouble(cell));
                        break;
                    case "receivedby":
                        invokeSetter(inboundOrderProcess, "setReceivedBy", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "origin":
                        invokeSetter(inboundOrderProcess, "setOrigin", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "brand":
                        invokeSetter(inboundOrderProcess, "setBrand", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "suppliername":
                        invokeSetter(inboundOrderProcess, "setSupplierName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "supplierinvoiceno":
                        invokeSetter(inboundOrderProcess, "setSupplierInvoiceNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "batchserialnumber":
                        invokeSetter(inboundOrderProcess, "setBatchSerialNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "barcodeid":
                        invokeSetter(inboundOrderProcess, "setBarcodeId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemtype":
                        invokeSetter(inboundOrderProcess, "setItemType", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemgroup":
                        invokeSetter(inboundOrderProcess, "setItemGroup", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "invoicenumber":
                        invokeSetter(inboundOrderProcess, "setInvoiceNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storeid":
                        invokeSetter(inboundOrderProcess, "setStoreId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "salesorderreference":
                        invokeSetter(inboundOrderProcess, "setSalesOrderReference", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "alternateuom":
                        invokeSetter(inboundOrderProcess, "setAlternateUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "nobags":
                        invokeSetter(inboundOrderProcess, "setNoBags", getCellValueAsDouble(cell));
                        break;
                    case "bagsize":
                        invokeSetter(inboundOrderProcess, "setBagSize", getCellValueAsDouble(cell));
                        break;
                    case "mrp":
                        invokeSetter(inboundOrderProcess, "setMrp", getCellValueAsDouble(cell));
                        break;
                    case "vehicleno":
                        invokeSetter(inboundOrderProcess, "setVehicleNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "skutext":
                        invokeSetter(inboundOrderProcess, "setSkuDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "vehiclereportingdate":
                        invokeSetter(inboundOrderProcess, "setVehicleReportingDate", getCellValueAsDate1(cell));
                        break;
                    case "vehicleunloadingdate":
                        invokeSetter(inboundOrderProcess, "setVehicleUnloadingDate", getCellValueAsDate1(cell));
                        break;
                    case "salesOrderNumber":
                        invokeSetter(inboundOrderProcess, "setSalesOrderNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "customerid":
                        invokeSetter(inboundOrderProcess, "setCustomerId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "customername":
                        invokeSetter(inboundOrderProcess, "setCustomerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                }
            }
        } catch (Exception e) {
            log.info("inboundOrderProcess Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }

    // Helper method to invoke the setter method using reflection
    private void invokeSetter(Object obj, String methodName, Object value) {
        try {
            if (value != null) {
                Method method = obj.getClass().getMethod(methodName, value.getClass());
                method.invoke(obj, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCellValueAsDate(Cell cell) {
        return cell != null ? getCellValueDateAsString(cell) : null;
    }

    private String getCellValueDateAsString(Cell cell) {
        if (cell.getCellType() == CellType.STRING) {
            return isValidDate(cell.getStringCellValue()) ? cell.getStringCellValue().trim() : null;
        } else {
            cell.setCellType(CellType.NUMERIC);
            return DateUtils.date2String_YYYYMMDD(cell.getDateCellValue());
        }
    }

    private Date getCellValueAsDate1(Cell cell) {
        if (cell == null) return null;

        if (cell.getCellType() == CellType.STRING) {
            String value = cell.getStringCellValue().trim();
            if (isValidDate(value)) {
                // Assuming isValidDate also works with date parsing
                return parseDate(value); // <-- You need to implement this
            }
            return null;
        } else {
            cell.setCellType(CellType.NUMERIC);
            return cell.getDateCellValue();
        }
    }

    private Long getCellValueAsLong(Cell cell) {
        return cell != null && cell.getCellType() == CellType.NUMERIC ? (long) cell.getNumericCellValue() : null ;
    }

    public static boolean isValidDate(String date) {
        // Define the regex pattern
        String regex = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        // Match the input date against the pattern
        return pattern.matcher(date).matches();
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param orderTypeId
     * @param loginUserId
     * @param file
     * @return
     * @throws IOException
     */
    public List<OutboundOrderProcessV4> outboundReadExcelFileV5(String companyCodeId, String plantId, String languageId,
                                                              String warehouseId, Long orderTypeId, String loginUserId, MultipartFile file) throws IOException {

        List<OutboundOrderProcessV4> outboundOrderList = new ArrayList<>();
        // Create Workbook for Excel file
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        //Get the header row first row
        Row headerRow = sheet.getRow(0);
        // Map column names their corresponding index
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            columnIndexMap.put(cell.getStringCellValue().toLowerCase().trim(), cell.getColumnIndex());
        }

        // Iterate through rows (skip the header row)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                OutboundOrderProcessV4 outboundOrderProcess = new OutboundOrderProcessV4();

                // Set the fields dynamically based on column name
                setOutboundFieldByColumnName(outboundOrderProcess, row, columnIndexMap);
                outboundOrderProcess.setCompanyCode(companyCodeId);
                outboundOrderProcess.setToCompanyCode(companyCodeId);
                outboundOrderProcess.setBranchCode(plantId);
                outboundOrderProcess.setToBranchCode(plantId);
                outboundOrderProcess.setLanguageId(languageId);
                outboundOrderProcess.setWarehouseId(warehouseId);
                outboundOrderProcess.setOrderType(String.valueOf(orderTypeId));
                outboundOrderProcess.setLoginUserId(loginUserId);

                // Add the mapped delivery object to the list
                outboundOrderList.add(outboundOrderProcess);
            }
        }

        // Close the workbook to free resource
        workbook.close();
        return outboundOrderList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param orderTypeId
     * @param loginUserId
     * @param file
     * @return
     * @throws IOException
     */
    public List<OutboundOrderProcessV4> outboundReadExcelFileV7(String companyCodeId, String plantId, String languageId,
                                                                String warehouseId, Long orderTypeId, String loginUserId, MultipartFile file) throws IOException {

        List<OutboundOrderProcessV4> outboundOrderList = new ArrayList<>();
        // Create Workbook for Excel file
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        //Get the header row first row
        Row headerRow = sheet.getRow(0);
        // Map column names their corresponding index
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            columnIndexMap.put(cell.getStringCellValue().toLowerCase().trim(), cell.getColumnIndex());
        }

        // Iterate through rows (skip the header row)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                OutboundOrderProcessV4 outboundOrderProcess = new OutboundOrderProcessV4();

                // Set the fields dynamically based on column name
                setOutboundKnowellFieldByColumnName(outboundOrderProcess, row, columnIndexMap);
                outboundOrderProcess.setCompanyCode(companyCodeId);
                outboundOrderProcess.setToCompanyCode(companyCodeId);
                outboundOrderProcess.setBranchCode(plantId);
                outboundOrderProcess.setToBranchCode(plantId);
                outboundOrderProcess.setLanguageId(languageId);
                outboundOrderProcess.setWarehouseId(warehouseId);
                outboundOrderProcess.setOrderType(String.valueOf(orderTypeId));
                outboundOrderProcess.setLoginUserId(loginUserId);

                // Add the mapped delivery object to the list
                outboundOrderList.add(outboundOrderProcess);
            }
        }

        // Close the workbook to free resource
        workbook.close();
        return outboundOrderList;
    }

    /**
     *
     * @param outboundOrderProcess
     * @param row
     * @param columnIntexMap
     */
    public void setOutboundKnowellFieldByColumnName(OutboundOrderProcessV4 outboundOrderProcess, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "outbound":
                        invokeSetter(outboundOrderProcess, "setPickListNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itm":
                        invokeSetter(outboundOrderProcess, "setItm", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "customercode":
                        invokeSetter(outboundOrderProcess, "setCustomerId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "customer":
                        invokeSetter(outboundOrderProcess, "setCustomerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "sourcecompanycode":
                        invokeSetter(outboundOrderProcess, "setSourceCompanyCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "fromcompanycode":
                        invokeSetter(outboundOrderProcess, "setFromCompanyCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "sourcebranchcode":
                        invokeSetter(outboundOrderProcess, "setSourceBranchCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "frombranchcode":
                        invokeSetter(outboundOrderProcess, "setFromBranchCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "tocompanycode":
                        invokeSetter(outboundOrderProcess, "setToCompanyCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "tobranchcode":
                        invokeSetter(outboundOrderProcess, "setToBranchCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "shiptocode":
                        invokeSetter(outboundOrderProcess, "setShipToCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "shiptoparty":
                        invokeSetter(outboundOrderProcess, "setShipToParty", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "material":
                        invokeSetter(outboundOrderProcess, "setMaterialNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "pricesegment":
                        invokeSetter(outboundOrderProcess, "setPriceSegment", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "qty":
                        invokeSetter(outboundOrderProcess, "setOrderedQty", getCellValueAsDouble(cell));
                        break;
                    case "specialstock":
                        invokeSetter(outboundOrderProcess, "setSpecialStock", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "mtonumber":
                        invokeSetter(outboundOrderProcess, "setMtoNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "skucode":
                    case "sku":
                        invokeSetter(outboundOrderProcess, "setSku", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "barcodeid":
                        invokeSetter(outboundOrderProcess, "setBarcodeId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "skutext":
                        invokeSetter(outboundOrderProcess, "setSkuDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "uom":
                        invokeSetter(outboundOrderProcess, "setUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "qtyincases":
                        invokeSetter(outboundOrderProcess, "setQtyInCase", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "qtyinpcs":
                        invokeSetter(outboundOrderProcess, "setQtyInPiece", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "salesorderno":
                        invokeSetter(outboundOrderProcess, "setSalesOrderNumber", cell != null ? getCellValueAsString(cell) : null);
                        invokeSetter(outboundOrderProcess, "setPickListNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "linereference":
                    case "lineno":
                        invokeSetter(outboundOrderProcess, "setLineReference", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "transferorderno":
                        invokeSetter(outboundOrderProcess, "setTransferOrderNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "customerid":
                        invokeSetter(outboundOrderProcess, "setCustomerId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "customername":
                        invokeSetter(outboundOrderProcess, "setCustomerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "orderqty":
                        invokeSetter(outboundOrderProcess, "setOrderedQty", cell != null ? getCellValueAsDouble(cell) : null);
                        invokeSetter(outboundOrderProcess, "setQtyInPiece", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "caseqty":
                        invokeSetter(outboundOrderProcess, "setQtyInCase", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "itemcode":
                        invokeSetter(outboundOrderProcess, "setSku", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemtext":
                        invokeSetter(outboundOrderProcess, "setSkuDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "tocompany":
                        invokeSetter(outboundOrderProcess, "setToCompanyCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "tobranch":
                        invokeSetter(outboundOrderProcess, "setToBranchCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                }
            }
        } catch (Exception e) {
            log.info("outboundOrderProcess Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }


    /**
     *
     * @param outboundOrderProcess
     * @param row
     * @param columnIntexMap
     */
    public void setOutboundFieldByColumnName(OutboundOrderProcessV4 outboundOrderProcess, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "outbound":
                        invokeSetter(outboundOrderProcess, "setPickListNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itm":
                        invokeSetter(outboundOrderProcess, "setItm", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "customercode":
                        invokeSetter(outboundOrderProcess, "setCustomerId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "customer":
                        invokeSetter(outboundOrderProcess, "setCustomerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "sourcecompanycode":
                    case "fromcompanycode":
                        invokeSetter(outboundOrderProcess, "setFromCompanyCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "sourcebranchcode":
                    case "frombranchcode":
                        invokeSetter(outboundOrderProcess, "setFromBranchCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "tocompanycode":
                        invokeSetter(outboundOrderProcess, "setToCompanyCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "tobranchcode":
                        invokeSetter(outboundOrderProcess, "setToBranchCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "shiptocode":
                        invokeSetter(outboundOrderProcess, "setShipToCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "shiptoparty":
                        invokeSetter(outboundOrderProcess, "setShipToParty", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "material":
                        invokeSetter(outboundOrderProcess, "setMaterialNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "pricesegment":
                        invokeSetter(outboundOrderProcess, "setPriceSegment", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "qty":
                        invokeSetter(outboundOrderProcess, "setOrderedQty", getCellValueAsDouble(cell));
                        break;
                    case "specialstock":
                        invokeSetter(outboundOrderProcess, "setSpecialStock", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "mtonumber":
                        invokeSetter(outboundOrderProcess, "setMtoNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "skucode":
                    case "sku":
                        invokeSetter(outboundOrderProcess, "setSku", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "barcodeid":
                        invokeSetter(outboundOrderProcess, "setBarcodeId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "skutext":
                        invokeSetter(outboundOrderProcess, "setSkuDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "uom":
                        invokeSetter(outboundOrderProcess, "setUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "returnorderno":
                        invokeSetter(outboundOrderProcess, "setSalesOrderNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                }
            }
        } catch (Exception e) {
            log.info("outboundOrderProcess Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }

    //=========================================Knowell======================================================//

    /**
     * ReadExcelFile Modified for Knowell
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param orderTypeId
     * @param loginUserId
     * @param file
     * @return
     * @throws IOException
     */
    public List<InboundOrderProcessV4> inboundReadExcelFileKnowell(String companyCodeId, String plantId, String languageId, String warehouseId, Long orderTypeId, String loginUserId, MultipartFile file) throws IOException {
        List<InboundOrderProcessV4> inboundOrderList = new ArrayList<>();
        // Create Workbook for Excel file
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        //Get the header row first row
        Row headerRow = sheet.getRow(0);
        // Map column names their corresponding index
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            columnIndexMap.put(cell.getStringCellValue().toLowerCase().trim(), cell.getColumnIndex());
        }

        // Iterate through rows (skip the header row)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                InboundOrderProcessV4 inboundOrderProcess = new InboundOrderProcessV4();

                // Set the fields dynamically based on column name
                setFieldByColumnNameKnowell(inboundOrderProcess, row, columnIndexMap);
                inboundOrderProcess.setCompanyCode(companyCodeId);
//                inboundOrderProcess.setToCompanyCode(companyCodeId);
                inboundOrderProcess.setBranchCode(plantId);
//                inboundOrderProcess.setToBranchCode(plantId);
                inboundOrderProcess.setLanguageId(languageId);
                inboundOrderProcess.setWarehouseId(warehouseId);
                inboundOrderProcess.setInboundOrderTypeId(orderTypeId);
                inboundOrderProcess.setLoginUserId(loginUserId);

                // Add the mapped delivery object to the list
                inboundOrderList.add(inboundOrderProcess);
            }
        }

        // Close the workbook to free resource
        workbook.close();
        return inboundOrderList;
    }


    /**
     *
     * @param inboundOrderProcess
     * @param row
     * @param columnIntexMap
     */
    public void setFieldByColumnNameKnowell(InboundOrderProcessV4 inboundOrderProcess, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "inbound":
                    case "asnnumber":
                        invokeSetter(inboundOrderProcess, "setAsnNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "huserialnumber":
                        invokeSetter(inboundOrderProcess, "setBarcodeId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "material":
                        invokeSetter(inboundOrderProcess, "setMaterialNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "pricesegment":
                        invokeSetter(inboundOrderProcess, "setPriceSegment", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "plant":
                        invokeSetter(inboundOrderProcess, "setPlant", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storagelocation":
                        invokeSetter(inboundOrderProcess, "setStorageLocation", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "skucode":
                    case "sku":
                    case "itemcode":
                        invokeSetter(inboundOrderProcess, "setSku", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "articlenumber":
                        invokeSetter(inboundOrderProcess, "setArticleNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "noofpairs":
                        invokeSetter(inboundOrderProcess, "setNoPairs", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "gender":
                        invokeSetter(inboundOrderProcess, "setGender", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "color":
                        invokeSetter(inboundOrderProcess, "setColor", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "size":
                        invokeSetter(inboundOrderProcess, "setSize", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "qty":
                    case "expectedqty":
                        invokeSetter(inboundOrderProcess, "setExpectedQty", getCellValueAsDouble(cell));
                        break;
                    case "returnordernumber":
                        invokeSetter(inboundOrderProcess, "setTransferOrderNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "refordernumber":
                        invokeSetter(inboundOrderProcess, "setAsnNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "purchaseordernumber":
                        invokeSetter(inboundOrderProcess, "setPurchaseOrderNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "suppliercode":
                        invokeSetter(inboundOrderProcess, "setSupplierCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "transferordernumber":
                        invokeSetter(inboundOrderProcess, "setTransferOrderNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "sourcecompanycode":
                        invokeSetter(inboundOrderProcess, "setSourceCompanyCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "sourcebranchcode":
                        invokeSetter(inboundOrderProcess, "setSourceBranchCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "transferorderdate":
                        invokeSetter(inboundOrderProcess, "setTransferOrderDate", getCellValueAsDate(cell));
                        break;
                    case "linereference":
                        invokeSetter(inboundOrderProcess, "setLineReference", getCellValueAsLong(cell));
                        break;
                    case "skudescription":
                    case "itemtext":
                        invokeSetter(inboundOrderProcess, "setSkuDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "uom":
                        invokeSetter(inboundOrderProcess, "setUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturername":
                        invokeSetter(inboundOrderProcess, "setManufacturerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturercode":
                        invokeSetter(inboundOrderProcess, "setManufacturerCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturerfullname":
                        invokeSetter(inboundOrderProcess, "setManufacturerFullName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "containernumber":
                        invokeSetter(inboundOrderProcess, "setContainerNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "supplierpartnumber":
                        invokeSetter(inboundOrderProcess, "setSupplierPartNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "expecteddate":
                        invokeSetter(inboundOrderProcess, "setExpectedDate", getCellValueAsDate(cell));
                        break;
                    case "date":
                        invokeSetter(inboundOrderProcess, "setExpectedDate", getCellValueAsDate(cell));
                        break;
                    case "receiveddate":
                        invokeSetter(inboundOrderProcess, "setReceivedDate", getCellValueAsDate(cell));
                        break;
                    case "receivedqty":
                        invokeSetter(inboundOrderProcess, "setReceivedQty", getCellValueAsDouble(cell));
                        break;
                    case "packqty":
                        invokeSetter(inboundOrderProcess, "setPackQty", getCellValueAsDouble(cell));
                        break;
                    case "receivedby":
                        invokeSetter(inboundOrderProcess, "setReceivedBy", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "origin":
                        invokeSetter(inboundOrderProcess, "setOrigin", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "brand":
                        invokeSetter(inboundOrderProcess, "setBrand", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "suppliername":
                        invokeSetter(inboundOrderProcess, "setSupplierName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "supplierinvoiceno":
                        invokeSetter(inboundOrderProcess, "setSupplierInvoiceNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "batchserialnumber":
                        invokeSetter(inboundOrderProcess, "setBatchSerialNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "barcodeid":
                        invokeSetter(inboundOrderProcess, "setBarcodeId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemtype":
                        invokeSetter(inboundOrderProcess, "setItemType", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemgroup":
                        invokeSetter(inboundOrderProcess, "setItemGroup", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "invoicenumber":
                        invokeSetter(inboundOrderProcess, "setInvoiceNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storeid":
                        invokeSetter(inboundOrderProcess, "setStoreId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "salesorderreference":
                        invokeSetter(inboundOrderProcess, "setSalesOrderReference", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "alternateuom":
                        invokeSetter(inboundOrderProcess, "setAlternateUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "nobags":
                        invokeSetter(inboundOrderProcess, "setNoBags", getCellValueAsDouble(cell));
                        break;
                    case "bagsize":
                        invokeSetter(inboundOrderProcess, "setBagSize", getCellValueAsDouble(cell));
                        break;
                    case "mrp":
                        invokeSetter(inboundOrderProcess, "setMrp", getCellValueAsDouble(cell));
                        break;
                    case "ordernumber":
                        invokeSetter(inboundOrderProcess, "setAsnNumber", getCellValueAsString(cell));
                        break;
                    case "expectedqtyinpcs":
                        invokeSetter(inboundOrderProcess, "setExpectedQtyInPieces", getCellValueAsDouble(cell));
                        break;
                    case "expectedqtyincases":
                    case "caseqty":
                        invokeSetter(inboundOrderProcess, "setExpectedQtyInCases", getCellValueAsDouble(cell));
                        break;
                    case "returnorderno":
                        invokeSetter(inboundOrderProcess, "setAsnNumber", getCellValueAsString(cell));
                        break;
                    case "transferorderno":
                        invokeSetter(inboundOrderProcess, "setAsnNumber", getCellValueAsString(cell));
                        invokeSetter(inboundOrderProcess, "setTransferOrderNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case"tocompanycode":
                        invokeSetter(inboundOrderProcess,"setToCompanyCode",getCellValueAsString(cell));
                        break;
                    case"tobranchcode":
                        invokeSetter(inboundOrderProcess,"setToBranchCode",getCellValueAsString(cell));
                        break;
                    case"sourcecompany":
                        invokeSetter(inboundOrderProcess,"setSourceCompanyCode",getCellValueAsString(cell));
                        break;
                    case"sourcebranch":
                        invokeSetter(inboundOrderProcess,"setSourceBranchCode",getCellValueAsString(cell));
                        break;
                    case "orderqty":
                        invokeSetter(inboundOrderProcess,"setOrderQty",getCellValueAsDouble(cell));
                        invokeSetter(inboundOrderProcess, "setExpectedQtyInPieces", getCellValueAsDouble(cell));
                        invokeSetter(inboundOrderProcess, "setExpectedQty", getCellValueAsDouble(cell));
                        break;
                }
            }
        } catch (Exception e) {
            log.info("inboundOrderProcess Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }


    //============== BF =========================
    public List<ImBasicData1V2> readExcelFileV9(String companyCodeId, String plantId, String languageId, String warehouseId, String loginUserID, MultipartFile file) throws IOException {
        List<ImBasicData1V2> imBasicDataList = new ArrayList<>();
        // Create Workbook for Excel file
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        //Get the header row first row
        Row headerRow = sheet.getRow(0);
        // Map column names their corresponding index
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            columnIndexMap.put(cell.getStringCellValue().toLowerCase().trim(), cell.getColumnIndex());
        }

        // Iterate through rows (skip the header row)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                ImBasicData1V2 imBasicData1 = new ImBasicData1V2();

                // Set the fields dynamically based on column name
                setFieldByColumnV9(imBasicData1, row, columnIndexMap);
                imBasicData1.setCompanyCodeId(companyCodeId);
                imBasicData1.setPlantId(plantId);
                imBasicData1.setWarehouseId(warehouseId);
                imBasicData1.setLanguageId(languageId);
                imBasicData1.setCreatedBy(loginUserID);
                imBasicDataList.add(imBasicData1);
            }
        }

        // Close the workbook to free resource
        workbook.close();
        return imBasicDataList;
    }
    //============== BF =========================
    public void setFieldByColumnV9(ImBasicData1V2 imBasicData1, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "uomid":
                        invokeSetter(imBasicData1, "setUomId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemcode":
                        invokeSetter(imBasicData1, "setItemCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "description":
                        invokeSetter(imBasicData1, "setDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturerpartno":
                        invokeSetter(imBasicData1, "setManufacturerPartNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "hsncode":
                        invokeSetter(imBasicData1, "setHsnCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storagesectionid":
                        invokeSetter(imBasicData1, "setStorageSectionId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "capacitycheck":
                        invokeSetter(imBasicData1, "setCapacityCheck", cell != null ? getCellValueAsBoolean(cell) : null);
                        break;
                    case "capacityunit":
                        invokeSetter(imBasicData1, "setCapacityUnit", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "capacityuom":
                        invokeSetter(imBasicData1, "setCapacityUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "quantity":
                        invokeSetter(imBasicData1, "setQuantity", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturername":
                        invokeSetter(imBasicData1, "setManufacturerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturercode":
                        invokeSetter(imBasicData1, "setManufacturerCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturerfullname":
                        invokeSetter(imBasicData1, "setManufacturerFullName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "supplierpartnumber":
                        invokeSetter(imBasicData1, "setSupplierPartNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "dimensionuom":
                        invokeSetter(imBasicData1, "setDimensionUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "model":
                        invokeSetter(imBasicData1, "setModel", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "specifications1":
                        invokeSetter(imBasicData1, "setSpecifications1", cell != null ? getCellValueAsString(cell) :null);
                        break;
                    case "specifications2":
                        invokeSetter(imBasicData1, "setSpecifications2", cell != null ? getCellValueAsString(cell) :null);
                        break;
                    case "eanupcno":
                        invokeSetter(imBasicData1, "setEanUpcNo", cell != null ? getCellValueAsString(cell) :null);
                        break;
                    case "shelflifeindicator":
                        invokeSetter(imBasicData1, "setShelfLifeIndicator", cell != null ? getCellValueAsBoolean(cell) :null);
                        break;
                    case "brand":
                        invokeSetter(imBasicData1, "setBrand", cell != null ? getCellValueAsString(cell) :null);
                        break;
                    case "remarks":
                        invokeSetter(imBasicData1, "setRemarks", cell != null ? getCellValueAsString(cell) :null);
                        break;
                    case "itemtype":
                        invokeSetter(imBasicData1, "setItemType", cell != null ? getCellValueAsLong(cell) :null);
                        break;
                    case "itemgroup":
                        invokeSetter(imBasicData1, "setItemGroup", cell != null ? getCellValueAsLong(cell) :null);
                        break;
                    case "subitemgroup":
                        invokeSetter(imBasicData1, "setSubItemGroup", cell != null ? getCellValueAsLong(cell) :null);
                        break;
                    case "minimumstock":
                        invokeSetter(imBasicData1, "setMinimumStock", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "maximumstock":
                        invokeSetter(imBasicData1, "setMaximumStock", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "reorderlevel":
                        invokeSetter(imBasicData1, "setReorderLevel", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "replenishmentqty":
                        invokeSetter(imBasicData1, "setReplenishmentQty", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "safetystock":
                        invokeSetter(imBasicData1, "setSafetyStock", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "weight":
                        invokeSetter(imBasicData1, "setWeight", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "statusid":
                        invokeSetter(imBasicData1, "setStatusId", cell != null ? getCellValueAsLong(cell) :null);
                        break;
                    case "selflife":
                        invokeSetter(imBasicData1, "setSelfLife", cell != null ? getCellValueAsLong(cell) :null);
                        break;
                    case "length":
                        invokeSetter(imBasicData1, "setLength", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "width":
                        invokeSetter(imBasicData1, "setWidth", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "height":
                        invokeSetter(imBasicData1, "setHeight", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "volume":
                        invokeSetter(imBasicData1, "setVolume", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
//                    case "movingtype":
//                        invokeSetter(imBasicData1, "setReferenceField5", cell != null ? getCellValueAsDouble(cell) :null);
//                        break;
                    case "referencefield1":
                        invokeSetter(imBasicData1, "setReferenceField1", cell != null ? getCellValueAsString(cell) :null);
                        break;
                    case "movingtype":
                    case "referencefield5":
                        invokeSetter(imBasicData1, "setReferenceField5", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "referencefield2":
                        invokeSetter(imBasicData1, "setReferenceField2", cell != null ? getCellValueAsString(cell) :null);
                        break;
                }
            }
        } catch (Exception e) {
            log.info("imBasicData1 Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }

    //================= SPAREX ===================
    public List<ImBasicData1V2> readExcelFileV10(String companyCodeId, String plantId, String languageId, String warehouseId, String loginUserID, MultipartFile file) throws IOException {
        List<ImBasicData1V2> imBasicDataList = new ArrayList<>();
        // Create Workbook for Excel file
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        //Get the header row first row
        Row headerRow = sheet.getRow(0);
        // Map column names their corresponding index
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            columnIndexMap.put(cell.getStringCellValue().toLowerCase().trim(), cell.getColumnIndex());
        }

        // Iterate through rows (skip the header row)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                ImBasicData1V2 imBasicData1 = new ImBasicData1V2();

                // Set the fields dynamically based on column name
                setFieldByColumnV10(imBasicData1, row, columnIndexMap);
                imBasicData1.setCompanyCodeId(companyCodeId);
                imBasicData1.setPlantId(plantId);
                imBasicData1.setWarehouseId(warehouseId);
                imBasicData1.setLanguageId(languageId);
                imBasicData1.setCreatedBy(loginUserID);
                imBasicDataList.add(imBasicData1);
            }
        }
        workbook.close();
        return imBasicDataList;
    }

    private Boolean getCellValueAsBoolean(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case BOOLEAN:
                return cell.getBooleanCellValue();

            case STRING:
                String strVal = cell.getStringCellValue().trim().toLowerCase();
                if ("true".equals(strVal) || "1".equals(strVal)) return true;
                if ("false".equals(strVal) || "0".equals(strVal)) return false;
                return null;

            case NUMERIC:
                double numVal = cell.getNumericCellValue();
                if (numVal == 1.0) return true;
                if (numVal == 0.0) return false;
                return null;

            default:
                return null;
        }
    }

    //=============== SPAREX ======================
    public void setFieldByColumnV10(ImBasicData1V2 imBasicData1, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "uomid":
                        invokeSetter(imBasicData1, "setUomId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemcode":
                        invokeSetter(imBasicData1, "setItemCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "description":
                        invokeSetter(imBasicData1, "setDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storagesectionid":
                        invokeSetter(imBasicData1, "setStorageSectionId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturername":
                        invokeSetter(imBasicData1, "setManufacturerName", cell != null ? getCellValueAsString(cell) : null);
                        invokeSetter(imBasicData1, "setManufacturerFullName", cell != null ? getCellValueAsString(cell) : null);
                        invokeSetter(imBasicData1, "setManufacturerCode", cell != null ? getCellValueAsString(cell) : null);
                        invokeSetter(imBasicData1, "setManufacturerPartNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "shelflifeindicator":
                        invokeSetter(imBasicData1, "setShelfLifeIndicator", cell != null ? getCellValueAsBoolean(cell) :null);
                        break;
                    case "itemtype":
                        invokeSetter(imBasicData1, "setItemType", cell != null ? getCellValueAsLong(cell) :null);
                        break;
                    case "itemgroup":
                        invokeSetter(imBasicData1, "setItemGroup", cell != null ? getCellValueAsLong(cell) :null);
                        break;
                    case "subitemgroup":
                        invokeSetter(imBasicData1, "setSubItemGroup", cell != null ? getCellValueAsLong(cell) :null);
                        break;
                    case "referencefield1":
                        invokeSetter(imBasicData1, "setReferenceField1", cell != null ? getCellValueAsString(cell) :null);
                        break;

                }
            }
        } catch (Exception e) {
            log.info("imBasicData1 Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }


    //============== BF =========================
    public List<ImBasicData1V2> readExcelFile(String companyCodeId, String plantId, String languageId, String warehouseId, String loginUserID, MultipartFile file) throws IOException {
        List<ImBasicData1V2> imBasicDataList = new ArrayList<>();
        // Create Workbook for Excel file
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        //Get the header row first row
        Row headerRow = sheet.getRow(0);
        // Map column names their corresponding index
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            columnIndexMap.put(cell.getStringCellValue().toLowerCase().trim(), cell.getColumnIndex());
        }

        // Iterate through rows (skip the header row)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                ImBasicData1V2 imBasicData1 = new ImBasicData1V2();

                // Set the fields dynamically based on column name
                setFieldByColumn(imBasicData1, row, columnIndexMap);
                imBasicData1.setCompanyCodeId(companyCodeId);
                imBasicData1.setPlantId(plantId);
                imBasicData1.setWarehouseId(warehouseId);
                imBasicData1.setLanguageId(languageId);
                imBasicData1.setCreatedBy(loginUserID);
                imBasicDataList.add(imBasicData1);
            }
        }

        // Close the workbook to free resource
        workbook.close();
        return imBasicDataList;
    }
    //============== BF =========================
    public void setFieldByColumn(ImBasicData1V2 imBasicData1, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "uomid":
                        invokeSetter(imBasicData1, "setUomId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemcode":
                        invokeSetter(imBasicData1, "setItemCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "description":
                        invokeSetter(imBasicData1, "setDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturerpartno":
                        invokeSetter(imBasicData1, "setManufacturerPartNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "hsncode":
                        invokeSetter(imBasicData1, "setHsnCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storagesectionid":
                        invokeSetter(imBasicData1, "setStorageSectionId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "capacitycheck":
                        invokeSetter(imBasicData1, "setCapacityCheck", cell != null ? getCellValueAsBoolean(cell) : null);
                        break;
                    case "capacityunit":
                        invokeSetter(imBasicData1, "setCapacityUnit", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "capacityuom":
                        invokeSetter(imBasicData1, "setCapacityUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "quantity":
                        invokeSetter(imBasicData1, "setQuantity", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturername":
                        invokeSetter(imBasicData1, "setManufacturerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturercode":
                        invokeSetter(imBasicData1, "setManufacturerCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturerfullname":
                        invokeSetter(imBasicData1, "setManufacturerFullName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "supplierpartnumber":
                        invokeSetter(imBasicData1, "setSupplierPartNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "dimensionuom":
                        invokeSetter(imBasicData1, "setDimensionUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "model":
                        invokeSetter(imBasicData1, "setModel", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "specifications1":
                        invokeSetter(imBasicData1, "setSpecifications1", cell != null ? getCellValueAsString(cell) :null);
                        break;
                    case "specifications2":
                        invokeSetter(imBasicData1, "setSpecifications2", cell != null ? getCellValueAsString(cell) :null);
                        break;
                    case "eanupcno":
                        invokeSetter(imBasicData1, "setEanUpcNo", cell != null ? getCellValueAsString(cell) :null);
                        break;
                    case "shelflifeindicator":
                        invokeSetter(imBasicData1, "setShelfLifeIndicator", cell != null ? getCellValueAsBoolean(cell) :null);
                        break;
                    case "brand":
                        invokeSetter(imBasicData1, "setBrand", cell != null ? getCellValueAsString(cell) :null);
                        break;
                    case "remarks":
                        invokeSetter(imBasicData1, "setRemarks", cell != null ? getCellValueAsString(cell) :null);
                        break;
                    case "itemtype":
                        invokeSetter(imBasicData1, "setItemType", cell != null ? getCellValueAsLong(cell) :null);
                        break;
                    case "itemgroup":
                        invokeSetter(imBasicData1, "setItemGroup", cell != null ? getCellValueAsLong(cell) :null);
                        break;
                    case "subitemgroup":
                        invokeSetter(imBasicData1, "setSubItemGroup", cell != null ? getCellValueAsLong(cell) :null);
                        break;
                    case "minimumstock":
                        invokeSetter(imBasicData1, "setMinimumStock", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "maximumstock":
                        invokeSetter(imBasicData1, "setMaximumStock", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "reorderlevel":
                        invokeSetter(imBasicData1, "setReorderLevel", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "replenishmentqty":
                        invokeSetter(imBasicData1, "setReplenishmentQty", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "safetystock":
                        invokeSetter(imBasicData1, "setSafetyStock", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "weight":
                        invokeSetter(imBasicData1, "setWeight", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "statusid":
                        invokeSetter(imBasicData1, "setStatusId", cell != null ? getCellValueAsLong(cell) :null);
                        break;
                    case "selflife":
                        invokeSetter(imBasicData1, "setSelfLife", cell != null ? getCellValueAsLong(cell) :null);
                        break;
                    case "length":
                        invokeSetter(imBasicData1, "setLength", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "width":
                        invokeSetter(imBasicData1, "setWidth", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "height":
                        invokeSetter(imBasicData1, "setHeight", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
                    case "volume":
                        invokeSetter(imBasicData1, "setVolume", cell != null ? getCellValueAsDouble(cell) :null);
                        break;
//                    case "movingtype":
//                        invokeSetter(imBasicData1, "setReferenceField5", cell != null ? getCellValueAsDouble(cell) :null);
//                        break;
                    case "referencefield1":
                        invokeSetter(imBasicData1, "setReferenceField1", cell != null ? getCellValueAsString(cell) :null);
                        break;
                    case "movingtype":
                    case "referencefield5":
                        invokeSetter(imBasicData1, "setReferenceField5", cell != null ? getCellValueAsString(cell) :null);
                        break;
                    case "referencefield2":
                        invokeSetter(imBasicData1, "setReferenceField2", cell != null ? getCellValueAsString(cell) :null);
                        break;
                }
            }
        } catch (Exception e) {
            log.info("imBasicData1 Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }


    public List<ImAlternateUom> readExcelFileUom(String companyCodeId, String plantId, String languageId, String warehouseId, String loginUserID, MultipartFile file) throws IOException {
        List<ImAlternateUom> alternateUoms = new ArrayList<>();
        // Create Workbook for Excel file
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        //Get the header row first row
        Row headerRow = sheet.getRow(0);
        // Map column names their corresponding index
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            columnIndexMap.put(cell.getStringCellValue().toLowerCase().trim(), cell.getColumnIndex());
        }

        // Iterate through rows (skip the header row)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                ImAlternateUom alternateUom = new ImAlternateUom();

                // Set the fields dynamically based on column name
                setFieldByColumnUom(alternateUom, row, columnIndexMap);
                alternateUom.setCompanyCodeId(companyCodeId);
                alternateUom.setPlantId(plantId);
                alternateUom.setWarehouseId(warehouseId);
                alternateUom.setLanguageId(languageId);
                alternateUom.setCreatedBy(loginUserID);
                alternateUoms.add(alternateUom);
            }
        }

        // Close the workbook to free resource
        workbook.close();
        return alternateUoms;
    }



    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserID
     * @param file
     * @return
     * @throws IOException
     */
    public List<BusinessPartnerV2> businessPartnerReadExcelFile(String companyCodeId, String plantId, String languageId,
                                                                String warehouseId, String loginUserID, MultipartFile file) throws IOException {
        List<BusinessPartnerV2> businessPartnerList = new ArrayList<>();
        // Create Workbook for Excel file
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        //Get the header row first row
        Row headerRow = sheet.getRow(0);
        // Map column names their corresponding index
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            columnIndexMap.put(cell.getStringCellValue().toLowerCase().trim(), cell.getColumnIndex());
        }

        // Iterate through rows (skip the header row)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                BusinessPartnerV2 businessPartner = new BusinessPartnerV2();

                // Set the fields dynamically based on column name
                setFieldByBusinessPartner(businessPartner, row, columnIndexMap);
                businessPartner.setCompanyCodeId(companyCodeId);
                businessPartner.setPlantId(plantId);
                businessPartner.setLanguageId(languageId);
                businessPartner.setWarehouseId(warehouseId);
                businessPartner.setCreatedBy(loginUserID);

                // Add the mapped delivery object to the list
                businessPartnerList.add(businessPartner);
            }
        }

        // Close the workbook to free resource
        workbook.close();
        return businessPartnerList;
    }

    //===========BP Inventory=========================================
    public List<InventoryV2> inventoryReadExcelFileV6(String companyCodeId, String plantId, String languageID, String warehouseId, String loginUserId, MultipartFile file) throws IOException {
        List<InventoryV2> inventoryV2List = new ArrayList<>();
        // Create Workbook for Excel file
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        //Get the header row first row
        Row headerRow = sheet.getRow(0);
        // Map column names their corresponding index
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            columnIndexMap.put(cell.getStringCellValue().toLowerCase().trim(), cell.getColumnIndex());
        }

        // Iterate through rows (skip the header row)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {

                InventoryV2 inventory = new InventoryV2();
                setFieldByColumnNameInvV6(inventory, row, columnIndexMap);
                inventory.setCompanyCodeId(companyCodeId);
                inventory.setPlantId(plantId);
                inventory.setLanguageId(languageID);
                inventory.setWarehouseId(warehouseId);
                inventory.setCreatedBy(loginUserId);
                inventoryV2List.add(inventory);
            }
        }

        // Close the workbook to free resource
        workbook.close();
        return inventoryV2List;
    }


    public void setFieldByColumnUom(ImAlternateUom alternateUom, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "uomid":
                        invokeSetter(alternateUom, "setUomId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemcode":
                        invokeSetter(alternateUom, "setItemCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemdescription":
                        invokeSetter(alternateUom, "setItemDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "alternateuomqty":
                        invokeSetter(alternateUom, "setAlternateUomQty", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "uomidqty":
                        invokeSetter(alternateUom, "setUomIdQty", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "alternateuom":
                        invokeSetter(alternateUom, "setAlternateUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                }
            }
        } catch (Exception e) {
            log.info("alternateUom Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }


    //========================BusinessPartner Upload========================================================
    /**
     * @param businessPartner
     * @param row
     * @param columnIntexMap
     */
    public void setFieldByBusinessPartner(BusinessPartnerV2 businessPartner, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "partnercode":
                        invokeSetter(businessPartner, "setPartnerCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "partnername":
                        invokeSetter(businessPartner, "setPartnerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "partnertype":
                        invokeSetter(businessPartner, "setBusinessPartnerType", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "gst":
                        invokeSetter(businessPartner, "setReferenceField1", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "status":
                        invokeSetter(businessPartner, "setReferenceField2", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "address1":
                        invokeSetter(businessPartner, "setAddress1", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "address2":
                        invokeSetter(businessPartner, "setAddress2", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "state":
                        invokeSetter(businessPartner, "setState", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "city":
                        invokeSetter(businessPartner, "setReferenceField3", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "country":
                        invokeSetter(businessPartner, "setCountry", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "inventoryowner":
                        invokeSetter(businessPartner, "setReferenceField5", cell != null ? getCellValueAsString(cell) : null);
                        break;

                }
            }
        } catch (Exception e) {
            log.info("BusinessPartner Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }

    public void setFieldByColumnNameInvV6(InventoryV2 inventoryV2, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "palletcode":
                        invokeSetter(inventoryV2, "setPalletCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "casecode":
                        invokeSetter(inventoryV2, "setCaseCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemcode":
                        invokeSetter(inventoryV2, "setItemCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "packbarcodes":
                        invokeSetter(inventoryV2, "setPackBarcodes", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "variantsubcode":
                        invokeSetter(inventoryV2, "setVariantSubCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "batchserialnumber":
                        invokeSetter(inventoryV2, "setBatchSerialNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storagebin":
                        invokeSetter(inventoryV2, "setStorageBin", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storagemethod":
                        invokeSetter(inventoryV2, "setStorageMethod", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "description":
                        invokeSetter(inventoryV2, "setDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "inventoryuom":
                        invokeSetter(inventoryV2, "setInventoryUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturercode":
                        invokeSetter(inventoryV2, "setManufacturerCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "barcodeid":
                        invokeSetter(inventoryV2, "setBarcodeId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "cbm":
                        invokeSetter(inventoryV2, "setCbm", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "cbmunit":
                        invokeSetter(inventoryV2, "setCbmUnit", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "cbmperquantity":
                        invokeSetter(inventoryV2, "setCbmPerQuantity", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturername":
                        invokeSetter(inventoryV2, "setManufacturerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "origin":
                        invokeSetter(inventoryV2, "setOrigin", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "brand":
                        invokeSetter(inventoryV2, "setBrand", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencedocumentno":
                        invokeSetter(inventoryV2, "setReferenceDocumentNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "levelid":
                        invokeSetter(inventoryV2, "setLevelId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "stocktypedescription":
                        invokeSetter(inventoryV2, "setStockTypeDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield1":
                        invokeSetter(inventoryV2, "setReferenceField1", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield2":
                        invokeSetter(inventoryV2, "setReferenceField2", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield3":
                        invokeSetter(inventoryV2, "setReferenceField3", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield4":
                        invokeSetter(inventoryV2, "setReferenceField4", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "referencefield5":
                        invokeSetter(inventoryV2, "setReferenceField5", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield6":
                        invokeSetter(inventoryV2, "setReferenceField6", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield7":
                        invokeSetter(inventoryV2, "setReferenceField7", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield8":
                        invokeSetter(inventoryV2, "setReferenceField8", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield9":
                        invokeSetter(inventoryV2, "setReferenceField9", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield10":
                        invokeSetter(inventoryV2, "setReferenceField10", cell != null ? getCellValueAsString(cell) : null);
                        break;

                    case "netweight":
                        invokeSetter(inventoryV2, "setPriceSegment", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "grossweight":
                        invokeSetter(inventoryV2, "setThreePLPartnerId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "inventoryowner":
                        invokeSetter(inventoryV2, "setMaterialNo", cell != null ? getCellValueAsString(cell) : null);
                        break;

                    case "variantcode":
                        invokeSetter(inventoryV2, "setVariantCode", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "specialstockindicatorid":
                        invokeSetter(inventoryV2, "setSpecialStockIndicatorId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "storagetypeid":
                        invokeSetter(inventoryV2, "setStorageTypeId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "binclassid":
                        invokeSetter(inventoryV2, "setBinClassId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "stocktypeid":
                        invokeSetter(inventoryV2, "setStockTypeId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "allocatedquantity":
                        invokeSetter(inventoryV2, "setAllocatedQuantity", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "inventoryquantity":
                        invokeSetter(inventoryV2, "setInventoryQuantity", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "deletionindicator":
                        invokeSetter(inventoryV2, "setDeletionIndicator", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "manufacturerdate":
                        invokeSetter(inventoryV2, "setManufacturerDate", cell != null ? getCellValueAsDate1(cell) : null);
                        break;
                    case "expirydate":
                        invokeSetter(inventoryV2, "setExpiryDate", cell != null ? getCellValueAsDate1(cell) : null);
                        break;
                    case "mrp":
                        invokeSetter(inventoryV2, "setMrp", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                }
            }
        } catch (Exception e) {
            log.info("Inventory Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }


    //==========SPAREX Inventory=========================================
    public List<InventoryV2> inventoryReadExcelFileV10(String companyCodeId, String plantId, String languageID, String warehouseId, String loginUserId, MultipartFile file) throws IOException {
        List<InventoryV2> inventoryV2List = new ArrayList<>();
        // Create Workbook for Excel file
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        //Get the header row first row
        Row headerRow = sheet.getRow(0);
        // Map column names their corresponding index
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            columnIndexMap.put(cell.getStringCellValue().toLowerCase().trim(), cell.getColumnIndex());
        }

        // Iterate through rows (skip the header row)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {

                InventoryV2 inventory = new InventoryV2();
                setFieldByColumnNameInvV10(inventory, row, columnIndexMap);
                inventory.setCompanyCodeId(companyCodeId);
                inventory.setPlantId(plantId);
                inventory.setLanguageId(languageID);
                inventory.setWarehouseId(warehouseId);
                inventory.setCreatedBy(loginUserId);
                // Add the mapped delivery object to the list
                inventoryV2List.add(inventory);
            }
        }

        // Close the workbook to free resource
        workbook.close();
        return inventoryV2List;
    }

    public void setFieldByColumnNameInvV10(InventoryV2 inventoryV2, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "palletcode":
                        invokeSetter(inventoryV2, "setPalletCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "casecode":
                        invokeSetter(inventoryV2, "setCaseCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemcode":
                        invokeSetter(inventoryV2, "setItemCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "packbarcodes":
                        invokeSetter(inventoryV2, "setPackBarcodes", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "variantsubcode":
                        invokeSetter(inventoryV2, "setVariantSubCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "batchserialnumber":
                        invokeSetter(inventoryV2, "setBatchSerialNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storagebin":
                        invokeSetter(inventoryV2, "setStorageBin", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storagemethod":
                        invokeSetter(inventoryV2, "setStorageMethod", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "description":
                        invokeSetter(inventoryV2, "setDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "inventoryuom":
                        invokeSetter(inventoryV2, "setInventoryUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "barcodeid":
                        invokeSetter(inventoryV2, "setBarcodeId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturername":
                        invokeSetter(inventoryV2, "setManufacturerName", cell != null ? getCellValueAsString(cell) : null);
                        invokeSetter(inventoryV2, "setManufacturerCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "levelid":
                        invokeSetter(inventoryV2, "setLevelId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "stocktypedescription":
                        invokeSetter(inventoryV2, "setStockTypeDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield1":
                        invokeSetter(inventoryV2, "setReferenceField1", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield2":
                        invokeSetter(inventoryV2, "setReferenceField2", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield4":
                        invokeSetter(inventoryV2, "setReferenceField4", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "referencefield8":
                        invokeSetter(inventoryV2, "setReferenceField8", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield9":
                        invokeSetter(inventoryV2, "setReferenceField9", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield10":
                        invokeSetter(inventoryV2, "setReferenceField10", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "variantcode":
                        invokeSetter(inventoryV2, "setVariantCode", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "specialstockindicatorid":
                        invokeSetter(inventoryV2, "setSpecialStockIndicatorId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "storagetypeid":
                        invokeSetter(inventoryV2, "setStorageTypeId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "binclassid":
                        invokeSetter(inventoryV2, "setBinClassId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "stocktypeid":
                        invokeSetter(inventoryV2, "setStockTypeId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "allocatedquantity":
                        invokeSetter(inventoryV2, "setAllocatedQuantity", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "inventoryquantity":
                        invokeSetter(inventoryV2, "setInventoryQuantity", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "deletionindicator":
                        invokeSetter(inventoryV2, "setDeletionIndicator", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                }
            }
        } catch (Exception e) {
            log.info("Inventory Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }

    //===========MT Inventory=========================================
    public List<InventoryV2> inventoryReadExcelFile(String companyCodeId, String plantId, String languageID, String warehouseId, String loginUserId, MultipartFile file) throws IOException {
        List<InventoryV2> inventoryV2List = new ArrayList<>();
        // Create Workbook for Excel file
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        //Get the header row first row
        Row headerRow = sheet.getRow(0);
        // Map column names their corresponding index
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            columnIndexMap.put(cell.getStringCellValue().toLowerCase().trim(), cell.getColumnIndex());
        }

        // Iterate through rows (skip the header row)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {

                InventoryV2 inventory = new InventoryV2();
                setFieldByColumnNameInv(inventory, row, columnIndexMap);
                inventory.setCompanyCodeId(companyCodeId);
                inventory.setPlantId(plantId);
                inventory.setLanguageId(languageID);
                inventory.setWarehouseId(warehouseId);
                inventory.setCreatedBy(loginUserId);
                inventoryV2List.add(inventory);
            }
        }

        // Close the workbook to free resource
        workbook.close();
        return inventoryV2List;
    }

    public void setFieldByColumnNameInv(InventoryV2 inventoryV2, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "palletcode":
                        invokeSetter(inventoryV2, "setPalletCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "casecode":
                        invokeSetter(inventoryV2, "setCaseCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemcode":
                        invokeSetter(inventoryV2, "setItemCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "packbarcodes":
                        invokeSetter(inventoryV2, "setPackBarcodes", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "variantsubcode":
                        invokeSetter(inventoryV2, "setVariantSubCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "batchserialnumber":
                        invokeSetter(inventoryV2, "setBatchSerialNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storagebin":
                        invokeSetter(inventoryV2, "setStorageBin", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storagemethod":
                        invokeSetter(inventoryV2, "setStorageMethod", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "description":
                        invokeSetter(inventoryV2, "setDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "inventoryuom":
                        invokeSetter(inventoryV2, "setInventoryUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturercode":
                        invokeSetter(inventoryV2, "setManufacturerCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "barcodeid":
                        invokeSetter(inventoryV2, "setBarcodeId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "cbm":
                        invokeSetter(inventoryV2, "setCbm", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "cbmunit":
                        invokeSetter(inventoryV2, "setCbmUnit", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "cbmperquantity":
                        invokeSetter(inventoryV2, "setCbmPerQuantity", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturername":
                        invokeSetter(inventoryV2, "setManufacturerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "origin":
                        invokeSetter(inventoryV2, "setOrigin", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "brand":
                        invokeSetter(inventoryV2, "setBrand", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencedocumentno":
                        invokeSetter(inventoryV2, "setReferenceDocumentNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "levelid":
                        invokeSetter(inventoryV2, "setLevelId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "stocktypedescription":
                        invokeSetter(inventoryV2, "setStockTypeDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield1":
                        invokeSetter(inventoryV2, "setReferenceField1", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield2":
                        invokeSetter(inventoryV2, "setReferenceField2", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield3":
                        invokeSetter(inventoryV2, "setReferenceField3", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield4":
                        invokeSetter(inventoryV2, "setReferenceField4", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "referencefield5":
                        invokeSetter(inventoryV2, "setReferenceField5", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield6":
                        invokeSetter(inventoryV2, "setReferenceField6", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield7":
                        invokeSetter(inventoryV2, "setReferenceField7", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield8":
                        invokeSetter(inventoryV2, "setReferenceField8", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield9":
                        invokeSetter(inventoryV2, "setReferenceField9", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield10":
                        invokeSetter(inventoryV2, "setReferenceField10", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "variantcode":
                        invokeSetter(inventoryV2, "setVariantCode", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "specialstockindicatorid":
                        invokeSetter(inventoryV2, "setSpecialStockIndicatorId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "storagetypeid":
                        invokeSetter(inventoryV2, "setStorageTypeId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "binclassid":
                        invokeSetter(inventoryV2, "setBinClassId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "stocktypeid":
                        invokeSetter(inventoryV2, "setStockTypeId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "allocatedquantity":
                        invokeSetter(inventoryV2, "setAllocatedQuantity", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "inventoryquantity":
                        invokeSetter(inventoryV2, "setInventoryQuantity", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "deletionindicator":
                        invokeSetter(inventoryV2, "setDeletionIndicator", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "manufacturerdate":
                        invokeSetter(inventoryV2, "setManufacturerDate", cell != null ? getCellValueAsDate1(cell) : null);
                        break;
                    case "expirydate":
                        invokeSetter(inventoryV2, "setExpiryDate", cell != null ? getCellValueAsDate1(cell) : null);
                        break;

                }
            }
        } catch (Exception e) {
            log.info("Inventory Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }

    //========================SPAREX===================================================
    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param orderTypeId
     * @param loginUserId
     * @param file
     * @return
     * @throws IOException
     */
    public List<InboundOrderProcessV4> inboundReadExcelFileV10(String companyCodeId, String plantId, String languageId, String warehouseId, Long orderTypeId, String loginUserId, MultipartFile file) throws IOException {
        List<InboundOrderProcessV4> inboundOrderList = new ArrayList<>();
        // Create Workbook for Excel file
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        //Get the header row first row
        Row headerRow = sheet.getRow(0);
        // Map column names their corresponding index
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            columnIndexMap.put(cell.getStringCellValue().toLowerCase().trim(), cell.getColumnIndex());
        }

        // Iterate through rows (skip the header row)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                InboundOrderProcessV4 inboundOrderProcess = new InboundOrderProcessV4();

                // Set the fields dynamically based on column name
                setFieldByColumnNameV10(inboundOrderProcess, row, columnIndexMap);
                inboundOrderProcess.setCompanyCode(companyCodeId);
                inboundOrderProcess.setToCompanyCode(companyCodeId);
                inboundOrderProcess.setBranchCode(plantId);
                inboundOrderProcess.setToBranchCode(plantId);
                inboundOrderProcess.setLanguageId(languageId);
                inboundOrderProcess.setWarehouseId(warehouseId);
                inboundOrderProcess.setInboundOrderTypeId(orderTypeId);
                inboundOrderProcess.setLoginUserId(loginUserId);

                // Add the mapped delivery object to the list
                inboundOrderList.add(inboundOrderProcess);
            }
        }

        // Close the workbook to free resource
        workbook.close();
        return inboundOrderList;
    }
    //==================SPAREX====================================
    /**
     *
     * @param inboundOrderProcess
     * @param row
     * @param columnIntexMap
     */
    public void setFieldByColumnNameV10(InboundOrderProcessV4 inboundOrderProcess, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "inbound" :
                    case "asnnumber":
                        invokeSetter(inboundOrderProcess, "setAsnNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemcode":
                        invokeSetter(inboundOrderProcess, "setSku", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemtext":
                        invokeSetter(inboundOrderProcess, "setSkuDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "suppliername":
                        invokeSetter(inboundOrderProcess, "setSupplierName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "linereference":
                        invokeSetter(inboundOrderProcess, "setLineReference", getCellValueAsLong(cell));
                        break;
                    case "quantity":
                        invokeSetter(inboundOrderProcess, "setOrderQty", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "expecteddate":
                        invokeSetter(inboundOrderProcess, "setExpectedDate", getCellValueAsDate(cell));
                        break;
                }
            }
        } catch (Exception e) {
            log.info("inboundOrderProcess Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }



    //===================SPAREX========================================================
    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param orderTypeId
     * @param loginUserId
     * @param file
     * @return
     * @throws IOException
     */
    public List<OutboundOrderProcessV4> outboundReadExcelFileV10(String companyCodeId, String plantId, String languageId,
                                                                 String warehouseId, Long orderTypeId, String loginUserId, MultipartFile file) throws IOException {
        List<OutboundOrderProcessV4> outboundOrderList = new ArrayList<>();
        // Create Workbook for Excel file
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        //Get the header row first row
        Row headerRow = sheet.getRow(0);
        // Map column names their corresponding index
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            columnIndexMap.put(cell.getStringCellValue().toLowerCase().trim(), cell.getColumnIndex());
        }

        // Iterate through rows (skip the header row)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                OutboundOrderProcessV4 outboundOrderProcess = new OutboundOrderProcessV4();

                // Set the fields dynamically based on column name
                setOutboundFieldByColumnNameV10(outboundOrderProcess, row, columnIndexMap);
                outboundOrderProcess.setCompanyCode(companyCodeId);
                outboundOrderProcess.setToCompanyCode(companyCodeId);
                outboundOrderProcess.setBranchCode(plantId);
                outboundOrderProcess.setToBranchCode(plantId);
                outboundOrderProcess.setLanguageId(languageId);
                outboundOrderProcess.setWarehouseId(warehouseId);
                outboundOrderProcess.setOrderType(String.valueOf(orderTypeId));
                outboundOrderProcess.setLoginUserId(loginUserId);


                // Add the mapped delivery object to the list
                outboundOrderList.add(outboundOrderProcess);
            }
        }

        // Close the workbook to free resource
        workbook.close();
        return outboundOrderList;
    }


    //========================SPAREX========================================================
    /**
     * @param outboundOrderProcess
     * @param row
     * @param columnIntexMap
     */
    public void setOutboundFieldByColumnNameV10(OutboundOrderProcessV4 outboundOrderProcess, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "outbound":
                    case "salesordernumber":
                        invokeSetter(outboundOrderProcess, "setSalesOrderNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemcode":
                        invokeSetter(outboundOrderProcess, "setSku", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemtext":
                        invokeSetter(outboundOrderProcess, "setSkuDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "linereference":
                        invokeSetter(outboundOrderProcess, "setLineReference", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "quantity":
                        invokeSetter(outboundOrderProcess, "setOrderedQty", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "requireddeliverydate":
                        invokeSetter(outboundOrderProcess, "setRequiredDeliveryDate", cell != null ? getCellValueAsDate(cell) : null);
                        break;
                    case "customername":
                        invokeSetter(outboundOrderProcess, "setCustomerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                }
            }
        } catch (Exception e) {
            log.info("outboundOrderProcess Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }

    //====================================================BF====================================================
    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param orderTypeId
     * @param loginUserId
     * @param file
     * @return
     * @throws IOException
     */
    public List<OutboundOrderProcessV4> outboundReadExcelFileV9(String companyCodeId, String plantId, String languageId,
                                                                String warehouseId, Long orderTypeId, String loginUserId, MultipartFile file) throws IOException {

        List<OutboundOrderProcessV4> outboundOrderList = new ArrayList<>();
        // Create Workbook for Excel file
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        //Get the header row first row
        Row headerRow = sheet.getRow(0);
        // Map column names their corresponding index
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            columnIndexMap.put(cell.getStringCellValue().toLowerCase().trim(), cell.getColumnIndex());
        }

        // Iterate through rows (skip the header row)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                OutboundOrderProcessV4 outboundOrderProcess = new OutboundOrderProcessV4();

                // Set the fields dynamically based on column name
                setOutboundFieldByColumnNameV9(outboundOrderProcess, row, columnIndexMap);
                outboundOrderProcess.setCompanyCode(companyCodeId);
                outboundOrderProcess.setToCompanyCode(companyCodeId);
                outboundOrderProcess.setBranchCode(plantId);
                outboundOrderProcess.setToBranchCode(plantId);
                outboundOrderProcess.setLanguageId(languageId);
                outboundOrderProcess.setWarehouseId(warehouseId);
                outboundOrderProcess.setOrderType(String.valueOf(orderTypeId));
                outboundOrderProcess.setLoginUserId(loginUserId);

                // Add the mapped delivery object to the list
                outboundOrderList.add(outboundOrderProcess);
            }
        }

        // Close the workbook to free resource
        workbook.close();
        return outboundOrderList;
    }

    //===========================================================BF==================================================
    /**
     *
     * @param outboundOrderProcess
     * @param row
     * @param columnIntexMap
     */
    public void setOutboundFieldByColumnNameV9(OutboundOrderProcessV4 outboundOrderProcess, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "outbound":
                        invokeSetter(outboundOrderProcess, "setPickListNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itm":
                        invokeSetter(outboundOrderProcess, "setItm", cell != null ? getCellValueAsLong(cell) : null);
                        invokeSetter(outboundOrderProcess, "setLineReference", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "customercode":
                        invokeSetter(outboundOrderProcess, "setCustomerId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "customer":
                        invokeSetter(outboundOrderProcess, "setCustomerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "sourcecompanycode":
                    case "fromcompanycode":
                        invokeSetter(outboundOrderProcess, "setFromCompanyCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "sourcebranchcode":
                    case "frombranchcode":
                        invokeSetter(outboundOrderProcess, "setFromBranchCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "tocompanycode":
                        invokeSetter(outboundOrderProcess, "setToCompanyCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "tobranchcode":
                        invokeSetter(outboundOrderProcess, "setToBranchCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "shiptocode":
                        invokeSetter(outboundOrderProcess, "setShipToCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "shiptoparty":
                        invokeSetter(outboundOrderProcess, "setShipToParty", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "material":
                        invokeSetter(outboundOrderProcess, "setMaterialNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "pricesegment":
                        invokeSetter(outboundOrderProcess, "setPriceSegment", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "qty":
                        invokeSetter(outboundOrderProcess, "setOrderedQty", getCellValueAsDouble(cell));
                        break;
                    case "specialstock":
                        invokeSetter(outboundOrderProcess, "setSpecialStock", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "mtonumber":
                        invokeSetter(outboundOrderProcess, "setMtoNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "skucode":
                    case "sku":
                        invokeSetter(outboundOrderProcess, "setSku", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "barcodeid":
                        invokeSetter(outboundOrderProcess, "setBarcodeId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "skutext":
                        invokeSetter(outboundOrderProcess, "setSkuDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "uom":
                        invokeSetter(outboundOrderProcess, "setUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "returnorderno":
                        invokeSetter(outboundOrderProcess, "setSalesOrderNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manifestreference":
                        invokeSetter(outboundOrderProcess, "setTokenNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                }
            }
        } catch (Exception e) {
            log.info("outboundOrderProcess Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }


}
