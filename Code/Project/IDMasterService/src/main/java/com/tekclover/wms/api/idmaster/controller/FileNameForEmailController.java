package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.email.*;
import com.tekclover.wms.api.idmaster.service.FileNameForEmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"FileNameForEmail"}, value = "FileNameForEmail  Operations related to FileNameForEmailController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "FileNameForEmail ",description = "Operations related to FileNameForEmail ")})
@RequestMapping("/filenameforemail")
@RestController
public class FileNameForEmailController {
	
	@Autowired
	FileNameForEmailService filenameforemailService;
	
    @ApiOperation(response = FileNameForEmail.class, value = "Get all FileNameForEmail details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<FileNameForEmail> filenameforemailList = filenameforemailService.getFileNameForEmailList();
		return new ResponseEntity<>(filenameforemailList, HttpStatus.OK);
	}
    
    @ApiOperation(response = FileNameForEmail.class, value = "Get a FileNameForEmail") // label for swagger 
	@GetMapping("/{fileNameId}")
	public ResponseEntity<?> getFileNameForEmail(@PathVariable Long fileNameId) {
    	FileNameForEmail filenameforemail = 
    			filenameforemailService.getFileNameForEmail(fileNameId);
    	log.info("FileNameForEmail : " + filenameforemail);
		return new ResponseEntity<>(filenameforemail, HttpStatus.OK);
	}
	@ApiOperation(response = FileNameForEmail.class, value = "Get a FileNameForEmail by Date") // label for swagger
	@GetMapping("/fileNameByDate/{fileNameDate}")
	public ResponseEntity<?> getFileNameForEmailByDate(@PathVariable String fileNameDate) {
		FileNameForEmail filenameforemail =
				filenameforemailService.getFileNameForEmailByDate(fileNameDate);
		return new ResponseEntity<>(filenameforemail, HttpStatus.OK);
	}
    @ApiOperation(response = FileNameForEmail.class, value = "Update FileNameForEmail") // label for swagger
    @PostMapping("")
	public ResponseEntity<?> patchFileNameForEmail(
			@Valid @RequestBody FileNameForEmail updateFileNameForEmail)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		FileNameForEmail createdFileNameForEmail = 
				filenameforemailService.updateFileNameForEmail(updateFileNameForEmail);
		return new ResponseEntity<>(createdFileNameForEmail , HttpStatus.OK);
	}
    
    @ApiOperation(response = FileNameForEmail.class, value = "Delete FileNameForEmail") // label for swagger
	@DeleteMapping("/{fileNameId}")
	public ResponseEntity<?> deleteFileNameForEmail(@PathVariable Long fileNameId) {
    	filenameforemailService.deleteFileNameForEmail(fileNameId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}