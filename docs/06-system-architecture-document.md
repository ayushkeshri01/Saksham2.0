# System Architecture Document (SAD) — PartLog

**Version:** 1.0  
**Date:** July 2026  
**Prepared by:** Founder / Solutions Architect

---

## 1. Architecture Overview

### 1.1 Goals

- **Offline-first:** Mechanics must be able to log entries without internet connectivity
- **Simple to operate:** Single developer must be able to manage the entire infrastructure
- **Cost-predictable:** Azure costs must be forecastable within ~₹16,200/month
- **Secure by default:** All data encrypted in transit, access controlled, audit trailed
- **Scale-adequate:** Handle 500–2000 mechanics, 100–500 entries/day at 12 months

### 1.2 Architecture Principles

1. **Monolith first** — No microservices. A single Express API server is simpler to build, deploy, and debug.
2. **Managed services over self-managed** — Use Azure PaaS (App Service, PostgreSQL Flexible Server, Blob Storage) to reduce operational burden.
3. **Stateless API** — All server state in database or tokens. App Service can be restarted without data loss.
4. **Offline as first-class** — Mobile app architecture is designed around offline entry with eventual sync.
5. **Write-once, read-often** — Database queries are optimised for the read-heavy admin portal pattern.

### 1.3 Key Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| API architecture | Monolith (Express.js) | One developer; simpler than microservices |
| Database | PostgreSQL 16 (Azure Flexible Server) | Prisma support; JSONB for future flexibility; managed |
| Object storage | Azure Blob Storage | Integrated with Azure ecosystem; SAS tokens for secure access |
| Mobile framework | React Native + Expo SDK 51 | Cross-platform potential; managed build pipeline; OTA updates |
| Admin portal | Next.js 14 (App Router) | SSR for dashboard; RSC for data loading; Vercel deployment |
| Auth (mechanic) | JWT + refresh tokens | Token-based, works with offline mobile architecture |
| Auth (admin) | Session cookies (HttpOnly) | Traditional web auth; NextAuth.js handles this well |
| ORM | Prisma 5 | Type-safe, auto-generated types, migrations, connection pooling |
| Validation | Zod 3 | Runtime type safety; shared schemas between API and web |
| CI/CD | GitHub Actions + Vercel | Free tier adequate; single pipeline for API |
| APK distribution | Google Play internal testing | Managed distribution; update via EAS Update |
| Monitoring | Azure Application Insights + Azure Monitor | Native Azure integration; free tier adequate for scale |

---

## 2. System Context Diagram

```
+------------------+          +---------------------------+
|                  |          |                           |
|  Mobile App      |  HTTPS   |    External Services     |
|  (React Native)  |<-------->|                           |
|  - Expo SDK 51   |          |  +---------------------+  |
|  - expo-sqlite   |          |  | MSG91 (SMS OTP)     |  |
|  - expo-camera   |          |  +---------------------+  |
|  - expo-location |          |                           |
|  - i18next       |          |  +---------------------+  |
|                  |          |  | Expo Push API       |  |
+------------------+          |  +---------------------+  |
         |                    |                           |
         | HTTPS / SAS URLs   |  +---------------------+  |
         v                    |  | Google Play Store   |  |
+------------------+          |  +---------------------+  |
|                  |          |                           |
|  Azure Blob      |          +---------------------------+
|  Storage         |
|  (photos / audio)|
|                  |
+------------------+

         ^
         | HTTPS (upload via SAS)
         |
+------------------+          +------------------+
|                  |  HTTPS    |                  |
|  Admin Web       |<-------->|  Backend API      |
|  Portal          |          |  (Express.js)     |
|  (Next.js 14)    |          |  - Prisma ORM     |
|  - Vercel        |          |  - Zod validation |
|  - Tailwind CSS  |          |  - JWT + sessions |
|                  |          |  - Azure App Srvc |
+------------------+          +--------+---------+
                                        |
                                  HTTPS | (Prisma)
                                        v
                               +--------+---------+
                               |                  |
                               |  PostgreSQL 16   |
                               |  Azure Flexible  |
                               |  Server (B2ms)   |
                               |  + PgBouncer     |
                               |                  |
                               +------------------+
```

