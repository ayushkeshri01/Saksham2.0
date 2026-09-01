# APK Distribution & Go-to-Market Plan — PartLog

**Version:** 1.0  
**Date:** July 2026  
**Focus:** Getting the Android app onto mechanics' phones and driving adoption

---

## 1. The Core Challenge

PartLog's mobile app is useless unless it is on mechanics' phones. Unlike a consumer app (Paytm, WhatsApp) that people discover organically, PartLog targets a specific professional audience — independent AC mechanics across India — who do not search for "parts failure logging app" on the Play Store.

The distribution and adoption challenge breaks into three layers:

| Layer | Question | This document covers |
|-------|----------|---------------------|
| **Distribution** | How does the APK physically reach the mechanic's phone? | Sections 2–4 |
| **Marketing & Awareness** | How does the mechanic learn about PartLog and decide to install? | Sections 5–7 |
| **Activation & Retention** | How does the mechanic go from installed to regular logger? | Sections 8–9 |

---

## 2. Distribution Channel Options

### 2.1 Channel Comparison Matrix

| # | Channel | Reach Speed | Trust Level | Tech Barrier | Cost per Install | Scalability | Offline Viable |
|---|---------|------------|-------------|-------------|-----------------|-------------|----------------|
| D1 | Google Play Store (Internal Testing) | Slow | High | Medium (needs Google account + invite acceptance) | ₹0 | High | No |
| D2 | Google Play Store (Production) | Slow | High | Low (standard install) | ₹0 | Very High | No |
| D3 | Direct APK download from portal/landing page | Medium | Medium | Low | ₹0 | Medium | Yes |
| D4 | WhatsApp broadcast with APK file | Fast | High (from known contact) | Very low | ₹0 | Medium | Yes |
| D5 | Distributor network sideloading | Fast | High (in-person trust) | None (agent does it) | Labour cost only | Low–Med | Yes |
| D6 | QR code on workshop posters/standees | Medium | Medium | Low | Printing cost (~₹5/poster) | Medium | Yes |
| D7 | Field agent one-on-one installation | Fast | Very high | None | Labour + travel | Low | Yes |
| D8 | ShareIt / Xender / nearby share | Fast | Medium | Low | ₹0 | Low | Yes |
| D9 | Third-party app stores (APKPure, etc.) | Medium | Low | Low | ₹0 | Medium | Yes |

### 2.2 Channel Deep Dives

#### D1 — Google Play Store Internal Testing

**How it works:**
1. Developer uploads APK/AAB to Play Console internal testing track
2. Adds mechanic's Google account email to tester list
3. Mechanic receives invite email → clicks link → sees "Install" button on Play Store
4. Mechanic installs like any Play Store app

**Pros:**
- Automatic updates via Play Store
- No "unknown sources" sideloading warnings
- Play Store data safety section builds trust
- Crash reporting via Play Console

**Cons:**
- Mechanic needs a Google account (most have one for YouTube/Android)
- Mechanic must accept email invite — confusing for non-tech-savvy users
- Invite process does not scale well beyond 100 testers (1000 tester limit on internal track)
- Emails may go to spam

**Recommended for:** Pilot phase (first 100 mechanics). Not scalable for 2000+.

**Implementation:**
- Collect mechanic email during onboarding or via WhatsApp
- Add in batches of 100 using Play Console UI or Google Play Developer API
- Monitor invite acceptance rate — if <60%, switch strategy

#### D2 — Google Play Store Production

**How it works:**
App is published publicly on Play Store. Mechanics search "PartLog" and install.

**Pros:**
- Zero distribution effort — app is discoverable
- Automatic updates
- Maximum trust (Play Store protected)
- Unlimited installs

**Cons:**
- Mechanics do not search for "PartLog" — organic discovery is near zero
- Must pass Play Store production review (stricter than internal testing)
- Public listing means anyone can install (including competitors)
- Requires privacy policy, proper app category, etc.

**Recommended for:** Phase 2 (after pilot proves the model). Only after mechanic awareness is already built through other channels.

