package com.tekclover.wms.core.service;

import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.exception.BadRequestException;
import com.tekclover.wms.core.model.transaction.InhouseTransferUpload;
import com.tekclover.wms.core.model.transaction.StockAdjustment;
import com.tekclover.wms.core.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.core.model.warehouse.inbound.almailem.ASNV2;
import com.tekclover.wms.core.model.warehouse.inbound.almailem.InboundOrderProcessV4;
import com.tekclover.wms.core.model.warehouse.inbound.almailem.InterWarehouseTransferInV2;
import com.tekclover.wms.core.model.warehouse.inbound.almailem.SaleOrderReturnV2;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.*;
import com.tekclover.wms.core.model.warehouse.outbound.walkaroo.DeliveryConfirmationV3;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.ValidationException;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class FileStorageService {

	@Autowired
	PropertiesConfig propertiesConfig;

	@Autowired
	AuthTokenService authTokenService;

	//-----------------------------------------------------------------------------------
	@Autowired
	TransactionService transactionService;

	@Autowired
	ExcelDataProcessService excelDataProcessService;

	@Autowired
	OrderPreparationService orderPreparationService;
	//-----------------------------------------------------------------------------------

	private Path fileStorageLocation = null;

	private String getTransactionAuthToken() {
		return authTokenService.getTransactionServiceAuthToken().getAccess_token();
	}

	/**
	 *
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
	 *
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
				List<ShipmentOrderV2> shipmentOrders = orderPreparationService.prepSOData(allRowsList);
				log.info("shipmentOrders : " + shipmentOrders);
				WarehouseApiResponse dbWarehouseApiResponse = new WarehouseApiResponse();
				String authToken = getTransactionAuthToken();
				dbWarehouseApiResponse = transactionService.postShipmentOrderV2(shipmentOrders, authToken);

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
	 */
	public Map<String, String> processSalesOrders(MultipartFile file) {
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
				List<SalesOrderV2> salesOrders = orderPreparationService.prepSalesOrderData(allRowsList);
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
			if(allRowsList != null && !allRowsList.isEmpty()) {
//				List<SalesOrderV2> salesOrders = orderPreparationService.prepSalesOrderData(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
				List<SalesOrderV2> salesOrders = orderPreparationService.prepSalesOrderMultipleData(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
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
	 * @param loginUserId
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
	 * @param location
	 * @param file
	 * @return
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
	 *
	 * @param file
	 * @return
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
	 *
	 * @param file
	 * @return
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
				List<ASNV2> asnV2Orders = orderPreparationService.prepAsnData(allRowsList);
				log.info("asnOrders : " + asnV2Orders);

				// Uploading Orders
				WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
				String authToken = getTransactionAuthToken();
				dbWarehouseApiResponse = transactionService.postASNV2Upload(asnV2Orders, "Uploaded", authToken);

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
//				List<ASNV2> asnV2Orders = orderPreparationService.prepAsnData(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
				List<ASNV2> asnV2Orders = orderPreparationService.prepAsnMultipleData(companyCodeId, plantId, languageId, warehouseId, loginUserId, allRowsList);
				log.info("asnOrders : " + asnV2Orders);

				// Uploading Orders
				WarehouseApiResponse[] dbWarehouseApiResponse = new WarehouseApiResponse[0];
				String authToken = getTransactionAuthToken();
				dbWarehouseApiResponse = transactionService.postASNV2Upload(asnV2Orders, loginUserId, authToken);

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
	 * Walkaroo
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
	 * @param directoryPath
	 * @return
	 * @throws Exception
	 */
	public File createZipOfDirectory(String directoryPath) throws Exception {
		File directory = new File(directoryPath);
		if (!directory.exists() || !directory.isDirectory()) {
			throw new BadRequestException("Directory does not exist: " + directoryPath);
		}

		// Create temporary zip
		File zipFile = File.createTempFile("files_", ".zip");
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
			zipDirectory(directory, directory.getName(), zos);
		}
		return zipFile;
	}
	/**
	 * @param folder
	 * @param parentFolder
	 * @param zos
	 * @throws Exception
	 */
	private void zipDirectory(File folder, String parentFolder, ZipOutputStream zos) throws Exception {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				zipDirectory(file, parentFolder + "/" + file.getName(), zos);
				continue;
			}

			try (FileInputStream fis = new FileInputStream(file)) {
				ZipEntry zipEntry = new ZipEntry(parentFolder + "/" + file.getName());
				zos.putNextEntry(zipEntry);

				byte[] bytes = new byte[1024];
				int length;
				while ((length = fis.read(bytes)) >= 0) {
					zos.write(bytes, 0, length);
				}
				zos.closeEntry();
			}
		}
	}

	/**
	 * @param location
	 * @return
	 * @throws Exception
	 */
	public String getQualifiedFilePath(String location) throws Exception {
		String filePath = propertiesConfig.getDocStorageBasePath();
		if (!location.startsWith("/")) {
			location = "/" + location;
		}
		filePath = filePath + location;
		return filePath;
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
	 * -----------------------------Walkaroo changes--------------------------------------------------------------------
	 * @param file
	 * @return
	 */

	public Map<String, String> processDeliveryConfirmationV3 (MultipartFile file) {
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
				DeliveryConfirmationV3 deliveryLines = orderPreparationService.prepDeliveryConfirmationV3(allRowsList);
				log.info("deliveryLines : " + deliveryLines);
				WarehouseApiResponse dbWarehouseApiResponse = new WarehouseApiResponse();
				String authToken = getTransactionAuthToken();
				dbWarehouseApiResponse = transactionService.postDeliveryConfirmationV3 (deliveryLines, authToken);

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

	//==================================================IMPEX - V4=====INBOUND===============================================================
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

	public static void validateStringCell(Cell cell, int rowIndex, int colIndex,String header, List<String> errors) {
		boolean conditionPass = cell.getCellType() == CellType.STRING || cell.getCellType() == CellType.NUMERIC;
		if (!conditionPass) {
			errors.add("Invalid data type at row " + (rowIndex + 1) + ", column " + (colIndex + 1) +" (" + header + ") : Expected String.");
		} else if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty()) {
			errors.add("Empty value at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + ": String cannot be empty.");
		}
	}

	public static void validateIntegerCell(Cell cell, int rowIndex, int colIndex,String header, List<String> errors) {
		if (cell.getCellType() != CellType.NUMERIC) {
			errors.add("Invalid data type at row " + (rowIndex + 1) + ", column " + (colIndex + 1)  + " (" + header + "): Expected Integer.");
		} else {
			double value = cell.getNumericCellValue();
			if (value != (int) value) {
				errors.add("Invalid value at row " + (rowIndex + 1) + ", column " + (colIndex + 1) + " (" + header + "): Expected Integer.");
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
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public String storeFile(MultipartFile file, String filePath) throws Exception {

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
		return fileName;
	}
}