--liquibase formatted sql

--changeset a.slelin:006-create-table-refresh-token

DROP TABLE IF EXISTS refresh_token CASCADE;

CREATE TABLE refresh_token
(
    id          UUID                  DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id     UUID REFERENCES users (id) ON DELETE CASCADE,
    token       VARCHAR(255) NOT NULL UNIQUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expiry_date TIMESTAMP    NOT NULL
);

--rollback DROP TABLE IF EXISTS refresh_token CASCADE