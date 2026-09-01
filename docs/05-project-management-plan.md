# Project Management Plan (PMP) — PartLog

**Version:** 1.0  
**Date:** July 2026  
**Prepared by:** Founder / Product Owner

---

## 1. Project Overview

### 1.1 Scope

PartLog is a field data collection platform with two products:
1. **Android Mobile App** — for 500–2000 mechanics to log AC condenser and compressor failure entries (offline-first, 10 languages)
2. **Web Portal** — for company admins to manage data, mechanics, and monthly UPI payouts

### 1.2 Objectives

- Platform operational within 14–18 weeks from project start
- 500+ mechanics onboarded within 6 months of launch
- 18,000+ failure entries collected over 6–8 month data collection phase
- <48 hour monthly payout turnaround

### 1.3 Timeline Summary

| Phase | Duration | Weeks |
|-------|----------|-------|
| Phase 0: Pre-development documentation | 2 weeks | 1–2 |
| Phase 1: Infrastructure setup | 1 week | 3 |
| Phase 2: Backend API development | 3 weeks | 4–6 |
| Phase 3: Mobile app development | 4 weeks | 7–10 |
| Phase 4: Web portal development | 3 weeks | 11–13 |
| Phase 5: Testing and QA | 2 weeks | 14–15 |
| Phase 6: Play Store and pilot launch | 1 week | 16 |
| Phase 7: Data collection operations | Ongoing | 17+ |

---

## 2. Development Methodology

### Recommendation: **Kanban with weekly sprints**

**Why Kanban for a solo AI-assisted developer:**
- No dependency conflicts (only one developer)
- No need for sprint planning meetings
- Work-in-progress limits naturally enforced (can only do one thing at a time)
- AI tools integrate naturally into a flow-based system
- Flexibility to handle unplanned support work during operations phase

**Weekly cycle (loose sprint structure):**
- **Monday morning:** Plan the week — pick 3–5 tasks from the backlog
- **Daily:** Work through tasks using AI-assisted development
- **Friday afternoon:** Review progress, update backlog, deploy to staging if applicable
- **No formal retrospectives** (solo developer — mental reflection only)

**Tool:**
- GitHub Projects (Kanban board with columns: Backlog, Ready, In Progress, Review, Done)
- Or a simple markdown TODO list tracked in the repository

**Ticket types:**
- `feature` — new functionality
- `fix` — bug or issue
- `chore` — infrastructure, CI/CD, dependencies
- `docs` — documentation

---

## 3. Work Breakdown Structure (WBS)

### Phase 0: Pre-Development Documentation (Week 1–2)

| Task ID | Task | Hours |
|---------|------|-------|
| 0.1 | Write Project Charter | 4 |
| 0.2 | Write Business Requirements Document (BRD) | 8 |
| 0.3 | Write Feasibility Study Report (FSR) | 6 |
| 0.4 | Write Software Requirements Specification (SRS) | 12 |
| 0.5 | Write Project Management Plan (PMP) | 4 |
| 0.6 | Write System Architecture Document (SAD) | 10 |
| 0.7 | Write Data Model and Database Schema | 8 |
| 0.8 | Write UI/UX Wireframes and User Flows | 10 |

### Phase 1: Infrastructure Setup (Week 3)

| Task ID | Task | Hours |
|---------|------|-------|
| 1.1 | Create Azure subscription and resource group | 1 |
| 1.2 | Provision Azure App Service (Linux, B2) | 1 |
| 1.3 | Provision Azure PostgreSQL Flexible Server (B2ms) | 1 |
| 1.4 | Provision Azure Blob Storage account + container | 1 |
| 1.5 | Set up PostgreSQL database firewall rules (App Service IP, dev IP) | 0.5 |
| 1.6 | Create GitHub repository + branch protection rules | 0.5 |
| 1.7 | Set up GitHub Actions workflow for Node.js CI | 2 |
| 1.8 | Set up Azure App Service deployment from GitHub | 1 |
| 1.9 | Set up Vercel project for Next.js deployment | 1 |
| 1.10 | Set up Expo EAS project + first build | 2 |
| 1.11 | Create .env templates for all environments | 1 |
| 1.12 | Create Azure cost budgets and alerts | 0.5 |

