package a.slelin.work.task.management.core.dto.auth;

import a.slelin.work.task.management.core.dto.WriteDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

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
}
