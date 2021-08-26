package codeafifudin.fatakhul.projectta.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import codeafifudin.fatakhul.projectta.MainActivity
import codeafifudin.fatakhul.projectta.R

class AlarmPresensiReciver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val intent = Intent(context, MainActivity::class.java)
        intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context!!, "alarmPresensi")
            .setSmallIcon(R.drawable.logokemenag)
            .setContentText("Presensi")
            .setContentText("Harap segera melakukan presensi")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(123, builder.build())
    }

}