### Phase 2: Backend API Development (Week 4–6)

| Task ID | Task | Hours |
|---------|------|-------|
| 2.1 | Initialise Express.js project with TypeScript | 2 |
| 2.2 | Set up Prisma schema + run first migration | 3 |
| 2.3 | Implement Zod validation schemas for all entities | 4 |
| 2.4 | Implement middleware: auth, CORS, rate limiting, error handler | 3 |
| 2.5 | Implement MSG91 OTP integration (send, verify, resend) | 4 |
| 2.6 | Implement mechanic auth endpoints (register, login, refresh) | 4 |
| 2.7 | Implement admin auth endpoints (login with session) | 3 |
| 2.8 | Implement entry CRUD endpoints | 6 |
| 2.9 | Implement photo upload with SAS URL generation | 4 |
| 2.10 | Implement audio upload with SAS URL generation | 2 |
| 2.11 | Implement duplicate detection endpoint | 2 |
| 2.12 | Implement sync endpoint (batch entry submission) | 4 |
| 2.13 | Implement payout rate CRUD endpoints | 2 |
| 2.14 | Implement monthly payout calculation and listing endpoints | 4 |
| 2.15 | Implement analytics/dashboard endpoints (model, state, cause, daily volume) | 4 |
| 2.16 | Implement mechanic management endpoints (list, detail, deactivate) | 3 |
| 2.17 | Implement audit logging middleware | 2 |
| 2.18 | Implement CSV/Excel export endpoints | 3 |
| 2.19 | Implement Expo push notification endpoint | 2 |
| 2.20 | Write API tests (Jest, supertest) — critical paths | 6 |
| 2.21 | Write API documentation (README or Postman collection) | 2 |

### Phase 3: Mobile App Development (Week 7–10)

| Task ID | Task | Hours |
|---------|------|-------|
| 3.1 | Initialise Expo project + configure TypeScript | 2 |
| 3.2 | Set up i18next with 10 language files (English first) | 6 |
| 3.3 | Set up Zustand store (auth, entries, sync, UI state) | 3 |
| 3.4 | Set up expo-sqlite with local schema | 4 |
| 3.5 | Implement API client (Axios with token refresh interceptor) | 3 |
| 3.6 | Implement language selection screen | 2 |
| 3.7 | Implement mobile number entry + OTP verification screen | 4 |
| 3.8 | Implement onboarding form screen | 3 |
| 3.9 | Implement home screen (stats, sync status, quick actions) | 3 |
| 3.10 | Implement part type selection screen | 1 |
| 3.11 | Implement condenser entry flow (model chip grid → variant → fuel → year → reg → cause → severity → odometer) | 8 |
| 3.12 | Implement compressor entry flow (OEM free-text → model → variant → fuel → year → reg → cause → severity → odometer) | 4 |
| 3.13 | Implement photo capture screen (3 slots, camera preview) | 6 |
| 3.14 | Implement voice note recording screen | 4 |
| 3.15 | Implement text note screen | 1 |
| 3.16 | Implement GPS capture with expo-location | 2 |
| 3.17 | Implement entry review + confirmation screen | 3 |
| 3.18 | Implement sync queue manager + background sync service | 6 |
| 3.19 | Implement offline queue UI (pending entries list, retry) | 3 |
| 3.20 | Implement duplicate detection warning dialog | 2 |
| 3.21 | Implement earnings screen | 3 |
| 3.22 | Implement UPI ID entry/settings | 2 |
| 3.23 | Implement settings screen (profile edit, language, UPI, export, logout) | 4 |
| 3.24 | Implement push notification setup (Expo push token) | 2 |
| 3.25 | Implement entry detail view (read-only) | 2 |
| 3.26 | Add remaining 9 language translations | 16 |
| 3.27 | Test on physical device (Android 8–14, various screen sizes) | 8 |

