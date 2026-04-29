package a.slelin.work.task.management.auth.exception;

import a.slelin.work.task.management.auth.entity.Role;
import a.slelin.work.task.management.core.exception.BusinessFault;
import lombok.AllArgsConstructor;
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

    @Getter
    @AllArgsConstructor
    public enum UnmodifiableSystemRole {
        USER("ROLE_USER", "user"),
        ADMIN("ROLE_ADMIN", "admin");

        private final String fullName;

        private final String displayName;
    }

    public static boolean isSystemRole(Role role) {
        if (role == null) {
            return false;
        }

        for (UnmodifiableSystemRole systemRole : UnmodifiableSystemRole.values()) {
            if (role.getName().equals(systemRole.getFullName())) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("unused")
    public static void checkAndThrow(Role role) {
        if (isSystemRole(role)) {
            throw new ModifySystemRoleException(role.getName());
        }
    }
}
