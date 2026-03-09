package a.slelin.work.task.management.exception;

@SuppressWarnings("unused")
public class BusinessFault extends RuntimeException {

    public BusinessFault() {
        super("Business fault has occurred");
    }

    public BusinessFault(Throwable cause) {
        super("Business fault has occurred", cause);
    }

    public BusinessFault(String message) {
        super(message);
    }

    public BusinessFault(String message, Throwable cause) {
        super(message, cause);
    }
}
