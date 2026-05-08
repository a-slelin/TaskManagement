package a.slelin.work.task.management.core.dto.api;

import a.slelin.work.task.management.core.dto.WriteDto;
import lombok.Builder;
import lombok.NonNull;

import java.util.Objects;

@Builder
@SuppressWarnings("unused")
public record ProjectWD(String name,
                        String description) implements WriteDto {

    public ProjectWD(String name) {
        this(name, null);
    }

    @NonNull
    @Override
    public String toString() {
        return "ProjectWD: [name = %s, description = %s]"
                .formatted(name, description);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProjectWD project = (ProjectWD) o;
        return Objects.equals(name, project.name) &&
                Objects.equals(description, project.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}
