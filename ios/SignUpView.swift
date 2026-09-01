import SwiftUI

struct SignUpView: View {
    @Binding var currentScreen: AppScreen
    @Binding var language: AppLanguage
    
    @State private var name = ""
    @State private var mobileNumber = ""
    @State private var email = ""
    @State private var shopName = ""
    @State private var city = ""
    @State private var password = ""
    @State private var confirmPassword = ""
    @State private var agreeToTerms = false
    
    @State private var loading = false
    @State private var errorMessage = ""
    @State private var successAlert = false
    
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
                VStack(spacing: 16) {
                    // Logo Image
                    Image(systemName: "bolt.shield.fill")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(height: 50)
                        .foregroundColor(SakshamTheme.primaryRoyalBlue)
                    
                    VStack(alignment: .leading, spacing: 6) {
                        Text(language == .EN ? "Create Account" : "खाता बनाएं")
                            .font(.system(size: 24, weight: .bold))
                            .foregroundColor(SakshamTheme.primaryRoyalBlue)
                        
                        Text(language == .EN ? "Register to start submitting data and earn exciting incentives." : "डेटा सबमिट करना शुरू करने और रोमांचक प्रोत्साहन अर्जित करने के लिए पंजीकरण करें।")
                            .font(.system(size: 13))
                            .foregroundColor(SakshamTheme.subtleText)
                            .lineSpacing(3)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.horizontal, 4)
                    
                    // Form fields
                    VStack(spacing: 12) {
                        // Full Name
                        InputField(label: language == .EN ? "Full Name *" : "पूरा नाम *", placeholder: language == .EN ? "Enter your name" : "अपना नाम दर्ज करें", text: $name)
                        
                        // Mobile Number
                        InputField(label: language == .EN ? "Mobile Number *" : "मोबाइल नंबर *", placeholder: language == .EN ? "Enter 10 digit number" : "10 अंकों का नंबर दर्ज करें", text: $mobileNumber, keyboard: .phonePad)
                        
                        // Email (Optional)
                        InputField(label: language == .EN ? "Email Address (Optional)" : "ईमेल पता (वैकल्पिक)", placeholder: language == .EN ? "Enter email address" : "ईमेल दर्ज करें", text: $email, keyboard: .emailAddress)
                        
                        // Shop Name
                        InputField(label: language == .EN ? "Shop / Company Name *" : "दुकान / कंपनी का नाम *", placeholder: language == .EN ? "Enter shop or company name" : "अपनी दुकान का नाम दर्ज करें", text: $shopName)
                        
                        // City
                        InputField(label: language == .EN ? "City *" : "शहर *", placeholder: language == .EN ? "Enter your city" : "अपना शहर दर्ज करें", text: $city)
                        
                        // Create Password
                        VStack(alignment: .leading, spacing: 5) {
                            Text(language == .EN ? "Create Password *" : "पासवर्ड बनाएं *")
                                .font(.system(size: 13, weight: .semibold))
                                .foregroundColor(SakshamTheme.darkText)
                            SecureField(language == .EN ? "Min. 6 characters" : "कम से कम 6 अंक", text: $password)
                                .padding()
                                .background(Color.white)
                                .cornerRadius(6)
                                .overlay(RoundedRectangle(cornerRadius: 6).stroke(SakshamTheme.borderBase, lineWidth: 1))
                        }
                        
                        // Confirm Password
                        VStack(alignment: .leading, spacing: 5) {
                            Text(language == .EN ? "Confirm Password *" : "पासवर्ड की पुष्टि करें *")
                                .font(.system(size: 13, weight: .semibold))
                                .foregroundColor(SakshamTheme.darkText)
                            SecureField(language == .EN ? "Re-enter password" : "पासवर्ड दोबारा दर्ज करें", text: $confirmPassword)
                                .padding()
                                .background(Color.white)
                                .cornerRadius(6)
                                .overlay(RoundedRectangle(cornerRadius: 6).stroke(SakshamTheme.borderBase, lineWidth: 1))
                        }
                    }
                    
                    // Terms toggle
                    Toggle(isOn: $agreeToTerms) {
                        Text(language == .EN ? "I agree to the Terms & Conditions and Privacy Policy" : "मैं नियम व शर्तों और गोपनीयता नीति से सहमत हूँ")
                            .font(.system(size: 12))
                            .foregroundColor(SakshamTheme.darkText)
                    }
                    .toggleStyle(CheckboxStyle())
                    .frame(maxWidth: .infinity, alignment: .leading)
                    
                    // Error view
                    if !errorMessage.isEmpty {
                        Text(errorMessage)
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(.red)
                            .multilineTextAlignment(.center)
                            .padding(.vertical, 4)
                    }
                    
                    // Register action button
                    Button(action: {
                        performRegister()
                    }) {
                        HStack {
                            if loading {
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                    .scaleEffect(0.9)
                            } else {
                                Text(language == .EN ? "REGISTER" : "पंजीकरण करें")
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
                    .padding(.top, 10)
                    
                    // Signin redirection
                    Button(action: {
                        currentScreen = .login
                    }) {
                        Text(language == .EN ? "Already have an account? Login Now" : "पहले से ही एक खाता है? अभी लॉगिन करें")
                            .font(.system(size: 14, weight: .semibold))
                            .foregroundColor(SakshamTheme.primaryRoyalBlue)
                    }
                    .padding(.vertical, 20)
                }
                .padding(.horizontal, 24)
            }
        }
        .background(SakshamTheme.softBlueBackground.edgesIgnoringSafeArea(.all))
        .alert(isPresented: $successAlert) {
            Alert(
                title: Text(language == .EN ? "Registration Successful" : "पंजीकरण सफल रहा"),
                message: Text(language == .EN ? "Please login using your mobile number and password." : "कृपया अपने मोबाइल नंबर और पासवर्ड का उपयोग करके लॉगिन करें।"),
                dismissButton: .default(Text("OK")) {
                    currentScreen = .login
                }
            )
        }
    }
    
