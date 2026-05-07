package a.slelin.work.task.management.auth.exception.handler;

import a.slelin.work.task.management.auth.exception.*;
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
@SuppressWarnings("unused")
public class BusinessExceptionHandler {

    @ExceptionHandler(UserRoleRevokeException.class)
    public ResponseEntity<ErrorResponse> handleUserRoleRevokeException(UserRoleRevokeException e,
                                                                       ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.UNPROCESSABLE_CONTENT)
                        .debugMessage("Role \"USER\" cannot be revoked")
                        .build());
    }

    @ExceptionHandler(AdminActAdminException.class)
    public ResponseEntity<ErrorResponse> handleAdminActAdminException(AdminActAdminException e,
                                                                      ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.UNPROCESSABLE_CONTENT)
                        .debugMessage("Admin cannot influence on another admin")
                        .build());
    }

    @ExceptionHandler(UpdateNameSystemRoleException.class)
    public ResponseEntity<ErrorResponse> handleUpdateNameSystemRoleException(UpdateNameSystemRoleException e,
                                                                             ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.UNPROCESSABLE_CONTENT)
                        .debugMessage("Updating of system role's name '%s' is denied".formatted(e.getRole()))
                        .details(Map.of(
                                "role", e.getRole()
                        ))
                        .build());
    }

    @ExceptionHandler(DeleteSystemRoleException.class)
    public ResponseEntity<ErrorResponse> handleDeleteSystemRoleException(DeleteSystemRoleException e,
                                                                         ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.UNPROCESSABLE_CONTENT)
                        .debugMessage("Removing of system role '%s' is denied".formatted(e.getRole()))
                        .details(Map.of(
                                "role", e.getRole()
                        ))
                        .build());
    }

    @ExceptionHandler(ModifySystemRoleException.class)
    public ResponseEntity<ErrorResponse> handleModifySystemRoleException(ModifySystemRoleException e,
                                                                         ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.UNPROCESSABLE_CONTENT)
                        .debugMessage("Modifying of system role '%s' is denied".formatted(e.getRole()))
                        .details(Map.of(
                                "role", e.getRole()
                        ))
                        .build());
    }
}
