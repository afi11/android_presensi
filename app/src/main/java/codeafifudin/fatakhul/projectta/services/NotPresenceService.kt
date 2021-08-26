package codeafifudin.fatakhul.projectta.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import codeafifudin.fatakhul.projectta.MainActivity
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.controllers.WaktuPresensiController
import codeafifudin.fatakhul.projectta.utils.GetWaktu
class NotPresenceService: Service() {

    val CHANNEL_ID = "PresensiNotification"
    var waktuPresensiController : WaktuPresensiController?= null
    var getWaktu : GetWaktu ?= null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        onTaskRemoved(intent)

        waktuPresensiController = WaktuPresensiController(applicationContext)
        getWaktu = GetWaktu(applicationContext)

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        if(getWaktu!!.currentClock().equals("15:30:00")){
            sendNotification("Pemberitahuan Presensi","Waktu untuk melakukan presensi jam masuk",intent)
        }else if(getWaktu!!.currentClock().equals("16:00:00")){
            sendNotification("Pemberitahuan Presensi","Waktu untuk melakukan presensi jam pulang",intent)
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    fun sendNotification(title: String, body: String, intent: Intent) {
        val pendingIntent = PendingIntent.getActivity(this,100,intent, PendingIntent.FLAG_ONE_SHOT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val mChannel = NotificationChannel(
                CHANNEL_ID,"PresensiNotification",
                NotificationManager.IMPORTANCE_HIGH
            )
            mChannel.description = "Firebase Cloud Messaging"
            //mChannel.setSound(ringtoneUri, audioAttributes)
            notificationManager.createNotificationChannel(mChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(baseContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.logokemenag)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setSound(soundUri)
            .setAutoCancel(true).build()
        startForeground(1000,notificationBuilder)
    }


}