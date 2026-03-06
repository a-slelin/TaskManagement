package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dto.Dto;
import a.slelin.work.task.management.dto.ReadDto;
import a.slelin.work.task.management.dto.WriteDto;

import java.io.Serializable;
import java.util.List;

public interface Service<ID extends Serializable, RD extends ReadDto, WD extends WriteDto> {

    List<RD> getAll();

    RD getById(ID id);

    RD update(ID id, WD dto);

    RD create(WD dto);

    void delete(ID id);
}
