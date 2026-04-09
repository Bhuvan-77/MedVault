# MedVault

MedVault is a Spring Boot backend for a medical records and appointment management system. It supports authentication, patient and doctor workflows, appointment booking and completion, prescriptions, notifications, record access requests, and doctor verification.

## Tech Stack

- Java 17
- Spring Boot 4.0.3
- Spring Web MVC
- Spring Data JPA
- Spring Security
- MySQL
- Lombok
- Maven Wrapper

## Features

- User authentication and registration
- Patient profile and health record management
- Doctor profile setup and verification
- Appointment booking, approval, rejection, and completion
- Prescription handling and feedback
- Medical record storage and access requests
- Notification delivery for app events
- Admin management for doctors, patients, appointments, and records

## Project Structure

- `src/main/java/com/medvault/medvault/controller` - REST controllers
- `src/main/java/com/medvault/medvault/service` - business logic
- `src/main/java/com/medvault/medvault/entity` - JPA entities
- `src/main/java/com/medvault/medvault/dto` - request and response models
- `src/main/java/com/medvault/medvault/repository` - JPA repositories
- `src/main/java/com/medvault/medvault/security` - security configuration
- `src/main/resources/application.properties` - application configuration
- `uploads/` - stored files and uploaded records

## Prerequisites

- JDK 17
- MySQL 8 or compatible database
- Maven Wrapper or Maven

## Configuration

Update `src/main/resources/application.properties` for your local environment:

```properties
spring.application.name=medvault
spring.datasource.url=jdbc:mysql://localhost:3306/medvault_db
spring.datasource.username=root
spring.datasource.password=your_password
server.port=8081
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Create the database before starting the application:

```sql
CREATE DATABASE medvault_db;
```

## Run The Application

On Windows:

```bash
mvnw.cmd spring-boot:run
```

On macOS or Linux:

```bash
./mvnw spring-boot:run
```

The backend runs on `http://localhost:8081` by default.

## Build And Test

```bash
mvnw test
mvnw package
```

## Notes

- The application uses Spring Security, so some endpoints may require authentication.
- The backend is designed to work with the frontend in the sibling `medvault-frontend` project.
- Files uploaded by the application are stored under `uploads/`.

## Reference

For more project setup details, see `HELP.md`.
