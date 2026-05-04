package a.slelin.work.task.management.core.exception.handler;

import a.slelin.work.task.management.core.exception.ErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

@RestControllerAdvice
@SuppressWarnings("unused")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityExceptionHandler {

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ErrorResponse> authenticationCredentialsNotFoundException(
            AuthenticationCredentialsNotFoundException e,
            ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.UNAUTHORIZED)
                        .debugMessage("Credentials not found")
                        .build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> badCredentialsException(BadCredentialsException e,
                                                                 ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.UNAUTHORIZED)
                        .debugMessage("Invalid credentials")
                        .build());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> authenticationException(AuthenticationException e,
                                                                 ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.UNAUTHORIZED)
                        .debugMessage("Authentication failed")
                        .build());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> authorizationDeniedException(AuthorizationDeniedException e,
                                                                      ServletWebRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.buildDefault(e, request)
                        .httpStatus(HttpStatus.FORBIDDEN)
                        .debugMessage("Method is forbidden")
                        .build());
    }
}
