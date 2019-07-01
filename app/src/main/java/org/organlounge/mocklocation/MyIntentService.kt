package org.organlounge.mocklocation

import android.Manifest
import android.app.IntentService
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.SystemClock
import android.util.Log
import android.util.TimeUtils
import androidx.core.app.JobIntentService
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.concurrent.TimeUnit

class MyIntentService : IntentService("MyIntentService") {

    companion object {
        val TAG: String = MyIntentService::class.java.simpleName
    }

    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    override fun onHandleIntent(intent: Intent) {
        Log.e(TAG, "intent: " + intent.toString())

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "ACCESS_FINE_LOCATION not granted")
            return
        }

        fusedLocationProviderClient.setMockMode(true).addOnSuccessListener {
            Log.e(TAG, "setMockMode success")

            val duration = intent.getIntExtra("duration", 0)
            val times = intent.getIntExtra("times", 0)

            val latFrom = intent.getFloatExtra("lat_from", 0.0f).toDouble()
            val latTo = intent.getFloatExtra("lat_to", 0.0f).toDouble()

            val lonFrom = intent.getFloatExtra("lon_from", 0.0f).toDouble()
            val lonTo = intent.getFloatExtra("lon_to", 0.0f).toDouble()

            val latTick = (latTo - latFrom) / times
            val lonTick = (lonTo - lonFrom) / times

            for (i in 0..(times - 1)) {
                val lat = latFrom + latTick * i
                val lon = lonFrom + lonTick * i

                val location = Location("fused")
                location.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                location.time = SystemClock.currentThreadTimeMillis()
                location.latitude = lat
                location.longitude = lon
                location.accuracy = 3.0f

                Log.e(TAG, "location: " + location.toString())
                fusedLocationProviderClient.setMockLocation(location).addOnSuccessListener {
                    Log.e(TAG, "setMockLocation success")
                }.addOnFailureListener {
                    Log.e(TAG, "setMockLocation fail: ", it)
                }

                TimeUnit.SECONDS.sleep(duration.toLong())
            }

        }.addOnFailureListener {
            Log.e(TAG, "setMockMode fail: ", it)
        }
    }
}
