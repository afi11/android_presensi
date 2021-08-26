package codeafifudin.fatakhul.projectta.utils

import android.content.Context
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class GetWaktu(context: Context) {

    fun getDateNow(): String {
        val sdf  = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date()).toString()
    }

    fun currentDate(): String {
        val sdf  = SimpleDateFormat("MMM d, yyy")
        return sdf.format(Date()).toString()
    }

    fun isTimeBetweenTwoTime(initialTime : String, finalTime: String) : Boolean {
        var isAfter = false
        try {
            val time1 = SimpleDateFormat("HH:mm:ss").parse(initialTime)
            val time2 = SimpleDateFormat("HH:mm:ss").parse(finalTime)
            if(time1.before(time2)){
                isAfter = false
            }else if(time1.after(time2)){
                isAfter = true
            }
        }catch (e: ParseException){
            e.printStackTrace()
        }
        return isAfter
    }

    fun currentClock() : String {
        val sdf = SimpleDateFormat("HH:mm:ss")
        return sdf.format(Date()).toString()
    }

    fun getCurrentDay() : String {
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val day = SimpleDateFormat("EEEE",Locale.ENGLISH).format(date.time)
        return day
    }

    fun formatDate(dateStr: String?): String? {
        try {
            val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = fmt.parse(dateStr)
            val fmtOut = SimpleDateFormat("MMM d, yyyy")
            return fmtOut.format(date)
        } catch (e: ParseException) {
        }
        return ""
    }

}

