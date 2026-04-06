**Web Quiz Application**

A Spring Boot backend for creating, taking, and managing quizzes. It provides REST APIs, database migrations, and a local development stack with Docker.

## Table of Contents

- Features
- Demo
- Getting Started
- Usage
- Contributing
- License
- Contact

## Features

- Quiz creation and management APIs
- OpenAPI/Swagger UI for interactive API docs
- Flyway database migrations
- Docker Compose for PostgreSQL and Redis

## Getting Started

### Prerequisites

- JDK 21
- Apache Maven
- PostgreSQL and Redis (or Docker Compose)

### Installation

1. Clone the repository:
   ```bash
   git clone <https://github.com/tomzxy03/webquiz.git>
   cd webquiz
   ```
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Configure environment variables (used by `src/main/resources/application.yml`):
   ```bash
   export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/quizory
   export SPRING_DATASOURCE_USERNAME=your_username
   export SPRING_DATASOURCE_PASSWORD=your_password
   export SPRING_REDIS_HOST=localhost
   export SPRING_REDIS_PORT=6379
   export JWT_SECRET=your_secret
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Run with Docker Compose

```bash
export JWT_SECRET=your_secret
docker compose up --build
```

## Usage

- The application runs on port 8080 by default.
- Swagger UI is available at `http://localhost:8080/swagger-ui.html`.
- Database migrations live under `src/main/resources/db/migration` and run on startup.

## Contributing

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/my-change`.
3. Commit changes and push to your fork.
4. Open a pull request with a clear description.

## License

No license file is currently specified. If you intend this to be open source, add a license (for example: MIT or Apache 2.0).

## Contact

Open an issue or pull request in this repository for questions and support.
