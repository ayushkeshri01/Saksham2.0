# UI/UX Wireframes and User Flows — PartLog

**Version:** 1.0  
**Date:** July 2026  
**Prepared by:** Product Designer / Founder

---

## 1. Design Principles

1. **Speed over beauty.** Mechanics need to log an entry in under 90 seconds. Every animation, transition, or visual flourish is a distraction. No micro-interactions, no parallax, no loading skeletons that fade in.
2. **Field-first.** The mobile app is used outdoors (bright sunlight, rain) and in dim workshops. High contrast, large touch targets, no subtle greys.
3. **Text is content, not decoration.** No marketing copy, no onboarding carousels, no illustrations. Every word on screen serves a function.
4. **Data density for admin.** The web portal is a tool, not a brochure. Show as much data as possible on screen. No whitespace for elegance — whitespace exists to improve scanning.
5. **One primary action per screen.** Every screen has exactly one thing the user should do. Everything else is secondary.

---

## 2. User Personas

### Persona 1: Rajesh — Mechanic

- **Age:** 34
- **Location:** Meerut, Uttar Pradesh
- **Education:** Class 10 pass
- **Workshop:** "Meerut Auto AC Service" — 2 employees
- **Phone:** Realme Narzo 30 (Android 11, 4GB RAM, ₹11,999)
- **Languages:** Hindi (preferred), understands basic English
- **Digital habits:** Uses WhatsApp daily, watches YouTube videos for repair guidance, has used PhonePe for UPI payments
- **Motivation for PartLog:** ₹3,000–₹5,000 extra income per month
- **Frustrations:** Slow apps, apps that require constant internet, English-only apps, apps that ask too many questions
- **Entry volume:** 3–5 condenser replacements per week, 1–2 compressors

### Persona 2: Sunita — Admin (Operations Analyst)

- **Age:** 42
- **Location:** Delhi
- **Education:** B.Com, MBA
- **Role:** Supply chain analyst at Vikas Group, manages inventory data and reports to management
- **Devices:** Dell Windows laptop, 24-inch external monitor, Chrome browser
- **Digital habits:** Spends 6+ hours/day in Excel, comfortable with web applications, uses Zoho Books for accounting
- **Motivation for PartLog:** Wants to move from intuition-based to data-driven supply planning
- **Frustrations:** Data that cannot be exported, charts that do not drill down, slow page loads, having to click too many times to find information
- **Usage pattern:** Reviews entries 30 min every morning, processes payments 2 days at month-end, exports reports weekly

---

## 3. Complete User Journey Maps

### Journey: Mechanic — First Use to Regular Logger

```
Stage 1: Discovery
  Touchpoint: WhatsApp message from distributor or Vikas Group agent
  Action: Receives link to download app from Play Store
  Emotion: Curious but skeptical — "Will I actually get paid?"
  Pain point: Play Store invite link process is confusing
  ---
Stage 2: First Launch
  Touchpoint: Opens app for first time
  Action: Selects language → Enters mobile → Receives OTP → Verifies → Fills onboarding form
  Emotion: Cautious — wants to see if it works
  Pain point: OTP may take 10–30 seconds to arrive
  ---
Stage 3: First Entry (Tutorial effect)
  Touchpoint: Home screen after onboarding
  Action: Creates first condenser entry — model, cause, photo, submits
  Emotion: "That was fast" — positive surprise
  Pain point: Camera permission request may cause hesitation
  ---
Stage 4: Habit formation (Weeks 2–4)
  Touchpoint: Logs entries after each condenser/compressor replacement
  Action: Opens app, selects part type, fills form (muscle memory developing), submits
  Emotion: Neutral — it is now part of workflow
  ---
Stage 5: First Payout
  Touchpoint: Month-end, receives push notification
  Action: Checks earnings screen → Sees amount → Receives UPI payment
  Emotion: Happy, validated — "This is real"
  ---
Stage 6: Regular User (Month 2+)
  Touchpoint: Daily workshop operations
  Action: Logs entries automatically after each job
  Emotion: Indifferent — the app is now invisible
```

### Journey: Admin — First Login to Regular Operations

```
Stage 1: Account Provisioning
  Touchpoint: Receives login credentials via email
  Action: Opens portal URL → Logs in → Changes password
  Emotion: Curious
  ---
Stage 2: Dashboard First Look
  Touchpoint: Dashboard page
  Action: Scans KPI cards → Clicks on chart drill-downs → Browses entries table
  Emotion: Impressed — finally seeing field data
  Pain point: Entries table may be empty in first days
  ---
Stage 3: Daily Review Routine
  Touchpoint: Entries table
  Action: Filters by "Today" → Reviews each entry quickly → Rejects obvious fakes
  Emotion: Routine, efficient
  ---
Stage 4: Monthly Payout
  Touchpoint: Payout management page
  Action: Selects month → Reviews amounts → Marks paid → Enters UPI references
  Emotion: Satisfied — system does the math
  Pain point: Manual UPI transfers are tedious at 200+ mechanics
  ---
Stage 5: Report Generation
  Touchpoint: Export page
  Action: Selects filters → Exports CSV → Opens in Excel → Pivots data
  Emotion: Power user — data is usable
```

---

## 4. User Flows

### Flow 1: Mechanic Registration and Onboarding

```
[Language Selection]
    |
    v
[Mobile Number Entry]
    |  -- enters 10-digit mobile, taps "Send OTP"
    v
[OTP Verification]
    |  -- enters 6-digit OTP
    |  -- (if invalid) shows error, allows retry
    |  -- (if expired) shows "Resend OTP" after 30s
    v
(Check if returning user)
    |--- New user?
    |      v
    |   [Onboarding Form]
    |      |  -- enters Name (required)
    |      |  -- enters Workshop Name (required)
    |      |  -- selects City (required, with autocomplete)
    |      |  -- selects State (required, dropdown)
    |      |  -- language already selected; shown but editable
    |      |  -- taps "Save & Continue"
    |      v
    |   [Home Screen]
    |
    |--- Returning user?
           v
        [Home Screen] (auto-login via stored refresh token)
```

### Flow 2: Logging a Condenser Entry (Online)

```
[Home Screen]
    |  -- taps "New Entry"
    v
[Part Type Selection]
    |  -- taps "Condenser"
    v
[Vehicle Screen]
    |  -- selects model from chip grid (13 Maruti Suzuki models)
    |  -- enters variant (free text with autocomplete suggestions)
    |  -- selects fuel type (Petrol/Diesel/CNG/Electric/Hybrid)
    |  -- selects year (picker, 2010–current)
    |  -- enters registration (optional, auto-uppercase)
    |  -- taps "Next"
    v
[Failure Screen]
    |  -- selects failure cause (radio list: Stone impact/Corrosion/Accident damage/
    |     Pressure failure/Blockage/Manufacturing defect/Unknown)
    |  -- selects severity (chips: Minor/Major/Complete failure)
    |  -- enters odometer (numeric, mandatory)
    |  -- taps "Next"
    v
[Evidence Screen]
    |  -- captures/locates up to 3 photos (damage/label/installed)
    |  -- records voice note (optional, 60s max)
    |  -- taps "Next"
    v
[Notes Screen]
    |  -- enters text note (optional, 200 char max)
    |  -- GPS status indicator (capturing automatically)
    |  -- taps "Review"
    v
[Review & Submit Screen]
    |  -- shows all entered data in summary format
    |  -- includes edit button for each section
    |  -- taps "Submit Entry"
    v
[Confirmation Screen]
    |  -- shows "Entry Submitted" with entry ID
    |  -- option to "Log Another" or "Back to Home"
```

