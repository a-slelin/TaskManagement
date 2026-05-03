package a.slelin.work.task.management.auth.exception;

public class UserRoleRevokeException extends RuntimeException {

    private static final String MESSAGE = "Cannot revoke role \"ROLE_USER\" from user.";

    public UserRoleRevokeException() {
        super(MESSAGE);
    }

    @SuppressWarnings("unused")
    public UserRoleRevokeException(Throwable cause) {
        super(MESSAGE, cause);
    }
}

