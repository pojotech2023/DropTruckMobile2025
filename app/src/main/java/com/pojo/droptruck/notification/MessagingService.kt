package com.pojo.droptruck.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pojo.droptruck.R
import com.pojo.droptruck.activity.newmain.NewMainActivity
import java.io.IOException
import java.net.URL


class MessagingService : FirebaseMessagingService() {
    var TAG = "MyFirebaseMesaggingService"

    override fun onMessageReceived(message: RemoteMessage) {

        try{
            if (message.data.size > 0) {
                Log.d(TAG, "Message Data payload: " + message.data)
            }
            if (message.notification != null) {
                sendNotification(
                    message.notification!!.body, message.notification!!.title, message.notification!!
                        .imageUrl
                )
            }else {
                Log.d("Notification:::","Null")
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    private fun sendNotification(messageBody: String?, title: String?, imgUrl: Uri?) {

        try{

        Log.d("Notification:::","Success")
        val intent = Intent(this, NewMainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        var bmp: Bitmap? = null

        try {
            Log.d(TAG, "sendNotification: " + imgUrl.toString())
            val `in` = URL(imgUrl.toString()).openStream()
            bmp = BitmapFactory.decodeStream(`in`)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val channelId = getString(R.string.app_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/raw/notification_sound.mp3")
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.delivery_tracking)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bmp ?: BitmapFactory.decodeResource(resources,R.drawable.delivery_tracking)))
                .setPriority(Notification.PRIORITY_HIGH)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val channel = NotificationChannel(
                channelId,
                "Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setSound(sound , audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}