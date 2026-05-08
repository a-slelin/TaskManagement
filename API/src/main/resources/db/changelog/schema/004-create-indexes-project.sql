--liquibase formatted sql

--changeset a.slelin:004-create-indexes-project context:!test

CREATE INDEX idx_project_user_id ON project (user_id);
CREATE INDEX idx_project_user_id_and_name ON project (user_id, name);

--rollback DROP INDEX IF EXISTS idx_project_user_id;
--rollback DROP INDEX IF EXISTS idx_project_user_id_and_name;