### Phase 4: Web Portal Development (Week 11–13)

| Task ID | Task | Hours |
|---------|------|-------|
| 4.1 | Initialise Next.js 14 project + Tailwind CSS | 2 |
| 4.2 | Set up NextAuth.js with credentials provider | 3 |
| 4.3 | Set up TanStack Query with API client | 2 |
| 4.4 | Implement layout (sidebar, header, main content area) | 4 |
| 4.5 | Implement login page | 2 |
| 4.6 | Implement dashboard page with 5 KPI cards + Recharts | 6 |
| 4.7 | Implement entries table with TanStack Table (filtering, sorting, pagination) | 8 |
| 4.8 | Implement entry detail side panel with photo lightbox and audio player | 6 |
| 4.9 | Implement entry approve/reject with rejection reason modal | 4 |
| 4.10 | Implement bulk rejection | 3 |
| 4.11 | Implement mechanics list page | 3 |
| 4.12 | Implement mechanic detail page with tabs (entries, payouts, activity) | 4 |
| 4.13 | Implement mechanic deactivate/reactivate | 2 |
| 4.14 | Implement payout management page (monthly summary, mark paid) | 6 |
| 4.15 | Implement payout rate configuration page | 2 |
| 4.16 | Implement export page/buttons (CSV + Excel) | 3 |
| 4.17 | Implement responsive design (1440, 1024, 768) | 3 |
| 4.18 | Test all flows end-to-end | 6 |

### Phase 5: Testing and QA (Week 14–15)

| Task ID | Task | Hours |
|---------|------|-------|
| 5.1 | End-to-end testing of all mobile flows | 8 |
| 5.2 | End-to-end testing of all web portal flows | 6 |
| 5.3 | Offline sync testing (airplane mode, background sync) | 4 |
| 5.4 | Language testing (verify all 10 languages display correctly) | 4 |
| 5.5 | Photo upload and audio upload testing | 2 |
| 5.6 | Duplicate detection testing | 1 |
| 5.7 | Payout calculation accuracy testing | 2 |
| 5.8 | Performance testing (API load with 100 concurrent requests) | 2 |
| 5.9 | Security testing (unauthorised access, injection, file upload) | 3 |
| 5.10 | Fix all critical and high-severity bugs | 8 |

### Phase 6: Play Store Submission and Pilot Launch (Week 16)

| Task ID | Task | Hours |
|---------|------|-------|
| 6.1 | Create Play Console developer account (if not existing) | 1 |
| 6.2 | Prepare Play Store listing (screenshots, description, privacy policy) | 3 |
| 6.3 | Build APK via Expo EAS for internal testing | 1 |
| 6.4 | Submit for internal testing track review | 1 |
| 6.5 | Invite 10 pilot mechanics | 1 |
| 6.6 | Conduct pilot: 7 days of real usage | 8 |
| 6.7 | Fix pilot issues | 4 |
| 6.8 | Scale invitations to 50 mechanics | 1 |

### Phase 7: Data Collection Operations (Week 17+)

| Task ID | Task | Frequency |
|---------|------|-----------|
| 7.1 | Monitor mechanic adoption and entry volume | Daily |
| 7.2 | Review and reject entries (admin) | Daily |
| 7.3 | Process monthly payouts | Monthly |
| 7.4 | Monitor Azure costs and performance | Weekly |
| 7.5 | Respond to mechanic support requests | As needed |
| 7.6 | Bug fixes and minor improvements | As needed |
| 7.7 | Monthly report to management | Monthly |

---

## 4. Timeline and Milestones