**Play Store listing assets needed:**
- App name: PartLog (or PartLog — Vikas Group Field Data)
- Description in English + Hindi
- Screenshots (phone mockups showing key screens)
- Feature graphic
- Privacy policy URL
- Content rating questionnaire

#### D3 — Direct APK Download

**How it works:**
APK file hosted on a simple landing page (or behind login on the web portal). Mechanic opens link in phone browser → downloads APK → opens it → approves "install from unknown sources" → app installed.

**Pros:**
- No Play Store dependency
- Works for any Android phone regardless of Google account status
- Easy to share link via WhatsApp
- One landing page for all mechanics

**Cons:**
- "Install from unknown sources" warning scares non-tech users
- Browser download may fail on some phones
- No automatic updates (mechanic must download new APK manually)
- APK can be shared beyond intended audience

**Recommended for:** Primary distribution channel for scale (200–2000+ mechanics). Combine with QR codes on physical materials.

**Implementation details:**
- Host APK on Azure Blob Storage with CDN (or on Vercel/App Service static route)
- Create `partlog.app/download` or similar short URL
- Generate QR code for the URL
- Use Expo EAS Update for OTA JS updates (after initial APK install)
- Periodically sign new APK builds and replace the file

**Landing page content:**
```
PartLog — Install

Step 1: Tap the download button below
Step 2: Open the downloaded file
Step 3: Tap "Install anyway" if asked
Step 4: Open the app

[Download APK v1.0.0]

Having trouble? Watch this video [link]
Or call us: [support number]
```

#### D4 — WhatsApp Distribution

**How it works:**
APK file (or download link) sent directly to mechanics via WhatsApp. This is the most natural channel — every Indian mechanic uses WhatsApp daily.

**Pros:**
- Highest open rate (95%+ within 1 hour for WhatsApp in India)
- Trusted because it comes from a known contact (distributor, Vikas Group agent)
- Mechanic can ask questions immediately
- Easy to forward to other mechanics

**Cons:**
- APK file itself can be sent but may be blocked by WhatsApp (exe/apk files sometimes blocked)
- Better to send a link to download page, not the APK file itself
- Requires maintaining WhatsApp contact list

**Implementation:**
- Create a WhatsApp group: "PartLog — Meerut Mechanics" (city-wise)
- Send broadcast messages with download link (not APK file directly)
- Pin the download link in group description
- Use WhatsApp Business API for broadcast at scale (when >500 mechanics)

**Message template:**
```
Namaste! 👋

PartLog app ab install kar sakte hain. Isse aap har AC condenser ya compressor replacement ke entry karke ₹20 per entry kama sakte hain.

Download link: https://partlog.app/download
Video guide: https://partlog.app/guide

Koi problem ho to mujhe call karein: [number]
```

#### D5 — Distributor Network Sideloading

**How it works:**
Vikas Group's existing distributor network (parts wholesalers who supply to workshops) installs the app on mechanics' phones during their regular visits.

**Pros:**
- Highest trust — mechanic knows the distributor personally
- Hands-on installation — no tech barriers
- Distributor can demonstrate first entry
- Distributor already visits workshops weekly
- Zero incremental travel cost

**Cons:**
- Distributors need training and incentive to do this
- Not all distributors will cooperate
- Quality of installation may vary
- Requires managing distributor relationships

**Implementation:**
- Equip each distributor with:
  - APK file on their own phone (for sharing via ShareIt/nearby share)
  - Printed QR code card
  - A 2-minute script in Hindi/regional language
- Incentive for distributor: ₹10 per successful installation + first entry
- Track via distributor code entered during mechanic onboarding

#### D6 — QR Code on Physical Materials

**How it works:**
Printed QR codes placed in workshops, on parts packaging, or at distributor counters. Mechanics scan with any QR scanner (built into PhonePe/Google Pay/phone camera) → opens download page.

**Pros:**
- Very low cost per impression (once printed)
- Passive — works without active effort
- Can be placed at high-traffic locations

**Cons:**
- QR code alone rarely drives action (mechanic must already be motivated)
- Requires the mechanic to take initiative
- No explanation of value at point of scan

