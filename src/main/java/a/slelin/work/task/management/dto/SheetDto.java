package a.slelin.work.task.management.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Builder
public record SheetDto<D extends ReadDto>(@NotNull @Valid List<D> content,
                                          @NotNull @Valid PageDto page) implements ReadDto {

    public static <E, D extends ReadDto> SheetDto<D> of(Page<E> page, Function<E, D> mapper) {
        if (page == null || mapper == null) {
            throw new IllegalArgumentException("Page and mapper must not be null.");
        }

        List<D> content = page.stream().map(mapper).toList();
        PageDto pageDto = PageDto.of(page);

        return new SheetDto<>(content, pageDto);
    }
}
