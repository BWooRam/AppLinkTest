package com.hyundaiht.applink.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.hyundaiht.webviewtest.R
import kotlin.random.Random


object AppNotificationManager {
    private val tag = javaClass.simpleName
    val NOTI_CHANNEL_ID_DEFAULT = "NOTI_CHANNEL_ID_DEFAULT"
    val NOTI_CHANNEL_NAME_DEFAULT = "NOTI_CHANNEL_NAME_DEFAULT"
    val NOTI_ID_DEFAULT = 1000

    @SuppressLint("NotificationPermission", "MissingPermission")
    fun sendDeepLinkNotification(context: Context, contentIntent: Intent) {
        val randomId = Random.nextInt(0, 10000)
        val builder = createNotificationBuilder(context)
        val notification = createDeepLinkNotification(context, builder, contentIntent)
        NotificationManagerCompat.from(context).notify(randomId, notification)
    }

    @SuppressLint("MissingPermission", "NotificationPermission")
    fun sendNotification(context: Context, contentIntent: Intent) {
        val builder = createNotificationBuilder(context)
        val notification = createCustomNotification(context, builder, contentIntent)
        NotificationManagerCompat.from(context).notify(NOTI_ID_DEFAULT, notification)
    }

    /**
     * createNotificationBuilder
     *
     * @param context
     * @return
     */
    private fun createNotificationBuilder(context: Context): NotificationCompat.Builder {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            NOTI_CHANNEL_ID_DEFAULT,
            NOTI_CHANNEL_NAME_DEFAULT,
            NotificationManager.IMPORTANCE_HIGH
        )
//            channel.setShowBadge(false)
        if (notificationManager.getNotificationChannel(NOTI_CHANNEL_ID_DEFAULT) == null)
            notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(context, channel.id)
    }

    /**
     * createCustomNotification
     *
     * @param context
     * @param customNotification
     * @param contentIntent
     * @return
     */
    private fun createCustomNotification(
        context: Context,
        customNotification: NotificationCompat.Builder,
        contentIntent: Intent
    ): Notification {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return customNotification
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("test")
            .setContentText("test")
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent).build()
    }

    /**
     * createCustomNotification
     *
     * @param context
     * @param customNotification
     * @param contentIntent
     * @return
     */
    private fun createDeepLinkNotification(
        context: Context,
        customNotification: NotificationCompat.Builder,
        contentIntent: Intent,
        isNewActivity: Boolean = false
    ): Notification {
        val pendingIntent = if (isNewActivity)
            createTaskStackPendingIntent(context, contentIntent, bundleOf())
        else
            createBroadcastPendingIntent(context, contentIntent, bundleOf())

        return customNotification
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("test")
            .setContentText("test")
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent).build()
    }

    /**
     * PendingIntent.getBroadcast를 통한 PendingIntent 생성 방식
     *
     * @param context
     * @param intent
     * @param args
     * @return
     */
    private fun createBroadcastPendingIntent(
        context: Context,
        intent: Intent,
        args: Bundle?
    ): PendingIntent {
        val requestCode = createRequestCode(intent, args)

        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )
    }

    private fun createTaskStackBuilder(context: Context, intent: Intent): TaskStackBuilder {
        val taskStackBuilder = TaskStackBuilder
            .create(context)
            .addNextIntentWithParentStack(Intent(intent))
        return taskStackBuilder
    }

    /**
     * TaskStackBuilder addNextIntentWithParentStack를 통한 PendingIntent 생성 방식
     *
     * @param context
     * @param intent
     * @param args
     * @return
     */
    private fun createTaskStackPendingIntent(
        context: Context,
        intent: Intent,
        args: Bundle?
    ): PendingIntent {
        val requestCode = createRequestCode(intent, args)

        return createTaskStackBuilder(context, intent.apply {
            if (args != null) {
                putExtras(args)
            }
        }).getPendingIntent(
            requestCode,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )!!
    }

    private fun createRequestCode(intent: Intent, args: Bundle?): Int {
        var requestCode = 0

        if (args != null && !args.isEmpty) {
            for (key in args.keySet()) {
                val value = args[key]
                requestCode = 31 * requestCode + (value?.hashCode() ?: 0)
            }
        } else {
            requestCode = 31 * intent.hashCode()
        }

        Log.d(
            tag,
            "createRequestCode intentHashCode = ${intent.hashCode()}, argsHashCode = ${args?.hashCode()}, requestCode = $requestCode"
        )
        return requestCode
    }
}
