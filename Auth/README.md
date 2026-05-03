# Документация по TaskManagementSystem::Auth

В данном файле находится необходимая информация 💎, советы 📎и руководство 📒 по использованию
**<span style="color: yellow;">Auth</span>**-модуля 🔒.

## Модель

![Модель](images/model.svg)

## Данные

Для взаимодействия с **<span style="color: yellow;">Auth</span>** 🔒 используются следующие структуры:

* **<span style="color: green;">User</span>** 🧒 - пользователь;
* **<span style="color: green;">Token</span>** 🔑 - токен обновления;
* **<span style="color: green;">Role</span>** 💡- роль.
* **<span style="color: green;">Login</span>** 👋 - запрос на логин;
* **<span style="color: green;">Jwt</span>** 🌈 - jwt-токены;
* **<span style="color: green;">RoleCollection</span>** 💾 - коллекция ролей;
* **<span style="color: green;">Page</span>** 📜 - страница;
* **<span style="color: green;">Filters</span>** 🔎 - фильтры;

### Как работать с пользователем?

**<span style="color: green;">User</span>** 🧒:

* **id** (String): идентификатор пользователя;
* **username** (String): имя пользователя в системе;
* **password** (String): пароль пользователя.
* **gender** (String): гендер пользователя (опционально).
* **phone** (String): телефон пользователя (опционально).
* **email** (String): электронная почта пользователя (опционально).

Пример:

```json5
{
  id: "60f07b06-75fd-4444-ba75-a7d26963c925",
  username: "alex_slelin",
  gender: "male",
  phone: "+78956748482",
  email: "a.slelin.work@mail.ru"
}
```

Возможные варианты для **gender**:

* **male** 🧑: мужчина.
* **female** 👧: женщина

### Как работать с токенами?

**<span style="color: green;">Token</span>** 🔑:

* **id** (String): идентификатор токена;
* **token** (String): токен обновления;
* **createdAt** (DateTime): дата создания;
* **expiryDate** (DateTime): дата истекания токена.

Пример:

```json5
{
  id: "d816a4f3-7f6e-4dd1-921f-4fe15a9dc942",
  token: "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiIxNTk1NDkyMC01Y2U3LTRhYTktYTNmMi0zODAyYjFmY2Q1N2QiLCJzdWIiOiJjMTUyODYxYy05ZDQ2LTRmMjctYTU1NS1lYmIzM2Q3YjIwZmYiLCJpYXQiOjE3Nzc2MzA4MzAsImV4cCI6MTc3NzcxNzIzMH0.7_RDeFr8Xq9a0YbeE3aqIhAz3_WaVYkEA3k5dIhxxXAhwK6XGKMGDhEzEuV7YyYt7DSxcvF3LEYs0BTYwI2qlw",
  createAt: "01.05.2026 13:20:30",
  expiryDate: "02.05.2026 13:20:30"
}
```

### Как работать с ролями?

**<span style="color: green;">Role</span>** 💡:

* **id** (Long): идентификатор роли;
* **name** (String): название роли;
* **description** (String): описание роли (опционально).

Пример:

```json5
{
  id: 1,
  name: "ROLE_ADMIN",
  description: "The administrator role allows you to work with a system with a large number of features, such as working with user roles and security in general."
}
```

### Как работать с логином?

**<span style="color: green;">Login</span>** 👋:

* **factor** (String): один из факторов пользователя: имя, телефон или электронная почта;
* **password** (String): пароль пользователя.

Пример:

```json5
{
  factor: "alex_slelin",
  password: "my_hard_password"
}
```

### Как работать с Jwt?

**<span style="color: green;">Jwt</span>** 🌈:

* **accessToken** (String): токен доступа;
* **refreshToken** (String): токен обновления.

Пример:

```json5
{
  accessToken: "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiJmMGIxZWJmYy1lMDVkLTQyNDYtODFlMy01Y2ZiZjE0YTczZGEiLCJzdWIiOiJjMTUyODYxYy05ZDQ2LTRmMjctYTU1NS1lYmIzM2Q3YjIwZmYiLCJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sImlhdCI6MTc3NzY0NDgyNCwiZXhwIjoxNzc3NjQ1NzI0fQ.E98vbikMK_tGhA1u-cJgu3EA0_SpY8pqpMd3e6O71lGM_4l5M1q_MfKRFGy0U6KZdqUpZrb45-gxOj9YYzFXsw",
  refreshToken: "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiJhMGFiOTkzMi03OThlLTQ3MzMtYmU0YS00NjZmODAwNjYwNjkiLCJzdWIiOiJjMTUyODYxYy05ZDQ2LTRmMjctYTU1NS1lYmIzM2Q3YjIwZmYiLCJpYXQiOjE3Nzc2NDQ4MjQsImV4cCI6MTc3NzczMTIyNH0.yBdHxAhBqKeL22HFgBjgd9AL3JaQ4eesNYRVbKGBMGM1OMjpI5p4ysN00uBZQ5ArHLaZ4aMj4EOEUcH9ipxSxg"
}
```

