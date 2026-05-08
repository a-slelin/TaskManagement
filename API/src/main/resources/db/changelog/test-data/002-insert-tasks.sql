-- noinspection SqlWithoutWhereForFile

--liquibase formatted sql

--changeset a.slelin:002-insert-tasks context:!prod

DELETE
FROM task;

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

INSERT INTO task (title, description, status, project_id)
VALUES ('Разработка дизайна экранов',
        'Создать макеты главного экрана, экрана тренировок и профиля в Figma',
        'begin',
        (SELECT id FROM project WHERE name = 'Мобильное приложение для фитнеса')),

       ('Настройка бэкенда',
        'Разработать API для хранения данных пользователей и тренировок',
        'in_progress',
        (SELECT id FROM project WHERE name = 'Мобильное приложение для фитнеса')),

       ('Интеграция с HealthKit',
        'Подключить синхронизацию данных о здоровье с Apple Health',
        'on_hold',
        (SELECT id FROM project WHERE name = 'Мобильное приложение для фитнеса'));

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

INSERT INTO task (title, description, status, project_id)
VALUES ('Проектирование БД',
        'Создать схему данных для клиентов, сделок и контактов',
        'end',
        (SELECT id FROM project WHERE name = 'CRM для малого бизнеса')),

       ('Разработка модуля клиентов',
        'CRUD для клиентов с возможностью добавления заметок',
        'in_progress',
        (SELECT id FROM project WHERE name = 'CRM для малого бизнеса')),

       ('Интеграция с email-рассылками',
        'Подключить SendGrid для отправки уведомлений клиентам',
        'begin',
        (SELECT id FROM project WHERE name = 'CRM для малого бизнеса'));

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

INSERT INTO task (title, description, status, project_id)
VALUES ('Создание модели задач',
        'Определить поля задачи: заголовок, описание, статус, исполнитель',
        'end',
        (SELECT id FROM project WHERE name = 'Система управления задачами')),

       ('Реализация доски задач',
        'Разработать интерфейс доски в стиле Kanban',
        'in_progress',
        (SELECT id FROM project WHERE name = 'Система управления задачами')),

       ('Настройка уведомлений',
        'Добавить email-уведомления при изменении статуса задачи',
        'begin',
        (SELECT id FROM project WHERE name = 'Система управления задачами'));

INSERT INTO task (title, description, status, project_id)
VALUES ('Спроектировать очередь событий',
        'Определить схему сообщений для уведомлений: email, push, sms. Интеграция с RabbitMQ.',
        'begin',
        (SELECT id FROM project WHERE name = 'Разработка микросервиса уведомлений')),

       ('Реализовать адаптер для Email',
        'Подключить SMTP-провайдера (SendGrid / AWS SES) с шаблонами писем',
        'in_progress',
        (SELECT id FROM project WHERE name = 'Разработка микросервиса уведомлений'));

INSERT INTO task (title, description, status, project_id)
VALUES ('Проектирование базы данных LMS',
        'Таблицы: пользователи, курсы, уроки, тесты, прогресс учеников',
        'end',
        (SELECT id FROM project WHERE name = 'Платформа для онлайн-обучения')),

       ('Разработка видеоплеера с субтитрами',
        'Встроить HTML5-плеер с поддержкой VTT субтитров и отслеживанием просмотра',
        'in_progress',
        (SELECT id FROM project WHERE name = 'Платформа для онлайн-обучения'));

INSERT INTO task (title, description, status, project_id)
VALUES ('Настройка NLP-модели для распознавания интентов',
        'Обучить модель классифицировать обращения клиентов (проблемы с интернетом, оплата, перезагрузка оборудования)',
        'in_progress',
        (SELECT id FROM project WHERE name = 'Чат-бот для техподдержки интернет-провайдера')),

       ('Интеграция с Telegram и Viber',
        'Подключить бота к мессенджерам, настроить webhook-уведомления',
        'begin',
        (SELECT id FROM project WHERE name = 'Чат-бот для техподдержки интернет-провайдера')),

       ('Создание базы знаний с часто задаваемыми вопросами',
        'Собрать и структурировать FAQ, написать скрипты ответов',
        'end',
        (SELECT id FROM project WHERE name = 'Чат-бот для техподдержки интернет-провайдера')),

       ('Логирование диалогов и аналитика',
        'Сохранять историю чатов, настроить дашборд для операторов',
        'on_hold',
        (SELECT id FROM project WHERE name = 'Чат-бот для техподдержки интернет-провайдера'));

INSERT INTO task (title, description, status, project_id)
VALUES ('Реализация аутентификации и ролей',
        'Добавить JWT-аутентификацию, разграничить доступ: админ, менеджер, пользователь',
        'end',
        (SELECT id FROM project WHERE name = 'CRM для управления задачами и клиентами (TaskFlow)')),

       ('Разработка доски задач (Kanban)',
        'Сделать интерфейс с колонками ToDo, In Progress, Done, перетаскивание карточек',
        'in_progress',
        (SELECT id FROM project WHERE name = 'CRM для управления задачами и клиентами (TaskFlow)')),

       ('Создание воронки продаж',
        'Этапы: новый контакт, переговоры, выставление счета, закрытие сделки',
        'begin',
        (SELECT id FROM project WHERE name = 'CRM для управления задачами и клиентами (TaskFlow)')),

       ('Настройка отчетов и дашбордов',
        'Графики по количеству задач, эффективности сотрудников, конверсии сделок',
        'on_hold',
        (SELECT id FROM project WHERE name = 'CRM для управления задачами и клиентами (TaskFlow)'));

--rollback DELETE FROM task;