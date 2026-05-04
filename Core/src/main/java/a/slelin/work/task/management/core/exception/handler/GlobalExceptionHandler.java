package a.slelin.work.task.management.core.exception.handler;

import a.slelin.work.task.management.core.exception.ErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;
import java.util.stream.Collectors;

@Order(10)
@RestControllerAdvice
@SuppressWarnings("unused")
public class GlobalExceptionHandler {

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLocking(ObjectOptimisticLockingFailureException e,
                                                                 ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.CONFLICT)
                        .debugMessage("The record was updated by another user. Please refresh and try again")
                        .build());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException e,
                                                             ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .debugMessage("Missing required header: " + e.getHeaderName())
                        .build());
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErrorResponse> handleMissingPathVariable(MissingPathVariableException e, ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .debugMessage("Missing path variable: " + e.getVariableName())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                      ServletWebRequest request) {
        Map<String, String> validationErrors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null
                                ? fieldError.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing
                ));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .debugMessage("Validation failed")
                        .details(Map.of("errors", validationErrors))
                        .build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e,
                                                                          ServletWebRequest request) {
        String message = String.format("Parameter '%s' should be of type '%s'", e.getName(),
                e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .debugMessage(message)
                        .build());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException e,
                                                                              ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .debugMessage("Missing required parameter: " + e.getParameterName())
                        .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e,
                                                                      ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .debugMessage("Malformed JSON request or invalid value")
                        .build());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException e,
                                                                      ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.CONFLICT)
                        .debugMessage("Data integrity violation")
                        .build());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException e,
                                                              ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .debugMessage("No handler found for " + e.getHttpMethod() + " " + e.getRequestURL())
                        .build());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                                             ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.METHOD_NOT_ALLOWED)
                        .debugMessage("Method " + e.getMethod() + " is not supported for this endpoint")
                        .build());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e,
                                                                         ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .debugMessage("Content-Type '" + e.getContentType() + "' is not supported")
                        .build());
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException e,
                                                                          ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.NOT_ACCEPTABLE)
                        .debugMessage("Requested media type is not acceptable")
                        .build());
    }

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<ErrorResponse> notFoundHandler(HttpClientErrorException.NotFound e,
                                                         ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .debugMessage("Method not found. Check url and method")
                        .build());
    }

    @ExceptionHandler(HttpClientErrorException.MethodNotAllowed.class)
    public ResponseEntity<ErrorResponse> methodNotAllowedHandler(HttpClientErrorException.MethodNotAllowed e,
                                                                 ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.METHOD_NOT_ALLOWED)
                        .debugMessage("Method not allowed. Check url and method")
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalArgumentHandler(IllegalArgumentException e,
                                                                ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .debugMessage("Illegal argument. Check input data")
                        .build());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeExceptionHandler(RuntimeException e,
                                                                 ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .debugMessage("Unexpected error: unchecked exception")
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e,
                                                          ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .debugMessage("Unexpected error: checked exception")
                        .build());
    }
}
