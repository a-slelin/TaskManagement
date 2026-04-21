--liquibase formatted sql

--changeset a.slelin:001-create-table-project

DROP TABLE IF EXISTS project CASCADE;

CREATE TABLE project
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    user_id     UUID         NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_project_name_owner UNIQUE (name, user_id)
);

--rollback DROP TABLE IF EXISTS project CASCADE