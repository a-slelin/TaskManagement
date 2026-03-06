package a.slelin.work.task.management.service;

import a.slelin.work.task.management.dto.Dto;

import java.io.Serializable;
import java.util.List;

public interface Service<ID extends Serializable, D extends Dto> {

    List<D> getAll();

    D getById(ID id);

    D update(D dto);

    ID create(D dto);

    void delete(ID id);
}
