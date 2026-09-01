# Software Requirements Specification (SRS) — PartLog

**Version:** 1.0  
**Date:** July 2026  
**IEEE 830 Compliant**

---

## 1. Introduction

### 1.1 Purpose

This Software Requirements Specification (SRS) document defines the complete functional and non-functional requirements for PartLog, a field data collection platform for automotive AC part failure intelligence. It covers two products: an Android mobile application for mechanics and a web portal for company administrators.

### 1.2 Scope

PartLog enables mechanics across India to log AC condenser and compressor failure entries using an Android app (offline-first, 10 Indian languages). Entries are synced to a central database and reviewed via a web portal by Vikas Group administrators. Admins manage mechanics, configure payout rates, process monthly payouts, and view analytics dashboards with export capability.

### 1.3 Definitions and Acronyms

| Term | Definition |
|------|------------|
| API | Application Programming Interface |
| BRD | Business Requirements Document |
| CSV | Comma-Separated Values |
| JWT | JSON Web Token |
| OEM | Original Equipment Manufacturer |
| OTP | One-Time Password |
| SRS | Software Requirements Specification |
| UPI | Unified Payments Interface |
| UUID | Universally Unique Identifier |
| SAS | Shared Access Signature (Azure Blob Storage) |

### 1.4 References

| Document | Source |
|----------|--------|
| Project Charter | PartLog project documentation |
| Business Requirements Document (BRD) | PartLog project documentation |
| System Architecture Document (SAD) | PartLog project documentation |
| Data Model and Database Schema | PartLog project documentation |
| MSG91 API Documentation | https://docs.msg91.com |
| Expo SDK 51 Documentation | https://docs.expo.dev |
| Prisma 5 Documentation | https://www.prisma.io/docs |

### 1.5 Overview

This SRS is organised into: Overall Description (Section 2), Functional Requirements (Section 3), Non-Functional Requirements (Section 4), External Interface Requirements (Section 5), System Constraints (Section 6), Data Requirements (Section 7), Security Requirements (Section 8), and Glossary (Section 9).

---

## 2. Overall Description

### 2.1 Product Perspective

PartLog is a new, custom-built platform. It consists of three subsystems:

1. **Mobile Application (Android):** React Native (Expo SDK 51) app used by mechanics to register, log failure entries, view earnings, and manage their profile. Works offline-first using SQLite.
2. **Web Portal (Admin):** Next.js 14 web application used by company administrators to view data, manage mechanics, process payouts, and export reports.
3. **Backend API:** Node.js 20 + Express.js 4 REST API deployed on Azure App Service. Handles authentication, entry CRUD, sync, file upload, payout logic, and analytics.

External systems: MSG91 (SMS OTP), Expo Push API (notifications), Azure Blob Storage (file storage), Google Play Store (APK distribution).

### 2.2 Product Functions

- Mechanic self-registration via OTP (10 Indian languages)
- Condenser failure entry logging (Maruti Suzuki models only)
- Compressor failure entry logging (all OEMs, free-text)
- Offline-first entry creation with background sync
- Photo capture (up to 3 per entry), voice note recording (60 sec)
- Duplicate entry detection on registration number
- Mechanic earnings dashboard and payout request
- Admin login (email + password)
- Admin dashboard with KPIs and charts
- Entry table with filtering, sorting, pagination, detail view
- Entry approval/rejection with rejection reason
- Mechanic management (view, deactivate, reactivate)
- Payout rate configuration (per part type)
- Monthly payout generation and payment tracking
- Data export (CSV, Excel)
- Audit logging for all admin actions

### 2.3 User Characteristics

| User Type | Characteristics |
|-----------|-----------------|
| **Mechanic** | Independent auto AC mechanic, 22–45 years, basic smartphone literacy, intermittent 4G connectivity, speaks Hindi or regional language, motivated by UPI payouts |
| **Admin** | Vikas Group staff, comfortable with web apps and data analysis, reviews entries 1–2 hours daily, processes payouts monthly |

### 2.4 Constraints

- Android only (no iOS in this version)
- Single developer using AI-assisted tools
- Budget: lean, Azure ~₹16,200/month
- Timeline: platform operational within 14–18 weeks
- 6–8 month primary data collection phase

### 2.5 Assumptions

- Minimum Android 8.0, 3GB RAM
- Mechanics have basic smartphone proficiency
- 4G connectivity available at least once daily
- Google Play Store internal testing track will be approved

