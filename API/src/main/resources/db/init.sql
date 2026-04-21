-- Создаём базу данных;
CREATE
DATABASE "TaskManagement";

-- Создаём пользователя, под которым будем подключаться к СУБД;
CREATE
USER admin WITH PASSWORD 'admin';

-- Выдаём этому пользователю все права на базу данных;
ALTER
DATABASE "TaskManagement" OWNER TO admin;