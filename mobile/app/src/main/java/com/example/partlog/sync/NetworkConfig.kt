package com.example.partlog.sync

import android.os.Build

object NetworkConfig {
    // 10.0.2.2 is the standard Android emulator loopback to host machine
    private const val EMULATOR_URL = "http://10.0.2.2:5000/"

    // Connect via the public HTTPS backend on the Azure VPS (self-signed cert bundled in-app)
    private const val PHYSICAL_DEVICE_URL = "https://20.219.202.99/"

    val BASE_URL: String
        get() {
            val fingerprint = Build.FINGERPRINT.lowercase()
            val model = Build.MODEL.lowercase()
            val brand = Build.BRAND.lowercase()
            val device = Build.DEVICE.lowercase()
            val manufacturer = Build.MANUFACTURER.lowercase()
            val product = Build.PRODUCT.lowercase()
            val hardware = Build.HARDWARE.lowercase()

            val isEmulator = fingerprint.contains("generic")
                    || fingerprint.contains("unknown")
                    || fingerprint.contains("emulator")
                    || fingerprint.contains("sdk_gphone")       // Pixel 9 API 35: google/sdk_gphone64_x86_64/...
                    || model.contains("google_sdk")
                    || model.contains("emulator")
                    || model.contains("android sdk built for x86")
                    || model.contains("sdk_gphone")             // Pixel 9 API 35 model: sdk_gphone64_x86_64
                    || manufacturer.contains("genymotion")
                    || hardware.contains("goldfish")
                    || hardware.contains("ranchu")              // Pixel 9 API 35 hardware: ranchu
                    || product.contains("sdk_gphone")           // Pixel 9 API 35 product: sdk_gphone64_x86_64
                    || product.contains("google_sdk")
                    || product.contains("sdk_google")
                    || device.contains("emu64")                 // Pixel 9 API 35 device: emu64xa
                    || device.contains("generic")
                    || (brand == "google" && fingerprint.contains("sdk_gphone"))
                    || (brand.startsWith("generic") && device.startsWith("generic"))

            return if (isEmulator) EMULATOR_URL else PHYSICAL_DEVICE_URL
        }
}