---

## 3. Functional Requirements

### 3.1 Authentication Module (Mobile)

| ID | Description | Priority | Acceptance Criteria |
|----|-------------|----------|-------------------|
| FR-01 | Mechanic enters mobile number on a dedicated screen; app sends OTP via MSG91 API | Must | Enter 10-digit number; "Send OTP" button; loading state during API call; error if network fails |
| FR-02 | Mechanic enters 6-digit OTP received via SMS; app verifies against server | Must | 6 digit input fields; auto-submit on complete; "Resend OTP" after 30 seconds; max 3 attempts before lockout (5 min) |
| FR-03 | On successful OTP verification, mechanic is either logged in (returning) or shown onboarding form (new) | Must | JWT access token (15 min) + refresh token (30 days) returned; stored securely on device |
| FR-04 | Mechanic completes one-time onboarding: name, workshop name, city, state, language preference | Must | 5 form fields with validation; skip not allowed; saved to API on submit |
| FR-05 | Returning mechanic sees home screen on app launch (no re-onboarding) | Must | Token check; if valid refresh token exists, auto-login; else show mobile entry screen |
| FR-06 | Mechanic can log out from Settings | Must | Confirm dialog; clear tokens from device storage; redirect to mobile entry screen |
| FR-07 | Token refresh is automatic; app calls refresh endpoint when access token expires | Must | Silent refresh; no UI interruption; if refresh fails, redirect to login |

### 3.2 Condenser Entry Module (Mobile)

| ID | Description | Priority | Acceptance Criteria |
|----|-------------|----------|-------------------|
| FR-08 | Mechanic selects "Condenser" on part type selection screen | Must | Two large buttons: "Condenser" and "Compressor"; clear visual distinction |
| FR-09 | Mechanic selects vehicle model from a grid of chip buttons: Swift, Baleno, WagonR, Dzire, Brezza, Alto K10, Ertiga, XL6, Ignis, S-Presso, Celerio, Grand Vitara, Jimny | Must | Chips arranged 3 per row; scrollable; selected chip has filled blue background; unselected has outline |
| FR-10 | Mechanic enters variant as free text with autocomplete suggestions from server | Should | Text input; dropdown of suggestions fetched from API (cached locally); minimum 2 chars to trigger suggestions |
| FR-11 | Mechanic selects fuel type from: Petrol, Diesel, CNG, Electric, Hybrid | Must | Single-select chip group; default none |
| FR-12 | Mechanic selects vehicle year from 2010 to current year | Must | Scrollable picker or number input with range validation |
| FR-13 | Mechanic enters registration number (optional, alphanumeric, Indian format) | Could | 5–12 chars; alphanumeric; auto-uppercase; example placeholder |
| FR-14 | Mechanic selects failure cause from: Stone impact, Corrosion, Accident damage, Pressure failure, Blockage, Manufacturing defect, Unknown | Must | Single-select list with radio buttons or chips; Unknown is last option |
| FR-15 | Mechanic selects severity from: Minor, Major, Complete failure | Must | Three chips; single select; severity tooltip explaining each level |
| FR-16 | Mechanic enters odometer reading (mandatory, numeric, 4–7 digits, km) | Must | Numeric keyboard; minimum 100; maximum 9999999; validation on submit |
| FR-17 | Mechanic can capture up to 3 photos: damage close-up, part label, installed position | Must | Launch camera; preview after capture; replace individual photo; delete option |
| FR-18 | Mechanic can record a voice note (up to 60 seconds) | Should | Record/pause/stop UI; playback before submit; re-record option |
| FR-19 | Mechanic can enter a text note (up to 200 characters) | Could | Multi-line text input; character counter |
| FR-20 | GPS coordinates are automatically captured when entry creation begins | Must | Permission request on first launch; show GPS icon (green/red) on entry screen; fallback to manual if denied |
| FR-21 | Mechanic reviews entry summary before final submission | Must | Summary screen showing all fields; back to edit any section; confirm button |
| FR-22 | On confirm, entry is saved locally to SQLite immediately | Must | Success animation/feedback; entry moved to "pending sync" queue |
| FR-23 | If online, entry is synced immediately to server | Must | API call with full payload; on success, entry marked synced; on failure, retry queued |

### 3.3 Compressor Entry Module (Mobile)

