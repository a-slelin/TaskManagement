--liquibase formatted sql

--changeset a.slelin:011-create-indexes-refresh-token context:!test

CREATE INDEX idx_refresh_token_user ON refresh_token (user_id);
CREATE INDEX idx_refresh_token_token ON refresh_token (token);

--rollback DROP INDEX IF EXISTS idx_refresh_token_user;
--rollback DROP INDEX IF EXISTS idx_refresh_token_token;