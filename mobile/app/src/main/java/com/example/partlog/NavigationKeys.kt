package com.example.partlog

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Main : NavKey
@Serializable data object VehicleId : NavKey
@Serializable data object PhotoCapture : NavKey
@Serializable data object FailureCause : NavKey
@Serializable data object Confirmation : NavKey
@Serializable data object SignUp : NavKey
@Serializable data object Login : NavKey
@Serializable data object Landing : NavKey
@Serializable data object ComponentDetails : NavKey
@Serializable data object Measurements : NavKey
@Serializable data object WorkshopDetails : NavKey

