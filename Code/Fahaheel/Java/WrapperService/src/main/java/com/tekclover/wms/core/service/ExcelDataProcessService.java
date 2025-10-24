package com.tekclover.wms.core.service;


import com.tekclover.wms.core.model.masters.ImPartner;
import com.tekclover.wms.core.model.masters.StorageBinV2;
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
                    case "manufacturername":
                        invokeSetter(outboundOrderProcess, "setManufacturerName", cell != null ? getCellValueAsString(cell) : null);
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

//-----------------------------------Bharath Package Inbound ReadExcelFile-------------------------------------------
//RM Inbound
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
 public List<InboundOrderProcessV4> inboundRMReadExcelFile(String companyCodeId, String plantId, String languageId, String warehouseId, Long orderTypeId, String loginUserId, MultipartFile file) throws IOException {
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
             setFieldByColumnNameRM(inboundOrderProcess, row, columnIndexMap);
             inboundOrderProcess.setCompanyCode(companyCodeId);
             inboundOrderProcess.setToCompanyCode(companyCodeId);
             inboundOrderProcess.setBranchCode(plantId);
             inboundOrderProcess.setToBranchCode(plantId);
             inboundOrderProcess.setLanguageId(languageId);
             inboundOrderProcess.setWarehouseId(warehouseId);
             inboundOrderProcess.setInboundOrderTypeId(orderTypeId);
             inboundOrderProcess.setLoginUserId(loginUserId);
//             inboundOrderProcess.setNoBags(2.0);

             // Add the mapped delivery object to the list
             inboundOrderList.add(inboundOrderProcess);
         }
     }

     // Close the workbook to free resource
     workbook.close();
     return inboundOrderList;
 }

 public void setFieldByColumnNameRM(InboundOrderProcessV4 inboundOrderProcess, Row row, Map<String, Integer> columnIntexMap) {
     try {
         for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

             String columnName = entry.getKey().replaceAll("\\s+", "");
             Integer columnIndex = entry.getValue();
             Cell cell = row.getCell(columnIndex);

             switch (columnName) {
                 case "ponumber":
                     invokeSetter(inboundOrderProcess, "setAsnNumber", cell != null ? getCellValueAsString(cell) : null);
                     break;
                 case "poamendmentno":
                     invokeSetter(inboundOrderProcess, "setPoAmendmentNo", cell != null ? getCellValueAsString(cell) : null);
                     break;
                 case "itemcode":
                     invokeSetter(inboundOrderProcess, "setSku", cell != null ? getCellValueAsString(cell) : null);
                     break;
                 case "qty":
                     invokeSetter(inboundOrderProcess, "setExpectedQty", getCellValueAsDouble(cell));
                     break;
                 case "suppliercode":
                     invokeSetter(inboundOrderProcess, "setSupplierCode", cell != null ? getCellValueAsString(cell) : null);
                     break;
                 case "suppliername":
                     invokeSetter(inboundOrderProcess, "setSupplierName", cell != null ? getCellValueAsString(cell) : null);
                     break;
                 case "poamendmentdate":
                     invokeSetter(inboundOrderProcess, "setExpectedDate", getCellValueAsDate(cell));
                     break;
                 case "podate":
                     invokeSetter(inboundOrderProcess, "setReceivedDate", getCellValueAsDate1(cell));
                     break;
                 case "unitofmeasure":
                     invokeSetter(inboundOrderProcess, "setUom", cell != null ? getCellValueAsString(cell) : null);
                     break;
                 case "itemdescription":
                     invokeSetter(inboundOrderProcess, "setSkuDescription", cell !=null ? getCellValueAsString(cell) : null);
                     break;
                 case "potype":
                     invokeSetter(inboundOrderProcess, "setItemType", cell !=null ? getCellValueAsString(cell) : null);
                     break;
                 case "billercode":
                     invokeSetter(inboundOrderProcess, "setBillerCode", cell !=null ? getCellValueAsString(cell) : null);
                     break;
                 case "billername":
                     invokeSetter(inboundOrderProcess, "setBillerName", cell !=null ? getCellValueAsString(cell) : null);
                     break;
             }
         }
     } catch (Exception e) {
         log.info("inboundOrderProcess Upload Field Set Failed <----------------------------->" + e.getMessage());
     }
 }

    //FG Inbound
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
    public List<InboundOrderProcessV4> inboundFGReadExcelFile(String companyCodeId, String plantId, String languageId, String warehouseId, Long orderTypeId, String loginUserId, MultipartFile file) throws IOException {
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
                setFieldByColumnNameFG(inboundOrderProcess, row, columnIndexMap);
                inboundOrderProcess.setCompanyCode(companyCodeId);
                inboundOrderProcess.setToCompanyCode(companyCodeId);
                inboundOrderProcess.setBranchCode(plantId);
                inboundOrderProcess.setToBranchCode(plantId);
                inboundOrderProcess.setLanguageId(languageId);
                inboundOrderProcess.setWarehouseId(warehouseId);
                inboundOrderProcess.setInboundOrderTypeId(orderTypeId);
                inboundOrderProcess.setLoginUserId(loginUserId);
//             inboundOrderProcess.setNoBags(2.0);

                // Add the mapped delivery object to the list
                inboundOrderList.add(inboundOrderProcess);
            }
        }

        // Close the workbook to free resource
        workbook.close();
        return inboundOrderList;
    }

    public void setFieldByColumnNameFG(InboundOrderProcessV4 inboundOrderProcess, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {

                    case "workorderno":
                        invokeSetter(inboundOrderProcess, "setAsnNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "fgitemcode":
                        invokeSetter(inboundOrderProcess, "setSku", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "finishedqty":
                        invokeSetter(inboundOrderProcess, "setExpectedQty", getCellValueAsDouble(cell));
//                    case "workorderqty":
//                        invokeSetter(inboundOrderProcess, "setExpectedQtyInPieces", getCellValueAsDouble(cell));
//                        invokeSetter(inboundOrderProcess, "setExpectedQtyInCases", getCellValueAsDouble(cell));
//                        break;
                    case "fgreceiptdocumentnumber":
                        invokeSetter(inboundOrderProcess, "setSupplierCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
//                    case "fginwarddate":
//                        invokeSetter(inboundOrderProcess, "setExpectedDate", getCellValueAsDate(cell));
//                        break;
                    case "fginwarddate":
                        invokeSetter(inboundOrderProcess, "setReceivedDate", getCellValueAsDate1(cell));
                        break;
                    case "fgunitofmeasure":
                        invokeSetter(inboundOrderProcess, "setUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "fgitemdescription":
                        invokeSetter(inboundOrderProcess, "setSkuDescription", cell !=null ? getCellValueAsString(cell) : null);
                        break;
                }
            }
        } catch (Exception e) {
            log.info("inboundOrderProcess Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }

//-----------------------------------Bharath Package Outbound ReadExcelFile---------------------------------------------
// RM Outbound
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
    public List<OutboundOrderProcessV4> outboundReadExcelFileRM(String companyCodeId, String plantId, String languageId,
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
                setOutboundFieldByColumnNameRM(outboundOrderProcess, row, columnIndexMap);
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
    public void setOutboundFieldByColumnNameRM(OutboundOrderProcessV4 outboundOrderProcess, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {

                    case "workorderno":
                        invokeSetter(outboundOrderProcess, "setSalesOrderNumber", cell != null ? getCellValueAsString(cell) : null);
                        invokeSetter(outboundOrderProcess, "setPickListNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "fgqty":
                        invokeSetter(outboundOrderProcess, "setOrderedQty", getCellValueAsDouble(cell));
                        break;
                    case "rmitemcode":
                        invokeSetter(outboundOrderProcess, "setSku", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "rmitemdescription":
                        invokeSetter(outboundOrderProcess, "setSkuDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "rmweightrequired":
                        invokeSetter(outboundOrderProcess, "setWeight", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "rmunitofmeasure":
                        invokeSetter(outboundOrderProcess, "setUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "rmstock":
                        invokeSetter(outboundOrderProcess, "setStock", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "departmentrequested":
                        invokeSetter(outboundOrderProcess, "setDepartment", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "fgitemcode":
                        invokeSetter(outboundOrderProcess, "setFgSku", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "fgitemdescription":
                        invokeSetter(outboundOrderProcess, "setFgSkuDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
//                    case "requireddate":
//                        invokeSetter(outboundOrderProcess,"setRequiredDeliveryDate", cell != null ? getCellValueAsDate(cell) : null);
//                        break;
                    case "requireddate":
                        invokeSetter(outboundOrderProcess,"setRequiredDeliveryDate", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "fgunitofmeasure":
                        invokeSetter(outboundOrderProcess, "setFgUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "noofreelsrequired":
                        invokeSetter(outboundOrderProcess, "setLineReference", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                }
            }
        } catch (Exception e) {
            log.info("outboundOrderProcess Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }

// FG Outbound
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
    public List<OutboundOrderProcessV4> outboundReadExcelFileFG(String companyCodeId, String plantId, String languageId,
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
                setOutboundFieldByColumnNameFG(outboundOrderProcess, row, columnIndexMap);
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
    public void setOutboundFieldByColumnNameFG(OutboundOrderProcessV4 outboundOrderProcess, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {

                    case "saleorderno":
                        invokeSetter(outboundOrderProcess, "setSalesOrderNumber", cell != null ? getCellValueAsString(cell) : null);
                        invokeSetter(outboundOrderProcess, "setPickListNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
//                    case "saleorderdate":
//                        invokeSetter(outboundOrderProcess,"setRequiredDeliveryDate", cell != null ? getCellValueAsDate(cell) : null);
//                        break;
                    case "customercode":
                        invokeSetter(outboundOrderProcess, "setCustomerId", cell != null ? getCellValueAsString(cell) : null);
                        invokeSetter(outboundOrderProcess, "setCustomerCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "customername":
                        invokeSetter(outboundOrderProcess, "setCustomerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "salerorderamendmentdate":
                        invokeSetter(outboundOrderProcess,"setSalerOrderAmendmentDate", cell != null ? getCellValueAsDate1(cell) : null);
                        break;
                    case "salerorderamendmentno":
                        invokeSetter(outboundOrderProcess,"setSalerOrderAmendmentNo", cell != null ? getCellValueAsDate(cell) : null);
                        break;
                    case "status":
                        invokeSetter(outboundOrderProcess, "setStatus", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "deliverydate":
                        invokeSetter(outboundOrderProcess,"setRequiredDeliveryDate", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "fgorderqty":
                        invokeSetter(outboundOrderProcess, "setOrderedQty", getCellValueAsDouble(cell));
                        break;
                    case "fgitemcode":
                        invokeSetter(outboundOrderProcess, "setSku", cell != null ? getCellValueAsString(cell) : null);
                        invokeSetter(outboundOrderProcess, "setFgSku", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "fgitemdescription":
                        invokeSetter(outboundOrderProcess, "setSkuDescription", cell != null ? getCellValueAsString(cell) : null);
                        invokeSetter(outboundOrderProcess, "setFgSkuDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
//                    case "custreqddate":
//                        invokeSetter(outboundOrderProcess,"setRequiredDeliveryDate", cell != null ? getCellValueAsString(cell) : null);
//                        break;
                    case "fgunitofmeasure":
                        invokeSetter(outboundOrderProcess, "setUom", cell != null ? getCellValueAsString(cell) : null);
                        invokeSetter(outboundOrderProcess, "setFgUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                }
            }
        } catch (Exception e) {
            log.info("outboundOrderProcess Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }

    public List<StorageBinV2> storageBinReadExcelFile(String companyCodeId, String plantId, String languageID, String warehouseId, String loginUserId, MultipartFile file) throws IOException {
        List<StorageBinV2> storageBinV2List = new ArrayList<>();
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

                StorageBinV2 storageBin = new StorageBinV2();
                setFieldByColumnNameSB(storageBin, row, columnIndexMap);
                storageBin.setCompanyCodeId(companyCodeId);
                storageBin.setPlantId(plantId);
                storageBin.setLanguageId(languageID);
                storageBin.setWarehouseId(warehouseId);
                storageBin.setCreatedBy(loginUserId);
                // Add the mapped delivery object to the list
                storageBinV2List.add(storageBin);
            }
        }

        // Close the workbook to free resource
        workbook.close();
        return storageBinV2List;
    }

    public void setFieldByColumnNameSB(StorageBinV2 storageBinV2, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {

                    case "storagebin" :
                        invokeSetter(storageBinV2, "setStorageBin", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storagesectionid" :
                        invokeSetter(storageBinV2, "setStorageSectionId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "rowid" :
                        invokeSetter(storageBinV2, "setRowId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "aislenumber" :
                        invokeSetter(storageBinV2, "setAisleNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "spanid" :
                        invokeSetter(storageBinV2, "setSpanId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "shelfid" :
                        invokeSetter(storageBinV2, "setShelfId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "description" :
                        invokeSetter(storageBinV2, "setDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "binbarcode" :
                        invokeSetter(storageBinV2, "setBinBarcode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "blockreason" :
                        invokeSetter(storageBinV2, "setBlockReason", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "occupiedvolume" :
                        invokeSetter(storageBinV2, "setOccupiedVolume", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "occupiedweight" :
                        invokeSetter(storageBinV2, "setOccupiedWeight", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "occupiedquantity" :
                        invokeSetter(storageBinV2, "setOccupiedQuantity", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "remainingvolume" :
                        invokeSetter(storageBinV2, "setRemainingVolume", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "remainingweight" :
                        invokeSetter(storageBinV2, "setRemainingWeight", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "remainingquantity" :
                        invokeSetter(storageBinV2, "setRemainingQuantity", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "totalvolume" :
                        invokeSetter(storageBinV2, "setTotalVolume", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "totalweight" :
                        invokeSetter(storageBinV2, "setTotalWeight", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "totalquantity" :
                        invokeSetter(storageBinV2, "setTotalQuantity", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "capacitycheck" :
                        invokeSetter(storageBinV2, "setCapacityCheck", cell != null ? getCellValueAsBoolean(cell) : null);
                        break;
                    case "statusid" :
                        invokeSetter(storageBinV2, "setStatusId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "allocatedvolume" :
                        invokeSetter(storageBinV2, "setAllocatedVolume", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "binclassid" :
                        invokeSetter(storageBinV2, "setBinClassId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "binsectionid" :
                        invokeSetter(storageBinV2, "setBinSectionId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "floorid" :
                        invokeSetter(storageBinV2, "setFloorId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "storagetypeid" :
                        invokeSetter(storageBinV2, "setStorageTypeId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "putawayblock" :
                        invokeSetter(storageBinV2, "setPutawayBlock", cell != null ? getCellValueAsInteger(cell) : null);
                        break;
                    case "pickingblock" :
                        invokeSetter(storageBinV2, "setPickingBlock", cell != null ? getCellValueAsInteger(cell) : null);
                        break;

                }
            }
        } catch (Exception e) {
            log.info("Storage Bin Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }


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
                        invokeSetter(imBasicData1, "setCapacityUom", getCellValueAsString(cell));
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
                        invokeSetter(imBasicData1, "setModel",  getCellValueAsString(cell) );
                        break;
                    case "specifications1":
                        invokeSetter(imBasicData1, "setSpecifications1",  getCellValueAsString(cell) );
                        break;
                    case "specifications2":
                        invokeSetter(imBasicData1, "setSpecifications2",  getCellValueAsString(cell) );
                        break;
                    case "eanupcno":
                        invokeSetter(imBasicData1, "setEanUpcNo",  getCellValueAsString(cell) );
                        break;
                    case "shelflifeindicator":
                        invokeSetter(imBasicData1, "setShelfLifeIndicator",  getCellValueAsBoolean(cell) );
                        break;
                    case "brand":
                        invokeSetter(imBasicData1, "setBrand",  getCellValueAsString(cell) );
                        break;
                    case "remarks":
                        invokeSetter(imBasicData1, "setRemarks",  getCellValueAsString(cell) );
                        break;
                    case "itemtype":
                        invokeSetter(imBasicData1, "setItemType",  getCellValueAsLong(cell) );
                        break;
                    case "itemgroup":
                        invokeSetter(imBasicData1, "setItemGroup",  getCellValueAsLong(cell) );
                        break;
                    case "subitemgroup":
                        invokeSetter(imBasicData1, "setSubItemGroup",  getCellValueAsLong(cell) );
                        break;
                    case "minimumstock":
                        invokeSetter(imBasicData1, "setMinimumStock",  getCellValueAsDouble(cell) );
                        break;
                    case "maximumstock":
                        invokeSetter(imBasicData1, "setMaximumStock", getCellValueAsDouble(cell));
                        break;
                    case "reorderlevel":
                        invokeSetter(imBasicData1, "setReorderLevel", getCellValueAsDouble(cell));
                        break;
                    case "replenishmentqty":
                        invokeSetter(imBasicData1, "setReplenishmentQty", getCellValueAsDouble(cell));
                        break;
                    case "safetystock":
                        invokeSetter(imBasicData1, "setSafetyStock", getCellValueAsDouble(cell));
                        break;
                    case "weight":
                        invokeSetter(imBasicData1, "setWeight",  getCellValueAsDouble(cell) );
                        break;
                    case "statusid":
                        invokeSetter(imBasicData1, "setStatusId",  getCellValueAsLong(cell) );
                        break;
                    case "length":
                        invokeSetter(imBasicData1, "setLength",  getCellValueAsDouble(cell) );
                        break;
                    case "width":
                        invokeSetter(imBasicData1, "setWidth",  getCellValueAsDouble(cell) );
                        break;
                    case "height":
                        invokeSetter(imBasicData1, "setHeight",  getCellValueAsDouble(cell) );
                        break;
                    case "volume":
                        invokeSetter(imBasicData1, "setVolume",  getCellValueAsDouble(cell) );
                        break;
                }
            }
        } catch (Exception e) {
            log.info("imBasicData1 Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }

    //------------------------------------------Validation int-boolean------------------------------------------//

    private Integer getCellValueAsInteger(Cell cell) {
        return cell != null && cell.getCellType() == CellType.NUMERIC ? (int) cell.getNumericCellValue() : null;
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

        //=========================================Inventory=========================================
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
                // Add the mapped delivery object to the list
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

                    case "palletcode" :
                        invokeSetter(inventoryV2, "setPalletCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "casecode" :
                        invokeSetter(inventoryV2, "setCaseCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemcode" :
                        invokeSetter(inventoryV2, "setItemCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "packbarcodes" :
                        invokeSetter(inventoryV2, "setPackBarcodes", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "variantsubcode" :
                        invokeSetter(inventoryV2, "setVariantSubCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "batchserialnumber" :
                        invokeSetter(inventoryV2, "setBatchSerialNumber", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storagebin" :
                        invokeSetter(inventoryV2, "setStorageBin", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "storagemethod" :
                        invokeSetter(inventoryV2, "setStorageMethod", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "description" :
                        invokeSetter(inventoryV2, "setDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "inventoryuom" :
                        invokeSetter(inventoryV2, "setInventoryUom", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturercode" :
                        invokeSetter(inventoryV2, "setManufacturerCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "barcodeid" :
                        invokeSetter(inventoryV2, "setBarcodeId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "cbm" :
                        invokeSetter(inventoryV2, "setCbm", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "cbmunit" :
                        invokeSetter(inventoryV2, "setCbmUnit", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "cbmperquantity" :
                        invokeSetter(inventoryV2, "setCbmPerQuantity", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturername" :
                        invokeSetter(inventoryV2, "setManufacturerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "origin" :
                        invokeSetter(inventoryV2, "setOrigin", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "brand" :
                        invokeSetter(inventoryV2, "setBrand", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencedocumentno" :
                        invokeSetter(inventoryV2, "setReferenceDocumentNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "levelid" :
                        invokeSetter(inventoryV2, "setLevelId", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "stocktypedescription" :
                        invokeSetter(inventoryV2, "setStockTypeDescription", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield1" :
                        invokeSetter(inventoryV2, "setReferenceField1", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield2" :
                        invokeSetter(inventoryV2, "setReferenceField2", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield3" :
                        invokeSetter(inventoryV2, "setReferenceField3", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield4" :
                        invokeSetter(inventoryV2, "setReferenceField4", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "referencefield5" :
                        invokeSetter(inventoryV2, "setReferenceField5", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield6" :
                        invokeSetter(inventoryV2, "setReferenceField6", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield7" :
                        invokeSetter(inventoryV2, "setReferenceField7", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield8" :
                        invokeSetter(inventoryV2, "setReferenceField8", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield9" :
                        invokeSetter(inventoryV2, "setReferenceField9", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "referencefield10" :
                        invokeSetter(inventoryV2, "setReferenceField10", cell != null ? getCellValueAsString(cell) : null);
                        break;

                    case "variantcode" :
                        invokeSetter(inventoryV2, "setVariantCode", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "specialstockindicatorid" :
                        invokeSetter(inventoryV2, "setSpecialStockIndicatorId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "storagetypeid" :
                        invokeSetter(inventoryV2, "setStorageTypeId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "binclassid" :
                        invokeSetter(inventoryV2, "setBinClassId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "stocktypeid" :
                        invokeSetter(inventoryV2, "setStockTypeId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                    case "allocatedquantity" :
                        invokeSetter(inventoryV2, "setAllocatedQuantity", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "inventoryquantity" :
                        invokeSetter(inventoryV2, "setInventoryQuantity", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "deletionindicator" :
                        invokeSetter(inventoryV2, "setDeletionIndicator", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                }
            }
        } catch (Exception e) {
            log.info("Inventory Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }

    //===============================================ImPartner===========================================

    public List<ImPartner> imPartnerReadExcelFile(String companyCodeId, String plantId, String languageID, String warehouseId, String loginUserId, MultipartFile file) throws IOException {
        List<ImPartner> imPartnerList = new ArrayList<>();
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

                ImPartner imPartner = new ImPartner();
                setFieldByColumnNameImp(imPartner, row, columnIndexMap);
                imPartner.setCompanyCodeId(companyCodeId);
                imPartner.setPlantId(plantId);
                imPartner.setLanguageId(languageID);
                imPartner.setWarehouseId(warehouseId);
                imPartner.setCreatedBy(loginUserId);
                // Add the mapped delivery object to the list
                imPartnerList.add(imPartner);
            }
        }

        // Close the workbook to free resource
        workbook.close();
        return imPartnerList;
    }
    public void setFieldByColumnNameImp(ImPartner imPartner, Row row, Map<String, Integer> columnIntexMap) {
        try {
            for (Map.Entry<String, Integer> entry : columnIntexMap.entrySet()) {

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {

                    case "businesspartnercode" :
                        invokeSetter(imPartner, "setBusinessPartnerCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "itemcode" :
                        invokeSetter(imPartner, "setItemCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "businesspartnertype" :
                        invokeSetter(imPartner, "setBusinessPartnerType", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "partneritembarcode" :
                        invokeSetter(imPartner, "setPartnerItemBarcode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturercode" :
                        invokeSetter(imPartner, "setManufacturerCode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "manufacturername" :
                        invokeSetter(imPartner, "setManufacturerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "brandname" :
                        invokeSetter(imPartner, "setBrandName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "partnername" :
                        invokeSetter(imPartner, "setPartnerName", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "partneritemno" :
                        invokeSetter(imPartner, "setPartnerItemNo", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "vendoritembarcode" :
                        invokeSetter(imPartner, "setVendorItemBarcode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "mfrbarcode" :
                        invokeSetter(imPartner, "setMfrBarcode", cell != null ? getCellValueAsString(cell) : null);
                        break;
                    case "stockuom" :
                        invokeSetter(imPartner, "setStockUom", cell != null ? getCellValueAsString(cell) : null);
                        break;

                    case "stock" :
                        invokeSetter(imPartner, "setStock", cell != null ? getCellValueAsDouble(cell) : null);
                        break;
                    case "statusid" :
                        invokeSetter(imPartner, "setStatusId", cell != null ? getCellValueAsLong(cell) : null);
                        break;
                }
            }
        } catch (Exception e) {
            log.info("Inventory Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }

}
