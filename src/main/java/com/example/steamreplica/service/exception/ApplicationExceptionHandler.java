package com.example.steamreplica.service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final String  internalMessage = "Internal error: %s";

    @ExceptionHandler(GameException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleGameNotFoundException(Exception ex, WebRequest webRequest) {
        String errorMessage = String.format(serverExceptionMessage, ex.getMessage());
        String causerMessage = ex.getCause().getCause().getMessage();
        ErrorDataType exceptionData = new ErrorDataType(String.format(internalMessage, errorMessage), String.format(causer, causerMessage), webRequest.getDescription(false));
        return new ResponseEntity<>(exceptionData, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    ResponseEntity<?> handleOtherException(Exception ex, WebRequest webRequest) {
        String errorMessage = ex.getMessage();
        String causerMessage = ex.getCause().getCause().getMessage();
        ErrorDataType exceptionData = new ErrorDataType(String.format(internalMessage, errorMessage), String.format(causer, causerMessage), webRequest.getDescription(false));
        return new ResponseEntity<>(exceptionData, HttpStatus.NOT_FOUND);
    }
}
