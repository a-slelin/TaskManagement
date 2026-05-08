-- noinspection SqlWithoutWhereForFile

--liquibase formatted sql

--changeset a.slelin:012-insert-admin

DELETE
FROM users;

INSERT INTO users (id, username, password, gender)
VALUES ('c152861c-9d46-4f27-a555-ebb33d7b20ff',
        'admin',
        '$2a$10$u9koaSYf1BfoPPYOiPJHS.gW78bKHJ0QcI1GkrS38YjuQbIX5zBs2',
        'undefined');

INSERT INTO users_roles (user_id, role_id)
VALUES ('c152861c-9d46-4f27-a555-ebb33d7b20ff',
        (SELECT id
         FROM roles
         WHERE name = 'ROLE_USER')),

       ('c152861c-9d46-4f27-a555-ebb33d7b20ff',
        (SELECT id
         FROM roles
         WHERE name = 'ROLE_ADMIN'));

--rollback DELETE FROM users;