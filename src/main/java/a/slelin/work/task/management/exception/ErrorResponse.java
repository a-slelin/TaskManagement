package a.slelin.work.task.management.exception;

import a.slelin.work.task.management.util.HttpMethodDeserializer;
import a.slelin.work.task.management.util.HttpMethodSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Builder
public record ErrorResponse(@NotBlank String path,
                            @JsonSerialize(using = HttpMethodSerializer.class)
                            @JsonDeserialize(using = HttpMethodDeserializer.class)
                            @NotNull HttpMethod httpMethod,
                            @NotNull HttpStatus httpStatus,
                            String debugMessage,
                            @NotNull String message,
                            @NotNull String exception,
                            String causeException,
                            Map<String, Object> details,
                            @NotNull LocalDateTime timeStamp) {

    public final static String TIME_STAMP_PATTERN = "dd.MM.yyyy HH:mm:ss";

    public final static DateTimeFormatter TIME_STAMP_FORMATTER = DateTimeFormatter.ofPattern(TIME_STAMP_PATTERN);

    public static ErrorResponse.ErrorResponseBuilder buildDefault(Exception e) {
        if (e == null) {
            return ErrorResponse.builder();
        }

        return ErrorResponse.builder()
                .message(e.getMessage())
                .exception(e.getClass().getSimpleName())
                .causeException(e.getCause() == null ? "" : ((Exception) e.getCause()).getClass().getSimpleName())
                .details(Map.of())
                .timeStamp(LocalDateTime.now());
    }

    public static ErrorResponse.ErrorResponseBuilder buildDefault(Exception e, ServletWebRequest request) {
        if (e == null || request == null) {
            return ErrorResponse.builder();
        }

        return buildDefault(e)
                .path(request.getRequest().getRequestURL().toString())
                .httpMethod(request.getHttpMethod())
                .details(Map.of())
                .timeStamp(LocalDateTime.now());
    }
}
