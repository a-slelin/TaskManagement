package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dto.ReadDto;
import a.slelin.work.task.management.dto.SheetDto;
import a.slelin.work.task.management.dto.WriteDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;

public interface CrudService<ID extends Serializable, RD extends ReadDto, WD extends WriteDto> {

    SheetDto<RD> getAll(@NotNull @Valid Pageable pageable);

    RD getById(@NotNull ID id);

    RD update(@NotNull ID id, @NotNull @Valid WD dto);

    RD patch(@NotNull ID id, @NotNull @Valid WD dto);

    RD create(@NotNull @Valid WD dto);

    void delete(@NotNull ID id);
}