---

## 3. Component Architecture

### 3.1 Android Mobile App

#### Internal Architecture

```
+-------------------------------------------+
|               Screens                      |
|  LangSelect | MobileEntry | OTPVerify     |
|  Onboarding | Home        | PartTypeSelect |
|  CondenserFlow | CompressorFlow           |
|  Earnings   | Settings    | EntryDetail    |
+-------------------------------------------+
         |                    ^
         v                    |
+----------------------------+-----------+
|         Navigation (React Navigation)    |
+-----------------------------------------+
         |                    ^
         v                    |
+----------------------------+-----------+
|      State Management (Zustand)         |
|  authStore | entriesStore | syncStore    |
|  uiStore   | languageStore              |
+-----------------------------------------+
         |                    ^
         v                    |
+----------------------------+-----------+
|        Services Layer                    |
|  apiClient (Axios + token refresh)       |
|  syncManager (background queue)          |
|  storageService (expo-sqlite)            |
|  notificationService (expo-notifications)|
|  locationService (expo-location)         |
|  mediaService (camera, audio)            |
+-----------------------------------------+
         |                    ^
         v                    |
+----------------------------+-----------+
|         Local Database (SQLite)          |
|  - entries (synced + unsynced)           |
|  - sync_queue                            |
|  - pending_photos                        |
|  - app_settings                          |
|  - model_cache (autocomplete data)       |
+-----------------------------------------+
```

#### Sync Queue Logic

```
Entry Created (online or offline)
    |
    v
Save to SQLite (sync_status = 'pending')
    |
    v
Is online?
  YES -> Upload immediately:
           1. Get SAS URLs from server for each photo/audio
           2. Upload files directly to Azure Blob Storage
           3. Submit entry JSON to POST /api/sync
           4. On success: sync_status = 'synced'
           5. On failure: increment retry_count; set next_retry_at
  NO  -> Queue in SQLite; wait for background sync
           1. background-fetch fires every 5 min
           2. Check network state
           3. If online, process queue FIFO
           4. Exponential backoff: 1min, 5min, 15min, 1hr, 6hr
           5. After 24hr failed: notify mechanic "Sync failed, tap to retry"
```

### 3.2 Backend API

#### Middleware Stack (Order)

```
1. express.json (limit: 50MB for sync payloads)
2. express-rate-limit (100 req/min mechanic, 500 req/min admin)
3. cors (whitelist origins)
4. helmet (security headers)
5. morgan (request logging)
6. Auth middleware:
   - mechanicAuth (JWT verification + refresh)
   - adminAuth (session cookie verification)
7. Zod validation middleware (per-route schemas)
8. Route handlers
9. Error handler (global, catches all unhandled errors)
```

#### Route Structure