```
Week | Phase 0 | Phase 1 | Phase 2 | Phase 3 | Phase 4 | Phase 5 | Phase 6 | Phase 7
-----|---------|---------|---------|---------|---------|---------|---------|--------
 1   | Doc (1)  |         |         |         |         |         |         |
 2   | Doc (2)  |         |         |         |         |         |         |
 3   |         | Infra    |         |         |         |         |         |
 4   |         |         | Backend  |         |         |         |         |
 5   |         |         | Backend  |         |         |         |         |
 6   |         |         | Backend  |         |         |         |         |
 7   |         |         |         | Mobile   |         |         |         |
 8   |         |         |         | Mobile   |         |         |         |
 9   |         |         |         | Mobile   |         |         |         |
10   |         |         |         | Mobile   |         |         |         |
11   |         |         |         |          | Portal  |         |         |
12   |         |         |         |          | Portal  |         |         |
13   |         |         |         |          | Portal  |         |         |
14   |         |         |         |          |         | QA      |         |
15   |         |         |         |          |         | QA      |         |
16   |         |         |         |          |         |         | Launch  |
17+  |         |         |         |          |         |         |         | Ops
```

### Milestones

| Milestone | Week | Deliverable |
|-----------|------|-------------|
| M1: Documentation complete | 2 | All 8 pre-dev documents approved |
| M2: Infrastructure ready | 3 | Azure resources provisioned, CI/CD pipelines functional |
| M3: Backend API complete | 6 | All endpoints implemented, tested, deployed to staging |
| M4: Mobile app complete | 10 | APK builds successfully, all flows functional on device |
| M5: Web portal complete | 13 | All admin flows functional, deployed to production |
| M6: QA complete | 15 | All critical bugs fixed, performance acceptable |
| M7: Pilot launch | 16 | APK on Play Store, 10 pilot mechanics active |
| M8: Full-scale launch | 18 | 50+ mechanics onboarded, expanding to 500+ |

---

## 5. Resource Plan

### Human Resources

| Role | Person | Weekly Hours (Build Phase) | Weekly Hours (Ops Phase) |
|------|--------|---------------------------|--------------------------|
| Project Manager | Founder/PO | 5 (embedded) | 2 |
| Developer | Founder/PO | 40+ | 10–20 |
| QA | Founder/PO | Embedded in build | 2 |
| Admin (Vikas Group) | Company staff | 0 (build) | 5–10 (operations) |
| Mechanic support | Founder/PO | 0 (build) | 5 (operations) |

### External Services

| Service | Purpose | Cost |
|---------|---------|------|
| GitHub | Source control, CI/CD | Free |
| Azure | Cloud infrastructure | ~₹16,200/month |
| MSG91 | SMS OTP | ~₹2,000/month |
| Expo EAS | APK builds | Free tier |
| Google Play Console | APK distribution | $25 one-time |
| Vercel | Web portal hosting (or self-host) | Hobby tier (free) |

---

## 6. Budget Plan

### One-Time Costs

| Item | Cost (₹) |
|------|---------|
| Google Play Developer Account | ~2,100 |
| **Total one-time** | **~2,100** |

### Monthly Infrastructure Costs (Fixed)

| Item | Cost (₹/month) |
|------|---------------|
| Azure App Service B2 | ~3,500 |
| Azure PostgreSQL B2ms (128 GB) | ~5,000 |
| Azure Blob Storage (50 GB) | ~700 |
| Azure Bandwidth (50 GB) | ~3,000 |
| MSG91 SMS (~3,000 OTPs) | ~2,000 |
| **Total infrastructure** | **~14,200** |

### Variable Costs (Mechanic Payouts)

| Scenario | Mechanics | Entries/mech/mo | Rate (₹) | Payout Cost (₹/mo) |
|----------|-----------|----------------|-----------|-------------------|
| Pilot (month 1–3) | 50–200 | 10 | 20 | 10,000–40,000 |
| Growth (month 4–6) | 200–500 | 15 | 20 | 60,000–1,50,000 |
| Scale (month 7–12) | 500–1000 | 20 | 20 | 2,00,000–4,00,000 |

### Total Monthly Cost Ranges

| Month | Infrastructure | Payouts | Total (₹) |
|-------|---------------|---------|-----------|
| 1–3 (Pilot) | 14,200 | 10,000–40,000 | 24,200–54,200 |
| 4–6 (Growth) | 14,200 | 60,000–1,50,000 | 74,200–1,64,200 |
| 7–12 (Scale) | 14,200 | 2,00,000–4,00,000 | 2,14,200–4,14,200 |

