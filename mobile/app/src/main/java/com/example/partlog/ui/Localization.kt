package com.example.partlog.ui

import androidx.compose.runtime.mutableStateMapOf
import com.example.partlog.sync.SarvamTranslator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log

enum class AppLanguage(val code: String, val displayName: String) {
    EN("en-IN", "English"),
    HI("hi-IN", "हिन्दी (Hindi)"),
    AS("as-IN", "অসমীয়া (Assamese)"),
    BN("bn-IN", "বাংলা (Bengali)"),
    GU("gu-IN", "ગુજરાતી (Gujarati)"),
    KN("kn-IN", "ಕನ್ನಡ (Kannada)"),
    ML("ml-IN", "മലയാളം (Malayalam)"),
    MR("mr-IN", "मराठी (Marathi)"),
    OD("od-IN", "ଓଡ଼ିଆ (Odia)"),
    PA("pa-IN", "ਪੰਜਾਬੀ (Punjabi)"),
    TA("ta-IN", "தமிழ் (Tamil)"),
    TE("te-IN", "తెలుగు (Telugu)")
}

object Loc {
    private val translations = mapOf(
        AppLanguage.EN to mapOf(
            "app_name" to "Saksham 2.0",
            "greeting" to "Hello, %s",
            "signup_title" to "Mechanic Sign Up",
            "signup_subtitle" to "Register your profile to begin logging",
            "mechanic_id_label" to "Mechanic ID",
            "mechanic_id_hint" to "Enter unique ID (e.g. mech_004)",
            "mechanic_name_label" to "Mechanic Name",
            "mechanic_name_hint" to "Enter full name",
            "workshop_label" to "Workshop Name",
            "workshop_hint" to "Enter workshop and city details",
            "mobile_label" to "Mobile Number",
            "mobile_hint" to "Enter 10-digit mobile number",
            "password_label" to "Password",
            "password_hint" to "Enter password",
            "login_title" to "Mechanic Log In",
            "login_button" to "Log In",
            "login_error" to "Invalid credentials or server error",
            "no_account" to "Don't have an account? Sign Up",
            "has_account" to "Already have an account? Log In",
            "register_button" to "Register",
            "registering" to "Registering...",
            "signup_success" to "Registration successful!",
            "signup_error" to "Registration failed. Check internet/server.",
            "fill_fields_error" to "Please fill all required fields",
            "points_earned" to "Points: %d",
            "log_job" to "Log condenser job",
            "recent_entries" to "Recent Entries",
            "queued" to "Queued",
            "synced" to "Synced",
            "vehicle_id_title" to "Vehicle Identification",
            "make" to "Make",
            "model" to "Model",
            "variant" to "Variant",
            "year" to "Year",
            "reg_no" to "Registration Number (Optional)",
            "common_models" to "Common Models",
            "scan_reg" to "Scan registration (OCR)",
            "photo_capture_title" to "Photo Capture",
            "damage_photo" to "Damage photo",
            "label_photo" to "Part label photo",
            "installed_photo" to "Installed unit photo",
            "gps_captured" to "GPS Captured: %.4f, %.4f",
            "gps_acquiring" to "Acquiring GPS...",
            "gps_not_captured" to "No GPS (Check permissions)",
            "failure_cause_title" to "Failure Cause",
            "select_cause" to "Select Cause (Required)",
            "select_severity" to "Select Severity (Optional)",
            "add_details" to "Add Details (Optional)",
            "odometer" to "Odometer Reading (km)",
            "ac_usage" to "AC Usage Frequency",
            "prior_service" to "Prior Service Date",
            "notes" to "Voice/Text Notes",
            "confirmation_title" to "Confirmation",
            "job_saved_locally" to "Job saved locally!",
            "queued_badge" to "Queued for sync",
            "points_gained" to "+5 points earned!",
            "leaderboard" to "Leaderboard",
            "next" to "Next",
            "submit" to "Submit Job",
            "go_home" to "Go to Home",
            "cancel" to "Cancel",
            "skip" to "Skip",
            "photo_added" to "Photo added",
            "photo_missing" to "Tap to take photo",
            "select_model_error" to "Please select vehicle model",
            "select_cause_error" to "Please select a failure cause",
            "minor" to "Minor",
            "major" to "Major",
            "total_loss" to "Total loss",
            "ac_daily" to "Daily",
            "ac_occasional" to "Occasional",
            "ac_rarely" to "Rarely",
            "kyc_title" to "KYC Verification",
            "kyc_status" to "KYC Status",
            "kyc_verified" to "Verified",
            "kyc_pending" to "Pending Verification",
            "kyc_not_submitted" to "Not Verified",
            "kyc_failed" to "Verification Failed",
            "kyc_pan_number" to "PAN Card Number",
            "kyc_pan_hint" to "Enter 10-Digit PAN (e.g. ABCDE1234F)",
            "kyc_verify_btn" to "VERIFY PAN NOW",
            "kyc_verify_submit" to "Verify Now",
            "kyc_name" to "PAN Registered Name",
            "kyc_required_msg" to "KYC Verification is required to redeem points. Please verify your PAN card first.",
            "kyc_placeholder" to "XXXXX1234X",
            "kyc_dialog_desc" to "Please enter your 10-character PAN Card number. Ensure the name matches your profile name.",
            "kyc_success" to "PAN Card verified successfully!"
        ),
        AppLanguage.HI to mapOf(
            "app_name" to "सक्षम २.० (Saksham 2.0)",
            "greeting" to "नमस्ते, %s",
            "signup_title" to "मैकेनिक पंजीकरण (Sign Up)",
            "signup_subtitle" to "लॉगिंग शुरू करने के लिए अपना प्रोफाइल दर्ज करें",
            "mechanic_id_label" to "मैकेनिक आईडी",
            "mechanic_id_hint" to "अनूठी आईडी दर्ज करें (उदा. mech_004)",
            "mechanic_name_label" to "मैकेनिक का नाम",
            "mechanic_name_hint" to "पूरा नाम दर्ज करें",
            "workshop_label" to "कार्यशाला का नाम",
            "workshop_hint" to "कार्यशाला और शहर का विवरण दर्ज करें",
            "mobile_label" to "मोबाइल नंबर",
            "mobile_hint" to "10 अंकों का मोबाइल नंबर दर्ज करें",
            "password_label" to "पासवर्ड (Password)",
            "password_hint" to "पासवर्ड दर्ज करें",
            "login_title" to "मैकेनिक लॉगिन",
            "login_button" to "लॉगिन करें",
            "login_error" to "गलत क्रेडेंशियल या सर्वर त्रुटि",
            "no_account" to "खाता नहीं है? साइन अप करें",
            "has_account" to "पहले से खाता है? लॉगिन करें",
            "register_button" to "पंजीकरण करें",
            "registering" to "पंजीकरण हो रहा है...",
            "signup_success" to "पंजीकरण सफल रहा!",
            "signup_error" to "पंजीकरण विफल। इंटरनेट/सर्वर की जाँच करें।",
            "fill_fields_error" to "कृपया सभी आवश्यक फ़ील्ड भरें",
            "points_earned" to "अंक: %d",
            "log_job" to "कंडेंसर जॉब दर्ज करें (+)",
            "recent_entries" to "हाल की प्रविष्टियां",
            "queued" to "कतार में",
            "synced" to "सिंक हो गया",
            "vehicle_id_title" to "वाहन की पहचान",
            "make" to "निर्माता (Make)",
            "model" to "मॉडल (Model)",
            "variant" to "वेरिएंट (Variant)",
            "year" to "वर्ष (Year)",
            "reg_no" to "पंजीकरण संख्या (वैकल्पिक)",
            "common_models" to "लोकप्रिय मॉडल",
            "scan_reg" to "पंजीकरण संख्या स्कैन करें (OCR)",
            "photo_capture_title" to "फोटो कैप्चर",
            "damage_photo" to "नुकसान (Damage) फोटो",
            "label_photo" to "पार्ट लेबल (Label) फोटो",
            "installed_photo" to "लगाई गई यूनिट की फोटो",
            "gps_captured" to "जीपीएस स्थान: %.4f, %.4f",
            "gps_acquiring" to "जीपीएस स्थान खोजा जा रहा है...",
            "gps_not_captured" to "जीपीएस अनुपलब्ध (अनुमति जांचें)",
            "failure_cause_title" to "विफलता का कारण",
            "select_cause" to "कारण चुनें (अनिवार्य)",
            "select_severity" to "तीव्रता चुनें (वैकल्पिक)",
            "add_details" to "अतिरिक्त विवरण (वैकल्पिक)",
            "odometer" to "ओडोमीटर रीडिंग (किमी)",
            "ac_usage" to "एसी उपयोग की आवृत्ति",
            "prior_service" to "पिछली सर्विस की तारीख",
            "notes" to "आवाज/लिखकर नोट्स",
            "confirmation_title" to "पुष्टि",
            "job_saved_locally" to "जॉब स्थानीय रूप से सहेजा गया!",
            "queued_badge" to "सिंक के लिए कतारबद्ध",
            "points_gained" to "+5 अंक प्राप्त हुए!",
            "leaderboard" to "लीडरबोर्ड (रैंकिंग)",
            "next" to "अगला",
            "submit" to "जॉब सबमिट करें",
            "go_home" to "होम पर जाएं",
            "cancel" to "रद्द करें",
            "skip" to "छोड़ें",
            "photo_added" to "फोटो जोड़ी गई",
            "photo_missing" to "फोटो लेने के लिए टैप करें",
            "select_model_error" to "कृपया वाहन मॉडल चुनें",
            "select_cause_error" to "कृपया विफलता का कारण चुनें",
            "minor" to "मामूली (Minor)",
            "major" to "बड़ा (Major)",
            "total_loss" to "पूरी तरह खराब (Total loss)",
            "ac_daily" to "दैनिक (Daily)",
            "ac_occasional" to "कभी-कभी (Occasional)",
            "ac_rarely" to "शायद ही कभी",
            "kyc_title" to "केवाईसी सत्यापन (KYC)",
            "kyc_status" to "केवाईसी स्थिति",
            "kyc_verified" to "सत्यापित (Verified)",
            "kyc_pending" to "सत्यापन लंबित (Pending)",
            "kyc_not_submitted" to "सत्यापित नहीं है",
            "kyc_failed" to "सत्यापन विफल (Failed)",
            "kyc_pan_number" to "पैन कार्ड नंबर",
            "kyc_pan_hint" to "१० अंकों का पैन दर्ज करें (उदा. ABCDE1234F)",
            "kyc_verify_btn" to "पैन सत्यापित करें",
            "kyc_verify_submit" to "सत्यापित करें",
            "kyc_name" to "पैन पंजीकृत नाम",
            "kyc_required_msg" to "अंकों को रिडीम करने के लिए केवाईसी सत्यापन आवश्यक है। कृपया पहले अपना पैन कार्ड सत्यापित करें।",
            "kyc_placeholder" to "XXXXX1234X",
            "kyc_dialog_desc" to "कृपया अपना १०-अंकीय पैन कार्ड नंबर दर्ज करें। सुनिश्चित करें कि नाम आपके प्रोफाइल नाम से मेल खाता है।",
            "kyc_success" to "पैन कार्ड सफलतापूर्वक सत्यापित हो गया!"
        )
    )