### Flow 3: Logging a Compressor Entry

```
(Same as condenser flow, except:)

[Vehicle Screen]
    |  -- enters OEM (free text input, placeholder "e.g., Tata, Hyundai")
    |  -- enters model (free text input with autocomplete suggestions)
    |  -- (variant, fuel, year, registration same as condenser)

[Failure Screen]
    |  -- selects failure cause (Seized/Gas leak/Noise-bearing failure/
    |     Electrical failure/Pressure failure/Manufacturing defect/Unknown)
    |  -- severity, odometer same as condenser
```

### Flow 4: Offline Entry and Background Sync

```
[All screens same as online flow]

[On "Submit" while offline]
    -- Entry saved to local SQLite immediately
    -- sync_status = 'pending'
    -- Shows "Saved locally. Will sync when online."
    -- Entry appears in "Pending Sync" section on Home screen

[Background sync]
    -- expo-background-fetch runs every 5 minutes
    -- Checks network connectivity
    -- If online:
       1. For each pending entry (FIFO):
          a. Get SAS URLs from server
          b. Upload photos to Azure Blob
          c. POST entry JSON to /api/sync
          d. On success: mark synced in local DB
          e. On failure: increment retry, schedule next retry
    -- If offline: skip, wait for next cycle

[Manual sync]
    -- User can tap "Sync Now" on Home screen
    -- Shows progress: "Syncing 3 of 5..."
```

### Flow 5: Duplicate Warning Handling

```
[Vehicle Screen]
    |  -- user enters registration number
    v
[Background check]
    -- API called with registration + part_type
    -- If duplicate found (same reg + same part type within 30 days):

    v
[Warning Dialog]
    "This vehicle (DL5CAB1234) was logged on 15 June 2026.
     Are you sure this is a new repair?"
    [Cancel]  [Submit Anyway]

    |-- If "Submit Anyway": entry submitted with is_duplicate = true
    |-- If "Cancel": return to Vehicle screen, user can change registraton
```

### Flow 6: Requesting Payout / Entering UPI ID

```
[Earnings Screen]
    |  -- shows this month: entries, approved, estimated payout
    |  -- monthly history list with status (pending/paid)
    |  -- if UPI ID not set: tap "Set UPI ID to receive payments"
    v
[UPI ID Entry]
    |  -- text input with format validation (name@provider)
    |  -- example placeholder: "rajesh@paytm"
    |  -- "Save" button
    v
[Earnings Screen (updated)]
    |  -- UPI ID shown
    |  -- "Payout will be processed at month end"
```

### Flow 7: Receiving Payout Notification

```
[Background]
    -- Admin marks payout as paid in portal
    -- Server sends Expo push notification

[Mobile]
    -- Notification: "Payout of ₹3,200 credited to rajesh@paytm (Ref: UPI123456)"
    -- Tapping notification opens Earnings screen
    -- Monthly entry shows status "Paid" with green badge
```

### Flow 8: Admin Reviewing and Rejecting Entries

```
[Login Screen]
    |  -- email + password
    |  -- "Login" button
    v
[Dashboard]
    |  -- 5 KPI cards
    |  -- Charts (model, state, cause, daily volume)
    |  -- Click "View All Entries" or entries count
    v
[Entries Table]
    |  -- Paginated table (25 rows)
    |  -- Columns: Date, Mechanic, Part Type, Model, OEM, Severity, State, Status
    |  -- Filters: part type, model, state, status, date range, cause, severity
    |  -- Sort: click any column header
    |  -- Click row to open detail
    v
[Entry Detail (Side Panel)]
    |  -- Shows all entry fields
    |  -- Photo gallery with lightbox (click to zoom)
    |  -- Audio player for voice note
    |  -- GPS link (opens Google Maps)
    |  -- Entry activity log (created, synced, status changes)
    |
    |-- [Approve Button] (default auto-approved, visible for info)
    |
    |-- [Reject Button]
           v
    [Rejection Modal]
       |  -- Select reason (dropdown):
       |     fraudulent, incomplete, duplicate, poor_quality, other
       |  -- Optional note (200 chars)
       |  -- [Cancel] [Confirm Rejection]
       v
    [Entry updated to "Rejected" (red badge)]
       -- Entry excluded from payout calculation
       -- Audit log entry created
```

### Flow 9: Admin Processing Monthly Payout

```
[Payout Page]
    |  -- Month selector (default: previous month)
    |  -- Table: Mechanic Name | Mobile | UPI ID | Condenser Count | Compressor Count |
    |     Amount | Status
    |  -- Total row at bottom
    |
    v
[Review amounts]
    |  -- Admin checks totals against manual record
    |  -- Admin initiates UPI transfers via PhonePe / Google Pay (manual, outside system)
    |
    v
[For each paid mechanic]
    |  -- Click "Mark Paid" on row
    |  -- Enter UPI transaction reference number
    |  -- Auto-sets paid date to today
    v
[Payout marked "Paid" (green badge)]
    -- Server sends push notification to mechanic
    -- Audit log entry created
```

### Flow 10: Admin Exporting Data

```
[Export Page]
    |  -- Section: Entries Export
    |     |  -- Date range selector (required)
    |     |  -- Optional filters: part type, model, state, status
    |     |  -- Format: [CSV] [Excel]
    |     |  -- "Export" button
    |  -- Section: Payout Export
    |     |  -- Month selector (required)
    |     |  -- Format: [CSV] [Excel]
    |     |  -- "Export" button
    |
    v
[Download initiated]
    -- File generated server-side
    -- Downloaded to browser
    -- If >10,000 rows, warning: "Your export has N rows. Consider narrowing filters."
```

---

## 5. Screen-by-Screen Wireframe Specifications

### 5.1 MOBILE — Language Selection

**Purpose:** First screen on fresh install. Choose app language before anything else.

