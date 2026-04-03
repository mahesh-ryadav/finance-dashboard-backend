#  Finance Dashboard Backend

A production-ready RESTful backend API for a Finance Dashboard System built with
Java Spring Boot. This project demonstrates clean architecture, role-based access
control, JWT authentication, financial data management, and dashboard analytics.

---

##  Author

| Field        | Details                                      |
|--------------|----------------------------------------------|
| Name         | Mahesh Yadav                                 |
| Email        | mahi234xp@gmail.com                          |
| GitHub       | https://github.com/mahesh-ryadav             |
| LinkedIn     | https://linkedin.com/in/mahesh-ryadav        |
| Assignment   | Finance Data Processing & Access Control     |
| Organization | Zorvyn FinTech Pvt. Ltd.                     |

---

##  Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Features](#features)
- [Project Structure](#project-structure)
- [Database Design](#database-design)
- [API Endpoints](#api-endpoints)
- [Role & Access Control](#role--access-control)
- [Setup & Installation](#setup--installation)
- [Running the Application](#running-the-application)
- [Testing APIs with Swagger](#testing-apis-with-swagger)
- [Sample API Requests](#sample-api-requests)
- [Design Decisions & Assumptions](#design-decisions--assumptions)
- [Error Handling](#error-handling)

---

##  Overview

This backend powers a **Finance Dashboard System** where users interact with
financial records based on their assigned role. The system supports:

- Secure **JWT-based authentication**
- **Role-based access control** (VIEWER / ANALYST / ADMIN)
- Full **CRUD operations** on financial records
- **Dashboard analytics** — totals, trends, category breakdowns
- **Pagination and filtering** on all list endpoints
- **Soft delete** — no data is permanently lost
- **Swagger UI** for live API documentation and testing

---

##  Tech Stack

| Category         | Technology                        |
|------------------|-----------------------------------|
| Language         | Java 17                           |
| Framework        | Spring Boot 3.2.4                 |
| Security         | Spring Security + JWT (jjwt 0.12) |
| Database         | PostgreSQL                        |
| ORM              | Spring Data JPA (Hibernate)       |
| Validation       | Jakarta Bean Validation (JSR-380) |
| API Docs         | SpringDoc OpenAPI (Swagger UI)    |
| Build Tool       | Maven                             |
| Boilerplate      | Lombok                            |

---

##  Architecture

This project follows a strict **Layered Architecture** with clean separation
of concerns across every layer:
```
                        ┌─────────────────┐
                        │   CLIENT / UI   │
                        └────────┬────────┘
                                 │ HTTP Request
                        ┌────────▼────────┐
                        │   CONTROLLER    │  ← Accepts request, returns response
                        └────────┬────────┘
                                 │
                        ┌────────▼────────┐
                        │    SERVICE      │  ← Business logic, validations
                        └────────┬────────┘
                                 │
                        ┌────────▼────────┐
                        │   REPOSITORY   │  ← DB queries via JPA
                        └────────┬────────┘
                                 │
                        ┌────────▼────────┐
                        │   PostgreSQL    │  ← Persistent storage
                        └─────────────────┘

Supporting Layers:
  Security Layer   → JWT filter + Spring Security config
  Exception Layer  → Global exception handler
  Mapper Layer     → Manual DTO ↔ Entity conversion
  DTO Layer        → Request / Response objects
```

---

##  Features

###  Core Features
- **User Management** — Create, update, activate/deactivate, soft delete users
- **Role Assignment** — Assign VIEWER, ANALYST, or ADMIN roles
- **Financial Records** — Full CRUD with soft delete support
- **Advanced Filtering** — Filter by type, category, date range, or combinations
- **Pagination & Sorting** — All list endpoints support page, size, sortBy, sortDir
- **Dashboard APIs** — Summary totals, category breakdown, monthly trends, recent activity

###  Security Features
- JWT Authentication with configurable expiry
- Role-based route protection via Spring Security
- Method-level security with `@PreAuthorize`
- Password hashing with BCrypt
- Inactive user login prevention
- Token validation on every request

###  Quality Features
- Global exception handling with consistent error responses
- Input validation on all request DTOs
- Soft delete on users and records (data never lost)
- N+1 query prevention with JPQL optimization
- CORS configuration for frontend integration
- Swagger UI with JWT authorization support

---

##  Project Structure
```
finance-dashboard-backend/
│
├── pom.xml
├── README.md
│
└── src/main/java/com/finance/dashboard/
    │
    ├── FinanceDashboardApplication.java
    │
    ├── controller/
    │   ├── AuthController.java
    │   ├── UserController.java
    │   ├── RecordController.java
    │   └── DashboardController.java
    │
    ├── service/
    │   ├── AuthService.java
    │   ├── UserService.java
    │   ├── RecordService.java
    │   └── DashboardService.java
    │
    ├── repository/
    │   ├── UserRepository.java
    │   └── FinancialRecordRepository.java
    │
    ├── entity/
    │   ├── BaseEntity.java
    │   ├── User.java
    │   └── FinancialRecord.java
    │
    ├── dto/
    │   ├── request/
    │   │   ├── RegisterRequest.java
    │   │   ├── LoginRequest.java
    │   │   ├── CreateUserRequest.java
    │   │   ├── UpdateUserRequest.java
    │   │   ├── CreateRecordRequest.java
    │   │   └── UpdateRecordRequest.java
    │   └── response/
    │       ├── ApiResponse.java
    │       ├── AuthResponse.java
    │       ├── UserResponse.java
    │       ├── RecordResponse.java
    │       ├── DashboardSummaryResponse.java
    │       ├── CategorySummaryResponse.java
    │       ├── MonthlyTrendResponse.java
    │       └── PagedResponse.java
    │
    ├── mapper/
    │   ├── UserMapper.java
    │   └── RecordMapper.java
    │
    ├── enums/
    │   ├── Role.java
    │   ├── TransactionType.java
    │   └── UserStatus.java
    │
    ├── security/
    │   ├── SecurityConfig.java
    │   ├── JwtUtil.java
    │   ├── JwtAuthFilter.java
    │   ├── CustomUserDetailsService.java
    │   └── SecurityConstants.java
    │
    ├── exception/
    │   ├── GlobalExceptionHandler.java
    │   ├── ErrorResponse.java
    │   ├── ResourceNotFoundException.java
    │   ├── DuplicateResourceException.java
    │   ├── AccessDeniedException.java
    │   ├── UserInactiveException.java
    │   └── InvalidRequestException.java
    │
    └── config/
        ├── SwaggerConfig.java
        ├── AppConfig.java
        └── PasswordEncoderConfig.java
```

---

##  Database Design

### Tables

#### `users`
| Column      | Type          | Constraints              |
|-------------|---------------|--------------------------|
| id          | BIGSERIAL     | PRIMARY KEY              |
| name        | VARCHAR(100)  | NOT NULL                 |
| email       | VARCHAR(150)  | NOT NULL, UNIQUE         |
| password    | VARCHAR(255)  | NOT NULL (BCrypt hashed) |
| role        | VARCHAR(20)   | NOT NULL (ENUM as STRING)|
| status      | VARCHAR(20)   | NOT NULL, DEFAULT ACTIVE |
| is_deleted  | BOOLEAN       | NOT NULL, DEFAULT false  |
| created_at  | TIMESTAMP     | NOT NULL (auto)          |
| updated_at  | TIMESTAMP     | auto                     |

#### `financial_records`
| Column      | Type          | Constraints              |
|-------------|---------------|--------------------------|
| id          | BIGSERIAL     | PRIMARY KEY              |
| amount      | NUMERIC(15,2) | NOT NULL, > 0            |
| type        | VARCHAR(10)   | NOT NULL (INCOME/EXPENSE)|
| category    | VARCHAR(100)  | NOT NULL                 |
| date        | DATE          | NOT NULL                 |
| notes       | VARCHAR(500)  | NULLABLE                 |
| is_deleted  | BOOLEAN       | NOT NULL, DEFAULT false  |
| created_by  | BIGINT        | FK → users.id            |
| created_at  | TIMESTAMP     | NOT NULL (auto)          |
| updated_at  | TIMESTAMP     | auto                     |

### Entity Relationship
```
users (1) ──────────── (N) financial_records
         (via created_by FK)
```

---

##  API Endpoints

### Base URL
```
http://localhost:8080/api/v1
```

###  Authentication — Public
| Method | Endpoint              | Description          |
|--------|-----------------------|----------------------|
| POST   | /auth/register        | Register new user    |
| POST   | /auth/login           | Login & get JWT      |

###  User Management — ADMIN only
| Method | Endpoint              | Description          |
|--------|-----------------------|----------------------|
| GET    | /users                | Get all users        |
| GET    | /users/{id}           | Get user by ID       |
| POST   | /users                | Create user          |
| PUT    | /users/{id}           | Update user          |
| PATCH  | /users/{id}/status    | Toggle status        |
| DELETE | /users/{id}           | Soft delete user     |

###  Financial Records
| Method | Endpoint              | Access               | Description       |
|--------|-----------------------|----------------------|-------------------|
| GET    | /records              | ALL roles            | Get all (filtered)|
| GET    | /records/{id}         | ALL roles            | Get by ID         |
| POST   | /records              | ADMIN                | Create record     |
| PUT    | /records/{id}         | ADMIN                | Full update       |
| PATCH  | /records/{id}         | ADMIN                | Partial update    |
| DELETE | /records/{id}         | ADMIN                | Soft delete       |

#### Filter Query Parameters
```
GET /api/v1/records?type=INCOME
GET /api/v1/records?category=SALARY
GET /api/v1/records?startDate=2024-01-01&endDate=2024-12-31
GET /api/v1/records?type=EXPENSE&category=FOOD
GET /api/v1/records?page=0&size=10&sortBy=date&sortDir=desc
```

###  Dashboard — ANALYST & ADMIN
| Method | Endpoint                    | Description              |
|--------|-----------------------------|--------------------------|
| GET    | /dashboard/summary          | Income, expense, balance |
| GET    | /dashboard/category-summary | Totals by category       |
| GET    | /dashboard/monthly-trends   | Month-wise trends        |
| GET    | /dashboard/recent-activity  | Latest transactions      |

---

##  Role & Access Control

| Role    | Records (Read) | Records (Write) | Dashboard | User Management |
|---------|---------------|-----------------|-----------|-----------------|
| VIEWER  | ✅            | ❌              | ❌        | ❌              |
| ANALYST | ✅            | ❌              | ✅        | ❌              |
| ADMIN   | ✅            | ✅              | ✅        | ✅              |

Access control is enforced at **two levels**:
1. **Route level** — via `SecurityConfig` (Spring Security filter chain)
2. **Method level** — via `@PreAuthorize` on each controller method

---

##  Setup & Installation

### Prerequisites

Make sure you have the following installed:
```
Java 17+        → https://adoptium.net
Maven 3.8+      → https://maven.apache.org
PostgreSQL 14+  → https://www.postgresql.org
```

### Step 1 — Clone the Repository
```bash
git clone https://github.com/mahesh-ryadav/finance-dashboard-backend.git
cd finance-dashboard-backend
```

### Step 2 — Create PostgreSQL Database
```sql
CREATE DATABASE finance_dashboard;
```

### Step 3 — Configure application.properties

Open `src/main/resources/application.properties` and update:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/finance_dashboard
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password

app.jwt.secret=your_secret_key_minimum_32_characters_long
app.jwt.expiration=86400000
```

### Step 4 — Install Dependencies
```bash
mvn clean install
```

---

##  Running the Application
```bash
mvn spring-boot:run
```

The application starts at:
```
http://localhost:8080
```

Swagger UI available at:
```
http://localhost:8080/swagger-ui.html
```

> Hibernate will auto-create all tables on first run via `ddl-auto=update`
> No manual SQL scripts needed.

---

##  Testing APIs with Swagger

1. Open `http://localhost:8080/swagger-ui.html`
2. Register an ADMIN user:
```json
POST /api/v1/auth/register
{
  "name": "Mahesh Admin",
  "email": "admin@finance.com",
  "password": "admin123",
  "role": "ADMIN"
}
```
3. Copy the `token` from the response
4. Click the **Authorize ** button at the top right
5. Enter: `Bearer <paste_token_here>`
6. Click **Authorize** → now all protected endpoints are unlocked
7. Test any endpoint directly from Swagger UI

---

##  Sample API Requests

### Register
```json
POST /api/v1/auth/register
{
  "name": "Mahesh Yadav",
  "email": "mahesh@finance.com",
  "password": "mahesh123",
  "role": "ADMIN"
}
```

### Login
```json
POST /api/v1/auth/login
{
  "email": "mahesh@finance.com",
  "password": "mahesh123"
}
```

### Create Financial Record
```json
POST /api/v1/records
Authorization: Bearer <token>
{
  "amount": 75000.00,
  "type": "INCOME",
  "category": "SALARY",
  "date": "2024-03-01",
  "notes": "March salary credited"
}
```

### Filter Records
```
GET /api/v1/records?type=EXPENSE&category=FOOD&page=0&size=5
Authorization: Bearer <token>
```

### Dashboard Summary
```
GET /api/v1/dashboard/summary
Authorization: Bearer <token>
```

### Sample Success Response
```json
{
  "success": true,
  "message": "Record created successfully",
  "data": {
    "id": 1,
    "amount": 75000.00,
    "type": "INCOME",
    "category": "SALARY",
    "date": "2024-03-01",
    "notes": "March salary credited",
    "createdByName": "Mahesh Yadav",
    "createdAt": "2024-03-01T10:30:00",
    "updatedAt": "2024-03-01T10:30:00"
  },
  "timestamp": "2024-03-01T10:30:00"
}
```

### Sample Error Response
```json
{
  "success": false,
  "status": 400,
  "errorCode": "VALIDATION_FAILED",
  "message": "Input validation failed. Please check the field errors.",
  "fieldErrors": {
    "amount": "Amount must be greater than 0",
    "date": "Date cannot be in the future"
  },
  "path": "/api/v1/records",
  "timestamp": "2024-03-01T10:30:00"
}
```

---

##  Design Decisions & Assumptions

| Decision | Reasoning |
|----------|-----------|
| `BigDecimal` for amount | Avoids float/double precision loss on financial data — industry standard for money |
| `LocalDate` for transaction date | Transactions have a date, not a specific time — cleaner and timezone-safe |
| Enum stored as STRING in DB | `INCOME` is readable in DB directly, unlike index `0` — easier to debug |
| No separate Role table | Roles are fixed at design time (VIEWER/ANALYST/ADMIN) — a separate table adds complexity with zero benefit for this scope |
| Soft delete on all entities | Financial data should never be permanently deleted — audit trail preserved |
| Manual DTO mapping (no MapStruct) | Explicit control over what gets mapped — easier to debug and understand |
| JWT stored client-side | Stateless API design — no session management needed on server |
| `@Transactional(readOnly=true)` on GETs | Hibernate optimization — skips dirty checking for read operations |
| JPQL over native SQL | Database-agnostic queries — works with any JPA-compatible DB |
| `COALESCE` in sum queries | Returns `0` instead of `null` when no records exist — prevents NullPointerException |
| Default role = VIEWER on register | Principle of least privilege — new users get minimum access by default |
| Limit clamped in recent activity | Prevents abuse — maximum 50 records returned regardless of input |
| Filter routing in service layer | Controller stays thin — all business decisions (which query to call) live in service |

---

##  Error Handling

All errors return a consistent JSON structure:

| Scenario | HTTP Status | Error Code |
|----------|-------------|------------|
| Resource not found | 404 | RESOURCE_NOT_FOUND |
| Duplicate email | 409 | DUPLICATE_RESOURCE |
| Access denied | 403 | ACCESS_DENIED |
| Inactive user login | 403 | USER_INACTIVE |
| Invalid input | 400 | INVALID_REQUEST |
| Validation failure | 400 | VALIDATION_FAILED |
| Missing parameter | 400 | MISSING_PARAMETER |
| Wrong credentials | 401 | INVALID_CREDENTIALS |
| Server error | 500 | INTERNAL_SERVER_ERROR |

---

##  Project Stats
```
Total Layers      → 8  (controller, service, repository, entity,
                        dto, mapper, security, exception)
Total Endpoints   → 19
Total Java Files  → ~40
Database Tables   → 2  (users, financial_records)
Roles Supported   → 3  (VIEWER, ANALYST, ADMIN)
```

---

*Built with ❤️ by Mahesh Yadav for Zorvyn FinTech Backend Internship Assignment*
```

---

##  Final Project Checklist
```
✅ Entities + Enums
✅ application.properties
✅ Main Application class
✅ Repository Layer
✅ DTO Layer
✅ Mapper Layer
✅ Exception Layer
✅ Security Layer
✅ Service Layer
✅ Controller Layer
✅ Config Layer
✅ pom.xml
✅ README.md

 PROJECT IS 100% COMPLETE
```

---

##  What to Do Now
```
Step 1 → Create Spring Boot project on start.spring.io
Step 2 → Copy pom.xml dependencies
Step 3 → Create all packages and files in order
Step 4 → Create PostgreSQL database
Step 5 → Update application.properties
Step 6 → Run mvn spring-boot:run
Step 7 → Open swagger-ui.html and test all APIs
Step 8 → Push to GitHub
Step 9 → Submit repo link + swagger screenshot
