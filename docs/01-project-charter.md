# Project Charter — PartLog

**Version:** 1.0  
**Date:** July 2026  
**Status:** Draft for Approval

---

## 1. Project Title

**PartLog** — Field Data Collection Platform for Automotive Parts Failure Intelligence

---

## 2. Project Purpose

Vikas Group manufactures and supplies AC condensers (for Maruti Suzuki vehicles) and AC compressors (for all OEM vehicles) to the Indian automotive aftermarket. Currently, the company has no real-time visibility into where failures are occurring, which vehicle models fail most frequently, what causes the failures, or which regions generate the highest replacement demand. Supply decisions are based on historical purchase orders, distributor feedback, and intuition — resulting in stockouts at high-demand locations and overstocking at low-demand ones.

PartLog is a field data collection platform that deploys a lightweight Android application to independent mechanics across India. These mechanics log every AC condenser and compressor replacement they perform. The data flows to a central database and web portal where Vikas Group management can view real-time failure patterns by vehicle model, geographic region, failure cause, and season. This intelligence enables smarter inventory planning and will eventually support data-backed pitches to OEMs.

The primary objective is a 6–8 month data collection phase to build a proprietary failure intelligence dataset.

---

## 3. Business Objectives

| # | Objective | Measure |
|---|-----------|---------|
| 1 | Onboard 500–2000 active mechanics across India within 6 months of launch | Mechanic count in database |
| 2 | Collect 18,000–120,000 failure entries over the 6–8 month collection phase | Entry count in database |
| 3 | Achieve monthly active usage rate of ≥70% among registered mechanics | Active this month / total registered |
| 4 | Collect data from ≥20 Indian states | Distinct states in entry data |
| 5 | Achieve ≥90% entry completeness (odometer, GPS, photos optional) | Entries with mandatory fields populated |
| 6 | Maintain data quality with <5% rejection rate by admin | Rejected entries / total entries |
| 7 | Establish a repeatable monthly payout process with <48 hour turnaround from month-end | Hours from month-end to payout completion |

---

## 4. Project Scope

### In Scope

- Android mobile application for mechanics (React Native / Expo)
- Web portal for company admins (Next.js / Tailwind)
- Backend REST API (Node.js / Express / Prisma)
- PostgreSQL database on Azure
- Azure Blob Storage for photos and audio notes
- MSG91 SMS OTP integration for mechanic registration
- Expo Push API for mechanic notifications
- Google Play Store internal testing track for APK distribution
- Mechanic self-registration via mobile OTP
- Condenser failure logging (Maruti Suzuki models only)
- Compressor failure logging (all OEMs, free-text entry)
- Offline-first mobile architecture with background sync
- 10 Indian languages (English, Hindi, Tamil, Telugu, Malayalam, Kannada, Marathi, Gujarati, Bengali, Punjabi)
- Admin dashboard with charts (model-wise, state-wise, cause-wise, daily volume)
- Entry review and rejection workflow
- Mechanic management (view, deactivate)
- Payout rate configuration (separate for condenser and compressor)
- Monthly payout generation and manual UPI transfer tracking
- Data export (CSV and Excel)
- 5–8 month data collection phase operations

### Explicitly Out of Scope (this version)

- AI/ML models or demand forecasting
- Distributor portal
- Supervisor / regional manager role
- Automated UPI payments
- iOS application
- Real-time dashboard or WebSocket updates
- Multi-company / multi-brand support
- Inventory management
- CRM or mechanic communication features
- Public API for third-party integration

---

## 5. Stakeholders

| Stakeholder | Role | Interest |
|-------------|------|----------|
| Vikas Group Senior Management | Strategic decision-makers | Use data to improve supply planning and build OEM relationships |
| Company Admins (2–5 users) | Day-to-day platform operators | Process entries, manage mechanics, run payouts, generate reports |
| Independent Mechanics (500–2000) | Field data contributors | Earn supplementary income via UPI payouts for logged entries |
| Founder / Product Owner | Project sponsor and builder | Deliver the platform, manage development, ensure adoption |
| OEMs (future) | Potential data customers | Receive failure intelligence reports (future phase) |
| Google Play Store | APK distribution channel | Host the Android app for internal testing |
| MSG91 | SMS OTP service provider | Deliver authentication SMS to mechanics |

---

## 6. Project Sponsor

**Vikas Group**  
Owner and funder of the project. Provides budget for Azure infrastructure, mechanic payouts, and external services. Approves charter, milestones, and major scope changes.

---

## 7. Project Manager / Owner

**Founder / Product Owner**  
Single individual responsible for:
- Product definition and requirements
- AI-assisted software development
- Azure infrastructure setup and management
- Google Play Store publishing
- Mechanic onboarding and support
- Payout processing
- Reporting to senior management

No dedicated project manager, QA engineer, or developer team.

---

## 8. High-Level Deliverables

### Product Deliverables
1. **Android Mobile Application** — APK distributed via Google Play internal testing track
2. **Web Portal** — hosted on Azure App Service (or Vercel), accessed via browser
3. **Backend API** — Node.js/Express REST API on Azure App Service
4. **PostgreSQL Database** — on Azure Database for PostgreSQL Flexible Server

