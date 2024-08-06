package com.example.steamreplica.service.game.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@AllArgsConstructor
@NoArgsConstructor
class ErrorDataType {
    private String message;
    private String webRequest;
}

@RestControllerAdvice
public class ApplicationExceptionHandler {
    private final String serverExceptionMessage = "Server exception: %s";
    private final String  internalMessage= "Internal error: %s";

    @ExceptionHandler(GameException.class)
    public ResponseEntity<?> handleGameNotFoundException(WebRequest webRequest, GameException exception) {
        String errorMessage = String.format(exception.getMessage(), serverExceptionMessage);
        ErrorDataType errorData = new ErrorDataType(errorMessage, webRequest.getDescription(false));
        return new ResponseEntity<>(errorData, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOtherException(WebRequest webRequest, Exception exception) {
        String errorMessage = String.format(exception.getMessage(), internalMessage);
        ErrorDataType errorData = new ErrorDataType(errorMessage, webRequest.getDescription(false));
        return new ResponseEntity<>(errorData, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
