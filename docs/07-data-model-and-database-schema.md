# Data Model and Database Schema — PartLog

**Version:** 1.0  
**Date:** July 2026  
**Database:** PostgreSQL 16 on Azure Database for PostgreSQL Flexible Server  
**ORM:** Prisma 5

---

## 1. Data Model Overview

### Design Principles

1. **UUIDs for offline-first** — Entry IDs are UUIDs generated on-device to enable offline entry creation without ID conflicts.
2. **Enum types for constrained fields** — Failure causes, severity, part type, fuel type, approval status — all stored as PostgreSQL enum types for type safety and storage efficiency.
3. **CHECK constraints for business rules** — Condenser must have OEM = 'Maruti Suzuki' and condenser_cause not null; compressor must have compressor_cause not null. Enforced at database level.
4. **Index for query patterns** — Indexes match the admin portal query patterns (filtering, sorting, aggregation).
5. **Immutability of audit_log** — Audit log is INSERT-only. No UPDATE or DELETE privileges granted to the API role for this table.

### Naming Conventions

- Tables: `snake_case`, plural (e.g., `mechanics`, `entries`, `payout_rates`)
- Columns: `snake_case`
- Enums: `snake_case` (e.g., `condenser_cause`, `approval_status`)
- Indexes: `idx_{table}_{column}` or `idx_{table}_{col1}_{col2}` for composite
- Foreign keys: `fk_{child_table}_{parent_table}`

---

## 2. Entity Relationship Diagram

```
+----------------+          +------------------+
|    mechanics   |          |    entries        |
+----------------+          +------------------+
| id (UUID)      |<-------- | id (UUID)         |
| mobile (uniq)  |    1:N   | mechanic_id (FK)  |
| name           |          | part_type         |
| workshop       |          | oem               |
| city           |          | model             |
| state          |          | variant           |
| language       |          | fuel_type         |
| upi_id         |          | year              |
| push_token     |          | registration      |
| active         |          | condenser_cause   |
| created_at     |          | compressor_cause  |
+----------------+          | severity           |
        |                   | odometer          |
        | refresh_tokens    | latitude          |
        | 1:N               | longitude         |
        v                   | mechanic_note     |
+----------------+          | voice_note_blob   |
| refresh_tokens |          | is_duplicate      |
+----------------+          | approval_status   |
| id (UUID)      |          | rejection_reason  |
| mechanic_id(FK)|          | rejection_note    |
| token_hash     |          | synced_at         |
| expires_at     |          | created_at        |
+----------------+          | approved_at       |
                            | rejected_at       |
+----------------+          +------------------+
|    otp_logs    |                |        |
+----------------+                | pho    | voice
| id (UUID)      |                | 1:3    | 0:1
| mobile         |                v        v
| hashed_otp     |          +----------+  (stored in
| expires_at     |          | photos   |   blob, path
| attempts       |          +----------+   in entry)
| created_at     |          | id (UUID)|
+----------------+          | entry    |
                            | slot     |
+----------------+          | blob_path|
|    admins      |          | file_size|
+----------------+          +----------+
| id (UUID)      |
| email (uniq)   |          +------------------+
| password_hash  |          |   payout_rates    |
| name           |          +------------------+
| created_at     |          | id (UUID)         |
+----------------+          | part_type         |
        |                   | rate (paise)      |
        | admin_sessions    | effective_from    |
        | 1:N               | set_by (admin FK) |
        v                   | created_at        |
+----------------+          +------------------+
| admin_sessions |
+----------------+          +------------------+
| id (UUID)      |          | monthly_payouts  |
| admin_id (FK)  |          +------------------+
| expires_at     |          | id (UUID)         |
| created_at     |          | mechanic_id (FK)  |
+----------------+          | year              |
                            | month             |
+----------------+          | condenser_count   |
| payout_entry   |          | compressor_count  |
| snapshots      |          | amount_paise      |
+----------------+          | upi_id_snapshot   |
| id (UUID)      |          | status            |
| payout_id (FK) |          | transaction_ref   |
| entry_id (FK)  |          | paid_at           |
+----------------+          | created_at        |
                            +------------------+
+----------------+
|  audit_logs     |
+----------------+
| id (UUID)       |
| admin_id (FK)   |
| action          |
| target_type     |
| target_id       |
| old_value (JSONB)|
| new_value (JSONB)|
| ip_address      |
| user_agent      |
| created_at      |
+----------------+
```

---

## 3. Enum Type Definitions

```sql
-- Part type
CREATE TYPE part_type AS ENUM ('condenser', 'compressor');

-- Condenser failure causes
CREATE TYPE condenser_cause AS ENUM (
  'stone_impact',
  'corrosion',
  'accident_damage',
  'pressure_failure',
  'blockage',
  'manufacturing_defect',
  'unknown'
);

-- Compressor failure causes
CREATE TYPE compressor_cause AS ENUM (
  'seized',
  'gas_leak',
  'noise_bearing_failure',
  'electrical_failure',
  'pressure_failure',
  'manufacturing_defect',
  'unknown'
);

-- Severity
CREATE TYPE severity AS ENUM ('minor', 'major', 'complete_failure');

-- Fuel type
CREATE TYPE fuel_type AS ENUM ('petrol', 'diesel', 'cng', 'electric', 'hybrid');

-- Approval status
CREATE TYPE approval_status AS ENUM ('approved', 'rejected');

-- Rejection reason
CREATE TYPE rejection_reason AS ENUM (
  'fraudulent',
  'incomplete',
  'duplicate',
  'poor_quality',
  'other'
);

-- Payout status
CREATE TYPE payout_status AS ENUM ('pending', 'paid');

-- Payout rate part type
CREATE TYPE payout_part_type AS ENUM ('condenser', 'compressor');

-- Maruti Suzuki models
CREATE TYPE maruti_model AS ENUM (
  'swift',
  'baleno',
  'wagonr',
  'dzire',
  'brezza',
  'alto_k10',
  'ertiga',
  'xl6',
  'ignis',
  'spresso',
  'celerio',
  'grand_vitara',
  'jimny'
);
```

