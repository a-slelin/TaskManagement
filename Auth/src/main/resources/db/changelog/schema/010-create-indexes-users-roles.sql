--liquibase formatted sql

--changeset a.slelin:010-create-indexes-users-roles context:!test

CREATE INDEX idx_users_roles_id ON users_roles (user_id, role_id);

--rollback DROP INDEX IF EXISTS idx_users_roles_id;