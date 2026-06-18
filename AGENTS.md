# LinkSnap AI Development Rules

## Read First

Before implementing any feature, read these files in order:

1. context/project-overview.md
2. context/architecture.md
3. context/code-standards.md
4. context/build-plan.md
5. context/progress-tracker.md

Do not start implementation until all files are read.

---

## Project Overview

LinkSnap is a URL shortening and analytics platform built using:

* Spring Boot 3
* PostgreSQL
* Redis
* Spring Security + JWT
* PostHog
* Spring AI
* Next.js (future frontend)

---

## Development Workflow

For every feature:

1. Read progress-tracker.md
2. Identify current feature
3. Read architecture.md
4. Implement only the current feature
5. Test the feature
6. Update progress-tracker.md
7. Stop

Never start the next feature automatically.

---

## Rules

### Architecture

* Controllers contain no business logic.
* Services contain business logic.
* Repositories contain data access only.
* PostgreSQL is the source of truth.
* Redis is cache only.

### Security

* JWT protects private APIs.
* BCrypt for passwords.
* Never expose sensitive data.

### Database

* Use Flyway migrations.
* Never modify schema manually.
* UUID primary keys only.

### API Response Format

Success:

{
"success": true,
"data": {}
}

Failure:

{
"success": false,
"message": "Human readable message"
}

### Coding Standards

* Java 21
* Spring Boot 3
* Constructor Injection
* DTOs for all API requests/responses
* No Entity exposure
* Proper exception handling
* SLF4J logging

---

## Feature Completion Checklist

A feature is complete only when:

* Code implemented
* Validation added
* Exception handling added
* Tested locally
* Progress tracker updated

---

## Stop Conditions

Stop immediately if:

* Architecture conflicts with implementation
* Build plan is unclear
* Database design is missing
* Same bug survives two attempts

Investigate before proceeding.