---

## 4. Complete Table Definitions

### 4.1 Mechanics

```sql
CREATE TABLE mechanics (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  mobile VARCHAR(10) NOT NULL,
  name VARCHAR(100) NOT NULL,
  workshop VARCHAR(200) NOT NULL,
  city VARCHAR(100) NOT NULL,
  state VARCHAR(100) NOT NULL,
  language VARCHAR(10) NOT NULL DEFAULT 'en',
  upi_id VARCHAR(100),
  push_token TEXT,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

  CONSTRAINT uq_mechanics_mobile UNIQUE (mobile),
  CONSTRAINT chk_mechanics_mobile CHECK (mobile ~ '^\d{10}$'),
  CONSTRAINT chk_mechanics_language CHECK (
    language IN ('en', 'hi', 'ta', 'te', 'ml', 'kn', 'mr', 'gu', 'bn', 'pa')
  )
);

CREATE INDEX idx_mechanics_mobile ON mechanics (mobile);
CREATE INDEX idx_mechanics_state ON mechanics (state);
CREATE INDEX idx_mechanics_active ON mechanics (active) WHERE active = TRUE;

-- Row size: ~300 bytes
-- Estimated growth: 500–2,000 rows at 12 months
```

### 4.2 Entries

```sql
CREATE TABLE entries (
  id UUID PRIMARY KEY,  -- generated on-device for offline-first
  mechanic_id UUID NOT NULL,
  part_type part_type NOT NULL,
  oem VARCHAR(100) NOT NULL DEFAULT 'Maruti Suzuki',
  model VARCHAR(100) NOT NULL,
  variant VARCHAR(100),
  fuel_type fuel_type NOT NULL,
  year SMALLINT NOT NULL,
  registration VARCHAR(12),
  condenser_cause condenser_cause,
  compressor_cause compressor_cause,
  severity severity NOT NULL,
  odometer INTEGER NOT NULL,
  latitude DOUBLE PRECISION,
  longitude DOUBLE PRECISION,
  mechanic_note VARCHAR(200),
  voice_note_blob_path VARCHAR(500),
  is_duplicate BOOLEAN NOT NULL DEFAULT FALSE,
  approval_status approval_status NOT NULL DEFAULT 'approved',
  rejection_reason rejection_reason,
  rejection_note VARCHAR(500),
  synced_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  approved_at TIMESTAMPTZ,
  rejected_at TIMESTAMPTZ,

  CONSTRAINT fk_entries_mechanic FOREIGN KEY (mechanic_id) REFERENCES mechanics(id)
    ON DELETE RESTRICT,

  CONSTRAINT chk_entries_oem CHECK (
    (part_type = 'condenser' AND oem = 'Maruti Suzuki') OR
    (part_type = 'compressor')
  ),

  CONSTRAINT chk_entries_cause CHECK (
    (part_type = 'condenser' AND condenser_cause IS NOT NULL AND compressor_cause IS NULL) OR
    (part_type = 'compressor' AND compressor_cause IS NOT NULL AND condenser_cause IS NULL)
  ),

  CONSTRAINT chk_entries_year CHECK (year >= 2000 AND year <= EXTRACT(YEAR FROM NOW())::INT + 1),

  CONSTRAINT chk_entries_odometer CHECK (odometer >= 100 AND odometer <= 9999999),

  CONSTRAINT chk_entries_registration CHECK (
    registration IS NULL OR
    (registration ~ '^[A-Z0-9]{5,12}$')
  ),

  CONSTRAINT chk_entries_coordinates CHECK (
    (latitude IS NULL AND longitude IS NULL) OR
    (latitude >= 6.5 AND latitude <= 35.5 AND longitude >= 68.0 AND longitude <= 97.5)
  ),

  CONSTRAINT chk_entries_rejection CHECK (
    (approval_status = 'rejected' AND rejection_reason IS NOT NULL) OR
    (approval_status = 'approved' AND rejection_reason IS NULL)
  ),

  CONSTRAINT chk_entries_approval_dates CHECK (
    (approval_status = 'approved' AND approved_at IS NOT NULL AND rejected_at IS NULL) OR
    (approval_status = 'rejected' AND rejected_at IS NOT NULL AND approved_at IS NULL)
  )
);

-- Indexes for admin portal queries
CREATE INDEX idx_entries_mechanic_id ON entries (mechanic_id);
CREATE INDEX idx_entries_created_at ON entries (created_at DESC);
CREATE INDEX idx_entries_part_type ON entries (part_type);
CREATE INDEX idx_entries_model ON entries (model);
CREATE INDEX idx_entries_oem ON entries (oem);
CREATE INDEX idx_entries_state ON entries (state);
CREATE INDEX idx_entries_approval_status ON entries (approval_status);
CREATE INDEX idx_entries_severity ON entries (severity);

-- Composite indexes for common filter combinations
CREATE INDEX idx_entries_part_type_created ON entries (part_type, created_at DESC);
CREATE INDEX idx_entries_model_created ON entries (model, created_at DESC);
CREATE INDEX idx_entries_approval_created ON entries (approval_status, created_at DESC);

-- Index for duplicate detection query
CREATE INDEX idx_entries_registration_part_date ON entries (registration, part_type, created_at DESC)
  WHERE registration IS NOT NULL;

-- Index for monthly payout calculation
CREATE INDEX idx_entries_payout_calc ON entries (mechanic_id, approval_status, created_at)
  WHERE approval_status = 'approved';

-- Row size: ~500 bytes
-- Estimated growth: 45,000–180,000 rows at 12 months
```

