package a.slelin.work.task.management.core.dto.api;

import a.slelin.work.task.management.core.dto.ReadDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NonNull;

import java.util.Objects;

@Builder
public record ProjectRD(@NotNull @Min(1) Long id,
                        String name,
                        String description) implements ReadDto {

    @NonNull
    @Override
    public String toString() {
        return "ProjectRD: [id = %d, name = %s, description = %s]"
                .formatted(id, name, description);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProjectRD project = (ProjectRD) o;
        return Objects.equals(id, project.id) &&
                Objects.equals(name, project.name) &&
                Objects.equals(description, project.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }
}