```
/api/auth/mechanic
  POST /send-otp       -- Send OTP via MSG91
  POST /verify-otp     -- Verify OTP, return JWT tokens
  POST /refresh        -- Refresh access token
  POST /register       -- Complete onboarding

/api/auth/admin
  POST /login          -- Email + password login
  POST /logout         -- Clear session

/api/entries
  POST /               -- Create entry (sync)
  GET /                -- List entries (admin, paginated/filtered)
  GET /:id             -- Get entry detail
  PATCH /:id/approve   -- Approve entry (admin)
  PATCH /:id/reject    -- Reject entry (admin)
  POST /bulk-reject    -- Bulk reject (admin)
  GET /duplicate-check -- Check duplicate (reg + part_type + 30 days)

/api/mechanics
  GET /                -- List mechanics (admin)
  GET /:id             -- Mechanic detail (admin)
  PATCH /:id/deactivate
  PATCH /:id/reactivate

/api/payout-rates
  GET /                -- Current and historical rates
  POST /               -- Set new rate (admin)

/api/payouts
  GET /monthly         -- Monthly payout summary (admin)
  GET /mechanic        -- Mechanic's own payout history
  POST /mark-paid      -- Mark payout as paid (admin)

/api/analytics
  GET /dashboard       -- KPI numbers for admin dashboard
  GET /by-model        -- Entry count grouped by vehicle model
  GET /by-state        -- Entry count grouped by state
  GET /by-cause        -- Entry count grouped by failure cause
  GET /by-fuel         -- Entry count grouped by fuel type
  GET /daily-volume    -- Entry count by day for last 30 days

/api/media
  POST /generate-upload-url  -- Generate SAS URL for photo/audio upload
  GET /generate-download-url -- Generate SAS URL for photo/audio view

/api/export
  POST /entries        -- Generate CSV/Excel of entries
  POST /payouts        -- Generate CSV/Excel of payout summary

/api/mechanic-profile
  GET /                -- Get own profile
  PATCH /              -- Update profile (name, workshop, city, state, UPI)
  POST /push-token     -- Register Expo push token

/api/models
  GET /                -- List known vehicle models (for autocomplete)
```

### 3.3 Database Layer

- **Connection pooling:** PgBouncer (integrated with Azure PostgreSQL Flexible Server)
- **Pool size:** 20 connections (adequate for B2ms tier)
- **Query patterns:**
  - Write path: Single entry insert with related photos in transaction
  - Sync path: Batch insert (up to 10 entries per sync)
  - Read path (admin): Filtered + paginated queries on entries table with JOINs
  - Analytics path: Aggregation queries with GROUP BY on materialised or indexed columns
- **Prisma:** Schema defined in Prisma; migrations run as part of CI/CD

### 3.4 File Storage Layer

#### Upload Flow

```
1. Mobile app requests SAS URL from POST /api/media/generate-upload-url
   Request: { filename: "damage.jpg", contentType: "image/jpeg", entryId: "uuid" }
   Response: { uploadUrl: "https://...sas-token...", blobPath: "photos/2026/07/..." }

2. Mobile app uploads file directly to Azure Blob Storage using SAS URL
   PUT {uploadUrl} with binary data + x-ms-blob-type: BlockBlob

3. On success, mobile app includes blobPath in entry JSON payload to POST /api/entries

4. Backend validates file exists at blobPath, records metadata in Photo table
```

#### Download Flow

```
1. Admin web portal requests image URL from GET /api/media/generate-download-url
   Request: query { blobPath: "photos/2026/07/..." }
   Response: { downloadUrl: "https://...sas-token...", expiresAt: "..." }

2. Frontend loads image from signed URL (1-hour expiry)

3. If expired, frontend requests new URL (transparent to user)
```

### 3.5 Web Portal Architecture

- **Framework:** Next.js 14 with App Router
- **Data fetching pattern:** Mix of SSR (initial page load) + client-side (TanStack Query for interactivity)
- **Auth:** NextAuth.js with Credentials provider; session stored in HttpOnly cookie
- **Charts:** Recharts (lightweight, React-native compatible API)
- **Table:** TanStack Table (headless, fully customisable)
- **Styling:** Tailwind CSS with design tokens matching brand guidelines
- **Hosting:** Vercel (Pro tier if needed, Hobby tier sufficient initially)

---

## 4. Data Flow Diagrams

### 4.1 Mechanic Registration

