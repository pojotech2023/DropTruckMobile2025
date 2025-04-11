package com.pojo.droptruck.notification

/*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

fun showCustomSoundNotification(context: Context, title: String, body: String, soundFile: String, customId: Int) {
    val channelId = "my_custom_channel" // Replace with a unique channel ID

    // Create Notification Channel (Android 8.0+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "My Channel Name", // User-friendly name
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "My Channel Description"
            setSound(Uri.parse("android.resource://${context.packageName}/R.raw.${soundFile}"), AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build())
        }
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }

    // Set the sound
    val soundUri = Uri.parse("android.resource://${context.packageName}/R.raw.${soundFile}")

    // Build the notification
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_notification) // Replace with your app icon
        .setContentTitle(title)
        .setContentText(body)
        .setSound(soundUri)
        .putExtra("custom_id", customId) // Add custom data
        .setAutoCancel(true) // Dismiss notification after click
        .build()

    // Show the notification
    NotificationManagerCompat.from(context).notify(1, notification) // Use a unique notification ID
}*/
