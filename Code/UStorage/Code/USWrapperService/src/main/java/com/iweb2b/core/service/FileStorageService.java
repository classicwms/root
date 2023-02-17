package com.iweb2b.core.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.UploadErrorException;
import com.iweb2b.core.config.PropertiesConfig;
import com.iweb2b.core.exception.BadRequestException;
import com.iweb2b.core.model.auth.AuthToken;
import com.iweb2b.core.model.integration.AddDestinationDetail;
import com.iweb2b.core.model.integration.AddOriginDetail;
import com.iweb2b.core.model.integration.AddSoftDataUpload;
import com.iweb2b.core.model.integration.SoftDataUpload;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileStorageService {

	@Autowired
	PropertiesConfig propertiesConfig;

	@Autowired
	AuthTokenService authTokenService;
	
	@Autowired
	IntegrationService integrationService;

	private Path fileStorageLocation = null;

	/**
	 * 
	 * @param location
	 * @param file
	 * @param isNonEmailMerge
	 * @return
	 * @throws DbxException
	 * @throws UploadErrorException
	 */
	public Map<String, String> storeFile(String location, MultipartFile file) throws Exception {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		log.info("filename before: " + fileName);

		String locationPath = null;
		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new BadRequestException("Sorry! Filename contains invalid path sequence " + fileName);
			}

			this.fileStorageLocation = Paths.get(locationPath).toAbsolutePath().normalize();
			log.info("fileStorageLocation--------> " + fileStorageLocation);

			if (!Files.exists(fileStorageLocation)) {
				try {
					Files.createDirectories(this.fileStorageLocation);
				} catch (Exception ex) {
					ex.printStackTrace();
					throw new BadRequestException(
							"Could not create the directory where the uploaded files will be stored.");
				}
			}

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

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
	 * @throws Exception
	 */
	public Map<String, String> storeFile(MultipartFile file) throws Exception {
		this.fileStorageLocation = Paths.get(propertiesConfig.getFileUploadLocation()).toAbsolutePath().normalize();
		if (!Files.exists(fileStorageLocation)) {
			try {
				Files.createDirectories(this.fileStorageLocation);
			} catch (Exception ex) {
				throw new BadRequestException(
						"Could not create the directory where the uploaded files will be stored.");
			}
		}

		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			if (fileName.contains("..")) {
				throw new BadRequestException("Sorry! Filename contains invalid path sequence " + fileName);
			}
			
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			log.info("Copied : " + targetLocation);
			
			List<List<String>> allRowsList = readExcelData(targetLocation.toFile());
			AddSoftDataUpload newSoftDataUpload = prepSoftDataUpload (allRowsList);
			
			// Invoke service to store and call Shipsy API
			SoftDataUpload softDataUpload = integrationService.createSoftDataUpload(newSoftDataUpload, "Upload Process");
			log.info("softDataUpload : " + softDataUpload);
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
//			Workbook workbook = new XSSFWorkbook(file);
//			org.apache.poi.ss.usermodel.Sheet datatypeSheet = workbook.getSheetAt(0);
//			Iterator<Row> iterator = datatypeSheet.iterator();
//			
//			List<List<String>> allRowsList = new ArrayList<> ();
//			while (iterator.hasNext()) {
//				Row currentRow = iterator.next();
//				Iterator<Cell> cellIterator = currentRow.iterator();
//				
//				// Moving to data row instead of header row
//				currentRow = iterator.next();
//				cellIterator = currentRow.iterator();
//				
//				List<String> listUploadData = new ArrayList<String> ();
//				while (cellIterator.hasNext()) {
//					Cell currentCell = cellIterator.next();
//					if (currentCell.getCellType() == CellType.STRING) {
//						log.info(currentCell.getStringCellValue() + "||");
//						if (currentCell.getStringCellValue() != null && !currentCell.getStringCellValue().trim().isEmpty()) {
//							listUploadData.add(currentCell.getStringCellValue());
//							log.info("== " + listUploadData.size());
//						} else {
//							listUploadData.add(" ");
//							log.info("=#= " + listUploadData.size());
//						}
//					} else if (currentCell.getCellType() == CellType.NUMERIC) {
//						log.info(currentCell.getNumericCellValue() + "--");
//						listUploadData.add(String.valueOf(currentCell.getNumericCellValue()));
//					}
//				}
//				allRowsList.add(listUploadData);
//			}
//			return allRowsList;
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param allRowsListallRowsList
	 * @return 
	 */
	private AddSoftDataUpload prepSoftDataUpload (List<List<String>> allRowsList) {
		AddSoftDataUpload newSoftDataUpload = new AddSoftDataUpload();
		AddOriginDetail newOriginDetail = new AddOriginDetail();
		AddDestinationDetail newDestinationDetail = new AddDestinationDetail();

		Set<AddOriginDetail> setOrigDetails = new HashSet<>();
		Set<AddDestinationDetail> setDestinDetails = new HashSet<>();
		for (List<String> listUploadedData : allRowsList) {
			log.info("sixxe: " + listUploadedData.size());
			newOriginDetail.setName(listUploadedData.get(0));
			newOriginDetail.setPhone(listUploadedData.get(1));
			newOriginDetail.setAddressLine1(listUploadedData.get(2));
			newOriginDetail.setAddressLine2(listUploadedData.get(3));
			newOriginDetail.setPincode(listUploadedData.get(4));
			newOriginDetail.setCountry(listUploadedData.get(30));
			
			newDestinationDetail.setName(listUploadedData.get(5));
			newDestinationDetail.setPhone(listUploadedData.get(6));
			newDestinationDetail.setAddressLine1(listUploadedData.get(7));
			newDestinationDetail.setAddressLine2(listUploadedData.get(8));
			newDestinationDetail.setPincode(listUploadedData.get(9));
			newDestinationDetail.setCountry(listUploadedData.get(31));
			
			newDestinationDetail.setLatitude(listUploadedData.get(32));
			
			newDestinationDetail.setLongitude(listUploadedData.get(33));

			newSoftDataUpload.setDimensionUnit(listUploadedData.get(10));
			newSoftDataUpload.setLength(listUploadedData.get(11));
			newSoftDataUpload.setWidth(listUploadedData.get(12));
			newSoftDataUpload.setHeight(listUploadedData.get(13));
			newSoftDataUpload.setWeightUnit(listUploadedData.get(14));
			newSoftDataUpload.setWeight(listUploadedData.get(15));
			newSoftDataUpload.setServiceTypeId(listUploadedData.get(16));
			newSoftDataUpload.setReferenceNumber(listUploadedData.get(17));
			newSoftDataUpload.setCodAmount(listUploadedData.get(18));
			newSoftDataUpload.setReportingBranchCode(listUploadedData.get(19));
			newSoftDataUpload.setWorkerCode(listUploadedData.get(20));
			newSoftDataUpload.setCodCollectionMode(listUploadedData.get(21));
			newSoftDataUpload.setCodFavorOf(listUploadedData.get(22));
			newSoftDataUpload.setManifestNumber(listUploadedData.get(23));
			newSoftDataUpload.setManifestTime(listUploadedData.get(24));
			newSoftDataUpload.setHubCode(listUploadedData.get(25));
			newSoftDataUpload.setAutoAccept(listUploadedData.get(26));
			newSoftDataUpload.setCustomerCode(listUploadedData.get(27));
			newSoftDataUpload.setDeliveryTimeSlotStart(listUploadedData.get(28));
			newSoftDataUpload.setDeliveryTimeSlotEnd(listUploadedData.get(29));
			newSoftDataUpload.setConstraintTag(listUploadedData.get(34));
		}
		
		// Set <- newOriginDetail
		setOrigDetails.add(newOriginDetail);
		setDestinDetails.add(newDestinationDetail);
		
		// Parent - Set
		newSoftDataUpload.setOriginDetail(setOrigDetails);
		newSoftDataUpload.setDestinationDetail(setDestinDetails);
		return newSoftDataUpload;
	}
}
