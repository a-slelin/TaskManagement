-- Очищаем таблицы;
DELETE
FROM task;
DELETE
FROM project;
DELETE
FROM users;

-- Создаём 3х тестовых пользователей;
INSERT INTO users (id, username, password, gender, phone, email)
VALUES ('35e9edb1-4cc7-4278-9677-c581857c1998', 'alex_petrov', 'password', 'male',
        '+79051234567', 'alex.petrov@google.com'),
       ('bb9410b9-3b37-4eee-ab8c-1e221b7d001f', 'ekaterina_smirnova', 'password', 'female',
        '+79054567890', 'katya.s@example.com'),
       ('8925d0c8-eed8-46b7-a9b5-90d518500c8a', 'pavel_ivanov', 'password', 'male',
        '+79055678901', 'pavel.ivanov@example.com');

-- Создаём 3 проекта каждому пользователю;
INSERT INTO project (name, description, owner_id)
VALUES ('Интернет-магазин "Электроника"',
        'Разработка интернет-магазина по продаже электроники с интеграцией 1С и CRM',
        '35e9edb1-4cc7-4278-9677-c581857c1998'),
       ('Личный блог на React + Node.js',
        'Персональный блог с админкой и комментариями',
        'bb9410b9-3b37-4eee-ab8c-1e221b7d001f'),
       ('Корпоративный портал',
        'Внутренний портал для сотрудников с документами, задачами и новостями',
        '8925d0c8-eed8-46b7-a9b5-90d518500c8a');

-- Добавляем задачи для первого проекта;
INSERT INTO task (title, description, status, project_id)
VALUES ('Настроить структуру БД',
        'Спроектировать таблицы: товары, категории, заказы, пользователи',
        'end',
        (SELECT id FROM project WHERE name = 'Интернет-магазин "Электроника"')),

       ('Разработать API каталога',
        'REST API для получения товаров, фильтрации и поиска',
        'in_progress',
        (SELECT id FROM project WHERE name = 'Интернет-магазин "Электроника"')),

       ('Интеграция с платежной системой',
        'Подключить ЮKassa или Stripe для приема платежей',
        'begin',
        (SELECT id FROM project WHERE name = 'Интернет-магазин "Электроника"')),

       ('Верстка главной страницы',
        'Адаптивная верстка главной страницы каталога',
        'canceled',
        (SELECT id FROM project WHERE name = 'Интернет-магазин "Электроника"')),

       ('Написать тесты для корзины',
        'Юнит-тесты и интеграционные тесты для функционала корзины',
        'on_hold',
        (SELECT id FROM project WHERE name = 'Интернет-магазин "Электроника"'));

-- Добавляем задачи для второго проекта;
INSERT INTO task (title, description, status, project_id)
VALUES ('Настроить бэкенд на Node.js',
        'Express, MongoDB, JWT авторизация',
        'end',
        (SELECT id FROM project WHERE name = 'Личный блог на React + Node.js')),

       ('Сделать админку',
        'CRUD для постов, загрузка изображений',
        'in_progress',
        (SELECT id FROM project WHERE name = 'Личный блог на React + Node.js')),

       ('Написать фронтенд на React',
        'Главная страница, страница поста, комментарии',
        'begin',
        (SELECT id FROM project WHERE name = 'Личный блог на React + Node.js')),

       ('Добавить комментарии',
        'Комментарии с возможностью ответов и модерацией',
        'on_hold',
        (SELECT id FROM project WHERE name = 'Личный блог на React + Node.js'));

-- Добавляем задачи для третьего проекта;
INSERT INTO task (title, description, status, project_id)
VALUES ('Разработать систему авторизации',
        'JWT-аутентификация, роли: админ, сотрудник, HR',
        'end',
        (SELECT id FROM project WHERE name = 'Корпоративный портал')),

       ('Сделать ленту новостей',
        'Лента с постами, лайками и комментариями',
        'in_progress',
        (SELECT id FROM project WHERE name = 'Корпоративный портал')),

       ('Модуль документооборота',
        'Загрузка, хранение и согласование документов',
        'begin',
        (SELECT id FROM project WHERE name = 'Корпоративный портал'));
