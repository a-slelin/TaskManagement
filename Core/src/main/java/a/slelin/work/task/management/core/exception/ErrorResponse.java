package a.slelin.work.task.management.core.exception;

import a.slelin.work.task.management.core.util.HttpMethodDeserializer;
import a.slelin.work.task.management.core.util.HttpMethodSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import static a.slelin.work.task.management.core.util.DateTimeUtil.UNIVERSE_DATETIME_FORMATTER;

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

    @NonNull
    @Override
    public String toString() {
        String result = "ErrorResponse: [path = %s, method = %s, status = %s, message = %s"
                .formatted(path, httpMethod, httpStatus, message);

        if (debugMessage != null) {
            result += ", debugMessage = " + debugMessage;
        }

        result += ", exception = %s".formatted(exception);

        if (causeException != null) {
            result += ", causeException = %s".formatted(causeException);
        }

        if (details != null) {
            result += ", details = %s".formatted(details.toString());
        }

        return "%s, timestamp = %s]".formatted(result, timeStamp.format(UNIVERSE_DATETIME_FORMATTER));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ErrorResponse that = (ErrorResponse) o;
        return Objects.equals(path, that.path) &&
                Objects.equals(message, that.message) &&
                Objects.equals(exception, that.exception) &&
                Objects.equals(debugMessage, that.debugMessage) &&
                Objects.equals(httpMethod, that.httpMethod) &&
                httpStatus == that.httpStatus &&
                Objects.equals(causeException, that.causeException) &&
                Objects.equals(timeStamp, that.timeStamp) &&
                Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, httpMethod, httpStatus, debugMessage,
                message, exception, causeException, details, timeStamp);
    }
}