### 4.3 Photos

```sql
CREATE TABLE photos (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  entry_id UUID NOT NULL,
  slot VARCHAR(20) NOT NULL,
  blob_path VARCHAR(500) NOT NULL,
  file_size INTEGER NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

  CONSTRAINT fk_photos_entry FOREIGN KEY (entry_id) REFERENCES entries(id)
    ON DELETE CASCADE,

  CONSTRAINT chk_photos_slot CHECK (slot IN ('damage', 'label', 'installed')),

  CONSTRAINT chk_photos_file_size CHECK (file_size > 0 AND file_size <= 5242880)  -- 5 MB
);

CREATE INDEX idx_photos_entry_id ON photos (entry_id);
CREATE INDEX idx_photos_slot ON photos (slot);

-- Row size: ~200 bytes
-- Estimated growth: 3× entries = 135,000–540,000 rows at 12 months
```

### 4.4 Admins

```sql
CREATE TABLE admins (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  name VARCHAR(100) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

  CONSTRAINT uq_admins_email UNIQUE (email),
  CONSTRAINT chk_admins_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- Row size: ~200 bytes
-- Estimated growth: < 10 rows
```

### 4.5 Admin Sessions

```sql
CREATE TABLE admin_sessions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  admin_id UUID NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

  CONSTRAINT fk_admin_sessions_admin FOREIGN KEY (admin_id) REFERENCES admins(id)
    ON DELETE CASCADE
);

CREATE INDEX idx_admin_sessions_admin_id ON admin_sessions (admin_id);
CREATE INDEX idx_admin_sessions_expires ON admin_sessions (expires_at);

-- Row size: ~100 bytes
-- Estimated growth: proportional to admin login frequency
```

### 4.6 Refresh Tokens

```sql
CREATE TABLE refresh_tokens (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  mechanic_id UUID NOT NULL,
  token_hash VARCHAR(255) NOT NULL,  -- SHA-256 hash of the refresh token
  expires_at TIMESTAMPTZ NOT NULL,
  used BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

  CONSTRAINT fk_refresh_tokens_mechanic FOREIGN KEY (mechanic_id) REFERENCES mechanics(id)
    ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_mechanic ON refresh_tokens (mechanic_id);
CREATE INDEX idx_refresh_tokens_hash ON refresh_tokens (token_hash);

-- Row size: ~200 bytes
-- Estimated growth: ~10× mechanics (each token rotation creates a new row)
```

### 4.7 OTP Logs

```sql
CREATE TABLE otp_logs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  mobile VARCHAR(10) NOT NULL,
  hashed_otp VARCHAR(255) NOT NULL,  -- SHA-256 hash
  expires_at TIMESTAMPTZ NOT NULL,
  attempts SMALLINT NOT NULL DEFAULT 0,
  verified BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

  CONSTRAINT chk_otp_logs_mobile CHECK (mobile ~ '^\d{10}$'),
  CONSTRAINT chk_otp_logs_attempts CHECK (attempts >= 0 AND attempts <= 10)
);

CREATE INDEX idx_otp_logs_mobile ON otp_logs (mobile);
CREATE INDEX idx_otp_logs_created ON otp_logs (created_at DESC);

-- Row size: ~100 bytes
-- Estimated growth: ~3× mechanics per month (send + verify + resend)
```

### 4.8 Payout Rates

```sql
CREATE TABLE payout_rates (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  part_type payout_part_type NOT NULL,
  rate_paise INTEGER NOT NULL,  -- amount in paise (₹1 = 100 paise)
  effective_from DATE NOT NULL,
  set_by UUID NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

  CONSTRAINT fk_payout_rates_admin FOREIGN KEY (set_by) REFERENCES admins(id)
    ON DELETE RESTRICT,

  CONSTRAINT chk_payout_rates_amount CHECK (rate_paise >= 0)
);

CREATE INDEX idx_payout_rates_part_type ON payout_rates (part_type, effective_from DESC);

-- Row size: ~100 bytes
-- Estimated growth: < 50 rows (rate changes are infrequent)
```

### 4.9 Monthly Payouts

```sql
CREATE TABLE monthly_payouts (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  mechanic_id UUID NOT NULL,
  year SMALLINT NOT NULL,
  month SMALLINT NOT NULL,
  condenser_count INTEGER NOT NULL DEFAULT 0,
  compressor_count INTEGER NOT NULL DEFAULT 0,
  condenser_rate_paise INTEGER NOT NULL,
  compressor_rate_paise INTEGER NOT NULL,
  amount_paise INTEGER NOT NULL,  -- total = (condenser_count × condenser_rate) + (compressor_count × compressor_rate)
  upi_id_snapshot VARCHAR(100),
  status payout_status NOT NULL DEFAULT 'pending',
  transaction_ref VARCHAR(100),
  paid_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

  CONSTRAINT fk_monthly_payouts_mechanic FOREIGN KEY (mechanic_id) REFERENCES mechanics(id)
    ON DELETE RESTRICT,

  CONSTRAINT chk_monthly_payouts_month CHECK (month >= 1 AND month <= 12),
  CONSTRAINT chk_monthly_payouts_year CHECK (year >= 2024 AND year <= 2030),

  CONSTRAINT uq_monthly_payouts_mechanic_month UNIQUE (mechanic_id, year, month)
);

CREATE INDEX idx_monthly_payouts_mechanic ON monthly_payouts (mechanic_id);
CREATE INDEX idx_monthly_payouts_status ON monthly_payouts (status);
CREATE INDEX idx_monthly_payouts_year_month ON monthly_payouts (year DESC, month DESC);

-- Row size: ~200 bytes
-- Estimated growth: 500–2,000 rows per month = 6,000–24,000 rows at 12 months
```

