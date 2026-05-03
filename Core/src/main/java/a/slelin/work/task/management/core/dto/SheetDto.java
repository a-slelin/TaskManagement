package a.slelin.work.task.management.core.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Builder
@SuppressWarnings("unused")
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

    @NonNull
    @Override
    public String toString() {
        return "SheetDto: [hashcode = %d]".formatted(this.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SheetDto<?> sheetDto = (SheetDto<?>) o;
        return Objects.equals(page, sheetDto.page) &&
                Objects.equals(content, sheetDto.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, page);
    }
}