| ID | Description | Priority | Acceptance Criteria |
|----|-------------|----------|-------------------|
| FR-24 | Mechanic selects "Compressor" on part type selection screen | Must | Same UI as FR-08 |
| FR-25 | Mechanic enters OEM as free text | Must | Text input with placeholder "e.g., Tata, Hyundai, Mahindra"; autocomplete suggestions from cache |
| FR-26 | Mechanic enters model as free text | Must | Text input with autocomplete suggestions |
| FR-27 | Mechanic selects failure cause from: Seized, Gas leak, Noise/bearing failure, Electrical failure, Pressure failure, Manufacturing defect, Unknown | Must | Single-select list; Unknown is last option |
| FR-28 | All other condenser fields apply (variant, fuel type, year, registration, severity, odometer, photos, voice, note, GPS) | Must | Reuse same UI components as condenser flow |

### 3.4 Sync Module (Mobile)

| ID | Description | Priority | Acceptance Criteria |
|----|-------------|----------|-------------------|
| FR-29 | App maintains a local sync queue of unsynced entries in SQLite | Must | Queue ordered by created_at; each entry has sync_status (pending, uploading, failed, synced) |
| FR-30 | Background sync runs every 5 minutes using expo-background-fetch when internet is available | Must | Syncs in FIFO order; progress indicator in app (banner: "3 entries pending") |
| FR-31 | App checks network connectivity before attempting sync; skips if offline | Must | NetInfo listener; sync banner shows "Offline" state |
| FR-32 | Photos and voice notes are uploaded via pre-signed SAS URLs from the server | Must | Server generates SAS URL for each file; app uploads directly to Azure Blob Storage |
| FR-33 | If sync fails (network error, server error), entry remains in queue with retry_count incremented | Must | Exponential backoff: 1 min, 5 min, 15 min, 1 hour; give up after 24 hours (alert mechanic) |
| FR-34 | User can manually trigger sync from home screen | Must | "Sync now" button; visible when unsynced entries exist; shows progress |
| FR-35 | Synced entries are marked as "synced" in local DB and can be viewed but not edited | Must | Read-only after sync; deletion allowed locally (server data is authoritative) |

### 3.5 Duplicate Detection (Mobile + Backend)

| ID | Description | Priority | Acceptance Criteria |
|----|-------------|----------|-------------------|
| FR-36 | If registration number is provided, backend checks for existing entry with same reg + same part_type within last 30 days | Should | API endpoint called before entry creation; returns boolean + count |
| FR-37 | If duplicate is found, mechanic sees a warning dialog: "This vehicle was logged [N] days ago. Submit anyway?" | Should | Dialog with "Submit" and "Cancel" buttons; mechanic can override |
| FR-38 | Duplicate flag is stored on entry (is_duplicate boolean) and visible to admin in portal | Should | Column in entries table; filterable in admin view |

### 3.6 Earnings and Payout Module (Mobile)

| ID | Description | Priority | Acceptance Criteria |
|----|-------------|----------|-------------------|
| FR-39 | Mechanic sees earnings screen showing: entries this month, approved count, estimated payout, monthly history | Must | Card-style layout; history as list with month, entries, amount, status (pending/paid) |
| FR-40 | Mechanic can enter/edit their UPI ID from Settings or Payout screen | Must | Text input; validation (UPI format: name@provider); saved to profile on server |
| FR-41 | Mechanic receives a push notification when admin marks payout as paid | Should | Expo Push API; notification with amount and transaction ref |
| FR-42 | Mechanic can export their own entries as CSV from Settings | Could | Generates CSV of all synced entries; saved to device Downloads folder |

### 3.7 Admin Authentication Module (Web)

| ID | Description | Priority | Acceptance Criteria |
|----|-------------|----------|-------------------|
| FR-43 | Admin logs in with email and password on the web portal login page | Must | Email + password inputs; "Login" button; error state on invalid credentials |
| FR-44 | Session is maintained via HttpOnly cookie (8-hour expiry) | Must | NextAuth.js session provider; secure cookie in production |
| FR-45 | Admin can log out from the sidebar/profile menu | Must | Clear session; redirect to login |
| FR-46 | Failed login attempts are rate-limited (5 attempts per email per 15 minutes) | Must | express-rate-limit; account lockout notification optionally |

### 3.8 Admin Dashboard Module (Web)

