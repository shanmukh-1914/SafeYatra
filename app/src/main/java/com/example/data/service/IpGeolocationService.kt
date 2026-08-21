package com.example.data.service

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class IpLocationResult(
    val ip: String,
    val city: String,
    val region: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val isSuccess: Boolean = true
)

class IpGeolocationService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(4, TimeUnit.SECONDS)
        .build()
) {
    private val TAG = "IpGeolocationService"

    /**
     * Resolves the user's real public location via IP Geolocation APIs.
     * Tries primary and fallback endpoints for robust instant location resolution.
     */
    suspend fun getIpLocation(): IpLocationResult? = withContext(Dispatchers.IO) {
        // Attempt 1: ipapi.co (detailed & fast)
        try {
            val request = Request.Builder()
                .url("https://ipapi.co/json/")
                .header("User-Agent", "SafeYatraApp/1.0")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            if (response.isSuccessful && !body.isNullOrBlank()) {
                val json = JSONObject(body)
                val lat = json.optDouble("latitude", Double.NaN)
                val lng = json.optDouble("longitude", Double.NaN)
                val city = json.optString("city", "").ifBlank { json.optString("region", "") }
                val region = json.optString("region", "")
                val country = json.optString("country_name", "India")
                val ip = json.optString("ip", "")

                if (!lat.isNaN() && !lng.isNaN() && (lat != 0.0 || lng != 0.0)) {
                    Log.d(TAG, "Resolved IP Geolocation: $city, $region, $country ($lat, $lng)")
                    return@withContext IpLocationResult(
                        ip = ip,
                        city = city.ifBlank { "Live City" },
                        region = region,
                        country = country,
                        latitude = lat,
                        longitude = lng
                    )
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "ipapi.co query notice: ${e.message}")
        }

        // Attempt 2: freeipapi.com fallback
        try {
            val request = Request.Builder()
                .url("https://freeipapi.com/api/json")
                .header("User-Agent", "SafeYatraApp/1.0")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            if (response.isSuccessful && !body.isNullOrBlank()) {
                val json = JSONObject(body)
                val lat = json.optDouble("latitude", Double.NaN)
                val lng = json.optDouble("longitude", Double.NaN)
                val cityName = json.optString("cityName", "").ifBlank { json.optString("regionName", "") }
                val regionName = json.optString("regionName", "")
                val countryName = json.optString("countryName", "India")
                val ipAddress = json.optString("ipAddress", "")

                if (!lat.isNaN() && !lng.isNaN() && (lat != 0.0 || lng != 0.0)) {
                    Log.d(TAG, "Resolved freeipapi Geolocation: $cityName, $regionName ($lat, $lng)")
                    return@withContext IpLocationResult(
                        ip = ipAddress,
                        city = cityName.ifBlank { "Live City" },
                        region = regionName,
                        country = countryName,
                        latitude = lat,
                        longitude = lng
                    )
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "freeipapi.com fallback notice: ${e.message}")
        }

        null
    }
}
