# Deployment Guide

This project is split into two separate repositories inside one folder:

- `FamilyBlock-Frontend`: Vite + React frontend
- `FamilyBlock-Backend`: Spring Boot + PostgreSQL backend

## Frontend

The frontend can run on Vercel as a static Vite application.

Recommended Vercel settings:

- Root directory: `FamilyBlock-Frontend`
- Build command: `npm run build`
- Output directory: `dist`

Set these environment variables in Vercel:

```env
VITE_BACKEND_URL=https://your-backend.onrender.com
VITE_FIREBASE_API_KEY=your-firebase-web-api-key
VITE_FIREBASE_PROJECT_ID=your-firebase-project-id
VITE_YOUTUBE_API_KEY=optional-youtube-api-key
```

`VITE_*` variables are included in the browser bundle. Do not put server secrets in them.

## Can The Backend Run On Vercel?

Not as-is.

The backend is a traditional Spring Boot server. It expects an always-on JVM process, a PostgreSQL connection pool, in-process scheduled jobs, async background work, and local paths for Firebase/download artifacts. Vercel is best suited here for the frontend and for serverless functions, not for this current backend shape.

The main blockers are:

- `@EnableScheduling` and `@Scheduled` jobs need a process that stays alive.
- The backend is a Java web server, not a Vercel Function or Next.js route handler.
- Free/serverless execution can stop between requests, so in-memory schedulers and async jobs are not reliable.
- Firebase Admin credentials and downloadable ZIP artifacts need to be provided as env/secret files or moved to object storage.

To run this backend on Vercel, you would need a larger rewrite: convert API routes to Vercel-compatible functions, move scheduled work to Vercel Cron or a worker, and move persistent files to storage such as Vercel Blob, S3, or Supabase Storage.

## Recommended Free Demo Deployment

Use:

- Render Web Service for the Spring Boot backend
- Supabase Postgres for the database
- Vercel for the frontend

Supabase hosts the database. It does not host the Java backend.

Important caveat: Render free web services can sleep when idle. That is acceptable for demos, but not ideal for your screen-time app because in-process scheduled jobs may not run while the service is asleep. For production, use an always-on paid backend or move scheduled jobs to an external cron/worker setup.

For a free demo, you can reduce cold starts by using an external health-check cron service to ping the backend every few minutes. This is not a production reliability strategy, but it can keep demos smoother.

Suggested free options:

- cron-job.org
- UptimeRobot
- Better Stack uptime monitoring

Configure the monitor like this:

- URL: `https://your-backend.onrender.com/`
- Method: `GET`
- Interval: every 5 or 10 minutes
- Expected status: `200`

Do not ping too aggressively. If the app becomes production-critical, use Render's paid always-on instance or move scheduled jobs to a proper cron/worker service instead of relying on keep-alive pings.

## Required Accounts And Secrets

Create or open these accounts before deploying:

- Vercel: hosts the frontend.
- Render: hosts the Spring Boot backend.
- Supabase: hosts PostgreSQL.
- Firebase: provides client auth and Firebase Admin credentials for the backend.
- Google AI Studio: provides the Gemini API key if Gemini categorization is enabled.
- OpenAI: optional, only needed if OpenAI code paths are enabled later.

Where to get each value:

- `VITE_BACKEND_URL`: deploy the backend on Render first, then copy the Render service URL from the Render service dashboard. It usually looks like `https://your-service-name.onrender.com`.
- `VITE_FIREBASE_API_KEY` and `VITE_FIREBASE_PROJECT_ID`: Firebase Console -> Project settings -> General -> Your apps -> Web app -> Firebase SDK config.
- `FIREBASE_ADMIN_SERVICE_ACCOUNT_JSON`: Firebase Console -> Project settings -> Service accounts -> Firebase Admin SDK -> Generate new private key. Open the downloaded JSON file and paste the full JSON content into the Render environment variable value.
- `DATABASE_URL`, `DATABASE_USERNAME`, and `DB_PASSWORD`: Supabase Dashboard -> Project Settings -> Database -> Connection string. Use the session pooler or direct connection details and convert the URL to the `jdbc:postgresql://...` format shown below.
- `GEMINI_KEY`: Google AI Studio -> Get API key -> Create API key.
- `OPEN_AI_KEY`: OpenAI Platform -> API keys -> Create new secret key. This is optional for the current app if OpenAI is unused.
- `VITE_YOUTUBE_API_KEY`: Google Cloud Console -> APIs & Services -> Credentials -> Create API key. This is optional if YouTube features are not enabled.
- `CORS_ALLOWED_ORIGINS`: use your Vercel frontend URL, plus `http://localhost:5173` for local development.

You do not need a Vercel token, Render API key, or Supabase access token when deploying manually through their dashboards. Those tokens are only needed for CLI or CI/CD automation.

## Deployment Order

Use this order so each app has the URLs and credentials it needs:

1. Create the Supabase Postgres project and save the database connection values.
2. Create Firebase client and Admin credentials.
3. Deploy the backend to Render with Supabase and Firebase env vars.
4. Copy the Render backend URL.
5. Deploy the frontend to Vercel with `VITE_BACKEND_URL` pointing at Render.
6. Update Render `CORS_ALLOWED_ORIGINS` with the final Vercel production URL.
7. Redeploy or restart the Render backend after changing CORS.

