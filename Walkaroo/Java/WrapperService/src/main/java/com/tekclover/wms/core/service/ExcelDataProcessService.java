package com.tekclover.wms.core.service;

import com.tekclover.wms.core.model.IKeyValuePair;
import com.tekclover.wms.core.model.dto.AsnList;
import com.tekclover.wms.core.model.dto.SalesOrderV3;
import com.tekclover.wms.core.model.warehouse.inbound.almailem.InboundOrderProcessV4;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.OutboundOrderProcessV4;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.SalesOrderV2;
import com.tekclover.wms.core.repository.UserRepository;
import com.tekclover.wms.core.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    UserRepository userRepository;

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

                String columnName = entry.getKey().replaceAll("\\s+", "");
                Integer columnIndex = entry.getValue();
                Cell cell = row.getCell(columnIndex);

                switch (columnName) {
                    case "inbound":
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
                        invokeSetter(inboundOrderProcess, "setExpectedQty", getCellValueAsDouble(cell));
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
                }
            }
        } catch (Exception e) {
            log.info("outboundOrderProcess Upload Field Set Failed <----------------------------->" + e.getMessage());
        }
    }

    /*--------------------------------------------ExcelMethods---------------------------------------*/

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

    /**
     * 
     * @param asnv2
     * @return
     */
    public List<InboundOrderProcessV4> inboundDataSet(List<AsnList> asnv2) {
        List<InboundOrderProcessV4> inboundOrderList = new ArrayList<>();

        asnv2.forEach(asn->{
            IKeyValuePair iKeyValuePair = userRepository.getCompanyAndPlant(asn.getPlant());
            log.info("company -> {}",iKeyValuePair.getCompanyCode());
            log.info("lang -> {}",iKeyValuePair.getLanguage());
            log.info("warehouse -> {}",iKeyValuePair.getWarehouse());
            InboundOrderProcessV4 inboundOrderProcess = new InboundOrderProcessV4();
            inboundOrderProcess.setAsnNumber(asn.getInbound());
            String huSerial = asn.getHuSerialNumber();
            if (huSerial != null && huSerial.startsWith("000")) {
                huSerial = huSerial.substring(3);
            }
            inboundOrderProcess.setBarcodeId(huSerial);
            inboundOrderProcess.setMaterialNo(asn.getMaterial());
            inboundOrderProcess.setPriceSegment(asn.getPriceSegment());
            inboundOrderProcess.setPlant(asn.getPlant());
            inboundOrderProcess.setStorageLocation(asn.getStorageLocation());
            inboundOrderProcess.setSku(asn.getSkuCode());
            inboundOrderProcess.setArticleNo(asn.getArticleNumber());
            inboundOrderProcess.setNoPairs(asn.getNoOfPairs());
            inboundOrderProcess.setGender(asn.getGender());
            inboundOrderProcess.setColor(asn.getColor());
            inboundOrderProcess.setSize(asn.getSize());
            inboundOrderProcess.setExpectedQty(asn.getQty());
            inboundOrderProcess.setCompanyCode(iKeyValuePair.getCompanyCode());
            inboundOrderProcess.setToCompanyCode(iKeyValuePair.getCompanyCode());
            inboundOrderProcess.setBranchCode(asn.getPlant());
            inboundOrderProcess.setToBranchCode(asn.getPlant());
            inboundOrderProcess.setLanguageId(iKeyValuePair.getLanguage());
            inboundOrderProcess.setWarehouseId(iKeyValuePair.getWarehouse());
            inboundOrderProcess.setInboundOrderTypeId(1L);
            inboundOrderProcess.setLoginUserId("ADMIN");
            if (asn.getReversalFlag() != null) {
                inboundOrderProcess.setReversalFlag(asn.getReversalFlag());
            }
            inboundOrderProcess.setMtoNumber(asn.getMtoNumber());

            inboundOrderList.add(inboundOrderProcess);
        });
        log.info("SAP inboundOrderList -------------> {}", inboundOrderList);

     return inboundOrderList;

    }

    /**
     * 
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param inboundOrderTypeId
     * @param loginUserID
     * @param asnv2
     * @return
     */
    public List<InboundOrderProcessV4> inboundDataSet1(String companyCodeId, String plantId, String languageId, String warehouseId, Long inboundOrderTypeId, String loginUserID, List<AsnList> asnv2) {
        List<InboundOrderProcessV4> inboundOrderList = new ArrayList<>();

        asnv2.forEach(asn->{
            InboundOrderProcessV4 inboundOrderProcess = new InboundOrderProcessV4();
            inboundOrderProcess.setAsnNumber(asn.getInbound());
            inboundOrderProcess.setBarcodeId(asn.getHuSerialNumber());
            inboundOrderProcess.setMaterialNo(asn.getMaterial());
            inboundOrderProcess.setPriceSegment(asn.getPriceSegment());
            inboundOrderProcess.setPlant(asn.getPlant());
            inboundOrderProcess.setStorageLocation(asn.getStorageLocation());
            inboundOrderProcess.setSku(asn.getSkuCode());
            inboundOrderProcess.setArticleNo(asn.getArticleNumber());
            inboundOrderProcess.setNoPairs(asn.getNoOfPairs());
            inboundOrderProcess.setGender(asn.getGender());
            inboundOrderProcess.setColor(asn.getColor());
            inboundOrderProcess.setSize(asn.getSize());
            inboundOrderProcess.setExpectedQty(asn.getQty());
            inboundOrderProcess.setCompanyCode(companyCodeId);
            inboundOrderProcess.setToCompanyCode(companyCodeId);
            inboundOrderProcess.setBranchCode(plantId);
            inboundOrderProcess.setToBranchCode(plantId);
            inboundOrderProcess.setLanguageId(languageId);
            inboundOrderProcess.setWarehouseId(warehouseId);
            inboundOrderProcess.setInboundOrderTypeId(inboundOrderTypeId);
            inboundOrderProcess.setLoginUserId(loginUserID);

            inboundOrderList.add(inboundOrderProcess);
        });

        return inboundOrderList;
    }
    
    /**
     * 
     * @param salesOrderList
     * @return
     */
	public List<OutboundOrderProcessV4> populateOutboundOrderProcessV4 (List<SalesOrderV3> salesOrderList) {
		log.info("SAP---outbound----received---orders-----1----> {}", salesOrderList);
		List<OutboundOrderProcessV4> outboundOrderList = new ArrayList<>();
		salesOrderList.forEach(so -> {
			IKeyValuePair iKeyValuePair = userRepository.getCompanyAndPlant(so.getBranchCode());
			log.info("company -> {}", iKeyValuePair.getCompanyCode());
			log.info("lang -> {}", iKeyValuePair.getLanguage());
			log.info("warehouse -> {}", iKeyValuePair.getWarehouse());
			
			OutboundOrderProcessV4 obOrderProcessV4 = new OutboundOrderProcessV4();
			obOrderProcessV4.setPickListNumber(so.getSalesOrderNumber());
			obOrderProcessV4.setLanguageId(iKeyValuePair.getLanguage());
			obOrderProcessV4.setCompanyCode(iKeyValuePair.getCompanyCode());
			obOrderProcessV4.setBranchCode(so.getBranchCode());
			obOrderProcessV4.setWarehouseId(iKeyValuePair.getWarehouse());
			obOrderProcessV4.setItm(so.getItm());      
			obOrderProcessV4.setCustomerCode(so.getCustomerCode());
			obOrderProcessV4.setCustomer(so.getCustomer());
			obOrderProcessV4.setShipToCode(so.getShipToCode());
			obOrderProcessV4.setShipToParty(so.getShipToParty());
			obOrderProcessV4.setPriceSegment(so.getPriceSegment());
			obOrderProcessV4.setQty(so.getQty());
			obOrderProcessV4.setSpecialStock(so.getSpecialStock());
			obOrderProcessV4.setMtoNumber(so.getMtoNumber());
			obOrderProcessV4.setSkuCode(so.getSkuCode());
			obOrderProcessV4.setMaterialNo(so.getMaterialNo());
			obOrderProcessV4.setPairQty(so.getPairQty());
			obOrderProcessV4.setLineReference(so.getLineReference());
			obOrderProcessV4.setOrderedQty(so.getQty());
			
            // Add the mapped delivery object to the list
            outboundOrderList.add(obOrderProcessV4);
		});
		log.info("SAP outbound----OrderList -------------> {}", outboundOrderList);
		return outboundOrderList;
    }
}
