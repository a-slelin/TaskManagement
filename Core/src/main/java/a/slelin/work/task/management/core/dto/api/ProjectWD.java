package a.slelin.work.task.management.core.dto.api;

import a.slelin.work.task.management.core.dto.WriteDto;
import lombok.Builder;

@Builder
@SuppressWarnings("unused")
public record ProjectWD(String name,
                        String description) implements WriteDto {

    public ProjectWD(String name) {
        this(name, null);
    }
}
