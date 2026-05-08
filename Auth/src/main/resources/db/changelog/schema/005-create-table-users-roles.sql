--liquibase formatted sql

--changeset a.slelin:005-create-table-users-roles

DROP TABLE IF EXISTS users_roles CASCADE;

CREATE TABLE users_roles
(
    user_id UUID REFERENCES users (id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

--rollback DROP TABLE IF EXISTS users_roles CASCADE