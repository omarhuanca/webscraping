package com.example.webscraping.util.exception.response;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.webscraping.util.exception.response.custom.CustomBadRequestException;
import com.example.webscraping.util.exception.response.custom.CustomNotFoundException;
import com.example.webscraping.util.exception.response.custom.CustomRuntimeException;

/**
 * AEresponseEntityExceptionHandler captures all rest exception to be
 * customized.
 *
 * @author Elio Arias
 * @since 1.0
 */
@RestControllerAdvice
public class AEresponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    final static Logger logger = LoggerFactory.getLogger(AEresponseEntityExceptionHandler.class);

    // Custom Exceptions
    @ExceptionHandler(CustomBadRequestException.class)
    public final ResponseEntity<Object> handleCustomBadRequestEx(CustomBadRequestException ex, WebRequest webRequest) {
        logger.warn("CustomBadRequestException.class -> ({}): {}.", webRequest.getDescription(false), ex.getMessage());
        return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomNotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundEx(CustomNotFoundException ex, WebRequest webRequest) {
        logger.warn("CustomNotFoundException.class -> ({}): {}.", webRequest.getDescription(false), ex.getMessage());
        return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomRuntimeException.class)
    public final ResponseEntity<Object> handleCustomRuntimeEx(CustomRuntimeException ex, WebRequest webRequest) {
        logger.warn("CustomNotFoundException.class -> ({}): {}.", webRequest.getDescription(false), ex.getMessage());
        logger.warn("CustomRuntimeException.class -> handleException: {}.", ex.getXErrorResponse());
        return new ResponseEntity<Object>(ex.getXErrorResponse(), ex.getHttpStatus());
    }

    // Native Exceptions
    @ExceptionHandler(InvalidParameterException.class)
    public final ResponseEntity<Object> handleInvalidParameterEx(InvalidParameterException ex, WebRequest webRequest) {
        logger.warn("InvalidParameterException.class -> ({}): {}.", webRequest.getDescription(false), ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllException(Exception ex, WebRequest webRequest) {
        logger.warn("Exception.class -> ({}): {}.", webRequest.getDescription(false), ex.getMessage());
        return new ResponseEntity<>(new XErrorResponse(500, "Internal server error", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    @ExceptionHandler(RuntimeException.class)
//    public final ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest webRequest) {
//        logger.warn("RuntimeException.class -> ({}): {}.", webRequest.getDescription(false), ex.getMessage());
////        return new ResponseEntity<>(new XErrorResponse(409, "Internal server error", ex.getMessage()),
////                HttpStatus.CONFLICT);
//
//        String bodyOfResponse = ex.getMessage();
//        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, webRequest);
//    }

    // @Override Exceptions
    // other exception handlers below
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.warn("AEreeh -> handleMethodArgumentNotValid({}): {}.", request.getDescription(false), ex.getMessage());
        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        return new ResponseEntity<>(new XErrorResponse(HttpStatus.BAD_REQUEST, "Validation Failed", details),
                HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.warn("AEreeh -> handleHttpMessageNotReadable({}): {}.", request.getDescription(false), ex.getMessage());
        return new ResponseEntity<Object>(
                new XErrorResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request", ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}