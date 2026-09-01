import Foundation

enum AppLanguage: String, Codable {
    case EN
    case HI
}

struct MechanicProfile: Codable, Identifiable {
    var id: String
    var name: String
    var workshop: String
    var mobile: String
    var points: Int
}

struct JobEntry: Codable, Identifiable, Hashable {
    var id: String
    var make: String
    var model: String
    var variant: String
    var year: Int
    var registrationNumber: String?
    var photoPath1: String?
    var photoPath2: String?
    var photoPath3: String?
    var gpsLatitude: Double
    var gpsLongitude: Double
    var timestamp: Int64
    var failureCause: String
    var severity: String
    var odometer: Int?
    var acUsage: String?
    var priorServiceDate: String?
    var notes: String?
    var mechanicId: String
    var syncStatus: String // "QUEUED" or "SYNCED"
    var createdAt: Int64
    var componentType: String // "condenser" or "compressor"
}

struct SyncPayload: Codable {
    var id: String
    var make: String
    var model: String
    var variant: String
    var year: Int
    var registrationNumber: String?
    var photoBase64_1: String?
    var photoBase64_2: String?
    var photoBase64_3: String?
    var gpsLatitude: Double
    var gpsLongitude: Double
    var timestamp: Int64
    var failureCause: String
    var severity: String
    var odometer: Int?
    var acUsage: String?
    var priorServiceDate: String?
    var notes: String?
    var mechanicId: String
    var createdAt: Int64
}

struct LoginPayload: Codable {
    var id: String
    var password: String
}

struct LoginResponse: Codable {
    var message: String
    var mechanic: MechanicProfile
}

struct MechanicPayload: Codable {
    var id: String
    var name: String
    var workshop: String
    var mobile: String
    var password: String
}

// Localization Helper
struct Loc {
    private static let translations: [String: [String: String]] = [
        "EN": [
            "app_name": "Saksham 2.0",
            "greeting": "Hello, %@",
            "select_cause": "Select failure cause",
            "failure_cause_title": "Failure Cause",
            "recent_entries": "Recent Logged Entries",
            "synced": "SYNCED",
            "queued": "QUEUED",
            "cancel": "Cancel",
            "submit": "Submit",
            "next": "Next",
            "go_home": "Go to Home Screen",
            "select_cause_error": "Please select a failure cause",
            "vehicle_id_title": "Vehicle ID details"
        ],
        "HI": [
            "app_name": "सक्षम २.० (Saksham 2.0)",
            "greeting": "नमस्ते, %@",
            "select_cause": "विफलता का कारण चुनें",
            "failure_cause_title": "विफलता का कारण",
            "recent_entries": "हाल ही में दर्ज की गई प्रविष्टियाँ",
            "synced": "सिंक हो गया",
            "queued": "कतारबद्ध",
            "cancel": "रद्द करें",
            "submit": "जमा करें",
            "next": "आगे",
            "go_home": "होम स्क्रीन पर जाएं",
            "select_cause_error": "कृपया विफलता का कारण चुनें",
            "vehicle_id_title": "वाहन आईडी विवरण"
        ]
    ]

    static func get(_ key: String, _ lang: AppLanguage) -> String {
        return translations[lang.rawValue]?[key] ?? key
    }
}
