--liquibase formatted sql

--changeset a.slelin:008-create-indexes-roles context:!test

CREATE INDEX idx_roles_name ON roles (name);

--rollback DROP INDEX IF EXISTS idx_roles_name;