**Layout:**
```
+----------------------------------+
|                                  |
|   [Logo / App Name]              |
|   "PartLog"                      |
|                                  |
|   Select your language           |
|   अपनी भाषा चुनें               |
|                                  |
|   +----------------------------+ |
|   | English                    | |
|   +----------------------------+ |
|   +----------------------------+ |
|   | हिन्दी (Hindi)              | |
|   +----------------------------+ |
|   +----------------------------+ |
|   | தமிழ் (Tamil)               | |
|   +----------------------------+ |
|   +----------------------------+ |
|   | తెలుగు (Telugu)             | |
|   +----------------------------+ |
|   +----------------------------+ |
|   | മലയാളം (Malayalam)          | |
|   +----------------------------+ |
|   +----------------------------+ |
|   | ಕನ್ನಡ (Kannada)             | |
|   +----------------------------+ |
|   +----------------------------+ |
|   | मराठी (Marathi)             | |
|   +----------------------------+ |
|   +----------------------------+ |
|   | ગુજરાતી (Gujarati)          | |
|   +----------------------------+ |
|   +----------------------------+ |
|   | বাংলা (Bengali)             | |
|   +----------------------------+ |
|   +----------------------------+ |
|   | ਪੰਜਾਬੀ (Punjabi)            | |
|   +----------------------------+ |
|                                  |
+----------------------------------+
```

**Elements:**
- Screen title: "Select your language" (in English) / "अपनी भाषा चुनें" (in Hindi) — dual display only on this screen
- 10 language options: Each row shows language name in native script + English name in parentheses
- Language names are NOT translated — they always appear in their native script + English
- Tap a language → save to local storage → navigate to Mobile Number Entry screen
- No "Confirm" button needed — selection is immediate

**States:**
- Default: No selection
- Selected: Green checkmark on right side of tapped row
- Loading: None (selection is instant)

### 5.2 MOBILE — Mobile Number Entry

**Purpose:** Enter phone number to receive OTP.

**Layout:**
```
+----------------------------------+
|   < Back           PartLog       |
+----------------------------------+
|                                  |
|   Enter your mobile number       |
|   [  +91  |  98  _______  ]      |
|          [Send OTP]              |
|                                  |
|   By continuing, you agree to    |
|   our Terms & Conditions         |
|                                  |
+----------------------------------+
```