| ID | Description | Priority | Acceptance Criteria |
|----|-------------|----------|-------------------|
| FR-47 | Dashboard shows 5 KPI cards: Total Mechanics, Total Entries, Entries This Month, Pending Review, Payout Due (₹) | Must | Cards with icon, label, number, trend arrow (up/down vs last month) |
| FR-48 | Dashboard shows a bar chart of entries by model (top 10) | Must | Horizontal bar chart; clickable to filter entries |
| FR-49 | Dashboard shows a bar chart of entries by state (top 10) | Must | Vertical bar chart; tooltip with count |
| FR-50 | Dashboard shows a bar chart of entries by failure cause | Must | Horizontal bar chart; separate series for condenser vs compressor |
| FR-51 | Dashboard shows a bar chart of entries by fuel type | Must | Horizontal bar chart |
| FR-52 | Dashboard shows a daily entry volume line chart for last 30 days | Must | Line chart; X-axis: date, Y-axis: count; tooltip on hover |

### 3.9 Admin Entry Management Module (Web)

| ID | Description | Priority | Acceptance Criteria |
|----|-------------|----------|-------------------|
| FR-53 | Admin views all entries in a paginated, filterable, sortable table | Must | TanStack Table; 25 rows per page; columns: date, mechanic, part type, model, OEM, severity, state, status (approved/rejected/pending) |
| FR-54 | Admin can filter entries by: part type, model, state, status, date range, failure cause, severity | Must | Filter bar above table; multi-select dropdowns; date range picker; clear all filters button |
| FR-55 | Admin can click an entry row to view full detail in a side panel or modal | Must | Shows all fields; photo gallery with lightbox; audio player for voice note; GPS link to Google Maps |
| FR-56 | Admin can approve or reject an entry | Must | Reject requires rejection reason (enum: fraudulent, incomplete, duplicate, poor_quality, other) + optional note |
| FR-57 | Admin can bulk-reject entries (select multiple rows, click reject) | Could | Checkbox on each row; "Select all" in header; bulk action toolbar |
| FR-58 | Admin can view entry history (created, synced, status changes with timestamps) | Must | Activity log section in entry detail; shows all status transitions |

### 3.10 Admin Mechanic Management Module (Web)

| ID | Description | Priority | Acceptance Criteria |
|----|-------------|----------|-------------------|
| FR-59 | Admin views all mechanics in a paginated table with columns: name, mobile, workshop, city, state, entries count, status (active/inactive), registration date | Must | TanStack Table; search by name or mobile |
| FR-60 | Admin can click a mechanic to view detail: profile info, entry history, payout history, activity log | Must | Detail page with tabs: entries (filterable), payouts, activity |
| FR-61 | Admin can deactivate a mechanic account (prevents login, existing entries remain) | Must | Confirm dialog; audit log |
| FR-62 | Admin can reactivate a deactivated account | Must | Same as FR-61, reverse action |

### 3.11 Admin Payout Module (Web)

| ID | Description | Priority | Acceptance Criteria |
|----|-------------|----------|-------------------|
| FR-63 | Admin configures payout rate: separate ₹ amount for condenser and compressor entries, effective_from date | Must | Settings page; form with two number inputs; history of rate changes displayed |
| FR-64 | Admin views monthly payout summary: table with mechanic name, mobile, approved count (condenser), approved count (compressor), total amount, UPI ID, status (pending/paid) | Must | Month selector; totals row at bottom; export button |
| FR-65 | Admin can mark a payout as "paid" and enter UPI transaction reference number | Must | Click "Mark paid" on a row; modal with transaction ref input; date auto-set to today |
| FR-66 | System calculates payout as: (approved condenser count × condenser rate) + (approved compressor count × compressor rate) | Must | Only entries approved before the last day of the month count; entries rejected after month-end do not affect previous month |
| FR-67 | Admin can view payout history per mechanic (previous months with amounts and status) | Must | Shown in mechanic detail page |

### 3.12 Admin Export Module (Web)

| ID | Description | Priority | Acceptance Criteria |
|----|-------------|----------|-------------------|
| FR-68 | Admin can export entries as CSV with all columns | Must | Respects current filters; date range mandatory; file downloaded |
| FR-69 | Admin can export entries as Excel (.xlsx) with same columns | Must | SheetJS library; formatted columns; file downloaded |
| FR-70 | Admin can export payout summary as CSV and Excel | Must | Same month selector; includes all columns from payout table |
| FR-71 | Export respects row limits — max 10,000 rows per export; warns if exceeded | Could | Prompt to narrow filters if >10,000 |

