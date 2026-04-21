package a.slelin.work.task.management.core.service;

import a.slelin.work.task.management.core.dto.ReadDto;
import a.slelin.work.task.management.core.dto.SheetDto;
import a.slelin.work.task.management.core.dto.WriteDto;
import a.slelin.work.task.management.core.util.filter.FilterChain;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;

@SuppressWarnings("unused")
public interface CrudService<ID extends Serializable, RD extends ReadDto, WD extends WriteDto> {

    SheetDto<RD> getAll(@NotNull @Valid Pageable pageable);

    SheetDto<RD> search(@NotNull @Valid Pageable pageable, @NotNull @Valid FilterChain filters);

    RD getById(@NotNull ID id);

    RD update(@NotNull ID id, @NotNull @Valid WD dto);

    RD patch(@NotNull ID id, @NotNull @Valid WD dto);

    RD create(@NotNull @Valid WD dto);

    void delete(@NotNull ID id);
}
