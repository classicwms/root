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

}