    private func performRegister() {
        guard !name.isEmpty,
              !mobileNumber.isEmpty,
              !shopName.isEmpty,
              !city.isEmpty,
              !password.isEmpty,
              !confirmPassword.isEmpty else {
            errorMessage = language == .EN ? "Please fill in all mandatory fields (*)" : "कृपया सभी आवश्यक फ़ील्ड (*) भरें"
            return
        }
        
        guard password.count >= 6 else {
            errorMessage = language == .EN ? "Password must be at least 6 characters" : "पासवर्ड कम से कम 6 अंकों का होना चाहिए"
            return
        }
        
        guard password == confirmPassword else {
            errorMessage = language == .EN ? "Passwords do not match" : "पासवर्ड मेल नहीं खाते हैं"
            return
        }
        
        guard agreeToTerms else {
            errorMessage = language == .EN ? "You must agree to the Terms & Conditions" : "आपको नियम व शर्तों से सहमत होना होगा"
            return
        }
        
        loading = true
        errorMessage = ""
        
        Task {
            do {
                let payload = MechanicPayload(
                    id: mobileNumber.trimmingCharacters(in: .whitespaces),
                    name: name,
                    workshop: "\(shopName), \(city)",
                    mobile: mobileNumber.trimmingCharacters(in: .whitespaces),
                    password: password
                )
                
                try await NetworkManager.shared.register(payload: payload)
                
                DispatchQueue.main.async {
                    self.loading = false
                    self.successAlert = true
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

struct InputField: View {
    let label: String
    let placeholder: String
    @Binding var text: String
    var keyboard: UIKeyboardType = .default
    
    var body: some View {
        VStack(alignment: .leading, spacing: 5) {
            Text(label)
                .font(.system(size: 13, weight: .semibold))
                .foregroundColor(SakshamTheme.darkText)
            
            TextField(placeholder, text: $text)
                .keyboardType(keyboard)
                .padding()
                .background(Color.white)
                .cornerRadius(6)
                .overlay(
                    RoundedRectangle(cornerRadius: 6)
                        .stroke(SakshamTheme.borderBase, lineWidth: 1)
                )
        }
    }
}

// SwiftUI custom checkbox style
struct CheckboxStyle: ToggleStyle {
    func makeBody(configuration: Self.Configuration) -> some View {
        return HStack {
            Image(systemName: configuration.isOn ? "checkmark.square.fill" : "square")
                .resizable()
                .frame(width: 18, height: 18)
                .foregroundColor(configuration.isOn ? SakshamTheme.primaryRoyalBlue : Color.gray)
                .onTapGesture {
                    configuration.isOn.toggle()
                }
            configuration.label
        }
    }
}
