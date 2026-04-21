-- Создаём базу данных;
CREATE
DATABASE "APIDB";

-- Создаём пользователя, под которым будем подключаться к СУБД;
CREATE
USER api_user WITH PASSWORD 'password';

-- Выдаём этому пользователю все права на базу данных;
ALTER
DATABASE "APIDB" OWNER TO api_user;