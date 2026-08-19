# Djembe App — Backend

Spring Boot REST API for a djembe learning app: structured lessons, a traditional
rhythm library, and per-user progress/streak tracking. Pairs with a native Android
frontend that renders a playable virtual djembe.

## Stack
- Java 17, Spring Boot 3.3
- MongoDB (Spring Data MongoDB)
- Spring Security + JWT (jjwt)
- Lombok

## Project structure
```
src/main/java/com/djembe/app/
├── model/          User, Lesson, Rhythm, Progress (MongoDB documents)
├── repository/     Spring Data Mongo repositories
├── service/        AuthService, ProgressService
├── controller/     AuthController, LessonController, RhythmController, ProgressController
├── security/       JwtUtil, JwtAuthFilter
├── config/         SecurityConfig, GlobalExceptionHandler
└── dto/            RegisterRequest, LoginRequest, AuthResponse
```

## Running locally
1. Have MongoDB running locally on port 27017 (or update `application.yml`).
2. `mvn spring-boot:run`
3. API is live at `http://localhost:8080`

## Endpoints

### Auth (public)
- `POST /api/auth/register` — `{ username, email, password }` → returns JWT
- `POST /api/auth/login` — `{ email, password }` → returns JWT

### Lessons (public read)
- `GET /api/lessons` — all lessons, ordered by difficulty + sequence
- `GET /api/lessons/with-status` — same list, each entry flagged `unlocked: true/false`
  based on the caller's completed prerequisites (guest = only prerequisite-free
  lessons show as unlocked)
- `GET /api/lessons/difficulty/{level}` — e.g. `BEGINNER`
- `GET /api/lessons/{id}`
- `POST /api/lessons` — create (open for demo; lock behind admin role in production)

### Rhythms (public read)
- `GET /api/rhythms`
- `GET /api/rhythms/difficulty/{level}`
- `GET /api/rhythms/region/{region}`
- `GET /api/rhythms/{id}`
- `POST /api/rhythms`

### Progress (requires `Authorization: Bearer <token>`)
- `GET /api/progress/me`
- `POST /api/progress/lessons/{lessonId}/complete`
- `POST /api/progress/rhythms/{rhythmId}/master`

## Notes / next steps
- Passwords are hashed with BCrypt; JWT secret in `application.yml` must be
  replaced with a real secret (env var) before any real deployment.
- Lesson unlock logic (`prerequisiteLessonId`) is now enforced via
  `LessonService`: `GET /api/lessons/with-status` reports unlock state per
  lesson, and `POST /api/progress/lessons/{id}/complete` rejects completion
  (403) if the prerequisite isn't done yet.
- Audio sample files aren't served yet — plan is either static hosting (S3/Cloud
  Storage) with URLs stored on `Rhythm`/`Lesson`, or bundling core samples in
  the Android app itself for lowest latency, with the backend only serving
  extended/downloadable content.
- See `requirement-analysis-and-db-design.md` for the full requirements list
  and schema this backend implements.
