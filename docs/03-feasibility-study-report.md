# Feasibility Study Report (FSR) — PartLog

**Version:** 1.0  
**Date:** July 2026  
**Prepared for:** Vikas Group Senior Management

---

## 1. Executive Summary

PartLog proposes to build a field data collection platform that deploys an Android app to 500–2000 mechanics across India to log AC condenser and compressor failure data. The data will be used to improve supply planning and eventually pitch data-backed insights to OEMs.

This feasibility study evaluates PartLog across four dimensions: technical, operational, financial, and strategic. The overall verdict is **Go with Conditions**. The project is technically feasible and strategically aligned with Vikas Group's interests, but success depends heavily on mechanic adoption rates and the company's willingness to sustain the programme through an initial low-data period.

---

## 2. Technical Feasibility

### Assessment: Feasible with Moderate Risk

| Factor | Rating | Explanation |
|--------|--------|-------------|
| Tech stack maturity | High | React Native, Node.js, Next.js, PostgreSQL, Azure — all mature, well-documented technologies |
| Offline-first architecture | High | expo-sqlite + background sync is a proven pattern for field data collection apps |
| Single developer with AI | Medium | AI tools accelerate development significantly, but debugging complex issues, managing infrastructure, and handling edge cases still requires engineering skill |
| 10-language i18n | High | i18next is battle-tested; Indian language translation does not require RTL handling |
| Photo upload with offline | Medium | Large photos (3 per entry) while offline requires queue management and storage space management on device |
| MSG91 integration | High | REST API based, well-documented, widely used in India |
| Azure PaaS services | High | App Service, PostgreSQL Flexible Server, Blob Storage — all managed services reduce operational burden |
| Google Play internal testing | Medium | Policies around permissions (camera, location, storage) need careful handling for approval |

### Technical Risks and Mitigations

| Risk | Mitigation |
|------|------------|
| SQLite corruption on device | Use expo-sqlite with WAL mode; implement periodic integrity checks; purge old synced records |
| Sync conflicts if multiple devices use same account | Not applicable — one account per mechanic, single device |
| Large photo sizes cause slow sync | Compress to 720p JPEG before storing; queue uploads with background fetch; limit to 3 photos per entry |
| Azure costs exceed estimate | Set budget alerts; use B2 tier with potential to scale down if usage is lower than expected |
| API performance at 500 entries/day | B2 App Service + B2ms PostgreSQL with connection pooling is adequate for this volume; indexes on key query columns |

### Single Developer Feasibility

A single developer using AI-assisted tools can realistically build PartLog in 14–18 weeks assuming:
- The developer has prior experience with Node.js, React, and PostgreSQL
- AI tools (GitHub Copilot, Cursor, etc.) are used for code generation
- The developer works full-time (40+ hours/week) on the project
- Pre-existing UI component libraries and templates are used where possible

The primary risk is not the build itself but the ongoing maintenance and mechanic support burden once the platform is live. Documentation and automated monitoring are essential.

---

## 3. Operational Feasibility

### Assessment: Feasible with Significant Dependency on Mechanic Adoption

| Factor | Rating | Explanation |
|--------|--------|-------------|
| Mechanic smartphone adoption | High | ~95% of Indian mechanics own a smartphone; Android dominates |
| Mechanic digital literacy | Medium | Basic app navigation is fine; complex workflows must be avoided |
| Network connectivity | Medium | Tier 2/3 cities have 4G but not always reliable — offline mode is essential |
| Language support | High | 10 languages covers ~90%+ of mechanics |
| Play Store distribution | Medium | Internal testing track limits scalability; mechanics must accept Gmail invite |
| Data quality without supervisors | Medium | Auto-approve + admin reject model is pragmatic but relies on admin diligence |
| Monthly payout processing | Medium | Manual UPI transfer per mechanic is labour-intensive at 500+ mechanics |

### Mechanic Adoption Barriers

| Barrier | Severity | Mitigation |
|---------|----------|------------|
| Trust — why should I log data? | High | Clear payout promise; monthly payments build trust over time |
| Habit — remembering to log | Medium | Make entry fast (under 90 seconds); workshop visit is natural trigger |
| Network — no internet at workshop | Low | Offline mode; sync when phone connects to any network |
| Language — app not in my language | Low | 10 languages; default to device language |
| Device — old phone or low storage | Medium | Keep APK small; purge local data after sync; minimum Android 8.0 |
| Payout delay — payment not received | High | Admin must process on time; push notification on payment; clear payout schedule |

### APK Distribution Strategy

Google Play Store internal testing track is the primary distribution channel. Mechanics must:
1. Have or create a Google account (most already do)
2. Accept an email invitation from the developer's Play Console
3. Install from the Play Store link

This works for a pilot of 50 mechanics. At 500+, the process becomes cumbersome. If scaling beyond 2000, a managed Google Play private app or direct APK download from the web portal may be needed.