```
Mobile App                      Backend API                MSG91             Database
    |                              |                         |                  |
    |-- POST /send-otp ----------->|                         |                  |
    |   { mobile: "98XXXXXXXX" }  |                         |                  |
    |                              |-- Send OTP -------->   |                  |
    |                              |   to MSG91 API          |                  |
    |                              |                         |-- SMS to mobile  |
    |                              |-- Store hashed OTP ---->|                  |
    |                              |   + expiry + attempts   |                  |
    |<-- { success: true } --------|                         |                  |
    |                              |                         |                  |
    |-- POST /verify-otp --------->|                         |                  |
    |   { mobile, otp: "123456" } |                         |                  |
    |                              |-- Verify OTP hash ---->|                  |
    |                              |   + check expiry        |                  |
    |                              |   + check attempts      |                  |
    |                              |<-- valid/invalid ------ |                  |
    |                              |                         |                  |
    |<-- { token, isNewUser } -----|                         |                  |
    |                              |                         |                  |
    | (if new)                                              |                  |
    |-- POST /register ----------->|                         |                  |
    |   { name, workshop, city,   |                         |                  |
    |     state, language }       |                         |                  |
    |                              |-- INSERT mechanic ---->|                  |
    |<-- { mechanic } -------------|                         |                  |
```

### 4.2 Entry Submission (Online)

```
Mobile App                      Backend API              Azure Blob          Database
    |                              |                         |                  |
    | Step 1: Get SAS URLs        |                         |                  |
    |-- POST /generate-upload-url-->|                       |                  |
    |   (for each photo + audio)  |-- Generate SAS -------->|                  |
    |<-- [{uploadUrl, blobPath}] --|<-- SAS token --------- |                  |
    |                              |                         |                  |
    | Step 2: Upload files        |                         |                  |
    |-- PUT {uploadUrl} ---------->|                         |                  |
    |   (binary photo data)        |   (direct upload)      |                  |
    |                              |-- Store blob ---------> |                  |
    |<-- 201 Created --------------|<-- OK ------------------|                  |
    |                              |                         |                  |
    | Step 3: Submit entry        |                         |                  |
    |-- POST /entries ------------>|                         |                  |
    |   { entry JSON + blobPaths } |                         |                  |
    |                              |-- INSERT entry ------->|                  |
    |                              |   + photos + auto-approve|                 |
    |<-- { entry with id } --------|                         |                  |
```

### 4.3 Entry Submission (Offline + Sync)

```
Mobile App (offline)            Mobile App (later, online)   Backend API
    |                              |                              |
    | Save to SQLite              |                              |
    | sync_status = 'pending'     |                              |
    |                              |                              |
    | ... time passes ...          |                              |
    |                              |                              |
    | expo-background-fetch fires  |                              |
    | (every 5 min)                |                              |
    |                              |                              |
    | Check network: ONLINE        |                              |
    |                              |                              |
    | For each pending entry:     |                              |
    |   Get SAS URLs               |-- POST /generate-upload-url->|
    |                              |<-- URLs -------------------|
    |   Upload photos (direct)    |                              |
    |   Submit entry JSON          |-- POST /sync -------------->|
    |                              |   { entries: [...] }        |
    |                              |<-- { synced: [...] } -------|
    |   Mark synced in SQLite     |                              |
```

### 4.4 Admin Approval / Rejection

```
Admin Web Portal                Backend API                   Database
    |                              |                              |
    | View entries table           |                              |
    | Click entry for detail       |                              |
    |                              |                              |
    | Click "Reject"              |                              |
    | Select reason from dropdown  |                              |
    | Optional note                |                              |
    |                              |                              |
    |-- PATCH /entries/:id/reject->|                              |
    |   { reason, note }          |                              |
    |                              |-- UPDATE entry status ------>|
    |                              |   approval_status = 'rejected'|
    |                              |-- INSERT audit_log --------->|
    |                              |                              |
    |<-- { updated entry } --------|                              |
    |                              |                              |
    | (Entry removed from payout   |                              |
    |  calculation automatically)  |                              |
```

### 4.5 Monthly Payout Generation

