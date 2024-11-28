package com.tekclover.wms.api.enterprise.controller.exception;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, 
		HttpHeaders headers, HttpStatus status, WebRequest request) {
			Map<String, String> errors = new HashMap<>();
			log.info("hello:");
		    ex.getBindingResult().getAllErrors().forEach((error) -> {
		        String fieldName = ((FieldError) error).getField();
		        String errorMessage = error.getDefaultMessage();
		        errors.put(fieldName, errorMessage);
		    });
	    return new ResponseEntity<>(errors, status);
	}
	
	/**
	 * handleConstraintViolationException
	 * @param ex
	 * @return
	 */
    @ExceptionHandler(value = { ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Invalid Input Exception: ", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
		log.info("hello:");
	    ex.getConstraintViolations().forEach((error) -> {
	        String fieldName = ((FieldError) error).getField();
	        String errorMessage = error.getMessage();
	        errors.put(fieldName, errorMessage);
	    });
        return new ResponseEntity<Object>(errors,HttpStatus.BAD_REQUEST);
    }
    
    /**
     * handleException
     * @param ex
     * @return
     */
    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<?> handleException(Exception ex) {
    	HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new CustomErrorType(ex.getMessage(), status), status);
    }
    
    @ExceptionHandler(value = { BadRequestException.class })
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
    	HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new CustomErrorType(ex.getMessage(), status), status);
    }
}