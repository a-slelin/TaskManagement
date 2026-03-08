package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dto.ReadDto;
import a.slelin.work.task.management.dto.WriteDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

public interface Service<ID extends Serializable, RD extends ReadDto, WD extends WriteDto> {

    List<RD> getAll();

    RD getById(@NotNull ID id);

    RD update(@NotNull ID id, @NotNull @Valid WD dto);

    RD create(@NotNull @Valid WD dto);

    void delete(@NotNull ID id);
}
