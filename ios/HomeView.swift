import SwiftUI

struct HomeView: View {
    @Binding var currentScreen: AppScreen
    @Binding var language: AppLanguage
    @Binding var loggedInUser: MechanicProfile?
    @Binding var jobs: [JobEntry]
    
    // Logging flow trigger variables
    @Binding var selectedComponentType: String
    @Binding var showLoggingFlow: Bool
    
    @State private var selectedTab = "dashboard"
    
    var body: some View {
        VStack(spacing: 0) {
            // Custom Navigation Bar
            HStack(spacing: 8) {
                // Logo
                Image(systemName: "bolt.shield.fill")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 28, height: 28)
                    .foregroundColor(SakshamTheme.primaryRoyalBlue)
                
                Text("Saksham 2.0")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(SakshamTheme.primaryRoyalBlue)
                
                Spacer()
                
                LanguageToggleView(language: $language)
                    .scaleEffect(0.9)
                
                Button(action: {
                    logout()
                }) {
                    Image(systemName: "rectangle.portrait.and.arrow.right")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(SakshamTheme.primaryRoyalBlue)
                        .padding(.leading, 8)
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 10)
            .background(Color.white)
            .shadow(color: Color.black.opacity(0.04), radius: 3, x: 0, y: 2)
            
            // Tab View Content
            ZStack {
                switch selectedTab {
                case "dashboard":
                    DashboardTab(
                        name: loggedInUser?.name ?? "Abhishek",
                        language: language,
                        totalJobsCount: jobs.count,
                        onLogCondenser: {
                            selectedComponentType = "condenser"
                            showLoggingFlow = true
                        },
                        onLogCompressor: {
                            selectedComponentType = "compressor"
                            showLoggingFlow = true
                        }
                    )
                case "submissions":
                    SubmissionsTab(jobs: jobs, language: language)
                case "reports":
                    ReportsTab(jobs: jobs, language: language)
                case "profile":
                    ProfileTab(profile: loggedInUser, onLogout: logout, language: language)
                default:
                    Color.clear
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(SakshamTheme.softBlueBackground)
            
            // Bottom Tab Bar
            HStack {
                TabBarItem(iconName: "square.grid.2x2.fill", label: language == .EN ? "Dashboard" : "डैशबोर्ड", isSelected: selectedTab == "dashboard") {
                    selectedTab = "dashboard"
                }
                
                TabBarItem(iconName: "list.bullet.rectangle.fill", label: language == .EN ? "Submissions" : "सबमिशन", isSelected: selectedTab == "submissions") {
                    selectedTab = "submissions"
                }
                
                // Floating center new entry action button
                Button(action: {
                    selectedComponentType = "condenser"
                    showLoggingFlow = true
                }) {
                    VStack(spacing: 4) {
                        Image(systemName: "plus.circle.fill")
                            .resizable()
                            .frame(width: 32, height: 32)
                            .foregroundColor(SakshamTheme.accentCyan)
                        Text(language == .EN ? "New Entry" : "नई प्रविष्टि")
                            .font(.system(size: 9, weight: .bold))
                            .foregroundColor(SakshamTheme.accentCyan)
                    }
                }
                .frame(maxWidth: .infinity)
                .offset(y: -4)
                
                TabBarItem(iconName: "chart.bar.xaxis", label: language == .EN ? "Reports" : "रिपोर्ट", isSelected: selectedTab == "reports") {
                    selectedTab = "reports"
                }
                
                TabBarItem(iconName: "person.crop.circle.fill", label: language == .EN ? "Profile" : "प्रोफ़ाइल", isSelected: selectedTab == "profile") {
                    selectedTab = "profile"
                }
            }
            .padding(.top, 8)
            .padding(.bottom, 12)
            .background(Color.white)
            .shadow(color: Color.black.opacity(0.06), radius: 5, x: 0, y: -2)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(SakshamTheme.softBlueBackground.edgesIgnoringSafeArea(.all))
    }
    
    private func logout() {
        // Clear preferences
        UserDefaults.standard.removeObject(forKey: "mechanic_id")
        UserDefaults.standard.removeObject(forKey: "mechanic_name")
        UserDefaults.standard.removeObject(forKey: "mechanic_workshop")
        UserDefaults.standard.removeObject(forKey: "mechanic_points")
        
        loggedInUser = nil
        currentScreen = .landing
    }
}

// Custom Tab Bar Item View
struct TabBarItem: View {
    let iconName: String
    let label: String
    let isSelected: Bool
    let action: () -> Unit
    
    typealias Unit = () -> Void
    
    var body: some View {
        Button(action: action) {
            VStack(spacing: 4) {
                Image(systemName: iconName)
                    .font(.system(size: 18))
                    .foregroundColor(isSelected ? SakshamTheme.primaryRoyalBlue : Color.gray)
                Text(label)
                    .font(.system(size: 9, weight: isSelected ? .bold : .medium))
                    .foregroundColor(isSelected ? SakshamTheme.primaryRoyalBlue : Color.gray)
            }
        }
        .frame(maxWidth: .infinity)
    }
}

// DASHBOARD TAB VIEW
struct DashboardTab: View {
    let name: String
    let language: AppLanguage
    let totalJobsCount: Int
    let onLogCondenser: () -> Void
    let onLogCompressor: () -> Void
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                // Greeting Card
                VStack(alignment: .leading, spacing: 4) {
                    Text(language == .EN ? "Hello, \(name)!" : "नमस्ते, \(name)!")
                        .font(.system(size: 20, weight: .bold))
                        .foregroundColor(SakshamTheme.primaryRoyalBlue)
                    Text(language == .EN ? "Let's collect accurate data and drive quality together." : "आइए मिलकर सटीक डेटा एकत्र करें और गुणवत्ता बढ़ाएं।")
                        .font(.system(size: 13))
                        .foregroundColor(SakshamTheme.subtleText)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding()
                .background(Color.white)
                .cornerRadius(12)
                
                // Stats Card
                HStack {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(language == .EN ? "Total Submissions" : "कुल प्रविष्टियां")
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(Color.white.opacity(0.85))
                        Text("\(120 + totalJobsCount)") // Mock baseline + local jobs
                            .font(.system(size: 32, weight: .bold))
                            .foregroundColor(.white)
                    }
                    Spacer()
                    Text(language == .EN ? "This Month" : "इस महीने")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(.white)
                        .padding(.horizontal, 10)
                        .padding(.vertical, 5)
                        .background(Color.white.opacity(0.2))
                        .cornerRadius(6)
                }
                .padding()
                .background(SakshamTheme.primaryRoyalBlue)
                .cornerRadius(12)
                
                // Selection Slogans
                VStack(alignment: .leading, spacing: 3) {
                    Text(language == .EN ? "What would you like to log today?" : "आज आप क्या लॉग करना चाहेंगे?")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(SakshamTheme.darkText)
                    Text(language == .EN ? "Choose the component you want to submit data for" : "वह घटक चुनें जिसके लिए आप डेटा जमा करना चाहते हैं")
                        .font(.system(size: 12))
                        .foregroundColor(SakshamTheme.subtleText)
                }
                
                // CONDENSER CARD SELECTOR
                ComponentOptionCard(
                    title: language == .EN ? "CONDENSER" : "कंडेनसर",
                    description: language == .EN ? "Log condenser details, issues and related information." : "कंडेनसर विवरण, मुद्दों और संबंधित जानकारी को लॉग करें।",
                    buttonText: language == .EN ? "LOG CONDENSER" : "कंडेनसर लॉग करें",
                    onClick: onLogCondenser,
                    color: SakshamTheme.primaryRoyalBlue
                )
                
                // COMPRESSOR CARD SELECTOR
                ComponentOptionCard(
                    title: language == .EN ? "COMPRESSOR" : "कंप्रेसर",
                    description: language == .EN ? "Log compressor details, issues and related information." : "कंप्रेसर विवरण, मुद्दों और संबंधित जानकारी को लॉग करें।",
                    buttonText: language == .EN ? "LOG COMPRESSOR" : "कंप्रेसर लॉग करें",
                    onClick: onLogCompressor,
                    color: SakshamTheme.accentCyan
                )
                
                // Incentives promotion
                HStack(spacing: 8) {
                    Image(systemName: "gift.fill")
                        .foregroundColor(SakshamTheme.primaryRoyalBlue)
                    Text(language == .EN ? "Use Pranav Products. Earn extra incentives on every submission." : "प्रणव उत्पादों का उपयोग करें। प्रत्येक सबमिशन पर अतिरिक्त इंसेंटिव अर्जित करें।")
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundColor(SakshamTheme.primaryRoyalBlue)
                        .lineLimit(2)
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 10)
                .padding(.horizontal, 8)
                .background(Color(hex: "#EAF0F8"))
                .cornerRadius(8)
            }
            .padding(16)
        }
    }
}

