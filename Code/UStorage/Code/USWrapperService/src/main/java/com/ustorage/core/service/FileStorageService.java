package com.ustorage.core.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ustorage.core.config.PropertiesConfig;
import com.ustorage.core.exception.BadRequestException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileStorageService {

	@Autowired
	PropertiesConfig propertiesConfig;

	@Autowired
	AuthTokenService authTokenService;

	private Path fileStorageLocation = null;

	/**
	 * 
	 * @param location
	 * @param file
	 * @return
	 * @throws Exception
	 * @throws BadRequestException
	 */
	public Map<String, String> storeFile(String location, MultipartFile file) throws Exception {
		
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
		this.fileStorageLocation = Paths.get(propertiesConfig.getFileUploadDir()).toAbsolutePath().normalize();
		if (!Files.exists(fileStorageLocation)) {
			try {
				Files.createDirectories(this.fileStorageLocation);
			} catch (Exception ex) {
				throw new BadRequestException("Could not create the directory where the uploaded files will be stored.");
			}
		}

		log.info("loca : " + fileStorageLocation);

		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		log.info ("filename before: " + fileName);
		fileName = fileName.replace(" ", "_");
		log.info ("filename after: " + fileName);
		try {
			// Check if the file's name contains invalid characters
			if(fileName.contains("..")) {
				throw new BadRequestException("Sorry! Filename contains invalid path sequence " + fileName);
			}

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			log.info("Copied : " + targetLocation );

		} catch (IOException ex) {
			ex.printStackTrace();
			throw new BadRequestException("Could not store file " + fileName + ". Please try again!");
		}
		return null;
	}
}
