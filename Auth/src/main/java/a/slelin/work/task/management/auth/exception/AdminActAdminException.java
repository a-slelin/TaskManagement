package a.slelin.work.task.management.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class AdminActAdminException extends RuntimeException {

    private final Operation operation;

    @Getter
    @AllArgsConstructor
    public enum Operation {
        PATCH("patch", "Admin cannot update another admin."),
        DELETE("delete", "Admin cannot delete another admin."),
        REVOKE("revoke", "Admin cannot revoke any role from another admin."),
        TERMINATE_SESSION("terminate_session", "Admin cannot terminate sessions another admin.");

        private final String displayName;

        private final String exceptionMessage;
    }

    @SuppressWarnings("unused")
    public AdminActAdminException(Operation operation, Throwable cause) {
        this.operation = operation;
        super(operation.exceptionMessage, cause);
    }

    public AdminActAdminException(Operation operation) {
        this.operation = operation;
        super(operation.exceptionMessage);
    }
}
