# Business Requirements Document (BRD) — PartLog

**Version:** 1.0  
**Date:** July 2026  
**Status:** Draft

---

## 1. Executive Summary

Vikas Group is an Indian automotive parts manufacturer supplying AC condensers (Maruti Suzuki only) and AC compressors (all OEMs) to the automotive aftermarket. The company currently operates without systematic field failure intelligence. Supply decisions are driven by historical purchase orders, informal distributor feedback, and sales team intuition. This reactive approach causes stockouts at high-demand locations, overstocking at low-demand ones, and missed opportunities to demonstrate data-backed thought leadership to OEMs.

PartLog is a field data collection platform that addresses this gap. It consists of an Android mobile application used by independent mechanics to log every AC condenser and compressor replacement they perform, and a web portal where company administrators view aggregated failure patterns, manage mechanics, and process payouts.

Over a 6–8 month data collection phase, PartLog will build a proprietary dataset covering failure frequencies by vehicle model, geographic region, failure cause, and season. This dataset will enable Vikas Group to transition from reactive to data-driven supply planning and will serve as the foundation for future OEM engagement and predictive analytics.

Mechanics are incentivised to participate through monthly UPI payouts per approved entry, with rates configured by the company administrator. The platform supports 10 Indian languages and works fully offline to accommodate variable internet connectivity in Tier 2 and 3 cities.

---

## 2. Business Objectives

| Objective | Measure | Target | Timeline |
|-----------|---------|--------|----------|
| Improve supply planning accuracy | Reduction in stockout incidents at top-10 locations | 30% reduction vs baseline | 6 months post-launch |
| Build mechanic network | Number of active mechanics registered | 500–2000 | Within 6 months of launch |
| Collect comprehensive failure data | Total entries logged | 18,000–120,000 | Over 6–8 month collection phase |
| Achieve national coverage | States with ≥100 entries | 15+ states | Within 6 months of launch |
| Establish reliable payout process | Payout turnaround time from month-end | <48 hours | Within 3 payout cycles |
| Enable OEM engagement | Data quality (entries with complete mandatory fields) | ≥90% | Ongoing |

---

## 3. Current State Analysis

### How Vikas Group Operates Today

- Inventory planning is based on previous-year sales data, distributor purchase orders, and informal market feedback from the sales team.
- There is no systematic collection of field failure data. When a mechanic replaces a failed condenser or compressor, that information stays in the workshop — it never reaches Vikas Group.
- The company has no visibility into which vehicle models fail most frequently, at what age or odometer reading, or in which geographic regions.
- Failure cause information (stone impact, corrosion, manufacturing defect, etc.) is anecdotal at best. No structured data exists.
- Seasonal demand patterns are inferred from sales data, not from actual failure incidence.
- OEMs (Maruti Suzuki, Hyundai, Tata, etc.) do not share their warranty or service data with parts suppliers.

### Gaps Identified

| Gap | Impact |
|-----|--------|
| No field failure data | Cannot predict demand by model or region |
| No mechanic network | No direct channel to end-users of company parts |
| No failure cause data | Cannot identify quality trends or design improvements |
| No geographic demand visibility | Stockouts in high-demand regions, overstocking elsewhere |
| Seasonal patterns inferred, not measured | Inventory misalignment during peak seasons |

---

## 4. Future State Vision

After 6–8 months of PartLog operations:

- Vikas Group has a proprietary dataset of 18,000–120,000 failure records from 500–2000 mechanics across 20+ Indian states.
- Supply planners can query failure frequency by vehicle model, region, and season to make monthly inventory decisions.
- The company can identify which failure causes dominate in which regions (e.g., corrosion in coastal areas, stone impact on highways).
- Monthly payout processing is a routine, reliable operation completed within 48 hours of month-end.
- The mechanic network is an ongoing asset — continuously generating field intelligence at incremental cost.
- Vikas Group can approach OEMs with data-backed insights about their vehicle failure patterns, positioning the company as a value-added partner rather than a commodity supplier.
- The dataset serves as the foundation for future predictive models, demand forecasting, and automated supply optimisation.

---

## 5. Stakeholder Profiles

### Mechanic (Mobile App User)

- **Who:** Independent automotive AC mechanic working in a workshop in a Tier 2 or 3 Indian city
- **Age:** 22–45 years
- **Education:** 10th pass to diploma; comfortable with basic smartphone apps but not complex software
- **Devices:** Android phone, typically ₹8,000–₹15,000 range, Android 8–12, 3–4GB RAM
- **Motivation:** Supplementary income via UPI payouts per logged entry
- **Pain points:** Wants the app to be fast, works in all network conditions, uses their language, does not waste time
- **Goals:** Earn ₹2,000–₹5,000 per month reliably through entry logging

### Company Admin (Web Portal User)

- **Who:** Supply chain manager or operations analyst at Vikas Group
- **Age:** 28–50 years
- **Education:** Graduate; comfortable with web applications and data analysis
- **Motivation:** Improve supply planning accuracy, reduce stockouts, generate reports for management
- **Pain points:** Currently working with incomplete data; wants a single source of truth; needs exports for reporting
- **Goals:** See real-time failure trends, process payouts efficiently, generate monthly reports