### 4.10 Payout Entry Snapshots

```sql
CREATE TABLE payout_entry_snapshots (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  payout_id UUID NOT NULL,
  entry_id UUID NOT NULL,

  CONSTRAINT fk_snapshots_payout FOREIGN KEY (payout_id) REFERENCES monthly_payouts(id)
    ON DELETE CASCADE,

  CONSTRAINT fk_snapshots_entry FOREIGN KEY (entry_id) REFERENCES entries(id)
    ON DELETE RESTRICT,

  CONSTRAINT uq_snapshots_payout_entry UNIQUE (payout_id, entry_id)
);

CREATE INDEX idx_snapshots_payout ON payout_entry_snapshots (payout_id);
CREATE INDEX idx_snapshots_entry ON payout_entry_snapshots (entry_id);

-- Row size: ~100 bytes
-- Estimated growth: equal to number of approved entries
```

### 4.11 Audit Logs

```sql
CREATE TABLE audit_logs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  admin_id UUID,  -- NULL for system actions
  action VARCHAR(50) NOT NULL,
  target_type VARCHAR(50) NOT NULL,  -- 'entry', 'mechanic', 'payout_rate', 'payout'
  target_id VARCHAR(255),  -- UUID of the target entity
  old_value JSONB,
  new_value JSONB,
  ip_address VARCHAR(45),
  user_agent TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

  CONSTRAINT fk_audit_logs_admin FOREIGN KEY (admin_id) REFERENCES admins(id)
    ON DELETE SET NULL
);

CREATE INDEX idx_audit_logs_admin ON audit_logs (admin_id);
CREATE INDEX idx_audit_logs_target ON audit_logs (target_type, target_id);
CREATE INDEX idx_audit_logs_created ON audit_logs (created_at DESC);

-- Row size: ~500 bytes
-- Estimated growth: 5,000–20,000 rows at 12 months (roughly 1 audit per 10 entries + admin actions)
```

---

## 5. Prisma Schema

