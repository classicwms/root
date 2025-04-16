package com.tekclover.wms.core.service;

import com.tekclover.wms.core.model.warehouse.inbound.almailem.InboundOrderProcessV4;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ExcelDataProcessService {

    //==================================================IMPEX - V4=====INBOUND===============================================================
    /**
     * Inbound
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
    public List<InboundOrderProcessV4> inboundReadExcelFile(String companyCodeId, String plantId, String languageId,
                                                       String warehouseId, Long orderTypeId, String loginUserId, MultipartFile file) throws IOException {

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

    /**
     *
     * @param inboundOrderProcess
     * @param row
     * @param columnIntexMap
     */
    public void setFieldByColumnName(InboundOrderProcessV4 inboundOrderProcess, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey();
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "asnnumber":
                        invokeSetter(inboundOrderProcess, "setAsnNumber", cell != null ? getCellValueAsString(cell) : null);
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
                    case "sku":
                        invokeSetter(inboundOrderProcess, "setSku", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "skudescription":
                        invokeSetter(inboundOrderProcess, "setSkuDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "expectedqty":
                        invokeSetter(inboundOrderProcess, "setExpectedQty", getCellValueAsDouble(cell));
                        break;
                    case "qty":
                        invokeSetter(inboundOrderProcess, "setExpectedQty", getCellValueAsDouble(cell));
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
                    case "gender":
                        invokeSetter(inboundOrderProcess, "setGender", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "color":
                        invokeSetter(inboundOrderProcess, "setColor", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "size":
                        invokeSetter(inboundOrderProcess, "setSize", cell != null ? getCellValueAsString(cell) : null);
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
                }
            }
        } catch (Exception e) {
            log.info("inboundOrderProcess Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }

    //==================================================IMPEX - V4=====OUTBOUND===============================================================

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
    public List<OutboundOrderProcessV4> outboundReadExcelFile(String companyCodeId, String plantId, String languageId,
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
     * @param outboundOrderProcess
     * @param row
     * @param columnIntexMap
     */
    public void setOutboundFieldByColumnName(OutboundOrderProcessV4 outboundOrderProcess, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey();
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "salesordernumber":
                    case "salesorderno":
                        invokeSetter(outboundOrderProcess, "setSalesOrderNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "picklistnumber":
                    case "picklistno":
                        invokeSetter(outboundOrderProcess, "setPickListNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "returnordernumber":
                        invokeSetter(outboundOrderProcess, "setPoNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "refordernumber":
                        invokeSetter(outboundOrderProcess, "setPickListNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "requireddeliverydate":
                        invokeSetter(outboundOrderProcess, "setRequiredDeliveryDate", getCellValueAsDate(cell));
                        break;
                    case "date":
                        invokeSetter(outboundOrderProcess, "setRequiredDeliveryDate", getCellValueAsDate(cell));
                        break;
                    case "status":
                        invokeSetter(outboundOrderProcess, "setStatus", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "tokennumber":
                        invokeSetter(outboundOrderProcess, "setTokenNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "customerid":
                        invokeSetter(outboundOrderProcess, "setCustomerId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "customername":
                        invokeSetter(outboundOrderProcess, "setCustomerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "transferordernumber":
                        invokeSetter(outboundOrderProcess, "setTransferOrderNumber", cell != null ? getCellValueAsString(cell) : null);
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
                    case "ponumber":
                        invokeSetter(outboundOrderProcess, "setPoNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "address":
                        invokeSetter(outboundOrderProcess, "setAddress", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "invoice":
                        invokeSetter(outboundOrderProcess, "setInvoice", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "supplierinvoiceno":
                        invokeSetter(outboundOrderProcess, "setSupplierInvoiceNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "linereference":
                    case "lineno":
                        invokeSetter(outboundOrderProcess, "setLineReference", getCellValueAsLong(cell));
                        break;
                    case "sku":
                        invokeSetter(outboundOrderProcess, "setSku", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "skudescription":
                        invokeSetter(outboundOrderProcess, "setSkuDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "orderedqty":
                    case "orderqty":
                        invokeSetter(outboundOrderProcess, "setOrderedQty", getCellValueAsDouble(cell));
                        break;
                    case "uom":
                        invokeSetter(outboundOrderProcess, "setUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturername":
                        invokeSetter(outboundOrderProcess, "setManufacturerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturercode":
                        invokeSetter(outboundOrderProcess, "setManufacturerCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storagesectionid":
                        invokeSetter(outboundOrderProcess, "setStorageSectionId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "returnqty":
                        invokeSetter(outboundOrderProcess, "setReturnQty", getCellValueAsDouble(cell));
                        break;
                    case "qty":
                        invokeSetter(outboundOrderProcess, "setReturnQty", getCellValueAsDouble(cell));
                        break;
                    case "expectedqty":
                        invokeSetter(outboundOrderProcess, "setExpectedQty", getCellValueAsDouble(cell));
                        break;
                    case "packqty":
                        invokeSetter(outboundOrderProcess, "setPackQty", getCellValueAsDouble(cell));
                        break;
                    case "origin":
                        invokeSetter(outboundOrderProcess, "setOrigin", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "brand":
                        invokeSetter(outboundOrderProcess, "setBrand", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "batchserialnumber":
                        invokeSetter(outboundOrderProcess, "setBatchSerialNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "barcodeid":
                        invokeSetter(outboundOrderProcess, "setBarcodeId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "gender":
                        invokeSetter(outboundOrderProcess, "setGender", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "color":
                        invokeSetter(outboundOrderProcess, "setColor", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "size":
                        invokeSetter(outboundOrderProcess, "setSize", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemtype":
                        invokeSetter(outboundOrderProcess, "setItemType", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemgroup":
                        invokeSetter(outboundOrderProcess, "setItemGroup", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storeid":
                        invokeSetter(outboundOrderProcess, "setStoreId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "suppliername":
                        invokeSetter(outboundOrderProcess, "setSupplierName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "alternateuom":
                        invokeSetter(outboundOrderProcess, "setAlternateUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "nobags":
                        invokeSetter(outboundOrderProcess, "setNoBags", getCellValueAsDouble(cell));
                        break;
                    case "bagsize":
                        invokeSetter(outboundOrderProcess, "setBagSize", getCellValueAsDouble(cell));
                        break;
                    case "mrp":
                        invokeSetter(outboundOrderProcess, "setMrp", getCellValueAsDouble(cell));
                        break;
                }
            }
        } catch (Exception e) {
            log.info("outboundOrderProcess Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }

    //========================================================================================================================================
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

//    private String getCellValueAsString(Cell cell) {
//        return cell != null && cell.getCellType() == CellType.STRING ? cell.getStringCellValue().trim() : null ;
//    }

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

    private Long getCellValueAsLong(Cell cell) {
        return cell != null && cell.getCellType() == CellType.NUMERIC ? (long) cell.getNumericCellValue() : null ;
    }

    private Double getCellValueAsDouble(Cell cell) {
        return cell != null && cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : 0.0;
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

    public static boolean isValidDate(String date) {
        // Define the regex pattern
        String regex = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        // Match the input date against the pattern
        return pattern.matcher(date).matches();
    }
}