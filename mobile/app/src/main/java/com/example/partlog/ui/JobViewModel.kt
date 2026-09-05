package com.example.partlog.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.partlog.db.AppDatabase
import com.example.partlog.db.JobEntry
import com.example.partlog.sync.NetworkConfig
import com.example.partlog.sync.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

class JobViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val dao = database.jobEntryDao()
    private val prefs = getApplication<Application>().getSharedPreferences("partlog_prefs", Context.MODE_PRIVATE)

    // Language State
    val language = mutableStateOf(AppLanguage.EN)

    // Whether a language switch is in progress (translations still loading)
    var isLanguageSwitching = mutableStateOf(false)
        private set

    fun changeLanguage(lang: AppLanguage) {
        if (lang == language.value) return
        isLanguageSwitching.value = true
        language.value = lang
        viewModelScope.launch {
            // Let composition re-evaluate Loc.get(...) and fire any Sarvam translation
            // requests for the new language, then wait for them to settle.
            delay(150)
            val start = System.currentTimeMillis()
            val timeoutMs = 20000L
            while (Loc.pendingTranslationCount.value > 0 && System.currentTimeMillis() - start < timeoutMs) {
                delay(100)
            }
            // Keep the loader visible briefly even for English/cached languages so the
            // user notices the switch
            val minVisibleMs = 600L
            val visibleMs = System.currentTimeMillis() - start + 150
            if (visibleMs < minVisibleMs) {
                delay(minVisibleMs - visibleMs)
            }
            isLanguageSwitching.value = false
        }
    }

    // Current Mechanic Session Flow
    private val currentMechanicId = MutableStateFlow(prefs.getString("mechanic_id", "") ?: "")

    // Entries Flow filtered by current logged-in mechanic ID
    val entries = dao.getAllEntriesFlow().combine(currentMechanicId) { list, mechId ->
        list.filter { it.mechanicId == mechId }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Points (10 points per normal entry, 20 points for Pranav Condenser / Sanden Compressor)
    val points = entries.map { entryList ->
        entryList.sumOf { entry ->
            if (entry.componentType == "condenser") {
                if (entry.brandInstalled?.equals("pranav", ignoreCase = true) == true) 20 else 10
            } else {
                if (entry.brandInstalled?.equals("sanden", ignoreCase = true) == true) 20 else 10
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Redeemed Points State
    var redeemedPoints = mutableStateOf(prefs.getInt("redeemed_points", 0))

    fun redeemPoints(pts: Int) {
        val currentRedeemed = prefs.getInt("redeemed_points", 0)
        prefs.edit().putInt("redeemed_points", currentRedeemed + pts).apply()
        redeemedPoints.value = currentRedeemed + pts
    }

    // Draft State
    var componentType = mutableStateOf("condenser")
    var make = mutableStateOf("Maruti Suzuki")
    var model = mutableStateOf("")
    var variant = mutableStateOf("")
    var year = mutableStateOf(2022)
    var registrationNumber = mutableStateOf("")
    
    var photoPath1 = mutableStateOf<String?>(null)
    var photoPath2 = mutableStateOf<String?>(null)
    var photoPath3 = mutableStateOf<String?>(null)
    var photoPath4 = mutableStateOf<String?>(null)
    var photoPath5 = mutableStateOf<String?>(null)
    var photoPath6 = mutableStateOf<String?>(null)

    var gpsLatitude = mutableStateOf(0.0)
    var gpsLongitude = mutableStateOf(0.0)
    var gpsCaptured = mutableStateOf(false)
    var gpsAcquiring = mutableStateOf(false)
    var timestamp = mutableStateOf(0L)

    var failureCause = mutableStateOf("")
    var severity = mutableStateOf("") // "Minor", "Major", "Total loss"
    
    var odometer = mutableStateOf("")
    var acUsage = mutableStateOf("") // "Daily", "Occasional", "Rarely"
    var priorServiceDate = mutableStateOf("")
    var notes = mutableStateOf("")

    // New Dynamic OEM logging fields
    var fuelType = mutableStateOf("Petrol")
    var condenserCondition = mutableStateOf("Leak")
    var condenserReplacement = mutableStateOf("Aftermarket")
    var brandInstalled = mutableStateOf("Pranav")
    var compressorFailureType = mutableStateOf("Seized")
    var compressorOilPresent = mutableStateOf("Yes")
    var highSidePressure = mutableStateOf("")
    var lowSidePressure = mutableStateOf("")
    var ambientTemperature = mutableStateOf("")
    var coolingTemperature = mutableStateOf("")
    var workshopCity = mutableStateOf("")
    var condenserReplacementCount = mutableStateOf("1st Time")
    var currentMileage = mutableStateOf("")

    // Session Management and Mechanic State

    var mechanicId = mutableStateOf(prefs.getString("mechanic_id", "") ?: "")
    var mechanicName = mutableStateOf(prefs.getString("mechanic_name", "") ?: "")
    var mechanicWorkshop = mutableStateOf(prefs.getString("mechanic_workshop", "") ?: "")
    var mechanicDob = mutableStateOf(prefs.getString("mechanic_dob", "") ?: "")
    var mechanicCity = mutableStateOf(prefs.getString("mechanic_city", "") ?: "")
    var mechanicPanNumber = mutableStateOf(prefs.getString("mechanic_pan_number", "") ?: "")
    var mechanicPanStatus = mutableStateOf(prefs.getString("mechanic_pan_status", "NOT_SUBMITTED") ?: "NOT_SUBMITTED")
    var mechanicPanName = mutableStateOf(prefs.getString("mechanic_pan_name", "") ?: "")
    var payoutMethod = mutableStateOf(prefs.getString("payout_method", "upi") ?: "upi")
    var upiHandle = mutableStateOf(prefs.getString("upi_handle", "") ?: "")
    var bankAccountNumber = mutableStateOf(prefs.getString("bank_account_number", "") ?: "")
    var bankIfsc = mutableStateOf(prefs.getString("bank_ifsc", "") ?: "")
    var accountHolderName = mutableStateOf(prefs.getString("account_holder_name", "") ?: "")
    var easebuzzBeneficiaryStatus = mutableStateOf(prefs.getString("easebuzz_beneficiary_status", "NOT_REGISTERED") ?: "NOT_REGISTERED")

    val isUserRegistered: Boolean
        get() = mechanicId.value.isNotBlank()

    fun saveMechanic(
        id: String?, 
        name: String?, 
        workshop: String?, 
        dob: String? = null, 
        city: String? = null,
        panNumber: String? = null,
        panStatus: String? = null,
        panName: String? = null,
        payoutMethodIn: String? = null,
        upiHandleIn: String? = null,
        bankAccountNumberIn: String? = null,
        bankIfscIn: String? = null,
        accountHolderNameIn: String? = null,
        beneficiaryStatusIn: String? = null
    ) {
        val safeId = id ?: ""
        val safeName = name ?: ""
        val safeWorkshop = workshop ?: ""
        val safeDob = dob ?: ""
        val safeCity = city ?: ""
        val safePanNumber = panNumber ?: ""
        val safePanStatus = panStatus ?: "NOT_SUBMITTED"
        val safePanName = panName ?: ""
        val safePayoutMethod = payoutMethodIn ?: "upi"
        val safeUpiHandle = upiHandleIn ?: ""
        val safeBankAccountNumber = bankAccountNumberIn ?: ""
        val safeBankIfsc = bankIfscIn ?: ""
        val safeAccountHolderName = accountHolderNameIn ?: ""
        val safeBeneficiaryStatus = beneficiaryStatusIn ?: "NOT_REGISTERED"
        prefs.edit().apply {
            putString("mechanic_id", safeId)
            putString("mechanic_name", safeName)
            putString("mechanic_workshop", safeWorkshop)
            putString("mechanic_dob", safeDob)
            putString("mechanic_city", safeCity)
            putString("mechanic_pan_number", safePanNumber)
            putString("mechanic_pan_status", safePanStatus)
            putString("mechanic_pan_name", safePanName)
            putString("payout_method", safePayoutMethod)
            putString("upi_handle", safeUpiHandle)
            putString("bank_account_number", safeBankAccountNumber)
            putString("bank_ifsc", safeBankIfsc)
            putString("account_holder_name", safeAccountHolderName)
            putString("easebuzz_beneficiary_status", safeBeneficiaryStatus)
            apply()
        }
        mechanicId.value = safeId
        mechanicName.value = safeName
        mechanicWorkshop.value = safeWorkshop
        mechanicDob.value = safeDob
        mechanicCity.value = safeCity
        mechanicPanNumber.value = safePanNumber
        mechanicPanStatus.value = safePanStatus
        mechanicPanName.value = safePanName
        payoutMethod.value = safePayoutMethod
        upiHandle.value = safeUpiHandle
        bankAccountNumber.value = safeBankAccountNumber
        bankIfsc.value = safeBankIfsc
        accountHolderName.value = safeAccountHolderName
        easebuzzBeneficiaryStatus.value = safeBeneficiaryStatus
        currentMechanicId.value = safeId
    }

    fun registerMechanic(
        id: String,
        name: String,
        workshop: String,
        mobile: String,
        password: String,
        dob: String,
        city: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(NetworkConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(PartLogApi::class.java)

                val response = api.registerMechanic(MechanicPayload(id, name, workshop, mobile, password, dob, city))
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure("Registration failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("JobViewModel", "Error registering mechanic", e)
                onFailure(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun checkMechanicExists(
        mobile: String,
        onResult: (exists: Boolean, error: String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(NetworkConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(PartLogApi::class.java)

                val response = api.checkMechanicExists(mobile)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        onResult(body.exists, null)
                    } else {
                        onResult(false, "Invalid server response")
                    }
                } else {
                    onResult(false, "Server returned: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("JobViewModel", "Error checking mechanic existence", e)
                onResult(false, e.localizedMessage ?: "Network error")
            }
        }
    }

    fun loginMechanic(
        id: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(NetworkConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(PartLogApi::class.java)

                val response = api.loginMechanic(LoginPayload(id, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        val mech = body.mechanic
                        saveMechanic(
                            id = mech.id,
                            name = mech.name,
                            workshop = mech.workshop,
                            dob = mech.dob,
                            city = mech.city,
                            panNumber = mech.panNumber,
                            panStatus = mech.panStatus,
                            panName = mech.panName,
                            payoutMethodIn = mech.payoutMethod,
                            upiHandleIn = mech.upiHandle,
                            bankAccountNumberIn = mech.bankAccountNumber,
                            bankIfscIn = mech.bankIfsc,
                            accountHolderNameIn = mech.accountHolderName,
                            beneficiaryStatusIn = mech.easebuzzBeneficiaryStatus
                        )
                        onSuccess()
                    } else {
                        onFailure("Login response was not successful")
                    }
                } else {
                    onFailure("Login failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("JobViewModel", "Error logging in mechanic", e)
                onFailure(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun loginMechanicOtp(
        accessToken: String,
        mobile: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(NetworkConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(PartLogApi::class.java)

                val response = api.loginMechanicOtp(OtpLoginPayload(accessToken, mobile))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        val mech = body.mechanic
                        saveMechanic(
                            id = mech.id,
                            name = mech.name,
                            workshop = mech.workshop,
                            dob = mech.dob,
                            city = mech.city,
                            panNumber = mech.panNumber,
                            panStatus = mech.panStatus,
                            panName = mech.panName,
                            payoutMethodIn = mech.payoutMethod,
                            upiHandleIn = mech.upiHandle,
                            bankAccountNumberIn = mech.bankAccountNumber,
                            bankIfscIn = mech.bankIfsc,
                            accountHolderNameIn = mech.accountHolderName,
                            beneficiaryStatusIn = mech.easebuzzBeneficiaryStatus
                        )
                        onSuccess()
                    } else {
                        onFailure("Login response was not successful")
                    }
                } else {
                    onFailure("Login failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("JobViewModel", "Error logging in mechanic via OTP", e)
                onFailure(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun updateProfile(
        workshop: String,
        city: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(NetworkConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(PartLogApi::class.java)

                val response = api.updateMechanicProfile(mechanicId.value, ProfileUpdatePayload(workshop, city))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        val mech = body.mechanic
                        saveMechanic(
                            id = mech.id,
                            name = mech.name,
                            workshop = mech.workshop,
                            dob = mech.dob,
                            city = mech.city,
                            panNumber = mech.panNumber,
                            panStatus = mech.panStatus,
                            panName = mech.panName,
                            payoutMethodIn = mech.payoutMethod,
                            upiHandleIn = mech.upiHandle,
                            bankAccountNumberIn = mech.bankAccountNumber,
                            bankIfscIn = mech.bankIfsc,
                            accountHolderNameIn = mech.accountHolderName,
                            beneficiaryStatusIn = mech.easebuzzBeneficiaryStatus
                        )
                        onSuccess()
                    } else {
                        onFailure("Update profile failed: invalid response")
                    }
                } else {
                    onFailure("Update profile failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("JobViewModel", "Error updating profile", e)
                onFailure(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun verifyKycPan(
        panNumber: String,
        onSuccess: (panName: String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(NetworkConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(PartLogApi::class.java)

                val response = api.verifyKycPan(mechanicId.value, KycPayload(panNumber))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        saveMechanic(
                            id = mechanicId.value,
                            name = mechanicName.value,
                            workshop = mechanicWorkshop.value,
                            dob = mechanicDob.value,
                            city = mechanicCity.value,
                            panNumber = body.panNumber,
                            panStatus = body.panStatus,
                            panName = body.panName
                        )
                        onSuccess(body.panName ?: "")
                    } else {
                        onFailure(body?.message ?: "KYC verification failed: invalid response")
                    }
                } else {
                    onFailure("KYC verification failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("JobViewModel", "Error verifying KYC PAN", e)
                onFailure(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun updatePayoutDetails(
        payoutMethod: String,
        upiHandle: String? = null,
        accountHolderName: String? = null,
        bankAccountNumber: String? = null,
        bankIfsc: String? = null,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(NetworkConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(PartLogApi::class.java)

                val response = api.updatePayoutDetails(
                    mechanicId.value,
                    PayoutDetailsPayload(payoutMethod, upiHandle, accountHolderName, bankAccountNumber, bankIfsc)
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success && body.mechanic != null) {
                        val mech = body.mechanic
                        saveMechanic(
                            id = mech.id,
                            name = mech.name,
                            workshop = mech.workshop,
                            dob = mech.dob,
                            city = mech.city,
                            panNumber = mech.panNumber,
                            panStatus = mech.panStatus,
                            panName = mech.panName,
                            payoutMethodIn = mech.payoutMethod,
                            upiHandleIn = mech.upiHandle,
                            bankAccountNumberIn = mech.bankAccountNumber,
                            bankIfscIn = mech.bankIfsc,
                            accountHolderNameIn = mech.accountHolderName,
                            beneficiaryStatusIn = mech.easebuzzBeneficiaryStatus
                        )
                        onSuccess()
                    } else {
                        onFailure(body?.message ?: "Failed to update payout details")
                    }
                } else {
                    val err = response.errorBody()?.string() ?: "Failed"
                    onFailure("Failed to update payout details: $err")
                }
            } catch (e: Exception) {
                Log.e("JobViewModel", "Error updating payout details", e)
                onFailure(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun redeemPointsBackend(
        pointsToRedeem: Int,
        onSuccess: (message: String, newBalance: Int) -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(NetworkConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(PartLogApi::class.java)

                val response = api.redeemPoints(mechanicId.value, RedeemPayload(pointsToRedeem))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        // Update local points cache
                        val currentRedeemed = prefs.getInt("redeemed_points", 0)
                        prefs.edit().putInt("redeemed_points", currentRedeemed + pointsToRedeem).apply()
                        redeemedPoints.value = currentRedeemed + pointsToRedeem

                        onSuccess(body.message ?: "Redemption successful", body.newPointsBalance ?: 0)
                    } else {
                        onFailure(body?.message ?: "Redemption failed")
                    }
                } else {
                    val err = try { response.errorBody()?.string() } catch (_: Exception) { null }
                    val parsed = try { err?.let { com.google.gson.Gson().fromJson(it, com.google.gson.JsonObject::class.java) } } catch (_: Exception) { null }
                    onFailure(parsed?.get("error")?.asString ?: "Redemption failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("JobViewModel", "Error during redemption", e)
                onFailure(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun logout() {
        prefs.edit().apply {
            remove("mechanic_id")
            remove("mechanic_name")
            remove("mechanic_workshop")
            remove("mechanic_dob")
            remove("mechanic_city")
            remove("mechanic_pan_number")
            remove("mechanic_pan_status")
            remove("mechanic_pan_name")
            remove("payout_method")
            remove("upi_handle")
            remove("bank_account_number")
            remove("bank_ifsc")
            remove("account_holder_name")
            remove("easebuzz_beneficiary_status")
            apply()
        }
        mechanicId.value = ""
        mechanicName.value = ""
        mechanicWorkshop.value = ""
        mechanicDob.value = ""
        mechanicCity.value = ""
        mechanicPanNumber.value = ""
        mechanicPanStatus.value = "NOT_SUBMITTED"
        mechanicPanName.value = ""
        payoutMethod.value = "upi"
        upiHandle.value = ""
        bankAccountNumber.value = ""
        bankIfsc.value = ""
        accountHolderName.value = ""
        easebuzzBeneficiaryStatus.value = "NOT_REGISTERED"
        currentMechanicId.value = ""
    }

    fun resetDraft() {
        componentType.value = "condenser"
        make.value = "Maruti Suzuki"
        model.value = ""
        variant.value = ""
        year.value = 2022
        registrationNumber.value = ""
        photoPath1.value = null
        photoPath2.value = null
        photoPath3.value = null
        photoPath4.value = null
        photoPath5.value = null
        photoPath6.value = null
        gpsLatitude.value = 0.0
        gpsLongitude.value = 0.0
        gpsCaptured.value = false
        gpsAcquiring.value = false
        timestamp.value = 0L
        failureCause.value = ""
        severity.value = ""
        odometer.value = ""
        acUsage.value = ""
        priorServiceDate.value = ""
        notes.value = ""

        fuelType.value = "Petrol"
        condenserCondition.value = "Leak"
        condenserReplacement.value = "Aftermarket"
        brandInstalled.value = "Pranav"
        compressorFailureType.value = "Seized"
        compressorOilPresent.value = "Yes"
        highSidePressure.value = ""
        lowSidePressure.value = ""
        ambientTemperature.value = ""
        coolingTemperature.value = ""
        workshopCity.value = ""
        currentMileage.value = ""
        condenserReplacementCount.value = "1st Time"
    }

    @SuppressLint("MissingPermission")
    fun captureGpsAndTimestamp() {
        if (gpsCaptured.value || gpsAcquiring.value) return
        gpsAcquiring.value = true
        timestamp.value = System.currentTimeMillis()

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication<Application>())
        
        try {
            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                gpsAcquiring.value = false
                if (location != null) {
                    gpsLatitude.value = location.latitude
                    gpsLongitude.value = location.longitude
                    gpsCaptured.value = true
                    Log.d("JobViewModel", "GPS Captured: ${location.latitude}, ${location.longitude}")
                } else {
                    // Fallback to last location
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null) {
                            gpsLatitude.value = lastLoc.latitude
                            gpsLongitude.value = lastLoc.longitude
                            gpsCaptured.value = true
                        }
                    }
                }
            }.addOnFailureListener { e ->
                gpsAcquiring.value = false
                Log.e("JobViewModel", "Failed to get location", e)
            }
        } catch (e: SecurityException) {
            gpsAcquiring.value = false
            Log.e("JobViewModel", "Location permission missing", e)
        }
    }

    fun submitJob() {
        val entryId = UUID.randomUUID().toString()
        val odoInt = odometer.value.toIntOrNull()
        
        val jobEntry = JobEntry(
            id = entryId,
            make = make.value,
            model = model.value,
            variant = variant.value,
            year = year.value,
            registrationNumber = registrationNumber.value.ifBlank { null },
            photoPath1 = photoPath1.value,
            photoPath2 = photoPath2.value,
            photoPath3 = photoPath3.value,
            photoPath4 = photoPath4.value,
            photoPath5 = photoPath5.value,
            photoPath6 = photoPath6.value,
            gpsLatitude = gpsLatitude.value,
            gpsLongitude = gpsLongitude.value,
            timestamp = timestamp.value,
            failureCause = failureCause.value,
            severity = severity.value.ifBlank { "Minor" },
            odometer = odoInt,
            acUsage = acUsage.value.ifBlank { null },
            priorServiceDate = priorServiceDate.value.ifBlank { null },
            notes = notes.value.ifBlank { null },
            mechanicId = mechanicId.value,
            syncStatus = "QUEUED",
            createdAt = System.currentTimeMillis(),
            componentType = componentType.value,
            fuelType = fuelType.value,
            condenserCondition = condenserCondition.value,
            condenserReplacement = condenserReplacement.value,
            brandInstalled = brandInstalled.value,
            compressorFailureType = compressorFailureType.value,
            compressorOilPresent = compressorOilPresent.value,
            highSidePressure = highSidePressure.value,
            lowSidePressure = lowSidePressure.value,
            ambientTemperature = ambientTemperature.value,
            coolingTemperature = coolingTemperature.value,
            workshopCity = workshopCity.value,
            condenserReplacementCount = condenserReplacementCount.value,
            currentMileage = currentMileage.value.ifBlank { null }
        )

        viewModelScope.launch {
            dao.insert(jobEntry)
            Log.d("JobViewModel", "Job entry inserted: $entryId")
            triggerBackgroundSync()
        }
    }

    private fun triggerBackgroundSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(getApplication())
            .enqueue(syncRequest)
        Log.d("JobViewModel", "Background sync triggered")
    }
}
