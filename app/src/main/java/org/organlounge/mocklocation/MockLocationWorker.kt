package org.organlounge.mocklocation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.SystemClock
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class MockLocationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "MockLocationWorker"
        const val KEY_LAT_FROM = "lat_from"
        const val KEY_LON_FROM = "lon_from"
        const val KEY_LAT_TO = "lat_to"
        const val KEY_LON_TO = "lon_to"
        const val KEY_DURATION = "duration"
        const val KEY_TIMES = "times"
    }

    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    override suspend fun doWork(): Result {
        Log.i(TAG, "doWork started with params: $inputData")

        // Check location permission
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "ACCESS_FINE_LOCATION not granted")
            return Result.failure()
        }

        return try {
            // Get input parameters
            val latFrom = inputData.getDouble(KEY_LAT_FROM, 0.0)
            val lonFrom = inputData.getDouble(KEY_LON_FROM, 0.0)
            val latTo = inputData.getDouble(KEY_LAT_TO, 0.0)
            val lonTo = inputData.getDouble(KEY_LON_TO, 0.0)
            val duration = inputData.getInt(KEY_DURATION, 0)
            val times = inputData.getInt(KEY_TIMES, 0)

            if (times <= 0) {
                Log.e(TAG, "Invalid times parameter: $times")
                return Result.failure()
            }

            // Enable mock mode
            fusedLocationProviderClient.setMockMode(true).await()
            Log.i(TAG, "setMockMode success")

            try {
                // Calculate increments
                val latTick = (latTo - latFrom) / times
                val lonTick = (lonTo - lonFrom) / times

                // Update mock location incrementally
                for (i in 0 until times) {
                    val lat = latFrom + latTick * i
                    val lon = lonFrom + lonTick * i

                    val location = Location("fused").apply {
                        elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                        time = System.currentTimeMillis()
                        latitude = lat
                        longitude = lon
                        accuracy = 3.0f
                    }

                    Log.d(TAG, "Setting mock location [${i+1}/$times]: $location")

                    try {
                        fusedLocationProviderClient.setMockLocation(location).await()
                        Log.d(TAG, "setMockLocation success")
                    } catch (e: Exception) {
                        Log.e(TAG, "setMockLocation failed", e)
                    }

                    // Wait for the specified duration before next update
                    if (i < times - 1) {
                        delay(duration * 1000L)
                    }
                }

                Result.success()
            } finally {
                // Disable mock mode
                try {
                    fusedLocationProviderClient.setMockMode(false).await()
                    Log.i(TAG, "setMockMode disabled")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to disable mock mode", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Mock location update failed", e)
            Result.failure()
        }
    }
}
