package a.slelin.work.task.management.exception.handler;

import a.slelin.work.task.management.exception.ErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;

@Order(3)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<ErrorResponse> notFoundHandler(HttpClientErrorException.NotFound e,
                                                         ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .debugMessage("Method not found.Check url and method.")
                        .build());
    }

    @ExceptionHandler(HttpClientErrorException.MethodNotAllowed.class)
    public ResponseEntity<ErrorResponse> methodNotAllowedHandler(HttpClientErrorException.MethodNotAllowed e,
                                                                 ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.METHOD_NOT_ALLOWED)
                        .debugMessage("Method not allowed. Check url and method.")
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalArgumentHandler(IllegalArgumentException e,
                                                                ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .debugMessage("Illegal argument. Check input data.")
                        .build());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeExceptionHandler(RuntimeException e,
                                                                 ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .debugMessage("Unexpected error: unchecked exception.")
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e,
                                                          ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .debugMessage("Unexpected error: checked exception.")
                        .build());
    }
}
