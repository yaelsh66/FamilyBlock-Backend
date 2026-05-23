# Deployment Guide

Separate repos, same folder:

- `FamilyBlock-Frontend`: Vite + React frontend
- `FamilyBlock-Backend`: Spring Boot + PostgreSQL backend
- `FamilyBlock-Agent`: Windows service on child devices

## Recommended Free Demo Stack

- Frontend: Vercel
- Backend: Render Web Service
- Database: Supabase Postgres
- Agent: local Windows install

Supabase = Postgres only. Not the Java backend.

## Deploy Order

1. Supabase Postgres
2. Firebase (client auth + Admin SDK)
3. Render backend
4. Copy Render backend URL
5. Vercel frontend
6. Update Render `CORS_ALLOWED_ORIGINS`
7. Restart/redeploy backend
8. Windows agent on child devices

---

## 1. Database (Supabase)

1. Supabase Dashboard → New project
2. Save the database password
3. Project Settings → Database
4. Copy host, database, port, user, password
5. `postgresql://` → `jdbc:postgresql://`
6. Add `?sslmode=require` if missing

```env
DATABASE_URL=jdbc:postgresql://db.your-project-ref.supabase.co:5432/postgres?sslmode=require
DATABASE_USERNAME=postgres
DB_PASSWORD=your-supabase-database-password
```

Pooler host/username (e.g. `postgres.your-project-ref`): use Supabase values as-is, JDBC format.

Used by: Render backend env (`DATABASE_URL`, `DATABASE_USERNAME`, `DB_PASSWORD`).

---

## 2. Firebase

### Create project

1. Firebase Console → Add project
2. Disable Google Analytics if you don't need it

### Enable auth

1. Build → Authentication → Get started
2. Sign-in method → Email/Password → Enable → Save

The frontend uses Firebase email/password auth via the Identity Toolkit REST API.

### Web app (frontend)

1. Project settings → General → Your apps → Add app → Web
2. Register app name
3. Copy from SDK config:
   - `apiKey` → `VITE_FIREBASE_API_KEY`
   - `projectId` → `VITE_FIREBASE_PROJECT_ID`

Used by: Vercel frontend env.

### Admin SDK (backend)

1. Project settings → Service accounts
2. Firebase Admin SDK → Generate new private key
3. Download JSON
4. Paste full JSON content into Render env as `FIREBASE_ADMIN_SERVICE_ACCOUNT_JSON`

Used by: Render backend env. Backend verifies Firebase ID tokens on `/api/**`.

Prefer `FIREBASE_ADMIN_SERVICE_ACCOUNT_JSON` on Render. File fallback: `FIREBASE_ADMIN_SERVICE_ACCOUNT_PATH`.

### After frontend deploy

1. Authentication → Settings → Authorized domains
2. Add your Vercel production domain (e.g. `your-app.vercel.app`)

---

## 3. Backend (Render)

Render does not offer a native Java runtime. Deploy this repo with **Docker**.

- Root directory: `.` (repository root)
- Language: **Docker**
- Dockerfile path: `Dockerfile`
- Build command: leave empty (Docker builds the image)
- Start command: leave empty (defined in the Dockerfile)
- Health check path: `/`

Render steps:

1. New → Web Service
2. Connect this repository
3. Branch: `config-env-deployment-docs` (while testing)
4. Set language to **Docker** and leave build/start commands empty
5. Add env vars below (Supabase + Firebase from steps 1–2)
6. Create → watch logs
7. Copy service URL → used as `VITE_BACKEND_URL` and `FAMILYBLOCK_BACKEND_URL`

```env
SPRING_PROFILES_ACTIVE=prod
PORT=8081
SERVER_ADDRESS=0.0.0.0
DATABASE_URL=jdbc:postgresql://your-supabase-host:5432/postgres?sslmode=require
DATABASE_USERNAME=postgres.your-project-ref
DB_PASSWORD=your-supabase-database-password
CORS_ALLOWED_ORIGINS=https://your-frontend.vercel.app,http://localhost:5173
FIREBASE_ADMIN_SERVICE_ACCOUNT_JSON={"type":"service_account", "...":"..."}
GEMINI_KEY=your-gemini-api-key
GEMINI_BASE_URL=https://generativelanguage.googleapis.com/v1beta/
GEMINI_MODEL=models/gemini-2.5-flash
OPEN_AI_KEY=optional-openai-key
OPENAI_API_URL=https://api.openai.com/v1/chat/completions
OPENAI_MODEL=gpt-4o
DOWNLOADS_PATH=/opt/render/project/src/downloads
DOWNLOADS_UI_PATH=/opt/render/project/src/downloads
DOWNLOADS_AGENT_INSTALLER_PATH=/opt/render/project/src/downloads
HIBERNATE_DDL_AUTO=update
JPA_SHOW_SQL=false
```

### Render free tier

Services sleep when idle. In-process schedulers won't run while asleep. Fine for demos; not production.

Keep-alive for demos only (not production reliability):

- cron-job.org, UptimeRobot, Better Stack
- `GET https://your-backend.onrender.com/` every 5–10 min, expect `200`
- Don't ping aggressively; use paid always-on or external cron/worker for prod

---

## 4. Copy Backend URL

After Render deploy succeeds, copy the service URL (e.g. `https://your-service.onrender.com`).

Used as:

