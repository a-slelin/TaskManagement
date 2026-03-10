package a.slelin.work.task.management.util.filter;

public class FilterParseException extends RuntimeException {

    public FilterParseException(String message) {
        super(message);
    }

    @SuppressWarnings("unused")
    public FilterParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
