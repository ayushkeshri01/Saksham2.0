import SwiftUI

enum AppScreen: String, Codable {
    case landing
    case login
    case register
    case home
}

@main
struct SakshamApp: App {
    @State private var currentScreen: AppScreen = .landing
    @State private var language: AppLanguage = .EN
    @State private var loggedInUser: MechanicProfile? = nil
    @State private var jobs: [JobEntry] = []
    
    // Logging flow state variables
    @State private var selectedComponentType: String = "condenser"
    @State private var showLoggingFlow: Bool = false
    
    init() {
        // Restore session on application launch if present
        if let id = UserDefaults.standard.string(forKey: "mechanic_id"),
           let name = UserDefaults.standard.string(forKey: "mechanic_name"),
           let workshop = UserDefaults.standard.string(forKey: "mechanic_workshop") {
            let points = UserDefaults.standard.integer(forKey: "mechanic_points")
            
            _loggedInUser = State(initialValue: MechanicProfile(id: id, name: name, workshop: workshop, mobile: id, points: points))
            _currentScreen = State(initialValue: .home)
        }
    }
    
    var body: some Scene {
        WindowGroup {
            ZStack {
                switch currentScreen {
                case .landing:
                    LandingView(currentScreen: $currentScreen, language: $language)
                case .login:
                    LoginView(currentScreen: $currentScreen, language: $language, loggedInUser: $loggedInUser)
                case .register:
                    SignUpView(currentScreen: $currentScreen, language: $language)
                case .home:
                    HomeView(
                        currentScreen: $currentScreen,
                        language: $language,
                        loggedInUser: $loggedInUser,
                        jobs: $jobs,
                        selectedComponentType: $selectedComponentType,
                        showLoggingFlow: $showLoggingFlow
                    )
                    .sheet(isPresented: $showLoggingFlow) {
                        LoggingFlowView(
                            componentType: selectedComponentType,
                            isPresented: $showLoggingFlow,
                            jobs: $jobs,
                            language: $language,
                            mechanicId: loggedInUser?.id ?? ""
                        )
                    }
                }
            }
            .animation(.default, value: currentScreen)
        }
    }
}
