package codeafifudin.fatakhul.projectta.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import codeafifudin.fatakhul.projectta.MainActivity
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.config.Auth
import codeafifudin.fatakhul.projectta.models.Presensi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseNotificationConfig : FirebaseMessagingService() {

    val CHANNEL_ID = "PresensiNotification"
    var auth : Auth ?= null
    var waktu = ""

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        auth = Auth(applicationContext)
        val pegawai_id = p0.data.getValue("pegawaiId")
        val tipeNotifikasi = p0.data.getValue("tipeNotifikasi")
        if(tipeNotifikasi.equals("presensi")){
            val waktuPresensi = p0.data.getValue("waktuPresensi")
            val tipePegawai = p0.data.getValue("tipePegawai")
            val tipePresensi = p0.data.getValue("tipePresensi")
            if(tipePresensi.equals("jam_masuk")){
                waktu = "Jam Masuk"
            }else{
                waktu = "Jam Pulang"
            }
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // intent.putExtra("pegawaiId",pegawai_id)
            intent.putExtra("tipePage","presensi")
            intent.putExtra("waktuPresensi",waktuPresensi)
            intent.putExtra("tipePegawai",tipePegawai)

            if(tipePegawai.equals("allPegawai")){
                sendNotification("Waktu Presensi","Harap Segera Melakukan Presensi ${waktu}",intent)
            }else if(tipePegawai.equals(auth!!.tipePegawai())){
                sendNotification("Waktu Presensi","Harap Segera Melakukan Presensi ${waktu}",intent)
            }else if(pegawai_id.equals(auth!!.userId())){
                sendNotification("Waktu Presensi","Harap Segera Melakukan Presensi ${waktu}",intent)
            }
        }
        else if(tipeNotifikasi.equals("pengajuan_cuti")){
            val tglAwal = p0.data.getValue("tglAwal")
            val tglAkhir = p0.data.getValue("tglAkhir")
            val ket = p0.data.getValue("ket")
            var status = ""
            if(p0.data.getValue("status").equals("accepted")){
                status = "Diterima"
            }else{
                status = "Ditolak"
            }
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("tipePage","izin")
            intent.putExtra("pegawaiId",pegawai_id)

            if(pegawai_id.equals(auth!!.userId())){
                sendNotification("Pengajuan Izin ${tglAwal} sd ${tglAkhir} ${status}",
                    "Izin Anda ${status} dengan keterangan ${ket}",intent)
            }
        }
        else{
            val noSurat = p0.data.getValue("noSurat")
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("noSurat",noSurat)
            intent.putExtra("pegawaiId",pegawai_id)

            if(pegawai_id.equals(auth!!.userId())){
                sendNotification("Penugasan","Anda mendapat tugas dengan nomor surat ${noSurat}",intent)
            }
        }
    }

    fun sendNotification(title: String, body: String, intent: Intent) {
        val pendingIntent = PendingIntent.getActivity(this,100,intent,PendingIntent.FLAG_ONE_SHOT)

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
        notificationManager.notify(100, notificationBuilder)
    }

}