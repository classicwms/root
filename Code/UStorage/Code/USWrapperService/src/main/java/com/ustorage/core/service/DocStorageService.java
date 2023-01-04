package com.ustorage.core.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.core.config.PropertiesConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DocStorageService {
	
	private static final String ACCESS_TOKEN = null;
	
	@Autowired
	PropertiesConfig propertiesConfig;

	/**
	 * 
	 * @param location
	 * @param file
	 * @return
	 * @throws DbxException
	 * @throws IOException
	 */
	public String getQualifiedFilePath (String location, String file) throws Exception {
		String filePath = propertiesConfig.getDocStorageBasePath();
		
		log.info("getQualifiedFilePath---location------>: " + location);
		log.info("getQualifiedFilePath---file------>: " + file);
		
		try {
			if (location != null && location.startsWith("document")) {
				filePath = filePath + propertiesConfig.getDocStorageDocumentPath();

				log.info("filePath------in document------>: " + filePath);
			}
		} catch (Exception e) {
			log.info("getQualifiedFilePath------Error------>: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		filePath = filePath + "/" + file;
		log.info("filePath: " + filePath);
		return filePath;
	}
}
