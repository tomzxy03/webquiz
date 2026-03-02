# Web Quiz Application

This is a Spring Boot application designed for managing quizzes. It provides a robust backend for creating, taking, and managing quizzes.

## Technologies Used

*   **Spring Boot**: Framework for building the application.
*   **Java 21**: The programming language used.
*   **Maven**: Dependency management and build automation tool.
*   **PostgreSQL**: For connecting to PostgreSQL databases.
*   **Lombok**: To reduce boilerplate code (e.g., getters, setters, constructors).
*   **MapStruct**: For generating type-safe bean mappers.
*   **Springdoc OpenAPI (Swagger UI)**: For API documentation and testing.
*   **Hibernate Types 52**: For additional Hibernate type support.

## How to Run

### Prerequisites

*   Java Development Kit (JDK) 21
*   Apache Maven

### Steps

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd web_quiz
    ```
2.  **Build the project using Maven:**
    ```bash
    mvn clean install
    ```
3.  **Configure your database:**
    This application uses Spring Data JPA and can connect to either MySQL or PostgreSQL. You will need to configure your database connection properties in `src/main/resources/application.properties` (or `application.yml`).

    Example for PostgreSQL:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/webquizdb
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    ```

4.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    The application will start on port 8080 by default.

## API Documentation

The API documentation is available via Swagger UI once the application is running.
You can access it at:
`http://localhost:8080/swagger-ui.html`
