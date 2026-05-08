--liquibase formatted sql

--changeset a.slelin:005-create-indexes-task context:!test

CREATE INDEX idx_task_project_id ON task (project_id);
CREATE INDEX idx_task_project_id_and_title ON task (project_id, title);

--rollback DROP INDEX IF EXISTS idx_task_project_id;
--rollback DROP INDEX IF EXISTS idx_task_project_id_and_title;