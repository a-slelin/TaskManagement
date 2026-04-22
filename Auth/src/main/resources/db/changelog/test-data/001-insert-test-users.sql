-- noinspection SqlWithoutWhereForFile

--liquibase formatted sql

--changeset a.slelin:001-insert-test-users context:!prod

DELETE
FROM users;

INSERT INTO users (id, username, password, gender, phone, email)
VALUES ('ff80a205-67e1-4d22-b886-1be26e51ee9f', 'alex_petrov',
        '$2a$10$u9koaSYf1BfoPPYOiPJHS.gW78bKHJ0QcI1GkrS38YjuQbIX5zBs2',
        'male', '+79051234567', 'alex.petrov@google.com'),
       ('5a53277c-487f-4ef8-bd7e-c1256de14785', 'ekaterina_smirnova',
        '$2a$10$Y5ob5.sZWX/F0jetEfn3reNTxhr9mH2dFTDv3DEGNQdcsbD7Vt6na',
        'female', '+79054567890', 'katya.s@mail.ru'),
       ('5d427038-c6cd-4a23-90d3-c8dece778c44', 'pavel_ivanov',
        '$2a$10$rKW8fXNyEhckAuhFcnCqh.tlwvZ1K08UiMAL.8H5ypiWD2MWK3ISW',
        'male', '+79055678901', 'pavel.ivanov@yandex.ru');

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM (VALUES ('ff80a205-67e1-4d22-b886-1be26e51ee9f'::uuid),
             ('5a53277c-487f-4ef8-bd7e-c1256de14785'::uuid),
             ('5d427038-c6cd-4a23-90d3-c8dece778c44'::uuid)) AS u(id)
         CROSS JOIN (SELECT id FROM roles WHERE name = 'ROLE_USER') AS r;

--rollback DELETE FROM users;