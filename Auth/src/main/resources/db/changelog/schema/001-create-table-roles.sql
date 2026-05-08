--liquibase formatted sql

--changeset a.slelin:001-create-table-roles

DROP TABLE IF EXISTS roles CASCADE;

CREATE TABLE roles
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_role_name_prefix CHECK (name LIKE 'ROLE_%')
);

--rollback DROP TABLE IF EXISTS roles CASCADE