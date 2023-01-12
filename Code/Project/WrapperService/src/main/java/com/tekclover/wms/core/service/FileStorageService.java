package com.tekclover.wms.core.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.exception.BadRequestException;

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
     * @throws DbxException 
     * @throws UploadErrorException 
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
   
    /**
     * loadFileAsResource
     * @param fileName
     * @return
     */
    public Resource loadFileAsResource (String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new BadRequestException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new BadRequestException("File not found " + fileName);
        }
    }
}