### 3.13 Audit Log Module (Backend)

| ID | Description | Priority | Acceptance Criteria |
|----|-------------|----------|-------------------|
| FR-72 | All admin actions are logged to an immutable audit_log table | Must | Action, admin_id, target_type (entry/mechanic/payout_rate/payout), target_id, old_value, new_value, timestamp |
| FR-73 | Audit log is readable-only via API (no delete, no update) | Must | DB-level protection (read-only role on audit_log for API) |

### 3.14 Notifications (Backend)

| ID | Description | Priority | Acceptance Criteria |
|----|-------------|----------|-------------------|
| FR-74 | Push notification sent to mechanic when payout is marked paid | Should | Expo Push API; includes amount and transaction ref |
| FR-75 | Push notification sent to mechanic when entry is rejected (optional) | Could | Includes reason |

### 3.15 Profile and Settings (Mobile)

| ID | Description | Priority | Acceptance Criteria |
|----|-------------|----------|-------------------|
| FR-76 | Mechanic can edit profile: name, workshop, city, state | Must | Settings screen; updates synced to server |
| FR-77 | Mechanic can change app language from Settings | Must | Language list; app re-renders immediately in selected language |
| FR-78 | Mechanic can see app version and logout button in Settings | Must | Display current version; logout with confirmation |

---

## 4. Non-Functional Requirements

### 4.1 Performance

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-01 | API response time (p95) for all authenticated endpoints | < 300ms |
| NFR-02 | API response time (p95) for dashboard/analytics queries | < 2s |
| NFR-03 | Entry sync upload (with 1 photo) | < 5s |
| NFR-04 | Mobile app cold start to home screen | < 3s |
| NFR-05 | Offline entry save to SQLite | < 200ms |
| NFR-06 | Admin dashboard page load | < 2s (initial) |
| NFR-07 | Entry table with 10,000 rows (paginated, 25 per page) | < 1s page load |
| NFR-08 | Photo compression on device | < 2s per photo |

### 4.2 Reliability

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-09 | Backend API uptime (excludes planned maintenance) | 99.5% |
| NFR-10 | Database backups | Daily automated backup with 7-day retention |
| NFR-11 | Offline entry data loss | 0% — entries saved to SQLite before any server call |
| NFR-12 | Sync retry persistence | Queue survives app restart |

### 4.3 Usability

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-13 | Mechanic onboarding (registration through first entry) | < 3 minutes |
| NFR-14 | Single entry creation time (experienced user, online) | < 90 seconds |
| NFR-15 | Touch targets minimum | 48×48px |
| NFR-16 | Font size minimum (mobile) | 14sp |
| NFR-17 | Language switch applies without app restart | Instant |

### 4.4 Security

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-18 | All API endpoints require HTTPS (TLS 1.2+ enforced) | Yes |
| NFR-19 | API rate limiting: 100 req/min per mechanic, 500 req/min per admin | Yes |
| NFR-20 | Passwords hashed with bcrypt, cost factor 12 | Yes |
| NFR-21 | OTP hashed before storage (SHA-256) | Yes |
| NFR-22 | JWT access tokens expire in 15 minutes | Yes |
| NFR-23 | Refresh tokens expire in 30 days, single-use | Yes |
| NFR-24 | Admin sessions expire after 8 hours of inactivity | Yes |
| NFR-25 | CORS restricted to known origins (web portal domain, localhost for dev) | Yes |
| NFR-26 | File upload: only JPEG for photos (max 5MB each), only M4A for audio (max 2MB) | Yes |
| NFR-27 | File upload: virus scan on server side (ClamAV integration optional for v1 — manual review) | Should |

### 4.5 Scalability

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-28 | Supported mechanics | 500–2000 |
| NFR-29 | Supported entry volume | 100–500/day |
| NFR-30 | Database connection pool | 20 connections (PgBouncer) |
| NFR-31 | Concurrent API requests | 100 simultaneous |

### 4.6 Portability

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-32 | Minimum Android version | Android 8.0 (API 26) |
| NFR-33 | Minimum RAM | 3 GB |
| NFR-34 | Minimum storage free | 1 GB (for app + photo cache) |
| NFR-35 | Web portal supported browsers | Chrome 90+, Firefox 88+, Edge 90+ |

