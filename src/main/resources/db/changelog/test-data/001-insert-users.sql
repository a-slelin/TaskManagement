--liquibase formatted sql

--changeset a.slelin:001-insert-users

DELETE
FROM users;

INSERT INTO users (username, password, gender, phone, email)
VALUES ('alex_petrov', '$2a$10$u9koaSYf1BfoPPYOiPJHS.gW78bKHJ0QcI1GkrS38YjuQbIX5zBs2',
        'male', '+79051234567', 'alex.petrov@google.com'),
       ('ekaterina_smirnova', '$2a$10$Y5ob5.sZWX/F0jetEfn3reNTxhr9mH2dFTDv3DEGNQdcsbD7Vt6na',
        'female', '+79054567890', 'katya.s@mail.ru'),
       ('pavel_ivanov', '$2a$10$rKW8fXNyEhckAuhFcnCqh.tlwvZ1K08UiMAL.8H5ypiWD2MWK3ISW',
        'male', '+79055678901', 'pavel.ivanov@yandex.ru');

--rollback DELETE FROM users;