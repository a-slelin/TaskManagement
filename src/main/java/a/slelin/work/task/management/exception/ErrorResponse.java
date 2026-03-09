package a.slelin.work.task.management.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Builder
public record ErrorResponse(@NotBlank String path,
                            @NotNull HttpMethod httpMethod,
                            @NotNull HttpStatus httpStatus,
                            String debugMessage,
                            @NotNull String message,
                            @NotNull String exception,
                            String causeException,
                            Map<String, Object> details,
                            @JsonFormat(pattern = TIME_STAMP_PATTERN)
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
                .path(request.getRequest().getRequestURI())
                .httpMethod(request.getHttpMethod())
                .details(Map.of())
                .timeStamp(LocalDateTime.now());
    }
}
