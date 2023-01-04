package com.ustorage.api.master.controller.exception;

import java.time.LocalDateTime;
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
		    ex.getBindingResult().getAllErrors().forEach((error) -> {
		        String fieldName = ((FieldError) error).getField();
		        String errorMessage = error.getDefaultMessage();
		        errors.put(fieldName, errorMessage);
		    });
	    return new ResponseEntity<>(errors, status);
	}
	
    @ExceptionHandler(value = { ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Invalid Input Exception: ", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
	    ex.getConstraintViolations().forEach((error) -> {
	        String fieldName = ((FieldError) error).getField();
	        String errorMessage = error.getMessage();
	        errors.put(fieldName, errorMessage);
	    });
        return new ResponseEntity<Object>(errors,HttpStatus.BAD_REQUEST);
    }
    
	@ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
    	String str = ex.getLocalizedMessage();
		CustomErrorResponse error = new CustomErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setError(str);
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(value = { BadRequestException.class })
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
    	String str = ex.getLocalizedMessage();
		CustomErrorResponse error = new CustomErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setError(str);
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}