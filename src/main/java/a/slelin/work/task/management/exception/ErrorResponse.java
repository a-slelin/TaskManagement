package a.slelin.work.task.management.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Builder
public record ErrorResponse(@NotBlank String path,
                            @NotNull @Min(400) @Max(526) Long httpStatus,
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
                .timeStamp(LocalDateTime.now());
    }
}
