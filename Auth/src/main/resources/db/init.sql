-- Создаём базу данных;
CREATE
    DATABASE "AUTHDB";

-- Создаём пользователя, под которым будем подключаться к СУБД;
CREATE
    USER auth_user WITH PASSWORD 'password';

-- Выдаём этому пользователю все права на базу данных;
ALTER
    DATABASE "AUTHDB" OWNER TO auth_user;