package com.example.partlog.sync

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.partlog.db.AppDatabase
import com.example.partlog.db.JobEntry
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.jobEntryDao()
        val unsynced = dao.getEntriesBySyncStatus("QUEUED")

        if (unsynced.isEmpty()) {
            return Result.success()
        }

        Log.d("SyncWorker", "Found ${unsynced.size} unsynced entries to upload")

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(PartLogApi::class.java)
        var hasFailure = false

        for (entry in unsynced) {
            try {
                val payload = SyncPayload(
                    id = entry.id,
                    make = entry.make,
                    model = entry.model,
                    variant = entry.variant,
                    year = entry.year,
                    registrationNumber = entry.registrationNumber,
                    photoBase64_1 = fileToBase64(entry.photoPath1),
                    photoBase64_2 = fileToBase64(entry.photoPath2),
                    photoBase64_3 = fileToBase64(entry.photoPath3),
                    photoBase64_4 = fileToBase64(entry.photoPath4),
                    photoBase64_5 = fileToBase64(entry.photoPath5),
                    photoBase64_6 = fileToBase64(entry.photoPath6),
                    gpsLatitude = entry.gpsLatitude,
                    gpsLongitude = entry.gpsLongitude,
                    timestamp = entry.timestamp,
                    failureCause = entry.failureCause,
                    severity = entry.severity,
                    odometer = entry.odometer,
                    acUsage = entry.acUsage,
                    priorServiceDate = entry.priorServiceDate,
                    notes = entry.notes,
                    mechanicId = entry.mechanicId,
                    createdAt = entry.createdAt,
                    fuelType = entry.fuelType,
                    condenserCondition = entry.condenserCondition,
                    condenserReplacement = entry.condenserReplacement,
                    brandInstalled = entry.brandInstalled,
                    compressorFailureType = entry.compressorFailureType,
                    compressorOilPresent = entry.compressorOilPresent,
                    highSidePressure = entry.highSidePressure,
                    lowSidePressure = entry.lowSidePressure,
                    ambientTemperature = entry.ambientTemperature,
                    coolingTemperature = entry.coolingTemperature,
                    workshopCity = entry.workshopCity,
                    condenserReplacementCount = entry.condenserReplacementCount,
                    currentMileage = entry.currentMileage
                )

                val response = if (entry.componentType == "compressor") {
                    api.syncCompressorEntry(payload)
                } else {
                    api.syncEntry(payload)
                }
                if (response.isSuccessful) {
                    dao.updateSyncStatus(entry.id, "SYNCED")
                    Log.d("SyncWorker", "Successfully synced entry: ${entry.id}")
                } else {
                    Log.e("SyncWorker", "Failed to sync entry ${entry.id}: ${response.errorBody()?.string()}")
                    hasFailure = true
                }
            } catch (e: Exception) {
                Log.e("SyncWorker", "Exception syncing entry ${entry.id}", e)
                hasFailure = true
            }
        }

        return if (hasFailure) {
            Result.retry()
        } else {
            Result.success()
        }
    }

    private fun fileToBase64(filePath: String?): String? {
        if (filePath == null) return null
        val file = File(filePath)
        if (!file.exists()) return null
        return try {
            val bytes = file.readBytes()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }
}
