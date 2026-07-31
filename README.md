# Food Identity Service

## Service Overview 
This service is the core authentication engine for the Food Market MVP. 
It authenticates users via their credentials and generates JWTs to establish secure sessions.

## Prerequisites

Before running this service, ensure you have the following installed and configured on your machine:

- Java 21 and Maven for local development
- PostgreSQL, reachable using the database settings below
- Docker, if running the containerized application

## Configuration

Create a `.env` file in the repository root before starting the application locally or with Docker:

```properties
# PostgreSQL connection
DB_HOST=localhost
DB_PORT=5432
DB_NAME=food_identity
DB_USERNAME=<database-user>
DB_PASSWORD=<database-password>

# Application security
APP_SECURITY_ADMIN=admin
APP_SECURITY_USER=user
APP_SECURITY_PASSWORD=<bcrypt-password-hash>
APP_SECURITY_SECRET=<long-random-jwt-signing-secret>
```

The application requires every value above. Create the database named by `DB_NAME` and ensure the PostgreSQL server accepts connections from the application. The `.env` file is local-only and must not be committed.

## Running Locally

With PostgreSQL running and the `.env` file configured, start the application from the repository root:

```bash
mvn spring-boot:run
```

## Testing the API locally 
Once the application is running, you can test the authentication endpoint by sending a POST request to the server.

Open a new terminal window and run the `curl` command: 
```bash
curl -X POST http://localhost:8081/v1/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username": "user", "password": "123"}'
```

## Run with Docker

Build the image from the repository root:

```bash
docker build --tag food-identity:local .
```

Run the container with the `.env` configuration. Its database host must be reachable from inside the container. For a database running on the Docker host, use `DB_HOST=host.docker.internal`; for a database in another Docker container, use that container's service name and put both containers on the same Docker network.

The service is available on port `8081`:

```bash
docker run --rm --env-file .env --publish 8081:8081 food-identity:local
```