### 4.7 Internationalisation

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-36 | Supported languages | English, Hindi, Tamil, Telugu, Malayalam, Kannada, Marathi, Gujarati, Bengali, Punjabi |
| NFR-37 | Translation coverage | 100% of UI strings (all screens) |
| NFR-38 | Date format | Locale-aware (DD/MM/YYYY for India) |
| NFR-39 | Number format | Locale-aware (Indian numbering: 1,23,456) |

---

## 5. External Interface Requirements

### 5.1 MSG91 SMS OTP

- Endpoint: `https://api.msg91.com/api/v5/otp`
- Method: POST
- Auth: API key stored in environment variable
- Template: PartLog OTP template ID
- Flow: Send OTP → Verify OTP → Resend OTP (30s cooldown)
- Failure handling: Log delivery status; manual OTP bypass flow for support cases

### 5.2 Expo Push API

- Endpoint: `https://exp.host/--/api/v2/push/send`
- Method: POST
- Auth: None (Expo push token is the identifier)
- Flow: Store push token on mechanic profile → Send notification on payout → Error handling for invalid tokens

### 5.3 Azure Blob Storage

- Service: Blob Storage (Standard, LRS, Central India)
- Container: partlog-media (private, no anonymous access)
- Access: Server generates SAS tokens (1-hour expiry) for upload and download
- Structure: `photos/{YYYY}/{MM}/{entry_id}/{slot}.jpg`, `audio/{YYYY}/{MM}/{entry_id}.m4a`
- Lifecycle: Move to Cool tier after 90 days

### 5.4 Google Play Store Internal Testing

- Track: Internal testing (not production/closed alpha)
- Distribution: Email invite to mechanic's Google account
- Updates: Expo EAS Update for OTA JS updates; Play Store for native module updates

---

## 6. System Constraints

- **Single developer:** All code, infrastructure, and operations handled by one person. AI tools assist but do not replace human decision-making.
- **No automated UPI:** Payouts are processed manually by admin. No bank API integration.
- **No CI/CD for mobile:** APK builds are triggered on-demand via Expo EAS, not on every push.
- **No staging environment for production data:** Staging mirrors production architecture but uses synthetic data.
- **Azure budget cap:** Infrastructure costs must be predictable and monitorable. Auto-scaling disabled.

---

## 7. Data Requirements

### Core Entities (Refer to Data Model and Database Schema document for full detail)

- **Mechanic:** mobile, name, workshop, city, state, language, UPI ID, active status
- **Entry:** part_type, OEM, model, variant, fuel_type, year, registration, failure_cause, severity, odometer, lat, lng, mechanic_note, is_duplicate, approval_status
- **Photo:** entry_id, slot (damage/label/installed), blob_url, blob_path, file_size
- **Admin:** email, password_hash, name
- **PayoutRate:** part_type, rate (₹), effective_from, set_by
- **MonthlyPayout:** mechanic_id, year, month, condenser_count, compressor_count, amount, status, transaction_ref
- **OTPLog:** mobile, hashed_otp, expires_at, attempts
- **AuditLog:** admin_id, action, target_type, target_id, old_value, new_value

---

## 8. Security Requirements

- All communications over HTTPS/TLS 1.2+ enforced at Azure App Service level
- Application gateway with WAF (optional, v1 may skip for cost)
- Admin passwords: bcrypt cost 12
- OTP: hashed with SHA-256 before storage; plaintext never stored
- JWT: RS256 signing; 15-minute access, 30-day refresh (single use)
- CORS: whitelist web portal URL and localhost development
- File upload: content-type and magic byte validation; size limits enforced
- API rate limiting per IP and per user
- Input validation: Zod schemas reject malformed requests before they reach business logic
- Audit trail: all admin actions recorded in append-only audit_log table

---

## 9. Glossary

| Term | Definition |
|------|------------|
| **Condenser** | AC component that condenses refrigerant; PartLog tracks Maruti Suzuki only |
| **Compressor** | AC component that circulates refrigerant; PartLog tracks all OEMs |
| **OEM** | Original Equipment Manufacturer (vehicle maker) |
| **UPI** | Unified Payments Interface, India's mobile payment system |
| **SAS Token** | Shared Access Signature — time-limited secure URL for Azure Blob access |
| **Zod** | TypeScript-first schema validation library |
| **Prisma** | Node.js ORM for PostgreSQL |
| **Expo** | React Native framework for cross-platform mobile development |
| **TanStack Table** | Headless UI library for building data tables |
| **NextAuth.js** | Authentication library for Next.js |
