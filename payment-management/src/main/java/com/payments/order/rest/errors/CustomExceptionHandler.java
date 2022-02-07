package com.payments.order.rest.errors;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.TransactionException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;


@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler{

	
	@Autowired
	public CustomExceptionHandler() {

	}

	@ExceptionHandler
    void handleNotFoundException(NoSuchElementException e, HttpServletResponse response) throws IOException {
    	response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage()); 	
    }
	
	@ExceptionHandler
    void handleBadRequestException(BadRequestException e, HttpServletResponse response) throws IOException {
    	response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage()); 	
    }
    
    @ExceptionHandler
    void handleHttpClientErrorException(HttpClientErrorException e, HttpServletResponse response) throws IOException {
    	response.sendError(e.getRawStatusCode(), e.getMessage());
    }
    
    @ExceptionHandler
    void handleJsonProcessingException(JsonProcessingException e, HttpServletResponse response) throws IOException {
    	response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
    
    @ExceptionHandler
    void handleServerErrorException(ServerErrorException e, HttpServletResponse response) throws IOException {
    	response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }
    
    @ExceptionHandler
    void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
    	response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler
    void handleJsonParseException(JsonParseException e, HttpServletResponse response) throws IOException {
    	response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }
     
    @ExceptionHandler
    void handleConstraintViolationException(ConstraintViolationException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
    
    @ExceptionHandler
    void handleTransactionException(TransactionException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

}