    val dynamicTranslations = mapOf(
        AppLanguage.HI to mutableStateMapOf<String, String>(),
        AppLanguage.BN to mutableStateMapOf<String, String>(),
        AppLanguage.GU to mutableStateMapOf<String, String>(),
        AppLanguage.KN to mutableStateMapOf<String, String>(),
        AppLanguage.ML to mutableStateMapOf<String, String>(),
        AppLanguage.MR to mutableStateMapOf<String, String>(),
        AppLanguage.OD to mutableStateMapOf<String, String>(),
        AppLanguage.PA to mutableStateMapOf<String, String>(),
        AppLanguage.TA to mutableStateMapOf<String, String>(),
        AppLanguage.TE to mutableStateMapOf<String, String>(),
        AppLanguage.AS to mutableStateMapOf<String, String>()
    )

    private val pendingRequests = mutableSetOf<Pair<AppLanguage, String>>()

    fun get(key: String, lang: AppLanguage): String {
        if (lang == AppLanguage.EN) {
            return translations[AppLanguage.EN]?.get(key) ?: key
        }

        // 1. Check hardcoded translations first
        val hardcoded = translations[lang]?.get(key)
        if (hardcoded != null) {
            return hardcoded
        }

        // 2. Check dynamic translations cache
        val langCache = dynamicTranslations[lang]
        if (langCache != null) {
            val cachedValue = langCache[key]
            if (cachedValue != null) {
                return cachedValue
            }
        }

        // 3. Fallback to English source text as baseline to translate
        val englishText = translations[AppLanguage.EN]?.get(key) ?: key
        triggerTranslation(key, englishText, lang)
        return englishText
    }

    private fun triggerTranslation(key: String, text: String, lang: AppLanguage) {
        val requestKey = Pair(lang, key)
        synchronized(pendingRequests) {
            if (pendingRequests.contains(requestKey)) return
            pendingRequests.add(requestKey)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val translated = SarvamTranslator.translateText(text, lang.code)
                if (translated != null) {
                    launch(Dispatchers.Main) {
                        dynamicTranslations[lang]?.put(key, translated)
                        synchronized(pendingRequests) {
                            pendingRequests.remove(requestKey)
                        }
                    }
                } else {
                    synchronized(pendingRequests) {
                        pendingRequests.remove(requestKey)
                    }
                }
            } catch (e: Exception) {
                Log.e("Loc", "Failed to translate key: $key to lang: ${lang.name}", e)
                synchronized(pendingRequests) {
                    pendingRequests.remove(requestKey)
                }
            }
        }
    }

    fun getFormatted(key: String, lang: AppLanguage, vararg args: Any): String {
        val format = get(key, lang)
        return try {
            String.format(format, *args)
        } catch (e: Exception) {
            format
        }
    }
}
