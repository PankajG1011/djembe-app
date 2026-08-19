# Djembe Learning App — Requirement Analysis & Database Design

## 1. Project Overview
A mobile application for learning to play the djembe (West African hand drum),
combining a touch-playable virtual instrument, structured lessons, a
traditional rhythm library, and progress tracking. Built as a portfolio
project demonstrating full-stack ability (Spring Boot + MongoDB backend,
native Android frontend).

---

## 2. Requirement Analysis

### 2.1 Stakeholders / User Roles
| Role | Description |
|---|---|
| Guest | Can browse lessons and rhythms, play the virtual djembe, but progress isn't saved |
| Registered User | Guest capabilities + saved progress, streaks, personalized lesson unlocks |
| Admin (future) | Can create/edit lessons and rhythms (content management) |

### 2.2 Functional Requirements

**Authentication**
- FR1: User can register with username, email, password
- FR2: User can log in and receive a JWT for authenticated requests
- FR3: Passwords are stored hashed (BCrypt), never in plain text

**Playable Instrument**
- FR4: User can tap a virtual djembe head and hear bass/tone/slap sounds
  depending on where they tap
- FR5: Multi-touch supported (two fingers triggering independently)

**Lessons**
- FR6: User can browse lessons filtered by difficulty (Beginner/Intermediate/Advanced)
- FR7: Lessons unlock progressively — a lesson may have a prerequisite lesson
  that must be completed first
- FR8: User can mark a lesson complete; this updates their progress record

**Rhythm Library**
- FR9: User can browse traditional rhythms filtered by difficulty or region
- FR10: Each rhythm has a default tempo (BPM) and optional individual
  instrument-part audio stems (djembe 1, djembe 2, dundun, etc.)
- FR11: User can mark a rhythm as "mastered"

**Progress Tracking**
- FR12: System tracks which lessons are completed and which rhythms are mastered, per user
- FR13: System tracks a daily practice streak (increments on consecutive days of activity)

### 2.3 Non-Functional Requirements
| ID | Requirement |
|---|---|
| NFR1 | Audio trigger latency must feel instantaneous (<~50ms) for the instrument to feel playable |
| NFR2 | API responses for content browsing (lessons/rhythms) should not require auth, to reduce friction for new users |
| NFR3 | Passwords/JWT secrets must never be stored in plain text or committed to source control in a real deployment |
| NFR4 | Backend must support horizontal read scaling for content endpoints (lessons/rhythms are read-heavy, low-write) |
| NFR5 | Data model must allow adding new content (lessons, rhythms) without app store updates (content lives server-side, not hardcoded in the app) |

### 2.4 Out of Scope (for v1)
- Live/streamed lessons (competitors like DJEMBE FLOW have this — noted as a future differentiator, not v1)
- Social features (community chat, leaderboards)
- Payments/subscriptions
- Admin role enforcement (endpoints exist but aren't yet locked to ADMIN only)

---

## 3. Database Design

MongoDB is document-based (no foreign keys/joins like SQL), so "relationships"
here are either **embedded** (data nested inside a document) or
**referenced** (one document stores another's ID, and the app/service layer
resolves it — similar conceptually to a foreign key).

### 3.1 Collections (≈ Tables) Overview
| Collection | Purpose |
|---|---|
| `users` | Registered accounts, credentials, streak state |
| `lessons` | Structured lesson content, with a self-referencing prerequisite |
| `rhythms` | Traditional rhythm library content |
| `progress` | Per-user completion state — references `users`, `lessons`, `rhythms` |

### 3.2 `users`
| Field | Type | Notes |
|---|---|---|
| `_id` | ObjectId | Primary key |
| `username` | String | Required |
| `email` | String | Required, unique, indexed |
| `passwordHash` | String | BCrypt hash, never plain text |
| `roles` | Set\<String\> | e.g. `["USER"]`, `["USER","ADMIN"]` |
| `createdAt` | Instant | |
| `practiceStreak` | Int | Consecutive days practiced |
| `lastPracticeDate` | Instant | Used to compute streak continuation |

### 3.3 `lessons`
| Field | Type | Notes |
|---|---|---|
| `_id` | ObjectId | Primary key |
| `title` | String | |
| `description` | String | |
| `difficulty` | String | `BEGINNER` / `INTERMEDIATE` / `ADVANCED` |
| `sequenceOrder` | Int | Order within its difficulty tier |
| `contentUrl` | String | Video/audio lesson content |
| `prerequisiteLessonId` | String (ref → `lessons._id`) | Self-referencing — null for a tier's first lesson |
| `techniquesCovered` | List\<String\> | e.g. `["bass","tone"]` |

### 3.4 `rhythms`
| Field | Type | Notes |
|---|---|---|
| `_id` | ObjectId | Primary key |
| `name` | String | e.g. "Kuku", "Djole" |
| `region` | String | e.g. "Guinea" |
| `description` | String | |
| `defaultBpm` | Int | |
| `difficulty` | String | `BEGINNER` / `INTERMEDIATE` / `ADVANCED` |
| `audioTrackUrl` | String | Full-ensemble play-along track |
| `partStemUrls` | Map\<String,String\> | Individual instrument parts, e.g. `{"djembe1": url}` |
| `tags` | List\<String\> | |

### 3.5 `progress`
| Field | Type | Notes |
|---|---|---|
| `_id` | ObjectId | Primary key |
| `userId` | String (ref → `users._id`) | Indexed — one progress doc per user |
| `completedLessonIds` | Set\<String\> (ref → `lessons._id`) | |
| `masteredRhythmIds` | Set\<String\> (ref → `rhythms._id`) | |
| `lastUpdated` | Instant | |
| `totalPracticeMinutes` | Long | Reserved for future analytics |

### 3.6 How Data Flows Between Collections
```
users (1) ────────< progress (1)
                        │
                        ├──< references completedLessonIds ──> lessons
                        └──< references masteredRhythmIds  ──> rhythms

lessons (self-referencing) ── prerequisiteLessonId ──> lessons
```

- A **User** has exactly one **Progress** document (`progress.userId` is a
  unique reference back to `users._id`).
- **Progress** doesn't embed full lesson/rhythm objects — only their IDs. The
  backend resolves these IDs against `lessons`/`rhythms` when the client needs
  full details (avoids duplicating/desyncing content data).
- **Lessons** reference themselves via `prerequisiteLessonId` to express
  ordering/unlock chains without a separate join table.
- Content collections (`lessons`, `rhythms`) are independent of each other —
  no direct relationship — they're both just referenced by `progress`.

### 3.7 Why This Shape (Design Rationale)
- **Progress is separate from User** rather than embedded, because progress
  data grows and changes independently (frequent writes) while user auth data
  changes rarely — keeping them separate avoids rewriting the whole user
  document on every lesson completion.
- **IDs, not embedded copies**, are stored in `progress` for
  `completedLessonIds`/`masteredRhythmIds`, so lesson/rhythm content can be
  edited later (e.g. fixing a typo in a lesson title) without needing to
  update every user's progress record.
- **Self-referencing `prerequisiteLessonId`** keeps lesson-chain logic in one
  collection instead of a separate "lesson order" table, which fits MongoDB's
  denormalized style better than a strict relational join would.

---

## 4. Next Steps (once this is confirmed)
1. Backend: implement collections above exactly as designed (mostly matches
   the earlier scaffold — `Progress` model already matches this design)
2. Add server-side enforcement of `prerequisiteLessonId` unlock logic (FR7 —
   noted earlier as modeled but not yet enforced)
3. Frontend: build screens once backend contracts are stable, so the Android
   app is built against a finished, agreed-upon API rather than a moving target
