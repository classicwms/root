package com.tekclover.wms.api.inbound.transaction.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.errorlog.FindErrorLog;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.AddGrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.transaction.repository.ErrorLogRepository;
import com.tekclover.wms.api.inbound.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.inbound.transaction.model.errorlog.ErrorLog;
import com.tekclover.wms.api.inbound.transaction.repository.specification.ErrorLogSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class ErrorLogService {

    @Autowired
    ErrorLogRepository exceptionLogRepo;

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    ErrorLogRepository errorLogRepository;

    private Path fileStorageLocation = null;

    // Get All Exception Log Details
    public List<ErrorLog> getAllExceptionLogs() {
        List<ErrorLog> errorLogList = exceptionLogRepo.findAll();
        return errorLogList;
    }

    /**
     *
     * @param errorLogList
     * @throws IOException
     */
    public void writeLog(List<ErrorLog> errorLogList) throws IOException {
        this.fileStorageLocation = Paths.get(propertiesConfig.getErrorlogFolderName()).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new BadRequestException(
                        "Could not create the directory where the uploaded files will be stored.");
            }
        }
        log.info("loca : " + fileStorageLocation);

        FileWriter fout = new FileWriter(this.fileStorageLocation + "\\error_log.csv", true);

        //using custom delimiter and quote character
        CSVWriter csvWriter = new CSVWriter(fout, ',', '\'');

        List<ErrorLog> logList = parseCSVWithHeader(errorLogList);
        List<String[]> data = toStringArray(logList);
        csvWriter.writeAll(data);
        csvWriter.close();

        reader();
    }

    /**
     *
     * @param errorLogList
     * @return
     * @throws IOException
     */
    public List<ErrorLog> parseCSVWithHeader(List<ErrorLog> errorLogList) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(this.fileStorageLocation + "\\error_log.csv"), ',');

        HeaderColumnNameMappingStrategy<ErrorLog> beanStrategy = new HeaderColumnNameMappingStrategy<ErrorLog>();
        beanStrategy.setType(ErrorLog.class);

        CsvToBean<ErrorLog> csvToBean = new CsvToBean<ErrorLog>();
        List<ErrorLog> logs = csvToBean.parse(beanStrategy, reader);
        logs.addAll(errorLogList);
        reader.close();

        return logs;
    }

    /**
     *
     * @param els
     * @return
     */
    private static List<String[]> toStringArray(List<ErrorLog> els) {
        List<String[]> records = new ArrayList<String[]>();

        // adding header record
        records.add(new String[] { "orderId", "orderTypeId", "orderDate", "errorMessage", "languageId", "companyCode", "plantId", "warehouseId", "refDocNumber"});

        Iterator<ErrorLog> it = els.iterator();
        while (it.hasNext()) {
            ErrorLog elog = it.next();
            records.add(new String[] {
                    String.valueOf(elog.getOrderId()),
                    elog.getOrderTypeId(),
                    String.valueOf(elog.getOrderDate()),
                    elog.getErrorMessage(),
                    elog.getLanguageId(),
                    elog.getCompanyCodeId(),
                    elog.getPlantId(),
                    elog.getWarehouseId(),
                    elog.getRefDocNumber()
            });
        }
        return records;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    private List<ErrorLog> reader() throws IOException {
        CSVReader reader = new CSVReader(new FileReader(this.fileStorageLocation + "\\error_log.csv"), ',');
        HeaderColumnNameMappingStrategy<ErrorLog> beanStrategy = new HeaderColumnNameMappingStrategy<ErrorLog>();
        beanStrategy.setType(ErrorLog.class);
        CsvToBean<ErrorLog> csvToBean = new CsvToBean<ErrorLog>();
        List<ErrorLog> emps = csvToBean.parse(beanStrategy, reader);
        reader.close();
        return emps;
    }

//===============================================Find==============================================================

    /**
     *
     * @param findErrorLog
     * @return
     */
    public List<ErrorLog> findErrorlog(FindErrorLog findErrorLog){
            ErrorLogSpecification Spec = new ErrorLogSpecification(findErrorLog);
            List<ErrorLog> source = errorLogRepository.findAll(Spec);
            return source;
    }


    //=========================================PreInboundHeader_ExceptionLog===========================================

    /**
     *
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param error
     */
    public void createPreInboundHeaderLog1(String languageId, String companyCode, String plantId, String warehouseId,
                                            String refDocNumber, String preInboundNo, String error) {

        ErrorLog dbErrorLog = new ErrorLog();
        dbErrorLog.setOrderTypeId(refDocNumber);
        dbErrorLog.setOrderDate(new Date());
        dbErrorLog.setLanguageId(languageId);
        dbErrorLog.setCompanyCodeId(companyCode);
        dbErrorLog.setPlantId(plantId);
        dbErrorLog.setWarehouseId(warehouseId);
        dbErrorLog.setRefDocNumber(refDocNumber);
        dbErrorLog.setReferenceField1(preInboundNo);
        dbErrorLog.setErrorMessage(error);
        dbErrorLog.setCreatedBy("MSD_API");
        dbErrorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(dbErrorLog);
    }


    //===========================================GrHeader_ExceptionLog=================================================

    /**
     *
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param stagingNo
     * @param goodsReceiptNo
     * @param palletCode
     * @param caseCode
     * @param error
     */
    public void createGrHeaderLog(String languageId, String companyCode, String plantId, String warehouseId,
                                   String refDocNumber, String preInboundNo, String stagingNo,
                                   String goodsReceiptNo, String palletCode, String caseCode, String error) {

        ErrorLog dbErrorLog = new ErrorLog();
        dbErrorLog.setOrderTypeId(goodsReceiptNo);
        dbErrorLog.setOrderDate(new Date());
        dbErrorLog.setLanguageId(languageId);
        dbErrorLog.setCompanyCodeId(companyCode);
        dbErrorLog.setPlantId(plantId);
        dbErrorLog.setWarehouseId(warehouseId);
        dbErrorLog.setRefDocNumber(refDocNumber);
        dbErrorLog.setReferenceField1(preInboundNo);
        dbErrorLog.setReferenceField2(stagingNo);
        dbErrorLog.setReferenceField3(palletCode);
        dbErrorLog.setReferenceField4(caseCode);
        dbErrorLog.setErrorMessage(error);
        dbErrorLog.setCreatedBy("MSD_API");
        dbErrorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(dbErrorLog);
    }

    /**
     *
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param goodsReceiptNo
     * @param caseCode
     * @param error
     */
    public void createGrHeaderLog1(String languageId, String companyCode, String plantId, String warehouseId,
                                    String refDocNumber, String goodsReceiptNo, String caseCode, String error) {

        ErrorLog dbErrorLog = new ErrorLog();
        dbErrorLog.setOrderTypeId(refDocNumber);
        dbErrorLog.setOrderDate(new Date());
        dbErrorLog.setLanguageId(languageId);
        dbErrorLog.setCompanyCodeId(companyCode);
        dbErrorLog.setPlantId(plantId);
        dbErrorLog.setWarehouseId(warehouseId);
        dbErrorLog.setRefDocNumber(refDocNumber);
        dbErrorLog.setReferenceField1(goodsReceiptNo);
        dbErrorLog.setReferenceField4(caseCode);
        dbErrorLog.setErrorMessage(error);
        dbErrorLog.setCreatedBy("MSD_API");
        dbErrorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(dbErrorLog);
    }

    /**
     *
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param error
     */
    public void createGrHeaderLog2(String languageId, String companyCode, String plantId, String warehouseId,
                                    String preInboundNo, String refDocNumber, String error) {

        ErrorLog dbErrorLog = new ErrorLog();
        dbErrorLog.setOrderTypeId(refDocNumber);
        dbErrorLog.setOrderDate(new Date());
        dbErrorLog.setLanguageId(languageId);
        dbErrorLog.setCompanyCodeId(companyCode);
        dbErrorLog.setPlantId(plantId);
        dbErrorLog.setWarehouseId(warehouseId);
        dbErrorLog.setRefDocNumber(refDocNumber);
        dbErrorLog.setReferenceField1(preInboundNo);
        dbErrorLog.setErrorMessage(error);
        dbErrorLog.setCreatedBy("MSD_API");
        dbErrorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(dbErrorLog);
    }

    /**
     *
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param error
     */
    public void createGrHeaderLog3(String languageId, String companyCode, String plantId,
                                    String warehouseId, String refDocNumber, String error) {

        ErrorLog dbErrorLog = new ErrorLog();
        dbErrorLog.setOrderTypeId(refDocNumber);
        dbErrorLog.setOrderDate(new Date());
        dbErrorLog.setLanguageId(languageId);
        dbErrorLog.setCompanyCodeId(companyCode);
        dbErrorLog.setPlantId(plantId);
        dbErrorLog.setWarehouseId(warehouseId);
        dbErrorLog.setRefDocNumber(refDocNumber);
        dbErrorLog.setErrorMessage(error);
        dbErrorLog.setCreatedBy("MSD_API");
        dbErrorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(dbErrorLog);
    }

    /**
     *
     * @param grHeaderV2
     * @param error
     */
    public void createGrHeaderLog4(GrHeaderV2 grHeaderV2, String error) {

        ErrorLog dbErrorLog = new ErrorLog();
        dbErrorLog.setOrderTypeId(grHeaderV2.getRefDocNumber());
        dbErrorLog.setOrderDate(new Date());
        dbErrorLog.setLanguageId(grHeaderV2.getLanguageId());
        dbErrorLog.setCompanyCodeId(grHeaderV2.getCompanyCode());
        dbErrorLog.setPlantId(grHeaderV2.getPlantId());
        dbErrorLog.setWarehouseId(grHeaderV2.getWarehouseId());
        dbErrorLog.setRefDocNumber(grHeaderV2.getRefDocNumber());
        dbErrorLog.setReferenceField1(grHeaderV2.getPreInboundNo());
        dbErrorLog.setReferenceField2(grHeaderV2.getGoodsReceiptNo());
        dbErrorLog.setReferenceField3(grHeaderV2.getStagingNo());
        dbErrorLog.setReferenceField4(grHeaderV2.getPalletCode());
        dbErrorLog.setReferenceField5(grHeaderV2.getCaseCode());
        dbErrorLog.setErrorMessage(error);
        dbErrorLog.setCreatedBy("MSD_API");
        dbErrorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(dbErrorLog);
    }

    //============================================GrLine_ExceptionLog==================================================

    /**
     *
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param goodsReceiptNo
     * @param palletCode
     * @param caseCode
     * @param packBarcodes
     * @param lineNo
     * @param itemCode
     * @param error
     */
    public void createGrLineLog(String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
                                 String preInboundNo, String goodsReceiptNo, String palletCode, String caseCode,
                                 String packBarcodes, Long lineNo, String itemCode, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(goodsReceiptNo);
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(languageId);
        errorLog.setCompanyCodeId(companyCode);
        errorLog.setPlantId(plantId);
        errorLog.setWarehouseId(warehouseId);
        errorLog.setRefDocNumber(refDocNumber);
        errorLog.setReferenceField1(preInboundNo);
        errorLog.setReferenceField2(palletCode);
        errorLog.setReferenceField3(caseCode);
        errorLog.setReferenceField4(packBarcodes);
        errorLog.setReferenceField5(itemCode);
        errorLog.setReferenceField6(String.valueOf(lineNo));
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    /**
     *
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param refDocNumber
     * @param preInboundNo
     * @param packBarcodes
     * @param lineNo
     * @param itemCode
     * @param error
     */
    public void createGrLineLog1(String languageId, String companyCode, String plantId, String refDocNumber,
                                  String preInboundNo, String packBarcodes, Long lineNo, String itemCode, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(String.valueOf(lineNo));
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(languageId);
        errorLog.setCompanyCodeId(companyCode);
        errorLog.setPlantId(plantId);
        errorLog.setRefDocNumber(refDocNumber);
        errorLog.setReferenceField1(preInboundNo);
        errorLog.setReferenceField2(packBarcodes);
        errorLog.setReferenceField3(itemCode);
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    /**
     *
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param packBarcodes
     * @param caseCode
     * @param error
     */
    public void createGrLineLog2(String languageId, String companyCode, String plantId, String warehouseId,
                                  String refDocNumber, String preInboundNo, String packBarcodes, String caseCode, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(refDocNumber);
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(languageId);
        errorLog.setCompanyCodeId(companyCode);
        errorLog.setPlantId(plantId);
        errorLog.setWarehouseId(warehouseId);
        errorLog.setRefDocNumber(refDocNumber);
        errorLog.setReferenceField1(preInboundNo);
        errorLog.setReferenceField4(caseCode);
        errorLog.setReferenceField5(packBarcodes);
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    /**
     *
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param refDocNumber
     * @param packBarcodes
     * @param error
     */
    public void createGrLineLog3(String languageId, String companyCode, String plantId,
                                  String refDocNumber, String packBarcodes, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(refDocNumber);
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(languageId);
        errorLog.setCompanyCodeId(companyCode);
        errorLog.setPlantId(plantId);
        errorLog.setRefDocNumber(refDocNumber);
        errorLog.setReferenceField5(packBarcodes);
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    /**
     *
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param packBarcodes
     * @param error
     */
    public void createGrLineLog4(String languageId, String companyCode, String plantId, String warehouseId,
                                  String refDocNumber, String preInboundNo, String packBarcodes, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(refDocNumber);
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(languageId);
        errorLog.setCompanyCodeId(companyCode);
        errorLog.setPlantId(plantId);
        errorLog.setWarehouseId(warehouseId);
        errorLog.setRefDocNumber(refDocNumber);
        errorLog.setReferenceField1(preInboundNo);
        errorLog.setReferenceField5(packBarcodes);
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    /**
     *
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param packBarcodes
     * @param error
     */
    public void createGrLineLog5(String languageId, String companyCode, String plantId, String warehouseId,
                                  String refDocNumber, String packBarcodes, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(refDocNumber);
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(languageId);
        errorLog.setCompanyCodeId(companyCode);
        errorLog.setPlantId(plantId);
        errorLog.setWarehouseId(warehouseId);
        errorLog.setRefDocNumber(refDocNumber);
        errorLog.setReferenceField1(packBarcodes);
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    /**
     *
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param refDocNumber
     * @param preInboundNo
     * @param lineNo
     * @param itemCode
     * @param error
     */
    public void createGrLineLog6(String languageId, String companyCode, String plantId, String refDocNumber,
                                  String preInboundNo, Long lineNo, String itemCode, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(String.valueOf(lineNo));
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(languageId);
        errorLog.setCompanyCodeId(companyCode);
        errorLog.setPlantId(plantId);
        errorLog.setRefDocNumber(refDocNumber);
        errorLog.setReferenceField1(preInboundNo);
        errorLog.setReferenceField2(itemCode);
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    /**
     *
     * @param grLineV2
     * @param error
     */
    public void createGrLineLog7(GrLineV2 grLineV2, String error) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setOrderTypeId(grLineV2.getGoodsReceiptNo());
        errorLog.setOrderDate(new Date());
        errorLog.setLanguageId(grLineV2.getLanguageId());
        errorLog.setCompanyCodeId(grLineV2.getCompanyCode());
        errorLog.setPlantId(grLineV2.getPlantId());
        errorLog.setWarehouseId(grLineV2.getWarehouseId());
        errorLog.setRefDocNumber(grLineV2.getRefDocNumber());
        errorLog.setErrorMessage(error);
        errorLog.setCreatedBy("MSD_API");
        errorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(errorLog);
    }

    /**
     *
     * @param grLineV2List
     * @param error
     */
    public void createGrLineLog10(List<AddGrLineV2> grLineV2List, String error) {

        for (AddGrLineV2 addGrLineV2 : grLineV2List) {
            ErrorLog errorLog = new ErrorLog();

            errorLog.setOrderTypeId(addGrLineV2.getGoodsReceiptNo());
            errorLog.setOrderDate(new Date());
            errorLog.setLanguageId(addGrLineV2.getLanguageId());
            errorLog.setCompanyCodeId(addGrLineV2.getCompanyCode());
            errorLog.setPlantId(addGrLineV2.getPlantId());
            errorLog.setWarehouseId(addGrLineV2.getWarehouseId());
            errorLog.setRefDocNumber(addGrLineV2.getRefDocNumber());
            errorLog.setErrorMessage(error);
            errorLog.setCreatedBy("MSD_API");
            errorLog.setCreatedOn(new Date());
            exceptionLogRepo.save(errorLog);
        }
    }

    //===========================================StagingLine_ExceptionLog==============================================

    /**
     *
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @param caseCode
     * @param error
     */
    public void createStagingLineLog3(String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo,
                                       String refDocNumber, Long lineNo, String itemCode, String caseCode, String error) {

        ErrorLog dbErrorLog = new ErrorLog();
        dbErrorLog.setOrderTypeId(refDocNumber);
        dbErrorLog.setOrderDate(new Date());
        dbErrorLog.setLanguageId(languageId);
        dbErrorLog.setCompanyCodeId(companyCode);
        dbErrorLog.setPlantId(plantId);
        dbErrorLog.setWarehouseId(warehouseId);
        dbErrorLog.setRefDocNumber(refDocNumber);
        dbErrorLog.setReferenceField1(preInboundNo);
        dbErrorLog.setReferenceField2(String.valueOf(lineNo));
        dbErrorLog.setReferenceField3(itemCode);
        dbErrorLog.setReferenceField4(caseCode);
        dbErrorLog.setErrorMessage(error);
        dbErrorLog.setCreatedBy("MSD_API");
        dbErrorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(dbErrorLog);
    }
}