### Как работать с коллекциями ролей?

**<span style="color: green;">RoleCollection</span>** 💾:

* **roles** (List(String)): список названий ролей.

Пример:

```json5
{
  roles: [
    "ROLE_USER",
    "ROLE_OPERATOR",
    "ROLE_ADMIN"
  ]
}
```

### Как работать со страницей?

**<span style="color: green;">Page</span>** 📜:

* **number** (Integer): текущий номер;
* **size** (Integer): количество элементов на текущей странице;
* **sorts** (List(Sort)): используемые сортировки;
* **totalElements** (Long): количество всех элементов;
* **totalPages** (Integer): количество всех страниц;
* **first** (Boolean): первая ли страница;
* **last** (Boolean): последняя ли страница;
* **empty** (Boolean): пустая ли страница.

**Sort**:

* **property** (String): свойство для сортировки;
* **direction** (String): направление сортировки - ASC или DESC.

Пример:

```json5
{
  number: 5,
  size: 10,
  sorts: [
    {
      property: "name",
      direction: "ASC"
    },
    {
      property: "id",
      direction: "DESC"
    }
  ],
  totalElements: 100,
  totalPages: 10,
  first: false,
  last: false,
  empty: false
}
```

### Как работать с фильтрами?

**<span style="color: green;">Filters</span>** 🔎:

* **filters** (List(Filter)): фильтры.

**Filter**:

* **field** (String): поле для фильтрации;
* **operation** (String): операция фильтрации;
* **value** (Object): первое значение фильтра (опционально);
* **value2** (Object): второе значение фильтра (опционально).

Пример:

```json5
{
  filters: [
    {
      field: "property1",
      operation: "is true"
    },
    {
      field: "property2",
      operation: "between",
      value: 2000,
      value2: 2025
    },
    {
      field: "property3",
      operation: "like",
      value: "pro"
    }
  ]
}
```

Возможные варианты **operation** 💡:

* **equals** - равно;
* **not equals** - не равно;
* **greater** - больше;
* **greater or equals** - больше или равно;
* **less** - меньше;
* **less or equals** - меньше или равно;
* **like** - содержит;
* **not like** - не содержит;
* **starts with** - начинается с;
* **not starts with** - не начинается с;
* **ends with** - заканчивается;
* **not ends with** - не заканчивается;
* **is null** - не определено;
* **is not null** - определено;
* **is empty** - пусто;
* **is not empty** - не пусто;
* **is true** - истина;
* **is false** - ложь;
* **in** - в списке;
* **not in** - не в списке;
* **between** - между;
* **not between** - не между;
* **before** - до;
* **after** - после.

## REST

Для взаимодействия с **<span style="color: yellow;">Auth</span>** 🔒 можно использовать следующие медиа-типы:

* **<span style="color: green;">application/json</span>**;
* **<span style="color: purple;">application/xml</span>**;
* **<span style="color: red;">application/yaml</span>**.

### API аутентификации

```http request
GET auth/refresh
```

* <span style="color: yellow;">Операция</span> 🚀: выдаёт новый токен доступа в текущей сессии.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">409</span> CONFLICT**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
GET auth/logout
```

* <span style="color: yellow;">Операция</span> 🚀: завершает текущую сессию.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">409</span> CONFLICT**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">204</span> NO CONTENT**.

```http request
GET auth/logout/all
```

* <span style="color: yellow;">Операция</span> 🚀: завершает все сессии пользователя.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">409</span> CONFLICT**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">204</span> NO CONTENT**.

```http request
POST auth/login
```

* <span style="color: yellow;">Операция</span> 🚀: логинит пользователя и возвращает jwt-токены.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: логин.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: не ожидается.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">400</span> BAD REQUEST**,
  **<span style="color: orange;">409</span> CONFLICT**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
POST auth/register
```

