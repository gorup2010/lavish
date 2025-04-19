package com.nashrookie.lavish.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.nashrookie.lavish.dto.response.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    private static final String ERROR_LOG_FORMAT = "Error: URI: {}, ErrorCode: {}, Message: {}";

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex,
            WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(RefreshTokenInvalidException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenInvalidException(RefreshTokenInvalidException ex,
            WebRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex, request);
    }

    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request) {
    //     return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    // }
    
    private String getServletPath(WebRequest webRequest) {
        ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
        return servletRequest.getRequest().getServletPath();
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, Exception ex,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(status.value(), ex.getMessage());

        log.error(ERROR_LOG_FORMAT, this.getServletPath(request), status.value(), ex.getMessage());

        return ResponseEntity.status(status).body(errorResponse);
    }

}