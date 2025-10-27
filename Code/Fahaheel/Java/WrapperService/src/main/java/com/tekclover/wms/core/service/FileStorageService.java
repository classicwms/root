package com.tekclover.wms.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.exception.BadRequestException;
import com.tekclover.wms.core.model.auth.AuthToken;
import com.tekclover.wms.core.model.dto.Error;
import com.tekclover.wms.core.model.masters.ImPartner;
import com.tekclover.wms.core.model.masters.StorageBinV2;
import com.tekclover.wms.core.model.transaction.*;
import com.tekclover.wms.core.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.core.model.warehouse.inbound.almailem.*;
import com.tekclover.wms.core.model.warehouse.mastersorder.ImBasicData1V2;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.*;
import com.tekclover.wms.core.repository.MailingReportRepository;
import com.tekclover.wms.core.util.CommonUtils;
import com.tekclover.wms.core.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
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
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    ExcelDataProcessService excelDataProcessService;

    @Autowired
    OutboundTransactionService outboundTransactionService;

    @Autowired
    MailingReportRepository mailingReportRepository;

    //-----------------------------------------------------------------------------------
    @Autowired
    InboundTransactionService transactionService;

    @Autowired
    OrderProcessingService orderPreparationService;

    @Autowired
    MastersService mastersService;


    //-----------------------------------------------------------------------------------

    private Path fileStorageLocation = null;

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    private String getTransactionAuthToken() {
        return authTokenService.getTransactionServiceAuthToken().getAccess_token();
    }

    private String getIDMasterServiceApiUrl() {
        return propertiesConfig.getIdmasterServiceUrl();
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
    public Map<String, String> storeFileV5(MultipartFile file, String companyCodeId, String plantId, String languageId, String warehouseId) throws Exception {
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
            List<ShipmentOrder> shipmentOrders = prepSOData(allRowsList);
            log.info("shipmentOrders : " + shipmentOrders);
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
            for (int rn = sheet.getFirstRowNum(); rn <= sheet.getLastRowNum(); rn++) {
                List<String> listUploadData = new ArrayList<String>();
                Row row = sheet.getRow(rn);
                log.info("Row:  " + row.getRowNum());
                if (row == null) {
                    // There is no data in this row, handle as needed
                } else if (row.getRowNum() != 0) {
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
    private List<ShipmentOrder> prepSOData(List<List<String>> allRowsList) {
        List<ShipmentOrder> shipmentOrderList = new ArrayList<>();

        for (List<String> listUploadedData : allRowsList) {
            Set<SOHeader> setSOHeader = new HashSet<>();
            List<SOLine> soLines = new ArrayList<>();

            // Header
            SOHeader soHeader = null;
            boolean oneTimeAllow = true;
            for (String column : listUploadedData) {
                if (oneTimeAllow) {
                    soHeader = new SOHeader();
                    soHeader.setRequiredDeliveryDate(listUploadedData.get(0));
                    soHeader.setStoreID(listUploadedData.get(1));
                    soHeader.setStoreName(listUploadedData.get(2));
                    soHeader.setTransferOrderNumber(listUploadedData.get(3));
                    soHeader.setWareHouseId(listUploadedData.get(4));
                    setSOHeader.add(soHeader);
                }
                oneTimeAllow = false;

                // Line
                SOLine soLine = new SOLine();
                soLine.setLineReference(Long.valueOf(listUploadedData.get(5)));
                soLine.setOrderType(listUploadedData.get(6));
                soLine.setOrderedQty(Double.valueOf(listUploadedData.get(7)));
                soLine.setSku(listUploadedData.get(8));
                soLine.setSkuDescription(listUploadedData.get(9));
                soLine.setUom(listUploadedData.get(10));
                soLines.add(soLine);
            }

            ShipmentOrder shipmentOrder = new ShipmentOrder();
            shipmentOrder.setSoHeader(soHeader);
            shipmentOrder.setSoLine(soLines);
            shipmentOrderList.add(shipmentOrder);
        }
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
    public Map<String, String> processAsnOrders(String companyCodeId, String plantId, String languageId,
                                                String warehouseId, Long orderTypeId, String loginUserId, MultipartFile file) throws Exception {
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

//			List<List<String>> allRowsList = readExcelData(targetLocation.toFile());
            List<InboundOrderProcessV4> allRowsList = excelDataProcessService.inboundReadExcelFile(companyCodeId, plantId, languageId, warehouseId, orderTypeId, loginUserId, file);

            if (allRowsList != null && !allRowsList.isEmpty()) {
                // Uploading Orders
                WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
                AuthToken authToken = authTokenService.getInboundOrderServiceAuthToken();
                List<ASNV2> asnV2Orders = prepAsnData(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                log.info("asnOrders : " + asnV2Orders);
                dbWarehouseApiResponse = transactionService.postASNV2Upload(asnV2Orders, "Uploaded", authToken.getAccess_token());

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
     * @param file
     * @return
     */
    public Map<String, String> processSalesOrders(String companyCodeId, String plantId, String languageId,
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
            List<SalesOrderV2> salesOrders = null;
            if (allRowsList != null && !allRowsList.isEmpty()) {
                if (companyCodeId.equalsIgnoreCase("21")) {  // ----------------------- AMGHARA
                    salesOrders = prepSalesOrderData(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("salesOrders : " + salesOrders);
                } else if (companyCodeId.equalsIgnoreCase("1400")) {    // -------------------- NAMRATHA
                    salesOrders = orderPreparationService.prepSalesOrderMultipleData(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("salesOrders : " + salesOrders);
                }
                WarehouseApiResponse dbWarehouseApiResponse = new WarehouseApiResponse();
                AuthToken authToken = authTokenService.getOutboundOrderServiceAuthToken();
                dbWarehouseApiResponse = outboundTransactionService.postSalesOrderList(salesOrders, authToken.getAccess_token());

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
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param allRowsList
     * @return
     */
    public List<SalesOrderV2> prepSalesOrderData(String companyCodeId, String plantId, String languageId,
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
                    soLine.setUom("EACH");
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
     * @param list
     * @return
     */
    private List<ASNV2> prepAsnData(String companyCodeId, String plantId,
                                    String languageId, String warehouseId, String loginUserId, List<InboundOrderProcessV4> list) {
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

//----------------------------------------------------------------------------------------------------------------------

    /**
     * @param list
     * @return
     */
    private List<ASNV2> prepAsnDataBP(String companyCodeId, String plantId,
                                    String languageId, String warehouseId, String loginUserId, List<InboundOrderProcessV4> list) {
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

//----------------------------------------------------------------------------------------------------------------------


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

//	public static void validateIntegerCell(Cell cell, int rowIndex, int colIndex,String header, List<String> errors) {
//		if (cell.getCellType() != CellType.NUMERIC) {
//			errors.add("Invalid data type at row " + (rowIndex + 1) + ", column " + (colIndex + 1)  + " (" + header + "): Expected Integer.");
//		} else {
//			double value = cell.getNumericCellValue();
//			if (value != (int) value) {
//				errors.add("Invalid value at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + "): Expected Integer.");
//			}
//		}
//	}

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
                            case "salesordernumber":
                            case "asnnumber":
                            case "skucode":
                            case "outbound":
                            case "inbound":
                            case "skutext":
                            case "uom":
                            case "vehicleno":
                            case "customerid":
                            case "customername":
                                validateStringCell(cell, rowIndex, colIndex, header, errors);
                                break;
//                            case "specialstock":
                            case "barcodeid":
                                nullValidateStringCell(cell, rowIndex, colIndex, header, errors);
                                break;
//                            case "noofpairs":
                            case "qty":
                            case "itm":
                            case "linereference":
                                validateIntegerCell(cell, rowIndex, colIndex, header, errors);
                                break;
//							case "expecteddate":
//							case "requireddeliverydate":
                            case "vehiclereportingdate":
                            case "vehicleunloadingdate":
//							case "date":
                                validateDateCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            default:
                                errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": " + header);
                                break;
                        }
                    } else {
                        switch (header) {
//                            case "huserialnumber":
//                            case "skucode":
                            case "salesordernumber":
                            case "asnnumber":
                            case "inbound":
                            case "qty":
                            case "skucode":
                            case "uom":
                            case "vehicleno":
//                            case "itm":
                            case "skutext":
                            case "vehiclereportingdate":
                            case "vehicleunloadingdate":
                                errors.add("Empty cell at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + ") : : Mandatory Field cannot be empty.");
                                break;
                        }
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

    private List<com.tekclover.wms.core.model.dto.Error> validationFormatInboundV7(List<String> validationErrors) {
        Map<String, Object> response = null;
        List<com.tekclover.wms.core.model.dto.Error> errorList = new ArrayList<>();
//        for (String error : validationErrors) {
//            String[] parts = error.split(":");
//            String rowPart = parts[0];
////            String message = parts[1].trim().concat(rowPart);
//            String  message = parts[1].trim() + " (" + rowPart + ")";
//            Pattern pattern = Pattern.compile("\\d+");
//            Matcher matcher = pattern.matcher(rowPart);
//            int extractedInteger = 0;
//            if (matcher.find()) {
//                // Convert the extracted string to an integer
//                extractedInteger = Integer.parseInt(matcher.group());
//            }
//            // Extract line number (e.g., "Row 2" -> 2)
////			int lineNo = Integer.parseInt(rowPart.replaceAll("\\D", ""));
//            errorList.add(new com.tekclover.wms.core.model.dto.Error(extractedInteger, message));
//        }
        for (String error : validationErrors) {
            String rowPart = "Row 0";
            String message = error;

            if (error.contains(":")) {
                String[] parts = error.split(":", 2); // Split into max 2 parts
                rowPart = parts[0].trim(); // e.g., "Row 2"
                message = parts[1].trim() + " (" + rowPart + ")";

                // Append extra hint if it's a date format issue
                if (parts[1].toLowerCase().contains("expecteddate") || parts[1].toLowerCase().contains("invalid date") || parts[1].toLowerCase().contains("date")) {
                    message += " (Expected format: yyyy-MM-dd)";
                }

            }

            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(rowPart);
            int extractedInteger = 0;
            if (matcher.find()) {
                extractedInteger = Integer.parseInt(matcher.group());
            }

            errorList.add(new com.tekclover.wms.core.model.dto.Error(extractedInteger, message));
        }


        return errorList;
    }

    private List<com.tekclover.wms.core.model.dto.Error> validationFormatInbound(List<String> validationErrors) {
        Map<String, Object> response = null;
        List<com.tekclover.wms.core.model.dto.Error> errorList = new ArrayList<>();
        for (String error : validationErrors) {
            String[] parts = error.split(":", 2); // limit = 2 to avoid over-splitting
            String rowPart = parts[0];
            String message;

            if (parts.length >= 2) {
                message = parts[1].trim() + " (" + rowPart + ")";
            } else {
                message = "Validation error: " + error;  // fallback message
            }

            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(rowPart);
            int extractedInteger = 0;
            if (matcher.find()) {
                extractedInteger = Integer.parseInt(matcher.group());
            }

            errorList.add(new com.tekclover.wms.core.model.dto.Error(extractedInteger, message));
        }
        return errorList;
    }


    /**
     * Upload V4 Dynamic
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
    public Map<String, String> processInboundOrdersV5(String companyCodeId, String plantId, String languageId,
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

            if (allRowsList != null && !allRowsList.isEmpty()) {
                // Uploading Orders
                WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
                AuthToken authToken = authTokenService.getInboundOrderServiceAuthToken();

                if (orderTypeId == 1L) {
                    List<ASNV2> asnV2Orders = orderPreparationService.prepAsnMultipleDataV4(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("asnOrders : " + asnV2Orders);
                    dbWarehouseApiResponse = transactionService.postASNV2Upload(asnV2Orders, loginUserId, authToken.getAccess_token());
                }
                if (orderTypeId == 3L) {
                    List<SaleOrderReturnV2> saleOrderReturns = orderPreparationService.prepSaleOrderReturnDataV5(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("saleOrderReturn : " + saleOrderReturns);
                    dbWarehouseApiResponse = transactionService.postSOReturnUploadV2(saleOrderReturns, authToken.getAccess_token());
                }

                if (orderTypeId == 2L) {
                    List<InterWarehouseTransferInV2> wh2whOrders = orderPreparationService.prepInterwareHouseInDataV5(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("wh2whOrders : " + wh2whOrders);
                    dbWarehouseApiResponse = transactionService.postInterWarehouseTransferInUploadV7(wh2whOrders, loginUserId, authToken.getAccess_token());
                }
                if (orderTypeId == 11L) {
                    List<ASNV2> asnV2Orders = orderPreparationService.prepAsnMultipleDataV5(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("asnOrders : " + asnV2Orders);
                    dbWarehouseApiResponse = transactionService.postEmptyUpload(asnV2Orders, loginUserId, authToken.getAccess_token());
                }

                if (dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return null;
    }

    /**
     * @param fileName
     * @return
     */
    private Map<String, String> uploadSuccessMessage(String fileName) {
        Map<String, String> mapFileProps = new HashMap<>();
        mapFileProps.put("file", fileName);
        mapFileProps.put("status", "UPLOADED SUCCESSFULLY");
        return mapFileProps;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserID
     * @param file
     * @return
     */
    public Map<String, String> processAsnOrdersV6(String companyCodeId, String plantId, String languageId, String warehouseId, String loginUserID, MultipartFile file) {
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
            if (allRowsList != null && !allRowsList.isEmpty()) {
//				List<ASNV2> asnV2Orders = orderPreparationService.prepAsnData(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                List<ASNV2> asnV2Orders = prepAsnMultipleData(companyCodeId, plantId, languageId, warehouseId, loginUserID, allRowsList);
                log.info("asnOrders : " + asnV2Orders);

                // Uploading Orders
                WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
                AuthToken authToken = authTokenService.getInboundOrderServiceAuthToken();
                dbWarehouseApiResponse = transactionService.postASNV2Upload(asnV2Orders, loginUserID, authToken.getAccess_token());

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

    private List<ASNV2> prepAsnMultipleData(String companyCodeId, String plantId, String languageId, String warehouseId, String loginUserID, List<List<String>> allRowsList) {
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
                    header.setLoginUserId(loginUserID);
                    header.setAsnNumber(listUploadedData.get(0));
                    header.setSupplierCode(listUploadedData.get(7));
                    if (listUploadedData.size() > 18 && listUploadedData.get(18) != null && !listUploadedData.get(18).isBlank()) {
                        header.setInboundOrderTypeId(Long.valueOf(listUploadedData.get(18)));
                    } else {
                        header.setInboundOrderTypeId(1L);
                    }
                }
                oneTimeAllow = false;

                // Line
                ASNLineV2 line = new ASNLineV2();
                line.setLineReference(Long.valueOf(listUploadedData.get(1)));
                line.setSku(listUploadedData.get(2));
                line.setSkuDescription(listUploadedData.get(3));
                line.setExpectedDate(listUploadedData.get(4));
                line.setInwardDate(listUploadedData.get(5));
                line.setExpectedQtyInPieces(Double.valueOf(listUploadedData.get(6)));
                line.setExpectedQty(Double.valueOf(listUploadedData.get(6)));
                line.setExpectedQtyInCases(Double.valueOf(listUploadedData.get(7)));
                line.setNoBags(Double.valueOf(listUploadedData.get(7)));
                line.setMrp(Double.valueOf(listUploadedData.get(8)));
                line.setUnloadingIncharge(listUploadedData.get(9));
                line.setTotalUnLoaders(Long.valueOf(listUploadedData.get(10)));
                line.setUom("PIECE");

                line.setBranchCode(plantId);
                line.setCompanyCode(companyCodeId);

//                if (listUploadedData.size() > 11 && !listUploadedData.get(8).trim().isEmpty()) {
//                    line.setUom(listUploadedData.get(8));
//                } else {
//                    line.setUom(UOM);
//                }
//                if (listUploadedData.size() > 15 && !listUploadedData.get(15).trim().isEmpty()) {
//                    line.setOrigin(listUploadedData.get(15));
//                }
//                if (listUploadedData.size() > 16 && !listUploadedData.get(16).trim().isEmpty()) {
//                    line.setBrand(listUploadedData.get(16));
//                }
//                if (listUploadedData.size() > 17 && listUploadedData.get(17) != null && !listUploadedData.get(17).trim().isEmpty()) {
//                    line.setPackQty(Double.valueOf(listUploadedData.get(17)));
//                }
//                if (listUploadedData.size() > 18 && listUploadedData.get(18) != null && !listUploadedData.get(18).isBlank()) {
//                    line.setInboundOrderTypeId(Long.valueOf(listUploadedData.get(18)));
//                } else {
//                    header.setInboundOrderTypeId(1L);
//                }

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
     * @param orderTypeId
     * @param loginUserId
     * @param file
     * @return
     */
    public Map<String, String> processOutboundOrdersV5(String companyCodeId, String plantId, String languageId,
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
        if (!validationErrors.isEmpty()) {
            List<Error> errors = validationFormatOutbound(validationErrors);
            if (!errors.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errors);
                Map<String, String> mapFileProps = new HashMap<>();
                mapFileProps.put("errors", jsonResponse);
                return mapFileProps;
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
            List<OutboundOrderProcessV4> allRowsList = excelDataProcessService.outboundReadExcelFileV5(companyCodeId, plantId, languageId, warehouseId, orderTypeId, loginUserId, file);
            if (allRowsList != null && !allRowsList.isEmpty()) {
                WarehouseApiResponse dbWarehouseApiResponse = new WarehouseApiResponse();
                AuthToken authToken = authTokenService.getOutboundTransactionServiceAuthToken();
                if (orderTypeId != null) {
                    if (orderTypeId == 3L) {
                        List<SalesOrderV2> salesOrders = orderPreparationService.prepSalesOrderDataV5(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                        log.info("salesOrders : " + salesOrders);
                        dbWarehouseApiResponse = outboundTransactionService.postSalesOrderV5(salesOrders, authToken.getAccess_token());
                    }
                    if (orderTypeId == 11L) {
                        List<SalesOrderV2> emptyCrateOrders = orderPreparationService.emptyCrateV5(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                        log.info("emptyCrateOrders : " + emptyCrateOrders);
                        dbWarehouseApiResponse = outboundTransactionService.emptyCrateOrderV5(emptyCrateOrders, authToken.getAccess_token());
                    }
                    if (orderTypeId == 2L) {
                        List<ReturnPOV2> returnPO = orderPreparationService.purchaseReturn(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                        log.info("returnPO : " + returnPO);
                        dbWarehouseApiResponse = outboundTransactionService.postReturnPOV5(returnPO, authToken.getAccess_token());
                    }

                }
                if (dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
                }
            }
        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return null;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param orderTypeId
     * @param loginUserId
     * @param file
     * @return
     */
    public Map<String, String> processOutboundOrdersV7(String companyCodeId, String plantId, String languageId,
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

        List<String> validationErrors = validationOutboundKnowellDynamically(file);
        if (!validationErrors.isEmpty()) {
            List<Error> errors = validationFormatOutboundV7(validationErrors);
            if (!errors.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errors);
                Map<String, String> mapFileProps = new HashMap<>();
                mapFileProps.put("errors", jsonResponse);
                return mapFileProps;
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
            List<OutboundOrderProcessV4> allRowsList = excelDataProcessService.outboundReadExcelFileV7(companyCodeId, plantId, languageId, warehouseId, orderTypeId, loginUserId, file);
            if (allRowsList != null && !allRowsList.isEmpty()) {
                WarehouseApiResponse dbWarehouseApiResponse = new WarehouseApiResponse();
                AuthToken authToken = authTokenService.getOutboundTransactionServiceAuthToken();
                if (orderTypeId == 3L) {
                    List<SalesOrderV2> salesOrders = orderPreparationService.prepSalesOrderDataV7(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("salesOrders : " + salesOrders);
                    dbWarehouseApiResponse = outboundTransactionService.postSalesOrderV7(salesOrders, authToken.getAccess_token());
                }
                if(orderTypeId == 1L) {
                    List<InterWarehouseTransferInV2> wh2whOrders = orderPreparationService.prepInterwareHouseInDataV8(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("wh2whOrders : " + wh2whOrders);
                    AuthToken outboundTransactionServiceAuthToken = authTokenService.getOutboundTransactionServiceAuthToken();
                    dbWarehouseApiResponse = outboundTransactionService.postInterWarehouseTransferInUploadV8(wh2whOrders, loginUserId, outboundTransactionServiceAuthToken.getAccess_token());
                }
                if (dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return null;
    }

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
                            case "skucode":
                            case "outbound":
                            case "skutext":
                            case "customer":
                            case "customercode":
                            case "uom":
                            case "barcodeid":
                            case "returnorderno":
                            case "suppliername":
                                validateStringCell(cell, rowIndex, colIndex, header, errors);
                                break;
//                            case "specialstock":
//                            case "mtonumber":
//                                nullValidateStringCell(cell, rowIndex, colIndex, header, errors);
//                                break;
//							case "noofpairs":
                            case "qty":
                            case "itm":
                            case "returnqty":
                            case "linereference":
                                validateIntegerCell(cell, rowIndex, colIndex, header, errors);
                                break;
//							case "expecteddate":
//							case "requireddeliverydate":
//							case "date":
//								validateDateCell(cell,rowIndex,colIndex,header,errors);
//								break;
                            default:
                                errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": " + header);
                                break;
                        }
                    } else {
                        switch (header) {
                            case "skucode":
                            case "outbound":
                            case "qty":
                            case "skutext":
                            case "customer":
                            case "customercode":
                            case "uom":
                                errors.add("Empty cell at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + ") : : Mandatory Field cannot be empty.");
                                break;
                        }
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

    private List<String> validationOutboundKnowellDynamically(MultipartFile file) throws IOException {
        List<String> errors = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                errors.add("Header row is missing.");
                return errors;
            }

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isRowEmpty(row)) continue;

                for (int colIndex = 0; colIndex < headerRow.getPhysicalNumberOfCells(); colIndex++) {
                    Cell headerCell = headerRow.getCell(colIndex);
                    if (headerCell == null) continue;

                    String header = headerCell.getStringCellValue().trim().toLowerCase();
                    Cell cell = row.getCell(colIndex); // May be null

                    boolean isMandatory = false;
                    switch (header) {
                        case "salesorderno":
                        case "customername":
                        case "linereference":
                        case "sku":
                        case "qtyinpcs":
                        case "transferorderno":
                        case "tocompany":
                        case "tobranch":
                        case "godownname":
                            //case "requireddeliverydate":
                        case "lineno":
                        case "itemcode":
                        case "orderqty":
                        case "caseqty":
                        case "sourcecompany":
                        case "sourcebranch":
                            isMandatory = true;
                            break;
                    }

                    if (!isMandatory && !header.equals("customerid") &&
                            !header.equals("skudescription") &&
                            !header.equals("qtyincases") &&
                            !header.equals("requireddeliverydate") &&
                            !header.equals("mrp") &&
                            !header.equals("storagelocation") &&
                            !header.equals("pickername") &&
                            !header.equals("totalpickers") &&
                            !header.equals("itemtext") &&
                            !header.equals("uom") &&
                            !header.equals("ordertype") &&
                            !header.equals("manufacturercode") &&
                            !header.equals("brand") &&
                            !header.equals("manufacturername")) {
                        errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": " + header);
                        continue;
                    }

                    if (cell == null) {
                        if (isMandatory) {
                            errors.add("Missing value for mandatory field '" + header + "' at row " + (rowIndex + 1) + ", column " + (colIndex + 1));
                        }
                        continue;
                    }

                    // Field-specific validation
                    switch (header) {
                        case "salesorderno":
                        case "customername":
                        case "sku":
                        case "transferorderno":
                        case "tocompanycode":
                        case "tobranchcode":
                        case "godownname":
                        case "itemcode":
                        case "itemtext":
                        case "uom":
                        case "ordertype":
                        case "manufacturercode":
                        case "brand":
                        case "manufacturername":
                        case "sourcecompany":
                        case "sourcebranch":
                            validateStringCell(cell, rowIndex, colIndex, header, errors);
                            break;
                        case "linereference":
                        case "qtyinpcs":
                        case "lineno":
                        case "orderqty":
                        case "caseqty":
                            validateIntegerCell(cell, rowIndex, colIndex, header, errors);
                            break;
                        case "requireddeliverydate":
                            if (cell.getCellType() != CellType.BLANK) {
                                validateDateCell(cell, rowIndex, colIndex, header, errors);
                            }
                            break;
                        // Other optional fields are not validated
                    }
                }
            }

            if (errors.isEmpty()) {
                log.info("No validation errors found.");
            } else {
                log.info("Validation errors:");
                errors.forEach(log::info);
            }
        }

        return errors;
    }


//    private List<String> validationOutboundKnowellDynamically(MultipartFile file) throws IOException {
//        List<String> errors = new ArrayList<>();
//
//        try (InputStream inputStream = file.getInputStream()) {
//            Workbook workbook = new XSSFWorkbook(inputStream);
//            Sheet sheet = workbook.getSheetAt(0);
//
//            Row headerRow = sheet.getRow(0);
//            if (headerRow == null) {
//                errors.add("Header row is missing.");
//                return errors;
//            }
//
//            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
//                Row row = sheet.getRow(rowIndex);
//                if (row == null || isRowEmpty(row)) continue;
//
//                for (int colIndex = 0; colIndex < headerRow.getPhysicalNumberOfCells(); colIndex++) {
//                    Cell headerCell = headerRow.getCell(colIndex);
//                    if (headerCell == null) continue;
//
//                    String header = headerCell.getStringCellValue().trim().toLowerCase();
//                    Cell cell = row.getCell(colIndex); // May be null
//
//                    boolean isMandatory = false;
//                    switch (header) {
//                        case "salesorderno":
//                        case "customername":
//                        case "linereference":
//                        case "sku":
//                        case "qtyinpcs":
//                        case "transferorderno":
//                        case "tocompany":
//                        case "tobranch":
//                        case "godownname":
//                        //case "requireddeliverydate":
//                        case "lineno":
//                        case "itemcode":
//                        case "orderqty":
//                        case "caseqty":
//                        case "sourcecompany":
//                        case "sourcebranch":
//                            isMandatory = true;
//                            break;
//                    }
//
//                    if (!isMandatory && !header.equals("customerid") &&
//                            !header.equals("skudescription") &&
//                            !header.equals("qtyincases") &&
//                            !header.equals("requireddeliverydate") &&
//                            !header.equals("mrp") &&
//                            !header.equals("storagelocation") &&
//                            !header.equals("pickername") &&
//                            !header.equals("totalpickers") &&
//                            !header.equals("itemtext") &&
//                            !header.equals("uom") &&
//                            !header.equals("ordertype") &&
//                            !header.equals("manufacturercode") &&
//                            !header.equals("brand") &&
//                            !header.equals("manufacturername")) {
//                        errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": " + header);
//                        continue;
//                    }
//
//                    if (cell == null) {
//                        if (isMandatory) {
//                            errors.add("Missing value for mandatory field '" + header + "' at row " + (rowIndex + 1) + ", column " + (colIndex + 1));
//                        }
//                        continue;
//                    }
//
//                    // Field-specific validation
//                    switch (header) {
//                        case "salesorderno":
//                        case "customername":
//                        case "sku":
//                        case "transferorderno":
//                        case "tocompanycode":
//                        case "tobranchcode":
//                        case "godownname":
//                        case "itemcode":
//                        case "itemtext":
//                        case "uom":
//                        case "ordertype":
//                        case "manufacturercode":
//                        case "brand":
//                        case "manufacturername":
//                        case "sourcecompany":
//                        case "sourcebranch":
//                            validateStringCell(cell, rowIndex, colIndex, header, errors);
//                            break;
//                        case "linereference":
//                        case "qtyinpcs":
//                        case "lineno":
//                        case "orderqty":
//                        case "caseqty":
//                            validateIntegerCell(cell, rowIndex, colIndex, header, errors);
//                            break;
//                        case "requireddeliverydate":
//                            if (cell.getCellType() != CellType.BLANK) {
//                                validateDateCell(cell, rowIndex, colIndex, header, errors);
//                            }
//                            break;
//                        // Other optional fields are not validated
//                    }
//                }
//            }
//
//            if (errors.isEmpty()) {
//                log.info("No validation errors found.");
//            } else {
//                log.info("Validation errors:");
//                errors.forEach(log::info);
//            }
//        }
//
//        return errors;
//    }


    public List<Error> validationFormatOutboundV7(List<String> validationErrors) throws JsonProcessingException {
        Map<String, Object> response = null;
        List<com.tekclover.wms.core.model.dto.Error> errorList = new ArrayList<>();
        for (String error : validationErrors) {
            String[] parts = error.split(":", 2); // limit = 2 to avoid over-splitting
            String rowPart = parts[0];
            String message;

            if (parts.length >= 2) {
                message = parts[1].trim() + " (" + rowPart + ")";
            } else {
                message = "Validation error: " + error;  // fallback message
            }

            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(rowPart);
            int extractedInteger = 0;
            if (matcher.find()) {
                extractedInteger = Integer.parseInt(matcher.group());
            }

            errorList.add(new com.tekclover.wms.core.model.dto.Error(extractedInteger, message));
        }
        return errorList;
    }
//
    public List<Error> validationFormatOutbound(List<String> validationErrors) throws JsonProcessingException {
        Map<String, Object> response = null;
        List<Error> errorList = new ArrayList<>();
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
            int extractedInteger = 0;
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

    //=================================================Knowell============================================//

    /**
     * Upload V4 Dynamic
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
    public Map<String, String> processInboundOrdersV7(String companyCodeId, String plantId, String languageId,
                                                      String warehouseId, Long orderTypeId, String loginUserId, MultipartFile file) throws Exception {
        this.fileStorageLocation = Paths.get(propertiesConfig.getFileUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new BadRequestException(
                        "Could not create the directory where the uploaded files will be stored.");
            }
        }

        List<String> validationErrors = knowellDynamicInboundValidation(file);
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
            List<InboundOrderProcessV4> allRowsList = excelDataProcessService.inboundReadExcelFileKnowell(companyCodeId, plantId, languageId, warehouseId, orderTypeId, loginUserId, file);

            if (allRowsList != null && !allRowsList.isEmpty()) {
                // Uploading Orders
                WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
                AuthToken authToken = authTokenService.getInboundOrderServiceAuthToken();

                if (orderTypeId == 1L) {
                    List<ASNV2> asnV2Orders = orderPreparationService.prepAsnMultipleDataV7(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("asnOrders : " + asnV2Orders);
                    dbWarehouseApiResponse = transactionService.postASNV2Upload(asnV2Orders, loginUserId, authToken.getAccess_token());
                }

                if (orderTypeId == 3L) {
                    List<SaleOrderReturnV2> saleOrderReturns = orderPreparationService.prepSaleOrderReturnDataV4(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("saleOrderReturn : " + saleOrderReturns);
                    dbWarehouseApiResponse = transactionService.postSOReturnUploadV2(saleOrderReturns, authToken.getAccess_token());
                }

                if (orderTypeId == 4L) {
                    List<InterWarehouseTransferInV2> wh2whOrders = orderPreparationService.prepInterwareHouseInDataV7(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("wh2whOrders : " + wh2whOrders);
                    AuthToken inboundTransactionServiceAuthToken = authTokenService.getInboundTransactionServiceAuthToken();
                    dbWarehouseApiResponse = transactionService.postInterWarehouseTransferInUploadV7(wh2whOrders, loginUserId, inboundTransactionServiceAuthToken.getAccess_token());
                }

                if (dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return null;
    }

//    private List<String> knowellDynamicInboundValidation(MultipartFile file) throws IOException {
//        List<String> errors = new ArrayList<>();
//
//        try (InputStream inputStream = file.getInputStream()) {
//            Workbook workbook = new XSSFWorkbook(inputStream);
//            Sheet sheet = workbook.getSheetAt(0);
//
//            Row headerRow = sheet.getRow(0);
//            if (headerRow == null) {
//                errors.add("Header row is missing.");
//                return errors;
//            }
//
//            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
//                Row row = sheet.getRow(rowIndex);
//                if (row == null) continue;
//
//                for (int colIndex = 0; colIndex < headerRow.getPhysicalNumberOfCells(); colIndex++) {
//                    Cell headerCell = headerRow.getCell(colIndex);
//                    if (headerCell == null) continue;
//
//                    String header = headerCell.getStringCellValue().trim().toLowerCase();
//                    Cell cell = row.getCell(colIndex); // May be null
//
//                    boolean isMandatory = false;
//                    switch (header) {
//                        case "ordernumber":
//                        case "returnorderno":
//                        case "salesordernumber":
//                        case "linereference":
//                        case "sku":
//                        case "expecteddate":
//                        case "expectedqtyinpcs":
//                        //case "expectedqtyincases":
//                        case "transferorderno":
//                        case "tocompanycode":
//                        case "tobranchcode":
//                        case "godownname":
//                        //case "requireddeliverydate":
//                        //case "lineno":
//                        case "itemcode":
//                        case "orderqty":
//                        //case "caseqty":
//                        case "sourcecompany":
//                        case "sourcebranch":
//                            isMandatory = true;
//                            break;
//                        case "skudescription":
//                        case "itemtext":
//                        case "uom":
//                        case "ordertype":
//                        case "manufacturercode":
//                        case "brand":
//                        case "manufacturername":
//                        case "requireddeliverydate":
//                        case "lineno":
//                        case "caseqty":
//                        case "expectedqtyincases":
//                            isMandatory = false;
//                            break;
//                        default:
//                            errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": " + header);
//                            break;
//                    }
//
//                    if (cell == null) {
//                        if (isMandatory) {
//                            errors.add("Missing value for mandatory field '" + header + "' at row " + (rowIndex + 1) + ", column " + (colIndex + 1));
//                        }
//                        continue;
//                    }
//
//                    // Validate based on header
//                    switch (header) {
//                        case "ordernumber":
//                        case "returnorderno":
//                        case "salesordernumber":
//                        case "sku":
//                        case "transferorderno":
//                        case "tocompanycode":
//                        case "tobranchcode":
//                        case "godownname":
//                        case "itemcode":
//                        case "itemtext":
//                        case "uom":
//                        case "manufacturercode":
//                        case "brand":
//                        case "manufacturername":
//                        case "sourcecompany":
//                        case "sourcebranch":
//                            validateStringCell(cell, rowIndex, colIndex, header, errors);
//                            break;
//                        case "linereference":
//                        case "expectedqtyinpcs":
//                        case "expectedqtyincases":
//                        case "lineno":
//                        case "orderqty":
//                        case "ordertype":
//                        case "caseqty":
//                            validateIntegerCell(cell, rowIndex, colIndex, header, errors);
//                            break;
//                        case "expecteddate":
//                        case "requireddeliverydate":
//                        case "date":
//                            validateDateCell(cell, rowIndex, colIndex, header, errors);
//                            break;
//                    }
//                }
//            }
//
//            if (errors.isEmpty()) {
//                log.info("No validation errors found.");
//            } else {
//                log.info("Validation errors:");
//                errors.forEach(log::info);
//            }
//        }
//
//        return errors;
//    }

    private List<String> knowellDynamicInboundValidation(MultipartFile file) throws IOException {
        List<String> errors = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                errors.add("Header row is missing.");
                return errors;
            }

            short lastCol = headerRow.getLastCellNum(); // Use last cell number for safer looping

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                for (int colIndex = 0; colIndex < lastCol; colIndex++) {
                    Cell headerCell = headerRow.getCell(colIndex);
                    if (headerCell == null || headerCell.getCellType() == CellType.BLANK) continue;

                    String header = headerCell.getStringCellValue().trim().toLowerCase();
                    if (header.isEmpty()) continue;

                    boolean isMandatory = false;

                    switch (header) {
                        case "ordernumber":
                        case "returnorderno":
                        case "salesordernumber":
                        case "linereference":
                        case "sku":
                        case "expecteddate":
                        case "expectedqtyinpcs":
                        case "transferorderno":
                        case "tocompanycode":
                        case "tobranchcode":
                        case "godownname":
                        case "itemcode":
                        case "orderqty":
                        case "sourcecompany":
                        case "sourcebranch":
                            isMandatory = true;
                            break;
                        case "skudescription":
                        case "itemtext":
                        case "uom":
                        case "ordertype":
                        case "manufacturercode":
                        case "brand":
                        case "manufacturername":
                        case "requireddeliverydate":
                        case "lineno":
                        case "caseqty":
                        case "expectedqtyincases":
                            isMandatory = false;
                            break;
                        default:
                            errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": " + header);
                            continue;
                    }

                    Cell cell = row.getCell(colIndex);

                    if (cell == null || cell.getCellType() == CellType.BLANK) {
                        if (isMandatory) {
                            errors.add("Missing value for mandatory field '" + header + "' at row " + (rowIndex + 1) + ", column " + (colIndex + 1));
                        }
                        continue;
                    }

                    // Field-specific validation
                    switch (header) {
                        case "ordernumber":
                        case "returnorderno":
                        case "salesordernumber":
                        case "sku":
                        case "transferorderno":
                        case "tocompanycode":
                        case "tobranchcode":
                        case "godownname":
                        case "itemcode":
                        case "itemtext":
                        case "uom":
                        case "manufacturercode":
                        case "brand":
                        case "manufacturername":
                        case "sourcecompany":
                        case "sourcebranch":
                            validateStringCell(cell, rowIndex, colIndex, header, errors);
                            break;

                        case "linereference":
                        case "expectedqtyinpcs":
                        case "expectedqtyincases":
                        case "lineno":
                        case "orderqty":
                        case "ordertype":
                        case "caseqty":
                            validateIntegerCell(cell, rowIndex, colIndex, header, errors);
                            break;

                        case "expecteddate":
                        case "requireddeliverydate":
                        case "date":
                            validateDateCell(cell, rowIndex, colIndex, header, errors);
                            break;
                    }
                }
            }

            if (errors.isEmpty()) {
                log.info("No validation errors found.");
            } else {
                log.info("Validation errors:");
                errors.forEach(log::info);
            }
        }

        return errors;
    }
    //=================================helper class==============================================//
    // Validate date cell (ensure it's a date and not empty)
    public static void validateDateCell(Cell cell, int rowIndex, int colIndex, String header, List<String> errors) {
        if (cell.getCellType() != CellType.NUMERIC || !DateUtil.isCellDateFormatted(cell)) {
            errors.add("Invalid data type at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": Expected Date.");
        } else if (cell.getDateCellValue() == null) {
            errors.add("Empty value at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": Date cannot be empty.");
        }
    }
//-----------------------------------Bharath-Package Inbound-------------------------------------------

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
     * @throws Exception
     */

    public Map<String, String> processInboundOrders(String companyCodeId, String plantId, String languageId,
                                                    String warehouseId, Long orderTypeId,String loginUserId, MultipartFile file) throws Exception {
        this.fileStorageLocation = Paths.get(propertiesConfig.getFileUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new BadRequestException(
                        "Could not create the directory where the uploaded files will be stored.");
            }
        }

        List<String> validationErrors = null;

        if (orderTypeId.equals(1L)){
            validationErrors = inboundRMValidation(file);
        }

        if (orderTypeId.equals(12L)){
            validationErrors = inboundFGValidation(file);
        }

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

            List<InboundOrderProcessV4> allRowsList = null;

            if (orderTypeId.equals(1L)){
                allRowsList = excelDataProcessService.inboundRMReadExcelFile(companyCodeId, plantId, languageId, warehouseId, orderTypeId, loginUserId, file);
            }
            if (orderTypeId.equals(12L)){
                allRowsList = excelDataProcessService.inboundFGReadExcelFile(companyCodeId, plantId, languageId, warehouseId, orderTypeId, loginUserId, file);
            }
            if (allRowsList != null && !allRowsList.isEmpty()) {
                // Uploading Orders
                WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
                AuthToken authToken = authTokenService.getInboundOrderServiceAuthToken();
                if (orderTypeId.equals(1L) || orderTypeId.equals(12L)) {
                    List<ASNV2> asnV2Orders = prepAsnDataBP(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                    log.info("asnOrders : " + asnV2Orders);
                    dbWarehouseApiResponse = transactionService.inboundOrderUpload(asnV2Orders, authToken.getAccess_token());
                }
                if (dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return null;
    }
//-----------------------------------------Bharath Package Inbound Validation-------------------------------------------
    // validation for RM Inbound
    private List<String> inboundRMValidation(MultipartFile file) throws IOException {
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
                            case "ponumber":
                            case "potype":
                            case "fsctype":
                            case "itemcode":
                            case "itemdescription":
                            case "unitofmeasure":
                            case "suppliercode":
                            case "suppliername":
                            case "poamendmentno":
                            case "billercode":
                            case "billername":
                                validateStringCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            case "qty":
                                validateIntegerCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            case "podate":
                            case "poamendmentdate":
                                validateDateCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            default:
                                errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": " + header);
                                break;
                        }
                    } else {
                        switch (header) {
                            case "ponumber":
                            case "itemcode":
                            case "qty":
                            case "podate":
                                errors.add("Empty cell at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + ") : : Mandatory Field cannot be empty.");
                                break;
                        }
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

    // validation for FG Inbound
    private List<String> inboundFGValidation(MultipartFile file) throws IOException {
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
                            case "workorderno":
                            case "fgitemcode":
                            case "fgitemdescription":
                            case "fgunitofmeasure":
                            case "fgreceiptdocumentnumber":
                                validateStringCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            case "workorderqty":
                            case "finishedqty":
                                validateIntegerCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            case "fginwarddate":
                                validateDateCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            default:
                                errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": " + header);
                                break;
                        }
                    } else {
                        switch (header) {
                            case "workorderno":
                            case "fgitemcode":
                            case "workorderqty":
                            case "fginwarddate":
                                errors.add("Empty cell at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + ") : : Mandatory Field cannot be empty.");
                                break;
                        }
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

//-----------------------------------Bharath-Package Outbound-------------------------------------------
    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param file
     * @return
     */
    public Map<String, String> productionOrderUploadV6(String companyCodeId, String plantId, String languageId,
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

        List<String> validationErrors = null;

        if (orderTypeId.equals(3L)){
            validationErrors = OutboundRMValidation(file);
        }
        if (orderTypeId.equals(1L)){
            validationErrors = OutboundFGValidation(file);
        }
        if (!validationErrors.isEmpty()) {
            List<Error> errors = validationFormatOutbound(validationErrors);
            if (!errors.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errors);
                Map<String, String> mapFileProps = new HashMap<>();
                mapFileProps.put("errors", jsonResponse);
                return mapFileProps;
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

            List<OutboundOrderProcessV4> allRowsList = null;
            if (orderTypeId.equals(3L)){
                allRowsList = excelDataProcessService.outboundReadExcelFileRM(companyCodeId, plantId, languageId, warehouseId, orderTypeId, loginUserId, file);
            }
            if (orderTypeId.equals(1L)){
                allRowsList = excelDataProcessService.outboundReadExcelFileFG(companyCodeId, plantId, languageId, warehouseId, orderTypeId, loginUserId, file);
            }
            if (allRowsList != null && !allRowsList.isEmpty()) {
                WarehouseApiResponse dbWarehouseApiResponse = new WarehouseApiResponse();
                AuthToken authToken = authTokenService.getOutboundOrderServiceAuthToken();

                List<SalesOrderV2> salesOrders = orderPreparationService.prepSalesOrderDataV6(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                log.info("salesOrders : " + salesOrders);

                if(orderTypeId.equals(3L)) {
                    dbWarehouseApiResponse = outboundTransactionService.productionOrderUploadRM(salesOrders, authToken.getAccess_token());
                } else if (orderTypeId.equals(1L)) {
                    dbWarehouseApiResponse = outboundTransactionService.productionOrderUploadFG(salesOrders, authToken.getAccess_token());
                }
                if (dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }

        
        return null;
    }

//---------------------------------------Bharath Package Outbound Validation--------------------------------------------
//Validation for RM Outbound
    private List<String> OutboundRMValidation(MultipartFile file) throws IOException {
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
                            case "workorderno":
                            case "fgitemcode":
                            case "fgitemdescription":
                            case "fgunitofmeasure":
                            case "departmentrequested":
                            case "rmitemcode":
                            case "rmitemdescription":
                            case "rmweightrequired":
                            case "rmunitofmeasure":
                            case "rmstock":
                                validateStringCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            case "fgqty":
                            case "noofreelsrequired":
                                validateIntegerCell(cell, rowIndex, colIndex, header, errors);
                                break;

                            case "requireddate":
								validateDateCell(cell,rowIndex,colIndex,header,errors);
								break;
                            default:
                                errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": " + header);
                                break;
                        }
                    } else {
                        switch (header) {
                            case "workorderno":
                            case "rmitemcode":
                            case "fgqty":
                            case "requireddate":
                                errors.add("Empty cell at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + ") : : Mandatory Field cannot be empty.");
                                break;
                        }
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

//Validation for FG Outbound
    private List<String> OutboundFGValidation(MultipartFile file) throws IOException {
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
                            case "saleorderno":
                            case "customercode":
                            case "customername":
                            case "salerorderamendmentno":
                            case "status":
                            case "fgitemcode":
                            case "fgitemdescription":
                            case "fgunitofmeasure":
                                validateStringCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            case "fgorderqty":
                                validateIntegerCell(cell, rowIndex, colIndex, header, errors);
                                break;

                            case "saleorderdate":
                            case "salerorderamendmentdate":
                            case "deliverydate":
                            case "custreqddate":
                                validateDateCell(cell,rowIndex,colIndex,header,errors);
                                break;
                            default:
                                errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": " + header);
                                break;
                        }
                    } else {
                        switch (header) {
                            case "saleorderno":
                            case "fgitemcode":
                            case "fgorderqty":
                            case "deliverydate":
                                errors.add("Empty cell at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + ") : : Mandatory Field cannot be empty.");
                                break;
                        }
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

//--------------------------------------------------------------------------------------------------------------------

    public Map<String, String> processStorageBin(String companyCodeId, String plantId, String languageID,
                                                 String warehouseId,String loginUserId, MultipartFile file) throws Exception {
        this.fileStorageLocation = Paths.get(propertiesConfig.getFileUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new BadRequestException(
                        "Could not create the directory where the uploaded files will be stored.");
            }
        }

        List<String> validationErrors = validationStorageBin(file);
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

            List<StorageBinV2> allRowsList = excelDataProcessService.storageBinReadExcelFile(companyCodeId, plantId, languageID, warehouseId, loginUserId, file);

            if (allRowsList != null && !allRowsList.isEmpty()) {
                // Uploading
                WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
                List<StorageBinV2> storageBin = storageBin(companyCodeId, plantId, languageID, warehouseId, loginUserId, allRowsList);
                log.info("storage bin size " + storageBin.size());
                AuthToken authToken = authTokenService.getMastersServiceAuthToken();
                dbWarehouseApiResponse = mastersService.postStorageBin(storageBin, authToken.getAccess_token());

                if (dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return null;
    }

    // validation for storageBin
    private List<String> validationStorageBin(MultipartFile file) throws IOException {
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
                            case "storagebin" :
                            case "storagesectionid" :
                            case "rowid" :
                            case "aislenumber" :
                            case "spanid" :
                            case "shelfid" :
                            case "description" :
                            case "binbarcode" :
                            case "blockreason" :
                            case "occupiedvolume" :
                            case "occupiedweight" :
                            case "occupiedquantity" :
                            case "remainingvolume" :
                            case "remainingweight" :
                            case "remainingquantity" :
                            case "totalvolume" :
                            case "totalweight" :
                            case "totalquantity" :
                            case "capacitycheck" :
                                validateStringCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            case "statusid" :
                            case "allocatedvolume" :
                            case "binclassid" :
                            case "binsectionid" :
                            case "floorid" :
                            case "storagetypeid" :
                            case "putawayblock" :
                            case "pickingblock" :
                                validateIntegerCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            default:
                                errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": " + header);
                                break;
                        }
                    } else
                        switch (header) {
                            case "storagebin" :
                            case "binclassid" :
                                errors.add("Empty cell at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + ") : : Mandatory Field cannot be empty.");
                                break;
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

    private List<StorageBinV2> storageBin(String companyCodeId, String plantId,
                                          String languageId, String warehouseId, String loginUserID, List<StorageBinV2> list) {
        List<StorageBinV2> allRowsList = list.stream().sorted(Comparator.comparing(StorageBinV2::getStorageBin)).collect(Collectors.toList());
        List<StorageBinV2> saveStorageBin = new ArrayList<>();

        for (StorageBinV2 storageBinV2 : allRowsList) {
            StorageBinV2 storageBin = new StorageBinV2();
            BeanUtils.copyProperties(storageBinV2,storageBin,CommonUtils.getNullPropertyNames(storageBinV2));
            storageBin.setCompanyCodeId(companyCodeId);
            storageBin.setPlantId(plantId);
            storageBin.setWarehouseId(warehouseId);
            storageBin.setLanguageId(languageId);
            storageBin.setCreatedBy(loginUserID);
            saveStorageBin.add(storageBin);
        }

        return saveStorageBin;
    }


    //----------------------------New ImBasicData1 Upload ----------------------------------------
    public Map<String, String> processImBasicData1(String companyCodeId, String plantId, String languageId,
                                                   String warehouseId, String loginUserID,  MultipartFile file) throws Exception {
        this.fileStorageLocation = Paths.get(propertiesConfig.getFileUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new BadRequestException(
                        "Could not create the directory where the uploaded files will be stored.");
            }
        }

        List<String> validationErrors = validationImBasicData(file);
        if (!validationErrors.isEmpty()) {
            List<Error> errors = validationFormat(validationErrors);
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

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied : " + targetLocation);

            List<ImBasicData1V2> allRowsList = excelDataProcessService.readExcelFile(companyCodeId, plantId, languageId, warehouseId, loginUserID, file);

            if (allRowsList != null && !allRowsList.isEmpty()) {
                // Uploading
                WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
                List<ImBasicData1V2> imBasicDataList = imBasicData(companyCodeId, plantId, languageId, warehouseId, loginUserID, allRowsList);
                log.info("imbasic Data bin size " + imBasicDataList.size());


                AuthToken authToken = authTokenService.getMastersServiceAuthToken();
                dbWarehouseApiResponse = mastersService.postImBasicDataUpload(allRowsList, authToken.getAccess_token());

                if (dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return null;
    }


    private List<String> validationImBasicData (MultipartFile file) throws IOException {

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
                            case "uomid":
                            case "itemcode":
                            case "description":
                            case "manufacturerpartno":
                            case "hsncode":
                            case "storagesectionid":
                            case "capacitycheck":
                            case "capacityunit":
                            case "capacityuom":
                            case "quantity":
                            case "manufacturername":
                            case "manufacturerfullname":
                            case "manufacturercode":
                            case "dimensionuom":
                            case "supplierpartnumber":
                            case "model":
                            case "specifications1":
                            case "specifications2":
                            case "eanupcno":
                            case "shelflifeindicator":
                            case "brand":
                            case "remarks":
                                validateStringCell(cell, rowIndex, colIndex, header, errors);
                                break;

                            case "itemtype":
                            case "itemgroup":
                            case "subitemgroup":
                            case "minimumstock":
                            case "maximumstock":
                            case "reorderlevel":
                            case "replenishmentqty":
                            case "safetystock":
                            case "weight":
                            case "statusid":
                            case "length":
                            case "width":
                            case "height":
                            case "volume":
                                validateIntegerCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            default:
                                errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": " + header);
                                break;
                        }
                    } else {
                        switch (header) {
                            case "uomid":
                            case "itemcode":
                            case "description":
                                errors.add("Empty cell at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + ") : : Mandatory Field cannot be empty.");
                                break;
                        }
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

    private List<com.tekclover.wms.core.model.dto.Error> validationFormat (List < String > validationErrors) {
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

    private List<ImBasicData1V2> imBasicData (String companyCodeId, String plantId,
                                              String languageId, String warehouseId, String loginUserID, List < ImBasicData1V2 > list){
        List<ImBasicData1V2> allRowsList = list.stream().sorted(Comparator.comparing(ImBasicData1V2::getUomId)).collect(Collectors.toList());
        List<ImBasicData1V2> saveImBasicData = new ArrayList<>();

        for (ImBasicData1V2 imBasicDataV2 : allRowsList) {
            ImBasicData1V2 imBasicdata1 = new ImBasicData1V2();
            BeanUtils.copyProperties(imBasicDataV2, imBasicdata1, CommonUtils.getNullPropertyNames(imBasicDataV2));
            imBasicdata1.setCompanyCodeId(companyCodeId);
            imBasicdata1.setPlantId(plantId);
            imBasicdata1.setWarehouseId(warehouseId);
            imBasicdata1.setLanguageId(languageId);
            imBasicdata1.setCreatedBy(loginUserID);
            log.info("itemCode" + imBasicDataV2.getItemCode());
            log.info("itemCode" + imBasicdata1.getItemCode());

            saveImBasicData.add(imBasicdata1);
        }

        return saveImBasicData;
    }
    //--------------------------------------------------------InventoryupdateUpload----------------------------//
    /**
     * @param file
     * @return
     * @throws Exception
     */
    public Map<String, String> inventoryUpdateUpload(String companyCodeId, String plantId, String languageId,
                                                     String warehouseId, String loginUserId,MultipartFile file) throws Exception {
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

            List<List<String>> allRowsList = readExcelDataUpdateUpload(targetLocation.toFile());
            List<Inventory> inventoryUpdate = uploadUpdate(allRowsList);
            log.info("InventoryUpdate : " + inventoryUpdate);

            // Uploading Orders
            WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
            AuthToken authToken = authTokenService.getInboundTransactionServiceAuthToken();
//            dbWarehouseApiResponse = transactionService.inventoryUpdateUpload(inventoryUpdate, "Uploaded", authToken.getAccess_token());
            dbWarehouseApiResponse = transactionService.inventoryUpdateUpload(inventoryUpdate,companyCodeId,plantId,languageId,warehouseId,loginUserId,authToken.getAccess_token());

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
    private List<Inventory> uploadUpdate(List<List<String>> allRowsList) {
        List<Inventory> inventoryList = new ArrayList<>();

        for (List<String> listUploadedData : allRowsList) {
//            Set<Inventory> setInventory = new HashSet<>();

            Inventory inventory = null;
            boolean oneTimeAllow = true;
            for (String column : listUploadedData) {
                if (oneTimeAllow) {
                    inventory = new Inventory();
                    inventory.setPackBarcodes(listUploadedData.get(0));
                    inventory.setItemCode(listUploadedData.get(1));
                    inventory.setStorageBin(listUploadedData.get(2));
                    String stockTypeStr = listUploadedData.get(3);
                    String specialStockStr = listUploadedData.get(4);
                    if (stockTypeStr != null && !stockTypeStr.trim().isEmpty()) {
                        inventory.setStockTypeId(Long.valueOf(stockTypeStr.trim()));
                    } else {
                        inventory.setStockTypeId(null); // or default value if needed
                    }

                    if (specialStockStr != null && !specialStockStr.trim().isEmpty()) {
                        inventory.setSpecialStockIndicatorId(Long.valueOf(specialStockStr.trim()));
                    } else {
                        inventory.setSpecialStockIndicatorId(null); // or default value if needed
                    }
                    inventoryList.add(inventory);
                }
                oneTimeAllow = false;
            }
        }
        return inventoryList;
    }

    private List<List<String>> readExcelDataUpdateUpload(File file) {
        try {
            Workbook workbook = new XSSFWorkbook(file);
            org.apache.poi.ss.usermodel.Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            List<List<String>> allRowsList = new ArrayList<>();
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                // Moving to data row instead of header row
                currentRow = iterator.next();
                cellIterator = currentRow.iterator();

                List<String> listUploadData = new ArrayList<String>();
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    if (currentCell.getCellType() == CellType.STRING) {
                        log.info(currentCell.getStringCellValue() + "||");
                        if (currentCell.getStringCellValue() != null
                                && !currentCell.getStringCellValue().trim().isEmpty()) {
                            listUploadData.add(currentCell.getStringCellValue());
                            log.info("== " + listUploadData.size());
                        } else {
                            listUploadData.add(" ");
                            log.info("=#= " + listUploadData.size());
                        }
                    } else if (currentCell.getCellType() == CellType.NUMERIC) {
                        log.info(currentCell.getNumericCellValue() + "--");
//                        listUploadData.add(String.valueOf(currentCell.getNumericCellValue()));
                        double numValue = currentCell.getNumericCellValue();
                        if (numValue == Math.floor(numValue)) {
                            listUploadData.add(String.valueOf((long) numValue));
                        } else {
                            listUploadData.add(String.valueOf(numValue));
                        }

                    }
                }
                allRowsList.add(listUploadData);
            }
            return allRowsList;
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        return null;
    }


    //==============================================Inventory Upload=============================================

    public Map<String, String> processInventory(String companyCodeId, String plantId, String languageID,
                                                 String warehouseId,String loginUserId, MultipartFile file) throws Exception {
        this.fileStorageLocation = Paths.get(propertiesConfig.getFileUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new BadRequestException(
                        "Could not create the directory where the uploaded files will be stored.");
            }
        }

        List<String> validationErrors = validationInventory(file);
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

            List<InventoryV2> allRowsList = excelDataProcessService.inventoryReadExcelFile(companyCodeId, plantId, languageID, warehouseId, loginUserId, file);

            if (allRowsList != null && !allRowsList.isEmpty()) {
                // Uploading
                WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
                List<InventoryV2> inventory = inventory(companyCodeId, plantId, languageID, warehouseId, loginUserId, allRowsList);
                log.info("inventory size " + inventory.size());
                AuthToken authToken = authTokenService.getInboundTransactionServiceAuthToken();
                dbWarehouseApiResponse = transactionService.postInventory(inventory, authToken.getAccess_token());

                if (dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return null;
    }

    // validation for Inventory
    private List<String> validationInventory(MultipartFile file) throws IOException {
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
                            case "palletcode" :
                            case "casecode" :
                            case "itemcode" :
                            case"packbarcodes" :
                            case "variantsubcode" :
                            case "batchserialnumber" :
                            case "storagebin" :
                            case "storagemethod" :
                            case "description" :
                            case "inventoryuom" :
                            case "manufacturercode" :
                            case "barcodeid" :
                            case "cbm" :
                            case "cbmunit" :
                            case "cbmperquantity" :
                            case "manufacturername" :
                            case "origin" :
                            case "brand" :
                            case "levelid" :
                            case "stocktypedescription" :
                            case "referencefield1" :
                            case "referencefield2" :
                            case "referencefield3" :
                            case "referencefield5" :
                            case "referencefield6" :
                            case "referencefield7" :
                            case "referencefield8" :
                            case "referencefield9" :
                            case "referencefield10" :

                                validateStringCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            case "variantcode" :
                            case"stocktypeid" :
                            case"specialstockindicatorid" :
                            case "binclassid" :
                            case "allocatedquantity" :
                            case "inventoryquantity" :
                            case "deletionindicator" :
                            case "referencefield4" :

                                validateIntegerCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            default:
                                errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": " + header);
                                break;
                        }
                    } else
                        switch (header) {
                            case "palletcode" :
                            case "casecode" :
                            case "itemcode" :
                            case"packbarcodes" :
                            case "storagebin" :
                            case"stocktypeid" :
                            case"specialstockindicatorid" :
                            case "binclassid" :
                            case "description" :
                            case "allocatedquantity" :
                            case "inventoryquantity" :
                            case "inventoryuom" :
                            case "manufacturercode" :
//                            case "barcodeid" :
                            case "manufacturername" :
                            case "levelid" :
                            case "stocktypedescription" :
                            case "deletionindicator" :
                            case "referencefield4" :
                            case "referencefield8" :
                            case "referencefield9" :
                            case "referencefield10" :
                                errors.add("Empty cell at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + ") : : Mandatory Field cannot be empty.");
                                break;
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

    private List<InventoryV2> inventory(String companyCodeId, String plantId,
                                          String languageId, String warehouseId, String loginUserID, List<InventoryV2> list) {
//        List<InventoryV2> allRowsList = list.stream().sorted(Comparator.comparing(InventoryV2::getBarcodeId)).collect(Collectors.toList());
        List<InventoryV2> saveInventory = new ArrayList<>();

        for (InventoryV2 inventoryV2 : list) {
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(inventoryV2,inventory,CommonUtils.getNullPropertyNames(inventoryV2));
            inventory.setCompanyCodeId(companyCodeId);
            inventory.setPlantId(plantId);
            inventory.setWarehouseId(warehouseId);
            inventory.setLanguageId(languageId);
            inventory.setCreatedBy(loginUserID);
            saveInventory.add(inventory);
        }

        return saveInventory;
    }

    //=================================================ImPartner==============================================

    public Map<String, String> processImPartner(String companyCodeId, String plantId, String languageID,
                                                String warehouseId,String loginUserId, MultipartFile file) throws Exception {
        this.fileStorageLocation = Paths.get(propertiesConfig.getFileUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new BadRequestException(
                        "Could not create the directory where the uploaded files will be stored.");
            }
        }

        List<String> validationErrors = validationImPartner(file);
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

            List<ImPartner> allRowsList = excelDataProcessService.imPartnerReadExcelFile(companyCodeId, plantId, languageID, warehouseId, loginUserId, file);

            if (allRowsList != null && !allRowsList.isEmpty()) {
                // Uploading
                WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
                List<ImPartner> impartner = imPartner(companyCodeId, plantId, languageID, warehouseId, loginUserId, allRowsList);
                log.info("impartner size " + impartner.size());
                AuthToken authToken = authTokenService.getMastersServiceAuthToken();
                dbWarehouseApiResponse = mastersService.postImPartner(impartner, authToken.getAccess_token());

                if (dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
        }
        return null;
    }

    // validation for Inventory
    private List<String> validationImPartner(MultipartFile file) throws IOException {
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
                            case "businesspartnercode" :
                            case "itemcode" :
                            case "businesspartnertype" :
                            case "partneritembarcode" :
                            case "manufacturercode" :
                            case "manufacturername" :
                            case "brandname" :
                            case "partnername" :
                            case "partneritemno" :
                            case "vendoritembarcode" :
                            case "mfrbarcode" :
                            case "stockuom" :

                                validateStringCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            case "stock" :
                            case "statusid" :
                                validateIntegerCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            default:
                                errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": " + header);
                                break;
                        }
                    } else
                        switch (header) {
                            case "businesspartnercode" :
                            case "itemcode" :
                            case "businesspartnertype" :
                            case "partneritembarcode" :
                            case "manufacturercode" :
                            case "manufacturername" :
                            case "partnername" :
                            case "partneritemno" :
                            case "vendoritembarcode" :
                            case "mfrbarcode" :
                                errors.add("Empty cell at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + ") : : Mandatory Field cannot be empty.");
                                break;
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

    private List<ImPartner> imPartner(String companyCodeId, String plantId,
                                      String languageId, String warehouseId, String loginUserID, List<ImPartner> list) {
        List<ImPartner> allRowsList = list.stream().sorted(Comparator.comparing(ImPartner::getBusinessPartnerCode)).collect(Collectors.toList());
        List<ImPartner> saveImPartner = new ArrayList<>();
        for (ImPartner imPartners: allRowsList) {
            ImPartner imPartner = new ImPartner();
            BeanUtils.copyProperties(imPartners,imPartner,CommonUtils.getNullPropertyNames(imPartners));
            imPartner.setCompanyCodeId(companyCodeId);
            imPartner.setPlantId(plantId);
            imPartner.setWarehouseId(warehouseId);
            imPartner.setLanguageId(languageId);
            imPartner.setCreatedBy(loginUserID);
            saveImPartner.add(imPartner);
        }
        return saveImPartner;
    }


}
