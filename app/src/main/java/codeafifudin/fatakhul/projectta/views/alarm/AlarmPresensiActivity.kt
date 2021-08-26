package codeafifudin.fatakhul.projectta.views.alarm

import android.app.*
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.receiver.AlarmPresensiReciver
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.activity_alarm_presensi.*
import java.util.*

class AlarmPresensiActivity : AppCompatActivity() {

    private lateinit var picker : MaterialTimePicker
    private lateinit var calendar : Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_presensi)
        createNotificationChannel()

        selectTimeBtn.setOnClickListener {
            showTimePicker()
        }

        setAlarmBtn.setOnClickListener {
            setAlarm()
        }

        cancelAlarmBtn.setOnClickListener {
            cancelAlarm()
        }

    }

    private fun cancelAlarm() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,AlarmPresensiReciver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0)
        alarmManager.cancel(pendingIntent)
        Toast.makeText(this,"Alarm cancelled", Toast.LENGTH_SHORT).show()
    }

    private fun setAlarm() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,AlarmPresensiReciver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,pendingIntent)
        Toast.makeText(this,"Alarm set successfully", Toast.LENGTH_SHORT).show()
    }

    private fun showTimePicker() {
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Atur Alarm Presensi")
            .build()
        picker.show(supportFragmentManager, "alarmPresensi")

        picker.addOnPositiveButtonClickListener {
            if(picker.hour > 12) {
                seletedTime.text = String.format("%02d",picker.hour - 12) + " : "+ String.format("%02d",picker.minute) + "PM"
            }else{
                seletedTime.text = String.format("%02d",picker.hour) + " : "+ String.format("%02d",picker.minute) + "AM"
            }
            calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = picker.hour
            calendar[Calendar.MINUTE] = picker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
        }
    }

    private fun createNotificationChannel() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "AlarmPresensi"
            val description = "Time to present your attendance"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("alarmPresensi",name,importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

    }
}