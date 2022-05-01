package com.adrpien.workmanagerapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.work.*
import com.adrpien.workmanagerapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random

class MainActivity : AppCompatActivity() {


    /*
    WorkWanager / Alarm Manager / Service / JobScheduler
    - WorkManager is a background processing library which is used
    to execute background tasks which should run in a guaranteed way but not necessarily immediately.
    WorkManager is the recommended solution for persistent work.
    Work is persistent when it remains scheduled through app restarts and system reboots.
    Because most background processing is best accomplished through persistent work,
    WorkManager is the primary recommended API for background processing.
    - AlarmManager  class provides access to the system alarm services.
    These allow you to schedule your application to be run at some point in the future.
    - Service is an application component that can perform long-running operations in the background.
    It does not provide a user interface. Once started, a service might continue running for some time,
    even after the user switches to another application. Starts immediately
    - Job Scheduler is an API for scheduling various types of jobs
    against the framework that will be executed in your application's own process.

    WorkManager uses JobScheduler if API 23+, or AlarmManager + BroadcastReceiver if not.

    WorkRequest types:
    - OneTimeWorkRequest
    - PeriodicWorkRequest

    How to:
    1. Add depencies to build.gradle file
    2. Our WorkManager needs Worker, so create class inheriting Worker class.
    3. Create WorkManager
    4. Create Constrains object
    5. Create WorkRequest
    6. Add WorkRequest to queue

     */


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Creating Worker
        val workManager = WorkManager.getInstance(applicationContext)

        // Creating Button listener
        binding.submitButton.setOnClickListener {

            var isCharging = false
            var isBatteryLevelHigh = false
            var isNetworkEnabled = false

            if(binding.batteryChargingCheckBox.isChecked) isCharging = true
            if(binding.batteryLevelHighCheckBox.isChecked) isBatteryLevelHigh = true
            if(binding.networkEnabledCheckBox.isChecked) isNetworkEnabled = true

            /*
            WorkManager constraints specify the requirement that needs to be met before being executed task.
            In other words, you can say constraints specify the condition for running task under that specific condition.
            These constraints can be related to storage, battery or network.
             */
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(isBatteryLevelHigh)
                .setRequiredNetworkType(
                    if(isNetworkEnabled) NetworkType.CONNECTED
                    else NetworkType.NOT_REQUIRED
                )
                .setRequiresCharging(isCharging)
                .build()

            /*


             */

            val workRequest = OneTimeWorkRequestBuilder<SyncDataWithServer>()
                .setConstraints(constraints)
                .addTag("syncDataWorkRequest")
                .build()

            // Add WorkRequest to queue
            workManager.enqueue(workRequest)
            
            // beginUniqueWork checks if WorkRequest already exists and let decide what to do in such situation
            /*
            workManager.beginUniqueWork(
                    "syncDataWorkRequest",
                    ExistingWorkPolicy.KEEP,
                    workRequest)
            .enqueue()
            */

            Toast.makeText(applicationContext, "workRequest started", Toast.LENGTH_LONG).show()


        }

    }
}

// Worker
class SyncDataWithServer(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters){
    override fun doWork(): Result {
        val number = Random(100).nextInt()
        if (number % 2 == 0) {
            Log.d("Tag", "Synchronizacja z serwerem zakończyła się powodzeniem")
            return Result.success()
        } else {
            Log.d("Tag","Błąd synchronizacji!")
            return  Result.failure()
        }
    }
}