struct ComponentOptionCard: View {
    let title: String
    let description: String
    let buttonText: String
    let onClick: () -> Void
    let color: Color
    
    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            HStack(spacing: 8) {
                Circle()
                    .fill(color)
                    .frame(width: 8, height: 8)
                Text(title)
                    .font(.system(size: 15, weight: .bold))
                    .foregroundColor(color)
            }
            
            Text(description)
                .font(.system(size: 12))
                .foregroundColor(SakshamTheme.subtleText)
                .lineSpacing(2)
            
            Button(action: onClick) {
                Text(buttonText)
                    .font(.system(size: 13, weight: .bold))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 42)
                    .background(color)
                    .cornerRadius(6)
            }
        }
        .padding()
        .background(Color.white)
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(SakshamTheme.borderBase, lineWidth: 1)
        )
    }
}

// SUBMISSIONS LIST TAB VIEW
struct SubmissionsTab: View {
    let jobs: [JobEntry]
    let language: AppLanguage
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(Loc.get("recent_entries", language))
                .font(.system(size: 18, weight: .bold))
                .foregroundColor(SakshamTheme.primaryRoyalBlue)
                .padding([.top, .horizontal], 16)
                .padding(.bottom, 8)
            
            if jobs.isEmpty {
                VStack {
                    Spacer()
                    Image(systemName: "tray.fill")
                        .font(.system(size: 40))
                        .foregroundColor(Color.gray.opacity(0.5))
                        .padding(.bottom, 8)
                    Text(language == .EN ? "No jobs logged yet" : "अभी तक कोई काम लॉग नहीं किया गया")
                        .font(.system(size: 14))
                        .foregroundColor(.gray)
                    Spacer()
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .padding(20)
            } else {
                List(jobs) { job in
                    JobRowItem(job: job, language: language)
                        .listRowInsets(EdgeInsets(top: 6, leading: 16, bottom: 6, trailing: 16))
                        .listRowBackground(Color.clear)
                        .listRowSeparator(.hidden)
                }
                .listStyle(PlainListStyle())
            }
        }
    }
}

