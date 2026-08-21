package com.example.data.service

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

data class GeocodeResult(
    val latitude: Double,
    val longitude: Double,
    val displayName: String,
    val city: String = "",
    val state: String = "",
    val country: String = "India"
)

class NominatimGeocodingService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(4, TimeUnit.SECONDS)
        .build()
) {
    private val TAG = "GeocodingService"
    private val memoryCache = ConcurrentHashMap<String, GeocodeResult>()

    /**
     * Search multiple locations matching a user query with instant fallback and OSM search
     */
    suspend fun searchLocations(query: String): List<GeocodeResult> = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return@withContext emptyList()

        val results = mutableListOf<GeocodeResult>()

        // 1. Check curated instant hub matches
        val lower = trimmed.lowercase()
        getPredefinedHubs().filter {
            it.displayName.lowercase().contains(lower) ||
            it.city.lowercase().contains(lower) ||
            it.state.lowercase().contains(lower)
        }.forEach {
            results.add(it)
        }

        // 2. Query Nominatim API for live autocomplete
        try {
            val encoded = URLEncoder.encode(trimmed, "UTF-8")
            val url = "https://nominatim.openstreetmap.org/search?q=$encoded&format=json&addressdetails=1&limit=6"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "SafeYatra-TravelSafety-App/1.0 (travelsafety@safeyatra.org)")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && !body.isNullOrBlank()) {
                val jsonArray = JSONArray(body)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val lat = obj.getDouble("lat")
                    val lon = obj.getDouble("lon")
                    val name = obj.optString("display_name", trimmed)
                    val addr = obj.optJSONObject("address") ?: JSONObject()

                    val city = addr.optString("city", "").ifBlank {
                        addr.optString("town", "").ifBlank {
                            addr.optString("suburb", "").ifBlank {
                                addr.optString("county", "")
                            }
                        }
                    }
                    val state = addr.optString("state", "")
                    val country = addr.optString("country", "India")

                    val item = GeocodeResult(
                        latitude = lat,
                        longitude = lon,
                        displayName = name,
                        city = if (city.isNotBlank()) city else trimmed,
                        state = state,
                        country = country
                    )
                    if (results.none { Math.abs(it.latitude - lat) < 0.01 && Math.abs(it.longitude - lon) < 0.01 }) {
                        results.add(item)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Nominatim search query notice: ${e.message}")
        }

        if (results.isEmpty()) {
            results.add(geocodeDestination(trimmed))
        }

        results
    }

    suspend fun geocodeDestination(query: String): GeocodeResult = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) {
            return@withContext GeocodeResult(28.6139, 77.2090, "New Delhi, India", "New Delhi", "Delhi")
        }

        val cacheKey = trimmed.lowercase()
        memoryCache[cacheKey]?.let { return@withContext it }

        // Contextual coordinate heuristics for instant lookup of top travel hubs
        val predefined = getPredefinedHubs().firstOrNull {
            cacheKey in it.displayName.lowercase() || cacheKey in it.city.lowercase()
        }
        if (predefined != null) {
            memoryCache[cacheKey] = predefined
            return@withContext predefined
        }

        try {
            val encoded = URLEncoder.encode(trimmed, "UTF-8")
            val url = "https://nominatim.openstreetmap.org/search?q=$encoded&format=json&addressdetails=1&limit=1"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "SafeYatra-TravelSafety-App/1.0 (travelsafety@safeyatra.org)")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && !body.isNullOrBlank()) {
                val jsonArray = JSONArray(body)
                if (jsonArray.length() > 0) {
                    val first = jsonArray.getJSONObject(0)
                    val lat = first.getDouble("lat")
                    val lon = first.getDouble("lon")
                    val name = first.optString("display_name", trimmed)
                    val addr = first.optJSONObject("address") ?: JSONObject()
                    val city = addr.optString("city", "").ifBlank {
                        addr.optString("town", "").ifBlank {
                            addr.optString("suburb", trimmed)
                        }
                    }
                    val state = addr.optString("state", "")
                    val country = addr.optString("country", "India")

                    val result = GeocodeResult(lat, lon, name, city, state, country)
                    memoryCache[cacheKey] = result
                    return@withContext result
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Nominatim geocoding fast-fallback for '$trimmed': ${e.message}")
        }

        val fallbackResult = GeocodeResult(28.6139, 77.2090, trimmed, trimmed, "")
        memoryCache[cacheKey] = fallbackResult
        fallbackResult
    }

    suspend fun reverseGeocode(lat: Double, lng: Double): String = withContext(Dispatchers.IO) {
        val cacheKey = "rev_${(lat * 100).toInt()}_${(lng * 100).toInt()}"
        val heuristicCity = when {
            lat in 17.2..17.6 && lng in 78.2..78.7 -> "Hyderabad, Telangana"
            lat in 12.8..13.2 && lng in 77.4..77.8 -> "Bengaluru, Karnataka"
            lat in 18.8..19.4 && lng in 72.7..73.2 -> "Mumbai, Maharashtra"
            lat in 28.3..28.9 && lng in 76.8..77.5 -> "New Delhi, Delhi"
            lat in 12.9..13.3 && lng in 80.1..80.4 -> "Chennai, Tamil Nadu"
            lat in 22.4..22.8 && lng in 88.2..88.6 -> "Kolkata, West Bengal"
            lat in 26.7..27.2 && lng in 75.6..76.1 -> "Jaipur, Rajasthan"
            lat in 15.0..15.8 && lng in 73.6..74.4 -> "Goa, India"
            lat in 27.1..27.3 && lng in 77.9..78.2 -> "Agra, Uttar Pradesh"
            lat in 25.2..25.4 && lng in 82.9..83.1 -> "Varanasi, Uttar Pradesh"
            lat in 9.8..10.1 && lng in 76.1..76.5 -> "Kochi, Kerala"
            lat in 32.1..32.4 && lng in 77.1..77.3 -> "Manali, Himachal Pradesh"
            lat in 34.0..34.3 && lng in 77.4..77.8 -> "Leh, Ladakh"
            lat in 31.5..31.8 && lng in 74.7..75.0 -> "Amritsar, Punjab"
            lat in 24.4..24.8 && lng in 73.5..73.9 -> "Udaipur, Rajasthan"
            lat in 48.7..48.9 && lng in 2.2..2.5 -> "Paris, France"
            lat in 51.4..51.6 && lng in -0.3..0.1 -> "London, UK"
            lat in 35.5..35.8 && lng in 139.5..139.9 -> "Tokyo, Japan"
            lat in 40.6..40.9 && lng in -74.1..-73.8 -> "New York, USA"
            lat in 25.1..25.4 && lng in 55.1..55.4 -> "Dubai, UAE"
            lat in 1.2..1.5 && lng in 103.7..104.0 -> "Singapore"
            else -> "Live Traveler Location"
        }

        try {
            val url = "https://nominatim.openstreetmap.org/reverse?lat=$lat&lon=$lng&format=json&addressdetails=1"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "SafeYatra-TravelSafety-App/1.0 (travelsafety@safeyatra.org)")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            if (response.isSuccessful && !body.isNullOrBlank()) {
                val json = JSONObject(body)
                val address = json.optJSONObject("address")
                val road = address?.optString("road", "") ?: ""
                val suburb = address?.optString("suburb", "") ?: ""
                val cityRaw = address?.optString("city", "") ?: ""
                val townRaw = address?.optString("town", "") ?: ""
                val stateRaw = address?.optString("state", "") ?: ""
                val city = if (cityRaw.isNotBlank()) cityRaw else if (townRaw.isNotBlank()) townRaw else if (suburb.isNotBlank()) suburb else stateRaw
                val state = stateRaw
                val country = address?.optString("country", "") ?: ""

                val formatted = when {
                    suburb.isNotBlank() && city.isNotBlank() -> "$suburb, $city"
                    road.isNotBlank() && city.isNotBlank() -> "$road, $city"
                    city.isNotBlank() && state.isNotBlank() -> "$city, $state"
                    city.isNotBlank() -> city
                    else -> heuristicCity
                }
                if (formatted.isNotBlank()) {
                    return@withContext formatted
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Reverse geocoding notice: ${e.message}")
        }

        heuristicCity
    }

    fun getPredefinedHubs(): List<GeocodeResult> {
        return listOf(
            GeocodeResult(17.3850, 78.4867, "Hyderabad (Charminar, Banjara Hills, Hitec City)", "Hyderabad", "Telangana"),
            GeocodeResult(12.9716, 77.5946, "Bengaluru (MG Road, Indiranagar, Koramangala)", "Bengaluru", "Karnataka"),
            GeocodeResult(28.6139, 77.2090, "New Delhi (Connaught Place, India Gate)", "New Delhi", "Delhi"),
            GeocodeResult(19.0760, 72.8777, "Mumbai (Marine Drive, Colaba, Bandra)", "Mumbai", "Maharashtra"),
            GeocodeResult(13.0827, 80.2707, "Chennai (Marina Beach, T. Nagar)", "Chennai", "Tamil Nadu"),
            GeocodeResult(22.5726, 88.3639, "Kolkata (Park Street, Victoria Memorial)", "Kolkata", "West Bengal"),
            GeocodeResult(26.9124, 75.7873, "Jaipur (Hawa Mahal, Pink City, Amer)", "Jaipur", "Rajasthan"),
            GeocodeResult(15.2993, 74.1240, "Goa (Calangute, Baga Beach, Panaji)", "Goa", "Goa"),
            GeocodeResult(27.1767, 78.0081, "Agra (Taj Mahal, Agra Fort)", "Agra", "Uttar Pradesh"),
            GeocodeResult(25.3176, 82.9739, "Varanasi (Dashashwamedh Ghat, Kashi)", "Varanasi", "Uttar Pradesh"),
            GeocodeResult(9.9312, 76.2673, "Kochi / Cochin (Fort Kochi, Backwaters)", "Kochi", "Kerala"),
            GeocodeResult(32.2432, 77.1892, "Manali (Solang Valley, Mall Road)", "Manali", "Himachal Pradesh"),
            GeocodeResult(34.1526, 77.5771, "Leh Ladakh (Pangong Tso, Nubra Valley)", "Leh", "Ladakh"),
            GeocodeResult(31.6340, 74.8723, "Amritsar (Golden Temple)", "Amritsar", "Punjab"),
            GeocodeResult(24.5854, 73.7125, "Udaipur (City Palace, Lake Pichola)", "Udaipur", "Rajasthan"),
            GeocodeResult(48.8566, 2.3522, "Paris, France (Eiffel Tower, Louvre)", "Paris", "Île-de-France", "France"),
            GeocodeResult(51.5074, -0.1278, "London, United Kingdom (Big Ben, Soho)", "London", "England", "UK"),
            GeocodeResult(35.6762, 139.6503, "Tokyo, Japan (Shibuya, Shinjuku)", "Tokyo", "Kanto", "Japan"),
            GeocodeResult(40.7128, -74.0060, "New York, USA (Manhattan, Times Square)", "New York", "NY", "USA"),
            GeocodeResult(25.2048, 55.2708, "Dubai, United Arab Emirates (Burj Khalifa)", "Dubai", "Dubai", "UAE"),
            GeocodeResult(1.3521, 103.8198, "Singapore (Marina Bay Sands, Orchard)", "Singapore", "Singapore", "Singapore")
        )
    }
}