### Supporting Deliverables
5. Project Charter (this document)
6. Business Requirements Document (BRD)
7. Feasibility Study Report (FSR)
8. Software Requirements Specification (SRS)
9. Project Management Plan (PMP)
10. System Architecture Document (SAD)
11. Data Model and Database Schema
12. UI/UX Wireframes and User Flows

---

## 9. Milestones

| # | Milestone | Target Timeline |
|---|-----------|----------------|
| M1 | Pre-development documentation complete (8 documents) | Week 2 |
| M2 | Azure infrastructure provisioned, CI/CD pipeline operational | Week 3 |
| M3 | Backend API complete with all endpoints (auth, entries, sync, payout) | Week 6 |
| M4 | Android mobile app complete (offline-first, 10 languages, full flows) | Week 10 |
| M5 | Web portal complete (dashboard, entries, mechanics, payouts, export) | Week 13 |
| M6 | Google Play internal testing track live, pilot with 50 mechanics | Week 14 |
| M7 | Full-scale launch, 500+ mechanics onboarded | Week 18 |
| M8 | End of 8-month data collection phase — dataset handover | Month 8 from launch |

---

## 10. Assumptions

1. Mechanics own Android smartphones with minimum 3GB RAM and Android 8.0+
2. Mechanics have basic literacy and can navigate a simple touch interface
3. 4G internet connectivity is available at least once daily for sync
4. Mechanics are motivated by monthly UPI payouts (₹ per entry)
5. MSG91 SMS delivery rates in India are sufficient for OTP authentication
6. Google Play Store internal testing approval will be granted within 1 week
7. Azure services in Central India region provide adequate latency
8. A single developer using AI-assisted tools can build and maintain this platform
9. Vikas Group will provide domain expertise and admin availability for testing
10. No regulatory or legal barriers to collecting vehicle failure data from mechanics

---

## 11. Constraints

1. **Budget:** Early-stage internal initiative with lean budget. Azure infrastructure estimated at ₹16,200/month. No separate budget for development tools, office space, or support staff.
2. **Team size:** Single developer (founder/product owner) building with AI assistance.
3. **Timeline:** Primary goal is 6–8 months of data collection. Platform must be operational within 14–18 weeks from start.
4. **Technology stack:** Already decided — React Native (Expo), Node.js/Express/Prisma, Next.js, PostgreSQL on Azure, MSG91. No deviation permitted without strong justification.
5. **Mobile platform:** Android only for this phase. iOS explicitly excluded.
6. **Payment model:** No automated UPI integration. All payouts handled manually by admin.
7. **APK distribution:** Google Play Store internal testing track only. No side-loading or third-party stores.

---

## 12. High-Level Risks

| # | Risk | Likelihood | Impact | Mitigation |
|---|------|------------|--------|------------|
| R1 | Low mechanic adoption (<200 registered in 3 months) | Medium | High | Target workshops directly via distributor network; offer higher initial payout rates |
| R2 | Poor data quality (incomplete or fabricated entries) | Medium | High | Validate mandatory fields client-side; odometer format checks; geometry check on GPS; admin review with rejection capability |
| R3 | Solo developer becomes bottleneck (illness, burnout, skill gap) | Medium | High | Document all code; use managed services (Azure, Prisma) to reduce maintenance burden |
| R4 | Azure costs exceed ₹16,200/month estimate | Low | Medium | Set budget alerts at 80% and 100%; use reserved instances if predictable |
| R5 | MSG91 SMS delivery failure in certain regions/carriers | Low | Medium | Fallback to retry with exponential backoff; log delivery status; manual OTP bypass available for support cases |
| R6 | Google Play Store rejects app (policy, permissions, or content) | Low | High | Pre-review against Play Store policies before submission; use internal testing track not production |
| R7 | Database performance degradation at scale | Low | Medium | Use appropriate Azure tier with connection pooling; Prisma query optimisation; indexing strategy in schema |
| R8 | Mechanic UPI payout disputes (entries rejected after approval) | Medium | Medium | Clear terms visible in app; rejections only within 7 days of entry; audit log for all admin actions |

---

## 13. Success Criteria

The project is considered successful when:

1. **Platform is operational** — Mobile app is published on Google Play internal testing track and web portal is accessible at a stable URL.
2. **Mechanic adoption target met** — 500+ active mechanics registered within 6 months of launch.
3. **Data volume target met** — 18,000+ failure entries collected over the collection phase.
4. **Geographic coverage achieved** — Data collected from 20+ Indian states.
5. **Data quality acceptable** — Less than 5% of entries rejected by admin.
6. **Payout process functional** — Monthly payouts processed with <48 hour turnaround from month-end.
7. **Knowledge asset created** — The collected dataset is structured, documented, and ready for future analysis or OEM pitch decks.

---

## 14. Approvals

| Role | Name | Signature | Date |
|------|------|-----------|------|
| Project Sponsor | _________________________ | _________________________ | ______ |
| Project Manager / Owner | _________________________ | _________________________ | ______ |
| Senior Management | _________________________ | _________________________ | ______ |
