package com.example.tutle

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "NotificationReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notification_id", 0)
        val channelId = intent.getStringExtra("channel_id")
        val description = intent.getStringExtra("description")

        // 로그를 추가하여 값 확인
        Log.d(TAG, "Received notification with ID: $notificationId, Channel ID: $channelId, Description: $description")

        // 채널 ID가 null이 아닌지 확인
        if (channelId != null) {
            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_home) // 올바른 리소스 ID 사용
                .setContentTitle("Alarm Notification")
                .setContentText("Alarm: $description")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                // 알림 권한 체크 및 요청
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // 권한이 없는 경우, 여기서 종료하고 알림을 표시하지 않음
                    Log.e(TAG, "POST_NOTIFICATIONS 권한이 없음")
                    return
                }

                // 알림을 표시
                notify(notificationId, builder.build())
            }
        } else {
            Log.e(TAG, "Channel ID is null")
        }
    }
}
