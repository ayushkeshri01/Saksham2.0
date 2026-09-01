import SwiftUI

struct LandingView: View {
    @Binding var currentScreen: AppScreen
    @Binding var language: AppLanguage
    
    var body: some View {
        VStack(spacing: 0) {
            // Language Selection at the top right
            HStack {
                Spacer()
                LanguageToggleView(language: $language)
                    .padding(.trailing, 20)
                    .padding(.top, 10)
            }
            
            Spacer()
            
            // Branding section
            VStack(spacing: 12) {
                // Mock logo placeholder (or image assets in Xcode)
                Image(systemName: "bolt.shield.fill")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(height: 60)
                    .foregroundColor(SakshamTheme.primaryRoyalBlue)
                
                Text("SAKSHAM 2.0")
                    .font(.system(size: 26, weight: .bold))
                    .foregroundColor(SakshamTheme.primaryRoyalBlue)
                    .tracking(1.5)
                
                Text("VIKAS GROUP")
                    .font(.system(size: 13, weight: .semibold))
                    .foregroundColor(SakshamTheme.subtleText)
                    .tracking(2.0)
            }
            .padding(.bottom, 40)
            
            // Value propositions
            VStack(spacing: 14) {
                Text(language == .EN ? "Collect Data.\nDrive Quality.\nEarn More." : "डेटा एकत्र करें।\nगुणवत्ता बढ़ाएं।\nअधिक कमाएं।")
                    .font(.system(size: 32, weight: .black))
                    .foregroundColor(SakshamTheme.primaryRoyalBlue)
                    .multilineTextAlignment(.center)
                    .lineSpacing(4)
                
                Text(language == .EN ? "Submit accurate compressor & condenser data and get extra incentives for using Pranav products." : "सटीक कंप्रेसर और कंडेनसर डेटा सबमिट करें और प्रणव उत्पादों का उपयोग करने पर अतिरिक्त प्रोत्साहन प्राप्त करें।")
                    .font(.system(size: 14))
                    .foregroundColor(SakshamTheme.subtleText)
                    .multilineTextAlignment(.center)
                    .lineSpacing(4)
                    .padding(.horizontal, 30)
            }
            .padding(.bottom, 40)
            
            // Feature Highlights list
            VStack(alignment: .leading, spacing: 12) {
                FeatureHighlightRow(text: language == .EN ? "Easy & Quick Data Submission" : "आसान और त्वरित डेटा सबमिशन", language: language)
                FeatureHighlightRow(text: language == .EN ? "Trusted Quality Tracking Platform" : "विश्वसनीय गुणवत्ता ट्रैकिंग प्लेटफॉर्म", language: language)
                FeatureHighlightRow(text: language == .EN ? "Incentives for Pranav Components" : "प्रणव कंपोनेंट्स के लिए अतिरिक्त इंसेंटिव", language: language)
            }
            .padding(.horizontal, 24)
            .padding(.vertical, 16)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color.white)
            .cornerRadius(12)
            .shadow(color: Color.black.opacity(0.04), radius: 6, x: 0, y: 3)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(SakshamTheme.borderBase, lineWidth: 1)
            )
            .padding(.horizontal, 24)
            .padding(.bottom, 40)
            
            // Actions
            VStack(spacing: 12) {
                Button(action: {
                    currentScreen = .login
                }) {
                    Text(language == .EN ? "LOGIN" : "लॉगिन")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(SakshamTheme.primaryRoyalBlue)
                        .cornerRadius(8)
                }
                
                Button(action: {
                    currentScreen = .register
                }) {
                    Text(language == .EN ? "REGISTER" : "पंजीकरण करें")
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
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 30)
            
            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(SakshamTheme.softBlueBackground.edgesIgnoringSafeArea(.all))
    }
}

struct FeatureHighlightRow: View {
    let text: String
    let language: AppLanguage
    
    var body: some View {
        HStack(spacing: 10) {
            Image(systemName: "checkmark")
                .font(.system(size: 10, weight: .bold))
                .foregroundColor(.white)
                .frame(width: 18, height: 18)
                .background(SakshamTheme.accentCyan)
                .cornerRadius(4)
            
            Text(text)
                .font(.system(size: 13, weight: .medium))
                .foregroundColor(SakshamTheme.darkText)
        }
    }
}

struct LanguageToggleView: View {
    @Binding var language: AppLanguage
    
    var body: some View {
        HStack(spacing: 0) {
            Text("EN")
                .font(.system(size: 10, weight: .bold))
                .foregroundColor(language == .EN ? .white : SakshamTheme.primaryRoyalBlue)
                .frame(width: 32, height: 26)
                .background(language == .EN ? SakshamTheme.primaryRoyalBlue : Color.clear)
                .cornerRadius(13)
                .onTapGesture {
                    language = .EN
                }
            
            Text("HI")
                .font(.system(size: 10, weight: .bold))
                .foregroundColor(language == .HI ? .white : SakshamTheme.primaryRoyalBlue)
                .frame(width: 32, height: 26)
                .background(language == .HI ? SakshamTheme.primaryRoyalBlue : Color.clear)
                .cornerRadius(13)
                .onTapGesture {
                    language = .HI
                }
        }
        .background(Color(hex: "#EAF0F8"))
        .cornerRadius(15)
        .padding(2)
        .overlay(
            RoundedRectangle(cornerRadius: 15)
                .stroke(Color.clear, lineWidth: 1)
        )
    }
}
