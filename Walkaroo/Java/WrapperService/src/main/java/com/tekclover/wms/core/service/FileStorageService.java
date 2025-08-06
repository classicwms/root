package com.tekclover.wms.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.exception.BadRequestException;
import com.tekclover.wms.core.model.dto.Error;
import com.tekclover.wms.core.model.transaction.*;

import com.tekclover.wms.core.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.core.model.warehouse.inbound.almailem.*;
import com.tekclover.wms.core.model.warehouse.inbound.walkaroo.ReversalV3;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.*;
import com.tekclover.wms.core.model.warehouse.outbound.walkaroo.DeliveryConfirmationSAP;
import com.tekclover.wms.core.model.warehouse.outbound.walkaroo.DeliveryConfirmationV3;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
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
	ExcelDataProcessService excelDataProcessService;

	@Autowired
	OrderPreparationService orderPreparationService;

	//-----------------------------------------------------------------------------------
	@Autowired
	TransactionService transactionService;
	//-----------------------------------------------------------------------------------

	private Path fileStorageLocation = null;
	private static final String UOM = "EACH";

	private String getTransactionAuthToken() {
		return authTokenService.getTransactionServiceAuthToken().getAccess_token();
	}

	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
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
		return Collections.singletonMap("message", "File uploaded successfully!");
	}

	/**
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> processSOOrders(MultipartFile file) throws Exception {
			try {
            Map<String, List<List<String>>> result = storeTheFile(file);
            String fileName = result.keySet().iterator().next();            // Get the first key
            List<List<String>> allRowsList = result.get(fileName);            // Get the value for that key
			if(allRowsList != null && !allRowsList.isEmpty()) {
                List<ShipmentOrderV2> shipmentOrders = orderPreparationService.prepSOData(allRowsList);
			log.info("shipmentOrders : " + shipmentOrders);
				WarehouseApiResponse dbWarehouseApiResponse = new WarehouseApiResponse();
				String authToken = getTransactionAuthToken();
				dbWarehouseApiResponse = transactionService.postShipmentOrderV2(shipmentOrders, authToken);

				if (dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
				}
			}
        } catch (Exception ex) {
			ex.printStackTrace();
            throw new BadRequestException("Could not store file " + file.getOriginalFilename() + ". Please try again!");
		}
		return null;
	}

	/**
	 * @param file
	 * @return
	 */
	public Map<String, String> processSalesOrders(MultipartFile file) {
			try {
            Map<String, List<List<String>>> result = storeTheFile(file);
            String fileName = result.keySet().iterator().next(); 			// Get the first key
            List<List<String>> allRowsList = result.get(fileName); 			// Get the value for that key
			if(allRowsList != null && !allRowsList.isEmpty()) {
                List<SalesOrderV2> salesOrders = orderPreparationService.prepSalesOrderData(allRowsList);
				log.info("salesOrders : " + salesOrders);
				WarehouseApiResponse dbWarehouseApiResponse = new WarehouseApiResponse();
				String authToken = getTransactionAuthToken();
				dbWarehouseApiResponse = transactionService.postSalesOrderV2(salesOrders, authToken);

				if (dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
				}
			}
        } catch (Exception ex) {
			ex.printStackTrace();
            throw new BadRequestException("Could not store file " + file.getOriginalFilename() + ". Please try again!");
		}
		return null;
	}

	/**
	 * @param companyCodeId
	 * @param plantId
	 * @param languageId
	 * @param warehouseId
	 * @param file
	 * @return
	 */
	public Map<String, String> processSalesOrders(String companyCodeId, String plantId, String languageId,
                                                  String warehouseId, String loginUserId, MultipartFile file) throws Exception {
			try {
            Map<String, List<List<String>>> result = storeTheFile(file);
            String fileName = result.keySet().iterator().next(); 			// Get the first key
            List<List<String>> allRowsList = result.get(fileName); 			// Get the value for that key
			if(allRowsList != null && !allRowsList.isEmpty()) {
                List<SalesOrderV2> salesOrders = orderPreparationService.prepSalesOrderData(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
				log.info("salesOrders : " + salesOrders);
				WarehouseApiResponse dbWarehouseApiResponse = new WarehouseApiResponse();
				String authToken = getTransactionAuthToken();
				dbWarehouseApiResponse = transactionService.postSalesOrderV2(salesOrders, authToken);

				if (dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
				}
			}
        } catch (Exception ex) {
			ex.printStackTrace();
            throw new BadRequestException("Could not store file " + file.getOriginalFilename() + ". Please try again!");
		}
		return null;
	}

	/**
	 * @param companyCodeId
	 * @param plantId
	 * @param languageId
	 * @param warehouseId
	 * @param loginUserId
	 * @param file
	 * @return
	 */
	public Map<String, String> processSalesOrdersV3(String companyCodeId, String plantId, String languageId,
                                                    String warehouseId, String loginUserId, MultipartFile file) throws Exception {
			try {
            Map<String, List<List<String>>> result = storeTheFile(file);
            String fileName = result.keySet().iterator().next(); 			// Get the first key
            List<List<String>> allRowsList = result.get(fileName); 			// Get the value for that key
			if(allRowsList != null && !allRowsList.isEmpty()) {
                List<SalesOrderV2> salesOrders = orderPreparationService.prepSalesOrderDataV3(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
				log.info("salesOrders : " + salesOrders);
				WarehouseApiResponse dbWarehouseApiResponse = new WarehouseApiResponse();
				String authToken = getTransactionAuthToken();
				dbWarehouseApiResponse = transactionService.postSalesOrderV2(salesOrders, authToken);

				if (dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
				}
			}
        } catch (Exception ex) {
			ex.printStackTrace();
            throw new BadRequestException("Could not store file " + file.getOriginalFilename() + ". Please try again!");
		}
		return null;
	}
	
	/**
	 * -----------------------------Walkaroo changes--------------------------------------------------------------------
     *
	 * @param file
	 * @return
	 */
	public Map<String, String> processDeliveryConfirmationV3 (String companyCodeId, String plantId, String languageId,
															  String warehouseId, String loginUserId, MultipartFile file) {
			try {
            Map<String, List<List<String>>> result = storeTheFile(file);
            String fileName = result.keySet().iterator().next();            // Get the first key
            List<List<String>> allRowsList = result.get(fileName);            // Get the value for that key
			if (allRowsList != null && !allRowsList.isEmpty()) {
                DeliveryConfirmationV3 deliveryLines = orderPreparationService.prepDeliveryConfirmationV3(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
				log.info("deliveryLines : " + deliveryLines);
				WarehouseApiResponse dbWarehouseApiResponse = new WarehouseApiResponse();
				String authToken = getTransactionAuthToken();
				dbWarehouseApiResponse = transactionService.postDeliveryConfirmationV3 (deliveryLines, authToken);

				if (dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
				}
			}
        } catch (Exception ex) {
			ex.printStackTrace();
            throw new BadRequestException("Could not store file " + file.getOriginalFilename() + ". Please try again!");
		}
		return null;
	}

	/**
	 * 
	 * @param deliveryConfirmationSAPList
	 * @return
	 */
	public WarehouseApiResponse processDeliveryConfirmationV3(List<DeliveryConfirmationSAP> deliveryConfirmationSAPList) {
		try {
			DeliveryConfirmationV3 deliveryLines = orderPreparationService.prepDeliveryConfirmationV3(deliveryConfirmationSAPList);
			log.info("deliveryLines : " + deliveryLines);
			WarehouseApiResponse dbWarehouseApiResponse = new WarehouseApiResponse();
			String authToken = getTransactionAuthToken();
			dbWarehouseApiResponse = transactionService.postDeliveryConfirmationV3(deliveryLines, authToken);
			return dbWarehouseApiResponse;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @param companyCodeId
	 * @param plantId
	 * @param languageId
	 * @param warehouseId
	 * @param loginUserId
	 * @param file
	 * @return
	 */
	public Map<String, String> postInboundReversalUploadV3(String companyCodeId, String plantId, String languageId,
														   String warehouseId, String loginUserId, MultipartFile file) {
			try {
            Map<String, List<List<String>>> result = storeTheFile(file);
            String fileName = result.keySet().iterator().next();            // Get the first key
            List<List<String>> allRowsList = result.get(fileName);            // Get the value for that key
            if (allRowsList != null && !allRowsList.isEmpty()) {
                ReversalV3 reversal = orderPreparationService.prepareReversalV3(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
                log.info("InboundReversal : " + reversal);
                WarehouseApiResponse dbWarehouseApiResponse = new WarehouseApiResponse();
                String authToken = getTransactionAuthToken();
                dbWarehouseApiResponse = transactionService.postInboundReversalUploadV3(reversal, authToken);

                if (dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
                }
            }
			} catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException("Could not store file " + file.getOriginalFilename() + ". Please try again!");
			}
        return null;
		}

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param file
     * @return
     */
    public Map<String, String> postOutboundReversalUploadV3(String companyCodeId, String plantId, String languageId,
                                                            String warehouseId, String loginUserId, MultipartFile file) {
		try {
            Map<String, List<List<String>>> result = storeTheFile(file);
            String fileName = result.keySet().iterator().next();                // Get the first key
            List<List<String>> allRowsList = result.get(fileName);              // Get the value for that key
			if (allRowsList != null && !allRowsList.isEmpty()) {
                ReversalV3 reversal = orderPreparationService.prepareReversalV3(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
				log.info("InboundReversal : " + reversal);
				WarehouseApiResponse dbWarehouseApiResponse = new WarehouseApiResponse();
				String authToken = getTransactionAuthToken();
                dbWarehouseApiResponse = transactionService.postOutboundReversalUploadV3(reversal, authToken);

				if (dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
				}
			}
        } catch (Exception ex) {
			ex.printStackTrace();
            throw new BadRequestException("Could not store file " + file.getOriginalFilename() + ". Please try again!");
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
		String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
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

			if(locationPath != null) {
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
			}
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
	private List<List<String>> readExcelData(File file) {
		try {
			Workbook workbook = new XSSFWorkbook(file);
			workbook.setMissingCellPolicy(Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
			org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

			List<List<String>> allRowsList = new ArrayList<>();
			DataFormatter fmt = new DataFormatter();
			for (int rn=sheet.getFirstRowNum(); rn<=sheet.getLastRowNum(); rn++) {
				List<String> listUploadData = new ArrayList<>();
				Row row = sheet.getRow(rn);
				log.info("Row:  "+ row.getRowNum());
				if (row == null) {
					// There is no data in this row, handle as needed
				} else if (row.getRowNum() != 0) {
					for (int cn = 0; cn <= row.getLastCellNum(); cn ++) {
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
			try {
            Map<String, List<List<String>>> result = storeTheFile(file);
            String fileName = result.keySet().iterator().next();            // Get the first key
            List<List<String>> allRowsList = result.get(fileName);            // Get the value for that key
			if(allRowsList != null && !allRowsList.isEmpty()) {
                List<ASNV2> asnV2Orders = orderPreparationService.prepAsnData(allRowsList);
			log.info("asnOrders : " + asnV2Orders);

			// Uploading Orders
			WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
				String authToken = getTransactionAuthToken();
				dbWarehouseApiResponse = transactionService.postASNV2Upload(asnV2Orders, "Uploaded", authToken);

			if(dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
			}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
            throw new BadRequestException("Could not store file " + file.getOriginalFilename() + ". Please try again!");
		}
		return null;
	}

	/**
	 * @param companyCodeId
	 * @param plantId
	 * @param languageId
	 * @param warehouseId
	 * @param file
	 * @return
	 */
	public Map<String, String> processAsnOrders(String companyCodeId, String plantId, String languageId,
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
                List<ASNV2> asnV2Orders = orderPreparationService.prepAsnData(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
				log.info("asnOrders : " + asnV2Orders);

				// Uploading Orders
				WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
				String authToken = getTransactionAuthToken();
				dbWarehouseApiResponse = transactionService.postASNV2Upload(asnV2Orders, "Uploaded", authToken);

			if(dbWarehouseApiResponse != null) {
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
	 * Walkaroo
     *
	 * @param companyCodeId
	 * @param plantId
	 * @param languageId
	 * @param warehouseId
	 * @param file
	 * @return
	 */
	public Map<String, String> processAsnOrdersV3(String companyCodeId, String plantId, String languageId,
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
                List<ASNV2> asnV2Orders = orderPreparationService.prepAsnMultipleDataV3(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
				log.info("asnOrders : " + asnV2Orders);

				// Uploading Orders
				WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
				String authToken = getTransactionAuthToken();
				dbWarehouseApiResponse = transactionService.postASNV2Upload(asnV2Orders, "Uploaded", authToken);

			if(dbWarehouseApiResponse != null) {
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
                List<InterWarehouseTransferInV2> wh2whOrders = orderPreparationService.prepInterwareHouseInData(allRowsList);
			log.info("wh2whOrders : " + wh2whOrders);

			// Uploading Orders
			WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
				String authToken = getTransactionAuthToken();
				dbWarehouseApiResponse = transactionService.postInterWarehouseTransferInUploadV2(wh2whOrders, "Uploaded", authToken);

			if(dbWarehouseApiResponse != null) {
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
                List<InhouseTransferUpload> inhouseTransferUploads = orderPreparationService.prepInHouseTransferHeaderV2(allRowsList);
			log.info("inhouseTransferUploads bin-to-bin : " + inhouseTransferUploads);

			// Uploading Orders
			String authToken = getTransactionAuthToken();
			WarehouseApiResponse dbWarehouseApiResponse = transactionService.createInhouseTransferUploadV2(inhouseTransferUploads, "UP_AMS", authToken);

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
                List<StockAdjustment> stockAdjustmentList = orderPreparationService.prepStockAdjustment(allRowsList);
			log.info("StockAdjustment List: " + stockAdjustmentList);

			// Uploading Orders
				String authToken = getTransactionAuthToken();
				WarehouseApiResponse dbWarehouseApiResponse = transactionService.createStockAdjustmentUploadV2(stockAdjustmentList, authToken);

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

	//==================================================Walkaroo - V4=====INBOUND===============================================================

//	public static void validateStringCell(Cell cell, int rowIndex, int colIndex,String header, List<String> errors) {
//		boolean conditionPass = cell.getCellType() == CellType.STRING || cell.getCellType() == CellType.NUMERIC;
//		if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty()) {
//			errors.add("Empty value at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": String cannot be empty.");
//		} else if (!conditionPass) {
//			errors.add("Invalid data type at row " + (rowIndex + 1) + ", column " + (colIndex + 1) +" (" + header + ") : Expected String.");
//		}
//	}

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


	// Validate date cell (ensure it's a date and not empty)
	public static void validateDateCell(Cell cell, int rowIndex, int colIndex, String header, List<String> errors) {
		if (cell.getCellType() != CellType.NUMERIC || !DateUtil.isCellDateFormatted(cell)) {
			errors.add("Invalid data type at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": Expected Date.");
		} else if (cell.getDateCellValue() == null) {
			errors.add("Empty value at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": Date cannot be empty.");
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
                            case "huserialnumber":
                            case "material":
                            case "pricesegment":
                            case "plant":
                            case "storagelocation":
                            case "skucode":
                            case "articlenumber":
                            case "gender":
                            case "color":
                            case "size":
                            case "customercode":
                            case "customer":
                            case "shiptocode":
                            case "shiptoparty":
                            case "outbound":
                            case "inbound":
                                validateStringCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            case "specialstock":
                            case "mtonumber":
                                nullValidateStringCell(cell, rowIndex, colIndex, header, errors);
                                break;
                            case "noofpairs":
                            case "qty":
                            case "itm":
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
                            case "huserialnumber":
                            case "skucode":
                            case "inbound":
                            case "qty":
                            case "itm":
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
							case "huserialnumber":
							case "material":
							case "pricesegment":
							case "plant":
							case "storagelocation":
							case "skucode":
							case "articlenumber":
							case "gender":
							case "color":
							case "size":
							case "customercode":
							case "customer":
							case "shiptocode":
							case "shiptoparty":
							case "outbound":
							case "inbound":
								validateStringCell(cell, rowIndex, colIndex, header ,errors);
								break;
                            case "specialstock":
                            case "mtonumber":
                                nullValidateStringCell(cell, rowIndex, colIndex, header, errors);
                                break;
							case "noofpairs":
							case "qty":
							case "itm":
								validateIntegerCell(cell, rowIndex, colIndex,header, errors);
								break;
//							case "expecteddate":
//							case "requireddeliverydate":
//							case "date":
//								validateDateCell(cell,rowIndex,colIndex,header,errors);
//								break;
							default:
								errors.add("Unknown header at row " + (rowIndex + 1) + ", column " + (colIndex + 1)+ ": " + header);
								break;
						}
                    } else {
                        switch (header) {
                            case "skucode":
                            case "outbound":
                            case "inbound":
                            case "qty":
                            case "itm":
                            case "shiptoparty":
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

	public static String extractHeaderFromMessage(String errorMessage) {
		// Extract the portion inside parentheses, assuming the format is consistent
		int startIndex = errorMessage.indexOf("(") + 1;
		int endIndex = errorMessage.indexOf(")");

		if (startIndex > 0 && endIndex > startIndex) {
			return errorMessage.substring(startIndex, endIndex);
		}

		return null; // Return null if no header is found
	}


	/**
	 *  Upload V4 Dynamic
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
    public Map<String, String> processInboundOrdersV3(String companyCodeId, String plantId, String languageId,
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
		if (!validationErrors.isEmpty()){
			List<Error> errors = validationFormatInbound(validationErrors);
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errors);
			Map<String, String> mapFileProps = new HashMap<>();
			mapFileProps.put("errors",jsonResponse);
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
				if (orderTypeId == 2L) {
					List<SaleOrderReturnV2> saleOrderReturn = orderPreparationService.prepSoReturnMultipleData(allRowsList);
					log.info("salesOrderReturn : " + saleOrderReturn);
					dbWarehouseApiResponse = transactionService.postSOReturnUploadExcel(saleOrderReturn, authToken);
				}

				if(dbWarehouseApiResponse != null) {
                    return uploadSuccessMessage(fileName);
				}
			}

	} catch (IOException ex) {
		ex.printStackTrace();
		throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
	}
		return null;
	}

	private List<Error> validationFormatInbound(List<String> validationErrors) {
		Map<String, Object> response = null;
		List<Error> errorList = new ArrayList<>();
		for (String error : validationErrors) {
			String[] parts = error.split(":");
			String rowPart = parts[0];
			String message = parts[1].trim().concat(rowPart);

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

	//=======================================================OUTBOUND===============================================================

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
    public Map<String, String> processOutboundOrdersV3(String companyCodeId, String plantId, String languageId,
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
     * @param file
     * @return
     * @throws Exception
     */
    private Map<String, List<List<String>>> storeTheFile(MultipartFile file) throws Exception {
        try {
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
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())).replace(" ", "_");
            log.info("filename : " + fileName);
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new BadRequestException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied : " + targetLocation);
            List<List<String>> allRowsList = readExcelData(targetLocation.toFile());
            return Map.of(fileName, allRowsList);
        } catch (Exception e) {
            log.error("Exception while storing file..!");
            throw e;
        }
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
}