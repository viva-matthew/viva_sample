package com.example.viva_sample.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.viva_sample.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.orhanobut.logger.Logger


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Logger.d("## onNewToken ==> $token")
        super.onNewToken(token)
    }

    // 메세지를 수신할떄마다 실행
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Logger.d("## remoteMessage ==> ${remoteMessage.data}")

        val type = remoteMessage.data["type"]?.let { PushType.valueOf(it) }
        type ?: return
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]

        Logger.d("## version ==> ${Build.VERSION.SDK_INT}")
        // 오레오 기준 노티피케이션 채널생성 필수
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        NotificationManagerCompat.from(this).notify(type.id, createNotification(type, title, message))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNER_ID,
            CHANNER_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = CHANNER_DESCRIPTION
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

    }

    private fun createNotification(
        type: PushType,
        title: String?,
        message: String?,
    ): Notification {
//        val intent = Intent(this, PushActivity::class.java).apply {
//            putExtra("notificationType", "${type.title} 타입")
//            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            putExtra("notificationType", "${type.title} 타입")
            data = Uri.parse("market://details?id=com.viva.viva_kindoc")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // 인텐트를 다룰수 있는 권한을 넘겨준다.
        val pendingIntent = PendingIntent.getActivity(this, type.id, intent, FLAG_UPDATE_CURRENT)


        val notificationBuilder = NotificationCompat.Builder(this, CHANNER_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        Logger.d("## type ==>${type}")

        when (type) {
            PushType.NORMAL -> {
//                val intent = Intent(Intent.ACTION_VIEW)
////                intent.data = Uri.parse("market://details?id=$packageName")
//                intent.data = Uri.parse("market://details?id=com.viva.viva_kindoc")
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
            }
            PushType.EXPANDABLE -> {
                Logger.d("## EXPANDABLE")
                notificationBuilder.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "😀 😃 😄 😁 😆 😅 😂 🤣 🥲 ☺️ 😊 😇 🙂 🙃 😉 😌 😍 " +
                                    "🥰 😘 😗 😙 😚 😋 😛 😝 😜 🤪 🤨 🧐 🤓 😎 🥸 🤩 🥳" +
                                    " 😏 😒 😞 😔 😟 😕 🙁 ☹️ 😣 😖 😫 😩 🥺 😢 😭 😤 😠" +
                                    " 😡 🤬 🤯 😳 🥵 🥶 😱 😨 😰 😥 😓 🤗 🤔 🤭 🤫 🤥 😶" +
                                    " 😐 😑 😬 🙄 😯 😦 😧 😮 😲 🥱 😴 🤤 😪 😵 🤐 🥴 🤢" +
                                    " 🤮 🤧 😷 🤒 🤕"

                        )
                )

            }
            PushType.CUSTOM -> {
                notificationBuilder
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(
                        RemoteViews(
                            packageName,
//                            R.layout.view_cusom_notification
                            R.layout.item_push
                        ).apply {
//                            setTextViewText(R.id.title, title)
//                            setTextViewText(R.id.message, message)
                            setTextViewText(R.id.tvUpdateAlram, title)
                            setTextViewText(R.id.tvContent, message)
                        }
                    )
            }

        }

        return notificationBuilder.build()
    }

    companion object {
        private const val CHANNER_NAME = "Emoji Party"
        private const val CHANNER_DESCRIPTION = "Emoji Party를 위한 채"
        private const val CHANNER_ID = "Channer_Id"

    }
}


/* 토큰이 재 생성되는 경우

- 새 기기에서 앱 복원
- 사용자가 앱 삭제/재설치
- 사용자가 앱 데이터 소거

 */