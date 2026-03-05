-- Удаляем таблицы если они вдруг были в базе данных;
DROP TABLE IF EXISTS task CASCADE;
DROP TABLE IF EXISTS project CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Создаём таблицу пользователей;
CREATE TABLE users
(
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    gender   VARCHAR(9)   NOT NULL,
    phone    VARCHAR(15),
    email    VARCHAR(50),
    CHECK (gender IN ('male', 'female', 'undefined'))
);

-- Создаём таблицу проектов;
CREATE TABLE project
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    owner_id    UUID         NOT NULL,
    CONSTRAINT fk_project_to_user FOREIGN KEY (owner_id) REFERENCES users (id)
);

-- Создаём таблицу задач;
CREATE TABLE task
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    status      VARCHAR(11)  NOT NULL,
    project_id  BIGINT       NOT NULL,
    CONSTRAINT fk_task_to_project FOREIGN KEY (project_id) REFERENCES project (id),
    CHECK (status IN ('begin', 'end', 'in_progress', 'canceled', 'on_hold'))
);
