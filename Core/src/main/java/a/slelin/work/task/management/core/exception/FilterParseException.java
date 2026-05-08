package a.slelin.work.task.management.core.exception;

@SuppressWarnings("unused")
public class FilterParseException extends RuntimeException {

    public FilterParseException(String message) {
        super(message);
    }

    public FilterParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