---

## 4. Financial Feasibility

### Development Cost

| Item | Cost |
|------|------|
| Developer time (14–18 weeks, full-time) | Opportunity cost of founder's time |
| AI tool subscriptions (GitHub Copilot, etc.) | ~$20–$40/month |
| Google Play Developer Account | $25 one-time |
| **Total development cost** | **Minimal direct cost (labour only)** |

### Monthly Operating Costs

| Item | Configuration | Monthly Cost (₹) |
|------|---------------|------------------|
| Azure App Service | B2 (2 vCPU, 4 GB RAM, Linux, Central India) | ~3,500 |
| Azure PostgreSQL | B2ms (2 vCPU, 8 GB, 128 GB storage) | ~5,000 |
| Azure Blob Storage | Standard, 50 GB, LRS | ~700 |
| Azure Bandwidth | 50 GB outbound | ~3,000 |
| MSG91 SMS | ~3,000 OTPs/month | ~2,000 |
| Expo EAS Build | Free tier (30 builds/month) | 0 |
| Vercel (or self-host Next.js) | Hobby tier or on App Service same tier | 0 |
| **Total infrastructure** | | **~₹16,200/month** |

### Mechanic Payout Cost Projections

| Scenario | Mechanics | Entries/mechanic/month | Payout rate | Monthly payout cost |
|----------|-----------|----------------------|-------------|-------------------|
| Conservative | 200 | 10 | ₹20 | ₹40,000 |
| Moderate | 500 | 15 | ₹20 | ₹1,50,000 |
| Optimistic | 1000 | 20 | ₹20 | ₹4,00,000 |
| High volume | 2000 | 25 | ₹20 | ₹10,00,000 |

Note: Admin can set different rates for condenser vs compressor. ₹20 is used as a blended rate for estimation.

### Total Monthly Cost Ranges

| Scenario | Infrastructure + SMS | Payouts | Total (₹/month) |
|----------|-------------------|---------|-----------------|
| Low (200 mechanics, 10 entries/m) | ₹16,200 | ₹40,000 | ~₹56,200 |
| Medium (500 mechanics, 15 entries/m) | ₹16,200 | ₹1,50,000 | ~₹1,66,200 |
| High (1000 mechanics, 20 entries/m) | ₹16,200 | ₹4,00,000 | ~₹4,16,200 |

### Break-Even Analysis (Value of Data)

PartLog does not generate direct revenue. Its value is in the data asset it creates. The break-even question is: **what is the business value of having this data?**

- If the data helps avoid even one significant stockout event per quarter (e.g., 500 units of lost sales at ₹2,000 margin each = ₹10,00,000), the programme pays for itself many times over.
- If the data enables Vikas Group to negotiate better terms with OEMs or distributors, the value multiplies.
- If the data is packaged and sold as market intelligence reports to OEMs, it becomes a direct revenue stream.

**Estimated break-even:** The programme breaks even in business value if it prevents even a single major stockout per quarter OR if it contributes to a 5% improvement in supply planning accuracy (reduced carrying cost + reduced stockout cost).

### ROI Framework

| Benefit | Value Type | Estimated Annual Impact |
|---------|-----------|----------------------|
| Reduced stockouts (top 10 locations) | Cost saving | ₹5–15 lakhs |
| Reduced overstock carrying cost | Cost saving | ₹3–8 lakhs |
| Improved OEM relationship | Strategic | Difficult to quantify upfront |
| Data-backed sales pitches | Revenue potential | High — but 12–18 month horizon |
| Mechanic network as channel | Strategic | Future sales/marketing channel |

---

## 5. Market and Strategic Feasibility

### Assessment: Favourable but Unproven

| Factor | Rating | Explanation |
|--------|--------|-------------|
| Data need (OEM demand) | Medium | OEMs want field failure data but may not pay for it initially |
| Data need (internal) | High | Vikas Group clearly needs this data for supply planning |
| Competitive landscape | Low | No known competitor is doing exactly this for AC parts in India |
| Mechanic network value | Medium | Network takes time to build — not instant asset |
| Strategic alignment with trends | High | Automotive aftermarket is digitising; data-driven supply chain is the direction |

### Competitive Landscape

There is no known Indian startup or incumbent that specifically collects AC condenser and compressor failure data from mechanics. The nearest competitors are:
- **GoMechanic / Pitstop / Carnation** — Multi-brand car service networks. They have failure data but it is internal and not sold as a data product.
- **Bosch / Denso** — Global suppliers with their own warranty data, but they do not share it.
- **Automotive aftermarket platforms (Boodmo, SparesHub)** — Focus on parts e-commerce, not data collection.

PartLog's differentiation is that it is a dedicated failure data collection tool, not a service platform. The data is the product, not the means to another service.

### Realistic Mechanic Adoption Scenario