**Placement ideas:**
- Sticker on every AC condenser/compressor box leaving the warehouse
- Poster at distributor counter
- Laminated card handed with every parts invoice
- Sticker on workshop notice board

#### D7 — Field Agent One-on-One Installation

**How it works:**
A dedicated PartLog field agent visits workshops, installs the app, and helps the mechanic log their first entry.

**Pros:**
- Highest conversion rate (90%+ of visits result in installation)
- First entry done together — mechanic sees how easy it is
- Immediate feedback collection
- Builds personal relationship

**Cons:**
- Labour intensive — 1 agent can cover ~10 workshops per day
- Costly at scale (travel + salary)
- Only feasible for initial pilot and Tier 1 cities

**Coverage estimate:** 1 agent = 10 workshops/day = 200 workshops/month. For 2000 mechanics, need 10 agents for 1 month.

---

## 3. Recommended Distribution Strategy (Phased)

### Phase 1: Pilot (Weeks 1–4, Target: 50 mechanics)

**Primary channel:** D1 — Google Play Internal Testing  
**Secondary channel:** D7 — Field agent visits  
**Tertiary channel:** D4 — WhatsApp direct link

**Why:** Small number means Play Store invites are manageable. Field agents build trust and gather feedback. WhatsApp as backup for mechanics who cannot figure out the invite.

**Process:**
```
1. Identify 10 target cities (start with Delhi-NCR, Meerut, Lucknow, Jaipur, Indore, etc.)
2. Vikas Group sales team identifies 5 workshops per city
3. Field agent visits each workshop:
   a. Explains the programme (2 min pitch)
   b. Installs app via Play Store invite or direct APK
   c. Helps mechanic log first entry
   d. Adds mechanic to city WhatsApp group
4. First 50 mechanics onboarded within 2 weeks
5. Collect feedback, fix issues, iterate
```

### Phase 2: City-Wise Scale (Months 2–3, Target: 200–500 mechanics)

**Primary channel:** D3 — Direct APK download (QR code + landing page)  
**Secondary channel:** D5 — Distributor network sideloading  
**Tertiary channel:** D4 — WhatsApp broadcast

**Why:** Play Store invites do not scale. Direct APK download with QR codes on physical materials reaches more mechanics. Distributor network amplifies reach.

**Process:**
```
1. Print 5,000 QR code stickers
2. Place stickers on all AC condenser boxes leaving warehouse (date: Month 2 onwards)
3. Place posters at 100 distributor counters across 10 cities
4. Train 20 distributors:
   - Each distributor gets ₹10/install bonus
   - Each gets QR code cards to hand to mechanics
5. City-wise WhatsApp groups (1 per city, group admin from Vikas Group)
6. Weekly broadcast: entry count leaderboard, payout reminders
```

### Phase 3: Mass Adoption (Months 4–8, Target: 500–2000+ mechanics)

**Primary channel:** D4 — WhatsApp + D3 — QR/Download (combined)  
**Secondary channel:** D2 — Play Store production (if approved)  
**Tertiary channel:** Referral bonus (word of mouth)

**Why:** At this stage, existing mechanics become the best marketing channel. Each mechanic knows 5–10 other mechanics in their city. Referral bonus creates viral loop.

**Process:**
```
1. Launch referral programme:
   - Existing mechanic refers another mechanic
   - Referrer gets ₹50 when referee logs first 10 entries
   - Referee gets ₹25 bonus on registration
2. Publish to Play Store production track (more discoverable for late joiners)
3. Scale WhatsApp groups — use WhatsApp Business API for broadcast
4. Monthly payout becomes the marketing message:
   - Share payout screenshots (with permission) in WhatsApp groups
   - "Rajesh ne iss mahine ₹3,200 kamaye. Aap kyun nahi?"
5. AC parts distributors become channel partners
```

---

## 4. Technical Distribution Infrastructure

### 4.1 APK Build Pipeline

```
Git push to main (or manual trigger)
    |
    v
GitHub Actions → `eas build --platform android --profile production`
    |
    v
Expo EAS Build generates .aab file
    |
    v
Convert .aab to .apk (using bundletool or EAS)
    |
    v
Upload .apk to Azure Blob Storage (public-read, versioned)
    |
    v
Update landing page download link
    |
    v
Notify mechanics via WhatsApp: "New version available"
```

