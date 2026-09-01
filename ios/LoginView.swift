import SwiftUI

struct LoginView: View {
    @Binding var currentScreen: AppScreen
    @Binding var language: AppLanguage
    @Binding var loggedInUser: MechanicProfile?
    
    @State private var mobileNumber = ""
    @State private var password = ""
    @State private var loading = false
    @State private var errorMessage = ""
    
    var body: some View {
        VStack(spacing: 0) {
            // Header language bar
            HStack {
                Button(action: {
                    currentScreen = .landing
                }) {
                    HStack(spacing: 5) {
                        Image(systemName: "chevron.left")
                        Text(language == .EN ? "Back" : "पीछे")
                    }
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(SakshamTheme.primaryRoyalBlue)
                }
                Spacer()
                LanguageToggleView(language: $language)
            }
            .padding(.horizontal, 20)
            .padding(.top, 10)
            
            ScrollView {
                VStack(spacing: 20) {
                    // Logo Image
                    Image(systemName: "bolt.shield.fill")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(height: 50)
                        .foregroundColor(SakshamTheme.primaryRoyalBlue)
                        .padding(.top, 20)
                    
                    VStack(alignment: .leading, spacing: 6) {
                        Text(language == .EN ? "Welcome Back!" : "स्वागत है!")
                            .font(.system(size: 24, weight: .bold))
                            .foregroundColor(SakshamTheme.primaryRoyalBlue)
                        
                        Text(language == .EN ? "Let's collect data, drive quality & earn more." : "आइए डेटा एकत्र करें, गुणवत्ता बढ़ाएं और अधिक कमाएं।")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(SakshamTheme.darkText)
                        
                        Text(language == .EN ? "Login to continue and submit accurate compressor & condenser data." : "कंटिन्यू करने और सटीक कंप्रेसर और कंडेनसर डेटा सबमिट करने के लिए लॉगिन करें।")
                            .font(.system(size: 13))
                            .foregroundColor(SakshamTheme.subtleText)
                            .lineSpacing(3)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.horizontal, 4)
                    
                    // Form fields
                    VStack(spacing: 14) {
                        // Mobile text field
                        VStack(alignment: .leading, spacing: 5) {
                            Text(language == .EN ? "Mobile Number" : "मोबाइल नंबर")
                                .font(.system(size: 13, weight: .semibold))
                                .foregroundColor(SakshamTheme.darkText)
                            
                            TextField(language == .EN ? "Enter 10 digit number" : "10 अंकों का नंबर दर्ज करें", text: $mobileNumber)
                                .keyboardType(.phonePad)
                                .padding()
                                .background(Color.white)
                                .cornerRadius(6)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 6)
                                        .stroke(SakshamTheme.borderBase, lineWidth: 1)
                                )
                        }
                        
                        // Password field
                        VStack(alignment: .leading, spacing: 5) {
                            Text(language == .EN ? "Password" : "पासवर्ड")
                                .font(.system(size: 13, weight: .semibold))
                                .foregroundColor(SakshamTheme.darkText)
                            
                            SecureField(language == .EN ? "Enter password" : "पासवर्ड दर्ज करें", text: $password)
                                .padding()
                                .background(Color.white)
                                .cornerRadius(6)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 6)
                                        .stroke(SakshamTheme.borderBase, lineWidth: 1)
                                )
                        }
                    }
                    
                    // Error message label
                    if !errorMessage.isEmpty {
                        Text(errorMessage)
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(.red)
                            .multilineTextAlignment(.center)
                            .padding(.vertical, 4)
                    }
                    
                    // Forgot Password option
                    HStack {
                        Spacer()
                        Button(action: {
                            errorMessage = language == .EN ? "OTP system is mock. Login using your password." : "ओटीपी सिस्टम मॉक है। अपने पासवर्ड का उपयोग करके लॉगिन करें।"
                        }) {
                            Text(language == .EN ? "Forgot Password?" : "पासवर्ड भूल गए?")
                                .font(.system(size: 13, weight: .semibold))
                                .foregroundColor(SakshamTheme.primaryRoyalBlue)
                        }
                    }
                    
                    // Login action buttons
                    VStack(spacing: 12) {
                        Button(action: {
                            performLogin()
                        }) {
                            HStack {
                                if loading {
                                    ProgressView()
                                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                        .scaleEffect(0.9)
                                } else {
                                    Text(language == .EN ? "LOGIN" : "लॉगिन")
                                        .font(.system(size: 15, weight: .bold))
                                        .foregroundColor(.white)
                                }
                            }
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                            .background(SakshamTheme.primaryRoyalBlue)
                            .cornerRadius(8)
                        }
                        .disabled(loading)
                        
                        Button(action: {
                            errorMessage = language == .EN ? "OTP features are mock-only. Login with password." : "ओटीपी फीचर्स केवल मॉक हैं। पासवर्ड से लॉगिन करें।"
                        }) {
                            Text(language == .EN ? "LOGIN WITH OTP" : "ओटीपी से लॉगिन करें")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(SakshamTheme.primaryRoyalBlue)
                                .frame(maxWidth: .infinity)
                                .frame(height: 50)
                                .background(Color.white)
                                .cornerRadius(8)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 8)
                                        .stroke(SakshamTheme.primaryRoyalBlue, lineWidth: 1.5)
                                )
                        }
                        .disabled(loading)
                    }
                    .padding(.top, 10)
                    
                    Spacer()
                    
                    // Signup redirect
                    Button(action: {
                        currentScreen = .register
                    }) {
                        Text(language == .EN ? "Don't have an account? Register Now" : "खाता नहीं है? अभी पंजीकरण करें")
                            .font(.system(size: 14, weight: .semibold))
                            .foregroundColor(SakshamTheme.primaryRoyalBlue)
                    }
                    .padding(.bottom, 20)
                }
                .padding(.horizontal, 24)
            }
        }
        .background(SakshamTheme.softBlueBackground.edgesIgnoringSafeArea(.all))
    }
    
    private func performLogin() {
        guard !mobileNumber.trimmingCharacters(in: .whitespaces).isEmpty,
              !password.isEmpty else {
            errorMessage = language == .EN ? "Please fill in all fields" : "कृपया सभी फ़ील्ड भरें"
            return
        }
        
        loading = true
        errorMessage = ""
        
        Task {
            do {
                let profile = try await NetworkManager.shared.login(payload: LoginPayload(id: mobileNumber.trimmingCharacters(in: .whitespaces), password: password))
                
                // Persist session locally
                UserDefaults.standard.set(profile.id, forKey: "mechanic_id")
                UserDefaults.standard.set(profile.name, forKey: "mechanic_name")
                UserDefaults.standard.set(profile.workshop, forKey: "mechanic_workshop")
                UserDefaults.standard.set(profile.points, forKey: "mechanic_points")
                
                DispatchQueue.main.async {
                    self.loggedInUser = profile
                    self.loading = false
                    self.currentScreen = .home
                }
            } catch {
                DispatchQueue.main.async {
                    self.errorMessage = "\(error.localizedDescription)"
                    self.loading = false
                }
            }
        }
    }
}
