package a.slelin.work.task.management.auth.exception;

import a.slelin.work.task.management.auth.entity.Role;

public class DeleteSystemRoleException extends ModifySystemRoleException {

    public DeleteSystemRoleException(String role) {
        super(role, "An attempt was made to delete system role : " + role + ".");
    }

    public static void checkAndThrow(Role role) {
        if (isSystemRole(role)) {
            throw new DeleteSystemRoleException(role.getName());
        }
    }
}