```
Admin Web Portal                Backend API                   Database
    |                              |                              |
    | Open payout page             |                              |
    | Select month: June 2026     |                              |
    |                              |                              |
    |-- GET /payouts/monthly ----->|                              |
    |   { year: 2026, month: 6 }  |                              |
    |                              |-- SELECT mechanics          |
    |                              |   For each mechanic:        |
    |                              |     Count approved entries  |
    |                              |     in month                |
    |                              |     Calculate amount        |
    |                              |     (condenser_count * rate)|
    |                              |     + (compressor * rate)   |
    |                              |-- RETURN payout array       |
    |                              |                              |
    |<-- [{mechanic, condenser,    |                              |
    |     compressor, amount,      |                              |
    |     upiId, status}] ---------|                              |
    |                              |                              |
    | Click "Mark Paid"           |                              |
    | Enter UPI transaction ref   |                              |
    |                              |                              |
    |-- POST /payouts/mark-paid -->|                              |
    |   { mechanicId, month,      |                              |
    |     year, transactionRef }  |                              |
    |                              |-- INSERT/UPDATE MonthlyPayout|
    |                              |-- INSERT audit_log          |
    |                              |-- Send push notification    |
    |<-- { success } --------------|                              |
```

---

## 5. Azure Infrastructure Specification

### Resource Group: `rg-partlog-prod`

| Service | Tier | Spec | Monthly Cost (₹) |
|---------|------|------|-----------------|
| **App Service** (Linux) | B2 | 2 vCPU, 4 GB RAM, 50 GB storage, Central India | ~3,500 |
| **PostgreSQL Flexible Server** | B2ms | 2 vCPU, 8 GB RAM, 128 GB storage (GPSSD), Central India, Geo-redundant backup disabled | ~5,000 |
| **Blob Storage** | Standard LRS | 50 GB capacity, Hot tier, Central India | ~700 |
| **Bandwidth** | Pay-as-you-go | 50 GB outbound/month | ~3,000 |

**Total estimated: ~₹12,200–16,200/month**

### Network Configuration

- PostgreSQL: Private network (disable public access)
- App Service: Public endpoint with HTTPS only
- VNet integration: Not required at this scale (developer time constraint)
- Firewall: PostgreSQL firewall whitelist App Service outbound IPs + developer IP

### Monitoring

- **Azure Application Insights:** Installed on App Service; monitors request rate, response times, failure rates, dependency calls (PostgreSQL, MSG91)
- **Azure Monitor Alerts:**
  - CPU > 80% for 10 min → email
  - Memory > 80% for 10 min → email
  - HTTP 5xx rate > 5% for 5 min → email
  - App Service HTTP queue length > 50 → email
- **Azure Cost Alerts:**
  - Budget: ₹16,200/month
  - Alert at 80% spend
  - Alert at 100% spend
- **Uptime check:** Azure Application Insights availability test (ping /api/health every 5 min from 3 locations)

---

## 6. Security Architecture

### Authentication

| User Type | Method | Token | Expiry |
|-----------|--------|-------|--------|
| Mechanic | SMS OTP | JWT (access + refresh) | 15 min / 30 days |
| Admin | Email + password | HttpOnly session cookie | 8 hours |

### Transport Security

- TLS 1.2+ enforced at Azure App Service level
- HSTS header set
- CORS: restricted to web portal domain, localhost for development

### Storage Security

- API keys stored in Azure App Service application settings (not in code)
- Database connection string in App Service settings (not in code)
- Blob Storage: Private container; no anonymous access
- File access via SAS tokens only (1-hour expiry)
- OTP: Hashed with SHA-256 before storage; never stored in plaintext
- Passwords: bcrypt with cost factor 12
- JWT signing secret: 256-bit random string in environment variables

### Audit Trail

- All admin actions logged to immutable `audit_log` table
- Table is INSERT-only (API has no DELETE or UPDATE on audit_log)
- Columns: admin_id, action, target_type, target_id, old_value (JSON), new_value (JSON), ip_address, user_agent, created_at

