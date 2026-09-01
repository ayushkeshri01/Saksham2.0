import SwiftUI

struct LoggingFlowView: View {
    let componentType: String
    @Binding var isPresented: Bool
    @Binding var jobs: [JobEntry]
    @Binding var language: AppLanguage
    let mechanicId: String
    
    @State private var currentStep = 1
    
    // Step 1 states: Vehicle Details
    @State private var vehicleMake = "Maruti Suzuki"
    @State private var vehicleModel = ""
    @State private var vehicleVariant = ""
    @State private var vehicleYear = "2022"
    @State private var odometerReading = ""
    @State private var registrationNumber = ""
    
    // Step 2 states: Component / Failure Details
    @State private var failureCause = ""
    @State private var severity = "Minor"
    @State private var acUsage = "Daily"
    @State private var notes = ""
    
    // Step 3 states: Upload Images (Mock file paths)
    @State private var photo1 = false
    @State private var photo2 = false
    @State private var photo3 = false
    
    // Step 4 states: GPS Location Details
    @State private var latitude: Double = 0.0
    @State private var longitude: Double = 0.0
    @State private var locationCaptured = false
    @State private var capturingLocation = false
    
    let makes = ["Maruti Suzuki", "Hyundai", "Tata Motors", "Mahindra", "Honda", "Toyota", "Kia", "Other"]
    let causes = ["Stone impact", "Corrosion", "Accident", "Pressure failure", "Manufacturing defect", "Poor servicing", "Other"]
    let severities = ["Minor", "Major", "Total loss"]
    let acUsages = ["Daily", "Occasional", "Rarely"]
    
