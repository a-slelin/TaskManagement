package a.slelin.work.task.management.core.dto.api;

import a.slelin.work.task.management.core.dto.ReadDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NonNull;

import java.util.Objects;

@Builder
public record TaskRD(@NotNull @Min(1) Long id,
                     String title,
                     String status,
                     String description) implements ReadDto {

    @NonNull
    @Override
    public String toString() {
        return "TaskRD: [id = %d, title = %s, status = %s, description = %s]"
                .formatted(id, title, status, description);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaskRD task = (TaskRD) o;
        return Objects.equals(id, task.id) &&
                Objects.equals(title, task.title) &&
                Objects.equals(status, task.status) &&
                Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, status, description);
    }
}