```prisma
generator client {
  provider        = "prisma-client-js"
  previewFeatures = ["fullTextSearch"]
}

datasource db {
  provider = "postgresql"
  url      = env("DATABASE_URL")
}

// Enums
enum PartType {
  condenser
  compressor
}

enum CondenserCause {
  stone_impact
  corrosion
  accident_damage
  pressure_failure
  blockage
  manufacturing_defect
  unknown
}

enum CompressorCause {
  seized
  gas_leak
  noise_bearing_failure
  electrical_failure
  pressure_failure
  manufacturing_defect
  unknown
}

enum Severity {
  minor
  major
  complete_failure
}

enum FuelType {
  petrol
  diesel
  cng
  electric
  hybrid
}

enum ApprovalStatus {
  approved
  rejected
}

enum RejectionReason {
  fraudulent
  incomplete
  duplicate
  poor_quality
  other
}

enum PayoutStatus {
  pending
  paid
}

enum PayoutPartType {
  condenser
  compressor
}

// Models
model Mechanic {
  id              String   @id @default(uuid()) @db.Uuid
  mobile          String   @unique @db.VarChar(10)
  name            String   @db.VarChar(100)
  workshop        String   @db.VarChar(200)
  city            String   @db.VarChar(100)
  state           String   @db.VarChar(100)
  language        String   @default("en") @db.VarChar(10)
  upiId           String?  @map("upi_id") @db.VarChar(100)
  pushToken       String?  @map("push_token") @db.Text
  active          Boolean  @default(true)
  createdAt       DateTime @default(now()) @map("created_at")
  updatedAt       DateTime @updatedAt @map("updated_at")

  entries         Entry[]
  refreshTokens   RefreshToken[]
  monthlyPayouts  MonthlyPayout[]
  otpLogs         OtpLog[]

  @@map("mechanics")
}

model Entry {
  id                  String           @id @db.Uuid
  mechanicId          String           @map("mechanic_id") @db.Uuid
  partType            PartType         @map("part_type")
  oem                 String           @default("Maruti Suzuki") @db.VarChar(100)
  model               String           @db.VarChar(100)
  variant             String?          @db.VarChar(100)
  fuelType            FuelType         @map("fuel_type")
  year                Int              @db.SmallInt
  registration        String?          @db.VarChar(12)
  condenserCause      CondenserCause?  @map("condenser_cause")
  compressorCause     CompressorCause? @map("compressor_cause")
  severity            Severity
  odometer            Int
  latitude            Float?
  longitude           Float?
  mechanicNote        String?          @map("mechanic_note") @db.VarChar(200)
  voiceNoteBlobPath   String?          @map("voice_note_blob_path") @db.VarChar(500)
  isDuplicate         Boolean          @default(false) @map("is_duplicate")
  approvalStatus      ApprovalStatus   @default(approved) @map("approval_status")
  rejectionReason     RejectionReason? @map("rejection_reason")
  rejectionNote       String?          @map("rejection_note") @db.VarChar(500)
  syncedAt            DateTime?        @map("synced_at")
  createdAt           DateTime         @default(now()) @map("created_at")
  approvedAt          DateTime?        @map("approved_at")
  rejectedAt          DateTime?        @map("rejected_at")

  mechanic            Mechanic         @relation(fields: [mechanicId], references: [id], onDelete: Restrict)
  photos              Photo[]
  payoutSnapshots     PayoutEntrySnapshot[]

  @@index([mechanicId])
  @@index([createdAt(sort: Desc)])
  @@index([partType])
  @@index([model])
  @@index([oem])
  @@index([approvalStatus])
  @@index([severity])
  @@index([registration, partType, createdAt(sort: Desc)])
  @@map("entries")
}

model Photo {
  id        String   @id @default(uuid()) @db.Uuid
  entryId   String   @map("entry_id") @db.Uuid
  slot      String   @db.VarChar(20)
  blobPath  String   @map("blob_path") @db.VarChar(500)
  fileSize  Int      @map("file_size")
  createdAt DateTime @default(now()) @map("created_at")

  entry     Entry    @relation(fields: [entryId], references: [id], onDelete: Cascade)

  @@index([entryId])
  @@map("photos")
}

model Admin {
  id           String   @id @default(uuid()) @db.Uuid
  email        String   @unique @db.VarChar(255)
  passwordHash String   @map("password_hash") @db.VarChar(255)
  name         String   @db.VarChar(100)
  createdAt    DateTime @default(now()) @map("created_at")
  updatedAt    DateTime @updatedAt @map("updated_at")

  sessions     AdminSession[]
  payoutRates  PayoutRate[]
  auditLogs    AuditLog[]

  @@map("admins")
}

model AdminSession {
  id        String   @id @default(uuid()) @db.Uuid
  adminId   String   @map("admin_id") @db.Uuid
  expiresAt DateTime @map("expires_at")
  createdAt DateTime @default(now()) @map("created_at")

  admin     Admin    @relation(fields: [adminId], references: [id], onDelete: Cascade)

  @@index([adminId])
  @@index([expiresAt])
  @@map("admin_sessions")
}

model RefreshToken {
  id          String   @id @default(uuid()) @db.Uuid
  mechanicId  String   @map("mechanic_id") @db.Uuid
  tokenHash   String   @map("token_hash") @db.VarChar(255)
  expiresAt   DateTime @map("expires_at")
  used        Boolean  @default(false)
  createdAt   DateTime @default(now()) @map("created_at")

  mechanic    Mechanic @relation(fields: [mechanicId], references: [id], onDelete: Cascade)

  @@index([mechanicId])
  @@index([tokenHash])
  @@map("refresh_tokens")
}

model OtpLog {
  id        String   @id @default(uuid()) @db.Uuid
  mobile    String   @db.VarChar(10)
  hashedOtp String   @map("hashed_otp") @db.VarChar(255)
  expiresAt DateTime @map("expires_at")
  attempts  Int      @default(0) @db.SmallInt
  verified  Boolean  @default(false)
  createdAt DateTime @default(now()) @map("created_at")

  @@index([mobile])
  @@index([createdAt(sort: Desc)])
  @@map("otp_logs")
}

model PayoutRate {
  id            String         @id @default(uuid()) @db.Uuid
  partType      PayoutPartType @map("part_type")
  ratePaise     Int            @map("rate_paise")
  effectiveFrom DateTime       @map("effective_from") @db.Date
  setBy         String         @map("set_by") @db.Uuid
  createdAt     DateTime       @default(now()) @map("created_at")

  admin         Admin          @relation(fields: [setBy], references: [id], onDelete: Restrict)

  @@index([partType, effectiveFrom(sort: Desc)])
  @@map("payout_rates")
}

model MonthlyPayout {
  id                  String        @id @default(uuid()) @db.Uuid
  mechanicId          String        @map("mechanic_id") @db.Uuid
  year                Int           @db.SmallInt
  month               Int           @db.SmallInt
  condenserCount      Int           @default(0) @map("condenser_count")
  compressorCount     Int           @default(0) @map("compressor_count")
  condenserRatePaise  Int           @map("condenser_rate_paise")
  compressorRatePaise Int           @map("compressor_rate_paise")
  amountPaise         Int           @map("amount_paise")
  upiIdSnapshot       String?       @map("upi_id_snapshot") @db.VarChar(100)
  status              PayoutStatus  @default(pending)
  transactionRef      String?       @map("transaction_ref") @db.VarChar(100)
  paidAt              DateTime?     @map("paid_at")
  createdAt           DateTime      @default(now()) @map("created_at")
  updatedAt           DateTime      @updatedAt @map("updated_at")

  mechanic            Mechanic      @relation(fields: [mechanicId], references: [id], onDelete: Restrict)
  snapshots           PayoutEntrySnapshot[]

  @@unique([mechanicId, year, month])
  @@index([mechanicId])
  @@index([status])
  @@index([year(sort: Desc), month(sort: Desc)])
  @@map("monthly_payouts")
}

model PayoutEntrySnapshot {
  id        String        @id @default(uuid()) @db.Uuid
  payoutId  String        @map("payout_id") @db.Uuid
  entryId   String        @map("entry_id") @db.Uuid

  payout    MonthlyPayout @relation(fields: [payoutId], references: [id], onDelete: Cascade)
  entry     Entry         @relation(fields: [entryId], references: [id], onDelete: Restrict)

  @@unique([payoutId, entryId])
  @@index([payoutId])
  @@index([entryId])
  @@map("payout_entry_snapshots")
}

model AuditLog {
  id          String   @id @default(uuid()) @db.Uuid
  adminId     String?  @map("admin_id") @db.Uuid
  action      String   @db.VarChar(50)
  targetType  String   @map("target_type") @db.VarChar(50)
  targetId    String?  @map("target_id") @db.VarChar(255)
  oldValue    Json?    @map("old_value") @db.JsonB
  newValue    Json?    @map("new_value") @db.JsonB
  ipAddress   String?  @map("ip_address") @db.VarChar(45)
  userAgent   String?  @map("user_agent") @db.Text
  createdAt   DateTime @default(now()) @map("created_at")

  admin       Admin?   @relation(fields: [adminId], references: [id], onDelete: SetNull)

  @@index([adminId])
  @@index([targetType, targetId])
  @@index([createdAt(sort: Desc)])
  @@map("audit_logs")
}
```

