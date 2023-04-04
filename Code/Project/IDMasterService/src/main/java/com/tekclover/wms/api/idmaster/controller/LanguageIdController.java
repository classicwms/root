package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.languageid.AddLanguageId;
import com.tekclover.wms.api.idmaster.model.languageid.LanguageId;
import com.tekclover.wms.api.idmaster.model.languageid.UpdateLanguageId;
import com.tekclover.wms.api.idmaster.service.LanguageIdService;
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
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"LanguageId"}, value = "LanguageId  Operations related to LanguageIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "LanguageId ",description = "Operations related to LanguageId ")})
@RequestMapping("/languageid")
@RestController
public class LanguageIdController {
	
	@Autowired
	LanguageIdService languageidService;
	
    @ApiOperation(response = LanguageId.class, value = "Get all LanguageId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<LanguageId> languageidList = languageidService.getLanguageIds();
		return new ResponseEntity<>(languageidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = LanguageId.class, value = "Get a LanguageId") // label for swagger 
	@GetMapping("/{languageId}")
	public ResponseEntity<?> getLanguageId(@PathVariable String languageId) {
    	LanguageId languageid = 
    			languageidService.getLanguageId(languageId);
    	log.info("LanguageId : " + languageid);
		return new ResponseEntity<>(languageid, HttpStatus.OK);
	}
    
    @ApiOperation(response = LanguageId.class, value = "Create LanguageId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postLanguageId(@Valid @RequestBody AddLanguageId newLanguageId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		LanguageId createdLanguageId = languageidService.createLanguageId(newLanguageId, loginUserID);
		return new ResponseEntity<>(createdLanguageId , HttpStatus.OK);
	}
    
    @ApiOperation(response = LanguageId.class, value = "Update LanguageId") // label for swagger
    @PatchMapping("/{languageId}")
	public ResponseEntity<?> patchLanguageId(@PathVariable String languageId,
			@Valid @RequestBody UpdateLanguageId updateLanguageId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		LanguageId createdLanguageId = 
				languageidService.updateLanguageId(languageId, loginUserID, updateLanguageId);
		return new ResponseEntity<>(createdLanguageId , HttpStatus.OK);
	}
    
    @ApiOperation(response = LanguageId.class, value = "Delete LanguageId") // label for swagger
	@DeleteMapping("/{languageId}")
	public ResponseEntity<?> deleteLanguageId(@PathVariable String languageId,
											  @RequestParam String loginUserID) {
    	languageidService.deleteLanguageId(languageId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}