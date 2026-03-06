package a.slelin.work.task.management.entity;

import java.io.Serializable;

public interface Entity<ID extends Serializable> {

    ID getId();

    void setId(ID id);
}