### Annual Projection (Conservative)

| Item | Annual Cost (₹) |
|------|----------------|
| Infrastructure (12 months) | ~1,70,400 |
| SMS OTP (12 months) | ~24,000 |
| Payouts (average ₹1,00,000/month) | ~12,00,000 |
| One-time fees | ~2,100 |
| **Total annual** | **~13,96,500** |

---

## 7. Risk Management Plan

| # | Risk | Owner | Probability | Impact | Mitigation | Contingency |
|---|------|-------|------------|--------|------------|-------------|
| R1 | Solo developer illness or unavailability | Founder | Medium | High | Document all code; use managed services; cross-train on deployment | Pause non-critical development; focus on operations only |
| R2 | Low mechanic adoption | Founder | Medium | High | Target distribution network; offer competitive payout rates | Reduce Azure tier to save cost; focus on quality over quantity |
| R3 | AI tool limitations (code quality issues) | Founder | Medium | Medium | Review all AI-generated code; write tests for critical paths | Manual rewrite of problematic modules |
| R4 | Play Store rejection | Founder | Low | High | Pre-review policy requirements before submission | Use direct APK download from portal as fallback |
| R5 | Azure cost overrun | Founder | Low | Medium | Budget alerts at 80% and 100%; monthly review | Scale down tier; move to reserved instances |
| R6 | Data quality issues | Admin | Medium | High | Client-side validation; GPS checks; admin review workflow | Implement additional validation rules based on observed patterns |
| R7 | MSG91 outage or API change | Founder | Low | Medium | Monitor MSG91 status; keep OTP bypass capability | Evaluate alternative SMS provider |
| R8 | Expo SDK breaking changes | Founder | Low | Medium | Lock SDK version; test upgrades in staging before production | Stay on current version until forced to upgrade |
| R9 | Admin availability (Vikas Group) | Founder | Medium | High | Define clear admin responsibilities before launch | Founder handles entry review temporarily |
| R10 | Mechanic payout disputes | Admin | Medium | Medium | Clear terms in app; rejection window (7 days); audit log | Manual payout adjustment on case-by-case basis |

---

## 8. Quality Management Plan

### Code Quality

- **AI code review:** All AI-generated code reviewed by human before commit
- **TypeScript strict mode:** Enabled across all projects
- **ESLint + Prettier:** Standard config for consistency
- **Pre-commit hooks:** Husky + lint-staged (format + lint on commit)

### Testing Strategy

| Test Type | Coverage Target | Tool |
|-----------|----------------|------|
| Unit tests (API) | Critical paths (auth, entry CRUD, payout calculation) | Jest + supertest |
| Integration tests (API) | Entry sync, photo upload, OTP flow | Jest + supertest |
| Mobile smoke tests | All screens render, all navigation flows work | Manual on physical device |
| Web smoke tests | All pages load, all CRUD operations work | Manual in browser |
| Offline tests | Entry creation without network, sync when online | Manual with airplane mode |
| Language tests | All 10 languages display correctly | Manual verification per language |

### Entry Validation Rules (Data Quality)

| Field | Rule |
|-------|------|
| Mobile | 10 digits, Indian format |
| Odometer | Numeric, 100–9,99,999 |
| GPS | Valid coordinates (India bounding box: 6.5–35.5 N, 68.0–97.5 E) |
| Photos | JPEG only, max 5MB each, ≤3 per entry |
| Audio | M4A only, max 2MB, ≤60 seconds |
| Registration | Alphanumeric, 5–12 chars, optional |
| Notes | Max 200 characters |

### Operational Quality

- Monthly data quality report (rejection rate, completeness, duplicate rate)
- Weekly sync success rate monitoring
- Payout accuracy: manual reconciliation of calculated vs paid amounts for first 3 months

---

## 9. Communication Plan

