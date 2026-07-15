# Food Identity Service

## Service Overview 
This service is the core authentication engine for the Food Market MVP. 
It authenticates users via their credentials and generates JWTs to establish secure sessions.

## Prerequisite 
Before running this service, ensure you have the following installed and configured on your machine:

* **Java:** JDK 21
* **Maven** Installed 
* **Environment Variables:**
  You will need to configure the following environment variables locally 
  * `APP_SECURITY_ADMIN`: Default admin username
  * `APP_SECURITY_USER` : Default standard username
  * `APP_SECURITY_PASSWORD`: Default password for users
  * `APP_SECURITY_SECRET`: Secret key to sign in and generate JWTs.

## Running Locally
To start the application on your local machine, open your terminal in the root directory of this project and run the following command:

```bash
mvn spring-boot:run
```