struct JobRowItem: View {
    let job: JobEntry
    let language: AppLanguage
    
    var body: some View {
        let isCompressor = job.componentType == "compressor"
        let badgeColor = isCompressor ? SakshamTheme.accentCyan : SakshamTheme.primaryRoyalBlue
        
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text("\(job.make) \(job.model)")
                    .font(.system(size: 15, weight: .bold))
                    .foregroundColor(SakshamTheme.darkText)
                Spacer()
                Text(job.componentType.uppercased())
                    .font(.system(size: 9, weight: .bold))
                    .foregroundColor(badgeColor)
                    .padding(.horizontal, 6)
                    .padding(.vertical, 2)
                    .background(badgeColor.opacity(0.12))
                    .cornerRadius(4)
            }
            
            Text("\(Loc.get("failure_cause_title", language)): \(job.failureCause) (\(job.severity))")
                .font(.system(size: 12))
                .foregroundColor(SakshamTheme.subtleText)
            
            HStack {
                Text(formatTimestamp(job.timestamp))
                    .font(.system(size: 10))
                    .foregroundColor(.gray)
                Spacer()
                
                let isSynced = job.syncStatus == "SYNCED"
                Text(isSynced ? Loc.get("synced", language) : Loc.get("queued", language))
                    .font(.system(size: 10, weight: .bold))
                    .foregroundColor(isSynced ? Color.green : Color.orange)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 2)
                    .background(isSynced ? Color.green.opacity(0.1) : Color.orange.opacity(0.1))
                    .cornerRadius(4)
            }
        }
        .padding()
        .background(Color.white)
        .cornerRadius(8)
        .overlay(
            RoundedRectangle(cornerRadius: 8)
                .stroke(SakshamTheme.borderBase, lineWidth: 1)
        )
    }
    
    private func formatTimestamp(_ ts: Int64) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "dd MMM yyyy, hh:mm a"
        return formatter.string(from: Date(timeIntervalSince1970: Double(ts) / 1000.0))
    }
}

