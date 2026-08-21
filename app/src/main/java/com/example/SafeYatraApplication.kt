package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class SafeYatraApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:651029084406:android:c805ffe4427f49cf")
                    .setApiKey("AIzaSySafeYatraFallbackLocalApiKey001")
                    .setProjectId("safeyatra-app")
                    .build()
                FirebaseApp.initializeApp(this, options)
                Log.d("SafeYatraApplication", "FirebaseApp initialized with fallback configuration.")
            }
        } catch (e: Throwable) {
            Log.e("SafeYatraApplication", "Gracefully handled FirebaseApp initialization: ${e.message}", e)
        }
    }
}
