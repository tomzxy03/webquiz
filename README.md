# Web Quiz Application

This is a Spring Boot application designed for managing quizzes. It provides a backend for creating, taking, and managing quizzes.

## Technologies Used

*   **Spring Boot**: Framework for building the application.
*   **Java 21**: The programming language used.
*   **Maven**: Dependency management and build automation tool.
*   **PostgreSQL**: Primary database.
*   **Redis**: Cache/session store.
*   **Lombok**: To reduce boilerplate code (e.g., getters, setters, constructors).
*   **MapStruct**: For generating type-safe bean mappers.
*   **Springdoc OpenAPI (Swagger UI)**: For API documentation and testing.
*   **Hibernate Types 52**: For additional Hibernate type support.

## How to Run

### Prerequisites

*   Java Development Kit (JDK) 21
*   Apache Maven
*   PostgreSQL and Redis (or use Docker Compose below)

### Steps

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd webquiz
    ```
2.  **Build the project using Maven:**
    ```bash
    mvn clean install
    ```
3.  **Configure your database:**
    This application uses Spring Data JPA with PostgreSQL. Configure the environment variables used by `src/main/resources/application.yml`:

    ```bash
    export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/quizory
    export SPRING_DATASOURCE_USERNAME=your_username
    export SPRING_DATASOURCE_PASSWORD=your_password
    export SPRING_REDIS_HOST=localhost
    export SPRING_REDIS_PORT=6379
    export JWT_SECRET=your_secret
    ```

4.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    The application will start on port 8080 by default.

## Run with Docker Compose

If you prefer Docker, this repo includes a `docker-compose.yml` that starts PostgreSQL, Redis, and the API:

```bash
export JWT_SECRET=your_secret
docker compose up --build
```

## Database Migrations

Flyway is enabled and migrations live under `src/main/resources/db/migration`.

## API Documentation

The API documentation is available via Swagger UI once the application is running.
You can access it at:
`http://localhost:8080/swagger-ui.html`