### Rate Limiting

| Endpoint | Rate |
|----------|------|
| All mechanic endpoints | 100 requests/minute |
| All admin endpoints | 500 requests/minute |
| OTP send | 5 requests/15 minutes per mobile |
| OTP verify | 10 requests/15 minutes per mobile |
| Login (admin) | 5 attempts/15 minutes per email |

### File Upload Security

- Content-Type validation (image/jpeg, audio/mp4)
- Magic byte validation on server
- File size limits: 5 MB per photo, 2 MB per audio, max 3 photos
- Filenames: Server-generated UUIDs (user-supplied filenames discarded)

---

## 7. Offline Architecture Detail

### 7.1 SQLite Schema on Device

```sql
CREATE TABLE entries (
  local_id INTEGER PRIMARY KEY AUTOINCREMENT,
  id TEXT UNIQUE,                          -- UUID (null until synced)
  part_type TEXT NOT NULL,                 -- 'condenser' | 'compressor'
  oem TEXT,                                -- null for condenser
  model TEXT NOT NULL,
  variant TEXT,
  fuel_type TEXT NOT NULL,
  year INTEGER NOT NULL,
  registration TEXT,
  failure_cause TEXT NOT NULL,
  severity TEXT NOT NULL,
  odometer INTEGER NOT NULL,
  latitude REAL,
  longitude REAL,
  mechanic_note TEXT,
  voice_note_path TEXT,                    -- local file path
  created_at TEXT NOT NULL,                -- ISO 8601
  sync_status TEXT NOT NULL DEFAULT 'pending',  -- pending | uploading | synced | failed
  retry_count INTEGER DEFAULT 0,
  next_retry_at TEXT,
  last_sync_error TEXT,
  is_duplicate INTEGER DEFAULT 0,
  server_id TEXT                           -- server-assigned UUID after sync
);

CREATE TABLE photos (
  local_id INTEGER PRIMARY KEY AUTOINCREMENT,
  entry_local_id INTEGER NOT NULL REFERENCES entries(local_id),
  slot TEXT NOT NULL,                      -- 'damage' | 'label' | 'installed'
  local_path TEXT NOT NULL,                -- local file path
  blob_path TEXT,                          -- server path (null until synced)
  uploaded INTEGER DEFAULT 0,              -- 0 = pending, 1 = uploaded
  file_size INTEGER
);

CREATE TABLE app_settings (
  key TEXT PRIMARY KEY,
  value TEXT NOT NULL
);
```

### 7.2 Conflict Resolution Strategy

- **No conflict resolution needed** because:
  - Each mechanic has one device
  - Entries are created on device, never modified after sync
  - Admin actions (rejection) are server-only and do not conflict with local state
- **Sync is one-directional:** Device → Server
- Admin rejections are reflected in the next server response

### 7.3 Retry Strategy

```
Attempt 1: Immediate
Attempt 2: After 1 minute
Attempt 3: After 5 minutes
Attempt 4: After 15 minutes
Attempt 5: After 1 hour
Attempt 6: After 6 hours
Attempt 7+: Every 6 hours until 24 hours, then give up
```

After 24 hours of failed sync: Show persistent notification: "N entries failed to sync. Tap to retry."

---

## 8. Deployment Architecture

### Environments

```
DEV (local)
  - Windows/Mac machine
  - PostgreSQL via Docker
  - Azure Storage Emulator
  - MSG91 test credentials

STAGING (Azure)
  - rg-partlog-staging
  - B1 App Service (cheaper tier)
  - B1ms PostgreSQL
  - Same config as prod but smaller
  - Deployed from main branch

PRODUCTION (Azure)
  - rg-partlog-prod
  - B2 App Service
  - B2ms PostgreSQL
  - Blob Storage Standard LRS
  - Deployed from release tags
```

### CI/CD Pipeline (GitHub Actions)

