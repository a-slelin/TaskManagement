--liquibase formatted sql

--changeset a.slelin:006-create-indexes-project context:!test

CREATE INDEX idx_project_id ON project (id);
CREATE INDEX idx_project_owner_id ON project (owner_id);
CREATE INDEX idx_project_owner_id_and_name ON project (owner_id, name);

--rollback DROP INDEX IF EXISTS idx_project_id;
--rollback DROP INDEX IF EXISTS idx_project_owner_id;
--rollback DROP INDEX IF EXISTS idx_project_owner_id_and_name;