### Senior Management (Web Portal Viewer)

- **Who:** Director or business head at Vikas Group
- **Interest:** High-level trends, regional breakdowns, ROI of the data collection programme
- **Usage:** Dashboard review 1–2 times per week

---

## 6. Business Requirements

| ID | Requirement | Priority |
|----|-------------|----------|
| BR-01 | Mechanics must be able to self-register using their mobile number via SMS OTP | Must |
| BR-02 | Mechanics must be able to log a condenser failure with vehicle model, variant, fuel type, year, failure cause, severity, and odometer reading | Must |
| BR-03 | Mechanics must be able to log a compressor failure with OEM (free text), model, variant, fuel type, year, failure cause, severity, and odometer reading | Must |
| BR-04 | Mechanics must be able to attach up to 3 photos of the failed part per entry | Must |
| BR-05 | Mechanics must be able to record a voice note (up to 60 seconds) per entry | Should |
| BR-06 | Mechanics must be able to enter a text note (up to 200 characters) per entry | Could |
| BR-07 | The app must capture GPS coordinates automatically when logging an entry | Must |
| BR-08 | The app must work fully offline — entries must save locally and sync when internet is available | Must |
| BR-09 | The app must support 10 Indian languages: English, Hindi, Tamil, Telugu, Malayalam, Kannada, Marathi, Gujarati, Bengali, Punjabi | Must |
| BR-10 | The app must detect potential duplicate entries (same registration number + same part type within 30 days) and warn the mechanic | Should |
| BR-11 | Mechanics must be able to view their earnings for the current month (entries logged, approved, estimated payout) | Must |
| BR-12 | Mechanics must be able to enter their UPI ID when requesting payout | Must |
| BR-13 | Mechanics must receive a push notification when their payout is marked as paid | Should |
| BR-14 | Admins must be able to log in to the web portal using email and password | Must |
| BR-15 | Admins must see a dashboard with total mechanics, total entries, entries this month, pending review count, and payout due | Must |
| BR-16 | Admins must be able to view all entries in a paginated, filterable, sortable table | Must |
| BR-17 | Admins must be able to view entry details including photos and audio notes | Must |
| BR-18 | Admins must be able to reject entries (entries are auto-approved on sync by default) | Must |
| BR-19 | Admins must be able to reject entries in bulk | Could |
| BR-20 | Admins must be able to view a list of all mechanics with account status | Must |
| BR-21 | Admins must be able to deactivate or reactivate mechanic accounts | Must |
| BR-22 | Admins must be able to configure payout rates separately for condenser and compressor entries | Must |
| BR-23 | Admins must be able to view monthly payout summaries per mechanic | Must |
| BR-24 | Admins must be able to mark payouts as paid with a UPI transaction reference number | Must |
| BR-25 | Admins must be able to view charts showing entries by model, state, failure cause, and daily volume (last 30 days) | Must |
| BR-26 | Admins must be able to export entries data and payout data as CSV and Excel | Must |
| BR-27 | The system must record an audit log of all admin actions (approve, reject, deactivate, payout config changes) | Must |

---

## 7. User Stories

### Mechanic Stories

1. As a mechanic, I want to register with my mobile number using OTP so that I can start logging entries quickly without paperwork.
2. As a mechanic, I want to choose my language during onboarding so that I can use the app comfortably.
3. As a mechanic, I want to enter my workshop name and city so that the company knows where I work.
4. As a mechanic, I want to select the part type (condenser or compressor) when starting a new entry so that the form is relevant.
5. As a mechanic logging a condenser failure, I want to pick the Maruti Suzuki model from a chip grid so that I do not have to type it.
6. As a mechanic logging a compressor failure, I want to type the OEM name so that I can log failures for any vehicle make.
7. As a mechanic, I want to select the failure cause from a predefined list so that the data is structured.
8. As a mechanic, I want to rate the severity (Minor/Major/Complete failure) so that the company can prioritise serious cases.
9. As a mechanic, I want to enter the odometer reading so that the company knows the vehicle age at failure.
10. As a mechanic, I want to take photos of the damaged part using the app camera so that the company can verify the failure.
11. As a mechanic, I want to record a voice note explaining the failure so that I can provide details without typing.
12. As a mechanic, I want the app to work even when there is no internet so that I can log entries anywhere.
13. As a mechanic, I want my entries to sync automatically when the internet is available so that I do not have to remember to upload.
14. As a mechanic, I want to see a warning if I am logging a duplicate entry so that I do not waste time.
15. As a mechanic, I want to see my earnings for this month so that I know how much I will be paid.
16. As a mechanic, I want to enter my UPI ID when I request payout so that I receive payment directly.
17. As a mechanic, I want to receive a notification when my payout is sent so that I do not have to keep checking.
18. As a mechanic, I want to change my language preference in settings so that I can switch if needed.
19. As a mechanic, I want to export my own entries as a CSV file so that I can keep my own records.

