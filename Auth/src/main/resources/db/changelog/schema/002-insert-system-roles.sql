-- noinspection SqlWithoutWhereForFile

--liquibase formatted sql

--changeset a.slelin:002-system-roles

DELETE
FROM roles;

INSERT INTO roles (name, description)
VALUES ('ROLE_USER', 'A standard role to designate a regular customer. ' ||
                     'Allows you to log in, work with your projects and tasks.'),
       ('ROLE_ADMIN', 'The administrator role allows you to work' ||
                      ' with a system with a large number of features,' ||
                      ' such as working with user roles and security in general.');

--rollback DELETE FROM roles;