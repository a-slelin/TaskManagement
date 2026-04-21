--liquibase formatted sql

--changeset a.slelin:002-create-table-project

DROP TABLE IF EXISTS project CASCADE;

CREATE TABLE project
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    owner_id    UUID         NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_project_to_user FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uk_project_name_owner UNIQUE (name, owner_id)
);

--rollback DROP TABLE IF EXISTS project CASCADE