* <span style="color: yellow;">Операция</span> 🚀: создаёт новый пользователя и возвращает jwt-токены.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: пользователь.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: не ожидается.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">400</span> BAD REQUEST**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

### API для обычного пользователя

```http request
GET api/user
```

* <span style="color: yellow;">Операция</span> 🚀: возвращает пользователя.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">401</span> UNAUTHORIZED**,
  **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
PATCH api/user
```

* <span style="color: yellow;">Операция</span> 🚀: частично обновляет пользователя и возвращает его.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: пользователь.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">400</span> BAD REQUEST**,
  **<span style="color: orange;">401</span> UNAUTHORIZED**, **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
DELETE api/user
```

* <span style="color: yellow;">Операция</span> 🚀: удаляет пользователя.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">400</span> BAD REQUEST**,
  **<span style="color: orange;">401</span> UNAUTHORIZED**, **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">204</span> NO CONTENT**.

### API для администратора системы

#### Роли

```http request
GET api/admin/roles
```

* <span style="color: yellow;">Операция</span> 🚀: возвращает список ролей и информацию о странице.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: **page** (0) - номер страницы, **size** (10) - размер
  страницы, **sort** (name) - сортировка.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">401</span> UNAUTHORIZED**,
  **<span style="color: orange;">403</span> FORBIDDEN**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
GET api/admin/roles/{id}
```

* <span style="color: yellow;">Операция</span> 🚀: возвращает роль по идентификатору.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">401</span> UNAUTHORIZED**,
  **<span style="color: orange;">403</span> FORBIDDEN**, **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
GET api/admin/roles/name/{name}
```

* <span style="color: yellow;">Операция</span> 🚀: возвращает роль по имени.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">401</span> UNAUTHORIZED**,
  **<span style="color: orange;">403</span> FORBIDDEN**, **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
POST api/admin/roles/search
```

* <span style="color: yellow;">Операция</span> 🚀: возвращает список ролей по фильтру; также возвращает информацию о
  странице.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: **page** (0) - номер страницы, **size** (10) - размер
  страницы, **sort** (name) - сортировка.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: фильтр.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">400</span> BAD REQUEST**,
  **<span style="color: orange;">401</span> UNAUTHORIZED**, **<span style="color: orange;">403</span> FORBIDDEN**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
POST api/admin/roles
```

* <span style="color: yellow;">Операция</span> 🚀: создаёт новую роль и возвращает её; также возвращает ссылку на эту
  роль.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: роль.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">400</span> BAD REQUEST**,
  **<span style="color: orange;">401</span> UNAUTHORIZED**, **<span style="color: orange;">403</span> FORBIDDEN**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">201</span> CREATED**.

```http request
PUT api/admin/roles/{id}
```

* <span style="color: yellow;">Операция</span> 🚀: полностью заменяет роль по идентификатору.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: роль.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">400</span> BAD REQUEST**,
  **<span style="color: orange;">401</span> UNAUTHORIZED**, **<span style="color: orange;">403</span> FORBIDDEN**,
  **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
PATCH api/admin/roles/{id}
```

* <span style="color: yellow;">Операция</span> 🚀: частично обновляет роль по идентификатору.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: роль.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">400</span> BAD REQUEST**,
  **<span style="color: orange;">401</span> UNAUTHORIZED**, **<span style="color: orange;">403</span> FORBIDDEN**,
  **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
DELETE api/admin/roles/{id}
```

* <span style="color: yellow;">Операция</span> 🚀: удаляет роль по идентификатору.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">401</span> UNAUTHORIZED**,
  **<span style="color: orange;">403</span> FORBIDDEN**, **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">204</span> NO CONTENT**.

#### Токены

```http request
GET api/admin/tokens
```

* <span style="color: yellow;">Операция</span> 🚀: возвращает список токенов и информацию о странице.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: **page** (0) - номер страницы, **size** (10) - размер
  страницы, **sort** (token) - сортировка.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">401</span> UNAUTHORIZED**,
  **<span style="color: orange;">403</span> FORBIDDEN**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
GET api/admin/tokens/{id}
```

* <span style="color: yellow;">Операция</span> 🚀: возвращает токен по идентификатору.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">401</span> UNAUTHORIZED**,
  **<span style="color: orange;">403</span> FORBIDDEN**, **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
GET api/admin/tokens/user/{user_id}
```

* <span style="color: yellow;">Операция</span> 🚀: возвращает все токены пользователя по идентификатору.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">401</span> UNAUTHORIZED**,
  **<span style="color: orange;">403</span> FORBIDDEN**, **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
