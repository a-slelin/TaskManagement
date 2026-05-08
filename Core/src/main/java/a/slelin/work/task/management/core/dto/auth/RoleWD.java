package a.slelin.work.task.management.core.dto.auth;

import a.slelin.work.task.management.core.dto.WriteDto;
import lombok.Builder;
import lombok.NonNull;

import java.util.Objects;

@Builder
public record RoleWD(String name,
                     String description) implements WriteDto {

    @SuppressWarnings("unused")
    public RoleWD(String name) {
        this(name, null);
    }

    @NonNull
    @Override
    public String toString() {
        return "RoleWD: [name = %s, description = %s]"
                .formatted(name, description);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RoleWD role = (RoleWD) o;
        return Objects.equals(name, role.name) &&
                Objects.equals(description, role.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}
