package a.slelin.work.task.management.auth.exception.handler;

import a.slelin.work.task.management.auth.exception.DeleteSystemRoleException;
import a.slelin.work.task.management.auth.exception.ModifySystemRoleException;
import a.slelin.work.task.management.auth.exception.UpdateNameSystemRoleException;
import a.slelin.work.task.management.core.exception.ErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

@Order(2)
@RestControllerAdvice
public class BusinessExceptionHandler {

    @ExceptionHandler(UpdateNameSystemRoleException.class)
    public ResponseEntity<ErrorResponse> handleUpdateNameSystemRoleException(UpdateNameSystemRoleException e,
                                                                             ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .debugMessage("Updating of system role's name '%s' is denied.".formatted(e.getRole()))
                        .details(Map.of(
                                "role", e.getRole()
                        ))
                        .build());
    }

    @ExceptionHandler(DeleteSystemRoleException.class)
    public ResponseEntity<ErrorResponse> handleDeleteSystemRoleException(DeleteSystemRoleException e,
                                                                         ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .debugMessage("Removing of system role '%s' is denied.".formatted(e.getRole()))
                        .details(Map.of(
                                "role", e.getRole()
                        ))
                        .build());
    }

    @ExceptionHandler(ModifySystemRoleException.class)
    public ResponseEntity<ErrorResponse> handleModifySystemRoleException(ModifySystemRoleException e,
                                                                         ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .debugMessage("Modifying of system role '%s' is denied.".formatted(e.getRole()))
                        .details(Map.of(
                                "role", e.getRole()
                        ))
                        .build());
    }
}
