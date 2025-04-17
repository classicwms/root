package com.tekclover.wms.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.exception.BadRequestException;
import com.tekclover.wms.core.exception.CustomErrorResponse;
import com.tekclover.wms.core.exception.RestResponseEntityExceptionHandler;
import com.tekclover.wms.core.model.auth.AuthToken;
import com.tekclover.wms.core.model.dto.Error;
import com.tekclover.wms.core.model.idmaster.FileNameForEmail;
import com.tekclover.wms.core.model.idmaster.MailingReport;
import com.tekclover.wms.core.model.transaction.*;

import com.tekclover.wms.core.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.core.model.warehouse.inbound.almailem.*;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.*;
import com.tekclover.wms.core.repository.MailingReportRepository;
import com.tekclover.wms.core.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.ValidationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class FileStorageService {

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    AuthTokenService authTokenService;
    @Autowired
    IDMasterService idMasterService;

    @Autowired
    MailingReportRepository mailingReportRepository;

    //-----------------------------------------------------------------------------------
    @Autowired
    TransactionService transactionService;
    @Autowired
    OrderPreparationService orderPreparationService;
    @Autowired
    ExcelDataProcessService excelDataProcessService;
    //-----------------------------------------------------------------------------------

    private Path fileStorageLocation = null;

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    private String getIDMasterServiceApiUrl() {
        return propertiesConfig.getIdmasterServiceUrl();
    }
    private String getTransactionAuthToken() {
        return authTokenService.getTransactionServiceAuthToken().getAccess_token();
    }

    /**
     * @param file
     * @return
     * @throws Exception
     */
    public Map<String, String> storeFile(MultipartFile file) throws Exception {
        this.fileStorageLocation = Paths.get(propertiesConfig.getFileUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new BadRequestException(
                        "Could not create the directory where the uploaded files will be stored.");
            }
        }

        log.info("loca : " + fileStorageLocation);

        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        log.info("filename before: " + fileName);
        fileName = fileName.replace(" ", "_");
        log.info("filename after: " + fileName);
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new BadRequestException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied : " + targetLocation);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return Collections.singletonMap("message", "File uploaded successfully!");
    }

    /**
     * @param file
     * @return
     * @throws Exception
     */
    public Map<String, String> processSOOrders(MultipartFile file) throws Exception {
        this.fileStorageLocation = Paths.get(propertiesConfig.getFileUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new BadRequestException(
                        "Could not create the directory where the uploaded files will be stored.");
            }
        }

        List<String> validationErrors = validationOutboundDynamically(file);
        if (!validationErrors.isEmpty()){
            List<Error> errors = validationFormatOutbound(validationErrors);
            if(!errors.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errors);
                Map<String, String> mapFileProps = new HashMap<>();
                mapFileProps.put("errors", jsonResponse);
                return mapFileProps;
            }
        }

        log.info("loca : " + fileStorageLocation);

        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        log.info("filename before: " + fileName);
        fileName = fileName.replace(" ", "_");
        log.info("filename after: " + fileName);
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new BadRequestException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied : " + targetLocation);

            List<List<String>> allRowsList = readExcelData(targetLocation.toFile());
            List<ShipmentOrderV2> shipmentOrders = prepSOData(allRowsList);
            log.info("shipmentOrders : " + shipmentOrders);

            // Uploading Orders
            WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
            AuthToken authToken = authTokenService.getTransactionServiceAuthToken();
            dbWarehouseApiResponse = transactionService.postSOV2Upload(shipmentOrders, "Uploaded", authToken.getAccess_token());

            if (dbWarehouseApiResponse != null) {
                Map<String, String> mapFileProps = new HashMap<>();
                mapFileProps.put("file", fileName);
                mapFileProps.put("status", "UPLOADED SUCCESSFULLY");
                return mapFileProps;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return null;
    }

    /**
     * @param location
     * @param file
     * @return
     * @throws Exception
     * @throws BadRequestException
     */
    public Map<String, String> storingFile(String location, MultipartFile file) throws Exception {

        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        log.info("filename before: " + fileName);
        fileName = fileName.replace(" ", "_");
        log.info("filename after: " + fileName);

        String locationPath = null;
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new BadRequestException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            if (location != null && location.toLowerCase().startsWith("document")) {
                if (location.indexOf('/') > 0) {
                    locationPath = propertiesConfig.getDocStorageBasePath() + "/" + location;
                } else {
                    // Document template
                    locationPath = propertiesConfig.getDocStorageBasePath() + propertiesConfig.getDocStorageDocumentPath();
                }
            }

            log.info("locationPath : " + locationPath);

            this.fileStorageLocation = Paths.get(locationPath).toAbsolutePath().normalize();
            log.info("fileStorageLocation--------> " + fileStorageLocation);

            if (!Files.exists(fileStorageLocation)) {
                try {
                    Files.createDirectories(this.fileStorageLocation);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new BadRequestException("Could not create the directory where the uploaded files will be stored.");
                }
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//			if(fileName.toLowerCase().startsWith("110")){
//				if(fileName.toLowerCase().contains("delivery")){
//					AuthToken authTokenForSetupService = authTokenService.getIDMasterServiceAuthToken();
//					FileNameForEmail dbFileNameForEmail = new FileNameForEmail();
//					dbFileNameForEmail.setDelivery110(fileName);
//					dbFileNameForEmail.setReportDate(DateUtils.getCurrentDateWithoutTimestamp());
//					dbFileNameForEmail.setDeletionIndicator(0L);
//					FileNameForEmail fileNameForEmail = idMasterService.updateFileNameForEmail(dbFileNameForEmail, authTokenForSetupService.getAccess_token());
//				}else if(fileName.toLowerCase().contains("dispatch")){
//					AuthToken authTokenForSetupService = authTokenService.getIDMasterServiceAuthToken();
//					FileNameForEmail dbFileNameForEmail = new FileNameForEmail();
//					dbFileNameForEmail.setDispatch110(fileName);
//					dbFileNameForEmail.setReportDate(DateUtils.getCurrentDateWithoutTimestamp());
//					dbFileNameForEmail.setDeletionIndicator(0L);
//					FileNameForEmail fileNameForEmail = idMasterService.updateFileNameForEmail(dbFileNameForEmail, authTokenForSetupService.getAccess_token());
//				}
//			}
//			if(fileName.toLowerCase().startsWith("111")){
//				if(fileName.toLowerCase().contains("delivery")){
//					AuthToken authTokenForSetupService = authTokenService.getIDMasterServiceAuthToken();
//					FileNameForEmail dbFileNameForEmail = new FileNameForEmail();
//					dbFileNameForEmail.setDelivery111(fileName);
//					dbFileNameForEmail.setReportDate(DateUtils.getCurrentDateWithoutTimestamp());
//					dbFileNameForEmail.setDeletionIndicator(0L);
//					FileNameForEmail fileNameForEmail = idMasterService.updateFileNameForEmail(dbFileNameForEmail, authTokenForSetupService.getAccess_token());
//				}else if(fileName.toLowerCase().contains("dispatch")){
//					AuthToken authTokenForSetupService = authTokenService.getIDMasterServiceAuthToken();
//					FileNameForEmail dbFileNameForEmail = new FileNameForEmail();
//					dbFileNameForEmail.setDispatch111(fileName);
//					dbFileNameForEmail.setReportDate(DateUtils.getCurrentDateWithoutTimestamp());
//					dbFileNameForEmail.setDeletionIndicator(0L);
//					FileNameForEmail fileNameForEmail = idMasterService.updateFileNameForEmail(dbFileNameForEmail, authTokenForSetupService.getAccess_token());
//				}
//			}
            Map<String, String> mapFileProps = new HashMap<>();
            mapFileProps.put("file", fileName);
            mapFileProps.put("location", location);
            mapFileProps.put("status", "UPLOADED");
            return mapFileProps;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
    }

    /**
     * @param file
     * @return
     * @throws Exception
     * @throws BadRequestException
     */
//	public Map<String, String> storingFileMailingReport(String location, MultipartFile file)
//			throws Exception {
//
//		// Normalize file name
//		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//		log.info("filename before: " + fileName);
//		fileName = fileName.replace(" ", "_");
//		log.info("filename after: " + fileName);
//
//		String locationPath = null;
//		try {
//			// Check if the file's name contains invalid characters
//			if (fileName.contains("..")) {
//				throw new BadRequestException("Sorry! Filename contains invalid path sequence " + fileName);
//			}
//
//			if (location != null && location.toLowerCase().startsWith("document")) {
//				if (location.indexOf('/') > 0) {
//					locationPath = propertiesConfig.getDocStorageBasePath() + "/" + location;
//				} else {
//					// Document template
//					locationPath = propertiesConfig.getDocStorageBasePath() + propertiesConfig.getDocStorageDocumentPath();
//				}
//			}
//
//			log.info("locationPath : " + locationPath);
//
//			this.fileStorageLocation = Paths.get(locationPath).toAbsolutePath().normalize();
//			log.info("fileStorageLocation--------> " + fileStorageLocation);
//
//			if (!Files.exists(fileStorageLocation)) {
//				try {
//					Files.createDirectories(this.fileStorageLocation);
//				} catch (Exception ex) {
//					ex.printStackTrace();
//					throw new BadRequestException("Could not create the directory where the uploaded files will be stored.");
//				}
//			}
//
//			AuthToken authTokenForSetupService = authTokenService.getIDMasterServiceAuthToken();
//
//			MailingReport newMailingReport = new MailingReport();
//
//			newMailingReport.setReportDate(DateUtils.getCurrentDateWithoutTimestamp());
//			newMailingReport.setDeletionIndicator(0L);
//			newMailingReport.setCompanyCodeId("1000");		//HardCode
//			newMailingReport.setPlantId("1001");			//HardCode
//			newMailingReport.setLanguageId("EN");			//HardCode
//			newMailingReport.setMailSent("0");				//HardCode
//			newMailingReport.setMailSentFailed("0");		//HardCode
//
//			if(fileName.toLowerCase().startsWith("110")){
//
//				newMailingReport.setWarehouseId("110");
//
//			}
//			if(fileName.toLowerCase().startsWith("111")){
//
//				newMailingReport.setWarehouseId("111");
//
//			}
//
//			Optional<MailingReport> dbMailingReport = mailingReportRepository
//														.findBycompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndFileNameAndDeletionIndicator(
//																"1000",			//HardCode
//																"1001", 						//HardCode
//																newMailingReport.getWarehouseId(),
//																"EN",					//HardCode
//																fileName, 0L );
//
//			Long countMailingReportByDate = mailingReportRepository.countByReportDateAndWarehouseId(
//					DateUtils.getCurrentDateWithoutTimestamp(),
//					newMailingReport.getWarehouseId());
//
//			if(countMailingReportByDate == null) {
//				countMailingReportByDate = 0L;
//			}
//
//			if(dbMailingReport.isEmpty()) {
//				if (countMailingReportByDate != 1) {
//
//					// Copy file to the target location (Replacing existing file with the same name)
//					Path targetLocation = this.fileStorageLocation.resolve(fileName);
//					Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//
//					newMailingReport.setFileName(fileName);
//					newMailingReport.setUploaded(true);
//
//					Boolean uploadedMailingReport = idMasterService.createMailingReport(newMailingReport, authTokenForSetupService.getAccess_token());
//
//					if (uploadedMailingReport) {
//
//						Map<String, String> mapFileProps = new HashMap<>();
//						mapFileProps.put("file", fileName);
//						mapFileProps.put("location", location);
//						mapFileProps.put("status", "UPLOADED");
//						return mapFileProps;
//
//					} else {
//						throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
//					}
//				}
//			}
//		} catch (IOException ex) {
//			ex.printStackTrace();
//			throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
//		}
//		return null;
//	}
    private List<List<String>> readExcelData(File file) {
        try {
            Workbook workbook = new XSSFWorkbook(file);
            workbook.setMissingCellPolicy(Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

            List<List<String>> allRowsList = new ArrayList<>();
            DataFormatter fmt = new DataFormatter();
            for (int rn = sheet.getFirstRowNum() + 2; rn <= sheet.getLastRowNum(); rn++) {
                Row row = sheet.getRow(rn);
                log.info("Row:  " + row.getRowNum());
                if (row == null) {
                    // There is no data in this row, handle as needed
                } else if (row.getRowNum() != 0) {
                    List<String> listUploadData = new ArrayList<String>();
                    for (int cn = 0; cn <= row.getLastCellNum(); cn++) {
                        Cell cell = row.getCell(cn);
                        if (cell == null) {
                            log.info("cell empty: " + cell);
                            listUploadData.add("");
                        } else {
                            String cellStr = fmt.formatCellValue(cell);
                            log.info("cellStr: " + cellStr);
                            listUploadData.add(cellStr);
                        }
                    }
                    allRowsList.add(listUploadData);
                }
            }

//			Iterator<Row> iterator = sheet.iterator();
//			List<List<String>> allRowsList = new ArrayList<>();
//			while (iterator.hasNext()) {
//				Row currentRow = iterator.next();
//				Iterator<Cell> cellIterator = currentRow.iterator();
//
//				// Moving to data row instead of header row
//				currentRow = iterator.next();
//				cellIterator = currentRow.iterator();
//
//				List<String> listUploadData = new ArrayList<String>();
//				while (cellIterator.hasNext()) {
//					Cell currentCell = cellIterator.next();
//					log.info("===currentCell===== " + currentCell);
//					if (currentCell.getColumnIndex() == 7) {
//						listUploadData.add(" ");
//						log.info("=#= " + listUploadData.size());
//					}
//					if (currentCell.getCellType() == CellType.STRING) {
//						log.info(currentCell.getStringCellValue() + "*****");
//						if (currentCell.getStringCellValue() != null
//								&& !currentCell.getStringCellValue().trim().isEmpty()) {
//							listUploadData.add(currentCell.getStringCellValue());
////							log.info("== " + listUploadData.size());
//						} else {
//							listUploadData.add(" ");
////							log.info("=#= " + listUploadData.size());
//						}
//					} else if (currentCell.getCellType() == CellType.NUMERIC) {
////						log.info(currentCell.getNumericCellValue() + "--");
//						listUploadData.add(String.valueOf(currentCell.getNumericCellValue()));
//					}
//				}
//				log.info("=#= " + listUploadData);
//				allRowsList.add(listUploadData);
//			}
            log.info("list data: " + allRowsList);
            return allRowsList;
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        return null;
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
//	private List<ShipmentOrderV2> prepSOData (List<List<String>> allRowsList) {
//		List<ShipmentOrderV2> shipmentOrderList = new ArrayList<>();
//
//		for (List<String> listUploadedData : allRowsList) {
//			Set<SOHeaderV2> setSOHeader = new HashSet<>();
//			List<SOLineV2> soLines = new ArrayList<>();
//
//			// Header
//			SOHeaderV2 soHeader = null;
//			boolean oneTimeAllow = true;
//			for (String column : listUploadedData) {
//				if (oneTimeAllow) {
//					soHeader = new SOHeaderV2();
//					soHeader.setRequiredDeliveryDate(listUploadedData.get(0));
//					soHeader.setStoreID(listUploadedData.get(1));
//					soHeader.setStoreName(listUploadedData.get(2));
//					soHeader.setTransferOrderNumber(listUploadedData.get(3));
//					soHeader.setWareHouseId(listUploadedData.get(4));
//					setSOHeader.add(soHeader);
//				}
//				oneTimeAllow = false;
//
//				// Line
//				SOLineV2 soLine = new SOLineV2();
//				soLine.setLineReference(Long.valueOf(listUploadedData.get(5)));
//				soLine.setOrderType(listUploadedData.get(6));
//				soLine.setOrderedQty(Double.valueOf(listUploadedData.get(7)));
//				soLine.setSku(listUploadedData.get(8));
//				soLine.setSkuDescription(listUploadedData.get(9));
//				soLine.setUom(listUploadedData.get(10));
//				soLines.add(soLine);
//			}
//
//			ShipmentOrderV2 shipmentOrder = new ShipmentOrderV2();
//			shipmentOrder.setSoHeader(soHeader);
//			shipmentOrder.setSoLine(soLines);
//			shipmentOrderList.add(shipmentOrder);
//		}
//		return shipmentOrderList;
//	}
    private List<ShipmentOrderV2> prepSOData(List<List<String>> allRowsList) {
        List<ShipmentOrderV2> shipmentOrderList = new ArrayList<>();
        boolean oneTimeAllow = true;
        // Header
        SOHeaderV2 soHeader = null;
        List<SOLineV2> soLines = new ArrayList<>();

        for (List<String> listUploadedData : allRowsList) {

            if (oneTimeAllow) {
                soHeader = new SOHeaderV2();
                soHeader.setCompanyCode(listUploadedData.get(0));
                soHeader.setBranchCode(listUploadedData.get(1));
                soHeader.setRequiredDeliveryDate(listUploadedData.get(2));
                soHeader.setStoreID(listUploadedData.get(3));
                soHeader.setStoreName(listUploadedData.get(4));
                soHeader.setTransferOrderNumber(listUploadedData.get(5));
                soHeader.setWarehouseId(listUploadedData.get(6));
//					soHeader.add(soHeader);
            }
            oneTimeAllow = false;

            // Line
            SOLineV2 soLine = new SOLineV2();
            soLine.setLineReference(Long.valueOf(listUploadedData.get(7)));
            soLine.setOrderType(listUploadedData.get(8));
            soLine.setOrderedQty(Double.valueOf(listUploadedData.get(9)));
            soLine.setSku(listUploadedData.get(10));
            soLine.setSkuDescription(listUploadedData.get(11));
            soLine.setUom(listUploadedData.get(12));
            soLines.add(soLine);
        }

        ShipmentOrderV2 shipmentOrder = new ShipmentOrderV2();
        shipmentOrder.setSoHeader(soHeader);
        shipmentOrder.setSoLine(soLines);
        shipmentOrderList.add(shipmentOrder);

        return shipmentOrderList;
    }

    /**
     * loadFileAsResource
     *
     * @param fileName
     * @return
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new BadRequestException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new BadRequestException("File not found " + fileName);
        }
    }

//================================================================================================================

    /**
     * @param file
     * @return
     * @throws Exception
     */
    public Map<String, String> processAsnOrders(MultipartFile file) throws Exception {
        this.fileStorageLocation = Paths.get(propertiesConfig.getFileUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new BadRequestException(
                        "Could not create the directory where the uploaded files will be stored.");
            }
        }

        List<String> validationErrors = validationInboundDynamically(file);
        if (!validationErrors.isEmpty()) {
            List<Error> errors = validationFormatInbound(validationErrors);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errors);
            Map<String, String> mapFileProps = new HashMap<>();
            mapFileProps.put("errors", jsonResponse);
            return mapFileProps;
        }


        log.info("loca : " + fileStorageLocation);

        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        log.info("filename before: " + fileName);
        fileName = fileName.replace(" ", "_");
        log.info("filename after: " + fileName);
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new BadRequestException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied : " + targetLocation);

            List<List<String>> allRowsList = readExcelData(targetLocation.toFile());
            List<ASNV2> asnV2Orders = prepAsnData(allRowsList);
            log.info("asnOrders : " + asnV2Orders);

            // Uploading Orders
            WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
            AuthToken authToken = authTokenService.getTransactionServiceAuthToken();
            dbWarehouseApiResponse = transactionService.postASNV2Upload(asnV2Orders, "Uploaded", authToken.getAccess_token());

            if (dbWarehouseApiResponse != null) {
                Map<String, String> mapFileProps = new HashMap<>();
                mapFileProps.put("file", fileName);
                mapFileProps.put("status", "UPLOADED SUCCESSFULLY");
                return mapFileProps;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return null;
    }

    /**
     * @param location
     * @param file
     * @return
     */
    public String getQualifiedFilePath(String location, String file) {
        String filePath = propertiesConfig.getDocStorageBasePath();

        log.info("getQualifiedFilePath---location------>: " + location);
        log.info("getQualifiedFilePath---file------>: " + file);

        if (location.startsWith("/")) {
            filePath = filePath + location;
        } else {
            filePath = filePath + "/" + location;
        }

        if (location.endsWith("/")) {
            filePath = filePath + file;
        } else {
            filePath = filePath + "/" + file;
        }
        log.info("filePath: " + filePath);
        return filePath;
    }

    /**
     * @param file
     * @param filePath
     * @return
     */
    public Map<String, String> storeSingleFile(MultipartFile file, String filePath) {

        if (!filePath.startsWith("/")) {
            filePath = "/" + filePath;
        }

        this.fileStorageLocation = Paths.get(propertiesConfig.getDocStorageBasePath() + filePath).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new BadRequestException(
                        "Could not create the directory where the uploaded files will be stored.");
            }
        }

        log.info("location : " + fileStorageLocation);

        // Normalize file name
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        log.info("filename before: " + fileName);
        fileName = fileName.replace(" ", "_");
        log.info("filename after: " + fileName);
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new BadRequestException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied : " + targetLocation);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }

        Map<String, String> mapFileProps = new HashMap<>();
        mapFileProps.put("file", fileName);
        mapFileProps.put("location", filePath);
        mapFileProps.put("status", "UPLOADED");
        return mapFileProps;
    }


    /**
     * @param file
     * @return
     * @throws Exception
     */
    public Map<String, String> processInterWarehouseTransferInOrders(MultipartFile file) throws Exception {
        this.fileStorageLocation = Paths.get(propertiesConfig.getFileUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new BadRequestException(
                        "Could not create the directory where the uploaded files will be stored.");
            }
        }

        log.info("loca : " + fileStorageLocation);

        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        log.info("filename before: " + fileName);
        fileName = fileName.replace(" ", "_");
        log.info("filename after: " + fileName);
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new BadRequestException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied : " + targetLocation);

            List<List<String>> allRowsList = readExcelData(targetLocation.toFile());
            List<InterWarehouseTransferInV2> wh2whOrders = prepInterwareHouseInData(allRowsList);
            log.info("wh2whOrders : " + wh2whOrders);

            // Uploading Orders
            WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
            AuthToken authToken = authTokenService.getTransactionServiceAuthToken();
            dbWarehouseApiResponse = transactionService.postInterWarehouseTransferInUploadV2(wh2whOrders, "Uploaded", authToken.getAccess_token());

            if (dbWarehouseApiResponse != null) {
                Map<String, String> mapFileProps = new HashMap<>();
                mapFileProps.put("file", fileName);
                mapFileProps.put("status", "UPLOADED SUCCESSFULLY");
                return mapFileProps;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return null;
    }

    /**
     *
     * @param allRowsList
     * @return
     */
//	private List<ASNV2> prepAsnData(List<List<String>> allRowsList) {
//		List<ASNV2> orderList = new ArrayList<>();
//		for (List<String> listUploadedData : allRowsList) {
//			Set<ASNHeaderV2> setWHHeader = new HashSet<>();
//			List<ASNLineV2> lisAsnLine = new ArrayList<>();
//
//			// Header
//			ASNHeaderV2 header = new ASNHeaderV2();
//			header.setBranchCode(listUploadedData.get(0));
//			header.setCompanyCode(listUploadedData.get(1));
//			header.setAsnNumber(listUploadedData.get(2));
//			setWHHeader.add(header);
//
//			// Line
//			ASNLineV2 line = new ASNLineV2();
//			line.setLineReference(Long.valueOf(listUploadedData.get(3)));
//			line.setSku(listUploadedData.get(4));
//			line.setSkuDescription(listUploadedData.get(5));
//			line.setContainerNumber(listUploadedData.get(6));
//			line.setSupplierCode(listUploadedData.get(7));
//			line.setSupplierPartNumber(listUploadedData.get(8));
//			line.setManufacturerName(listUploadedData.get(9));
//			line.setManufacturerCode(listUploadedData.get(10));
//			line.setExpectedDate(listUploadedData.get(11));
//			line.setExpectedQty(Double.valueOf(listUploadedData.get(12)));
//			line.setUom(listUploadedData.get(13));
//			line.setOrigin(listUploadedData.get(14));
//			line.setSupplierName(listUploadedData.get(15));
//			line.setBrand(listUploadedData.get(16));
//
//			if (listUploadedData.get(17).trim().length() > 0) {
//				line.setPackQty(Double.valueOf(listUploadedData.get(17)));
//			}
//			lisAsnLine.add(line);
//
//
//			ASNV2 orders = new ASNV2();
//			orders.setAsnHeader(header);
//			orders.setAsnLine(lisAsnLine);
//			orderList.add(orders);
//		}
//		return orderList;
//	}


    /**
     * @param allRowsList
     * @return
     */
    private List<ASNV2> prepAsnData(List<List<String>> allRowsList) {
        List<ASNV2> orderList = new ArrayList<>();
        Map<String, ASNV2> asnMap = new HashMap<>(); // Store ASN headers and associated lines

        for (List<String> listUploadedData : allRowsList) {
            String asnNumber = listUploadedData.get(2); // Extract ASN Number

            ASNV2 asnOrder;
            List<ASNLineV2> asnLines;

            // Check if ASN already exists
            if (asnMap.containsKey(asnNumber)) {
                asnOrder = asnMap.get(asnNumber);
                asnLines = asnOrder.getAsnLine(); // Retrieve existing list of lines
            } else {
                // Create new ASN entry
                ASNHeaderV2 header = new ASNHeaderV2();
                header.setBranchCode(listUploadedData.get(0));
                header.setCompanyCode(listUploadedData.get(1));
                header.setAsnNumber(asnNumber);

                asnLines = new ArrayList<>(); // New list for ASN lines

                asnOrder = new ASNV2();
                asnOrder.setAsnHeader(header);
                asnOrder.setAsnLine(asnLines);

                asnMap.put(asnNumber, asnOrder); // Store in map
                orderList.add(asnOrder); // Add to final order list
            }

            // Create and add line item
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
            if(!listUploadedData.get(16).trim().isEmpty()) {
                line.setBrand(listUploadedData.get(16));
            }

            if (!listUploadedData.get(17).trim().isEmpty()) {
                line.setPackQty(Double.valueOf(listUploadedData.get(17)));
            }

            asnLines.add(line); // Add line to ASN
        }

        return orderList; // Return list of ASNs
    }


    /**
     * @param allRowsList
     * @return
     */
    private List<InterWarehouseTransferInV2> prepInterwareHouseInData(List<List<String>> allRowsList) {
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

                if (listUploadedData.get(17).trim().length() > 0) {
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
     * @param file
     * @return
     * @throws Exception
     */
    public Map<String, String> processBinToBin(MultipartFile file) throws Exception {
        this.fileStorageLocation = Paths.get(propertiesConfig.getFileUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new BadRequestException(
                        "Could not create the directory where the uploaded files will be stored.");
            }
        }

        log.info("loca : " + fileStorageLocation);

        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        log.info("filename before: " + fileName);
        fileName = fileName.replace(" ", "_");
        log.info("filename after: " + fileName);
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new BadRequestException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied : " + targetLocation);

            List<List<String>> allRowsList = readExcelData(targetLocation.toFile());
            List<InhouseTransferUpload> inhouseTransferUploads = prepInHouseTransferHeaderV2(allRowsList);
            log.info("inhouseTransferUploads bin-to-bin : " + inhouseTransferUploads);

            // Uploading Orders
            AuthToken authToken = authTokenService.getTransactionServiceAuthToken();
            WarehouseApiResponse dbWarehouseApiResponse = transactionService.createInhouseTransferUploadV2(inhouseTransferUploads, "UP_AMS", authToken.getAccess_token());

            if (dbWarehouseApiResponse != null) {
                Map<String, String> mapFileProps = new HashMap<>();
                mapFileProps.put("file", fileName);
                mapFileProps.put("status", "UPLOADED SUCCESSFULLY");
                return mapFileProps;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return null;
    }

    /**
     * @param allRowsList
     * @return
     */
    private List<InhouseTransferUpload> prepInHouseTransferHeaderV2(List<List<String>> allRowsList) {
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
            if (listUploadedData.get(9).trim().length() > 0) {
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
     * @param file
     * @return
     * @throws Exception
     */
    public Map<String, String> processStockAdjustment(MultipartFile file) throws Exception {
        this.fileStorageLocation = Paths.get(propertiesConfig.getFileUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new BadRequestException(
                        "Could not create the directory where the uploaded files will be stored.");
            }
        }

        log.info("loca : " + fileStorageLocation);

        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        log.info("filename before: " + fileName);
        fileName = fileName.replace(" ", "_");
        log.info("filename after: " + fileName);
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new BadRequestException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied : " + targetLocation);

            List<List<String>> allRowsList = readExcelData(targetLocation.toFile());
            List<StockAdjustment> stockAdjustmentList = prepStockAdjustment(allRowsList);
            log.info("StockAdjustment List: " + stockAdjustmentList);

            // Uploading Orders
            AuthToken authToken = authTokenService.getTransactionServiceAuthToken();
            WarehouseApiResponse dbWarehouseApiResponse = transactionService.createStockAdjustmentUploadV2(stockAdjustmentList, authToken.getAccess_token());

            if (dbWarehouseApiResponse != null) {
                Map<String, String> mapFileProps = new HashMap<>();
                mapFileProps.put("file", fileName);
                mapFileProps.put("status", "UPLOADED SUCCESSFULLY");
                return mapFileProps;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return null;
    }

    /**
     *
     * @param file
     * @return
     */
    public Map<String, String> processSalesOrdersV3(String companyCodeId, String plantId, String languageId,
                                                    String warehouseId, String loginUserId, MultipartFile file) {
        this.fileStorageLocation = Paths.get(propertiesConfig.getFileUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new BadRequestException(
                        "Could not create the directory where the uploaded files will be stored.");
            }
        }

        log.info("loca : " + fileStorageLocation);

        // Normalize file name
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        log.info("filename before: " + fileName);
        fileName = fileName.replace(" ", "_");
        log.info("filename after: " + fileName);
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new BadRequestException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied : " + targetLocation);

            List<List<String>> allRowsList = readExcelData(targetLocation.toFile());
            if(allRowsList != null && !allRowsList.isEmpty()) {
                List<SalesOrderV2> salesOrders = orderPreparationService.prepSalesOrderDataV3(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                log.info("salesOrders : " + salesOrders);
                WarehouseApiResponse dbWarehouseApiResponse = new WarehouseApiResponse();
                String authToken = getTransactionAuthToken();
                dbWarehouseApiResponse = transactionService.postSalesOrderV2(salesOrders, authToken);

                if (dbWarehouseApiResponse != null) {
                    Map<String, String> mapFileProps = new HashMap<>();
                    mapFileProps.put("file", fileName);
                    mapFileProps.put("status", "UPLOADED SUCCESSFULLY");
                    return mapFileProps;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return null;
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
     * @throws ValidationException
     */
    public Map<String, String> processInboundOrdersV4(String companyCodeId, String plantId, String languageId,
                                                      String warehouseId, Long orderTypeId, String loginUserId, MultipartFile file) throws IOException, ValidationException {
        this.fileStorageLocation = Paths.get(propertiesConfig.getFileUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new BadRequestException(
                        "Could not create the directory where the uploaded files will be stored.");
            }
        }

        List<String> validationErrors = validationDynamically(file);
        if (!validationErrors.isEmpty()){
            throw new ValidationException(String.join(", ", validationErrors));
        }

        log.info("loca : " + fileStorageLocation);

        // Normalize file name
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        log.info("filename before: " + fileName);
        fileName = fileName.replace(" ", "_");
        log.info("filename after: " + fileName);
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new BadRequestException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied : " + targetLocation);

//			List<List<String>> allRowsList = readExcelData(targetLocation.toFile());
            List<InboundOrderProcessV4> allRowsList = excelDataProcessService.inboundReadExcelFile(companyCodeId, plantId, languageId, warehouseId, orderTypeId, loginUserId, file);
            if(allRowsList != null && !allRowsList.isEmpty()) {
                // Uploading Orders
                WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
                String authToken = getTransactionAuthToken();

                if(orderTypeId == 1L) {
                    List<ASNV2> asnV2Orders = orderPreparationService.prepAsnMultipleDataV4(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("asnOrders : " + asnV2Orders);
                    dbWarehouseApiResponse = transactionService.postASNV2Upload(asnV2Orders, loginUserId, authToken);
                }
                if(orderTypeId == 4L) {
                    List<InterWarehouseTransferInV2> wh2whOrders = orderPreparationService.prepInterwareHouseInDataV4(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("wh2whOrders : " + wh2whOrders);
                    dbWarehouseApiResponse = transactionService.postInterWarehouseTransferInUploadV2(wh2whOrders, loginUserId, authToken);
                }
                if(orderTypeId == 2L) {
                    List<SaleOrderReturnV2> saleOrderReturns = orderPreparationService.prepSaleOrderReturnDataV4(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("saleOrderReturn : " + saleOrderReturns);
                    transactionService.postSOReturnUploadV2(saleOrderReturns, authToken);
                }

                if(dbWarehouseApiResponse != null) {
                    Map<String, String> mapFileProps = new HashMap<>();
                    mapFileProps.put("file", fileName);
                    mapFileProps.put("status", "UPLOADED SUCCESSFULLY");
                    return mapFileProps;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return null;
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
     * @throws ValidationException
     */
    public Map<String, String> processOutboundOrdersV4(String companyCodeId, String plantId, String languageId,
                                                       String warehouseId, Long orderTypeId, String loginUserId, MultipartFile file) throws IOException, ValidationException {
        this.fileStorageLocation = Paths.get(propertiesConfig.getFileUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new BadRequestException(
                        "Could not create the directory where the uploaded files will be stored.");
            }
        }

        List<String> validationErrors = validationDynamically(file);
        if (!validationErrors.isEmpty()){
            throw new ValidationException(String.join(", ", validationErrors));
        }


        log.info("loca : " + fileStorageLocation);

        // Normalize file name
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        log.info("filename before: " + fileName);
        fileName = fileName.replace(" ", "_");
        log.info("filename after: " + fileName);
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new BadRequestException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied : " + targetLocation);

            List<OutboundOrderProcessV4> allRowsList = excelDataProcessService.outboundReadExcelFile(companyCodeId, plantId, languageId, warehouseId, orderTypeId, loginUserId, file);
            if(allRowsList != null && !allRowsList.isEmpty()) {
                WarehouseApiResponse dbWarehouseApiResponse = new WarehouseApiResponse();
                String authToken = getTransactionAuthToken();
                if(orderTypeId == 3L) {
                    List<SalesOrderV2> salesOrders = orderPreparationService.prepSalesOrderDataV4(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("salesOrders : " + salesOrders);
                    dbWarehouseApiResponse = transactionService.postSalesOrderV2(salesOrders, authToken);
                }
                if(orderTypeId == 1L) {
                    List<InterWarehouseTransferOutV2> interWarehouseTransferOuts = orderPreparationService.prepInterWarehouseOutDataV4(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("InterWarehouseTransferOutV2 : " + interWarehouseTransferOuts);
                    dbWarehouseApiResponse = transactionService.postInterWarehouseTransferOutUploadV2(interWarehouseTransferOuts, authToken);
                }
                if(orderTypeId == 2L) {
                    List<ReturnPOV2> returnPOs = orderPreparationService.prepReturnOrderDataV4(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("returnpov2 : " + returnPOs);
                    dbWarehouseApiResponse = transactionService.postReturnPOUploadV2(returnPOs, authToken);
                }

                if (dbWarehouseApiResponse != null) {
                    Map<String, String> mapFileProps = new HashMap<>();
                    mapFileProps.put("file", fileName);
                    mapFileProps.put("status", "UPLOADED SUCCESSFULLY");
                    return mapFileProps;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return null;
    }

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    private List<String> validationDynamically(MultipartFile file) throws IOException {
        List<String> errors = new ArrayList<>();

        // Read Excel file
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Assuming the first row contains the headers
            Row headerRow = sheet.getRow(0);
            // Validate data in each row (excluding the header row)
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                for (int colIndex = 0; colIndex < headerRow.getPhysicalNumberOfCells(); colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    String header = headerRow.getCell(colIndex).getStringCellValue().toLowerCase();

                    // Validate cell based on header and column index
                    if (cell != null) {
                        switch (header) {
                            case "asnnumber":
                            case "transferordernumber":
                            case "sku":
                            case "skudescription":
                            case "suppliercode":
                            case "suppliername":
                            case "uom":
                            case "alternateuom":
                            case "salesorderno":
                            case "picklistnumber":
                            case "picklistno":
                            case "ponumber":
                            case "customerid":
                            case "customername":
                            case "refordernumber":
                            case "returnordernumber":
                                validateStringCell(cell, rowIndex, colIndex, header ,errors);
                                break;
                            case "linereference":
                            case "lineno":
                            case "expectedqty":
                            case "orderqty":
                            case "returnqty":
                            case "nobags":
                            case "bagsize":
                            case "mrp":
                            case "qty":
                                validateIntegerCell(cell, rowIndex, colIndex,header, errors);
                                break;
                            case "expecteddate":
                            case "requireddeliverydate":
                            case "date":
                                validateDateCell(cell,rowIndex,colIndex,header,errors);
                                break;
                            default:
                                errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1)+ ": " + header);
                                break;
                        }
                    } else {
                        errors.add("Empty cell at row " + (rowIndex + 1) + ", column " + (colIndex + 1));
                    }
                }
            }
            if (errors.isEmpty()) {
                System.out.println("No validation errors found.");
            } else {
                System.out.println("Validation errors:");
                for (String error : errors) {
                    System.out.println(error);
                }
            }
        }
        return errors;
    }


    /**
     * @param allRowsList
     * @return
     */
    private List<StockAdjustment> prepStockAdjustment(List<List<String>> allRowsList) {
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
    //=============================Dynamic Excel Validation code Inbound======================//

    private List<String> validationInboundDynamically(MultipartFile file) throws IOException {
        List<String> errors = new ArrayList<>();

        // Read Excel file
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Assuming the first row contains the headers
            Row headerRow = sheet.getRow(0);
            // Validate data in each row (excluding the header row)
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                // Skip the row if it is completely empty
                if (isRowEmpty(row)) {
                    continue;
                }
                for (int colIndex = 0; colIndex < headerRow.getPhysicalNumberOfCells(); colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    String header = headerRow.getCell(colIndex).getStringCellValue().toLowerCase();

                    // Validate cell based on header and column index
                    if (cell != null) {
                        switch (header) {
                            case "asnnumber":
                            case "branchcode":
                            case "companycode":
                            case "manufacturername":
                            case "manufacturercode":
                            case "containernumber":
                            case "brand":
                            case "sku":
                            case "skudescription":
                            case "uom":

                                validateStringCell(cell, rowIndex, colIndex, header, errors);
                                break;

                            case "suppliercode":
                            case "suppliername":
                            case "origin":
                                nullValidateStringCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            case "linereference":
                            case "packqty":
                            case "expectedqty":
                                validateIntegerCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            case "expecteddate":
                                validateDateCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            default:
                                errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": " + header);
                                break;
                        }
                    }
//					else {
//						switch (header) {
//							case "huserialnumber":
//							case "skucode":
//							case "inbound":
//							case "qty":
//							case "itm":
//								errors.add("Empty cell at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + ") : : Mandatory Field cannot be empty.");
//								break;
//						}
//					}
                }
            }
            if (errors.isEmpty()) {
                System.out.println("No validation errors found.");
            } else {
                System.out.println("Validation errors:");
                for (String error : errors) {
                    System.out.println(error);
                }
            }
        }
        return errors;
    }

    //=============================Dynamic Excel Validation code Outbound======================//

    private List<String> validationOutboundDynamically(MultipartFile file) throws IOException {
        List<String> errors = new ArrayList<>();

        // Read Excel file
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Assuming the first row contains the headers
            Row headerRow = sheet.getRow(0);
            // Validate data in each row (excluding the header row)
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                // Skip the row if it is completely empty
                if (isRowEmpty(row)) {
                    continue;
                }
                for (int colIndex = 0; colIndex < headerRow.getPhysicalNumberOfCells(); colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    String header = headerRow.getCell(colIndex).getStringCellValue().toLowerCase();

                    // Validate cell based on header and column index
                    if (cell != null) {
                        switch (header) {
                            case "companycode":
                            case "branchcode":
                            case "storeid":
                            case "storename":
                            case "transferordernumber":
                            case "warehouseid":
                            case "ordertype":
                            case "sku":
                            case "skudescription":
                            case "uom":
                                validateStringCell(cell, rowIndex, colIndex, header ,errors);
                                break;
//							case "specialstock":
//							case "mtonumber":
//								nullValidateStringCell(cell, rowIndex, colIndex, header, errors);
//								break;
                            case "linereference":
                            case "orderedqty":
                                validateIntegerCell(cell, rowIndex, colIndex,header, errors);
                                break;
                            case "requireddeliverydate":
                                validateDateCell(cell,rowIndex,colIndex,header,errors);
                                break;
                            default:
                                errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1)+ ": " + header);
                                break;
                        }
//					} else {
//						switch (header) {
//							case "skucode":
//							case "outbound":
//							case "inbound":
//							case "qty":
//							case "itm":
//							case "shiptoparty":
//								errors.add("Empty cell at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + ") : : Mandatory Field cannot be empty.");
//								break;
//						}
//						errors.add("Empty cell at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + ") : : String cannot be empty.");
                    }
                }
            }
            if (errors.isEmpty()) {

                System.out.println("No validation errors found.");
            } else {
                System.out.println("Validation errors:");
                for (String error : errors) {
                    System.out.println(error);
                }
            }
        }
        return errors;
    }

    public List<com.tekclover.wms.core.model.dto.Error> validationFormatOutbound(List<String> validationErrors) throws JsonProcessingException {
        Map<String, Object> response = null;
        List<com.tekclover.wms.core.model.dto.Error> errorList = new ArrayList<>();
        for (String error : validationErrors) {
            String[] parts = error.split(":");
            String rowPart = parts[0];
            String message = parts[1].trim().concat(rowPart);

//			String extractedHeader = extractHeaderFromMessage(message);
//			if(extractedHeader != null) {
//				if (extractedHeader.equals("specialstock") && message.contains("Empty")) {
//					break;
//				}
//				else if(extractedHeader.equals("mtonumber") && message.contains("Empty")){
//					break;
//				}
//			}
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(rowPart);
            int extractedInteger=0;
            if (matcher.find()) {
                // Convert the extracted string to an integer
                extractedInteger = Integer.parseInt(matcher.group());
            }
            // Extract line number (e.g., "Row 2" -> 2)
//			int lineNo = Integer.parseInt(rowPart.replaceAll("\\D", ""));
            errorList.add(new Error(extractedInteger, message));
        }
        return errorList;
    }


    // Helper method to check if a row is empty
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
            Cell cell = row.getCell(colIndex);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }


    public static void validateStringCell(Cell cell, int rowIndex, int colIndex, String header, List<String> errors) {
        // Check if cell is blank
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            errors.add("Empty value at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + ") : String cannot be empty.");
        } else {
            // Check if the cell is either STRING or NUMERIC
            boolean conditionPass = cell.getCellType() == CellType.STRING || cell.getCellType() == CellType.NUMERIC;

            if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty()) {
                errors.add("Empty value at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + ") : String cannot be empty.");
            } else if (!conditionPass) {
                errors.add("Invalid data type at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + "): Expected String.");
            }
        }
    }

    public static void nullValidateStringCell(Cell cell, int rowIndex, int colIndex, String header, List<String> errors) {
        if (cell != null && cell.getCellType() != CellType.BLANK) {
            // Check if the cell is either STRING or NUMERIC
            boolean conditionPass = cell.getCellType() == CellType.STRING || cell.getCellType() == CellType.NUMERIC;
            if (!conditionPass) {
                errors.add("Invalid data type at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + ") : Expected String.");
            }
        }
    }

    public static void validateIntegerCell(Cell cell, int rowIndex, int colIndex, String header, List<String> errors) {
        // Check if the cell is blank first
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            errors.add("Empty value at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + ") : Integer cannot be empty.");
        } else {
            // Check if the cell is of numeric type
            if (cell.getCellType() != CellType.NUMERIC) {
                errors.add("Invalid data type at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + "): Expected Integer.");
            } else {
                double value = cell.getNumericCellValue();
                // Check if the value is an integer (i.e., no decimal part)
                if (value != (int) value) {
                    errors.add("Invalid value at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + "): Expected Integer.");
                }
            }
        }
    }

    private List<com.tekclover.wms.core.model.dto.Error> validationFormatInbound(List<String> validationErrors) {
        Map<String, Object> response = null;
        List<com.tekclover.wms.core.model.dto.Error> errorList = new ArrayList<>();
        for (String error : validationErrors) {
            String[] parts = error.split(":");
            String rowPart = parts[0];
            String message = parts[1].trim().concat(rowPart);

            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(rowPart);
            int extractedInteger = 0;
            if (matcher.find()) {
                // Convert the extracted string to an integer
                extractedInteger = Integer.parseInt(matcher.group());
            }
            // Extract line number (e.g., "Row 2" -> 2)
//			int lineNo = Integer.parseInt(rowPart.replaceAll("\\D", ""));
            errorList.add(new com.tekclover.wms.core.model.dto.Error(extractedInteger, message));
        }
        return errorList;
    }

    public static void validateDateCell(Cell cell, int rowIndex, int colIndex, String header, List<String> errors) {
        if (cell.getCellType() != CellType.NUMERIC || !DateUtil.isCellDateFormatted(cell)) {
            errors.add("Invalid data type at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": Expected Date.");
        } else if (cell.getDateCellValue() == null) {
            errors.add("Empty value at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": Date cannot be empty.");
        }

    }
}
