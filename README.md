# property-view

REST API для работы с отелями. Java 21, Spring Boot 3, Spring Data JPA, Liquibase, H2, Maven.

## Запуск

    mvn spring-boot:run

Приложение стартует на порту 8092. Все методы доступны с префиксом /property-view, например http://localhost:8092/property-view/hotels

Swagger: http://localhost:8092/property-view/swagger-ui.html

При старте Liquibase создаёт таблицы и добавляет один отель для примера.

## Методы

GET /property-view/hotels - список отелей с краткой информацией
GET /property-view/hotels/{id} - подробная информация об отеле
GET /property-view/search - поиск по name, brand, city, country, amenities
POST /property-view/hotels - создание отеля
POST /property-view/hotels/{id}/amenities - добавление списка amenities к отелю
GET /property-view/histogram/{param} - количество отелей по brand, city, country или amenities

Примеры поиска:

    /property-view/search?city=minsk
    /property-view/search?brand=hilton&country=belarus
    /property-view/search?amenities=Free WiFi,Free parking

Поиск не зависит от регистра. Поле name ищется по вхождению. Если указано несколько amenities, отель должен иметь все.

Пример создания отеля (description и checkOut можно не указывать):

    POST /property-view/hotels

    {
      "name": "DoubleTree by Hilton Minsk",
      "brand": "Hilton",
      "address": {
        "houseNumber": 9,
        "street": "Pobediteley Avenue",
        "city": "Minsk",
        "country": "Belarus",
        "postCode": "220004"
      },
      "contacts": {
        "phone": "+375 17 309-80-00",
        "email": "doubletreeminsk.info@hilton.com"
      },
      "arrivalTime": {
        "checkIn": "14:00"
      }
    }

Пример добавления amenities:

    POST /property-view/hotels/1/amenities

    ["Free parking", "Free WiFi", "Fitness center"]

Если отель не найден, возвращается 404. Если тело запроса неверное или указан неизвестный параметр гистограммы, возвращается 400.

## Тесты

    mvn test

## Другая база данных

Схема создаётся через Liquibase, запросы написаны на JPQL и Criteria API, поэтому для смены базы хватает настроек подключения. Для PostgreSQL есть готовый профиль:

    mvn spring-boot:run -Dspring-boot.run.profiles=postgres

Адрес и учётные данные задаются переменными окружения DB_URL, DB_USER и DB_PASSWORD. Если их не задавать, используется postgres на localhost:5432, база hotels.

## Структура

Проект разделён на слои: controller, service, repository. Отдельно лежат entity, dto, mapper и exception. Для поиска используется Specification.