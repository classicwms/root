package com.tekclover.wms.core.service;

import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.exception.BadRequestException;
import com.tekclover.wms.core.exception.CustomErrorResponse;
import com.tekclover.wms.core.exception.RestResponseEntityExceptionHandler;
import com.tekclover.wms.core.model.auth.AuthToken;
import com.tekclover.wms.core.model.idmaster.FileNameForEmail;
import com.tekclover.wms.core.model.idmaster.MailingReport;
import com.tekclover.wms.core.model.transaction.SOHeader;
import com.tekclover.wms.core.model.transaction.SOLine;
import com.tekclover.wms.core.model.transaction.ShipmentOrder;

import com.tekclover.wms.core.repository.MailingReportRepository;
import com.tekclover.wms.core.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

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

	private Path fileStorageLocation = null;

	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	private String getIDMasterServiceApiUrl () {
		return propertiesConfig.getIdmasterServiceUrl();
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
		return null;
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
			List<ShipmentOrder> shipmentOrders = prepSOData (allRowsList);
			log.info("shipmentOrders : " + shipmentOrders);
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
			if(fileName.toLowerCase().startsWith("110")){
				if(fileName.toLowerCase().contains("delivery")){
					AuthToken authTokenForSetupService = authTokenService.getIDMasterServiceAuthToken();
					FileNameForEmail dbFileNameForEmail = new FileNameForEmail();
					dbFileNameForEmail.setDelivery110(fileName);
					dbFileNameForEmail.setReportDate(DateUtils.getCurrentDateWithoutTimestamp());
					dbFileNameForEmail.setDeletionIndicator(0L);
					FileNameForEmail fileNameForEmail = idMasterService.updateFileNameForEmail(dbFileNameForEmail, authTokenForSetupService.getAccess_token());
				}else if(fileName.toLowerCase().contains("dispatch")){
					AuthToken authTokenForSetupService = authTokenService.getIDMasterServiceAuthToken();
					FileNameForEmail dbFileNameForEmail = new FileNameForEmail();
					dbFileNameForEmail.setDispatch110(fileName);
					dbFileNameForEmail.setReportDate(DateUtils.getCurrentDateWithoutTimestamp());
					dbFileNameForEmail.setDeletionIndicator(0L);
					FileNameForEmail fileNameForEmail = idMasterService.updateFileNameForEmail(dbFileNameForEmail, authTokenForSetupService.getAccess_token());
				}
			}
			if(fileName.toLowerCase().startsWith("111")){
				if(fileName.toLowerCase().contains("delivery")){
					AuthToken authTokenForSetupService = authTokenService.getIDMasterServiceAuthToken();
					FileNameForEmail dbFileNameForEmail = new FileNameForEmail();
					dbFileNameForEmail.setDelivery111(fileName);
					dbFileNameForEmail.setReportDate(DateUtils.getCurrentDateWithoutTimestamp());
					dbFileNameForEmail.setDeletionIndicator(0L);
					FileNameForEmail fileNameForEmail = idMasterService.updateFileNameForEmail(dbFileNameForEmail, authTokenForSetupService.getAccess_token());
				}else if(fileName.toLowerCase().contains("dispatch")){
					AuthToken authTokenForSetupService = authTokenService.getIDMasterServiceAuthToken();
					FileNameForEmail dbFileNameForEmail = new FileNameForEmail();
					dbFileNameForEmail.setDispatch111(fileName);
					dbFileNameForEmail.setReportDate(DateUtils.getCurrentDateWithoutTimestamp());
					dbFileNameForEmail.setDeletionIndicator(0L);
					FileNameForEmail fileNameForEmail = idMasterService.updateFileNameForEmail(dbFileNameForEmail, authTokenForSetupService.getAccess_token());
				}
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
	 * @param location
	 * @param file
	 * @return
	 * @throws Exception
	 * @throws BadRequestException
	 */
	public Map<String, String> storingFileMailingReport(String location, MultipartFile file)
			throws Exception {

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

			AuthToken authTokenForSetupService = authTokenService.getIDMasterServiceAuthToken();

			MailingReport newMailingReport = new MailingReport();

			newMailingReport.setReportDate(DateUtils.getCurrentDateWithoutTimestamp());
			newMailingReport.setDeletionIndicator(0L);
			newMailingReport.setCompanyCodeId("1000");		//HardCode
			newMailingReport.setPlantId("1001");			//HardCode
			newMailingReport.setLanguageId("EN");			//HardCode
			newMailingReport.setMailSent("0");				//HardCode
			newMailingReport.setMailSentFailed("0");		//HardCode

			if(fileName.toLowerCase().startsWith("110")){

				newMailingReport.setWarehouseId("110");

			}
			if(fileName.toLowerCase().startsWith("111")){

				newMailingReport.setWarehouseId("111");

			}

			Optional<MailingReport> dbMailingReport = mailingReportRepository
														.findBycompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndFileNameAndDeletionIndicator(
																"1000",			//HardCode
																"1001", 						//HardCode
																newMailingReport.getWarehouseId(),
																"EN",					//HardCode
																fileName, 0L );

			Long countMailingReportByDate = mailingReportRepository.countByReportDateAndWarehouseId(
					DateUtils.getCurrentDateWithoutTimestamp(),
					newMailingReport.getWarehouseId());

			if(countMailingReportByDate == null) {
				countMailingReportByDate = 0L;
			}

			if(dbMailingReport.isEmpty()) {
				if (countMailingReportByDate != 1) {

					// Copy file to the target location (Replacing existing file with the same name)
					Path targetLocation = this.fileStorageLocation.resolve(fileName);
					Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

					newMailingReport.setFileName(fileName);
					newMailingReport.setUploaded(true);

					Boolean uploadedMailingReport = idMasterService.createMailingReport(newMailingReport, authTokenForSetupService.getAccess_token());

					if (uploadedMailingReport) {

						Map<String, String> mapFileProps = new HashMap<>();
						mapFileProps.put("file", fileName);
						mapFileProps.put("location", location);
						mapFileProps.put("status", "UPLOADED");
						return mapFileProps;

					} else {
						throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
					}
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
	private List<List<String>> readExcelData(File file) {
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
						listUploadData.add(String.valueOf(currentCell.getNumericCellValue()));
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
	 * @param allRowsList
	 * @return
	 */
	private List<ShipmentOrder> prepSOData (List<List<String>> allRowsList) {
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
}
