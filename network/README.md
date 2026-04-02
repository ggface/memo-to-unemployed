# Сеть

- [REST](#rest)

### REST
REST — это архитектурный стиль для построения сетевых API. Расшифровывается как Representational State Transfer.

1) Все в REST — это ресурсы. Ресурс — это просто сущность, с которой ты работаешь.

Примеры:
```
/users
/users/1
/posts/42/comments
```

2) HTTP-методы = действия

| Метод  | Что делает         | Пример          |
| ------ | ------------------ | --------------- |
| GET    | получить данные    | GET /users      |
| POST   | создать            | POST /users     |
| PUT    | полностью обновить | PUT /users/1    |
| PATCH  | частично обновить  | PATCH /users/1  |
| DELETE | удалить            | DELETE /users/1 |

3) Статусы ответа (HTTP Status Codes)

| Код | Значение     |
| --- | ------------ |
| 200 | OK           |
| 201 | Created      |
| 204 | No Content   |
| 400 | Bad Request  |
| 401 | Unauthorized |
| 403 | Forbidden    |
| 404 | Not Found    |
| 500 | Server Error |

4) Stateless (без состояния)
   Каждый запрос самодостаточный.

❗ Сервер не хранит состояние клиента между запросами.

Это значит что токен нужно передавать каждый раз.
```http request
GET /profile
Authorization: Bearer token
```