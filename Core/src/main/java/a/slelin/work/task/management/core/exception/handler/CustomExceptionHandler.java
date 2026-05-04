package a.slelin.work.task.management.core.exception.handler;

import a.slelin.work.task.management.core.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

@Order(10)
@RestControllerAdvice
@SuppressWarnings("unused")
public class CustomExceptionHandler {

    @ExceptionHandler(Unique2FieldsEntityException.class)
    public ResponseEntity<ErrorResponse> unique2FieldsEntityException(Unique2FieldsEntityException e,
                                                                      ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.CONFLICT)
                        .debugMessage("Violation of the uniqueness of two entity fields")
                        .details(Map.of(
                                "entity", e.getEntity().getSimpleName(),
                                "field", e.getField(),
                                "invalidValue", e.getInvalidValue().toString(),
                                "field2", e.getField2(),
                                "invalidValue2", e.getInvalidValue().toString()))
                        .build());
    }

    @ExceptionHandler(UniqueFieldEntityException.class)
    public ResponseEntity<ErrorResponse> uniqueFieldEntityException(UniqueFieldEntityException e,
                                                                    ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.CONFLICT)
                        .debugMessage("Violation of the uniqueness of an entity's value")
                        .details(Map.of(
                                "entity", e.getEntity().getSimpleName(),
                                "field", e.getField(),
                                "invalidValue", e.getInvalidValue().toString()))
                        .build());
    }

    @ExceptionHandler(EntityNotFoundByIdException.class)
    public ResponseEntity<ErrorResponse> entityNotFoundByIdExceptionHandler(EntityNotFoundByIdException e,
                                                                            ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .debugMessage("Cannot find entity by id. Check input id")
                        .details(Map.of(
                                "entity", e.getEntity().getSimpleName(),
                                "invalidId", e.getInvalidProperty().toString()))
                        .build());
    }

    @ExceptionHandler(EntityNotFoundByPropertyException.class)
    public ResponseEntity<ErrorResponse> entityNotFoundByPropertyExceptionHandler(
            EntityNotFoundByPropertyException e,
            ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .debugMessage("Cannot find entity by property. Check input property")
                        .details(Map.of("entity", e.getEntity().getSimpleName(),
                                "invalidProperty", e.getInvalidProperty().toString()))
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> constraintViolationExceptionHandler(ConstraintViolationException e,
                                                                             ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .debugMessage("Validation failed")
                        .details(Map.of("errors", ValidationError.fromException(e)))
                        .build());
    }

    @ExceptionHandler(EnumParseException.class)
    public ResponseEntity<ErrorResponse> enumParseExceptionHandler(EnumParseException e,
                                                                   ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .debugMessage("'%s' is not valid %s"
                                .formatted(e.getInvalidKey().toString(),
                                        e.getEnumClass().getSimpleName().toLowerCase()))
                        .details(Map.of("enum", e.getEnumClass().getSimpleName(),
                                "invalidKey", e.getInvalidKey().toString()))
                        .build());
    }

    @ExceptionHandler(FilterParseException.class)
    public ResponseEntity<ErrorResponse> filterParseExceptionHandler(FilterParseException e,
                                                                     ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .debugMessage("Filter parsing failed")
                        .build());
    }

    @ExceptionHandler(BusinessFault.class)
    public ResponseEntity<ErrorResponse> handleBusinessFault(BusinessFault e,
                                                             ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.CONFLICT)
                        .debugMessage("Business fault has occurred")
                        .build());
    }
}