**Update frequency:** Once every 2–4 weeks for the first 3 months, then monthly.

**OTA updates (Expo EAS Update):**
- JS-only changes can be pushed OTA without APK reinstall
- Mechanic just needs to restart the app (or gets prompted)
- Use for: bug fixes, UI changes, new text/translations
- NOT for: native module changes (camera, SQLite, etc.) — those need new APK

### 4.2 Version Tracking

```sql
-- Track which version each mechanic has installed
ALTER TABLE mechanics ADD COLUMN app_version VARCHAR(10);
ALTER TABLE mechanics ADD COLUMN last_version_check_at TIMESTAMPTZ;

-- Prompt update if version < latest
-- Show in-app: "New version available. Update now? [Later] [Update]"
-- Update link directs to same download URL (always serves latest APK)
```

### 4.3 APK File Hosting

| Resource | Location | Purpose |
|----------|----------|---------|
| APK file | Azure Blob → CDN | Primary download source |
| Landing page | Vercel (partlog.app/download) | Marketing + download instructions |
| QR code generator | Built into web portal admin section | Generate city-specific QR codes |
| Download tracking | Azure App Insights / custom analytics | Track download count, conversion |

---

## 5. Marketing Strategy

### 5.1 The Value Proposition

The core message must be simple and benefit-driven:

> **"Har AC part replacement ka photo lo, entry karo, aur ₹20 kamaye."**
> (Take a photo of every AC part replacement, log the entry, earn ₹20.)

**Three-part message structure:**

| Element | English | Hindi |
|---------|---------|-------|
| What to do | Log every AC condenser/compressor replacement | Har AC condenser ya compressor replacement ka entry karein |
| How much | ₹20 per approved entry | ₹20 har approved entry par |
| When paid | Monthly UPI payment, guaranteed | Har mahine UPI payment, guaranteed |

### 5.2 Marketing Channels

#### M1 — Distributor Network (Highest Conversion)

Distributors and wholesalers are the most credible messengers. They already visit workshops daily.

**Tactics:**
- Train each distributor on a 2-minute pitch
- Provide laminated QR code card + 10 sticker sheets per distributor
- Weekly WhatsApp reminder to distributor: "Aaj kitne mechanics ko app bataye?"
- Monthly distributor bonus: top 3 distributors by installs get ₹5,000 / ₹3,000 / ₹2,000

**Pitch script (Hindi, 2 minutes):**
```
"Bhai, ek naya programme aaya hai — PartLog.
Vikas Group ne launch kiya hai.

Jab bhi aap kisi gaadi ka AC condenser ya compressor badalte hain,
toh app mein uski entry karein — photo lo, model batao, kyun bigda.
Bas 1 minute ka kaam hai.

Har approved entry ke ₹20 aapko UPI se milenge.
Har mahine 100–150 entries aaram se ho sakti hain = ₹2,000–3,000 extra income.

Main abhi install kar deta hoon aapke phone mein."
```

#### M2 — WhatsApp Groups (Highest Engagement)

Every city in India has mechanic WhatsApp groups. Piggyback on existing groups or create new ones.

**Tactics:**
- Identify existing mechanic WhatsApp groups in target cities (distributors can add you)
- Share daily: tip of the day + entry count update
- Share weekly: top earners (with permission), payout reminders
- Share monthly: payout confirmation screenshots

**Content calendar:**

| Frequency | Content | Purpose |
|-----------|---------|---------|
| Daily (morning) | "Aaj ka tip: [quick logging tip]" | Habit building |
| Daily (evening) | "Aaj PartLog mein kitni entries hui: 45" | Social proof |
| Weekly (Monday) | "Iss hafte ke top 5 mechanics" | Recognition |
| Weekly (Friday) | "Mahina khatam hone wala hai. Entries check karein." | Urgency |
| Monthly (1st) | "Payout processing start. UPI ID update karein." | Action |
| Monthly (5th) | "Payout completed. Screenshot." | Trust building |

