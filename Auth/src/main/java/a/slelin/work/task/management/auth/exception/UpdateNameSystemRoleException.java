package a.slelin.work.task.management.auth.exception;

import a.slelin.work.task.management.auth.entity.Role;
import a.slelin.work.task.management.auth.util.SystemRole;

public class UpdateNameSystemRoleException extends ModifySystemRoleException {

    public UpdateNameSystemRoleException(String role) {
        super(role, "An attempt was made to update name system role : " + role + ".");
    }

    public static void checkAndThrow(Role role) {
        if (SystemRole.isSystemRole(role)) {
            throw new UpdateNameSystemRoleException(role.getName());
        }
    }
}
