This is a demo project of a microservice for subscriptions management.

# Running the application

To start the application with its Postgres dependency, open a terminal at the root of the application and run:

```shell
make run
```

If you prefer doing it manually:

```shell
# Start Postgres
docker-compose up -d

# Start the application
./gradlew bootRun
```

Or run the main method on `com.github.lucasls.subscriptions.SubscriptionsApplication`

# Testing it

## Demo data

The application will load with demo data by default (unless the default "demo" profile is not used) using
the `com.github.lucasls.subscriptions.DemoDataSetup` class.

Demo data consists of:

### Products

- Annual
- Semi-Annual
- Quarterly

### Subscriptions

Some user-ids come pre-configured to help testing:

- `00000000-0000-0000-0000-000000000001`: Active subscription
- `00000000-0000-0000-0000-000000000002`: Active subscription with a 10 days pause
- `00000000-0000-0000-0000-000000000003`: Paused subscription
- `00000000-0000-0000-0000-000000000004`: Active subscription with old product price and tax
- `00000000-0000-0000-0000-000000000005`: Expired subscription

## Restore original demo data

To restore the original data, run

```shell
make restore
```

## Swagger UI

The service exposes a Swagger UI address, where the endpoints of the service can be checked. Once the application is
running, you can access http://localhost:8080/swagger-ui.html.

## Postman Collection

If you use Postman, you can test the endpoints using the following collection:
https://www.getpostman.com/collections/333948a5ebb09faf4dd0

## Example calls

### List products

```shell
curl --location --request GET 'localhost:8080/v1/products/'
```

### Find a user subscription

```shell
curl --location --request GET 'localhost:8080/v1/users/00000000-0000-0000-0000-000000000001/subscription'
```

### Create a subscription

```shell
curl --location --request POST 'localhost:8080/v1/users/10000000-0000-0000-0000-000000000000/subscription' \
--header 'Content-Type: application/json' \
--data-raw '{
    "paymentToken": "some-token",
    "paymentProvider": "PAYPAL",
    "productCode": "SEMI_ANNUAL"
}'
```

### Set subscription status (Pause/Unpause)

````shell
curl --location --request PUT 'localhost:8080/v1/users/10000000-0000-0000-0000-000000000000/subscription/status' \
--header 'Content-Type: application/json' \
--data-raw '"PAUSED"'
````

### Cancel a subscription

```shell
curl --location --request DELETE 'localhost:8080/v1/users/10000000-0000-0000-0000-000000000000/subscription'
```

**Note:** Observe that `expireBy` will have a different value for every request on
user `00000000-0000-0000-0000-000000000003` ;)

# Modules structure

This project implements a domain-centric architecture, and in order to separate the domain layer from the rest of the
application it is divided in 2 modules: **subscriptions-application** and **subscriptions-domain**.

```
+-------------------------------------------------+
|                   APPLICATION                   |
+-------------------------------------------------+
|                                                 |
|                   [Boundary]                    |
|                                                 |
|          +---------------------------+          |
|          |          DOMAIN           |          |
|          |---------------------------|          |
|          |                           |          |
|          |      [Domain model]       |          |
|          |                           |          |
|          |        [Use cases]        |          |
|          |                           |          |
|          +---------------------------+          |
|                                                 |
|    [Persistence]                 [External]     |
|                                                 |
+-------------------------------------------------+
```

## Domain Module

Contains the main business logic. It's independent of boundary, persistence and external components.

This domain module does depend on Spring, but only on `spring-context` for Dependency Injection.

All classes can be unit tested.

Repositories and external services gateways are defined here as interfaces, and only reference domain model classes.

Packaging here is done by resources, e.g. `com.github.lucasls.subscriptions.domain.subscription` and
`com.github.lucasls.subscriptions.domain.product`

## Application Module

This is the actual application. It's based on Spring Boot, with Spring Web (not Webflux) and Spring Data JPA.

Persistence repositories and external services gateways are implemented here.

### Boundary

REST Controllers and DTO classes used to communicate with this service are put in the
`com.github.lucasls.subscriptions.boundary` package. Domain classes are not exposed by the controllers.

Mapping between the domain and the boundary classes is done in `BoundaryMappers`, using Map Struct.

### Persistence

This application uses JPA, but unlike commonly done, the domain model classes are not persisted. Instead, dedicated
classes are used to allow the domain to be fully independent of the database schema and JPAs requirements and
limitations. Those classes are located at `com.github.lucasls.subscriptions.persistence.jpa.entity`

Spring Data JPA also uses the name "Repository", to make a distinction between the Domain repositories and JPA
repositories, the later are suffixed with `JpaRepository` and put on the `jpa` package,
e.g. `com.github.lucasls.subscriptions.persistence.jpa.ProductJpaRepository`

### External

The external Gateway is not a real implementation, only a simple fake.

# Technologies

- Kotlin
- Spring Boot
- Postgres
- Flyway
- Gradle
- Docker
- [Springdoc](https://springdoc.org/) (Swagger UI and OpenAPI)
- [Kotest](https://kotest.io/) (Kotlin test platform and assertions)
- [Testcontainers](https://www.testcontainers.org/) (Docker containers for tests)
- [MapStruct](https://mapstruct.org/) (Compile time generated mappers)
- [Kotlin Logging](https://github.com/MicroUtils/kotlin-logging) (Slf4J wrapper for Kotlin)
- [Ktlint](https://github.com/pinterest/ktlint) (Kotlin linter)