    var body: some View {
        VStack(spacing: 0) {
            // Header bar
            HStack {
                Button(action: {
                    if currentStep > 1 {
                        currentStep -= 1
                    } else {
                        isPresented = false
                    }
                }) {
                    HStack(spacing: 4) {
                        Image(systemName: "chevron.left")
                        Text(language == .EN ? "Back" : "पीछे")
                    }
                    .font(.system(size: 14, weight: .bold))
                    .foregroundColor(SakshamTheme.primaryRoyalBlue)
                }
                Spacer()
                
                Text("\(language == .EN ? "Step" : "चरण") \(currentStep) / 5")
                    .font(.system(size: 14, weight: .bold))
                    .foregroundColor(SakshamTheme.primaryRoyalBlue)
                
                Spacer()
                Button(action: {
                    isPresented = false
                }) {
                    Text(language == .EN ? "Cancel" : "रद्द करें")
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundColor(.red)
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .background(Color.white)
            .shadow(color: Color.black.opacity(0.04), radius: 2, x: 0, y: 1)
            
            // Progress Bar indicator
            GeometryReader { geo in
                Rectangle()
                    .fill(Color(hex: "#EAF0F8"))
                    .frame(height: 4)
                    .overlay(
                        Rectangle()
                            .fill(SakshamTheme.accentCyan)
                            .frame(width: geo.size.width * CGFloat(currentStep) / 5.0, height: 4),
                        alignment: .leading
                    )
            }
            .frame(height: 4)
            
            // Main Form Body
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    switch currentStep {
                    case 1:
                        VehicleDetailsStep()
                    case 2:
                        ComponentDetailsStep()
                    case 3:
                        UploadImagesStep()
                    case 4:
                        LocationStep()
                    case 5:
                        ReviewStep()
                    default:
                        EmptyView()
                    }
                }
                .padding(20)
            }
            .background(SakshamTheme.softBlueBackground)
            
            // Navigation Bottom Action Buttons
            HStack(spacing: 16) {
                if currentStep < 5 {
                    Button(action: {
                        nextStep()
                    }) {
                        Text(language == .EN ? "NEXT" : "आगे")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                            .background(canProceed() ? SakshamTheme.primaryRoyalBlue : Color.gray.opacity(0.5))
                            .cornerRadius(8)
                    }
                    .disabled(!canProceed())
                } else {
                    Button(action: {
                        submitLog()
                    }) {
                        Text(language == .EN ? "SUBMIT LOG" : "सबमिट करें")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                            .background(SakshamTheme.primaryRoyalBlue)
                            .cornerRadius(8)
                    }
                }
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 16)
            .background(Color.white)
            .shadow(color: Color.black.opacity(0.04), radius: 4, x: 0, y: -2)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(SakshamTheme.softBlueBackground.edgesIgnoringSafeArea(.all))
    }
    
    // STEP 1: VEHICLE DETAILS
    @ViewBuilder
    private func VehicleDetailsStep() -> some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(language == .EN ? "Step 1: Vehicle details" : "चरण 1: वाहन विवरण")
                .font(.system(size: 18, weight: .bold))
                .foregroundColor(SakshamTheme.primaryRoyalBlue)
            
            VStack(alignment: .leading, spacing: 5) {
                Text(language == .EN ? "Vehicle Manufacturer *" : "वाहन निर्माता *")
                    .font(.system(size: 13, weight: .semibold))
                    .foregroundColor(SakshamTheme.darkText)
                
                Picker("Make", selection: $vehicleMake) {
                    ForEach(makes, id: \.self) { make in
                        Text(make).tag(make)
                    }
                }
                .pickerStyle(MenuPickerStyle())
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding()
                .background(Color.white)
                .cornerRadius(6)
                .overlay(RoundedRectangle(cornerRadius: 6).stroke(SakshamTheme.borderBase, lineWidth: 1))
            }
            
            InputField(label: language == .EN ? "Vehicle Model *" : "वाहन मॉडल *", placeholder: "e.g. Swift, i20, Nexon", text: $vehicleModel)
            
            InputField(label: language == .EN ? "Variant (Optional)" : "वेरिएंट (वैकल्पिक)", placeholder: "e.g. VXI, Magna, XM", text: $vehicleVariant)
            
            InputField(label: language == .EN ? "Manufacturing Year *" : "निर्माण वर्ष *", placeholder: "e.g. 2022", text: $vehicleYear, keyboard: .numberPad)
            
            InputField(label: language == .EN ? "Odometer Reading (km) *" : "ओडोमीटर रीडिंग (किमी) *", placeholder: "e.g. 45000", text: $odometerReading, keyboard: .numberPad)
            
            InputField(label: language == .EN ? "Registration Number" : "पंजीकरण संख्या", placeholder: "e.g. DL1CA1234 (Optional)", text: $registrationNumber)
        }
    }
    
    // STEP 2: FAILURE/COMPONENT DETAILS
    @ViewBuilder
    private func ComponentDetailsStep() -> some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(language == .EN ? "Step 2: Component issues" : "चरण 2: घटक मुद्दे")
                .font(.system(size: 18, weight: .bold))
                .foregroundColor(SakshamTheme.primaryRoyalBlue)
            
            Text(language == .EN ? "Select Failure Cause * (Tap to select)" : "विफलता का कारण चुनें *")
                .font(.system(size: 13, weight: .semibold))
                .foregroundColor(SakshamTheme.darkText)
            
            VStack(spacing: 8) {
                ForEach(causes, id: \.self) { cause in
                    let isSelected = failureCause == cause
                    HStack {
                        Text(cause)
                            .font(.system(size: 13, weight: isSelected ? .bold : .medium))
                            .foregroundColor(isSelected ? SakshamTheme.primaryRoyalBlue : SakshamTheme.darkText)
                        Spacer()
                        if isSelected {
                            Image(systemName: "checkmark.circle.fill")
                                .foregroundColor(SakshamTheme.primaryRoyalBlue)
                        }
                    }
                    .padding()
                    .background(Color.white)
                    .cornerRadius(6)
                    .overlay(
                        RoundedRectangle(cornerRadius: 6)
                            .stroke(isSelected ? SakshamTheme.primaryRoyalBlue : SakshamTheme.borderBase, lineWidth: isSelected ? 1.5 : 1)
                    )
                    .onTapGesture {
                        failureCause = cause
                    }
                }
            }
            
            // Severity Picker
            VStack(alignment: .leading, spacing: 5) {
                Text(language == .EN ? "Issue Severity *" : "गंभीरता *")
                    .font(.system(size: 13, weight: .semibold))
                    .foregroundColor(SakshamTheme.darkText)
                
                Picker("Severity", selection: $severity) {
                    ForEach(severities, id: \.self) { sev in
                        Text(sev).tag(sev)
                    }
                }
                .pickerStyle(SegmentedPickerStyle())
            }
            .padding(.top, 5)
            
            // AC Usage Picker
            VStack(alignment: .leading, spacing: 5) {
                Text(language == .EN ? "AC Usage Profile *" : "एसी उपयोग प्रोफ़ाइल *")
                    .font(.system(size: 13, weight: .semibold))
                    .foregroundColor(SakshamTheme.darkText)
                
                Picker("AC Usage", selection: $acUsage) {
                    ForEach(acUsages, id: \.self) { usage in
                        Text(usage).tag(usage)
                    }
                }
                .pickerStyle(SegmentedPickerStyle())
            }
            
            InputField(label: language == .EN ? "Mechanic Diagnostic Notes" : "मैकेनिक डायग्नोस्टिक नोट्स", placeholder: language == .EN ? "Enter failure notes here..." : "यहाँ विफलता नोट्स दर्ज करें...", text: $notes)
        }
    }
    
    // STEP 3: MOCK UPLOAD IMAGES
    @ViewBuilder
    private func UploadImagesStep() -> some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(language == .EN ? "Step 3: Capture diagnostic images" : "चरण 3: नैदानिक चित्र कैप्चर करें")
                .font(.system(size: 18, weight: .bold))
                .foregroundColor(SakshamTheme.primaryRoyalBlue)
            Text(language == .EN ? "Upload up to 3 diagnostic photos of the component fail area. At least 1 photo required." : "घटक विफलता क्षेत्र की 3 नैदानिक तस्वीरें तक अपलोड करें। कम से कम 1 तस्वीर आवश्यक है।")
                .font(.system(size: 13))
                .foregroundColor(SakshamTheme.subtleText)
                .lineSpacing(2)
            
            HStack(spacing: 12) {
                PhotoBoxView(title: "PHOTO 1 *", isCaptured: $photo1)
                PhotoBoxView(title: "PHOTO 2", isCaptured: $photo2)
                PhotoBoxView(title: "PHOTO 3", isCaptured: $photo3)
            }
            .padding(.vertical, 10)
        }
    }
    
    // STEP 4: LOCATION GPS DATA
    @ViewBuilder
    private func LocationStep() -> some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(language == .EN ? "Step 4: Location details" : "चरण 4: स्थान विवरण")
                .font(.system(size: 18, weight: .bold))
                .foregroundColor(SakshamTheme.primaryRoyalBlue)
            
            Text(language == .EN ? "Locate the workshop coordinates to associate GPS timestamps with the log entry." : "लॉग प्रविष्टि के साथ जीपीएस टाइमस्टैम्प को जोड़ने के लिए वर्कशॉप निर्देशांक का स्थान खोजें।")
                .font(.system(size: 13))
                .foregroundColor(SakshamTheme.subtleText)
                .lineSpacing(2)
            
            VStack(spacing: 16) {
                if locationCaptured {
                    VStack(spacing: 8) {
                        Image(systemName: "mappin.and.ellipse")
                            .font(.system(size: 36))
                            .foregroundColor(Color.green)
                        Text(language == .EN ? "GPS Coordinates Captured Successfully" : "जीपीएस निर्देशांक सफलतापूर्वक कैप्चर किया गया")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(Color.green)
                        
                        Text("Lat: \(String(format: "%.5f", latitude))\nLong: \(String(format: "%.5f", longitude))")
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundColor(SakshamTheme.darkText)
                            .multilineTextAlignment(.center)
                            .padding(.top, 4)
                    }
                    .padding()
                    .frame(maxWidth: .infinity)
                    .background(Color.green.opacity(0.08))
                    .cornerRadius(8)
                } else if capturingLocation {
                    VStack(spacing: 12) {
                        ProgressView()
                            .scaleEffect(1.2)
                        Text(language == .EN ? "Acquiring GPS Lock..." : "जीपीएस लॉक प्राप्त कर रहा है...")
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundColor(SakshamTheme.primaryRoyalBlue)
                    }
                    .padding()
                    .frame(maxWidth: .infinity)
                } else {
                    Button(action: {
                        captureLocation()
                    }) {
                        HStack {
                            Image(systemName: "location.fill")
                            Text(language == .EN ? "ACQUIRE WORKSHOP LOCATION" : "स्थान निर्देशांक कैप्चर करें")
                                .font(.system(size: 14, weight: .bold))
                        }
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 48)
                        .background(SakshamTheme.primaryRoyalBlue)
                        .cornerRadius(6)
                    }
                }
            }
            .padding()
            .background(Color.white)
            .cornerRadius(12)
            .overlay(RoundedRectangle(cornerRadius: 12).stroke(SakshamTheme.borderBase, lineWidth: 1))
        }
    }
    
    // STEP 5: REVIEW LOG DETAILS
    @ViewBuilder
    private func ReviewStep() -> some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(language == .EN ? "Step 5: Review & Submit" : "चरण 5: समीक्षा और सबमिट करें")
                .font(.system(size: 18, weight: .bold))
                .foregroundColor(SakshamTheme.primaryRoyalBlue)
            Text(language == .EN ? "Please verify all diagnostic parameters before submitting. Once submitted, incentives will be queued." : "कृपया सबमिट करने से पहले सभी डायग्नोस्टिक मापदंडों को सत्यापित करें। एक बार सबमिट करने के बाद, इंसेंटिव कतारबद्ध हो जाएंगे।")
                .font(.system(size: 13))
                .foregroundColor(SakshamTheme.subtleText)
                .lineSpacing(2)
            
            VStack(spacing: 0) {
                ReviewRow(label: language == .EN ? "Component Type" : "घटक का प्रकार", value: componentType.uppercased())
                Divider()
                ReviewRow(label: language == .EN ? "Vehicle Make" : "वाहन निर्माता", value: vehicleMake)
                Divider()
                ReviewRow(label: language == .EN ? "Vehicle Model" : "वाहन मॉडल", value: vehicleModel)
                Divider()
                ReviewRow(label: language == .EN ? "Odometer" : "ओडोमीटर", value: "\(odometerReading) km")
                Divider()
                ReviewRow(label: language == .EN ? "Failure Cause" : "विफलता का कारण", value: failureCause)
                Divider()
                ReviewRow(label: language == .EN ? "Severity" : "गंभीरता", value: severity)
                Divider()
                ReviewRow(label: language == .EN ? "AC Usage" : "एसी उपयोग", value: acUsage)
                Divider()
                ReviewRow(label: language == .EN ? "GPS Location" : "जीपीएस स्थान", value: "\(String(format: "%.4f", latitude)), \(String(format: "%.4f", longitude))")
            }
            .background(Color.white)
            .cornerRadius(10)
            .overlay(RoundedRectangle(cornerRadius: 10).stroke(SakshamTheme.borderBase, lineWidth: 1))
        }
    }
    
    private func nextStep() {
        if currentStep < 5 {
            currentStep += 1
        }
    }
    
    private func canProceed() -> Bool {
        switch currentStep {
        case 1:
            return !vehicleModel.trimmingCharacters(in: .whitespaces).isEmpty &&
                   !vehicleYear.trimmingCharacters(in: .whitespaces).isEmpty &&
                   !odometerReading.trimmingCharacters(in: .whitespaces).isEmpty
        case 2:
            return !failureCause.isEmpty
        case 3:
            return photo1 // Minimum 1 image required
        case 4:
            return locationCaptured
        default:
            return true
        }
    }
    
    private func captureLocation() {
        capturingLocation = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
            // Mock Delhi / Noida workshop coordinates
            self.latitude = 28.6139 + Double.random(in: -0.05...0.05)
            self.longitude = 77.2090 + Double.random(in: -0.05...0.05)
            self.capturingLocation = false
            self.locationCaptured = true
        }
    }
    
    private func submitLog() {
        let entryId = UUID().uuidString
        let entry = JobEntry(
            id: entryId,
            make: vehicleMake,
            model: vehicleModel,
            variant: vehicleVariant,
            year: Int(vehicleYear) ?? 2022,
            registrationNumber: registrationNumber.isEmpty ? nil : registrationNumber,
            photoPath1: photo1 ? "mock_photo_1.jpg" : nil,
            photoPath2: photo2 ? "mock_photo_2.jpg" : nil,
            photoPath3: photo3 ? "mock_photo_3.jpg" : nil,
            gpsLatitude: latitude,
            gpsLongitude: longitude,
            timestamp: Int64(Date().timeIntervalSince1970 * 1000),
            failureCause: failureCause,
            severity: severity,
            odometer: Int(odometerReading),
            acUsage: acUsage,
            priorServiceDate: nil,
            notes: notes.isEmpty ? nil : notes,
            mechanicId: mechanicId,
            syncStatus: "QUEUED", // Offline-first queue state
            createdAt: Int64(Date().timeIntervalSince1970 * 1000),
            componentType: componentType
        )
        
        // Save locally immediately
        jobs.insert(entry, at: 0)
        
        // Trigger async sync upload in the background
        Task {
            do {
                try await NetworkManager.shared.syncJob(entry: entry)
                
                // Update sync status locally if successful
                DispatchQueue.main.async {
                    if let index = self.jobs.firstIndex(where: { $0.id == entryId }) {
                        self.jobs[index].syncStatus = "SYNCED"
                    }
                }
            } catch {
                print("Failed to sync log: \(error.localizedDescription)")
            }
        }
        
        isPresented = false
    }
}

