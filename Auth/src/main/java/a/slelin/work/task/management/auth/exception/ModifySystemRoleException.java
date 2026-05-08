package a.slelin.work.task.management.auth.exception;

import a.slelin.work.task.management.auth.entity.Role;
import a.slelin.work.task.management.auth.util.SystemRole;
import a.slelin.work.task.management.core.exception.BusinessFault;
import lombok.Getter;

@Getter
public class ModifySystemRoleException extends BusinessFault {

    private final String role;

    @SuppressWarnings("unused")
    public ModifySystemRoleException(String role) {
        this(role, "An attempt was made to modify system role : " + role + ".");
    }

    public ModifySystemRoleException(String role, String message) {
        super(message);
        this.role = role;
    }

    @SuppressWarnings("unused")
    public static void checkAndThrow(Role role) {
        if (SystemRole.isSystemRole(role)) {
            throw new ModifySystemRoleException(role.getName());
        }
    }
}
