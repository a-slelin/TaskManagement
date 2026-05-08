package a.slelin.work.task.management.core.dto.auth;

import a.slelin.work.task.management.core.dto.ReadDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;

import java.util.Objects;

@Builder
public record RoleRD(@NonNull Long id,
                     @NotBlank String name,
                     String description) implements ReadDto {

    @NonNull
    @Override
    public String toString() {
        return "RoleRD: [id = %d, name = %s, description = %s]"
                .formatted(id, name, description);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RoleRD role = (RoleRD) o;
        return Objects.equals(id, role.id) &&
                Objects.equals(name, role.name) &&
                Objects.equals(description, role.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }
}