#### M3 — Referral Programme (Viral Loop)

The best mechanics know other mechanics. Referral creates organic growth.

**Structure:**
```
Referrer gets: ₹50 when referee completes first 10 entries
Referee gets: ₹25 bonus on registration

How it works:
1. Mechanic shares referral link/code from app Settings
2. Referee enters referral code during onboarding
3. Both get bonus after referee's 10th approved entry
4. Bonus added to next monthly payout
```

**Referral code format:** `PARTLOG-{MECHANIC_MOBILE_LAST_4_DIGITS}`  
**Example:** `PARTLOG-3210`

#### M4 — In-Workshop Materials (Passive Awareness)

Low-cost physical materials that stay in the workshop.

**Items:**

| Material | Unit Cost | Quantity | Total | Placement |
|----------|-----------|----------|-------|-----------|
| QR code sticker (3×3 inch) | ~₹3 | 5,000 | ₹15,000 | On parts box, tool cabinet |
| Laminated card (credit card size) | ~₹5 | 10,000 | ₹50,000 | Hand with every parts invoice |
| Wall poster (A3 size) | ~₹15 | 500 | ₹7,500 | Workshop notice board |

**Design guidelines:**
- Navy blue (#1F4E79) + white — matches brand
- Large QR code (minimum 3×3 cm)
- Hindi text dominant, English subtitle
- Phone number for support helpline
- No cluttered design — one message: "Har entry = ₹20"

#### M5 — YouTube / Videos (Low Effort, Long Tail)

Mechanics watch YouTube for repair guidance. A simple video can reach thousands.

**Tactics:**
- Create one 90-second video in Hindi:
  - Show app being installed (screen recording)
  - Show a real entry being logged (60 seconds)
  - Show earnings screen
- Upload to YouTube with title: "AC repair mechanics ke liye — PartLog se har entry par ₹20 kamaye"
- Share link in WhatsApp groups
- Distributors can show the video to mechanics

**Video production:** Screen recording + voiceover. No actor needed. Done in-house with phone.

#### M6 — Payout as Marketing (Strongest at Scale)

Every successful payout is a marketing event.

**Tactics:**
- When a mechanic receives their first payout: WhatsApp message with amount + congratulations
- Monthly top earner announcement in groups
- Testimonial collection: "Main PartLog se ₹3,200/month extra kama raha hoon"

---

## 6. City-Wise Launch Plan

### 6.1 City Prioritisation

| Tier | Cities | Target Mechanics | Timeline | Distribution Channel |
|------|--------|-----------------|----------|---------------------|
| Pilot (4 cities) | Delhi-NCR, Meerut, Lucknow, Jaipur | 50 | Month 1 | Field agent + Play Store |
| Tier 1 (6 cities) | Indore, Pune, Ahmedabad, Chandigarh, Bhopal, Nagpur | 200 | Month 2–3 | Distributor + QR + WhatsApp |
| Tier 2 (10 cities) | Patna, Ranchi, Bhubaneswar, Guwahati, Surat, Vadodara, Ludhiana, Agra, Varanasi, Kanpur | 300 | Month 3–4 | WhatsApp + Distributor + Referral |
| Tier 3 (All India) | Remaining cities via distributor network | 500–1500 | Month 4–8 | Referral + Play Store + WhatsApp |

### 6.2 City Launch Checklist

For each city launch:

```
[ ] Identify 3–5 target distributors/wholesalers in the city
[ ] Meet distributors, explain programme, train on installation
[ ] Print QR code materials for each distributor (100 stickers, 50 cards, 5 posters)
[ ] Create city WhatsApp group
[ ] Add first 10–20 mechanics via distributor visit
[ ] Day 7: Review — how many installed? How many entries?
[ ] Day 14: First mini-payout (or at least earnings screen share)
[ ] Day 30: Review city adoption, share in group
```

---

## 7. Incentives and Pricing Strategy

### 7.1 Mechanic Incentives (Tiered)

| Incentive | Amount | Timing | Purpose |
|-----------|--------|--------|---------|
| Per approved entry | ₹20 | Monthly payout | Core motivation |
| Welcome bonus | ₹50 | After first 10 approved entries | Initial activation |
| Referral bonus (referrer) | ₹50 | After referee completes 10 entries | Viral growth |
| Referral bonus (referee) | ₹25 | On registration (after 10 entries) | Reduces friction |
| Top 10 monthly earner | ₹500 bonus | Monthly | Competitive motivation |
| Clean data bonus (no rejections) | ₹100 | Monthly (if 100% approval rate) | Data quality motivation |

### 7.2 Distributor Incentives

| Incentive | Amount | Timing |
|-----------|--------|--------|
| Per installation | ₹10 | After mechanic logs first entry |
| Top 3 monthly installers | ₹5,000 / ₹3,000 / ₹2,000 | Monthly |
| City monthly entry volume bonus | ₹500/city if >500 entries | Monthly |

### 7.3 Why ₹20 Per Entry?

- **Mechanic perspective:** 5 minutes of work (including photo) for ₹20. At 15 entries/day, that is ₹300/day extra. At 20 working days/month, ₹6,000/month.
- **Vikas Group perspective:** 500 mechanics × 15 entries/month × ₹20 = ₹1,50,000/month. The data value far exceeds this cost.
- **Competitive reference:** Comparable to survey apps, micro-task platforms in India. ₹20 is meaningful.

---

## 8. Activation and Onboarding Flow

### 8.1 The Activation Funnel

```
Install (100%)
    |
    v
Open app (expect 95%)
    |
    v
Select language (90%)
    |
    v
Enter OTP (85%)
    |
    v
Complete onboarding (80%)
    |
    v
See Home screen (75%)
    |
    v
Create first entry (60%)
    |
    v
Create 5 entries (40%)  ← THIS IS THE CRITICAL POINT
    |
    v
Active user (30% → 70% if first payout received)
```

**Key insight:** The drop-off is highest between first entry and fifth entry. A mechanic who has logged 10+ entries in their first week has a 80%+ probability of being active in month 3.

### 8.2 Activation Tactics

**Tactic A: First Entry Assisted (Field Agent)**

Best conversion. Agent helps log the first entry on the spot. Mechanic sees how easy it is.

**Tactic B: Welcome Call**

Within 24 hours of first entry, a Vikas Group or field agent calls the mechanic:
- "Thank you for installing PartLog!"
- "Did you face any issue with the app?"
- "Remember — every entry is ₹20. Try to log at least 3–5 entries this week."

**Tactic C: First Entry Bonus**

Mechanic gets ₹25 bonus (in addition to regular ₹20) for their first entry. Creates immediate positive reinforcement.

**Tactic D: WhatsApp Reminder Sequence (Automated)**

```
Day 1 (after install): "PartLog install karne ke liye dhanyavaad! Aaj ek entry try karein."
Day 3 (if 0 entries): "Koi problem hai? Hum madad kar sakte hain. Call karein [number]."
Day 7 (if 0 entries): "Yaad rakhein — har entry = ₹20. Iss hafte 5 entries poori karein."
Day 14 (if <5 entries): "Aapne abhi tak sirf X entries ki hain. 10 entries complete karte hi ₹50 bonus."
Day 30 (if <10 entries): Mechanic is likely churned. Flag for call.
```

### 8.3 First-Entry Experience Optimisation

The first entry MUST take <90 seconds. The app should auto-advance, pre-fill defaults, and minimise typing.

**Optimisation for first entry:**
- Skip voice note (show "Skip" prominently)
- Pre-select "Unknown" for failure cause (mechanic can change)
- Pre-fill today's date
- Auto-detect city from GPS
- Only 1 photo required (damage), label and installed optional

---

## 9. Retention and Habit Building

### 9.1 The 10-Entry Threshold

Analysis from similar field data collection apps shows: a mechanic who crosses 10 entries in their first 14 days has a >80% retention rate at 3 months.

**How to push mechanics to 10 entries:**
- Day 1: Welcome call + assisted first entry
- Day 3: WhatsApp tip + "Bas 9 entries baki hain 10 complete karne ke liye"
- Day 7: 10-entry bonus reminder (+₹50 on top of regular payout)
- Day 10: "Sirf X entries baki hain bonus ke liye"
- Day 14: Bonus credited notification

### 9.2 Habit Stacking

Encourage mechanics to associate entry logging with an existing habit:

> "Jab bhi AC part badalte hain, part nikalte waqt photo lo. Part lagane ke baad turant app mein entry karo."

Natural trigger points:
- When removing the old part (photo opportunity)
- When the customer is paying the bill (waiting time — log the entry)
- During tea break in the afternoon (batch log for the day)

### 9.3 Churn Prevention

| Risk Factor | Early Signal | Intervention |
|------------|-------------|--------------|
| No entries in 7 days | App installed but unused | WhatsApp reminder + call |
| Entries dropped 50%+ vs previous week | Mechanic losing interest | Call + ask for feedback |
| App not opened in 14 days | Near-churn | Call + offer help + remind of missed earnings |
| Payout not received | Mechanic may blame the app | Ensure payouts are always on time; call to resolve |
| Rejected entry | Mechanic may feel cheated | Call and explain reason politely |

---

## 10. Measurement and KPIs

### 10.1 Distribution KPIs

| Metric | Target | Measured By |
|--------|--------|-------------|
| APK downloads | 2× target mechanics | Download page analytics |
| Install completion rate | >70% (downloads that result in first app open) | App analytics (first_open event) |
| Play Store invite acceptance | >60% (internal testing phase) | Play Console |
| Distributor install conversion | >50% (distributors who install on >10 mechanics) | Distributor code tracking |
| QR code scans → installs | >5% conversion | URL analytics + QR code with tracking |

### 10.2 Marketing KPIs

| Metric | Target | Measured By |
|--------|--------|-------------|
| WhatsApp group join rate | >80% of invited mechanics | Group membership |
| Referral programme conversion | >20% of mechanics refer someone | Referral code usage |
| Cost per install (CPI) | <₹50 | Total marketing cost / total installs |
| Time from first aware to install | <7 days | Self-reported or inferred from analytics |

### 10.3 Activation KPIs

| Metric | Target | Measured By |
|--------|--------|-------------|
| First entry within 24h of install | >60% | Event: entry_created - first_open |
| 5 entries in first 14 days | >40% | Entry count per mechanic |
| 10 entries in first 30 days | >30% | Entry count per mechanic |
| Onboarding completion rate | >85% | Event: onboarding_completed / installs |

### 10.4 Retention KPIs

| Metric | Target | Measured By |
|--------|--------|-------------|
| D7 retention (app opened in last 7 days) | >60% | Last opened date |
| D30 retention (app opened in last 30 days) | >50% | Last opened date |
| Monthly active users / total registered | >70% | Entry in last 30 days |
| 3-month retention | >50% of month 1 cohort | Active in month 3 |

---

## 11. Budget Allocation for Distribution & Marketing

| Item | Quantity | Unit Cost | Total (₹) | Phase |
|------|----------|-----------|-----------|-------|
| QR code stickers (3×3") | 10,000 | ₹3 | 30,000 | Phase 2 |
| Laminated cards | 10,000 | ₹5 | 50,000 | Phase 2 |
| A3 posters | 1,000 | ₹15 | 15,000 | Phase 2 |
| Field agent (1 person, 3 months) | 3 months | ₹25,000/month | 75,000 | Phase 1 |
| Distributor incentives (installs) | 2,000 installs | ₹10 | 20,000 | Phase 2–3 |
| Mechanic welcome bonus | 2,000 mechanics | ₹50 | 1,00,000 | Phase 1–3 |
| Mechanic referral bonus | 1,000 referrals | ₹50 | 50,000 | Phase 3 |
| Play Store developer account | 1 | ₹2,100 | 2,100 | Phase 1 |
| WhatsApp Business API (optional) | 6 months | ₹2,000/month | 12,000 | Phase 3 |
| **Total** | | | **~₹3,54,100** | |

---

## 12. Risk Register — Distribution & Adoption

| # | Risk | Likelihood | Impact | Mitigation |
|---|------|------------|--------|------------|
| R1 | Mechanics do not trust the app (scam fear) | Medium | High | Use Vikas Group brand name; field agent face-to-face installation; first payout builds trust |
| R2 | Play Store internal testing invite flow too confusing | High | Medium | Switch to direct APK download as primary channel |
| R3 | APK sideloading blocked by phone security settings | Medium | Medium | Provide step-by-step video guide; Xiaomi/OPPO/Vivo phones have different permission flows |
| R4 | Distributors do not cooperate | Medium | High | Provide financial incentive (₹10/install); make it easy (QR card + script) |
| R5 | Mechanics install but never open after first day | High | High | Welcome call within 24h; first entry bonus; WhatsApp reminder sequence |
| R6 | Referral programme gamed (fake referrals) | Low | Medium | Referee must complete 10 entries before bonus pays out |
| R7 | WhatsApp group spam or misuse | Medium | Low | Group admin controls; strict posting rules; broadcast only mode for large groups |
| R8 | APK file leaked outside target audience | Low | Low | Acceptable risk — more installs only help; no sensitive data accessible without login |

---

## 13. Phased Rollout Timeline Summary

```
Month 1: PILOT
  - 4 cities, 50 mechanics
  - Field agent installation + Play Store invites
  - Test and iterate on onboarding flow
  - Weekly feedback calls with first 50 mechanics

Month 2: DISTRIBUTOR ACTIVATION
  - Print QR materials (stickers, cards, posters)
  - Train 20 distributors across 10 cities
  - City-wise WhatsApp groups created
  - Direct APK download page live

Month 3: REFERRAL LAUNCH
  - Referral programme live in app
  - WhatsApp broadcast scaled
  - First monthly payouts processed (builds trust)
  - Target: 200–500 mechanics

Month 4–6: SCALE
  - Aggressive WhatsApp + distributor push
  - YouTube video published
  - Play Store production launch (if ready)
  - Target: 500–1000 mechanics

Month 7–8: NETWORK EFFECT
  - Referral programme generates majority of new installs
  - Payout reputation spreads organically
  - Field agent presence reduced (trigger-based only)
  - Target: 1000–2000+ mechanics
```

---

## 14. Appendix: Field Agent Training Script

### 14.1 The 2-Minute Pitch

```
[Greet the mechanic by name — find out from the distributor beforehand]

"Namaste [name] ji. Main Vikas Group ki taraf se aaya hoon.
Aapko pata hai, Vikas Group AC condenser aur compressor banata hai?"

[Let them nod or confirm]

"To hum ek naya programme launch kar rahe hain — PartLog.
Iska matlab hai ki aap jab bhi kisi gaadi ka AC condenser ya compressor badalte hain,
toh app mein uski entry karein.

Bas photo lo, model batao, kyun bigda — 1 minute ka kaam hai.
Har entry ke ₹20 aapko UPI se milenge.
Har mahine 100–150 entries aaram se ho sakti hain.

Main abhi aapke phone mein install kar deta hoon.
PhoTo lekar dekhte hain ek entry — 1 minute mein ho jaayega."

[Install the app, log the first entry together with the mechanic]
```

### 14.2 Handling Objections

| Objection | Response |
|-----------|----------|
| "Mere paas time nahi hai" | "Sirf 1 minute lagta hai. Photo + model + cause — bas itna hai. Aur ₹20 milte hain." |
| "Mujhe English nahi aati" | "App Hindi mein hai. Aur 9 aur Indian languages mein bhi." |
| "Kya main sach mein paisa paunga?" | "Har mahine UPI payment. Pehle mahine ke baad aapko trust ho jaayega." |
| "Mera internet nahi hai" | "App bina internet ke bhi chalti hai. Jab internet aaye to auto-sync ho jaata hai." |
| "Data chori ho jaayega?" | "Sirf vehicle details hai. Aapka koi personal data nahi." |
| "Mujhe kyun karna chahiye?" | "Har mahine ₹2,000–₹3,000 extra income. Koi downside nahi hai." |
