package com.example.partlog.sync

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path

interface PartLogApi {
    @GET("api/mechanics/check/{mobile}")
    suspend fun checkMechanicExists(@Path("mobile") mobile: String): Response<CheckMechanicResponse>

    @POST("api/sync")
    suspend fun syncEntry(@Body payload: SyncPayload): Response<Unit>

    @POST("api/compressor/sync")
    suspend fun syncCompressorEntry(@Body payload: SyncPayload): Response<Unit>

    @POST("api/mechanics")
    suspend fun registerMechanic(@Body payload: MechanicPayload): Response<Unit>

    @POST("api/mechanics/login")
    suspend fun loginMechanic(@Body payload: LoginPayload): Response<LoginResponse>

    @POST("api/mechanics/login-otp")
    suspend fun loginMechanicOtp(@Body payload: OtpLoginPayload): Response<LoginResponse>

    @retrofit2.http.PUT("api/mechanics/{id}")
    suspend fun updateMechanicProfile(
        @Path("id") id: String,
        @Body payload: ProfileUpdatePayload
    ): Response<LoginResponse>

    @POST("api/mechanics/{id}/kyc")
    suspend fun verifyKycPan(
        @Path("id") id: String,
        @Body payload: KycPayload
    ): Response<KycResponse>

    @retrofit2.http.PUT("api/mechanics/{id}/payout-details")
    suspend fun updatePayoutDetails(
        @Path("id") id: String,
        @Body payload: PayoutDetailsPayload
    ): Response<PayoutDetailsResponse>

    @POST("api/mechanics/{id}/redeem")
    suspend fun redeemPoints(
        @Path("id") id: String,
        @Body payload: RedeemPayload
    ): Response<RedeemResponse>
}
