package com.example.partlog.sync

data class SyncPayload(
    val id: String,
    val make: String,
    val model: String,
    val variant: String,
    val year: Int,
    val registrationNumber: String?,
    val photoBase64_1: String?,
    val photoBase64_2: String?,
    val photoBase64_3: String?,
    val photoBase64_4: String?,
    val photoBase64_5: String?,
    val photoBase64_6: String?,
    val gpsLatitude: Double,
    val gpsLongitude: Double,
    val timestamp: Long,
    val failureCause: String,
    val severity: String,
    val odometer: Int?,
    val acUsage: String?,
    val priorServiceDate: String?,
    val notes: String?,
    val mechanicId: String,
    val createdAt: Long,
    
    val fuelType: String? = null,
    val condenserCondition: String? = null,
    val condenserReplacement: String? = null,
    val brandInstalled: String? = null,
    val compressorFailureType: String? = null,
    val compressorOilPresent: String? = null,
    val highSidePressure: String? = null,
    val lowSidePressure: String? = null,
    val ambientTemperature: String? = null,
    val coolingTemperature: String? = null,
    val workshopCity: String? = null,
    val condenserReplacementCount: String? = null,
    val currentMileage: String? = null
)

data class MechanicPayload(
    val id: String,
    val name: String,
    val workshop: String,
    val mobile: String,
    val password: String,
    val dob: String? = null,
    val city: String? = null
)

data class ProfileUpdatePayload(
    val workshop: String,
    val city: String
)

data class LoginPayload(
    val id: String,
    val password: String
)

data class OtpLoginPayload(
    val accessToken: String,
    val mobile: String
)

data class LoginResponse(
    val success: Boolean,
    val mechanic: MechanicInfo
)

data class MechanicInfo(
    val id: String?,
    val name: String?,
    val workshop: String?,
    val mobile: String?,
    val points: Int?,
    val dob: String? = null,
    val city: String? = null,
    val panNumber: String? = null,
    val panStatus: String? = null,
    val panName: String? = null,
    val payoutMethod: String? = null,
    val upiHandle: String? = null,
    val bankAccountNumber: String? = null,
    val bankIfsc: String? = null,
    val accountHolderName: String? = null,
    val easebuzzBeneficiaryStatus: String? = null
)

data class KycPayload(
    val panNumber: String
)

data class KycResponse(
    val success: Boolean,
    val panNumber: String?,
    val panStatus: String?,
    val panName: String?,
    val message: String? = null
)

data class CheckMechanicResponse(
    val exists: Boolean,
    val mechanic: MechanicBriefInfo? = null
)

data class MechanicBriefInfo(
    val id: String?,
    val name: String?
)

data class PayoutDetailsPayload(
    val payoutMethod: String,
    val upiHandle: String? = null,
    val accountHolderName: String? = null,
    val bankAccountNumber: String? = null,
    val bankIfsc: String? = null
)

data class PayoutDetailsResponse(
    val success: Boolean,
    val message: String? = null,
    val mechanic: MechanicInfo? = null
)

data class RedeemPayload(
    val pointsToRedeem: Int
)

data class RedeemResponse(
    val success: Boolean,
    val message: String? = null,
    val newPointsBalance: Int? = null,
    val transfer: RedeemTransferInfo? = null
)

data class RedeemTransferInfo(
    val transferId: String? = null,
    val status: String? = null,
    val uniqueRequestNumber: String? = null
)
