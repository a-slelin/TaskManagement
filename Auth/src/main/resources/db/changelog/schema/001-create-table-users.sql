--liquibase formatted sql

--changeset a.slelin:001-create-table-users

DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users
(
    id         UUID                  DEFAULT gen_random_uuid() PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    gender     VARCHAR(9)   NOT NULL DEFAULT 'undefined',
    phone      VARCHAR(15) UNIQUE,
    email      VARCHAR(50) UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (gender IN ('male', 'female', 'undefined'))
);

--rollback DROP TABLE IF EXISTS users CASCADE