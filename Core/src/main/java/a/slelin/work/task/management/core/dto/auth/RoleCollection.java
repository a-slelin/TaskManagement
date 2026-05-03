package a.slelin.work.task.management.core.dto.auth;

import a.slelin.work.task.management.core.dto.WriteDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record RoleCollection(@NotNull List<@NotBlank String> roles) implements WriteDto {

    public static RoleCollection of(String... roles) {
        return transform(new RoleCollection(List.of(roles)));
    }

    public static RoleCollection transform(RoleCollection roles) {
        List<String> trRoles = new ArrayList<>();

        for (String role : roles.roles) {
            String trRole = role.toUpperCase().trim();

            if (!trRole.startsWith("ROLE_")) {
                trRole = "ROLE_" + trRole;
            }

            trRoles.add(trRole);
        }

        return new RoleCollection(trRoles);
    }

    @NonNull
    @Override
    public String toString() {
        return "RoleCollection: [roles = " + String.join(", ", roles) + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RoleCollection that = (RoleCollection) o;
        return Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(roles);
    }
}
