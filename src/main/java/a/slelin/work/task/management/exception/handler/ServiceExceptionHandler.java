package a.slelin.work.task.management.exception.handler;

import a.slelin.work.task.management.exception.EntityNotFoundByIdException;
import a.slelin.work.task.management.exception.EnumParseException;
import a.slelin.work.task.management.exception.ErrorResponse;
import a.slelin.work.task.management.exception.ValidationError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

@RestControllerAdvice
public class ServiceExceptionHandler {

    @ExceptionHandler(EntityNotFoundByIdException.class)
    public ResponseEntity<ErrorResponse> entityNotFoundByIdExceptionHandler(EntityNotFoundByIdException e,
                                                                            ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .debugMessage("Cannot find entity by id. Check input id.")
                        .details(Map.of("entity", e.getEntity().getSimpleName(),
                                "invalidId", e.getInvalidKey().toString()))
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> constraintViolationExceptionHandler(ConstraintViolationException e,
                                                                             ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .debugMessage("Validation failed.")
                        .details(Map.of("errors", ValidationError.fromException(e)))
                        .build());
    }

    @ExceptionHandler(EnumParseException.class)
    public ResponseEntity<ErrorResponse> enumParseExceptionHandler(EnumParseException e,
                                                                   ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .debugMessage("'%s' is not valid %s."
                                .formatted(e.getInvalidKey().toString(),
                                        e.getEnumClass().getSimpleName().toLowerCase()))
                        .details(Map.of("enum", e.getEnumClass().getSimpleName(),
                                "invalidKey", e.getInvalidKey().toString()))
                        .build());
    }
}