---

## 6. Relationship Descriptions

| Relationship | Type | Description |
|-------------|------|-------------|
| Mechanic → Entries | 1:N | One mechanic can log many entries |
| Entry → Photos | 1:N | One entry can have up to 3 photos |
| Mechanic → RefreshTokens | 1:N | One mechanic can have multiple refresh tokens (token rotation creates new rows) |
| Mechanic → MonthlyPayouts | 1:N | One mechanic has one payout record per month |
| MonthlyPayout → PayoutEntrySnapshots | 1:N | One payout references many entries |
| Entry → PayoutEntrySnapshots | 1:N | One entry can be part of exactly one payout (prevents double-counting) |
| Admin → PayoutRates | 1:N | One admin can set multiple payout rates over time |
| Admin → AuditLogs | 1:N | One admin generates many audit log entries |
| Admin → AdminSessions | 1:N | One admin can have multiple active sessions |

---

## 7. Key Constraints and Business Rules

1. **Condenser OEM lock:** `CHECK (part_type = 'condenser' AND oem = 'Maruti Suzuki')` — enforced at DB level
2. **Cause-part type matching:** Condenser entries must have condenser_cause, compressor entries must have compressor_cause — the other must be NULL
3. **Approval-rejection consistency:** Rejected entries must have a rejection_reason; approved entries must not
4. **Rejection requires reason:** `rejection_reason` is NOT NULL when `approval_status = 'rejected'`
5. **Unique mechanic per month:** One payout record per mechanic per month (unique constraint on mechanic_id + year + month)
6. **No double-counting entries:** PayoutEntrySnapshot prevents an entry from being included in multiple payouts
7. **Immutable audit_log:** API database user has only INSERT privilege on audit_log table
8. **Active mechanics only:** Deactivated mechanics cannot log in but their existing entries remain in the system

---

## 8. Query Patterns

### Q1: Dashboard KPIs

```sql
-- Total mechanics
SELECT COUNT(*) AS total FROM mechanics;

-- Active mechanics (entry in last 30 days)
SELECT COUNT(DISTINCT mechanic_id) AS active
FROM entries
WHERE created_at >= NOW() - INTERVAL '30 days';

-- Total entries
SELECT COUNT(*) AS total FROM entries;

-- Entries this month
SELECT COUNT(*) AS this_month
FROM entries
WHERE created_at >= DATE_TRUNC('month', NOW());

-- Pending review (rejected possible — usually auto-approved, but here for review-needed)
SELECT COUNT(*) AS pending_review
FROM entries
WHERE approval_status IS NULL;  -- if partial approval is implemented

-- Payout due this month
SELECT COALESCE(SUM(amount_paise), 0) / 100.0 AS payout_due_rupees
FROM monthly_payouts
WHERE status = 'pending'
  AND year = EXTRACT(YEAR FROM NOW())
  AND month = EXTRACT(MONTH FROM NOW());
```

### Q2: Entries by Model (Bar Chart)

```sql
SELECT
  model,
  COUNT(*) AS count
FROM entries
WHERE created_at >= NOW() - INTERVAL '90 days'
GROUP BY model
ORDER BY count DESC
LIMIT 10;
```

### Q3: Entries by State (Bar Chart)

```sql
SELECT
  m.state,
  COUNT(*) AS count
FROM entries e
JOIN mechanics m ON e.mechanic_id = m.id
WHERE e.created_at >= NOW() - INTERVAL '90 days'
GROUP BY m.state
ORDER BY count DESC
LIMIT 10;
```

### Q4: Entries by Failure Cause (Bar Chart)

```sql
-- Condenser causes
SELECT
  'condenser' AS part_type,
  e.condenser_cause AS cause,
  COUNT(*) AS count
FROM entries e
WHERE e.part_type = 'condenser'
  AND e.created_at >= NOW() - INTERVAL '90 days'
GROUP BY e.condenser_cause

UNION ALL

-- Compressor causes
SELECT
  'compressor' AS part_type,
  e.compressor_cause AS cause,
  COUNT(*) AS count
FROM entries e
WHERE e.part_type = 'compressor'
  AND e.created_at >= NOW() - INTERVAL '90 days'
GROUP BY e.compressor_cause

ORDER BY count DESC;
```

### Q5: Daily Volume (Last 30 Days)

```sql
SELECT
  DATE(created_at) AS day,
  COUNT(*) AS count
FROM entries
WHERE created_at >= NOW() - INTERVAL '30 days'
GROUP BY DATE(created_at)
ORDER BY day ASC;
```