GET api/admin/tokens/search
```

* <span style="color: yellow;">Операция</span> 🚀: возвращает список токенов по фильтру; также возвращает информацию о
  странице.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: **page** (0) - номер страницы, **size** (10) - размер
  страницы, **sort** (token) - сортировка.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: фильтр.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">400</span> BAD REQUEST**,
  **<span style="color: orange;">401</span> UNAUTHORIZED**, **<span style="color: orange;">403</span> FORBIDDEN**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
DELETE api/admin/tokens
```

* <span style="color: yellow;">Операция</span> 🚀: удаляет все токены.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">401</span> UNAUTHORIZED**,
  **<span style="color: orange;">403</span> FORBIDDEN**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">204</span> NO CONTENT**.

```http request
DELETE api/admin/tokens/{id}
```

* <span style="color: yellow;">Операция</span> 🚀: удаляет токен по идентификатору.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">401</span> UNAUTHORIZED**,
  **<span style="color: orange;">403</span> FORBIDDEN**, **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">204</span> NO CONTENT**.

```http request
DELETE api/admin/tokens/user/{user_id}
```

* <span style="color: yellow;">Операция</span> 🚀: удаляет все токен пользователя по идентификатору.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">401</span> UNAUTHORIZED**,
  **<span style="color: orange;">403</span> FORBIDDEN**, **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">204</span> NO CONTENT**.

#### Пользователи

```http request
GET api/admin/users
```

* <span style="color: yellow;">Операция</span> 🚀: возвращает список пользователей и информацию о странице.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: **page** (0) - номер страницы, **size** (10) - размер
  страницы, **sort** (username) - сортировка.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">401</span> UNAUTHORIZED**,
  **<span style="color: orange;">403</span> FORBIDDEN**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
GET api/admin/users/{id}
```

* <span style="color: yellow;">Операция</span> 🚀: возвращает пользователя по идентификатору.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">401</span> UNAUTHORIZED**,
  **<span style="color: orange;">403</span> FORBIDDEN**, **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
GET api/admin/users/factor/{factor}
```

* <span style="color: yellow;">Операция</span> 🚀: возвращает пользователя по фактору (имя, телефон или почта).
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">401</span> UNAUTHORIZED**,
  **<span style="color: orange;">403</span> FORBIDDEN**, **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
POST api/admin/users/search
```

* <span style="color: yellow;">Операция</span> 🚀: возвращает список пользователей по фильтру; также возращает
  информацию о странице.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: **page** (0) - номер страницы, **size** (10) - размер
  страницы, **sort** (username) - сортировка.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: фильтр.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">400</span> BAD REQUEST**,
  **<span style="color: orange;">401</span> UNAUTHORIZED**, **<span style="color: orange;">403</span> FORBIDDEN**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
POST api/admin/users;
```

* <span style="color: yellow;">Операция</span> 🚀: создаёт нового пользователя и возвращает его; также возвращает
  ссылку на него.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: пользователь.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">400</span> BAD REQUEST**,
  **<span style="color: orange;">401</span> UNAUTHORIZED**, **<span style="color: orange;">403</span> FORBIDDEN**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">201</span> CREATED**.

```http request
PATCH api/admin/users/{id};
```

* <span style="color: yellow;">Операция</span> 🚀: частично обновляет пользователя и возвращает его.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: пользователь.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">400</span> BAD REQUEST**,
  **<span style="color: orange;">401</span> UNAUTHORIZED**, **<span style="color: orange;">403</span> FORBIDDEN**,
  **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">200</span> OK**.

```http request
DELETE api/admin/users/{id};
```

* <span style="color: yellow;">Операция</span> 🚀: удаляет пользователя по идентификатору.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">401</span> UNAUTHORIZED**,
  **<span style="color: orange;">403</span> FORBIDDEN**, **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">204</span> NO CONTENT**.

```http request
PATCH api/admin/users/{id}/grant;
```

* <span style="color: yellow;">Операция</span> 🚀: добавляет пользователя в роли.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: коллекция ролей.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">400</span> BAD REQUEST**,
  **<span style="color: orange;">401</span> UNAUTHORIZED**, **<span style="color: orange;">403</span> FORBIDDEN**,
  **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">204</span> NO CONTENT**.

```http request
PATCH api/admin/users/{user_id}/grant/{role_name};
```

* <span style="color: yellow;">Операция</span> 🚀: добавляет пользователя в роль.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">401</span> UNAUTHORIZED**,
  **<span style="color: orange;">403</span> FORBIDDEN**, **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">204</span> NO CONTENT**.