## Backend On Render

Create a new Render Web Service:

- Root directory: `FamilyBlock-Backend`
- Runtime: Java
- Build command: `./mvnw clean package -DskipTests`
- Start command: `java -jar target/manageYourMoney-0.0.1-SNAPSHOT.jar`
- Health check path: `/`

Render dashboard steps:

1. Render Dashboard -> New -> Web Service.
2. Connect the GitHub repository that contains `FamilyBlock-Backend`.
3. Choose the branch `config-env-deployment-docs` while testing this work.
4. Set root directory, build command, start command, and health check path as listed above.
5. Open Environment and add every backend variable listed below.
6. Click Create Web Service, then watch the deploy logs.
7. After the first successful deploy, copy the service URL and use it as `VITE_BACKEND_URL` in Vercel.

Set these Render environment variables:

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

For Firebase Admin credentials, prefer `FIREBASE_ADMIN_SERVICE_ACCOUNT_JSON` on Render. If you use a file instead, set `FIREBASE_ADMIN_SERVICE_ACCOUNT_PATH` to the deployed secret file path.

## Database On Supabase

Create a Supabase project and copy the direct PostgreSQL connection details.

Supabase dashboard steps:

1. Supabase Dashboard -> New project.
2. Save the database password you choose during project creation.
3. Go to Project Settings -> Database.
4. Copy the connection host, database name, port, user, and password.
5. If Supabase shows a URI, convert it to JDBC by changing `postgresql://` to `jdbc:postgresql://`.
6. Add `?sslmode=require` if the copied connection string does not already include SSL settings.

Use the JDBC format for Spring:

```env
DATABASE_URL=jdbc:postgresql://db.your-project-ref.supabase.co:5432/postgres?sslmode=require
DATABASE_USERNAME=postgres
DB_PASSWORD=your-supabase-database-password
```

Some Supabase connection strings use a pooler host and username like `postgres.your-project-ref`. Use the exact host, username, and password Supabase gives you, then convert the URL to the `jdbc:postgresql://...` format.

## Frontend On Vercel

Vercel dashboard steps:

1. Vercel Dashboard -> Add New -> Project.
2. Import the repository that contains `FamilyBlock-Frontend`.
3. Set Root Directory to `FamilyBlock-Frontend`.
4. Keep Framework Preset as Vite if Vercel detects it.
5. Set Build Command to `npm run build`.
6. Set Output Directory to `dist`.
7. Add the frontend environment variables from the `Frontend` section.
8. Deploy.
9. Copy the final production URL and add it to Render as part of `CORS_ALLOWED_ORIGINS`.

For preview deployments, add any Vercel preview URL that should call the backend to `CORS_ALLOWED_ORIGINS`, or use a stable custom domain for the frontend.

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

Spring Boot does not automatically read `.env` files in every environment. In local shells or hosting dashboards, make sure these variables are exported before starting the backend, or configure them directly in the platform.

## Local Docker Compose

The workspace root includes `docker-compose.yml` for local development with:

- PostgreSQL on `localhost:5432`
- Spring Boot backend on `localhost:8081`
- Vite frontend on `localhost:5173`

Before starting Compose, create both local env files:

```bash
cp FamilyBlock-Frontend/.env.example FamilyBlock-Frontend/.env
cp FamilyBlock-Backend/.env.example FamilyBlock-Backend/.env
```

Then add the required Firebase and API values to those `.env` files. The Compose file overrides the database connection to use the local `postgres` container, so you do not need Supabase for local Docker development.

Start the stack from the workspace root:

```bash
docker compose up --build
```

Open:

- Frontend: `http://localhost:5173`
- Backend health check: `http://localhost:8081/`
- Postgres: `localhost:5432`, database `family_block`, user `family_block`, password `family_block`

The backend repo also contains a branch-trackable copy at `FamilyBlock-Backend/docker-compose.yml`. From the backend directory, run:

```bash
docker compose up --build
```

## Makefile Commands

The workspace root includes a `Makefile` with shortcuts for Docker and verification:

```bash
make help
make setup-env
make up-build
make logs
make down
make verify
```

Common commands:

- `make setup-env`: creates missing local `.env` files from `.env.example`.
- `make up`: starts Docker services.
- `make up-build`: starts Docker services and rebuilds images if needed.
- `make down`: stops Docker services.
- `make logs-backend`: follows backend logs.
- `make logs-frontend`: follows frontend logs.
- `make logs-db`: follows Postgres logs.
- `make reset-db`: stops services and deletes the local Postgres volume.
- `make verify`: runs frontend build, lints changed API files, and packages the backend.

The backend repo also includes a branch-trackable `Makefile`. From `FamilyBlock-Backend`, the same commands work:

```bash
make up-build
make logs-backend
make down
```

## Production Notes

- Keep secrets out of Git. `.env` files are ignored; `.env.example` is safe to commit.
- Keep `CORS_ALLOWED_ORIGINS` in sync with your Vercel production and preview URLs.
- Consider changing `HIBERNATE_DDL_AUTO` from `update` to `validate` after the schema stabilizes.
- Move agent ZIP downloads to persistent object storage if they need to survive rebuilds or redeploys.
- For reliable daily/interval jobs, replace in-process schedulers with a hosted cron or worker before production use.