Mechanics in India are pragmatic. They will adopt PartLog if:
1. **It pays.** ₹2,000–₹5,000 per month is meaningful supplementary income for a typical workshop mechanic.
2. **It is easy.** Entry takes under 90 seconds, works in their language, works offline.
3. **It is reliable.** Payments arrive on time, every month.

**Most realistic scenario:** 200–300 mechanics in months 1–3, growing to 800–1200 by month 6, and potentially 2000+ by month 12 if payout reputation spreads.

---

## 6. Risk Register

| # | Risk | Category | Likelihood | Impact | Score | Mitigation |
|---|------|----------|------------|--------|-------|------------|
| 1 | Low mechanic adoption (<200 by month 3) | Operational | M | H | 9 | Target via distributor network; higher initial payouts; simplify onboarding |
| 2 | Data quality issues (fake entries, incomplete data) | Operational | M | H | 9 | Validation rules; GPS plausibility checks; admin rejection workflow |
| 3 | Solo developer burnout or bottleneck | Technical | M | H | 9 | Document code; use managed services; realistic timeline |
| 4 | Azure costs exceed budget | Financial | L | M | 4 | Budget alerts at 80% and 100%; right-size monthly |
| 5 | MSG91 SMS delivery failures | Operational | L | M | 4 | Retry logic; manual OTP bypass for support |
| 6 | Play Store rejection / policy violation | Technical | L | H | 6 | Pre-review against policies; test with internal track first |
| 7 | Mechanic payout disputes (rejected after approval) | Operational | M | M | 6 | Clear terms in app; rejection window (7 days); audit log |
| 8 | PostgreSQL performance degradation at scale | Technical | L | M | 4 | Indexes; connection pooling; monitor and scale up if needed |
| 9 | Difficulty recruiting 2000 mechanics | Operational | H | M | 8 | Set realistic target (500 is success); expand network via word-of-mouth |
| 10 | GPS spoofing (mechanic logs from home, not workshop) | Operational | M | M | 6 | Flag entries where GPS does not match registered workshop city |
| 11 | Device storage fills with photos before sync | Technical | M | L | 3 | Compress photos; auto-purge successfully synced photos from device |
| 12 | Expo SDK version updates cause breaking changes | Technical | M | L | 3 | Lock SDK version; test before upgrading |
| 13 | Admin does not process payouts on time | Operational | M | H | 9 | Automate payout calculation; send reminder; management oversight |
| 14 | Mechanics submit same entry via multiple devices | Technical | L | M | 4 | Duplicate detection on registration number + part type + 30 day window |
| 15 | i18n translation errors or missing translations | Operational | L | M | 4 | Core languages first (Hindi, English); add others in batches |

**Score key:** L=1, M=3, H=3. Score = Likelihood × Impact. (1–3=Low, 4–6=Medium, 7–9=High)

---

## 7. Feasibility Verdict

### Verdict: **Go with Conditions**

PartLog is technically feasible, strategically aligned with Vikas Group's business interests, and has a clear path to financial viability if mechanic adoption targets are met. The risk is not in building the platform but in achieving sufficient mechanic adoption to generate valuable data.

### Conditions for Go

1. **Mechanic adoption plan must be defined before launch.** The technology alone does not attract mechanics. A clear onboarding pipeline through the distributor network or workshop visits is essential.
2. **First 3 months are a pilot.** Target 50–200 mechanics. Validate the payout model, data quality, and operational workflow before scaling.
3. **Admin availability must be committed.** At least one Vikas Group staff member must be allocated to review entries and process payouts — 2–4 hours per day.
4. **Budget commitment for 12 months.** The programme requires sustained investment. The data becomes valuable after 6+ months of collection. Stopping early wastes the investment.
5. **Mechanic payout fund must be ring-fenced.** Late or missed payments will destroy trust and the mechanic network irreparably.

---

## 8. Recommendations

1. **Start small, prove the model.** Build for 50 mechanics first. Validate the full cycle: registration → entry logging → admin review → payout → mechanic satisfaction. Then scale.
2. **Prioritise onboarding over features.** The most important feature is not the fancy dashboard — it is a fast, working, offline-capable entry form that pays mechanics reliably.
3. **Invest in the mechanic relationship.** The app alone is not enough. Distributor visits, WhatsApp groups, phone support — the human layer is critical for adoption.
4. **Plan for data quality from day one.** Validation rules, GPS checks, odometer sanity checks, duplicate detection. Poor data is worse than no data.
5. **Document everything.** With a solo developer, knowledge loss is a real risk. Document architecture, deployment, and operations processes.
6. **Monitor costs and usage monthly.** Keep Azure costs predictable by setting budgets and reviewing resource utilisation.
7. **Design for the future, but build for today.** The architecture should allow adding AI/ML later, but do not build AI now. Do not build features no one will use in the first 6 months.