```yaml
name: Deploy Backend
on:
  push:
    branches: [main]
    paths: ['backend/**']

jobs:
  test-and-deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 20
      - run: npm ci
        working-directory: ./backend
      - run: npx prisma generate
        working-directory: ./backend
      - run: npm run lint
        working-directory: ./backend
      - run: npm test
        working-directory: ./backend
      - name: Deploy to Azure
        uses: azure/webapps-deploy@v3
        with:
          app-name: partlog-api-prod
          slot-name: production
          publish-profile: ${{ secrets.AZURE_WEBAPP_PUBLISH_PROFILE }}
```

### Environment Variables

| Variable | Source | Notes |
|----------|--------|-------|
| DATABASE_URL | Azure App Service settings | PostgreSQL connection string |
| MSG91_API_KEY | Azure App Service settings | MSG91 authentication |
| MSG91_SENDER_ID | Azure App Service settings | MSG91 sender ID |
| JWT_SECRET | Azure App Service settings | 256-bit random string |
| AZURE_STORAGE_CONNECTION_STRING | Azure App Service settings | Blob Storage access |
| EXPO_ACCESS_TOKEN | Azure App Service settings | Expo push notifications |
| NEXT_PUBLIC_API_URL | Vercel env vars | Web portal API base URL |
| NEXTAUTH_SECRET | Vercel env vars | NextAuth session secret |
| NEXTAUTH_URL | Vercel env vars | Web portal URL |

---

## 9. Monitoring and Alerting

### Azure Application Insights

| Metric | Dashboard |
|--------|-----------|
| Server response time (p50, p95, p99) | Performance |
| Request rate (requests/minute) | Performance |
| Failure rate (5xx / total) | Failures |
| Dependency duration (PostgreSQL, MSG91) | Performance |
| Live metrics | Live Metrics |

### Azure Monitor Alert Rules

| Alert | Condition | Severity | Action |
|-------|-----------|----------|--------|
| High CPU | CPU > 80% for 10 min | 2 (Warning) | Email |
| High Memory | Memory > 80% for 10 min | 2 (Warning) | Email |
| High HTTP 5xx | 5xx rate > 5% for 5 min | 1 (Critical) | Email + phone |
| App Service down | Availability < 90% for 5 min | 1 (Critical) | Email + phone |
| Budget at 80% | Cost > 80% of monthly budget | 3 (Informational) | Email |
| Budget exceeded | Cost > 100% of monthly budget | 2 (Warning) | Email |
| Sync failure rate high | > 5% sync failures in 1 hour | 2 (Warning) | Email |

### Dashboard

Single Azure Dashboard with:
- App Service CPU/Memory (time chart, last 24 hours)
- Request rate and response time
- Database connections and DTU usage
- Blob Storage capacity
- Active alerts

---

## 10. Storage Estimation

### Database Growth Projection

| Entity | Row Size (bytes) | Rows at 6 months | Rows at 12 months | Storage at 12 months |
|--------|-----------------|------------------|-------------------|---------------------|
| entries | ~500 | 45,000 | 180,000 | ~90 MB |
| photos | ~200 | 135,000 (3 per entry) | 540,000 | ~108 MB |
| mechanics | ~300 | 1,000 | 2,000 | ~0.6 MB |
| admins | ~200 | 5 | 5 | ~1 KB |
| payout_rates | ~100 | 10 | 20 | ~2 KB |
| monthly_payouts | ~200 | 6,000 | 24,000 | ~4.8 MB |
| payout_entry_snapshots | ~100 | 45,000 | 180,000 | ~18 MB |
| otp_logs | ~100 | 15,000 | 60,000 | ~6 MB |
| refresh_tokens | ~200 | 5,000 | 20,000 | ~4 MB |
| audit_log | ~500 | 5,000 | 20,000 | ~10 MB |
| **Total** | | | | **~241 MB** |