### Admin Stories

20. As an admin, I want to log in with my email and password so that I can access the portal securely.
21. As an admin, I want to see a dashboard with key numbers (mechanics, entries, payout due) so that I know the state of the programme at a glance.
22. As an admin, I want to view all entries in a table and filter by model, state, date, or status so that I can find specific records quickly.
23. As an admin, I want to click on an entry to see its full details including photos and voice note so that I can review it thoroughly.
24. As an admin, I want to reject entries that appear suspicious or invalid so that the data quality is maintained.
25. As an admin, I want to see charts showing entries by model, state, and failure cause so that I can spot trends.
26. As an admin, I want to manage mechanic accounts and deactivate inactive or problematic ones so that the system stays clean.
27. As an admin, I want to set separate payout rates for condenser and compressor entries so that different effort levels are compensated appropriately.
28. As an admin, I want to see a monthly payout summary showing each mechanic's approved entries and amount due so that I can process payouts.
29. As an admin, I want to mark payouts as paid with a UPI reference number so that there is a clear record.
30. As an admin, I want to export entries and payout data as CSV or Excel so that I can share it with management.

---

## 8. Success Metrics

| Metric | Target | Measurement Method |
|--------|--------|-------------------|
| Mechanic adoption rate | 500–2000 active (logged in last 30 days) at 6 months | Database query |
| Entry volume | 100–500 entries/day at 12 months | Database query |
| Data completeness | ≥90% of entries have all mandatory fields populated | Automated check on entry submission |
| Data accuracy (approve rate) | ≥95% of entries approved (≤5% rejected) | Admin action log |
| Geographic coverage | Entries from ≥20 states | Database query |
| Mechanic retention | ≥70% of mechanics who registered in month 1 are still active in month 6 | Database query |
| Payout turnaround | <48 hours from month-end to payout processing | Admin log |
| Entry per mechanic per month | ≥10 entries/month for active mechanics | Database query |
| Sync success rate | ≥99% of offline entries sync within 24 hours | Sync log |
| App crash-free rate | ≥99.5% | Expo / Play Console |

---

## 9. Assumptions and Dependencies

### Assumptions

- Mechanics have Android smartphones with internet connectivity at least once daily
- Mechanics will participate given adequate UPI payout incentives
- MSG91 SMS OTP service will have ≥95% delivery rate
- Google Play Store will approve internal testing track listing within 1 week
- Azure services in Central India region will provide acceptable latency
- A single developer using AI tools can build the platform within 14–18 weeks
- Vikas Group will provide at least one admin user for testing and operations

### Dependencies

- MSG91 account must be active with sufficient SMS credits
- Azure subscription must be active with budget allocated
- Google Play Console account must be active (one-time \$25 registration)
- Expo EAS account (free tier sufficient for initial phase)
- Vikas Group must provide mechanic payout funds monthly

---

## 10. Out of Scope (This Version)

- AI/ML models or predictive analytics
- Demand forecasting or inventory optimisation
- Distributor or dealer portal
- Supervisor role with regional oversight
- Automated UPI payment integration
- iOS mobile application
- Real-time dashboard or live map view
- Multi-company support (only Vikas Group)
- Inventory or stock management
- CRM features (mechanic messaging, support tickets)
- Public REST API for third-party integration
- WhatsApp or email notifications
- Mechanic gamification, leaderboards, or badges
- Dark mode for mobile app
- Web portal dark mode

---

## 11. Glossary

| Term | Definition |
|------|------------|
| **AC Condenser** | A radiator-like component in a vehicle's air conditioning system. Condenses refrigerant gas to liquid form. |
| **AC Compressor** | The pump that circulates refrigerant through the AC system. Driven by the engine belt. |
| **OEM** | Original Equipment Manufacturer — the company that manufactures the vehicle (e.g., Maruti Suzuki, Hyundai, Tata). |
| **OEM (free text, compressor)** | For compressor entries, the mechanic types the vehicle make manually (e.g., Tata, Mahindra). |
| **Parc** | The total number of vehicles of a particular model registered and on the road in a given region. |
| **UPI** | Unified Payments Interface — India's real-time payment system for mobile money transfers. |
| **UPI ID** | A virtual payment address linked to a bank account (e.g., mechanic@upi). |
| **MSG91** | An Indian SMS gateway service used for sending OTP messages. |
| **Expo** | A framework and platform for building React Native applications. |
| **Condenser (Maruti only)** | PartLog only collects condenser data for Maruti Suzuki vehicles (company's product focus). |
| **Compressor (all OEMs)** | PartLog collects compressor data for all vehicle manufacturers. |
| **Offline-first** | The app saves data locally on the device and syncs to the server when internet connectivity is available. |
| **SAS Token** | Shared Access Signature — a secure URL that provides time-limited access to Azure Blob Storage files. |
| **Auto-approve** | All entries are automatically marked as approved when synced. Admin can later reject suspicious entries. |
| **Payout rate** | The amount (in ₹) paid per approved entry. Separate rates for condenser and compressor, configurable by admin. |