**Elements:**
- Header: Back button (left), "PartLog" (center)
- Title: "Enter your mobile number"
- Phone input: Country code (+91, fixed) + 10-digit number input
- Input type: Numeric keyboard, max 10 digits
- "Send OTP" button: Disabled (grey) until 10 digits entered; enabled (navy blue #1F4E79) when valid
- Terms text: Small, grey (#6B6B6B), below the button
- On tap "Send OTP": Show loading spinner on button → call API → navigate to OTP Verification screen on success

**States:**
- Default: Empty input, button disabled
- Filling: Partial input, button still disabled
- Complete: 10 digits entered, button enabled (navy)
- Loading: Button shows spinner, input disabled
- Error: "Failed to send OTP. Check your number and try again." (red text below input)
- Network error: "No internet connection. Please try again."

### 5.3 MOBILE — OTP Verification

**Purpose:** Enter 6-digit OTP received via SMS.

**Layout:**
```
+----------------------------------+
|   < Back           PartLog       |
+----------------------------------+
|                                  |
|   Enter OTP                      |
|                                  |
|   We've sent a 6-digit code to   |
|   98XXXXXXXX                     |
|                                  |
|   [  _  ][  _  ][  _  ][  _  ]  |
|   [  _  ][  _  ]                 |
|                                  |
|   [Verify OTP]                   |
|                                  |
|   Resend code in 00:30           |
|   or                             |
|   [Resend via Call]              |
|                                  |
+----------------------------------+
```

**Elements:**
- Header: Back button, "PartLog"
- Title: "Enter OTP"
- Subtitle: "We've sent a 6-digit code to 98XXXXXXXX"
- OTP input: 6 individual digit boxes (48×48px minimum)
- Auto-advance: Entering a digit moves focus to next box
- Auto-submit: When all 6 digits entered, auto-submit (no need to tap button)
- "Verify OTP" button: Below input, can also be tapped manually
- Timer: "Resend code in 00:30" — counts down from 30 seconds
- After timer: "Resend code" becomes tappable
- "Resend via Call" link: Alternative OTP delivery via voice call

**States:**
- Default: Empty boxes, button disabled, timer counting
- Partial: Some digits entered, not all
- Complete: All 6 digits entered, auto-submitting
- Verifying: Loading spinner, input and button disabled
- Invalid OTP: Red border on boxes, "Invalid code. 2 attempts remaining." (red text)
- Max attempts (3 failed): "Too many attempts. Try again in 5 minutes."
- Expired OTP: "Code expired. Request a new one." → Shows resend button
- Verified successfully: Navigate to onboarding (new) or home (returning)

### 5.4 MOBILE — Onboarding Form

**Purpose:** Collect basic profile information from new mechanic.

**Layout:**
```
+----------------------------------+
|             PartLog              |
+----------------------------------+
|                                  |
|   Welcome! Tell us about         |
|   yourself                       |
|                                  |
|   Full Name *                    |
|   [________________________]     |
|                                  |
|   Workshop Name *                |
|   [________________________]     |
|                                  |
|   City *                         |
|   [________________________]     |
|                                  |
|   State *                        |
|   [______ (dropdown) _______]    |
|                                  |
|   Language  [Hindi  >]           |
|                                  |
|   [Save & Continue]              |
|                                  |
+----------------------------------+
```

**Elements:**
- Title: "Welcome! Tell us about yourself"
- Full Name: Text input, required
- Workshop Name: Text input, required
- City: Text input with autocomplete (fetched from server, cached locally)
- State: Dropdown (28 states + 8 UTs of India)
- Language: Shows previously selected language; tappable to change
- "Save & Continue" button: Disabled until all required fields filled

**States:**
- Default: Empty fields, button disabled
- Error: Red border on invalid field + "This field is required" message
- Submitting: Button shows spinner
- Network error on submit: "Could not save. Check your connection and try again."
- Success: Navigate to Home screen

### 5.5 MOBILE — Home Screen

**Purpose:** Central hub showing key stats, sync status, and quick actions.

**Layout:**
```
+----------------------------------+
|   PartLog              [Avatar]  |
+----------------------------------+
|                                  |
|   +----------------------------+ |
|   | This Month                  | |
|   | Entries: 12                 | |
|   | Approved: 10                | |
|   | Estimated: ₹200             | |
|   +----------------------------+ |
|                                  |
|   [  New Entry  ]  [ Pending: 2]|
|                                  |
|   +----------------------------+ |
|   | Pending Sync: 2 entries     | |
|   | [Sync Now]                  | |
|   +----------------------------+ |
|                                  |
|   Recent Entries                 |
|   +----------------------------+ |
|   | Swift | Condenser | Today  | |
|   | Baleno | Compressor | Yest | |
|   | WagonR | Condenser | 2d ago| |
|   +----------------------------+ |
|                                  |
|   Bottom Nav:                    |
|   [Home] [Earnings] [Settings]   |
+----------------------------------+
```

**Elements:**
- Header: "PartLog" (left), profile avatar with first letter (right)
- This Month card: Entries count, approved count, estimated payout in ₹
- "New Entry" button: Primary action, large, navy blue (#1F4E79)
- "Pending" badge: Shows count of unsynced entries; tappable to view pending list
- Sync banner: Shows when entries are pending sync; "Sync Now" button
- Recent Entries list: Last 5 entries with model, part type, relative date
- Bottom navigation: Home (active), Earnings, Settings — 3 tabs

**States:**
- First time (no entries): Empty state with "No entries yet. Tap 'New Entry' to start."
- Has entries: Shows recent list
- Offline: Sync banner shows "No internet connection" in amber (#92400E)
- Pending sync: Blue banner with count + "Sync Now" button
- All synced: No banner (clean)

### 5.6 MOBILE — Part Type Selection

**Purpose:** Choose between logging a condenser or compressor entry.

**Layout:**
```
+----------------------------------+
|   < New Entry                    |
+----------------------------------+
|                                  |
|   Select Part Type               |
|                                  |
|   +----------------------------+ |
|   |                            | |
|   |    [Condenser Icon]        | |
|   |    Condenser                | |
|   |    Maruti Suzuki only       | |
|   |                            | |
|   +----------------------------+ |
|                                  |
|   +----------------------------+ |
|   |                            | |
|   |    [Compressor Icon]       | |
|   |    Compressor               | |
|   |    All vehicle makes        | |
|   |                            | |
|   +----------------------------+ |
|                                  |
+----------------------------------+
```

**Elements:**
- Header: Back button, "New Entry" title
- Two large cards (touch target minimum 64px high):
  - Condenser: Icon + label + "Maruti Suzuki only" subtitle
  - Compressor: Icon + label + "All vehicle makes" subtitle
- Tap card → navigate to Vehicle screen with part_type pre-selected

**States:**
- Default: Both cards visible
- Loading when navigating to next screen

### 5.7 MOBILE — Vehicle Screen (Condenser)

**Purpose:** Enter vehicle details for a condenser failure.

**Layout:**
```
+----------------------------------+
|   < Condenser Entry       Step 1/4|
+----------------------------------+
|                                  |
|   Select Model *                 |
|   +--------+  +--------+         |
|   | Swift  |  | Baleno |         |
|   +--------+  +--------+         |
|   +--------+  +--------+         |
|   | WagonR |  | Dzire  |         |
|   +--------+  +--------+         |
|   +--------+  +--------+         |
|   | Brezza |  |Alto K10|         |
|   +--------+  +--------+         |
|   +--------+  +--------+         |
|   | Ertiga |  |  XL6   |         |
|   +--------+  +--------+         |
|   +--------+  +--------+         |
|   | Ignis  |  |S-Presso|         |
|   +--------+  +--------+         |
|   +--------+  +--------+         |
|   |Celerio |  |Grand   |         |
|   +--------+  |Vitara  |         |
|   +--------+  +--------+         |
|   | Jimny  |                      |
|   +--------+                      |
|                                  |
|   Variant (optional)             |
|   [VXi___________]               |
|                                  |
|   Fuel Type *                    |
|   [Petrol] [Diesel] [CNG]        |
|   [Electric] [Hybrid]            |
|                                  |
|   Year *                         |
|   [ 2020  [<] [>] ]              |
|                                  |
|   Registration No. (optional)    |
|   [______________]               |
|                                  |
|   [Next]                         |
|                                  |
+----------------------------------+
```

**Elements:**
- Step indicator: "Step 1/4"
- Model grid: 13 Maruti Suzuki models as chips in a scrollable grid (3 columns)
- Selected model: Filled navy (#1F4E79) background, white text
- Unselected model: Outlined border, dark text
- Variant: Free text input with autocomplete suggestions (cached from server)
- Fuel type: Chip group, single-select
- Year: Plus/minus stepper (tap to increment/decrement) or scroll wheel
- Registration: Optional, auto-uppercase, Indian format
- "Next" button: Enabled only when model, fuel type, and year selected

**For compressor (same screen variant):**
- OEM free text input replaces model grid
- Model free text input with autocomplete
- No change to rest of fields

**States:**
- Default: No model selected, "Next" disabled
- Model selected: Chip fills navy, others remain outline
- Validation error: Red message below specific field
- Autocomplete dropdown: Appears when user types ≥2 chars in variant field

### 5.8 MOBILE — Failure Screen

**Purpose:** Enter failure details.

**Layout:**
```
+----------------------------------+
|   < Condenser Entry       Step 2/4|
+----------------------------------+
|                                  |
|   Failure Cause *                |
|   ○ Stone impact                 |
|   ○ Corrosion                    |
|   ○ Accident damage              |
|   ○ Pressure failure             |
|   ○ Blockage                     |
|   ○ Manufacturing defect         |
|   ○ Unknown                      |
|                                  |
|   Severity *                     |
|   [Minor] [Major] [Complete]     |
|                                  |
|   Odometer Reading *             |
|   [____] km                      |
|                                  |
|   [Back]    [Next]               |
|                                  |
+----------------------------------+
```

**Elements:**
- Step indicator: "Step 2/4"
- Failure cause: Radio button list (condenser causes)
- Severity: 3 chips, single-select
- Odometer: Numeric input with "km" suffix
- "Back" button (left) and "Next" button (right) — bottom of screen

**States:**
- Default: No selection, "Next" disabled
- All selected: "Next" enabled
- Odometer validation: "Enter a value between 100 and 99,99,999 km"

### 5.9 MOBILE — Evidence Screen

**Purpose:** Capture photos and record voice note.

**Layout:**
```
+----------------------------------+
|   < Condenser Entry       Step 3/4|
+----------------------------------+
|                                  |
|   Photos (up to 3)               |
|                                  |
|   +----------+  +----------+     |
|   | Damage   |  |  Label   |     |
|   | [  +  ]  |  | [  +  ]  |     |
|   +----------+  +----------+     |
|   +----------+                    |
|   | Installed|                    |
|   | [  +  ]  |                    |
|   +----------+                    |
|                                  |
|   Voice Note (optional)          |
|   [  Record  ]  0:00 / 0:60     |
|                                  |
|   [Back]    [Next]               |
|                                  |
+----------------------------------+
```

**Elements:**
- Step indicator: "Step 3/4"
- 3 photo slots: Damage, Label, Installed
- Each slot: Empty (camera icon + "+") or filled (thumbnail + "×" to remove)
- Tap empty slot → open camera
- Tap filled slot → open full-screen preview
- Voice note: Record button (circle, red when recording), timer, playback
- "Next" button: Always enabled (photos are optional)

**Camera behavior:**
- Opens in-app camera (expo-camera), not system camera
- After capture: Show preview with "Retake" and "Use" buttons
- Photo auto-compressed to ~720p JPEG before saving

**States:**
- Empty: 3 slots with "+" icon
- 1–2 filled: Some slots with thumbnails, others with "+"
- All 3 filled: All slots show thumbnails, "+" hidden
- Recording: Red pulsing dot on record button, timer counting up
- Playback: Speaker icon, progress bar on voice note

### 5.10 MOBILE — Notes Screen

**Purpose:** Add text note and confirm GPS.

**Layout:**
```
+----------------------------------+
|   < Condenser Entry       Step 4/4|
+----------------------------------+
|                                  |
|   Notes (optional)               |
|   [____________________________] |
|   [____________________________] |
|   [____________________________] |
|   [____________________________] |
|                0/200 characters   |
|                                  |
|   Location                       |
|   GPS: [Active] 28.61°N 77.23°E |
|                                  |
|   [Back]    [Review]             |
|                                  |
+----------------------------------+
```

**Elements:**
- Step indicator: "Step 4/4"
- Text note: Multi-line input, 200 char max with counter
- GPS status indicator: Green dot + coordinates (if captured) or red dot + "GPS not available" (if denied/timed out)
- "Review" button: Always enabled (note is optional)

**States:**
- GPS captured: Green dot, shows coordinates
- GPS denied: Red dot, "GPS not available. Location will not be recorded."
- GPS pending: Yellow pulsing dot, "Acquiring GPS..."
- Character count: Shows "X/200 characters" — turns red when >200

### 5.11 MOBILE — Review & Submit Screen

**Purpose:** Final review before submission.

**Layout:**
```
+----------------------------------+
|   < Review Entry                  |
+----------------------------------+
|                                  |
|   Review Entry                    |
|                                  |
|   Vehicle                         |
|   Model: Swift                    |
|   Variant: VXi                   |
|   Fuel: Petrol                   |
|   Year: 2020                     |
|   Reg: DL5CAB1234                |
|   [Edit >]                       |
|                                  |
|   Failure                         |
|   Cause: Stone impact             |
|   Severity: Major                 |
|   Odometer: 45,000 km            |
|   [Edit >]                       |
|                                  |
|   Evidence                        |
|   Photos: 2 of 3                 |
|   Voice note: 12 sec             |
|   [Edit >]                       |
|                                  |
|   Notes                           |
|   "Customer reported AC stopped   |
|    working after hitting a pothole|
|    on highway."                   |
|   [Edit >]                       |
|                                  |
|   Location: Meerut, UP            |
|                                  |
|   [Submit Entry]                  |
|                                  |
+----------------------------------+
```

**Elements:**
- 4 sections (Vehicle, Failure, Evidence, Notes), each with "Edit >" link
- Location summary at bottom (city, state derived from GPS)
- "Submit Entry" button: Large, navy blue (#1F4E79)
- On submit: Button shows spinner → entry saved locally → success animation

**States:**
- Default: All sections displayed
- Submitting: Button shows loading spinner, all edit links disabled
- Success: "Entry Submitted ✓" with entry ID, "Log Another" and "Back to Home" buttons
- Network error: Entry saved locally if offline with note; retry option

### 5.12 MOBILE — Earnings Screen

**Purpose:** View current month earnings and payout history.

**Layout:**
```
+----------------------------------+
|   [Home]  Earnings  [Settings]   |
+----------------------------------+
|                                  |
|   +----------------------------+ |
|   | This Month                 | |
|   | Total entries: 12           | |
|   | Approved: 10                | |
|   | Estimated payout: ₹200     | |
|   +----------------------------+ |
|                                  |
|   UPI ID: rajesh@paytm           |
|   [Change]                       |
|                                  |
|   Payout History                 |
|   +----------------------------+ |
|   | Jun 2026 | ₹180 | Paid ✓  | |
|   | May 2026 | ₹150 | Paid ✓  | |
|   | Apr 2026 | ₹120 | Paid ✓  | |
|   +----------------------------+ |
|                                  |
+----------------------------------+
```

**Elements:**
- This Month card: Entries, approved count, estimated payout
- UPI ID section: Current UPI ID with "Change" link
- Payout History: Monthly list with month, amount, status badge (Paid = green, Pending = grey)

**States:**
- No entries this month: "No entries logged this month"
- No UPI ID: "Set UPI ID to receive payments" with prompt
- Payout pending: Grey badge with "Pending"
- Payout paid: Green badge with "Paid ✓"

### 5.13 MOBILE — Settings Screen

**Purpose:** Profile management, language, app info.

**Layout:**
```
+----------------------------------+
|   [Home]  Earnings  [Settings]   |
+----------------------------------+
|                                  |
|   Profile                        |
|   Name: Rajesh Kumar             |
|   Workshop: Meerut Auto AC       |
|   City: Meerut                   |
|   State: Uttar Pradesh           |
|   [Edit Profile >]               |
|                                  |
|   Payment                        |
|   UPI ID: rajesh@paytm           |
|   [Change UPI ID >]              |
|                                  |
|   App                            |
|   Language: Hindi                |
|   [Change Language >]            |
|                                  |
|   Export My Entries              |
|   >                              |
|                                  |
|   App Version: 1.0.0             |
|                                  |
|   [Logout]                       |
|                                  |
+----------------------------------+
```

**Elements:**
- Profile section: Shows current values, "Edit Profile >" link
- Payment section: UPI ID display + "Change UPI ID >" link
- App section: Language dropdown, "Export My Entries" link
- App version at bottom
- "Logout" button: Red text, confirmation dialog before proceeding

**States:**
- Default: All settings displayed
- Logout confirmation dialog: "Are you sure you want to logout?"

### 5.14 WEB — Login Page

**Purpose:** Admin login.

**Layout:**
```
+----------------------------------+
|                                  |
|       +------------------+       |
|       | PartLog Admin    |       |
|       |                  |       |
|       | Email            |       |
|       | [______________] |       |
|       |                  |       |
|       | Password         |       |
|       | [______________] |       |
|       |                  |       |
|       | [Login]          |       |
|       |                  |       |
|       +------------------+       |
|                                  |
+----------------------------------+
```

**Elements:**
- Centered card on page (max 400px wide)
- "PartLog Admin" heading
- Email input: type="email", full width
- Password input: type="password", full width
- "Login" button: Full width, navy (#1F4E79)
- No "Forgot password" in v1 (admin accounts created by developer)

**States:**
- Default: Empty inputs
- Filling: Show/hide password toggle
- Error: "Invalid email or password" (red text above inputs)
- Loading: Button shows spinner
- Rate limited: "Too many login attempts. Try again in 15 minutes."

### 5.15 WEB — Dashboard

**Purpose:** High-level overview of platform metrics.

**Layout:**
```
+------------------------------------------+
| [Logo] PartLog              Admin Name  v|
+------------------------------------------+
| Sidebar |  Main Content                   |
|          |                                |
|  [navy]  |  +--------+ +--------+ +----+ |
| Dashboard|  |Total   | |Entries | |This | |
| Entries  |  |Mechs   | |Total   | |Month| |
| Mechanics|  | 342     | | 8,450   | |1,230| |
| Payouts  |  +--------+ +--------+ +----+ |
| Settings |  +--------+ +--------+        |
| Logout   |  |Pending | |Payout  |        |
|          |  |Review  | |Due     |        |
|          |  | 12      | |₹18,400  |        |
|          |  +--------+ +--------+        |
|          |                                |
|          |  +---- Entries by Model ------+|
|          |  | Swift    ████████████ 340  ||
|          |  | Baleno   ██████████  280  ||
|          |  | WagonR   █████████   210  ||
|          |  | Dzire    ████████    180  ||
|          |  | Brezza   █████       120  ||
|          |  +----------------------------+|
|          |                                |
|          |  +---- Daily Volume 30d ------+|
|          |  | ░░░░░░░░░░░░░░░░░░░░░░░░░  ||
|          |  | Line chart                  ||
|          |  +----------------------------+|
|          |                                |
|          |  +-- State +-- Cause ---------+|
|          |  | UP: 120 | Stone: 340      ||
|          |  | Mah: 80 | Corrosion: 210  ||
|          |  | Guj: 60 | Blockage: 98    ||
|          |  +---------+------------------+|
|          |                                |
+------------------------------------------+
```

**Elements:**
- Sidebar: Fixed left, 220px wide, navy background (#1F4E79), white text
- Active nav item: White background, navy text
- 5 KPI cards: 2 rows (3 + 2), each with label, value, trend arrow
- Charts in 2×2 grid:
  - Entries by Model (horizontal bar)
  - Daily Volume (line chart, 30 days)
  - Entries by State (vertical bar)
  - Entries by Cause (horizontal bar)
- All charts are clickable — clicking a bar filters to that entity

**States:**
- Loading: Skeleton placeholders for KPI cards and charts (no animation — just grey rectangles)
- Empty (first day): "No data yet. Data will appear once mechanics start logging entries."
- Data populated: Charts and KPIs show actual values
- Error: "Could not load dashboard data. [Retry]"

### 5.16 WEB — Entries Table

**Purpose:** View, filter, sort, and manage all failure entries.

**Layout:**
```
+------------------------------------------+
| Entries                                   |
|                                           |
| [Filters Bar]                             |
| Part Type: [All v]  Model: [All v]        |
| State: [All v]      Status: [All v]       |
| Date: [01/06/26] to [30/06/26]            |
| Cause: [All v]      Severity: [All v]     |
| [Apply Filters] [Clear All]               |
|                                           |
| [Export CSV] [Export Excel] [Bulk Reject] |
|                                           |
| +--+------+-------+-----+-------+---+--+ |
| |☐ | Date |Mech.  |Part |Model  |Sev|St| |
| +--+------+-------+-----+-------+---+--+ |
| |☐ |26/06 |Rajesh |Cond |Swift  |Maj|A | |
| |☐ |26/06 |Suresh |Comp |Hyundai|Min|A | |
| |☐ |25/06 |Amit   |Cond |Baleno |Maj|R | |
| |☐ |25/06 |Vijay  |Cond |WagonR |CF |A | |
| +--+------+-------+-----+-------+---+--+ |
|                                           |
| Showing 1-25 of 8,450   [<] [1] [2] ...>]|
+------------------------------------------+
```

**Elements:**
- Filter bar: 6 dropdown filters + date range picker + "Apply Filters" and "Clear All" buttons
- Action buttons: Export CSV, Export Excel, Bulk Reject (disabled until rows selected)
- Table: 25 rows per page, sortable by clicking column headers
- Columns: Checkbox (for bulk select), Date, Mechanic, Part Type, Model, Severity, Status
- Status badges: Approved (green), Rejected (red), Pending (grey), Duplicate (amber)
- Severity: Minor (green dot), Major (amber dot), Complete Failure (red dot)
- Pagination: Page numbers with prev/next
- Click row → opens Entry Detail side panel (not a separate page)

**States:**
- No data: "No entries match your filters. [Clear Filters]" button
- Loading: "Loading..." text with spinner
- Filters active: Filter badges shown above table showing active filters
- Bulk select: Checkbox in header selects all on current page; select-all across pages shown
- Export in progress: "Generating export..." with download link when ready

### 5.17 WEB — Entry Detail Side Panel

**Purpose:** Full detail view of a single entry.

**Layout:**
```
+------------------------------------------+
| Entries                    [X] Close      |
|         +-- Entry Detail ---------------+ |
|         | Entry ID: abc-123-def-456     | |
|         |                                | |
|         | Mechanic: Rajesh Kumar         | |
|         | Mobile: 9876543210             | |
|         | Workshop: Meerut Auto AC       | |
|         | City: Meerut, UP               | |
|         |                                | |
|         | Part: Condenser                | |
|         | Model: Maruti Suzuki Swift     | |
|         | Variant: VXi                   | |
|         | Fuel: Petrol  | Year: 2020    | |
|         | Reg: DL5CAB1234               | |
|         |                                | |
|         | Cause: Stone impact            | |
|         | Severity: Major                | |
|         | Odometer: 45,000 km           | |
|         |                                | |
|         | GPS: 28.61°N, 77.23°E         | |
|         | [View on Google Maps >]        | |
|         |                                | |
|         | Photos:                        | |
|         | [damage] [label] [installed]   | |
|         | (click to open lightbox)       | |
|         |                                | |
|         | Voice Note: [▶ Play] 12 sec   | |
|         |                                | |
|         | Note: "Customer reported AC... | |
|         |                                | |
|         | Status: [Approved] | [Reject]  | |
|         | Created: 26 Jun 2026, 14:30    | |
|         | Synced: 26 Jun 2026, 14:31     | |
|         +--------------------------------+|
+------------------------------------------+
```

**Elements:**
- Panel slides in from right (no animation in v1 — instant appear)
- Close button (X) top right
- All entry fields displayed in structured layout
- Photo thumbnails (small, clickable) — click opens lightbox overlay
- Audio player: Play/pause button with progress bar
- GPS: "View on Google Maps" link
- Status badge (large, coloured)
- "Reject" button: Opens rejection modal (if currently approved)
- Activity log at bottom: Timestamped list of status changes

**Photo Lightbox:**
- Full-screen overlay with black background
- Image centered, zoomable (pinch or scroll)
- Left/right arrows to navigate between photos
- "Close" button (X) top right

**Rejection Modal:**
```
+-----------------------------------+
| Reject Entry                      |
|                                   |
| Reason:                           |
| [fraudulent         v]            |
|                                   |
| Note (optional):                  |
| [_____________________________]   |
|                                   |
| [Cancel]    [Confirm Rejection]   |
+-----------------------------------+
```

### 5.18 WEB — Mechanics List

**Purpose:** View and manage all registered mechanics.

**Layout:**
```
+------------------------------------------+
| Mechanics                                 |
|                                           |
| Search: [Search by name or mobile___]     |
| Status: [All v]  State: [All v]           |
|                                           |
| +--+----------+----------+----------+--+ |
| |  | Name     | Mobile   | City     |St| |
| +--+----------+----------+----------+--+ |
| |  | Rajesh   | 98765... | Meerut   |AC| |
| |  | Suresh   | 98765... | Lucknow  |AC| |
| |  | Amit     | 98765... | Delhi    |IN| |
| +--+----------+----------+----------+--+ |
|                                           |
| Showing 1-25 of 342    [<] [1] [2] ... >]|
+------------------------------------------+
```

**Elements:**
- Search bar: Free text search on name or mobile
- Filters: Status dropdown (All/Active/Inactive), State dropdown
- Table: Name, Mobile (masked middle digits), City, State, Status badge (Active=green, Inactive=red), Entries Count, Registered Date
- Click row → navigate to Mechanic Detail page

### 5.19 WEB — Mechanic Detail

**Purpose:** Detailed view of a single mechanic.

**Layout:**
```
+------------------------------------------+
| Mechanics > Rajesh Kumar                  |
|                                           |
| Profile:                                  |
| Name: Rajesh Kumar                        |
| Mobile: 9876543210                        |
| Workshop: Meerut Auto AC                  |
| City: Meerut, Uttar Pradesh               |
| Language: Hindi                           |
| UPI ID: rajesh@paytm                      |
| Status: [Active] | [Deactivate]           |
| Registered: 15 Jan 2026                   |
|                                           |
| [Tabs: Entries | Payouts | Activity]      |
|                                           |
| Entries (active tab)                      |
| (Same entries table as Entries page,      |
|  pre-filtered to this mechanic)           |
+------------------------------------------+
```

**Elements:**
- Header: Breadcrumb "Mechanics > {name}"
- Profile card: All mechanic fields
- Status: Green "Active" badge or red "Inactive" badge
- "Deactivate" / "Reactivate" button: With confirmation dialog
- 3 tabs: Entries (table), Payouts (monthly list), Activity (audit log)

### 5.20 WEB — Payout Management

**Purpose:** Monthly payout summary and payment tracking.

**Layout:**
```
+------------------------------------------+
| Payouts                                   |
|                                           |
| Month: [June 2026          v]             |
|                                           |
| Payout Rates:                             |
| Condenser: ₹20/entry  | [Change]         |
| Compressor: ₹25/entry | [Change]         |
|                                           |
| +--+----------+------+------+------+---+ |
| |  | Mechanic | UPIs | Cond | Comp | Amt| |
| +--+----------+------+------+------+---+ |
| |  | Rajesh   | raj@ |  12  |  3   | 315| |
| |  | Suresh   | sur@ |   8  |  2   | 210| |
| |  | Amit     | ami@ |  15  |  5   | 425| |
| +--+----------+------+------+------+---+ |
|    | Totals   |      | 35   | 10   | 950| |
|                                           |
| [Export CSV] [Export Excel]              |
|                                           |
| (Click "Mark Paid" on a row to record     |
|  payment)                                 |
+------------------------------------------+
```

**Elements:**
- Month selector: Dropdown, defaults to previous month
- Payout rates section: Current rates with "Change" link (opens rate change modal)
- Table: Mechanic, UPI ID (masked), condenser count, compressor count, total amount (₹), status (Pending/Paid)
- Totals row at bottom (bold)
- "Mark Paid" button on each row or in row action menu
- Export buttons

**Payout Rate Change Modal:**
```
+-----------------------------------+
| Change Payout Rate                |
|                                   |
| Part Type: Condenser              |
| New Rate (₹ per entry):           |
| [ 20  ]                          |
|                                   |
| Effective from:                   |
| [01/07/2026]                      |
|                                   |
| [Cancel]    [Save]               |
+-----------------------------------+
```

**Mark Paid Modal:**
```
+-----------------------------------+
| Mark Payout as Paid               |
|                                   |
| Mechanic: Rajesh Kumar            |
| Amount: ₹315                      |
| UPI ID: rajesh@paytm              |
|                                   |
| UPI Transaction Ref:              |
| [___________________________]     |
|                                   |
| [Cancel]    [Confirm Payment]     |
+-----------------------------------+
```

### 5.21 WEB — Export Page

**Purpose:** Export data as CSV or Excel.

**Layout:**
```
+------------------------------------------+
| Export                                    |
|                                           |
| ---- Entries Export ----                  |
|                                           |
| Date Range:                               |
| [01/06/2026] to [30/06/2026]             |
|                                           |
| Filters (optional):                       |
| Part Type: [All v]  Model: [All v]       |
| State: [All v]      Status: [All v]      |
|                                           |
| Format: [CSV]  [Excel]                    |
|                                           |
| [Export Entries]                          |
|                                           |
| ---- Payout Export ----                   |
|                                           |
| Month: [June 2026 v]                      |
| Format: [CSV]  [Excel]                    |
|                                           |
| [Export Payouts]                          |
|                                           |
+------------------------------------------+
```

**Elements:**
- Two sections: Entries Export and Payout Export
- Entries: Date range (required) + optional filters + format toggle + button
- Payout: Month selector + format toggle + button
- "Export" button: Triggers server-side generation, file downloads automatically

**States:**
- Default: No date range or month selected, export buttons disabled
- Ready: Date range selected, button enabled
- Generating: Button shows spinner, "Generating your export..."
- Complete: File download triggers automatically
- Large export warning: "Your export has 12,000 rows. Consider narrowing your date range." (shown if >10,000 rows)

---

## 6. Component Library

### 6.1 Primary Button (Mobile)

| Property | Value |
|----------|-------|
| Background | #1F4E79 (navy) |
| Text color | #FFFFFF |
| Font | System default, 16sp, Medium weight |
| Padding | 16px vertical, 24px horizontal |
| Border radius | 6px |
| Min touch target | 48×48px |
| Disabled | #B0B0B0 background, #FFFFFF text |
| Loading | Show spinner, hide text |

### 6.2 Primary Button (Web)

| Property | Value |
|----------|-------|
| Background | #1F4E79 (navy) |
| Text color | #FFFFFF |
| Font | Inter, 14px, 500 weight |
| Padding | 8px 16px |
| Border radius | 4px |
| Hover | #2E75B6 (steel blue) |
| Disabled | #DDE1E7 background, #9CA3AF text |

### 6.3 Secondary Button (Web)

| Property | Value |
|----------|-------|
| Background | #FFFFFF |
| Border | 1px solid #DDE1E7 |
| Text color | #1A1A1A |
| Font | Inter, 14px, 500 weight |
| Padding | 8px 16px |
| Border radius | 4px |
| Hover | #F4F6F8 background |

### 6.4 Input Field

| State | Border | Background | Text |
|-------|--------|-----------|------|
| Default | 1px solid #DDE1E7 | #FFFFFF | #1A1A1A |
| Focused | 2px solid #2E75B6 | #FFFFFF | #1A1A1A |
| Error | 1px solid #991B1B | #FFF5F5 | #1A1A1A |
| Disabled | 1px solid #DDE1E7 | #F4F6F8 | #6B6B6B |

### 6.5 Model Chip (Mobile)

| State | Background | Border | Text |
|-------|------------|--------|------|
| Default (unselected) | #FFFFFF | 1.5px solid #DDE1E7 | #1A1A1A |
| Selected | #1F4E79 | 1.5px solid #1F4E79 | #FFFFFF |
| Disabled | #F4F6F8 | 1px solid #DDE1E7 | #B0B0B0 |

### 6.6 Failure Cause Row (Mobile)

| State | Background | Left indicator |
|-------|------------|----------------|
| Default (unselected) | #FFFFFF | Empty circle ○ |
| Selected | #D6E4F0 (light blue) | Filled circle ● (navy #1F4E79) |
| Error | #FFFFFF | Red border |

### 6.7 Status Badge

| Variant | Background | Text | Border |
|---------|-----------|------|--------|
| Approved | #E6F7ED | #1E6B3C | None |
| Rejected | #FFEAEA | #991B1B | None |
| Pending | #F4F6F8 | #6B6B6B | None |
| Duplicate | #FFF8E1 | #92400E | None |

### 6.8 Sync Status Banner (Mobile)

| State | Background | Text | Action |
|-------|-----------|------|--------|
| Online, all synced | None (hidden) | — | — |
| Pending sync | #D6E4F0 | "X entries pending sync" | "Sync Now" button |
| Offline | #FFF8E1 | "No internet connection" | None |
| Syncing | #E6F7ED | "Syncing..." | Progress indicator |
| Sync error | #FFEAEA | "Sync failed. Tap to retry." | "Retry" button |

### 6.9 KPI Card (Web)

| Property | Value |
|----------|-------|
| Background | #FFFFFF |
| Border | 1px solid #DDE1E7 |
| Border radius | 4px |
| Padding | 16px |
| Shadow | None |
| Label | #6B6B6B, Inter 12px, 500 weight |
| Value | #1A1A1A, Inter 24px, 600 weight |
| Trend | +/-- #1E6B3C / #991B1B, Inter 12px |

### 6.10 Data Table Row (Web)

| State | Background |
|-------|------------|
| Default (odd row) | #FFFFFF |
| Default (even row) | #F4F6F8 |
| Hover | #D6E4F0 |
| Selected | #E6F7ED |
| Clickable | Cursor: pointer |

---

## 7. Responsive Behaviour (Web Portal)

| Breakpoint | Layout Changes |
|------------|---------------|
| ≥1440px (desktop) | Sidebar visible, 4-column KPI grid, 2×2 chart grid |
| 1024–1439px (small desktop) | Sidebar collapsed (icons only, labels on hover), 3-column KPI grid, charts stack vertically |
| 768–1023px (tablet) | Sidebar hidden (hamburger menu), 2-column KPI grid, all charts full width, entry table horizontal scroll |
| <768px (mobile) | Not supported — web portal is desktop-only. Show message: "For best experience, use on a desktop computer." |

---

## 8. Internationalisation Considerations

- **Text expansion:** Hindi and other Indian languages can be 20–30% longer than English. All UI elements must accommodate text expansion without truncation.
- **No RTL:** None of the 10 supported languages are RTL. No RTL layout changes needed.
- **Date format:** DD/MM/YYYY (Indian standard) for all locales. No MM/DD/YYYY anywhere.
- **Number format:** Indian numbering system (1,23,456.00) for all locales. Space as thousands separator, not comma, for some languages — but for simplicity, use Indian comma format for all.
- **Language fallback:** If a translation key is missing, fall back to English. Never show a blank string.
- **Dynamic content (model names, failure causes):** These are stored in English in the database. They are NOT translated in v1. Only UI chrome is translated.

---

## 9. Accessibility Requirements

| Requirement | Specification |
|-------------|--------------|
| Minimum touch target (mobile) | 48×48px |
| Minimum font size (mobile) | 14sp |
| Minimum font size (web) | 12px |
| Contrast ratio (normal text) | ≥ 4.5:1 |
| Contrast ratio (large text) | ≥ 3:1 |
| Focus indicator (web) | 2px solid #2E75B6 outline on keyboard focus |
| Error communication | Text error message + red border; colour is not the only indicator |
| Form labels | Always visible (no placeholder-as-label pattern) |
| Image alt text | Photos have descriptive alt text: "Damage photo of [model] [part type]" |

---

## 10. Animation and Transition Rules

**Rule: Almost nothing should animate.**

- Screen transitions: Instant (no slide, no fade, no scale)
- Button state changes: Instant (no hover animation beyond colour change, no click ripple)
- Modals: Instant appear (no slide-up or fade-in)
- Loading states: Static spinner or skeleton — no skeleton shimmer animation
- Only acceptable animation: Progress bar during sync (determinate, not indeterminate)

**Rationale:** Mechanics need speed. Every animation adds perceived latency. Admin portal is a data tool — animations distract from data scanning.

---

## 11. Empty States

| Screen | Empty State |
|--------|-------------|
| Home (mobile) | "No entries yet. Tap 'New Entry' to log your first failure." + illustration placeholder (or just text) |
| Earnings (mobile) | "No entries this month. Start logging to see your earnings." |
| Dashboard (web) | "No data yet. Data will appear here once mechanics start logging entries." |
| Entries Table (web, no filters) | "No entries have been logged yet." |
| Entries Table (web, filtered) | "No entries match your filters." + "Clear Filters" button |
| Mechanics (web) | "No mechanics have registered yet." |
| Payouts (web) | "No payouts for this month yet." |
| Mechanic Detail (web, no entries) | "This mechanic has not logged any entries." |

---

## 12. Error States

| Error | Where | Display |
|-------|-------|---------|
| Network error (mobile, any API call) | Inline | Red banner at top: "No internet connection. Data saved locally." |
| API server error (mobile) | Inline | Red banner: "Server error. Please try again later." |
| Network error (web, any API call) | Inline | Red toast notification: "Could not load data. [Retry]" — persists until dismissed |
| OTP send failure | OTP screen | Red text below input: "Failed to send OTP. Check your number and try again." |
| OTP invalid | OTP screen | Red text: "Invalid code. X attempts remaining." |
| OTP expired | OTP screen | Red text: "Code expired. Request a new one." |
| Login invalid (web) | Login page | Red text above form: "Invalid email or password." |
| Login rate limited (web) | Login page | Red text: "Too many login attempts. Try again in 15 minutes." |
| Form validation error (mobile) | Inline per field | Red text below field: "This field is required." / "Enter a valid value." |
| Form validation error (web) | Inline per field | Same pattern as mobile |
| Sync failure (mobile) | Sync banner | Amber banner: "3 entries failed to sync. Tap to retry." |
| File upload failure (mobile) | Evidence screen | Red text below photo slot: "Upload failed. [Retry]" |
| Photo size too large | Evidence screen | Red text: "Photo too large. Max 5MB." |
| Record permission denied (audio) | Evidence screen | Red text: "Microphone access denied. Enable in Settings." |
| GPS permission denied | Notes screen | Red text: "GPS access denied. Location will not be recorded." |
| Camera permission denied | Evidence screen | Red text: "Camera access denied. Enable in Settings." |
| Push notification permission denied | Settings | Grey text: "Notifications disabled. Enable in Settings to receive payout alerts." |
