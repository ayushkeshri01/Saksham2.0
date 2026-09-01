package com.example.partlog.sync

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.security.SecureRandom
import java.security.cert.CertificateException
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

interface SarvamTranslateApi {
    @POST("translate")
    suspend fun translate(
        @Header("api-subscription-key") apiKey: String,
        @Body request: TranslateRequest
    ): TranslateResponse
}

data class TranslateRequest(
    val input: String,
    val source_language_code: String,
    val target_language_code: String,
    val model: String = "sarvam-translate:v1"
)

data class TranslateResponse(
    val request_id: String,
    val translated_text: String,
    val source_language_code: String
)

object SarvamTranslator {
    private const val BASE_URL = "https://api.sarvam.ai/"
    private const val API_KEY = "sk_t38lc7b9_RIgYTNzd3ljLnlBgTqU8wCTI"

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        try {
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                        return arrayOf()
                    }
                }
            )

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })

            return builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private val api: SarvamTranslateApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getUnsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SarvamTranslateApi::class.java)
    }

    suspend fun translateText(text: String, targetLanguageCode: String): String? {
        if (text.isBlank()) return text
        try {
            val response = api.translate(
                apiKey = API_KEY,
                request = TranslateRequest(
                    input = text,
                    source_language_code = "en-IN", // English as base source language
                    target_language_code = targetLanguageCode
                )
            )
            return response.translated_text
        } catch (e: Exception) {
            Log.e("SarvamTranslator", "Error translating text '$text' to '$targetLanguageCode'", e)
            return null
        }
    }
}
