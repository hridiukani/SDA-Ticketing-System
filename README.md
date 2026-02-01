# SDA Ticketing System

Full-stack IT Ticket Management System for an office helpdesk.

## Tech Stack

- Backend: Java 17, Spring Boot 3, Spring Web, Spring Data JPA, Spring Security (JWT), Flyway, PostgreSQL, WebSockets, Jakarta Validation, Springdoc OpenAPI.
- Frontend: React + Vite, TypeScript, React Router, Tailwind CSS, TanStack Query.

## Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 18+ and npm or pnpm
- Docker (for Postgres via docker-compose)

## Running the backend

1. Start PostgreSQL:

   ```bash
   docker-compose up -d postgres
   ```

2. Configure environment (optional – defaults are fine for local dev):

   Copy `backend/.env.example` to `.env` and adjust as needed.

3. Run the Spring Boot app:

   ```bash
   cd backend
   mvn spring-boot:run
   ```

   Backend will listen on `http://localhost:8080`.

4. API docs:

   Open Swagger UI at `http://localhost:8080/swagger-ui/index.html`.

## Running the frontend

1. Install dependencies:

   ```bash
   cd frontend
   npm install
   ```

2. Configure environment (optional):

   Copy `.env.example` to `.env` and adjust `VITE_API_BASE_URL` / `VITE_WEBSOCKET_URL` if needed.

3. Start the dev server:

   ```bash
   npm run dev
   ```

   Frontend will run on `http://localhost:5173` and talk to the backend on `http://localhost:8080`.

## Default data

On first startup Flyway creates schema and seed data:

- Users (password hashes are placeholders; replace with real BCrypt values in `V1__init.sql`):
  - `admin@example.com` – role `ADMIN`
  - `tech@example.com` – role `TECHNICIAN`
  - `requester@example.com` – role `REQUESTER`

- Categories:
  - Hardware, Software, Network

## Key endpoints (selection)

- Auth:
  - `POST /api/auth/login` → `{ accessToken }`
- Tickets:
  - `GET /api/tickets` (pagination + filters)
  - `POST /api/tickets`
  - `GET /api/tickets/{id}`
  - `POST /api/tickets/{id}/claim`
  - `POST /api/tickets/{id}/assign`
  - `POST /api/tickets/{id}/status`
  - `POST /api/tickets/{id}/priority`
  - `POST /api/tickets/{id}/close`
  - `POST /api/tickets/{id}/reopen`
- Comments:
  - `POST /api/tickets/{id}/comments`
  - `GET /api/tickets/{id}/comments`
- Attachments:
  - `POST /api/tickets/{id}/attachments`
  - `GET /api/tickets/{id}/attachments`
  - `GET /api/attachments/{id}/download`
- Admin:
  - `GET /api/admin/users`
  - `GET/POST /api/admin/categories`
  - `GET/POST /api/admin/sla`
- Reports:
  - `GET /api/reports/tickets-by-status`
  - `GET /api/reports/tickets-by-priority`
  - `GET /api/reports/summary`
- AI suggestions (stub):
  - `POST /api/ai/suggest`

## WebSockets

- STOMP endpoint: `ws://localhost:8080/ws`
- Topic for queue updates: `/topic/queue-updates`

Frontend subscribes via `frontend/src/modules/ws/queueSocket.ts` and refreshes the tech queue automatically.

## Notes

- Email notifications are controlled via `notifications.email.enabled` (see `backend/.env.example` and `application.yml`).
- File uploads are stored locally under `./uploads` by default; the `FileStorageService` is designed so S3-compatible storage can be added later.
