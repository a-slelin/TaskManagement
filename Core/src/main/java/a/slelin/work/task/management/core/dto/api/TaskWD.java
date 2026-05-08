package a.slelin.work.task.management.core.dto.api;

import a.slelin.work.task.management.core.dto.WriteDto;
import lombok.Builder;
import lombok.NonNull;

import java.util.Objects;

@Builder
@SuppressWarnings("unused")
public record TaskWD(String title,
                     String status,
                     String description) implements WriteDto {

    public TaskWD(String title) {
        this(title, null);
    }

    public TaskWD(String title, String status) {
        this(title, status, null);
    }

    @NonNull
    @Override
    public String toString() {
        return "TaskWD: [title = %s, status = %s, description = %s]"
                .formatted(title, status, description);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaskWD task = (TaskWD) o;
        return Objects.equals(title, task.title) &&
                Objects.equals(status, task.status) &&
                Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, status, description);
    }
}
