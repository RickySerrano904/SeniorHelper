# SeniorHelper

SeniorHelper is a full-stack web application designed for older adults and caregivers to stay organized, connected, and safer online. It combines practical care coordination features (shared appointments and caregiver links) with cybersecurity education (guided lessons, quizzes, and progress tracking) in a simple, high-legibility interface.

![App Screenshot](./public/seniorhelperr.jpg)

## Architecture and Stack

- **Architecture:** Monolithic full-stack deployment where a Spring Boot API serves both REST endpoints and the built Angular SPA.
- **Backend:** Java 17, Spring Boot 3, Spring MVC, Spring Data JPA, Spring Security, JWT auth, and PostgreSQL.
- **Frontend:** Angular 21 with standalone components, route guards, HTTP interceptor for bearer tokens, template/reactive forms, Cypress E2E, and Angular unit test tooling.
- **Build/Delivery:** Maven-driven pipeline builds Angular in `frontend/`, then copies `dist` assets into Spring Boot static resources for unified deployment.

## Key UX and Accessibility Decisions

- Large base typography and plain-language content to reduce cognitive and visual friction.
- Role-aware workflows for seniors, caregivers, and admins.
- Progress-first learning UX: module completion status, quiz scoring, and clear "continue" actions.
- Persistent light/dark theme, mobile-responsive layouts, and consistent navigation patterns.
- Form-first interaction with inline validation/error states and explicit labels/autocomplete hints.

## Running the Application

SeniorHelper is a Spring Boot backend with an Angular frontend. The project can be run in two common ways depending on whether you are actively developing the frontend or testing the packaged application.

### Option 1: Run Frontend and Backend Separately

Best for development. This enables live reload for frontend changes.

Start the backend:

1. In your IDE, open `SeniorHelper/src/main/java/seniorhelper/SeniorHelperApplication.java`.
2. Click Run.

Start the frontend:

```powershell
cd SeniorHelper/frontend
ng serve
```

Open `http://localhost:4200`.

### Option 2: Run as a Single Packaged App

Deployment-style. This runs the full application from a single JAR, like the Module 6 individual project.

Build the project:

```powershell
cd SeniorHelper
.\mvnw clean package
```

Run the application:

```powershell
java -jar target/SeniorHelper-0.0.1-SNAPSHOT.jar
```

Open `http://localhost:8080`.
