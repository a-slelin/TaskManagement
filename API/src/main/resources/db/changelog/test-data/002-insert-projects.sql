--liquibase formatted sql

--changeset a.slelin:002-insert-projects

DELETE
FROM project;

INSERT INTO project (name, description, owner_id)
VALUES ('Интернет-магазин "Электроника"',
        'Разработка интернет-магазина по продаже электроники с интеграцией 1С и CRM',
        (SELECT id FROM users WHERE username = 'alex_petrov')),
       ('Мобильное приложение для фитнеса',
        'Разработка мобильного приложения для отслеживания тренировок и питания',
        (SELECT id FROM users WHERE username = 'alex_petrov')),

       ('Личный блог на React + Node.js',
        'Персональный блог с админкой и комментариями',
        (SELECT id FROM users WHERE username = 'ekaterina_smirnova')),
       ('CRM для малого бизнеса',
        'Система управления взаимоотношениями с клиентами для небольших компаний',
        (SELECT id FROM users WHERE username = 'ekaterina_smirnova')),

       ('Корпоративный портал',
        'Внутренний портал для сотрудников с документами, задачами и новостями',
        (SELECT id FROM users WHERE username = 'pavel_ivanov')),
       ('Система управления задачами',
        'Внутренний таск-трекер для команды разработки',
        (SELECT id FROM users WHERE username = 'pavel_ivanov'));

--rollback DELETE FROM projects;