### Q6: Entries by Fuel Type

```sql
SELECT
  fuel_type,
  COUNT(*) AS count
FROM entries
WHERE created_at >= NOW() - INTERVAL '90 days'
GROUP BY fuel_type
ORDER BY count DESC;
```

### Q7: Monthly Payout Calculation

```sql
-- Get approved entries for a mechanic in a given month
SELECT
  e.mechanic_id,
  COUNT(*) FILTER (WHERE e.part_type = 'condenser') AS condenser_count,
  COUNT(*) FILTER (WHERE e.part_type = 'compressor') AS compressor_count
FROM entries e
WHERE e.mechanic_id = $1
  AND e.approval_status = 'approved'
  AND e.approved_at >= DATE_TRUNC('month', DATE($2 || '-01'))
  AND e.approved_at < DATE_TRUNC('month', DATE($2 || '-01')) + INTERVAL '1 month'
GROUP BY e.mechanic_id;
```

### Q8: Duplicate Detection

```sql
SELECT COUNT(*) AS duplicate_count
FROM entries
WHERE registration = $1
  AND part_type = $2
  AND created_at >= NOW() - INTERVAL '30 days'
  AND id != $3;  -- exclude current entry if re-checking
```

### Q9: Mechanic Entry History (Admin Detail)

```sql
SELECT
  e.id,
  e.part_type,
  e.model,
  e.oem,
  e.severity,
  e.approval_status,
  e.created_at
FROM entries e
WHERE e.mechanic_id = $1
ORDER BY e.created_at DESC
LIMIT 50 OFFSET $2;
```

### Q10: Payout Monthly Summary (Admin)

```sql
SELECT
  m.id AS mechanic_id,
  m.name,
  m.mobile,
  m.upi_id,
  COALESCE(mp.condenser_count, 0) AS condenser_count,
  COALESCE(mp.compressor_count, 0) AS compressor_count,
  COALESCE(mp.amount_paise, 0) / 100.0 AS amount_rupees,
  mp.status,
  mp.transaction_ref
FROM mechanics m
LEFT JOIN monthly_payouts mp ON m.id = mp.mechanic_id
  AND mp.year = $1
  AND mp.month = $2
WHERE m.active = TRUE
ORDER BY m.name ASC;
```

---

## 9. Index Strategy

### Philosophy

The database is **write-heavy during sync** (batch inserts of entries) and **read-heavy during admin portal usage** (complex filtered queries). Index strategy prioritises:

1. **Filter columns** (part_type, model, approval_status, state, created_at) — these power the admin entry table filters
2. **Sort columns** (created_at DESC) — entries are displayed newest-first
3. **Join columns** (mechanic_id in entries, entry_id in photos) — all detail views require JOINs
4. **Search columns** (registration for duplicate detection) — infrequent but performance-critical when used
5. **Aggregation columns** (model, state, cause) — dashboard charts

### Index Summary

| Table | Index | Type | Purpose |
|-------|-------|------|---------|
| mechanics | mobile | B-tree, UNIQUE | Login lookup |
| mechanics | state | B-tree | Dashboard filtering |
| mechanics | active (partial) | B-tree, WHERE active | Active mechanic queries |
| entries | mechanic_id | B-tree | Mechanic entry history |
| entries | created_at DESC | B-tree | Default sort order |
| entries | part_type | B-tree | Admin filter |
| entries | model | B-tree | Admin filter, chart |
| entries | approval_status | B-tree | Admin filter |
| entries | severity | B-tree | Admin filter |
| entries | registration + part_type + created_at (partial) | B-tree, WHERE registration NOT NULL | Duplicate detection |
| entries | mechanic_id + approval_status + created_at (partial) | B-tree, WHERE approval_status = 'approved' | Payout calculation |
| entries | part_type + created_at DESC | B-tree (composite) | Common filter combo |
| entries | model + created_at DESC | B-tree (composite) | Common filter combo |
| entries | approval_status + created_at DESC | B-tree (composite) | Common filter combo |
| photos | entry_id | B-tree | Photo lookup for entry detail |
| monthly_payouts | mechanic_id | B-tree | Mechanic payout history |
| monthly_payouts | year DESC, month DESC | B-tree | Admin payout page |
| monthly_payouts | status | B-tree | Pending payout queries |
| payout_rates | part_type + effective_from DESC | B-tree | Current rate lookup |
| audit_logs | target_type + target_id | B-tree | Entity action history |
| audit_logs | created_at DESC | B-tree | Audit trail browsing |

---

## 10. Migration Strategy

### Prisma Migrations Workflow

```bash
# Development: Create migration from schema changes
npx prisma migrate dev --name add_rejection_note

# Staging: Apply migration
npx prisma migrate deploy

# Production: Apply migration (part of CI/CD)
npx prisma migrate deploy
```

### Safe Migration Principles

1. **Always backward-compatible:** Schema changes should not break running application code. Add columns as nullable with defaults before making them required.
2. **Deploy migrations during low-traffic periods:** Sync activity is lowest at night (Indian time).
3. **Test on staging first:** Run migrations against staging database before production.
4. **Have a rollback plan:** Prisma does not auto-rollback. For each migration, document the revert SQL.
5. **No destructive operations on production:** Avoid DROP COLUMN, DROP TABLE in initial versions. Prefer soft-delete or deprecate-and-ignore.

### Migration Workflow

```
1. Edit schema.prisma
2. Run `npx prisma migrate dev --name description` (creates migration SQL + applies locally)
3. Review migration SQL in prisma/migrations/<timestamp>/
4. Commit schema.prisma + migration folder
5. CI/CD runs `npx prisma migrate deploy` against staging
6. Test staging
7. Manual approval → CI/CD runs against production
```

