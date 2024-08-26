package com.example.steamreplica.service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
class ErrorDataType {
    private String message;
    private String causerMessage;
    private String webRequest;
}

@RestControllerAdvice
public class ApplicationExceptionHandler {
    private final String serverExceptionMessage = "Server exception: %s";
    private final String  causer = "Causer message error: %s";

    @ExceptionHandler({UsernameNotFoundException.class, ResourceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleResourceFoundException(RuntimeException ex, WebRequest webRequest) {
        ErrorDataType exceptionData = extractErrorData(ex, webRequest);
        return new ResponseEntity<>(exceptionData, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(ResourceExitedException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<?> handleResourceExitedException(ResourceExitedException ex, WebRequest webRequest) {
        ErrorDataType exceptionData = extractErrorData(ex, webRequest);
        return new ResponseEntity<>(exceptionData, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ResponseEntity<?> handleOtherException(Exception ex, WebRequest webRequest) {
        ErrorDataType exceptionData = extractErrorData(ex, webRequest);
        return new ResponseEntity<>(exceptionData, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CacheException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    ResponseEntity<?> handleOtherException(CacheException ex, WebRequest webRequest) {
        ErrorDataType exceptionData = extractErrorData(ex, webRequest);
        return new ResponseEntity<>(exceptionData, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ResponseEntity<?> handleOtherException(AuthenticationException ex, WebRequest webRequest) {
        ErrorDataType exceptionData = extractErrorData(ex, webRequest);
        return new ResponseEntity<>(exceptionData, HttpStatus.UNAUTHORIZED);
    }

    ErrorDataType extractErrorData(Exception ex, WebRequest webRequest) {
        String errorMessage = String.format(serverExceptionMessage, ex.getMessage());
        String causerMessage = String.format(serverExceptionMessage, ex.getMessage());
        if (ex.getCause() != null && ex.getCause().getCause() != null) causerMessage = ex.getCause().getCause().getMessage();
        return new ErrorDataType(errorMessage, String.format(causer, causerMessage), webRequest.getDescription(false));
    }
}