- `VITE_BACKEND_URL` on Vercel
- `FAMILYBLOCK_BACKEND_URL` on Windows agent

---

## 5. Frontend (Vercel)

Static Vite SPA.

Settings:

- Root directory: `FamilyBlock-Frontend`
- Build command: `npm run build`
- Output directory: `dist`

```env
VITE_BACKEND_URL=https://your-backend.onrender.com
VITE_FIREBASE_API_KEY=your-firebase-web-api-key
VITE_FIREBASE_PROJECT_ID=your-firebase-project-id
VITE_YOUTUBE_API_KEY=optional-youtube-api-key
```

`VITE_*` = browser bundle. No server secrets.

Vercel steps:

1. Add New → Project
2. Import `FamilyBlock-Frontend` repo
3. Root Directory: `FamilyBlock-Frontend`
4. Framework: Vite
5. Build: `npm run build`, Output: `dist`
6. Add env vars above (backend URL from step 4, Firebase from step 2)
7. Deploy
8. Add Vercel domain to Firebase Authorized domains (step 2)

Preview URLs: add to `CORS_ALLOWED_ORIGINS` or use a stable custom domain.

---

## 6. Update CORS

Add Vercel production URL to Render backend env:

```env
CORS_ALLOWED_ORIGINS=https://your-frontend.vercel.app,http://localhost:5173
```

Include preview URLs if needed.

---

## 7. Restart Backend

Restart or redeploy Render backend after CORS change.

---

## 8. Windows Agent

Repo: `FamilyBlock-Agent`. Local Windows only. Not Vercel/Render/Supabase.

```env
FAMILYBLOCK_BACKEND_URL=https://your-backend.onrender.com
DEVICE_ID=child-device-id
DEVICE_SECRET=device-password-from-parent-ui
FAMILYBLOCK_CONFIG_PATH=C:\FamilyBlockService\config.json
```

Device credentials: parent login → device management → add device → copy ID/password → `.env` or `config.json`.

Build/install:

1. Open `FamilyBlockService.sln` in Visual Studio 2022
2. Build `Release | x64`
3. Install/run service as admin

Local Docker backend:

```env
FAMILYBLOCK_BACKEND_URL=http://localhost:8081
```

Agent details: `FamilyBlock-Agent/DEPLOYMENT.md`

---

## Backend on Vercel?

No, as-is.

Spring Boot needs always-on JVM, PostgreSQL pool, in-process schedulers, async work, Firebase/download file paths. Vercel fits the frontend and serverless functions, not this backend.

Blockers:

- `@EnableScheduling` / `@Scheduled` need a live process
- Java web server, not a Vercel Function or Next.js route handler
- Serverless sleep breaks in-process schedulers and async jobs
- Firebase Admin + ZIP downloads need env/secrets or object storage

Rewrite path: Vercel Functions, Vercel Cron/worker, Vercel Blob/S3/Supabase Storage.

## Accounts & Optional Secrets

- Vercel: frontend
- Render: backend
- Supabase: Postgres
- Firebase: client auth + Admin SDK
- Google AI Studio: `GEMINI_KEY` (if enabled)
- OpenAI: `OPEN_AI_KEY` (optional, unused currently)
- GCP: `VITE_YOUTUBE_API_KEY` (optional)

Dashboard deploy = no Vercel/Render/Supabase API tokens. Tokens only for CLI/CI.

## Local Development

Frontend:

```bash
cd FamilyBlock-Frontend
cp .env.example .env
npm install
npm run dev
```

Backend:

```bash
cd FamilyBlock-Backend
cp .env.example .env
./mvnw spring-boot:run
```

Spring Boot doesn't auto-load `.env` everywhere — export vars or set in platform.

## Local Docker Compose

Root `docker-compose.yml`:

- PostgreSQL: `localhost:5432`
- Backend: `localhost:8081`
- Frontend: `localhost:5173`

```bash
cp FamilyBlock-Frontend/.env.example FamilyBlock-Frontend/.env
cp FamilyBlock-Backend/.env.example FamilyBlock-Backend/.env
```

Fill Firebase/API secrets. Compose overrides DB to local `postgres` — no Supabase needed.

```bash
docker compose up --build
```

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8081/`
- Postgres: `localhost:5432`, database `family_block`, user `family_block`, password `family_block`

Also: `FamilyBlock-Backend/docker-compose.yml` — `docker compose up --build` from backend dir.

## Makefile

Root `Makefile`:

```bash
make help
make setup-env
make up-build
make logs
make down
make verify
```

- `setup-env`: `.env` from `.env.example` (frontend, backend, agent)
- `up` / `up-build`: Docker services
- `down`: stop services
- `logs-backend` / `logs-frontend` / `logs-db`
- `reset-db`: stop + delete Postgres volume
- `verify`: frontend build + API eslint + backend package

`FamilyBlock-Backend/Makefile` — same from backend dir.

## Production Notes

- Secrets out of Git (`.env` ignored, `.env.example` OK)
- `CORS_ALLOWED_ORIGINS` sync with Vercel prod + preview URLs
- Firebase Authorized domains sync with Vercel URLs
- `HIBERNATE_DDL_AUTO`: `update` → `validate` after schema stable
- Agent ZIPs: object storage if rebuilds wipe them
- Schedulers: hosted cron/worker for reliable daily/interval jobs