// REPORTS TAB VIEW
struct ReportsTab: View {
    let jobs: [JobEntry]
    let language: AppLanguage
    
    var body: some View {
        let condensers = jobs.count { $0.componentType == "condenser" }
        let compressors = jobs.count { $0.componentType == "compressor" }
        let totalPoints = jobs.count * 10
        
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text(language == .EN ? "Activity Report" : "गतिविधि रिपोर्ट")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(SakshamTheme.primaryRoyalBlue)
                
                VStack(spacing: 14) {
                    Text(language == .EN ? "Logged Component Statistics" : "लॉग किए गए घटक सांख्यिकी")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(SakshamTheme.darkText)
                        .frame(maxWidth: .infinity, alignment: .leading)
                    
                    ReportMetricRow(label: language == .EN ? "Condensers Logged" : "कंडेंसर लॉग किए गए", value: "\(condensers)")
                    Divider()
                    ReportMetricRow(label: language == .EN ? "Compressors Logged" : "कंप्रेसर लॉग किए गए", value: "\(compressors)")
                    Divider()
                    ReportMetricRow(label: language == .EN ? "Total Incentives Earned" : "अर्जित कुल इंसेंटिव", value: "₹\(totalPoints * 20)", valColor: Color.green)
                }
                .padding()
                .background(Color.white)
                .cornerRadius(12)
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .stroke(SakshamTheme.borderBase, lineWidth: 1)
                )
            }
            .padding(16)
        }
    }
}

struct ReportMetricRow: View {
    let label: String
    let value: String
    var valColor: Color = SakshamTheme.darkText
    
    var body: some View {
        HStack {
            Text(label)
                .font(.system(size: 13))
                .foregroundColor(SakshamTheme.subtleText)
            Spacer()
            Text(value)
                .font(.system(size: 13, weight: .bold))
                .foregroundColor(valColor)
        }
    }
}

// PROFILE TAB VIEW
struct ProfileTab: View {
    let profile: MechanicProfile?
    let onLogout: () -> Void
    let language: AppLanguage
    
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // Avatar Card
                VStack(spacing: 8) {
                    Image(systemName: "person.crop.circle.fill")
                        .resizable()
                        .frame(width: 80, height: 80)
                        .foregroundColor(SakshamTheme.primaryRoyalBlue.opacity(0.15))
                        .background(Color.white)
                        .clipShape(Circle())
                    
                    Text(profile?.name ?? "Abhishek")
                        .font(.system(size: 20, weight: .bold))
                        .foregroundColor(SakshamTheme.darkText)
                }
                .padding(.top, 20)
                
                // Profile attributes list
                VStack(spacing: 12) {
                    ProfileAttributeRow(label: language == .EN ? "Mobile Number" : "मोबाइल नंबर", value: profile?.mobile ?? "")
                    Divider()
                    ProfileAttributeRow(label: language == .EN ? "Workshop" : "वर्कशॉप विवरण", value: profile?.workshop ?? "")
                    Divider()
                    ProfileAttributeRow(label: language == .EN ? "Total Points" : "कुल पॉइंट", value: "\(profile?.points ?? 0) pts")
                }
                .padding()
                .background(Color.white)
                .cornerRadius(12)
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .stroke(SakshamTheme.borderBase, lineWidth: 1)
                )
                
                Button(action: onLogout) {
                    Text(language == .EN ? "Logout Profile" : "लॉगआउट प्रोफ़ाइल")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 48)
                        .background(Color.red.opacity(0.85))
                        .cornerRadius(8)
                }
                .padding(.top, 16)
            }
            .padding(16)
        }
    }
}

struct ProfileAttributeRow: View {
    let label: String
    let value: String
    
    var body: some View {
        HStack {
            Text(label)
                .font(.system(size: 13))
                .foregroundColor(SakshamTheme.subtleText)
            Spacer()
            Text(value)
                .font(.system(size: 13, weight: .semibold))
                .foregroundColor(SakshamTheme.darkText)
        }
    }
}