struct PhotoBoxView: View {
    let title: String
    @Binding var isCaptured: Bool
    
    var body: some View {
        Button(action: {
            isCaptured.toggle()
        }) {
            VStack(spacing: 8) {
                if isCaptured {
                    Image(systemName: "checkmark.circle.fill")
                        .font(.system(size: 26))
                        .foregroundColor(Color.green)
                    Text("CAPTURED")
                        .font(.system(size: 9, weight: .bold))
                        .foregroundColor(Color.green)
                } else {
                    Image(systemName: "camera.fill")
                        .font(.system(size: 24))
                        .foregroundColor(Color.gray)
                    Text(title)
                        .font(.system(size: 9, weight: .bold))
                        .foregroundColor(Color.gray)
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: 80)
            .background(Color.white)
            .cornerRadius(8)
            .overlay(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(isCaptured ? Color.green : SakshamTheme.borderBase, lineWidth: isCaptured ? 1.5 : 1)
            )
        }
    }
}

struct ReviewRow: View {
    let label: String
    let value: String
    
    var body: some View {
        HStack {
            Text(label)
                .font(.system(size: 13, weight: .semibold))
                .foregroundColor(SakshamTheme.subtleText)
            Spacer()
            Text(value)
                .font(.system(size: 13, weight: .bold))
                .foregroundColor(SakshamTheme.darkText)
        }
        .padding()
    }
}
