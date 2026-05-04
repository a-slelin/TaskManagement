-- noinspection SqlWithoutWhereForFile

--liquibase formatted sql

--changeset a.slelin:002-insert-another-admin context:!prod

INSERT INTO users (id, username, password, gender)
VALUES ('5aeb998d-c8c5-4d42-85c5-b472498e7c15', 'admin2',
        '$2a$10$rKW8fXNyEhckAuhFcnCqh.tlwvZ1K08UiMAL.8H5ypiWD2MWK3ISW', 'undefined');

INSERT INTO users_roles (user_id, role_id)
VALUES ('5aeb998d-c8c5-4d42-85c5-b472498e7c15',
        (SELECT id
         FROM roles
         WHERE name = 'ROLE_USER')),

       ('5aeb998d-c8c5-4d42-85c5-b472498e7c15',
        (SELECT id
         FROM roles
         WHERE name = 'ROLE_ADMIN'));

--rollback DELETE FROM users;