| Audience | Frequency | Channel | Content |
|----------|-----------|---------|---------|
| Founder (self) | Daily | GitHub Projects / TODO | Task progress |
| Vikas Group management | Weekly | Email / WhatsApp | Status update: mechanics onboarded, entries collected, issues |
| Vikas Group admin | Daily (build) / Weekly (ops) | WhatsApp / Phone | Feature demo, bug reports, feedback |
| Pilot mechanics | Weekly (pilot) | WhatsApp group | Tips, updates, payout reminders |
| All mechanics | Monthly | In-app notification | Payout announcement, version updates |

### Reporting Template (Weekly to Management)

```
PartLog Weekly Update — Week [X]

Mechanics onboarded: [N] (+[N] vs last week)
Entries collected this week: [N]
Total entries all time: [N]
Payouts processed this month: ₹[N]
Issues this week: [list]
Plans for next week: [list]
Blockers: [list or "None"]
```

---

## 10. Change Management

### Change Request Process

1. **Request received** from any stakeholder (mechanic feedback, admin request, founder idea)
2. **Logged** in GitHub Issues with label `change-request`
3. **Assessed** for impact on timeline, budget, and existing functionality
4. **Categorised**:
   - **Critical:** Must fix immediately (blocks operations) — no formal process, just fix
   - **Important:** Should add in current phase — slot into backlog
   - **Nice-to-have:** Future consideration — add to Phase 8+ backlog
5. **Communicated** back to requester with decision and timeline

### Scope Change Principles

- **Phase 0–6 (Build):** No new features that add >4 hours of work without management approval
- **Phase 7 (Operations):** Bug fixes and minor improvements only; major features postponed to v2
- Any feature in the "Out of Scope" list in the Project Charter requires a formal scope change with management sign-off

---

## 11. Deployment Plan

### Environments

| Environment | URL | Purpose | Deployed From |
|-------------|-----|---------|---------------|
| Development | localhost | Active development | Local machine |
| Staging | staging.partlog.app | Integration testing, QA | GitHub Actions (main branch) |
| Production | app.partlog.app | Live platform | GitHub Actions (release tag) |

### CI/CD Pipeline (Backend)

```
Git push to main → GitHub Actions:
  1. Install dependencies
  2. Run ESLint + Prettier check
  3. Run Prisma generate
  4. Run tests (Jest)
  5. Deploy to Azure App Service (staging)
  6. (Manual approval) Deploy to production
```

### CI/CD Pipeline (Web Portal)

```
Git push to main → Vercel auto-deploy:
  1. Build Next.js
  2. Deploy to Vercel production
```

### Mobile App Build

```
On demand via CLI: `eas build --platform android --profile production`
  → Expo EAS builds APK
  → Uploaded to Google Play Console internal testing track
```

### Release Process

1. Develop feature on feature branch
2. Open PR to main
3. AI code review + human review
4. Merge to main → auto-deploy to staging
5. Test on staging
6. Tag release (v1.x.x) → deploy to production
7. If mobile build needed: run `eas build` + upload to Play Store

---

## 12. KPIs for Project Success

### Development Phase KPIs

| KPI | Target |
|-----|--------|
| Weeks to platform operational (Phase 0–6) | ≤ 16 weeks |
| API test coverage (critical paths) | ≥ 80% |
| Critical bugs at launch | 0 |
| Major bugs at launch | ≤ 3 |

### Operations Phase KPIs

| KPI | Target | Measurement |
|-----|--------|-------------|
| Active mechanics | 500+ at 6 months | Monthly active (≥1 entry in 30 days) |
| Entry volume | 100–500/day at 12 months | Daily entry count |
| Data quality (approve rate) | ≥ 95% | Approved / total entries |
| Geographic coverage | ≥ 20 states | Distinct states in entries |
| Mechanic retention (6-month) | ≥ 70% | Active at month 6 / registered in month 1 |
| Payout turnaround | < 48 hours | Month-end to last payment marked |
| App crash-free rate | ≥ 99.5% | Play Console / Expo |
| Admin entry review time | < 5 seconds per entry | Average time to approve/reject |