**PostgreSQL B2ms (128 GB) is significantly over-provisioned for this scale.** This is intentional — the B2ms tier provides adequate RAM (8 GB) for query performance and connection handling.

### Blob Storage Growth Projection

| Asset | Size per unit | Units per entry | Daily volume | Storage at 12 months |
|-------|--------------|----------------|-------------|---------------------|
| Photo (compressed) | ~300 KB | 3 | 150–1,500 photos | ~16–164 GB |
| Audio (compressed) | ~200 KB | 0.5 (50% of entries) | 50–250 audio files | ~3–18 GB |
| **Total** | | | | **~19–182 GB** |

Azure Blob Storage standard tier at 50 GB is sufficient for the first 6–8 months. After that, lifecycle policy moves older blobs to Cool tier.

---

## 11. Technology Decisions Log

| Decision | Option A | Option B | Chosen | Rationale |
|----------|----------|----------|--------|-----------|
| API framework | Express.js | Fastify | Express | Larger ecosystem, more examples for AI tools, simpler |
| ORM | Prisma | Drizzle | Prisma | Auto-generated types, better migration tooling, Prisma Studio for debugging |
| Validation | Zod | Joi | Zod | TypeScript-first, composable, Prisma-like DX |
| Mobile framework | Expo (managed) | React Native CLI | Expo | Faster development, OTA updates, managed builds |
| Mobile state | Zustand | Redux Toolkit | Zustand | Lighter, simpler API, sufficient for this scale |
| Charts | Recharts | Chart.js | Recharts | React-native, composable, tree-shakeable |
| Table | TanStack Table | MUI Data Grid | TanStack | Headless, fully customisable, no heavy dependency |
| Auth (web) | NextAuth.js | Custom | NextAuth | Battle-tested, session management out-of-box |
| CSS | Tailwind | Chakra UI | Tailwind | Utility-first, smaller bundle, more control |
| Cloud | Azure | AWS | Azure | Company preference; PostgreSQL Flexible Server quality |
| Mobile DB | expo-sqlite | WatermelonDB | expo-sqlite | Simpler API; sufficient for ~500 offline entries at a time |
| Notifications | Expo Push | Firebase Cloud Messaging | Expo Push | Simpler integration; no native module needed |

---

## 12. Known Limitations and Future Considerations

### Current Limitations (v1)

1. **No auto-scaling** — B2 tier is fixed. If concurrent users exceed capacity, manual scale-up required.
2. **No read replicas** — All analytics queries run on the primary database. At scale, this may impact write performance.
3. **No CDN for images** — Photo loading on admin portal may be slow if admin is geographically distant from Central India Azure region.
4. **No caching layer** — Redis would improve dashboard query performance but adds complexity and cost.
5. **Single developer ops** — No on-call rotation, no disaster recovery drill.
6. **Manual UPI payouts** — At 500+ mechanics, manual payment processing becomes labour-intensive.

### Future Considerations (v2+)

| Feature | When needed | What to change |
|---------|-------------|----------------|
| Auto-scaling | Mechanics > 2,000 | Enable Azure App Service auto-scale rule (CPU > 70%) |
| Read replica | Daily entries > 1,000 | Add read replica for analytics queries |
| CDN for images | Admin portal slow globally | Azure CDN with Blob Storage origin |
| Redis cache | Dashboard queries > 3s | Azure Cache for Redis with query result caching |
| Automated UPI payments | Monthly manual processing unsustainable | Razorpay / Cashfree UPI API integration |
| AI/ML forecasting | 12+ months of data collected | Azure ML or custom Python service reading from DB |
| Distributor portal | Sales team requests access | New Next.js app or sub-path with role-based access |
| iOS app | Market demand from mechanics | Expo supports iOS — build with same codebase; add to App Store |
| Supervisor role | Regional management requirement | Add role field to admin/mechanic; region-based filtering |
| WebSocket live updates | Admin wants real-time entry feed | Add Socket.io to backend; React context on frontend |
