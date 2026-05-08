package a.slelin.work.task.management.auth.util;

import a.slelin.work.task.management.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@AllArgsConstructor
public enum SystemRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    @Getter
    private final String dbName;

    public static boolean isSystemRole(Role role) {
        if (role == null) {
            return false;
        }

        String roleName = role.getName();

        for (SystemRole systemRole : SystemRole.values()) {
            if (systemRole.dbName.equalsIgnoreCase(roleName)) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("unused")
    public static boolean hasSystemRole(Collection<Role> roles) {
        if (roles == null) {
            return false;
        }

        for (Role role : roles) {
            if (isSystemRole(role)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAdminRole(Role role) {
        if (role == null) {
            return false;
        }

        String roleName = role.getName();

        return ADMIN.dbName.equalsIgnoreCase(roleName);
    }

    public static boolean hasAdminRole(Collection<Role> roles) {
        if (roles == null) {
            return false;
        }

        for (Role role : roles) {
            if (isAdminRole(role)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isUserRole(Role role) {
        if (role == null) {
            return false;
        }

        String roleName = role.getName();

        return USER.dbName.equalsIgnoreCase(roleName);
    }

    @SuppressWarnings("unused")
    public static boolean hasUserRole(Collection<Role> roles) {
        if (roles == null) {
            return false;
        }

        for (Role role : roles) {
            if (isUserRole(role)) {
                return true;
            }
        }

        return false;
    }
}
