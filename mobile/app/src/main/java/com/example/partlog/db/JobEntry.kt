package com.example.partlog.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job_entries")
data class JobEntry(
    @PrimaryKey val id: String,
    val make: String,
    val model: String,
    val variant: String,
    val year: Int,
    val registrationNumber: String?,
    val photoPath1: String?,
    val photoPath2: String?,
    val photoPath3: String?,
    val photoPath4: String?,
    val photoPath5: String?,
    val photoPath6: String?,
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
    var syncStatus: String, // "QUEUED" or "SYNCED"
    val createdAt: Long,
    val componentType: String = "condenser",
    
    // Dynamic OEM logging fields
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
