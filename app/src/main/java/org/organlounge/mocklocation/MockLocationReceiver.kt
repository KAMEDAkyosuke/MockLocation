package org.organlounge.mocklocation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class MockLocationReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "MockLocationReceiver"
        const val ACTION_UPDATE_MOCK_LOCATION = "org.organlounge.mocklocation.UPDATE_MOCK_LOCATION"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        Log.i(TAG, "Received intent: ${intent.action}")

        if (intent.action == ACTION_UPDATE_MOCK_LOCATION) {
            // Extract parameters from intent
            val latFrom = intent.getFloatExtra("lat_from", 0.0f).toDouble()
            val lonFrom = intent.getFloatExtra("lon_from", 0.0f).toDouble()
            val latTo = intent.getFloatExtra("lat_to", 0.0f).toDouble()
            val lonTo = intent.getFloatExtra("lon_to", 0.0f).toDouble()
            val duration = intent.getIntExtra("duration", 0)
            val times = intent.getIntExtra("times", 0)

            // Create input data for Worker
            val inputData = Data.Builder()
                .putDouble(MockLocationWorker.KEY_LAT_FROM, latFrom)
                .putDouble(MockLocationWorker.KEY_LON_FROM, lonFrom)
                .putDouble(MockLocationWorker.KEY_LAT_TO, latTo)
                .putDouble(MockLocationWorker.KEY_LON_TO, lonTo)
                .putInt(MockLocationWorker.KEY_DURATION, duration)
                .putInt(MockLocationWorker.KEY_TIMES, times)
                .build()

            // Create and enqueue work request
            val workRequest = OneTimeWorkRequestBuilder<MockLocationWorker>()
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)

            Log.i(TAG, "Mock location work enqueued with params: " +
                    "from=($latFrom,$lonFrom) to=($latTo,$lonTo) " +
                    "duration=$duration times=$times")
        }
    }
}
