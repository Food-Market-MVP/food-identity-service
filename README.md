# Food Identity Service

## Service Overview 
This service is the core authentication engine for the Food Market MVP. 
It authenticates users via their credentials and generates JWTs to establish secure sessions.

## Prerequisite 
Before running this service, ensure you have the following installed and configured on your machine:

* **Java:** JDK 21
* **Maven** Installed 

## Running Locally
To start the application on your local machine, open your terminal in the root directory of this project and run the following command:

```bash
mvn spring-boot:run
```

## Testing the API locally 
Once the application is running, you can test the authentication endpoint by sending a POST request to the server.

Open a new terminal window and run the `curl` command: 
```bash
curl -X POST http://localhost:8080/v1/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username": "user", "password": "123"}'
```

## Run with Docker

Build the image from the repository root:

```bash
docker build --tag food-identity:local .
```

Create a local `.env` file containing the required security configuration:

```properties
APP_SECURITY_ADMIN=admin
APP_SECURITY_USER=user
APP_SECURITY_PASSWORD=<bcrypt-password-hash>
APP_SECURITY_SECRET=<long-random-jwt-signing-secret>
```

Run the container with those values. The service is available on port `8081`:

```bash
docker run --rm --env-file .env --publish 8081:8081 food-identity:local
```

