# CRM на Java

Небольшая CRM, написанная в учебных целях.

- [Демо](http://javacrm.giv13.beget.tech/) (логин/пароль администратора: admin/admin, логин/пароль пользователя: user1/user)
- [API-документация](http://javacrm.giv13.beget.tech/api/swagger-ui/index.html) (Swagger)

## Используемый стек

- **Backend**: Spring
- **Frontend**: За основу взят Vuestic Admin (Vue, TypeScript)
- **БД**: PostgreSQL
- **ORM**: Hibernate
- Docker для быстрого развертывания

## Особенности

- Авторизация на основе JWT. В том числе реализованы Refresh-токены. Хранение токенов в HttpOnly Cookie.
- Разграничение прав пользователей по ролям и разрешениям. Проверка разрешений как на бэкенде, так и на фронтенде - весь недоступный функционал скрывается.
- Валидация полей при регистрации/добавлении/удалении пользователя. Валидация пароля с помощью Passay, подтверждение пароля. Кастомные валидаторы, проверка на занятость имени пользователя/почты.
- Загрузка аватарок для пользователей, обрезка/оптимизация изображений на бэкенде.
- По максимуму старался оптимизировать запросы, избежать проблем N + 1, использовать Lazy Load, EntityGraph.
- Стандартизация ответов от бэкенда (RestControllerAdvice).
- Использование Liquibase для миграций.
- Покрытие контроллеров и сервисов Unit-тестами.
- Swagger для API-документации.

## Установка (Docker)

1. Склонировать репозиторий

    ```bash
    git clone https://github.com/giv13/javacrm.git
    ```

2. Перейти в папку с репозиторием

    ```bash
    cd javacrm
    ```
   
3. Скопировать пример файла переменных окружения

    ```bash
    cp .env.example .env
    ```

4. Открыть скопированный файл. В моем случае я открываю файл стандартным блокнотом Windows.

    ```bash
    notepad .env
    ```
   
5. В файле задать настройки БД. В `CORS_ORIGIN` задать `http://localhost:9000`. В `JWT_SECRET` задать [сгенерированный секрет](https://jwtsecret.com/generate). Пример файла:

    ```txt
    DB_NAME=javacrm-db
    DB_USER=javacrm-user
    DB_PASSWORD=javacrm-pass
    
    CORS_ORIGIN=http://localhost:9000
    
    JWT_SECRET=d197017c078ef847d23c2648a60b559c75d8fee74d02e35e8cfd5f152f00f45bef3a79833c749e64c846ffa006ee4a5671d87f951be483946d9fb13dc190b410dc77a8b6289e16d823edb300d00dc6f2b74f3f8927ee9b383aefd5a1f91733f443f36207d5eaa80dbde4fe2e36558e257261fee03be3e93ce3e21339aa718f5d
    ```

6. Развернуть приложение

    ```bash
    docker-compose up -d --build
    ```

7. Приложение будет доступно по пути `http://localhost:9000/`. Чтобы остановить приложение и удалить все сервисы, выполните команду:

    ```bash
    docker-compose down
    ```