---

## 11. Azure Blob Storage Structure

### Container: `partlog-media`

```
partlog-media/
├── photos/
│   ├── 2026/
│   │   ├── 01/
│   │   │   ├── <entry-uuid>/
│   │   │   │   ├── damage.jpg
│   │   │   │   ├── label.jpg
│   │   │   │   └── installed.jpg
│   │   │   └── <entry-uuid>/
│   │   │       └── damage.jpg
│   │   ├── 02/
│   │   └── ...
│   └── 2027/
└── audio/
    ├── 2026/
    │   ├── 01/
    │   │   ├── <entry-uuid>.m4a
    │   │   └── <entry-uuid>.m4a
    │   ├── 02/
    │   └── ...
    └── 2027/
```

### Naming Convention

- Photos: `photos/{YYYY}/{MM}/{entry_id}/{slot}.jpg`
- Audio: `audio/{YYYY}/{MM}/{entry_id}.m4a`
- No spaces, no special characters (only UUIDs, letters, numbers, underscores)
- Slots: `damage`, `label`, `installed`

### Lifecycle Policy

- **Hot tier:** First 90 days (frequently accessed by admin for review)
- **Cool tier:** After 90 days (infrequently accessed, 30% cheaper storage)
- **Archive tier:** Not implemented in v1 (may be needed at year 2+)
- **Deletion:** Never (unless explicitly purged by admin)

---

## 12. SQLite Schema on Device

```sql
-- Local entries pending sync
CREATE TABLE local_entries (
  local_id INTEGER PRIMARY KEY AUTOINCREMENT,
  server_id TEXT UNIQUE,  -- UUID after sync
  part_type TEXT NOT NULL,
  oem TEXT NOT NULL DEFAULT 'Maruti Suzuki',
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
  voice_note_local_path TEXT,
  created_at TEXT NOT NULL,  -- ISO 8601
  sync_status TEXT NOT NULL DEFAULT 'pending',  -- pending | uploading | synced | failed
  retry_count INTEGER DEFAULT 0,
  next_retry_at TEXT,
  last_error TEXT,
  is_duplicate INTEGER DEFAULT 0
);

-- Local photos (before sync)
CREATE TABLE local_photos (
  local_id INTEGER PRIMARY KEY AUTOINCREMENT,
  entry_local_id INTEGER NOT NULL REFERENCES local_entries(local_id),
  slot TEXT NOT NULL,  -- 'damage' | 'label' | 'installed'
  local_path TEXT NOT NULL,
  blob_path TEXT,  -- server path after upload
  uploaded INTEGER DEFAULT 0
);

-- Sync queue
CREATE TABLE sync_queue (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  entry_local_id INTEGER NOT NULL REFERENCES local_entries(local_id),
  priority INTEGER DEFAULT 0,
  created_at TEXT NOT NULL
);

-- Cached model data for autocomplete
CREATE TABLE model_cache (
  model TEXT NOT NULL,
  variant TEXT NOT NULL,
  PRIMARY KEY (model, variant)
);

-- App settings
CREATE TABLE app_settings (
  key TEXT PRIMARY KEY,
  value TEXT NOT NULL
);
```

---

## 13. Data Retention Policy

| Table | Retention | Cleanup |
|-------|-----------|---------|
| entries | Indefinite | No automatic deletion |
| photos | Indefinite (blob) | No automatic deletion |
| mechanics | Indefinite (deactivated status instead of delete) | No automatic deletion |
| otp_logs | 90 days | `DELETE FROM otp_logs WHERE created_at < NOW() - INTERVAL '90 days'` |
| refresh_tokens | 90 days after expiry | `DELETE FROM refresh_tokens WHERE expires_at < NOW() - INTERVAL '90 days'` |
| admin_sessions | 30 days after expiry | `DELETE FROM admin_sessions WHERE expires_at < NOW() - INTERVAL '30 days'` |
| audit_logs | 2 years | Review before deletion; archive to cold storage if needed |
| monthly_payouts | Indefinite | Financial records — no deletion |
| payout_entry_snapshots | Indefinite | Linked to payouts — no deletion |

---

## 14. Storage Projections

### Database Size

| Time | Entries | Mechanics | Photos | Audit Logs | Total Estimated Size |
|------|---------|-----------|--------|------------|---------------------|
| Launch | 0 | 0 | 0 | 0 | ~50 MB (empty DB overhead) |
| 3 months | ~15,000 | 500 | ~45,000 | ~2,000 | ~65 MB |
| 6 months | ~45,000 | 1,000 | ~135,000 | ~5,000 | ~100 MB |
| 12 months | ~180,000 | 2,000 | ~540,000 | ~20,000 | ~250 MB |

**Azure PostgreSQL B2ms (128 GB) is over-provisioned but intentional for query performance (8 GB RAM).**

### Blob Storage Size

| Time | Photos (3 × 300 KB average) | Audio (50% of entries × 200 KB) | Total |
|------|---------------------------|--------------------------------|-------|
| 3 months | ~13.5 GB | ~1.5 GB | ~15 GB |
| 6 months | ~40.5 GB | ~4.5 GB | ~45 GB |
| 12 months | ~162 GB | ~18 GB | ~180 GB |

**50 GB initial estimate is insufficient for 12 months.** Recommend starting with 100 GB or implementing lifecycle policy to move blobs > 90 days to Cool tier earlier than planned.
