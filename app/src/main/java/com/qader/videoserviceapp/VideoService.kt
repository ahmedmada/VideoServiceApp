package com.qader.videoserviceapp

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager

@UnstableApi
class VideoService : Service() {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var notificationManager: PlayerNotificationManager

    companion object {
        const val CHANNEL_ID = "VideoServiceChannel"
        const val NOTIFICATION_ID = 1

        // Static reference to the player
        var playerInstance: ExoPlayer? = null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // Initialize ExoPlayer
        player = ExoPlayer.Builder(this).build()
        playerInstance = player // Expose the player instance

        // Initialize MediaSessionCompat
        mediaSessionCompat = MediaSessionCompat(this, "VideoService")

        // Setup PlayerNotificationManager
        notificationManager = PlayerNotificationManager.Builder(
            this,
            NOTIFICATION_ID,
            CHANNEL_ID
        )
            .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence = "Playing Video"
                override fun getCurrentContentText(player: Player): CharSequence? = null
                override fun getCurrentSubText(player: Player): CharSequence? = null
                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? = null

                override fun createCurrentContentIntent(player: Player) = null
            })
            .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    player.stop()
                    stopSelf()
                }

                @SuppressLint("ForegroundServiceType")
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    // Modify the notification to make it non-cancelable
                    val modifiedNotification = NotificationCompat.Builder(this@VideoService, CHANNEL_ID)
                        .setContentTitle(notification.extras.getCharSequence(Notification.EXTRA_TITLE))
                        .setContentText(notification.extras.getCharSequence(Notification.EXTRA_TEXT))
                        .setSmallIcon(android.R.drawable.ic_media_play)
                        .setOngoing(true) // Make it non-cancelable
                        .build()

                    startForeground(notificationId, modifiedNotification)
                }
            })
            .build()

        // Enable seek bar in notification
        notificationManager.setUsePlayPauseActions(true)
        notificationManager.setUseChronometer(true)

        notificationManager.setPlayer(player)
        notificationManager.setMediaSessionToken(mediaSessionCompat.sessionToken)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val videoUri = intent?.getStringExtra("VIDEO_URI") ?: ""
        if (videoUri.isNotEmpty()) {
            val mediaItem = MediaItem.fromUri(videoUri)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        mediaSessionCompat.release()
        playerInstance = null
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Video Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}

//package com.qader.videoserviceapp
//
//import android.annotation.SuppressLint
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.Service
//import android.content.Intent
//import android.graphics.Bitmap
//import android.os.Build
//import android.os.IBinder
//import android.support.v4.media.session.MediaSessionCompat
//import androidx.media3.common.MediaItem
//import androidx.media3.common.Player
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.ui.PlayerNotificationManager
//
//@UnstableApi
//class VideoService : Service() {
//
//    private lateinit var player: ExoPlayer
//    private lateinit var mediaSessionCompat: MediaSessionCompat
//    private lateinit var notificationManager: PlayerNotificationManager
//
//    companion object {
//        const val CHANNEL_ID = "VideoServiceChannel"
//        const val NOTIFICATION_ID = 1
//
//        // Static reference to the player
//        var playerInstance: ExoPlayer? = null
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        createNotificationChannel()
//
//        // Initialize ExoPlayer
//        player = ExoPlayer.Builder(this).build()
//        playerInstance = player // Expose the player instance
//
//        // Initialize MediaSessionCompat
//        mediaSessionCompat = MediaSessionCompat(this, "VideoService")
//
//        // Setup PlayerNotificationManager
//        notificationManager = PlayerNotificationManager.Builder(
//            this,
//            NOTIFICATION_ID,
//            CHANNEL_ID
//        )
//            .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {
//                override fun getCurrentContentTitle(player: Player): CharSequence = "Playing Video"
//                override fun getCurrentContentText(player: Player): CharSequence? = null
//                override fun getCurrentSubText(player: Player): CharSequence? = null
//                override fun getCurrentLargeIcon(
//                    player: Player,
//                    callback: PlayerNotificationManager.BitmapCallback
//                ): Bitmap? = null
//
//                override fun createCurrentContentIntent(player: Player) = null
//            })
//            .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
//                override fun onNotificationCancelled(
//                    notificationId: Int,
//                    dismissedByUser: Boolean
//                ) {
//                    player.stop()
//                    stopSelf()
//                }
//
//                @SuppressLint("ForegroundServiceType")
//                override fun onNotificationPosted(
//                    notificationId: Int,
//                    notification: Notification,
//                    ongoing: Boolean
//                ) {
//                    startForeground(notificationId, notification)
//                }
//            })
//            .build()
//
//        notificationManager.setPlayer(player)
//        notificationManager.setMediaSessionToken(mediaSessionCompat.sessionToken)
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val videoUri = intent?.getStringExtra("VIDEO_URI") ?: ""
//        if (videoUri.isNotEmpty()) {
//            val mediaItem = MediaItem.fromUri(videoUri)
//            player.setMediaItem(mediaItem)
//            player.prepare()
//            player.play()
//        }
//        return START_STICKY
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        player.release()
//        mediaSessionCompat.release()
//        playerInstance = null
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                "Video Playback",
//                NotificationManager.IMPORTANCE_LOW
//            )
//            val manager = getSystemService(NotificationManager::class.java)
//            manager?.createNotificationChannel(channel)
//        }
//    }
//}