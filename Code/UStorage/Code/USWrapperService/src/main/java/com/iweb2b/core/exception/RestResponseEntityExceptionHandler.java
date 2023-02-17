package com.iweb2b.core.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

	@ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomErrorResponse> customHandleNotFound(Exception ex, WebRequest request) {
		String str = removeUnwantedStrings(ex.getLocalizedMessage());
		CustomErrorResponse error = new CustomErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setError(str);
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
	
	@Override
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, 
		HttpHeaders headers, HttpStatus status, WebRequest request) {
		String str = removeUnwantedStrings(ex.getLocalizedMessage());
		
		CustomErrorResponse error = new CustomErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setError(str);
        error.setStatus(HttpStatus.BAD_REQUEST.value());

	    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * handleConstraintViolationException
	 * @param ex
	 * @return
	 */
    @ExceptionHandler(value = { ConstraintViolationException.class })
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Invalid Input Exception: ", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
    }
	
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
    	String str = removeUnwantedStrings(ex.getLocalizedMessage());
		CustomErrorResponse error = new CustomErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setError(str);
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 
     * @param data
     * @return
     */
    private String removeUnwantedStrings (String data) {
    	data = data.replace('"', ' ');
    	return data = data.substring(data.indexOf('[')+1, data.lastIndexOf(']'));
    }
}