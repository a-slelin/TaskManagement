package a.slelin.work.task.management.core.exception.handler;

import a.slelin.work.task.management.core.exception.BusinessFault;
import a.slelin.work.task.management.core.exception.ErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

@Order(3)
@RestControllerAdvice
@SuppressWarnings("unused")
public class BusinessExceptionHandler {

    @ExceptionHandler(BusinessFault.class)
    public ResponseEntity<ErrorResponse> handleBusinessFault(BusinessFault e,
                                                             ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .debugMessage("Business fault has occurred.")
                        .build());
    }
}
