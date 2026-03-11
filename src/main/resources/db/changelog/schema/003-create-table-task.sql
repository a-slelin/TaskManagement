--liquibase formatted sql

--changeset a.slelin:003-create-table-task

DROP TABLE IF EXISTS task CASCADE;

CREATE TABLE task
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    status      VARCHAR(11)  NOT NULL,
    project_id  BIGINT       NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_task_to_project FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE,
    CONSTRAINT uk_task_title_project UNIQUE (title, project_id),
    CHECK (status IN ('begin', 'end', 'in_progress', 'canceled', 'on_hold'))
);

--rollback DROP TABLE IF EXISTS task CASCADE