--liquibase formatted sql

--changeset a.slelin:005-create-indexes-users context:!test

CREATE INDEX idx_users_id ON users (id);
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_phone ON users (phone);
CREATE INDEX idx_users_email ON users (email);

--rollback DROP INDEX IF EXISTS idx_users_id;
--rollback DROP INDEX IF EXISTS idx_users_username;
--rollback DROP INDEX IF EXISTS idx_users_phone;
--rollback DROP INDEX IF EXISTS idx_users_email;