package com.example.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.astro.PanchangCalculator
import com.example.data.local.DatabaseProvider
import com.example.data.model.CityLocation
import java.util.Date
import java.util.concurrent.TimeUnit

class AstroNotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "astro_daily_notification_work"
        const val CHANNEL_ID = "astro_daily_notifications"

        fun scheduleDailyNotification(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val dailyWorkRequest = PeriodicWorkRequestBuilder<AstroNotificationWorker>(24, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                dailyWorkRequest
            )
        }
    }

    override suspend fun doWork(): Result {
        return try {
            val context = applicationContext
            val dao = DatabaseProvider.getKundaliDao(context)
            val profiles = dao.getSavedProfilesList()
            val primaryProfile = profiles.firstOrNull()

            val defaultCity = CityLocation("Jaipur", "जयपुर", "Rajasthan", 26.9124, 75.7873)
            val panchang = PanchangCalculator.calculatePanchang(Date(), defaultCity)

            val profileName = primaryProfile?.name ?: "AstroVeda User"
            val title = "✨ Today's Panchang & Muhurat for $profileName"
            val content = "Tithi: ${panchang.tithi} | Abhijit: ${panchang.abhijitMuhurat} | Rahu Kaal: ${panchang.rahuKaal}"

            showNotification(title, content)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotification(title: String, content: String) {
        val context = applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily Astro & Muhurat Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily notifications for planetary transits, auspicious Abhijit Muhurat, and Panchang insights."
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(1001, notification)
    }
}
