package com.tekclover.wms.api.inbound.orders.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.tekclover.wms.api.inbound.orders.config.PropertiesConfig;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.errorlog.ErrorLog;
import com.tekclover.wms.api.inbound.orders.model.inbound.InboundHeader;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.ASNV2;
import com.tekclover.wms.api.inbound.orders.repository.ErrorLogRepository;
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
     * @param els
     * @return
     */
    private static List<String[]> toStringArray(List<ErrorLog> els) {
        List<String[]> records = new ArrayList<String[]>();

        // adding header record
        records.add(new String[]{"orderId", "orderTypeId", "orderDate", "errorMessage", "languageId", "companyCode", "plantId", "warehouseId", "refDocNumber"});

        Iterator<ErrorLog> it = els.iterator();
        while (it.hasNext()) {
            ErrorLog elog = it.next();
            records.add(new String[]{
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

    //=================================================SupplierInvoiceServiceV4========================================

    public void createProcessInboundReceivedV2(ASNV2 asnv2, String error) {

        ErrorLog dbErrorLog = new ErrorLog();

        dbErrorLog.setOrderTypeId(asnv2.getAsnHeader().getPurchaseOrderNumber());
        dbErrorLog.setOrderId(asnv2.getAsnHeader().getInboundOrderTypeId());
        dbErrorLog.setCompanyCodeId(asnv2.getAsnHeader().getCompanyCode());
        dbErrorLog.setLanguageId(asnv2.getAsnHeader().getLanguageId());
        dbErrorLog.setPlantId(asnv2.getAsnHeader().getBranchCode());
        dbErrorLog.setWarehouseId(asnv2.getAsnHeader().getWarehouseId());
        dbErrorLog.setRefDocNumber(asnv2.getAsnHeader().getAMSSupplierInvoiceNo());
        dbErrorLog.setErrorMessage(error);
        exceptionLogRepo.save(dbErrorLog);


    }

//===========================================================ProcessInboundOrder===================================
    public void createProcessInboundOrder(InboundIntegrationHeader inbound, String error) {

        ErrorLog dbErrorLog = new ErrorLog();
        dbErrorLog.setOrderDate(new Date());
        dbErrorLog.setOrderId(inbound.getInboundOrderTypeId());
        dbErrorLog.setLanguageId(inbound.getLanguageId());
        dbErrorLog.setCompanyCodeId(inbound.getCompanyCode());
        dbErrorLog.setPlantId(inbound.getBranchCode());
        dbErrorLog.setWarehouseId(inbound.getWarehouseID());
        dbErrorLog.setRefDocNumber(inbound.getRefDocumentNo());
        dbErrorLog.setErrorMessage(error);
        dbErrorLog.setCreatedBy(inbound.getLoginUserId());
        dbErrorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(dbErrorLog);
    }
}
