package com.github.danrog303.shelfspace.errors;

import com.github.danrog303.shelfspace.data.shelf.ShelfQuotaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;

import java.util.NoSuchElementException;

/**
 * Catches all exceptions thrown by MVC controllers and converts them to {@link ErrorResponse} instances.
 */
@Slf4j
@RestControllerAdvice
public class ErrorResponseAdvice {
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ShelfQuotaException.class)
    public ErrorResponse handleShelfQuotaExceedException(ShelfQuotaException e) {
        return new ErrorResponse("SHELF_QUOTA_EXCEEDED", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleBeanValidationFail(MethodArgumentNotValidException e) {
        String msg = String.format("Validation failed for field %s", e.getParameter().getParameterName());
        return new ErrorResponse("INVALID_DATA", msg);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponse handleUnknownCognitoUser() {
        return new ErrorResponse("UNAUTHORIZED", "You are not authorized to access this server.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse handleJsonParsingFail() {
        return new ErrorResponse("INVALID_DATA", "Invalid request body was sent.");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleAccessDenied() {
        return new ErrorResponse("ACCESS_DENIED", "Server understands your request, but refuses to authorize it.");
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponse handleMethodNotAllowed() {
        return new ErrorResponse("METHOD_NOT_ALLOWED", "Used HTTP method is not allowed on this endpoint.");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public ErrorResponse handleNotFound(Exception e) {
        return new ErrorResponse("NOT_FOUND", "Resource was not found.");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleFallback(Exception e) {
        log.error("Unknown error happened", e);
        return new ErrorResponse("INTERNAL_SERVER_ERROR", "Unknown server error happened");
    }
}
