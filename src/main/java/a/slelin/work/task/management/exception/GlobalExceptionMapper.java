package a.slelin.work.task.management.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.sql.SQLException;
import java.util.Map;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Exception e) {
        Response.Status status;
        var builder = ErrorResponse.buildDefault(e)
                .path(uriInfo.getAbsolutePath().toString());

        switch (e) {
            case EntityNotFoundByIdException ex -> {
                status = Response.Status.NOT_FOUND;
                builder.httpStatus(404L)
                        .debugMessage("Cannot find entity by id. Check input id.")
                        .details(Map.of("entity", ex.getEntity().getSimpleName(),
                                "invalidId", ex.getInvalidKey().toString()));
            }
            case NotFoundException _ -> {
                status = Response.Status.NOT_FOUND;
                builder.httpStatus(404L)
                        .debugMessage("Method not found.Check url and method.")
                        .details(Map.of());
            }
            case ConstraintViolationException ex -> {
                status = Response.Status.BAD_REQUEST;
                builder.httpStatus(400L)
                        .debugMessage("Validation failed.")
                        .details(Map.of("errors", ValidationError.fromException(ex)));
            }
            case SQLException _ -> {
                status = Response.Status.BAD_REQUEST;
                builder.httpStatus(400L)
                        .debugMessage("SQL script failed.")
                        .details(Map.of());
            }
            case NotAllowedException _ -> {
                status = Response.Status.METHOD_NOT_ALLOWED;
                builder.httpStatus(405L)
                        .debugMessage("Method not allowed. Check url and method.")
                        .details(Map.of());
            }
            case IllegalArgumentException _ -> {
                status = Response.Status.BAD_REQUEST;
                builder.httpStatus(400L)
                        .debugMessage("Illegal argument. Check input data.")
                        .details(Map.of());
            }
            case null, default -> {
                status = Response.Status.INTERNAL_SERVER_ERROR;
                builder.httpStatus(500L)
                        .debugMessage("Unexpected error.")
                        .details(Map.of());
            }
        }

        return Response.status(status)
                .entity(builder.build())
                .build();
    }
}