```http request
PATCH api/admin/users/{id}/revoke;
```

* <span style="color: yellow;">Операция</span> 🚀: удаляет пользователя из ролей.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: коллекция ролей.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">400</span> BAD REQUEST**,
  **<span style="color: orange;">401</span> UNAUTHORIZED**, **<span style="color: orange;">403</span> FORBIDDEN**,
  **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">204</span> NO CONTENT**.

```http request
PATCH api/admin/users/{user_id}/revoke/{role_name};
```

* <span style="color: yellow;">Операция</span> 🚀: удаляет пользователя из роли.
* <span style="color: purple;">Ожидаемые параметры</span> 📊: не ожидается.
* <span style="color: purple;">Ожидаемое тело запроса</span> 🧰: не ожидается.
* <span style="color: purple;">Ожидаемые заголовки запроса</span> 📰: **Bearer**-токен.
* <span style="color: orange;">Предсказуемые ошибки</span> ⛔: **<span style="color: orange;">401</span> UNAUTHORIZED**,
  **<span style="color: orange;">403</span> FORBIDDEN**, **<span style="color: orange;">404</span> NOT FOUND**.
* <span style="color: green;">Результат</span> ✅: **<span style="color: green;">204</span> NO CONTENT**.

## Запуск

### Стандартный запуск

Для запуска 🚀 необходимо выполнить следующие действия:

```shell

# Клонируем репозиторий
git clone https://github.com/a-slelin/TaskManagement.git;

# Переходим в папку корневой библиотеки Core;
cd TaskManagement/Core;

# Устанавливаем корневую библиотеку в локальный репозиторий maven (.m2);
mvn clean install;

# Переходим в папку Auth;
cd ../TaskManagement/Auth;

# Собираем проект в jar-файл, минуя тесты;
mvn clean package -DskipTests;

# Запускаем приложение. Управление переменными окружения;
# происходит в файле .env для удобства;
export $(grep -v '^#' .env | xargs) && java -jar target/Auth.jar;

# Либо передаём в ручную все переменные окружения;
java -jar target/Auth.jar \
    --DB_HOST=<хост СУБД> \
    --DB_PORT=<порт СУБД> \
    --DB_NAME=<название БД> \
    --DB_USER_NAME=<пользователь в БД> \
    --DB_USER_PASSWORD=<пароль пользователя в БД> \
    --APP_PORT=<порт приложения> \
    --LOG_PATH=<папка для логов приложения> \
    --JWT_SECRET=<секрет для приложения> \
    --JWT_ACCESS_EXPIRATION=<время жизни токена доступа> \
    --JWT_REFRESH_EXPIRATION=<время жизни токена обновления> ;

```

Остановить программу ⛔ можно обычным "Ctrl+C".

### Быстрый запуск с помощью DockerCompose

Для быстрого запуска 🚀 можно использовать 🐳 DockerCompose файл, для этого необходимо, чтобы был установлен docker на
вашу машину. Также убедитесь, что база данных запущена и готова к работе. Следуйте инструкциям (необходимыми настройками
🔧 можно управлять в .env файле):

```shell

# Клонируем репозиторий;
git clone https://github.com/TaskManagement.git;

# Переходим в папку Auth;
cd TaskManagement/Auth;

# Запускаем приложение;
docker compose up;
```

Для остановки ⛔ программы можно сделать следующее (или "Ctrl+C"):

```shell

docker compose down;

```

## Улучшения

Для **<span style="color: yellow;">Auth</span>**-модуля 🔒 планируются некоторые улучшения:

**1)** В модель данных добавить аудит, который будет показывать кто создал сущность и кто последний обновлял эту
сущность. Под слово "кто" можно понимать UUID-идентификатор пользователя. Данное улучшение может быть необходимо для
работы администраторов.

**2)** Описать документацию с помощью javadoc. Однозначно будет полезно для развивающихся проектов, особенно, если
разработка начнётся в команде.

**3)** Продумать вариант пересмотра модели и добавления сущности вроде "Аккаунт".

**4)** Рассмотреть варианты использования SUPERUSER роли в качестве "над админской роли".

## Релизы

На данный момент версия **<span style="color: yellow;">Auth</span>** 🔒 - **1.0** 🔖.

## Статус

Находится на этапе разработки 💻.

## Разработчики

Слелин Александр 🤗 (**a-slelin**).
