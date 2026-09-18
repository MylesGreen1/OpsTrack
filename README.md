# OpsTrack

OpsTrack is a full-stack aircraft maintenance operations management platform built as an original portfolio project. It models maintenance workflows such as aircraft status, maintenance task assignment, technician work, QA inspections, work notes, parts requests, role-based access, and audit history.

## Technology

- Java 21 and Spring Boot 4
- Spring Web MVC, Spring Data JPA, Spring Security, Bean Validation
- PostgreSQL
- Angular 22 and TypeScript
- Maven
- Docker / Docker Compose
- JUnit, Mockito, Spring Security Test, Angular/Vitest

## Roles

- `TECHNICIAN` — assigned-task workflow, work notes, parts requests
- `SUPERVISOR` — maintenance oversight, technician operations, parts status management
- `QA_INSPECTOR` — QA inspection workflow
- `ADMIN` — broad operational access plus audit history

## Major Features

- Aircraft records and mission/maintenance status
- Maintenance task CRUD, priority, status, aircraft association, and technician assignment
- Authenticated technician **My Assigned Tasks** workflow with server-side ownership verification
- Technician records linked to application users
- QA inspection records and approval status
- Authenticated technician work notes
- Parts request creation and lifecycle (`REQUESTED`, `ORDERED`, `RECEIVED`, `CANCELLED`)
- HTTP Basic authentication with BCrypt password hashing and role-based backend/frontend authorization
- Centralized REST exception handling and validation responses
- Audit log for API write operations
- Angular operations dashboard and role-specific navigation
- PostgreSQL persistence
- Dockerized backend/database development environment

## Project Structure

```text
OpsTrack/
├── backend/                  Spring Boot REST API
│   └── src/main/java/com/opstrack/
│       ├── aircraft/
│       ├── audit/
│       ├── exception/
│       ├── inspection/
│       ├── maintenance/
│       ├── parts/
│       ├── security/
│       ├── technician/
│       └── worknote/
├── frontend/                 Angular application
│   └── src/app/
│       ├── aircraft/
│       ├── audit/
│       ├── auth/
│       ├── dashboard/
│       ├── inspection/
│       ├── maintenance/
│       ├── my-tasks/
│       ├── parts/
│       ├── technician/
│       └── worknote/
├── Dockerfile
└── docker-compose.yml
```

## Local Development

### PostgreSQL

Create a database named `opstrack`, then provide credentials as environment variables:

```powershell
$env:DB_USERNAME="your_postgres_username"
$env:DB_PASSWORD="your_postgres_password"
```

The backend defaults to `jdbc:postgresql://localhost:5432/opstrack`. Override it with `DB_URL` when necessary.

### Backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Backend: `http://localhost:8081`

### Frontend

```powershell
cd frontend
npm install
npm start
```

Frontend: `http://localhost:4200`

## Docker

Start PostgreSQL, the Spring Boot backend, and the Angular/Nginx frontend:

```powershell
docker compose up --build
```

Docker frontend: `http://localhost:8080`  
Docker backend: `http://localhost:8081`  

The compose file uses development-only database credentials. Use secret environment variables in a real deployment.

## Deployment Configuration

The backend supports environment-based configuration:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `PORT`
- `JPA_DDL_AUTO`
- `JPA_SHOW_SQL`

For production, set `JPA_DDL_AUTO=validate` after managing schema migrations and never commit real credentials.

The Angular API base is centralized in `frontend/src/app/api.config.ts`. It defaults to `http://localhost:8081` and can be overridden by defining `globalThis.__OPSTRACK_API_URL__` before Angular boots in a hosted environment.

## Security Note

OpsTrack currently uses HTTP Basic authentication for this portfolio implementation. Passwords are stored server-side as BCrypt hashes. The browser client retains credentials for the active session so its interceptor can authenticate API calls; a production evolution would typically move to a server-managed session or short-lived token strategy.

## Testing

Backend:

```powershell
cd backend
.\mvnw.cmd test
```

Frontend build verification:

```powershell
cd frontend
npm run build
```

## Portfolio Talking Points

OpsTrack demonstrates a layered Spring architecture (controller → service → repository), relational domain modeling with JPA, authenticated ownership checks, role-based authorization, REST API integration with Angular, error handling, validation, automated testing, Git feature-branch workflow, PostgreSQL persistence, and containerization.
